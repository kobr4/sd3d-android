package com.nicolasmy.bigbangwallpaper;

import org.nicolasmy.sd3d.Sd3dGLWallpaperService;
import org.nicolasmy.sd3d.Sd3dGLWallpaperService.GLEngine;
import org.nicolasmy.sd3d.gfx.Sd3dRessourceManager;


import android.util.Log;
import android.view.SurfaceHolder;

public class BigBangWallpaper extends Sd3dGLWallpaperService {

	private class BigBangEngine extends GLEngine
	{
		private BigBangFrameProcessor mProcessor;
		
		public BigBangEngine() {
			super();
			//Sd3dRessourceManager.Manager.setAssetManager(this.getAssets());
			mProcessor = new BigBangFrameProcessor();
			
		}
		
		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			
			super.onCreate(surfaceHolder);
			this.getGame().addFrameProcessor(mProcessor);	
			this.getGame().setShowFps(false);
			mProcessor.init();
		}			
		
		
		@Override
		public void onSurfaceCreated(SurfaceHolder holder){
			Log.d("BlueFalconWallpaper","Surface created= "+holder.getSurfaceFrame().width()+" "+holder.getSurfaceFrame().height());
			super.onSurfaceCreated(holder);			
			
		}
		
		
	    @Override
	    public void onVisibilityChanged(boolean visible) {
	        super.onVisibilityChanged(visible);
	        if (visible) {
	        	
	        } else {
	            
	        }
	    }    		
	}	
	
	@Override
	public Engine onCreateEngine() {
		Log.d("BlueFalconWallpaper","onCreateEngine() Entering");

		Sd3dRessourceManager.Manager = new Sd3dRessourceManager(this.getApplicationContext());
		Sd3dRessourceManager.Manager.setAssetManager(this.getAssets());
		BigBangEngine bb = new BigBangEngine();
			

		Log.d("BlueFalconWallpaper","onCreateEngine() Exiting");
		return bb;
	}

}
