package org.nicolasmy.sd3d;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import javax.microedition.khronos.opengles.GL11;

import org.nicolasmy.sd3d.gfx.Sd3dRendererInterface;

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
                //Log.d("GLThread","Surface created= "+mHolder.getSurfaceFrame().width()+" "+mHolder.getSurfaceFrame().height());
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
            }
            if ((w > 0) && (h > 0)) {
                /* draw a frame here */
                //mRenderer.drawFrame(gl);
            	
            	mRenderer.setGL11Context(gl);
            	
            	//gl = mEglHelper.makePickCurrent();
          	    //mGame.processFrame(gl);
            	//gl.glColor4f(1.0, 1.0, blue, alpha)
            	/*
            	gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
          	    gl.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
          	    mEglHelper.makePickCurrent();
            	gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
          	    gl.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);              	    
            	*/
            	//
          	    //gl.glFlush();
          	    //mEglHelper.swapPicking();
          	    //mEglHelper.makeDisplayCurrent();
          	    //mEglHelper.swapPicking();

            	if (mGame.mHasPick)
            	{
            	  gl = mEglHelper.makePickCurrent();	
            	  mRenderer.sizeChanged(gl, w, h);
            	  mGame.processPickingFrame(gl);
            	  //mEglHelper.swapPicking();
              	  //gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
          	      //gl.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);                	  
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
            	            	    
            	/*
            	if (!mGame.mHasPick)
            	{
            	  mGame.processFrame(gl);
            	  mEglHelper.swap();
            	}
            	else
            	{
            		//mEglHelper.makePickCurrent();
              	    //mGame.processFrame(gl); 
              	    //gl.glFlush();
              	    //mEglHelper.swapPicking();
              	    //mEglHelper.swap();
              	    //mEglHelper.makeDisplayCurrent();
              	    mGame.mPickCount++;
              	    //mEglHelper.swap();
              	    if (mGame.mPickCount == 2)
              	    {
              	    	mGame.mHasPick = false;
              	    	mGame.mPickCount = 0;
              	    }
            	}
            	*/
            	/*
                 * Once we're done with GL, we need to call swapBuffers()
                 * to instruct the system to display the rendered frame
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
