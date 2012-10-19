package org.nicolasmy.sd3d.gfx;

import java.util.ArrayList;


public class Sd3dScene
{
	public class Sd3dCamera
	{
		private float mPosition[];
		private float mOrientation[];
		private float mRotationMatrix[];
		
		Sd3dCamera()
		{
			mPosition = new float[3];
			mOrientation = new float[3];
		}
		
		public float[] getPosition()
		{
			return this.mPosition;
		}
		
		public void setPosition(float x,float y,float z)
		{
			mPosition[0] = x;
			mPosition[1] = y;
			mPosition[2] = z;
		}
		
		public float[] getOrientation()
		{
			return this.mOrientation;
		}		
		
		public void setOrientation(float Rx,float Ry,float Rz)
		{
			mOrientation[0] = Rx;
			mOrientation[1] = Ry;
			mOrientation[2] = Rz;			
		}

		public float[] getRotationMatrix() {
			return mRotationMatrix;
		}

		public void setRotationMatrix(float mRotationMatrix[]) {
			this.mRotationMatrix = mRotationMatrix;
		}			
	}	
	private Sd3dCamera mCamera;
	public Sd3dObject mObjectList[];
	private int mMaxObject;
	public int mCountObject;	
	private int mTriangleCount;	
	private Sd3dLight ambientLight;
	private ArrayList<Sd3dLight> directionLight = new ArrayList<Sd3dLight>();
	private ArrayList<Sd3dLight> positionLight = new ArrayList<Sd3dLight>();
	
	public Sd3dScene(int maxObject)
	{
		this.mMaxObject = maxObject;
	    this.mCountObject = 0;
	    this.mObjectList = new Sd3dObject[maxObject];
	    this.mCamera = new Sd3dCamera();
	}
	
	public void setCamera(float position[],float orientation[],float rotationMatrix[])
	{
		this.mCamera.setPosition(position[0], position[1], position[2]);
		this.mCamera.setOrientation(orientation[0], orientation[1], orientation[2]);
		this.mCamera.setRotationMatrix(rotationMatrix);
	}
	
	public Sd3dCamera getCamera()
	{
		return this.mCamera;
	}
	
	public void addLight(Sd3dLight light)
	{
		switch (light.getLighType())
		{
			case AMBIENT : 
				this.setAmbientLight(light);
				break; 
			case DIRECTION : 
				this.addDirectionLight(light);
				break;
			case POINT : 
				this.addPointLight(light);
				break;
		}
	}
	
	public void addObject(Sd3dObject object)
	{

		if (mCountObject < mMaxObject)
		{
			mObjectList[mCountObject] = object;
			if (object.mMesh != null)
			{
				for (int i = 0;i < object.mMesh.length;i++)
					if (object.mMesh[i] != null)
						mTriangleCount += (int) (object.mMesh[i].mIndices.capacity() / 3.);
			}
			
			mCountObject++;
		}		
	}
	
	public int getObjectCount()
	{
		return mCountObject;
	}
	
	public int getTriangleCount()
	{
		return mTriangleCount;
	}	
	
	public void initScene()
	{
		mCountObject = 0;
		mTriangleCount = 0;
		this.directionLight.clear();
		this.positionLight.clear();
	}

	public Sd3dLight getAmbientLight() {
		return ambientLight;
	}

	public void setAmbientLight(Sd3dLight ambientLight) {
		this.ambientLight = ambientLight;
	}

	public ArrayList<Sd3dLight> getDirectionLight() {
		return directionLight;
	}

	public void addDirectionLight(Sd3dLight directionLight)
	{
		this.directionLight.add(directionLight);
	}
	
	public void addPointLight(Sd3dLight positionLight)
	{
		this.positionLight.add(positionLight);
	}
	
	public void setDirectionLight(ArrayList<Sd3dLight> directionLight) {
		this.directionLight = directionLight;
	}

	public ArrayList<Sd3dLight> getPositionLight() {
		return positionLight;
	}

	public void setPositionLight(ArrayList<Sd3dLight> positionLight) {
		this.positionLight = positionLight;
	}
}
