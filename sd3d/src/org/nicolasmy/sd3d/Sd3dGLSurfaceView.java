package org.nicolasmy.sd3d;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;




import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
//import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL11;

import org.nicolasmy.sd3d.gfx.Sd3dRessourceManager;
import org.nicolasmy.sd3d.gfx.renderer.Sd3dRenderer;
import org.nicolasmy.sd3d.gfx.renderer.Sd3dRendererInterface;
//import javax.microedition.khronos.opengles.GL10;

import org.nicolasmy.sd3d.utils.Sd3dLogger;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
//import android.hardware.Sensor;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.view.MotionEvent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;


public class Sd3dGLSurfaceView extends SurfaceView 
implements 
SurfaceHolder.Callback
//,android.hardware.SensorEventListener, 
//android.view.View.OnTouchListener
{
	
	private SensorManager mSensorManager; 
	
	public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
    	Log.d("onAccuracyChanged(android.hardware.SensorEvent event)","");		
    	if ((this.mGLThread != null)&&(this.mGLThread.mGame != null))
    		this.mGLThread.mGame.onAccuracyChanged(sensor, accuracy);
    }
    
    public void onSensorChanged(SensorEvent event)
    {
    	

    	if ((this.mGLThread != null)&&(this.mGLThread.mGame != null))
    		this.mGLThread.mGame.onSensorChanged(event);   	
    }
    
	
	public boolean onTouch(View v, MotionEvent event) {
    	if ((this.mGLThread != null)&&(this.mGLThread.mGame != null))
    		this.mGLThread.mGame.onTouch(event);
    		
		return true;
	}    
    
	public Sd3dGLSurfaceView(Context context) {
        super(context);
        init(context);    
    }

    public Sd3dGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed
        mHolder = getHolder();
        mHolder.addCallback((Callback) this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_GPU);
        
        //FOR Cam
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
        

        
        //this.setOnTouchListener(this);   
        Sd3dLogger.log("Surface INIT");
        
        // setup accelerometer sensor manager.
//        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        // register our accelerometer so we can receive values.
        // SENSOR_DELAY_GAME is the recommended rate for games
//        mSensorManager.registerListener(
//        		(android.hardware.SensorEventListener)this,
//        		mSensorManager.getSensorList(android.hardware.Sensor.TYPE_ACCELEROMETER).get(0),
//                SensorManager.SENSOR_DELAY_GAME
//                );       
     
    }

    public SurfaceHolder getSurfaceHolder() {
        return mHolder;
    }

    //public void setGLWrapper(GLWrapper glWrapper) {
      //  mGLWrapper = glWrapper;
    //}

    public void setRendererGame(Sd3dRendererInterface renderer,Sd3dGame game) {
        mGLThread = new GLThread(renderer,game);
        mGLThread.start();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        int pos[] = new int[2];
        this.getLocationOnScreen(pos);
        this.mGLThread.mGame.mRenderer.setTop(pos[1]);
        
    	mGLThread.setHolder(holder);
        mGLThread.surfaceCreated();

        setOnTouchListener((OnTouchListener)GameHolder.mGame.getFrameProcessor(0));
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return
    	mGLThread.setHolder(null);
    	mGLThread.surfaceDestroyed();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Surface size or format has changed. This should not happen in this
        // example.
        mGLThread.onWindowResize(w, h);
    }

    /**
     * Inform the view that the activity is paused.
     */
    public void onPause() {
        mGLThread.onPause();
       // mSensorManager.unregisterListener(this);
    }

    /**
     * Inform the view that the activity is resumed.
     */
    public void onResume() {
        mGLThread.onResume();
        mSensorManager.registerListener(
        		(android.hardware.SensorEventListener)this,
        		mSensorManager.getSensorList(android.hardware.Sensor.TYPE_ACCELEROMETER).get(0),
                SensorManager.SENSOR_DELAY_GAME
                );               
    }

    /**
     * Inform the view that the window focus has changed.
     */
    @Override public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        
        if (mGLThread != null)
        	mGLThread.onWindowFocusChanged(hasFocus);
    }

    /**
     * Queue an "event" to be run on the GL rendering thread.
     * @param r the runnable to be run on the GL rendering thread.
     */
    public void queueEvent(Runnable r) {
        mGLThread.queueEvent(r);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mGLThread.requestExitAndWait();
    }

    // ----------------------------------------------------------------------

    public interface GLWrapper {
      GL11 wrap(GL11 gl);
    }

    // ----------------------------------------------------------------------

    private GLThread mGLThread;
    public SurfaceHolder mHolder;

}