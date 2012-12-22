package org.nicolasmy.sd3d;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import org.nicolasmy.sd3d.gfx.renderer.Sd3dRendererInterface;

import android.opengl.GLES20;
import android.util.Log;
import android.view.SurfaceHolder;

public class GLThread extends Thread {
	
	
    public GLThread(Sd3dRendererInterface renderer,Sd3dGame game) {
        super();
        mDone = false;
        mWidth = 0;
        mHeight = 0;
        mRenderer = renderer;
        mGame = game;
        setName("GLThread");
    }

    @Override
    public void run() {
        /*
         * When the android framework launches a second instance of
         * an activity, the new instance's onCreate() method may be
         * called before the first instance returns from onDestroy().
         *
         * This semaphore ensures that only one instance at a time
         * accesses EGL.
         */
    	Log.d("GLThread","ACQUIRING EGL SEMAPHORE");
        try {
            try {
            sEglSemaphore.acquire();
            Log.d("GLThread","ACQUIRING EGL SEMAPHORE -- DONE");
            } catch (InterruptedException e) {
                return;
            }
            guardedRun();
        } catch (InterruptedException e) {
            // fall thru and exit normally
        } finally {
            sEglSemaphore.release();
        }
    }

    private void guardedRun() throws InterruptedException {
        mEglHelper = new EglHelper();
        /*
         * Specify a configuration for our opengl session
         * and grab the first configuration that matches is
         */
        int[] configSpec = mRenderer.getConfigSpec();
        mEglHelper.start(configSpec);

        GL11 gl = null;
        boolean tellRendererSurfaceCreated = true;
        boolean tellRendererSurfaceChanged = true;

        /*
         * This is our main activity thread's loop, we go until
         * asked to quit.
         */
        while (!mDone) {

            /*
             *  Update the asynchronous state (window size)
             */
            int w, h;
            boolean changed;
            boolean needStart = false;
            synchronized (this) {
            	
            	Runnable r;
                while ((r = getEvent()) != null) {
                    r.run();
                }
                if (mPaused) {
                    mEglHelper.finish();
                    needStart = true;
                }
                if(needToWait()) {
                    while (needToWait()) {
                        wait();
                    }
                }
                if (mDone) {
                    break;
                }
                
                changed = mSizeChanged;
                w = mWidth;
                h = mHeight;
                mSizeChanged = false;
            }

            if (needStart) {
                mEglHelper.start(configSpec);
                tellRendererSurfaceCreated = true;
                changed = true;
            }
            if (changed) {
                gl = (GL11) mEglHelper.createSurface(mHolder);
                tellRendererSurfaceChanged = true;
            }
            if (tellRendererSurfaceCreated) {
                mRenderer.surfaceCreated(gl);
                mRenderer.updateScreenResolution(w, h);
                tellRendererSurfaceCreated = false;
            }
            if (tellRendererSurfaceChanged) {
                mRenderer.sizeChanged(gl, w, h);
                tellRendererSurfaceChanged = false;
                //mGame.invalidateRenderElements = true;
            }
            if ((w > 0) && (h > 0)) {
                /* draw a frame here */
            	mRenderer.setGL11Context(gl);
             
            	
            	mGame.processFrame(gl);
            	mEglHelper.swap(); 
            	/*
            	if (mGame.mHasPick)
            	{
            	  gl = mEglHelper.makePickCurrent();	
            	  mRenderer.sizeChanged(gl, w, h);
            	  mGame.processPickingFrame(gl);
            	                  	  
            	  gl.glFlush();
            	  mGame.processPicking(gl);
            	  mEglHelper.makeDisplayCurrent();
            	} 
            	else if (mGame.mHasPrePick)
            	{
            	  Log.d("","mGame.mHasPrePick");
            	  gl = mEglHelper.makePickCurrent();	
            	  mRenderer.sizeChanged(gl, w, h);
            	  mGame.processPickingFrame(gl);               	  
            	  gl.glFlush();
            	  mGame.processPrePicking(gl);
            	  mEglHelper.makeDisplayCurrent();
            	}                 	
            	else
            	{
                	mGame.processFrame(gl);
                	mEglHelper.swap();                		
            	}
            	*/          	                    
            }
         }

        /*
         * clean-up everything...
         */
        Log.d("GLThread","EXITING !");
        mEglHelper.finish();
    }

    private boolean needToWait() {
    	/*
    	Log.d("GLThread","mPaused "+mPaused);
    	Log.d("GLThread","mHasFocus "+mHasFocus);
    	Log.d("GLThread","mHasSurface "+mHasSurface);
    	Log.d("GLThread","mContextLost "+mContextLost);
    	*/
        return (mPaused || (! mHasFocus) || (! mHasSurface) || mContextLost)
            && (! mDone);
    }

    public void surfaceCreated() {
        synchronized(this) {
            mHasSurface = true;
            mContextLost = false;
            notify();
            //GameHolder.mGame.invalidateRenderElements = true;
        }
    }

    public void surfaceDestroyed() {
        synchronized(this) {
            mHasSurface = false;
            notify();
            //Log.d("=>","View destroyed");
            //GameHolder.mGame.invalidateRenderElements = true;
        }
    }

    public void onPause() {
        synchronized (this) {
            mPaused = true;
        }
    }

    public void onResume() {
        synchronized (this) {
            mPaused = false;
            notify();
        }
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        synchronized (this) {
        	//GameHolder.mGame.invalidateRenderElements = true;
            mHasFocus = hasFocus;
            if (mHasFocus == true) {
                notify();
            }
        }
    }
    public void onWindowResize(int w, int h) {
        synchronized (this) {
            mWidth = w;
            mHeight = h;
            mSizeChanged = true;
            //GameHolder.mGame.invalidateRenderElements = true;
       
        }      
    }

    public void requestExitAndWait() {
        // don't call this from GLThread thread or it is a guaranteed
        // deadlock!
        synchronized(this) {
            mDone = true;
            notify();
        }
        try {
            join();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Queue an "event" to be run on the GL rendering thread.
     * @param r the runnable to be run on the GL rendering thread.
     */
    public void queueEvent(Runnable r) {
        synchronized(this) {
            mEventQueue.add(r);
        }
    }

    private Runnable getEvent() {
        synchronized(this) {
            if (mEventQueue.size() > 0) {
                return mEventQueue.remove(0);
            }

        }
        return null;
    }

    private boolean mDone;
    private boolean mPaused;
    private boolean mHasFocus;
    private boolean mHasSurface;
    private boolean mContextLost;
    private int mWidth;
    private int mHeight;
    private Sd3dRendererInterface mRenderer;
    public Sd3dGame mGame;
    private ArrayList<Runnable> mEventQueue = new ArrayList<Runnable>();
    private EglHelper mEglHelper;
    private static final Semaphore sEglSemaphore = new Semaphore(1);
    private boolean mSizeChanged = true;
    private SurfaceHolder mHolder;
    
	public SurfaceHolder getHolder() {
		return mHolder;
	}

	public void setHolder(SurfaceHolder mHolder) {
		this.mHolder = mHolder;
	}
}
