package org.nicolasmy.sd3d.gfx.entity;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;

import org.nicolasmy.sd3d.gfx.Sd3dMaterial;
import org.nicolasmy.sd3d.gfx.Sd3dMesh;
import org.nicolasmy.sd3d.gfx.Sd3dObject;

import android.util.Log;

public class Sd3dGameSkyBoxEntityPerFace  extends Sd3dGameEntity
{
	Sd3dGameEntity mTargetEntity;
	public Sd3dGameSkyBoxEntityPerFace(Sd3dGameCameraEntity targetEntity)
	{
		mTargetEntity = targetEntity;
		float one = (float)500.0;
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
		
        float texcoords[] = {
        		/*
        		(float)0.,(float)0.,
        		(float)0.,(float)1.,
        		(float)1.,(float)1., 
        		
        		(float)1.,(float)0., 
        		(float)0.,(float)0.,
        		(float)1.,(float)1.,    		
        		*/ 	   
        		
        		(float)1.,(float)0.,
        		(float)1.,(float)1.,
        		(float)0.,(float)1., 
        		
        		(float)0.,(float)0., 
        		(float)1.,(float)0.,
        		(float)0.,(float)1.,          		
        };					
        
        char indices[] = {
        		
                5, 4, 0,    1, 5, 0,
                6, 5, 1,    2, 6, 1,
                7, 6, 2,    3, 7, 2,
                   3, 0, 4,  7, 3, 4,
                //0, 7, 3,    7, 0, 4,
                7, 4, 5,	6, 7, 5,
                2, 1, 0, 	3, 2, 0               		
        };        
		
        /*
		float f1 = 1.0f;
		
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
        */
		float colors[] = {
				1f, 1f, 1f,  1f,
				1f, 1f, 1f,  1f,
				1f, 1f, 1f,  1f,
				1f, 1f, 1f,  1f,
				1f, 1f, 1f,  1f,
				1f, 1f, 1f,  1f,
				1f, 1f, 1f,  1f,
				1f, 1f, 1f,  1f,
        };        
		/*
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
		*/
        /*
        char indicesFace0[] = { 5, 4, 0,    1, 5, 0 };
   
        char indicesFace1[] = { 6, 5, 1,    2, 6, 1 };
        
        char indicesFace2[] = { 7, 6, 2,    3, 7, 2 };
        
        char indicesFace3[] = { 4, 7, 3,    0, 4, 3 };  
        
        char indicesFace4[] = { 6, 7, 4,    5, 6, 4 };         
		
        char indicesFace5[] = { 1, 0, 3,    2, 1, 3 };     
        */
		mObject = new Sd3dObject();
		hasObject = true;
		mObject.mMesh = new Sd3dMesh[6];
		mObject.mMaterial = new Sd3dMaterial[6];
		for (int i = 0;i < 6;i++)
		{
			
			
			mObject.mMesh[i] = new Sd3dMesh();
			mObject.mMesh[i].mVertices = FloatBuffer.allocate(18);
			mObject.mMesh[i].mIndices = CharBuffer.allocate(6);
			mObject.mMaterial[i] = new Sd3dMaterial();
			mObject.mMaterial[i].mColors = FloatBuffer.allocate(24);
			for (int j = 0;j < 6;j++)
			{
				mObject.mMesh[i].mIndices.put((char)j);
				mObject.mMesh[i].mVertices.put(vertices[indices[i*6+j]*3]);
				mObject.mMesh[i].mVertices.put(vertices[indices[i*6+j]*3+1]);
				mObject.mMesh[i].mVertices.put(vertices[indices[i*6+j]*3+2]);
				
				mObject.mMaterial[i].mColors.put(colors[indices[i*6+j]*4]);
				mObject.mMaterial[i].mColors.put(colors[indices[i*6+j]*4+1]);
				mObject.mMaterial[i].mColors.put(colors[indices[i*6+j]*4+2]);	
				mObject.mMaterial[i].mColors.put(colors[indices[i*6+j]*4+3]);	
			}
			//mObject.mMesh[0].mVertices.put(vertices);
			mObject.mMesh[i].mVertices.position(0);	
			mObject.mMesh[i].mIndices.position(0);	
			mObject.mMaterial[i].mColors.position(0);
			mObject.mMesh[i].mTexCoords = FloatBuffer.allocate(texcoords.length);
			mObject.mMesh[i].mTexCoords.put(texcoords);
			mObject.mMesh[i].mTexCoords.position(0);  
			      			
			
	        /*
			mObject.mMesh[0].mIndices = CharBuffer.allocate(indicesFace0.length);
			mObject.mMesh[0].mIndices.put(indicesFace0);
			mObject.mMesh[0].mIndices.position(0);	
			*/
		}
		
		this.mOrientation = new float[3];
		/*
		mObject.mMaterial = new Sd3dMaterial[6];
		for (int i = 0; i < 6; i++)
		{
			mObject.mMaterial[i] = new Sd3dMaterial();

			mObject.mMaterial[i].mColors = FloatBuffer.allocate(colors.length);
			mObject.mMaterial[i].mColors.put(colors);
			mObject.mMaterial[i].mColors.position(0);				
		}
		*/
		
		this.isActive = true;	
		hasOnProcessFrame = true;
		
		this.mPosition = new float[3];
		this.setPosition(0, 50, 0);
		
		mObject.mPosition = this.mPosition;
	}
	
	public void setTextureLeft(String filename)
	{
		try {
			mObject.mMaterial[1].loadTexture(filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setTextureFront(String filename)
	{
		try {
			mObject.mMaterial[4].loadTexture(filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	public void setTextureRight(String filename)
	{
		try {
			mObject.mMaterial[3].loadTexture(filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}		
	
	public void setTextureBack(String filename)
	{
		try {
			mObject.mMaterial[5].loadTexture(filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}		
	
	public void setTextureBottom(String filename)
	{
		try {
			mObject.mMaterial[0].loadTexture(filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}		
		
	
	public void onProcessFrame(int elapsedtime)
	{	
		this.mPosition[0] = mTargetEntity.mPosition[0];		
		this.mPosition[1] = mTargetEntity.mPosition[1];	
		this.mPosition[2] = mTargetEntity.mPosition[2];
	}
	
}
