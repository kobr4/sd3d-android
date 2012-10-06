package com.nicolasmy.bigbangwallpaper;

import org.nicolasmy.sd3d.GameHolder;
import org.nicolasmy.sd3d.gfx.entity.Sd3dGameLookAroundCameraEntity;
import org.nicolasmy.sd3d.gfx.entity.Sd3dParticlesEffectHPEntity;

import android.util.Log;

public class BigBangFrameProcessor implements org.nicolasmy.sd3d.interfaces.Sd3dFrameProcessorInterface {

	private Sd3dGameLookAroundCameraEntity mCamera;
	public void init()
	{
		Sd3dParticlesEffectHPEntity source = new Sd3dParticlesEffectHPEntity(10000);
		source.setSource(0.0f, 0.0f, 0f, 1000);
		source.setParticleSpeedInterval(-3f, 3f, -3f, 3f, -3f, 3f);
		source.isActive = true;		
		
		mCamera = new Sd3dGameLookAroundCameraEntity(source);
		mCamera.isActive = true;
		GameHolder.mGame.addEntity(source);
		GameHolder.mGame.addEntity(mCamera);
	}
	
	@Override
	public void processFrame() {
		if (mCamera != null)
		{
			
			mCamera.rotate(0.5f);
		}
	}

}
