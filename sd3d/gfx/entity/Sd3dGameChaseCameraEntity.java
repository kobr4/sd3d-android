package org.nicolasmy.sd3d.gfx.entity;


public class Sd3dGameChaseCameraEntity extends Sd3dGameCameraEntity
{
	Sd3dGameEntity mTargetEntity;
	float mDistance = (float)5.0;
	
	public Sd3dGameChaseCameraEntity(Sd3dGameEntity entity)
	{
		this.isActive = true;
		this.hasOnProcessFrame = true;
		mTargetEntity = entity;
		//this.mOrientation[0] = 45;
		this.mOrientation[1] = 180f;
		//this.mOrientation[2] = -90.f;
		//this.mOrientation[2] = 90f;
		
		//this.mOrientation[0] = -90f;
		//this.mOrientation[1] = -90;
		//PC
		//this.mOrientation[2] = 0.f;
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
		/*
		float dirX = 0;
		float dirY = 1;
		double x = dirX * java.lang.Math.cos(mTargetEntity.mOrientation[1]* 3.1415/180) - dirY * java.lang.Math.sin(mTargetEntity.mOrientation[1] * 3.1415/180);
		double y = dirY * java.lang.Math.cos(mTargetEntity.mOrientation[1]* 3.1415/180) + dirX * java.lang.Math.sin(mTargetEntity.mOrientation[1] * 3.1415/180); 
		
		this.mPosition[0] = mTargetEntity.mPosition[0] - mDistance * (float)x;
		this.mPosition[1] = mTargetEntity.mPosition[2] - mDistance * (float)y;
		this.mPosition[0] = mTargetEntity.mPosition[1] + 1.f;
		*/
		float dirX = 0;
		float dirY = 1;
		double x = dirX * java.lang.Math.cos(mTargetEntity.mOrientation[1]* 3.1415/180) - dirY * java.lang.Math.sin(mTargetEntity.mOrientation[1] * 3.1415/180);
		double y = dirY * java.lang.Math.cos(mTargetEntity.mOrientation[1]* 3.1415/180) + dirX * java.lang.Math.sin(mTargetEntity.mOrientation[1] * 3.1415/180); 
		
		this.mPosition[0] = mTargetEntity.mPosition[0] - mDistance * (float)x;
		this.mPosition[1] = mTargetEntity.mPosition[1] + 1.0f;	
		this.mPosition[2] = mTargetEntity.mPosition[2] - mDistance * (float)y;
			
		/*
		float dirX = 0;
		float dirY = 1;
		
		double x = dirX * java.lang.Math.cos(mTargetEntity.mOrientation[1]* 3.1415/180) - dirY * java.lang.Math.sin(mTargetEntity.mOrientation[1] * 3.1415/180);
		double y = dirY * java.lang.Math.cos(mTargetEntity.mOrientation[1]* 3.1415/180) + dirX * java.lang.Math.sin(mTargetEntity.mOrientation[1] * 3.1415/180);
		
		this.mPosition[0] = mTargetEntity.mPosition[0] - mDistance * (float)x;
		this.mPosition[1] = mTargetEntity.mPosition[1] - mDistance;
		this.mPosition[2] = mTargetEntity.mPosition[2] - mDistance * (float)y;		
		*/
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
		
		
		this.mOrientation[1] = mTargetEntity.mOrientation[1] + 180.f;
		//PC
		//this.mOrientation[1] = mTargetEntity.mOrientation[1];
		
		/*
		this.mOrientation[0] = mTargetEntity.mOrientation[0];
		this.mOrientation[1] = mTargetEntity.mOrientation[1];
		this.mOrientation[2] = mTargetEntity.mOrientation[2] - 90.f;		
		*/	
	}
}