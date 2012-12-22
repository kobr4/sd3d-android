package org.nicolasmy.sd3d.gfx.entity;

import org.nicolasmy.sd3d.gfx.Sd3dMaterial;
import org.nicolasmy.sd3d.gfx.Sd3dMesh;
import org.nicolasmy.sd3d.gfx.Sd3dObject;
import org.nicolasmy.sd3d.Sd3dGame;
import org.nicolasmy.sd3d.Sd3dGame.Sd3dInputEvent;

/**
 * Abstract class of a game entity, a game entity is an 
 * entity able to interract (ie : receive events)
 * @author kobr4
 *
 */
public abstract class Sd3dGameEntity
{
	public String mName;

	protected float mPosition[];
	protected float mOrientation[];
	
	public boolean isCamera;
	
	public boolean isActive;
	
	public boolean receiveInput = true;
	
	
	public boolean isReceiveInput() {
		return receiveInput;
	}

	public void setReceiveInput(boolean receiveInput) {
		this.receiveInput = receiveInput;
	}

	public boolean hasOnAccelerometerEvent;
	public void onAccelerometerEvent(Sd3dInputEvent event)
	{	
	}

	public boolean hasOnTouchEvent;
	public void onTouchEvent()
	{	
	}		
	
	public boolean hasOnKeyboardEvent;
	public void onKeyboardEvent(Sd3dInputEvent event)
	{
		
	}
	
	
	public boolean hasOnProcessFrame;
	public void onProcessFrame(int elapsedtime)
	{	
	}
	
	public void setObject(Sd3dObject obj)
	{
		hasObject = true;
		mObject = obj;
		obj.mPosition = mPosition;
	}
	
	public void mergeObject(Sd3dObject obj)
	{
		if (mObject == null)
			setObject(obj);
		else 
		{
			Sd3dMesh tmpMesh[] = mObject.mMesh;
			mObject.mMesh = new Sd3dMesh[tmpMesh.length + obj.mMesh.length];
			for (int i = 0;i < tmpMesh.length;i++)
				mObject.mMesh[i] = tmpMesh[i];
			for (int i = 0;i < obj.mMesh.length;i++)
				mObject.mMesh[i+tmpMesh.length] = obj.mMesh[i];			
			
			Sd3dMaterial tmpMaterial[] = mObject.mMaterial;
			mObject.mMaterial = new Sd3dMaterial[tmpMaterial.length + obj.mMaterial.length];
			for (int i = 0;i < tmpMaterial.length;i++)
				mObject.mMaterial[i] = tmpMaterial[i];
			for (int i = 0;i < obj.mMaterial.length;i++)
				mObject.mMaterial[i+tmpMaterial.length] = obj.mMaterial[i];					
			
		}
		
	}
	
	public boolean hasObject;
	public Sd3dObject mObject;
	
	public boolean hasProcessTimer;
	public int mLastTimer;
	public int mNextTimer;
	
	public boolean isMarkForDeletion;

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
	
	public void onProcessTimer()
	{
		
	}
	public void release(Sd3dGame game)
	{
		this.isMarkForDeletion = true;
		if (mObject != null)
		{
			game.mRenderer.destroyRenderElement(mObject);
		}
	}
}
