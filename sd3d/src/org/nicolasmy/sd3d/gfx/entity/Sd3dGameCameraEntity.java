package org.nicolasmy.sd3d.gfx.entity;

public class Sd3dGameCameraEntity extends Sd3dGameEntity
{
	public float rotationMatrix[];
	public Sd3dGameCameraEntity()
	{
		this.isCamera = true;
		
		this.mPosition = new float[3];	
		this.mOrientation = new float[3];				
	}
}
