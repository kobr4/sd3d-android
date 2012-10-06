package org.nicolasmy.sd3d.gfx;

import java.util.ArrayList;

public class Sd3dObjectFrameAnimator
{
  private ArrayList<FrameObject> mFrameObjectList;
  //private FrameObject mCurrentFrameObject;
  private int mCurrentDuration;
  private int mFrameIndice;
  public class FrameObject
  {
    public Sd3dObject mObject;
    public int mDuration;
  }
  
  public void addFrame(Sd3dObject o, int duration)
  {
    FrameObject fo = new FrameObject();
    fo.mDuration = duration;
    fo.mObject = o;
    this.mFrameObjectList.add(fo);
  }

  private void makeFrameCurrent(int indice)
  {
    FrameObject fo = mFrameObjectList.get(indice);
    //this.mCurrentFrameObject = fo;
    this.mFrameIndice = indice;
    this.mCurrentDuration = fo.mDuration;
  }

  public void processFrame(int dt)
  {
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
    return fo.mObject;
  }
}
