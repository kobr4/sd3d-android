package org.nicolasmy.sd3d.gfx;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;

import org.nicolasmy.sd3d.gfx.Sd3dScene.Sd3dCamera;
import org.nicolasmy.sd3d.gfx.entity.Sd3dGameCameraEntity;
import org.nicolasmy.sd3d.gfx.entity.Sd3dGameEntity;

import android.util.Log;

public class Sd3dLensFlareEntity extends Sd3dGameEntity {
	Sd3dVector2d mScreenCenter = new Sd3dVector2d();
	Sd3dVector2d mLightPosition = new Sd3dVector2d();
	Sd3dVector2d mToFlare = new Sd3dVector2d();
	Sd3dVector2d mVector = new Sd3dVector2d();
	float flareCoefList[] = new float[4];
	float lightpos[] = new float[3];
	private Sd3dRendererInterface mRenderer;
	private Sd3dGameCameraEntity mCamera;
    public Sd3dGameCameraEntity getCamera() {
		return mCamera;
	}

	public void setCamera(Sd3dGameCameraEntity camera) {
		this.mCamera = camera;
	}

	public Sd3dLensFlareEntity()
    {
    	this.mObject = new Sd3dObject();
    	this.mObject.mMesh = new Sd3dMesh[4];
    	this.mObject.mMaterial = new Sd3dMaterial[4];
    	
    	
    	float size = 300.f;
    	this.mObject.mMesh[0] = new Sd3dMesh();
    	this.mObject.mMaterial[0] = new Sd3dMaterial();    	
    	setupFlareElement(this.mObject.mMesh[0],this.mObject.mMaterial[0],"flare/flare00.png",size);
    	flareCoefList[0] = 1.f;
    	
    	this.mObject.mMesh[1] = new Sd3dMesh();
    	this.mObject.mMaterial[1] = new Sd3dMaterial();    	
    	setupFlareElement(this.mObject.mMesh[1],this.mObject.mMaterial[1],"flare/flare01.png",size);
    	flareCoefList[1] = 0.5f;

    	this.mObject.mMesh[2] = new Sd3dMesh();
    	this.mObject.mMaterial[2] = new Sd3dMaterial();    	
    	setupFlareElement(this.mObject.mMesh[2],this.mObject.mMaterial[2],"flare/flare02.png",size);
    	flareCoefList[2] = -0.25f;
    	
    	this.mObject.mMesh[3] = new Sd3dMesh();
    	this.mObject.mMaterial[3] = new Sd3dMaterial();    	
    	setupFlareElement(this.mObject.mMesh[3],this.mObject.mMaterial[3],"flare/flare03.png",size);
    	flareCoefList[3] = -0.75f;

    	this.hasOnProcessFrame = true;  
    	this.hasObject = true;
    }
    
    public Sd3dRendererInterface getRenderer() {
		return mRenderer;
	}

	public void setRenderer(Sd3dRendererInterface renderer) {
		this.mRenderer = renderer;
	}

	private static void setupFlareElement(Sd3dMesh mesh,Sd3dMaterial material, String texture, float size)
    {
    	int nbTriangle = 2;
    	float x = -size/2.f;
    	float y = -size/2.f;
    	float z = 0.f;
    	mesh.mMeshPosition = new float[3];
    	mesh.mIndices = CharBuffer.allocate(nbTriangle*3);
    	mesh.mVertices = FloatBuffer.allocate(nbTriangle * 3 * 3);
    	mesh.mTexCoords = FloatBuffer.allocate(nbTriangle * 3 * 2);
    	
    	mesh.putTexturedQuad(x, y, z, x+size, y, z, x+size, y+size, z,  x, y+size, z,0f,0f,1f,0f,1f,1f,0f,1f);
    	//mesh.putTexturedQuad(x, y+size, z, x+size, y+size, z, x+size, y, z,  x, y, z,0f,0f,1f,0f,1f,1f,0f,1f);
    	
    	mesh.mIndices.position(0);
    	mesh.mVertices.position(0);
    	mesh.mTexCoords.position(0);
    	
    	mesh.mIsInScreenSpace = true;
    	
    	try {
			material.loadTexture(texture);
			material.alphaBlending = true;
		} catch (IOException e) {
			Log.d("setupFlareElement()","CANNOT LOAD TEXTURE: "+texture);
		}
    	
    }
  
	public void onProcessFrame(int elapsedtime)
	{	
		if (mRenderer.pointInFrustum(100.f+mCamera.getPosition()[0], -20.f, 100.f+mCamera.getPosition()[2]))
		{
			this.hasObject = true;
			this.mScreenCenter.set(this.mRenderer.getScreenWidth()*0.5f, this.mRenderer.getScreenHeight()*0.5f);
			this.mRenderer.pointToScreen(100.f+mCamera.getPosition()[0], -30.f, 100.f+mCamera.getPosition()[2],lightpos);
			
			//Log.d("LENS FLARE","LIGHT POSITION="+lightpos[0]+" "+lightpos[1]);
			//this.mLightPosition.set(600, 150);
			
			this.mLightPosition.set(lightpos[0], lightpos[1]);
			this.mToFlare.set(this.mLightPosition.getX(), this.mLightPosition.getY());
			this.mToFlare.sub(this.mScreenCenter);
			float length = this.mToFlare.length();
			this.mToFlare.mul(1.f/length);
			for (int i = 0; i < this.mObject.mMesh.length;i++)
			{
				mVector.set(this.mToFlare.getX(), this.mToFlare.getY());

				mVector.mul(length * flareCoefList[i]);
				mVector.add(this.mScreenCenter);
				this.mObject.mMesh[i].mMeshPosition[0] = mVector.getX();
				this.mObject.mMesh[i].mMeshPosition[1] = mVector.getY();
			}
		}
		else
		{
			//Log.d("LENS FLARE","NOT IN FRUSTUM !");
			this.hasObject = false;
		}
	}
}
