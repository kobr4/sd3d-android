package org.nicolasmy.sd3d.gfx.entity;

import java.nio.CharBuffer;
import java.nio.FloatBuffer;
import java.util.Random;

import org.nicolasmy.sd3d.gfx.Sd3dMaterial;
import org.nicolasmy.sd3d.gfx.Sd3dMesh;
import org.nicolasmy.sd3d.gfx.Sd3dObject;


public class Sd3dParticlesEffectEntity extends Sd3dGameEntity{
	public class Sd3dParticle
	{
		float x;
		float y;
		float z;
		float xspeed;
		float yspeed;
		float zspeed;
		int lifetime;
		boolean isActive;
	}
	
	public Sd3dParticle mParticlesArray[];
	private float mXSource;
	private float mYSource;
	private float mZSource;	
	
	private float mXSpeedMin;
	private float mYSpeedMin;
	private float mZSpeedMin;
	
	private float mXSpeedMax;
	private float mYSpeedMax;
	private float mZSpeedMax;	
	
	Random mRandom;
	
	private int mEmitingSpeed;
	private int mLastEmission;
	
	//private Sd3dObject mBackupObject;
	
	
	public Sd3dParticlesEffectEntity(int maxParticles)
	{
		mParticlesArray = new Sd3dParticle[maxParticles];
		for (int i = 0;i < maxParticles;i++)
		{
			mParticlesArray[i] = new Sd3dParticle();
		}
		
		setupObject(maxParticles);

		this.mOrientation = new float[3];
		this.mPosition = new float[3];
		
		this.isActive = true;		
		this.hasOnProcessFrame = true;
		this.hasObject = true;
		
		mRandom = new Random();
	}
	
	public void setSource(float x,float y,float z,int emitingSpeed)
	{
		mXSource = x;
		mYSource = y;
		mZSource = z;
		mEmitingSpeed = emitingSpeed;
	}
	
	public static float generateFloat(Random rand,float fmin,float fmax)
	{
		float finterval = fmax - fmin;
		float frand = rand.nextFloat();
		return fmin + frand * finterval;
	}
	
	public void setParticleSpeedInterval(float xmin,float xmax,float ymin,float ymax,float zmin,float zmax)
	{
		mXSpeedMin = xmin;
		mYSpeedMin = ymin;
		mZSpeedMin = zmin;
		
		mXSpeedMax = xmax;
		mYSpeedMax = ymax;
		mZSpeedMax = zmax;			
	}
	
	
	private void setupObject(int maxParticles)
	{
		float one = 0.1f;	
		
		float vertices[] = {
                -one, -one, -one,
                one, -one, -one,
                one,  one, -one,
                -one,  one, -one,
                -one, -one,  one,
                one, -one,  one,
                one,  one,  one,
                -one,  one,  one,
        };
/*
		float colors[] = {
                0,    0,    0,  one,
                one,    0,    0,  one,
                one,  one,    0,  one,
                0,  one,    0,  one,
                0,    0,  one,  one,
                one,    0,  one,  one,
                one,  one,  one,  one,
                0,  one,  one,  one,
        };
        
*/
		float colors[] = {
                1.f,    1.f,    1.f,  1.f,
                1.f,    1.f,    1.f,  1.f,
                1.f,    1.f,    1.f,  1.f,
                1.f,    1.f,    1.f,  1.f,
                1.f,    1.f,    1.f,  1.f,
                1.f,    1.f,    1.f,  1.f,
                1.f,    1.f,    1.f,  1.f,
                1.f,    1.f,    1.f,  1.f,
        };		
		

        char indices[] = {
        		
                0, 4, 5,    0, 5, 1,
                1, 5, 6,    1, 6, 2,
                2, 6, 7,    2, 7, 3,
                3, 7, 4,    3, 4, 0,
                4, 7, 6,    4, 6, 5,
                3, 0, 1,    3, 1, 2
                
        		
                        		
        };
		
		mObject = new Sd3dObject();
		mObject.mMesh = new Sd3dMesh[maxParticles];
		mObject.mMaterial = new Sd3dMaterial[maxParticles];		
		for (int i = 0;i < maxParticles;i++)
		{
			mObject.mMesh[i] = new Sd3dMesh();
			mObject.mMesh[i].mVertices = FloatBuffer.allocate(vertices.length);
			mObject.mMesh[i].mVertices.put(vertices);
			mObject.mMesh[i].mVertices.position(0);			
	        
			/*
			mObject.mMesh[i].mTexCoords = FloatBuffer.allocate(texcoords.length);
			mObject.mMesh[i].mTexCoords.put(texcoords);
			mObject.mMesh[i].mTexCoords.position(0);	
			*/
			
			mObject.mMesh[i].mIndices = CharBuffer.allocate(indices.length);
			mObject.mMesh[i].mIndices.put(indices);
			mObject.mMesh[i].mIndices.position(0);
			mObject.mMesh[i].setMeshPosition(0, 0, 0);
			
			mObject.mMaterial[i] = new Sd3dMaterial();
			mObject.mMaterial[i].mColors = FloatBuffer.allocate(colors.length);
			mObject.mMaterial[i].mColors.put(colors);
			mObject.mMaterial[i].mColors.position(0);				
		}
	}
	
	
	public void onProcessFrame(int elapsedtime)
	{
		if (elapsedtime == 0)
		 return;
		
		float felapsedtime = elapsedtime /1000.f;		
		mLastEmission += elapsedtime;
		int emitingCount = 0;
		for (int i = 0;i < mParticlesArray.length;i++)
		{
			if (mParticlesArray[i].isActive)
			{
				if (mParticlesArray[i].lifetime < elapsedtime)
				{
					mParticlesArray[i].isActive = false;
					
					if (mObject.mRenderElement != null)
						mObject.mRenderElement[i].mDisable = true;						
				} else mParticlesArray[i].lifetime = mParticlesArray[i].lifetime - elapsedtime;
				
				mParticlesArray[i].x += mParticlesArray[i].xspeed * felapsedtime;
				mParticlesArray[i].y += mParticlesArray[i].yspeed * felapsedtime;
				mParticlesArray[i].z += mParticlesArray[i].zspeed * felapsedtime;
				
				mObject.mMesh[i].setMeshPosition(mParticlesArray[i].x, mParticlesArray[i].y, mParticlesArray[i].z);
							
			}
			else
			{
				if (mObject.mRenderElement != null)
					mObject.mRenderElement[i].mDisable = true;			
				
				if ((emitingCount < mEmitingSpeed)&&(mLastEmission > 100))
				{
					
					
					mParticlesArray[i].x = mXSource;
					mParticlesArray[i].y = mYSource;		
					mParticlesArray[i].z = mZSource;	
					
					mParticlesArray[i].xspeed = generateFloat(mRandom,mXSpeedMin,mXSpeedMax);
					mParticlesArray[i].yspeed = generateFloat(mRandom,mYSpeedMin,mYSpeedMax);
					mParticlesArray[i].zspeed = generateFloat(mRandom,mZSpeedMin,mZSpeedMax);
					mParticlesArray[i].lifetime = 1000;
					emitingCount++;
					mParticlesArray[i].isActive = true;	
					
					if (mObject.mRenderElement != null)
						mObject.mRenderElement[i].mDisable = false;
					
					mObject.mMesh[i].setMeshPosition(mParticlesArray[i].x, mParticlesArray[i].y, mParticlesArray[i].z);
					
					mLastEmission = 0;
				}					
			}
		}
	}
	
}
