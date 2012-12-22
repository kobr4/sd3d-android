package org.nicolasmy.sd3d.gfx.entity;

import java.util.List;

import org.nicolasmy.sd3d.gfx.Sd3dMaterial;
import org.nicolasmy.sd3d.gfx.Sd3dMesh;
import org.nicolasmy.sd3d.gfx.Sd3dObject;
import org.nicolasmy.sd3d.gfx.Sd3dObjectFrameAnimator;

import android.util.Log;

public class Sd3dGameAnimatedEntity extends Sd3dGameMobileEntity{
	protected Sd3dObjectFrameAnimator mAnimator;
	public Sd3dGameAnimatedEntity(Sd3dObjectFrameAnimator animator)
	{
		super();
		mAnimator = animator;
		
		
		hasOnProcessFrame = true;
	}
	
	@Override
	public void onProcessFrame(int elapsedtime)
	{
		super.onProcessFrame(elapsedtime);
		mAnimator.processFrame(elapsedtime);
		mObject = mAnimator.getCurrentObject();
	}
}
