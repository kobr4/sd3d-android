package org.nicolasmy.sd3d.gfx.entity;

import java.io.IOException;

import org.nicolasmy.sd3d.gfx.Sd3dLight;
import org.nicolasmy.sd3d.gfx.Sd3dMaterial;
import org.nicolasmy.sd3d.gfx.Sd3dMesh;
import org.nicolasmy.sd3d.gfx.Sd3dObject;
import org.nicolasmy.sd3d.math.Sd3dVector;

import android.util.Log;

public class Sd3dGameMobileEntity extends Sd3dGameEntity
{
	protected float mAcceleration[];
	protected float mVelocity[];	
	protected float mFrictionC = (float)0.6;
	protected float mCollisionRadius;
	protected float mMass = 1.0f;
	
	public Sd3dGameMobileEntity()
	{
		mAcceleration = new float[3];
		mVelocity = new float[3];
	}
	
	public Sd3dGameMobileEntity(String mesh,String texture, float scale, boolean generateShadow)
	{
		this(mesh,texture,scale);
		
		
		if (generateShadow)
		{
			Sd3dVector lightVector = new Sd3dVector(); 
			lightVector.set(0, -1.0f);
			lightVector.set(1, 1.0f);
			lightVector.set(2, -1.0f);				
		
			Sd3dMesh shadowMesh = mObject.mMesh[0].buildShadowVolume(lightVector);
		
			Sd3dMesh[] meshes = new Sd3dMesh[2];
			meshes[0] = mObject.mMesh[0];
			meshes[1] = shadowMesh;
			meshes[1].mIsShadowVolume = true;
			mObject.mMesh = meshes;
		}
	}
	
	public Sd3dGameMobileEntity(String mesh,String texture, float scale)
	{
		this();
		
		mObject = new Sd3dObject();
		hasObject = true;
		mObject.mMesh = new Sd3dMesh[1];
		mObject.mMesh[0] = new Sd3dMesh();
		try {
			mObject.mMesh[0].load3ds(mesh,1,0f,0f,scale);
			mObject.mMesh[0].generateNormalsPerVertex();
		} catch (IOException e) {
			Log.d("Sd3dGameMobileEntity()","Error while loading mesh "+e.toString());
		}
		mObject.mMesh[0].mVertices.position(0);
		
		mObject.mMaterial = new Sd3dMaterial[1];
		mObject.mMaterial[0] = new Sd3dMaterial();
		try {
			mObject.mMaterial[0].loadTexture(texture);
			mObject.mMaterial[0].renderLight = false;
			
			Sd3dVector lightVector = new Sd3dVector(); 
			lightVector.set(0, 1.0f);
			lightVector.set(1, -1.0f);
			lightVector.set(2, 1.0f);				
			
			Sd3dLight.computeLighting(lightVector,mObject.mMesh[0],mObject.mMaterial[0]);
		} catch (IOException e) {
			Log.d("Sd3dGameMobileEntity()","Error while loading texture "+e.toString());			
		}
		mOrientation = new float[3];
		mPosition = new float[3];
		mObject.mPosition = this.mPosition;
		mObject.mRotation = new float[3];
		
		this.hasOnProcessFrame = false;
		this.hasOnTouchEvent = false;
		this.hasOnAccelerometerEvent = false;
		this.setCollisionRadius(1.f);		
		
		Sd3dVector v = new Sd3dVector(10,-10,10);
		
		//Sd3dLight.computeLighting(v,mObject.mMesh[0],mObject.mMaterial[0]);
	}
	
	public float getMass() {
		return mMass;
	}

	public void setMass(float mass) {
		this.mMass = mass;
	}

	public void setAcceleration(float x, float y, float z)
	{
		mAcceleration[0] = x;
		mAcceleration[1] = y;
		mAcceleration[2] = z;		
	}
	
	public void setCollisionRadius(float r)
	{
		this.mCollisionRadius = r;
	}
	
	public float getCollisionRadius()
	{
		return this.mCollisionRadius;
	}
	
	public float[] getAcceleration()
	{
		return mAcceleration;
	}

	public float[] getVelocity()
	{
		return mVelocity;
	}	
	
	public void setVelocity(float x, float y, float z)
	{
		this.mVelocity[0] = x;
		this.mVelocity[1] = y;
		this.mVelocity[2] = z;
	}
	
	public void onPhysics(float dt)
	{
		//Setup gravity in a dirty way
		mAcceleration[0] = mAcceleration[0];
		mAcceleration[1] = mAcceleration[1] - 9.8f;
		mAcceleration[2] = mAcceleration[2];		
		
		mVelocity[0] = mVelocity[0] - mVelocity[0] * mFrictionC * dt;
		mVelocity[1] = mVelocity[1] - mVelocity[1] * mFrictionC * dt;
		mVelocity[2] = mVelocity[2] - mVelocity[2] * mFrictionC * dt;
		
		mPosition[0] = mPosition[0] + mVelocity[0] * dt;
		mPosition[1] = mPosition[1] + mVelocity[1] * dt;
		mPosition[2] = mPosition[2] + mVelocity[2] * dt;				
		
		mVelocity[0] = mVelocity[0] + mAcceleration[0] * mMass * dt;
		mVelocity[1] = mVelocity[1] + mAcceleration[1] * mMass * dt;
		mVelocity[2] = mVelocity[2] + mAcceleration[2] * mMass * dt;			
		
		mAcceleration[0] = 0;
		mAcceleration[1] = 0;
		mAcceleration[2] = 0;
	}
}
