package org.nicolasmy.sd3d.gfx.entity;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;

import org.nicolasmy.sd3d.gfx.Sd3dMaterial;
import org.nicolasmy.sd3d.gfx.Sd3dMesh;
import org.nicolasmy.sd3d.gfx.Sd3dObject;

import android.util.Log;

public class Sd3dCloudsEntity extends Sd3dGameEntity{
  private float mClipingTopX;
  private float mClipingTopY;
  private float mClipingBottomX;
  private float mClipingBottomY;
  private float mSize;
  private int mCloudLength;
  private float mVelocity[] = new float[3];
  public Sd3dCloudsEntity()
  {
    mCloudLength = 8;
    mSize = 100.f;

    float middleX = mCloudLength*mSize/2.f;
    float middleY = mCloudLength*mSize/2.f;
    this.mObject = new Sd3dObject();
    this.mObject.mMesh = new Sd3dMesh[mCloudLength*mCloudLength];
    this.mObject.mMaterial = new Sd3dMaterial[mCloudLength*mCloudLength];

    for (int i = 0;i < mCloudLength;i++)
      for (int j = 0;j < mCloudLength;j++)
      {
        this.mObject.mMesh[i*mCloudLength+j] = new Sd3dMesh();
        this.mObject.mMaterial[i*mCloudLength+j] = new Sd3dMaterial();
        setupCloudTile(i*mSize - middleX,-20f,j*mSize,mSize,"textures/clouds.png",
		this.mObject.mMesh[i*mCloudLength+j],this.mObject.mMaterial[i*mCloudLength+j]);
      }
    
    this.hasOnProcessFrame = true;
    this.hasObject = true;
    
    mClipingBottomX =  - middleX;
    mClipingBottomY = 0;
    mClipingTopX = mCloudLength * mSize  - middleX;
    mClipingTopY = mCloudLength * mSize;
    mVelocity[0] = 10.f;
  }

  public void setupCloudTile(float x,float y,float z,float size,String texture,Sd3dMesh mesh,Sd3dMaterial material)
  {
    float f = size / 2.f;
    int nbTriangle = 2;
    mesh.mVertices = FloatBuffer.allocate(nbTriangle * 3 * 3);
    mesh.mIndices = CharBuffer.allocate(nbTriangle*3);
    mesh.mTexCoords = FloatBuffer.allocate(nbTriangle * 3 * 2);
    //mesh.putTexturedQuad(-f,0,f,f,0,f,f,0,-f,-f,0,-f,0f,0f,1f,0f,1f,1f,0f,1f);

    mesh.putTexturedQuad(-f,0,-f,f,0,-f,f,0,f,-f,0,f,0f,0f,1f,0f,1f,1f,0f,1f);
    mesh.mVertices.position(0);
    mesh.mIndices.position(0);		
    mesh.mTexCoords.position(0);

    mesh.mMeshPosition = new float[3];
    mesh.mMeshPosition[0] = x;
    mesh.mMeshPosition[1] = y;
    mesh.mMeshPosition[2] = z;
    try {
       material.loadTexture(texture);
       material.alphaBlending = true;
    } catch (IOException e) {
      e.printStackTrace();
    }	
  }

  public void onProcessFrame(int elapsedtime)
  {
     float fDt = elapsedtime / 1000.f;
     for (int i = 0;i < mObject.mMesh.length;i++)
     {
    	 
       this.mObject.mMesh[i].mMeshPosition[0] += this.mVelocity[0] * fDt;
       this.mObject.mMesh[i].mMeshPosition[1] += this.mVelocity[1] * fDt;
       this.mObject.mMesh[i].mMeshPosition[2] += this.mVelocity[2] * fDt;

       if ((this.mObject.mMesh[i].mMeshPosition[0] < mClipingBottomX)||(this.mObject.mMesh[i].mMeshPosition[0] > mClipingTopX)
         ||(this.mObject.mMesh[i].mMeshPosition[2] < mClipingBottomY)||(this.mObject.mMesh[i].mMeshPosition[2] > mClipingTopY))
    //     if ((this.mObject.mMesh[i].mMeshPosition[0] < mClipingBottomX)
    //       ||(this.mObject.mMesh[i].mMeshPosition[2] < mClipingBottomY))
       
	{
		int signX = (int)((float)Math.abs(this.mVelocity[0]) / this.mVelocity[0]);
		float signY;
		if (this.mVelocity[2] != 0.f)
			signY = (float)Math.abs(this.mVelocity[2]) / this.mVelocity[2];
		else signY = 0.f;
		this.mObject.mMesh[i].mMeshPosition[0] = this.mObject.mMesh[i].mMeshPosition[0] - signX*mSize*mCloudLength;
		this.mObject.mMesh[i].mMeshPosition[2] = this.mObject.mMesh[i].mMeshPosition[2] + signY*mSize*mCloudLength;
		//Log.d("CLOUDS",this.mObject.mMesh[i].mMeshPosition[0]+" "+this.mObject.mMesh[i].mMeshPosition[2]);
	}   
         
     }
  }
}
