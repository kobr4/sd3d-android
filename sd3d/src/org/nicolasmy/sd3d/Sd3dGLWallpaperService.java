package org.nicolasmy.sd3d;

import org.nicolasmy.sd3d.gfx.renderer.Sd3dRendererInterface;

import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

public abstract class Sd3dGLWallpaperService extends WallpaperService {

	/*
	@Override
	public Engine onCreateEngine() {
		return new GLEngine();
	}
	*/
	
	public class GLEngine extends Engine {
		private GLThread mGLThread;
		private Sd3dGame mGame;
		
		public GLEngine()
		{
			super();
		}
		
	    private void setRendererGame(Sd3dRendererInterface renderer,Sd3dGame game) {
	        mGLThread = new GLThread(renderer,game);
	        mGLThread.start();
	    }		
		
	    
	    @Override
	    public void onCreate(SurfaceHolder surfaceHolder)
	    {
	    	Log.d("GLEngine","CREATE");	
	    	
	    	super.onCreate(surfaceHolder);
	    	if (mGame == null)
	    	{
	    		
	    		Log.d("GLEngine","CREATE NEW SD3DGAME");	
	    		mGame = new Sd3dGame();	
	    		GameHolder.mGame = mGame;
	    		Log.d("GLEngine","INIT GAME");
	    		mGame.init(surfaceHolder);
	    		setRendererGame(mGame.mRenderer,mGame);
	    		mGLThread.setHolder(surfaceHolder);					

	    	}
	    }
	    
	    
		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			Log.d("GLEngine","SURFACE CREATED");	   
			
			super.onSurfaceCreated(holder);
			
			mGLThread.surfaceCreated();
			mGLThread.onWindowResize(480, 800);
			mGLThread.onWindowFocusChanged(true);
			//mGame.invalidateRenderElements = true;			
		}
		
	    @Override
	    public void onVisibilityChanged(boolean visible) {
			Log.d("GLEngine","VISIBILITY CHANGED "+visible);	    	
	        if (visible) {
	        	mGLThread.onResume();
	        } else {
	        	mGLThread.onPause();
	        }
	    }    		
		
		
		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			Log.d("GLEngine","SURFACE DESTROYED");
			super.onSurfaceDestroyed(holder);
			//mGLThread.surfaceDestroyed();
			mGLThread.requestExitAndWait();

		}
		
		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height){
			Log.d("GLEngine","SURFACE CHANGED w="+width+" h="+height);
			super.onSurfaceChanged(holder,format,width,height);
			mGLThread.onWindowResize(width, height);
			mGLThread.onWindowFocusChanged(true);
		}

		public Sd3dGame getGame() {
			return mGame;
		}

		public void setGame(Sd3dGame mGame) {
			this.mGame = mGame;
		}
		
	}

}
