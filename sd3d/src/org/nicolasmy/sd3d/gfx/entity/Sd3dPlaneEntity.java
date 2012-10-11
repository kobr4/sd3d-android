package org.nicolasmy.sd3d.gfx.entity;

import java.util.ArrayList;

import org.nicolasmy.sd3d.interfaces.Sd3dCollisionAgainstInterface;
import org.nicolasmy.sd3d.math.Sd3dVector;

import android.util.Log;

public class Sd3dPlaneEntity extends Sd3dGameEntity implements Sd3dCollisionAgainstInterface{
	Sd3dVector mNormal;
	Sd3dVector mHelper;
	Sd3dVector mHelper2;
	Sd3dVector mNormal2;
	float mDistanceToOrigin = 0;
	Sd3dVector mIntersection  = new Sd3dVector();
	ArrayList<Sd3dVector> mPolyPoint = new ArrayList<Sd3dVector>();
	private int contactPrecision = 100;
  public int getContactPrecision() {
		return contactPrecision;
	}
	public void setContactPrecision(int contactPrecision) {
		this.contactPrecision = contactPrecision;
	}
public Sd3dPlaneEntity(float xnormal,float ynormal, float znormal, float distanceToOrigin)
  {
	  this.mNormal = new Sd3dVector();
	  this.mNormal2 = new Sd3dVector();
	  this.mHelper = new Sd3dVector();
	  this.mHelper2 = new Sd3dVector();
	  this.mDistanceToOrigin = distanceToOrigin;
	  this.setNormal(xnormal, ynormal, znormal);
  }
  public void setNormal(float x, float y, float z)
  {
	this.mNormal.set(0, x);
	this.mNormal.set(1, y);
	this.mNormal.set(2, z);
	
	this.mNormal2.set(0,1.5f*x);
	this.mNormal2.set(1,1.5f*y);
	this.mNormal2.set(2,1.5f*z);
  }
  
  public void addPolyPoint(float x, float y, float z)
  {
	  mPolyPoint.add(new Sd3dVector(x,y,z));
  }
  private float distanceTo(Sd3dGameMobileEntity entity)
  {
	  float d = 0;
	  this.mHelper.set(entity.getPosition());
	  //Log.d("distanceTo()"," "+entity.getPosition()[1]);	  
	  //Log.d("distanceTo()"," "+this.mNormal.get(1));
	  d = Sd3dVector.dot(mNormal, mHelper) - entity.getCollisionRadius() + this.mDistanceToOrigin;
	  return d;
  }
  
  private void computeIntersection(Sd3dGameMobileEntity entity, float distance)
  {
	  mIntersection.set(0,entity.getPosition()[0] - distance * mNormal.get(0));
	  mIntersection.set(1,entity.getPosition()[1] - distance * mNormal.get(1));
	  mIntersection.set(2,entity.getPosition()[2] - distance * mNormal.get(2));
  }
  
  private double acos(float x) {
	   return (-0.69813170079773212 * x * x - 0.87266462599716477) * x + 1.5707963267948966;
	}  
  
  private boolean checkIntersectionIsInbound()
  {
	  boolean ret = false;
	  float angle = 0f;
	  float d = 0f;
	  for (int i = 0;i < this.mPolyPoint.size();i++)
	  {
		  Sd3dVector v1 = this.mPolyPoint.get(i);
		  Sd3dVector v2 = this.mPolyPoint.get((i+1)%this.mPolyPoint.size());
		  
		  Sd3dVector.sub(this.mHelper,v1,mIntersection);
		  Sd3dVector.sub(this.mHelper2,v2,mIntersection);		  
		  d = Sd3dVector.dot(this.mHelper, this.mHelper2);
		  float a = this.mHelper.length();
		  float b = this.mHelper2.length();
		  //Log.d("checkIntersectionIsInbound()","x="+v1.get(0)+" y="+v1.get(1)+" z="+v1.get(2)); 
		  //Log.d("checkIntersectionIsInbound()"," d="+d / (a * b));
		  //d = java.lang.Math.abs(d);
		  if ((a != 0.f)||(b != 0.f))
		  angle += acos(d / (a * b));
	  }
	  
	  int a = (int)(angle * 100f); 
	  int b = (int)(2 * java.lang.Math.PI * 100f);
	  //Log.d("checkIntersectionIsInbound()",a+" "+b);
	  if (a > b)
		  a = a - b;
	  else a = b - a;
	  if (a < contactPrecision)
		  return true;
	  //Log.d("computeIntersection()","x="+mIntersection.get(0)+" y="+mIntersection.get(1)+" z="+mIntersection.get(2));	  
	  //Log.d("checkIntersectionIsInbound()"," d="+d / (a * b));
	  return false;
  }
  
  public synchronized void collideAgainst(Sd3dGameMobileEntity entity)
  {
	 
	  float d = this.distanceTo(entity);
	  //Log.d("distanceTo()"," "+d);
	  if (d < 0)
	  {
		  //Log.d("collideAgainst()",">[collision]<");
		  this.mHelper.set(entity.getVelocity());	  
		  float vn = Sd3dVector.dot(this.mNormal, this.mHelper);
		  //Log.d("collideAgainst()","In collision => "+vn);
		  //if mobile velocity is towards plane
		  if (vn < 0)
		  {
			  if (this.mPolyPoint.size() > 0)
			  {
				  computeIntersection(entity,d);
				  if (checkIntersectionIsInbound() == false)
					  return;
			  }
			  
			  this.mHelper.set(this.mNormal2);
			  this.mHelper.mul(-vn);
			  this.mHelper.add(entity.getVelocity());
			  entity.setVelocity(this.mHelper.get(0), this.mHelper.get(1), this.mHelper.get(2));
			  
			  //entity.setVelocity(0f, 0f, 0f);
			  if (-d < entity.getCollisionRadius())
				  entity.setPosition(mIntersection.get(0), mIntersection.get(1), mIntersection.get(2));
		  }
	  }
  }
  /*
	public void onProcessFrame(int elapsedtime)
	{	
		//float dt = (float)elapsedtime / 1000.f;		
	}
	*/
}
