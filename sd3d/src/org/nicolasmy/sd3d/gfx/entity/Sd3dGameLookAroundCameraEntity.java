package org.nicolasmy.sd3d.gfx.entity;

import org.nicolasmy.sd3d.Sd3dGame.Sd3dInputEvent;

public class Sd3dGameLookAroundCameraEntity extends Sd3dGameCameraEntity {
	public Sd3dGameEntity mTargetEntity;
	float mDistance = -(float)20.0;
	float mOffsetOrientation[];
	
	public float getDistance() {
		return mDistance;
	}
	
	public void setDistance(float distance) {
		this.mDistance = distance;
	}
	
	public Sd3dGameLookAroundCameraEntity(float distance, Sd3dGameEntity entity)
	{
		this.isActive = true;
		this.hasOnProcessFrame = true;
		this.hasOnKeyboardEvent = true;
		mTargetEntity = entity;
		this.mOrientation[2] = 0.f;	
		
		this.mOrientation[1] = mTargetEntity.mOrientation[1];
		
		//vu 3/4
		//this.mOrientation[0] = 45;
		
		mOffsetOrientation = new float [3];
		
		
		this.mOffsetOrientation[1] =  this.mOffsetOrientation[1] + 180.f;
		this.mOrientation[1] =  this.mOrientation[1] + 180.f;			
		mDistance = -distance;
		//this.mPosition[1] = mTargetEntity.mPosition[1] = 1.f;
	}	
	
	public void onProcessFrame(int elapsedtime)
	{	
		//float targetPosition[] =  mTargetEntity.mPosition;
		float targetDirection[] = new float[4];
		targetDirection[1] = (float)1.0;
		/*
		android.opengl.Matrix.setRotateM(targetDirection,0,mTargetEntity.mOrientation[0],(float)1.0,(float)0.0,(float)0.0);
		android.opengl.Matrix.setRotateM(targetDirection,0,mTargetEntity.mOrientation[1],(float)0.0,(float)1.0,(float)0.0);
		android.opengl.Matrix.setRotateM(targetDirection,0,mTargetEntity.mOrientation[2],(float)0.0,(float)0.0,(float)1.0);			
		*/
		
		/*
		float rotationMatrix[] = new float [16];
		android.opengl.Matrix.setRotateEulerM (rotationMatrix, 0, mTargetEntity.mOrientation[0], mTargetEntity.mOrientation[2], mTargetEntity.mOrientation[1]);
		android.opengl.Matrix.multiplyMV(targetDirection,0,rotationMatrix,0,targetDirection,0);
		
		
		this.mPosition[0] = mTargetEntity.mPosition[0] - mDistance * targetDirection[0];
		//this.mPosition[1] = mTargetEntity.mPosition[1] - mDistance * targetDirection[2];
		
		this.mPosition[2] = mTargetEntity.mPosition[2] - mDistance * targetDirection[1];		
		*/
		float dirX = 0;
		float dirY = 1;
		
		double x = dirX * java.lang.Math.cos(mOffsetOrientation[1]* 3.1415/180) - dirY * java.lang.Math.sin(mOffsetOrientation[1] * 3.1415/180);
		double y = dirY * java.lang.Math.cos(mOffsetOrientation[1]* 3.1415/180) + dirX * java.lang.Math.sin(mOffsetOrientation[1] * 3.1415/180);
		
		this.mPosition[0] = mTargetEntity.mPosition[0] - mDistance * (float)x;
		
		//vu 3/4
		//this.mPosition[1] = mTargetEntity.mPosition[1] - mDistance;
		this.mPosition[2] = mTargetEntity.mPosition[2] - mDistance * (float)y;
		
		/*
		this.mPosition[0] = mTargetEntity.mPosition[0];
		this.mPosition[1] = mTargetEntity.mPosition[1];
		this.mPosition[2] = mTargetEntity.mPosition[2] + 3.0f;	
		*/
		
		/*
		this.mPosition[0] = 0;
		this.mPosition[1] = 0;
		this.mPosition[2] = 3.0f;	
			*/	
		
		
		//this.mOrientation[1] = mTargetEntity.mOrientation[1];
		
		/*
		this.mOrientation[0] = mTargetEntity.mOrientation[0];
		this.mOrientation[1] = mTargetEntity.mOrientation[1];
		this.mOrientation[2] = mTargetEntity.mOrientation[2] - 90.f;		
		*/	
	}
	
	public void rotate(float angle)
	{
		this.mOffsetOrientation[1] =  this.mOffsetOrientation[1] + angle;
		this.mOrientation[1] =  this.mOrientation[1] + angle;		
	}	
	
	public void rotateX(float angle)
	{
		this.mPosition[1] = mDistance * (float)Math.sin((float)angle);
		this.mOrientation[0] -= angle;
	}
	
	public void rotateLeft()
	{
		this.mOffsetOrientation[1] =  this.mOffsetOrientation[1] + 5.f;
		this.mOrientation[1] =  this.mOrientation[1] + 5.f;		
	}
	
	public void rotateRight()
	{
		this.mOffsetOrientation[1] =  this.mOffsetOrientation[1] - 5.f;
		this.mOrientation[1] =  this.mOrientation[1] - 5.f;
	}
		
	public void zoomIn()
	{
		mDistance += 0.1f;
	}	
	
	public void zoomOut()
	{
		mDistance -= 0.1f;
	}		
	
	
	public void onKeyboardEvent(Sd3dInputEvent event)
	{
		if (event.key == 1)
		{
			mDistance += 0.1f;
		}

		if (event.key == 3)
		{
			mDistance -= 0.1f;
		}
				
		
		if (event.key == 2)
		{
			this.mOffsetOrientation[1] =  this.mOffsetOrientation[1] + 5.f;
			this.mOrientation[1] =  this.mOrientation[1] + 5.f;
		}		

		if (event.key == 4)
		{
			this.mOffsetOrientation[1] =  this.mOffsetOrientation[1] - 5.f;
			this.mOrientation[1] =  this.mOrientation[1] - 5.f;
		}			
	}	
}
