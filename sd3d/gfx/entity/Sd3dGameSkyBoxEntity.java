package org.nicolasmy.sd3d.gfx.entity;

import java.nio.CharBuffer;
import java.nio.FloatBuffer;

import org.nicolasmy.sd3d.gfx.Sd3dMaterial;
import org.nicolasmy.sd3d.gfx.Sd3dMesh;
import org.nicolasmy.sd3d.gfx.Sd3dObject;


public class Sd3dGameSkyBoxEntity extends Sd3dGameEntity
{
	Sd3dGameEntity mTargetEntity;
	public Sd3dGameSkyBoxEntity(Sd3dGameCameraEntity targetEntity)
	{
		mTargetEntity = targetEntity;
		float one = (float)80.0;
        
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

		float f1 = 1.0f;
		//float f2 = 0.5f;
		float colors[] = {
				0.1f,    0.1f,    f1,  f1,
				0.1f,    0.1f,    f1,  f1,
				0.8f,  0.8f,    f1,  f1,
				0.8f,  0.8f,    f1,  f1,
				0.1f,    0.1f,  f1,  f1,
				0.1f,    0.1f,  f1,  f1,
				0.8f,  0.8f,  f1,  f1,
				0.8f,  0.8f,  f1,  f1,
        };

        char indices[] = {
        		
                5, 4, 0,    1, 5, 0,
                6, 5, 1,    2, 6, 1,
                7, 6, 2,    3, 7, 2,
                4, 7, 3,    0, 4, 3,
                6, 7, 4,    5, 6, 4,
                1, 0, 3,    2, 1, 3                		
        };
        
        float texcoords[] = {
        		0.f,0.25f,
        		0.f,0.75f,
        		0.25f,0.25f, 
        		0.25f,0.75f,      
        		
        		1.f,0.25f,
        		1.f,0.75f,
        		0.75f,0.75f, 
        		0.75f,0.25f,              		
        };			
		
		mObject = new Sd3dObject();
		hasObject = true;
		mObject.mMesh = new Sd3dMesh[1];
		mObject.mMesh[0] = new Sd3dMesh();
		mObject.mMesh[0].mVertices = FloatBuffer.allocate(vertices.length);
		mObject.mMesh[0].mVertices.put(vertices);
		mObject.mMesh[0].mVertices.position(0);			
        
		mObject.mMesh[0].mTexCoords = FloatBuffer.allocate(texcoords.length);
		mObject.mMesh[0].mTexCoords.put(texcoords);
		mObject.mMesh[0].mTexCoords.position(0);	
		
		mObject.mMesh[0].mIndices = CharBuffer.allocate(indices.length);
		mObject.mMesh[0].mIndices.put(indices);
		mObject.mMesh[0].mIndices.position(0);	
		
		mObject.mMaterial = new Sd3dMaterial[1];
		mObject.mMaterial[0] = new Sd3dMaterial();
		mObject.mMaterial[0].mColors = FloatBuffer.allocate(colors.length);
		mObject.mMaterial[0].mColors.put(colors);
		mObject.mMaterial[0].mColors.position(0);		
		
		this.isActive = true;
		
		
		this.mPosition = new float[3];
		this.mPosition[0] = (float)0.;
		this.mPosition[1] = (float)0.;
		this.mPosition[2] = (float)0.;		
		
		this.mOrientation = new float[3];
		this.mOrientation[0] = (float)0.;
		this.mOrientation[1] = (float)0.;
		this.mOrientation[2] = (float)0.;		
		
		mObject.mPosition = this.mPosition;
		mObject.mRotation = this.mOrientation;	

		
		//loadTexture("/sdcard/textures/SkyBox-Clouds-Med-Early.png");
        hasOnProcessFrame = true;
	}		
	
	public void onProcessFrame(int elapsedtime)
	{	
		this.mPosition[0] = mTargetEntity.mPosition[0];		
		this.mPosition[1] = mTargetEntity.mPosition[1];	
		this.mPosition[2] = mTargetEntity.mPosition[2];				
	}
}	
