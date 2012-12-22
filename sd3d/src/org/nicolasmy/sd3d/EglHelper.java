package org.nicolasmy.sd3d;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL11;

import org.nicolasmy.sd3d.gfx.Sd3dRessourceManager;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.util.Log;
import android.view.SurfaceHolder;

public class EglHelper {
    private static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
    private static final int EGL_OPENGL_ES2_BIT = 4;
    
    public EglHelper() {

    }

    /**
     * Initialize EGL for a given configuration spec.
     * @param configSpec
     */
    public void start(int[] configSpec){
        /*
         * Get an EGL instance
         */
        mEgl = (EGL10) EGLContext.getEGL();
        
        /*
         * Get to the default display.
         */
        mEglDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

        /*
         * We can now initialize EGL for that display
         */
        int[] version = new int[2];
        mEgl.eglInitialize(mEglDisplay, version);

        EGLConfig[] configs = new EGLConfig[1];
        int[] num_config = new int[1];
        mEgl.eglChooseConfig(mEglDisplay, configSpec, configs, 1,
                num_config);
        mEglConfig = configs[0];

        /*
        * Create an OpenGL ES context. This must be done only once, an
        * OpenGL context is a somewhat heavy object.
        */
        final ActivityManager activityManager = Sd3dRessourceManager.getManager().getActivityManager();
        
        //final ActivityManager activityManager = (ActivityManager) Context.getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
        
        if ((supportsEs2))
        {
        	int[] attrib_list = new int[]{EGL_CONTEXT_CLIENT_VERSION, 2,EGL10.EGL_NONE};

        	mEglContext = mEgl.eglCreateContext(mEglDisplay, mEglConfig, EGL10.EGL_NO_CONTEXT, attrib_list);
        }
        else
        {
        	mEglContext = mEgl.eglCreateContext(mEglDisplay, mEglConfig, EGL10.EGL_NO_CONTEXT, null);
        }
        
        if (mEgl.eglGetError() != EGL10.EGL_SUCCESS)
        {
        	
        	Log.d("createSurface()","ERROR FAILED TO CREATE CONTEXT");
        }               
        
        mEglSurface = null;
    }

    /*
     * Create and return an OpenGL surface
     */
    public GL11 createSurface(SurfaceHolder holder) {
        /*
         *  The window size has changed, so we need to create a new
         *  surface.
         */
        if (mEglSurface != null) {

            /*
             * Unbind and destroy the old EGL surface, if
             * there is one.
             */
            mEgl.eglMakeCurrent(mEglDisplay, EGL10.EGL_NO_SURFACE,
                    EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
            mEgl.eglDestroySurface(mEglDisplay, mEglSurface);
            
            if (mEGLPickingSurface != null)
            	mEgl.eglDestroySurface(mEglDisplay, mEGLPickingSurface);                
        }

        /*
         * Create an EGL surface we can render into.
         */
        //int[] attrib_list = new int[]{EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE};
        //int[] attrib_list = new int[]{EGL10.EGL_VERSION, 2,EGL10.EGL_NONE};
        
        if (holder != null)
        	Log.d("EglHelper",holder.getClass().toString());
        mEglSurface = mEgl.eglCreateWindowSurface(mEglDisplay,
                mEglConfig, holder, null);
        
        if (mEgl.eglGetError() != EGL10.EGL_SUCCESS)
        {
        	
        	Log.d("createSurface()","ERROR FAILED TO CREATE SURFACE");
        }       
        
/*
        int attrlist[] = {EGL10.EGL_WIDTH,512,EGL10.EGL_HEIGHT,512,EGL10.EGL_NONE};
        //int attrlist[] = {};
        //int[] attrlist = new int[]{EGL_CONTEXT_CLIENT_VERSION, 2,EGL10.EGL_NONE};
        mEGLPickingSurface = mEgl.eglCreatePbufferSurface(mEglDisplay, mEglConfig,  attrlist);
        
        int error = mEgl.eglGetError();
        if (error != EGL10.EGL_SUCCESS)
        {
        	Log.e("createSurface()","ERROR SETTING CONTEXT ERROR CODE="+error);
        	
        }        
  */      
        /*
         * Before we can issue GL commands, we need to make sure
         * the context is current and bound to a surface.
         */
        mEgl.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface,
                mEglContext);

        
        if (mEgl.eglGetError() != EGL10.EGL_SUCCESS)
        {
        	
        	Log.d("createSurface()","ERROR SETTING CONTEXT");
        }
        
        GL11 gl = (GL11)mEglContext.getGL();

        return gl;
    }

    public GL11 makePickCurrent()
    {
    	Log.d("makePickCurrent()", "Making the picking pixel buffer the current context");
        
    	boolean res = mEgl.eglMakeCurrent(mEglDisplay, mEGLPickingSurface, mEGLPickingSurface,
                mEglContext);
        
        Log.d("makePickCurrent()","RESULT= "+res);
       
        
        return (GL11)mEglContext.getGL();
    }
    
    public void makeDisplayCurrent()
    {
    	//Log.d("makeDisplayCurrent()", "Making the screen surface the current context");        	
        mEgl.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface,
                mEglContext);    	
    }             
    
    /**
     * Display the current render surface.
     * @return false if the context has been lost.
     */
    public boolean swap() {
        mEgl.eglSwapBuffers(mEglDisplay, mEglSurface);
    	/*
        boolean b = mEgl.eglSwapBuffers(mEglDisplay, mEGLPickingSurface);
       if (b == false){
    	   Log.e("swap()","Failure to swap !");
       }
       */
        
        /*
         * Always check for EGL_CONTEXT_LOST, which means the context
         * and all associated data were lost (For instance because
         * the device went to sleep). We need to sleep until we
         * get a new surface.
         */
        return mEgl.eglGetError() != EGL11.EGL_CONTEXT_LOST;
    }

    public void finish() {
        if (mEglSurface != null) {
            mEgl.eglMakeCurrent(mEglDisplay, EGL10.EGL_NO_SURFACE,
                    EGL10.EGL_NO_SURFACE,
                    EGL10.EGL_NO_CONTEXT);
            mEgl.eglDestroySurface(mEglDisplay, mEglSurface);
            mEglSurface = null;
            
            if (mEGLPickingSurface != null)
            {
            	mEgl.eglDestroySurface(mEglDisplay, mEGLPickingSurface);
            	mEGLPickingSurface = null;
            }
        }
        if (mEglContext != null) {
            mEgl.eglDestroyContext(mEglDisplay, mEglContext);
            mEglContext = null;
        }
        if (mEglDisplay != null) {
            mEgl.eglTerminate(mEglDisplay);
            mEglDisplay = null;
        }
    }

    EGL10 mEgl;
    EGLDisplay mEglDisplay;
    EGLSurface mEglSurface;
    EGLConfig mEglConfig;
    EGLContext mEglContext;
    EGLSurface mEGLPickingSurface;
}	
