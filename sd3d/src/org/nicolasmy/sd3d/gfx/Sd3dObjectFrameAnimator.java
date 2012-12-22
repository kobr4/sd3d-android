package org.nicolasmy.sd3d.gfx;

import java.util.ArrayList;

import org.nicolasmy.sd3d.math.Sd3dMatrix;

import android.util.Log;

public class Sd3dObjectFrameAnimator
{
  private ArrayList<FrameObject> mFrameObjectList = new ArrayList<FrameObject>();
  private int mCurrentDuration;
  private int mFrameIndice;
  private Sd3dObjectFrameAnimator mChild;
  private class FrameObject
  {
    public Sd3dObject mObject;
    public int mDuration;
    public float mTag[];
  }
  
  public void addFrame(Sd3dObject o, int duration, float [] tag)
  {
    FrameObject fo = new FrameObject();
    fo.mDuration = duration;
    fo.mObject = o;
    fo.mTag = tag;
    this.mFrameObjectList.add(fo);
  }

  private void makeFrameCurrent(int indice)
  {
    FrameObject fo = mFrameObjectList.get(indice);
    this.mFrameIndice = indice;
    this.mCurrentDuration = fo.mDuration;
    //Log.d("sd3d","Current frame "+this.mFrameIndice);
  }

  public void processFrame(int dt)
  {
	  if (this.mChild != null)
	  {
		  this.mChild.processFrame(dt);
	  }
	  
    this.mCurrentDuration -= dt;
    if (this.mCurrentDuration < 0)
    {
       int indice = (this.mFrameIndice + 1)%mFrameObjectList.size();  
       makeFrameCurrent(indice);
    }	
  }

  public Sd3dObject getCurrentObject()
  {
    FrameObject fo = mFrameObjectList.get(mFrameIndice);
    
    if (this.mChild != null)
    {
    	Sd3dObject o = this.mChild.getCurrentObject();
    	
    	//Set objet position
    	if (o.mPosition == null)
    	{
    		o.mPosition = new float[3];
    	}
    	
    	if (fo.mTag != null)
    	{
    		/*
	    	if (fo.mObject.mPosition != null)
	    	{
	    		o.mPosition[0] =  fo.mObject.mPosition[0] + fo.mTag[0];
	    		o.mPosition[1] =  fo.mObject.mPosition[1] + fo.mTag[1];
	    		o.mPosition[2] =  fo.mObject.mPosition[2] + fo.mTag[2];
	    	}
	    	else
	    	*/
	    	{
	    		o.mPosition[0] =  fo.mTag[0];
	    		o.mPosition[1] =  fo.mTag[1];
	    		o.mPosition[2] =  fo.mTag[2];   		
	    	}
	    	
	    	if (o.mTransformMatrix == null)
	    	{
	    		o.mTransformMatrix = Sd3dMatrix.convert33to44(fo.mTag, 3);
	    	}
    	}
    	
    	fo.mObject.mChild = o;
    }
    
    return fo.mObject;
  }
  
  public void addChild(Sd3dObjectFrameAnimator childAnimator)
  {
	  this.mChild = childAnimator;
  }
}
