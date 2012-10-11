package org.nicolasmy.sd3d.math;

import java.nio.FloatBuffer;

import org.nicolasmy.sd3d.gfx.Sd3dMesh;

public class Sd3dVector {
	float mT[];
	public Sd3dVector(){
		mT = new float[3];
	}
	
	public Sd3dVector(float x, float y, float z)
	{
		this();
		this.set(0, x);
		this.set(1, y);
		this.set(2, z);
	}
	
	public Sd3dVector clone()
	{
		return new Sd3dVector(this.get(0),this.get(1),this.get(2));
	}
	
	public Sd3dVector(FloatBuffer fb,int indice,int nbFloatPerVertex)
	{
		mT[0] = fb.get(indice*nbFloatPerVertex);
		mT[1] = fb.get(indice*nbFloatPerVertex+1);
		mT[2] = fb.get(indice*nbFloatPerVertex+2);		
	}
	
	public void set(Sd3dVector v)
	{
		this.set(0,v.get(0));
		this.set(1,v.get(1));
		this.set(2,v.get(2));
	}
	
	public void set(float v[])
	{
		this.set(0, v[0]);
		this.set(1, v[1]);
		this.set(2, v[2]);		
	}
	
	public void setFromVertice(FloatBuffer fb,int indice)
	{
		mT[0] = fb.get(indice*Sd3dMesh.nbFloatPerVertex);
		mT[1] = fb.get(indice*Sd3dMesh.nbFloatPerVertex+1);
		mT[2] = fb.get(indice*Sd3dMesh.nbFloatPerVertex+2);			
	}
	
	public void setFromNormal(FloatBuffer fb,int indice)
	{
		mT[0] = fb.get(indice*Sd3dMesh.nbFloatPerVertex+3);
		mT[1] = fb.get(indice*Sd3dMesh.nbFloatPerVertex+4);
		mT[2] = fb.get(indice*Sd3dMesh.nbFloatPerVertex+5);			
	}	
	
	public float get(int index)
	{
		return mT[index];
	}
	
	public void set(int index,float value)
	{
		mT[index] = value;
	}
	
	public void mul(float f)
	{
		mT[0] = mT[0] * f;
		mT[1] = mT[1] * f;
		mT[2] = mT[2] * f;
	}
	
	public void add (float v[])
	{
		mT[0] = mT[0] + v[0];
		mT[1] = mT[1] + v[1];
		mT[2] = mT[2] + v[2];			
	}
	
	public void add(Sd3dVector v)
	{
		mT[0] = mT[0] + v.get(0);
		mT[1] = mT[1] + v.get(1);
		mT[2] = mT[2] + v.get(2);		
	}
	
	public static void sub(Sd3dVector res,Sd3dVector v1,Sd3dVector v2)
	{
		res.set(0,v1.get(0)-v2.get(0));
		res.set(1,v1.get(1)-v2.get(1));
		res.set(2,v1.get(2)-v2.get(2));		
	}		
	
	public static void add(Sd3dVector res,Sd3dVector v1,Sd3dVector v2)
	{
		res.set(0,v1.get(0)+v2.get(0));
		res.set(1,v1.get(1)+v2.get(1));
		res.set(2,v1.get(2)+v2.get(2));		
	}	
	
	public static float dot(Sd3dVector v1,Sd3dVector v2)
	{
		return v1.get(0)*v2.get(0)+v1.get(1)*v2.get(1)+v1.get(2)*v2.get(2);
	}
	
	public static void cross(Sd3dVector res,Sd3dVector v1,Sd3dVector v2)
	{
		res.set(0,v1.get(1) * v2.get(2) - v1.get(2) * v2.get(1));
		res.set(1,v1.get(2) * v2.get(0) - v1.get(0) * v2.get(2));
		res.set(2,v1.get(0) * v2.get(1) - v1.get(1) * v2.get(0)); 		
	}
	public static float distance(float x1,float y1,float z1,float x2,float y2,float z2)
	{
		return (float)java.lang.Math.sqrt( (x2-x1)*(x2-x1)+(y2-y1)*(y2-y1)+(z2-z1)*(z2-z1));
	}		
	
	public static float distance(Sd3dVector v1,Sd3dVector v2)
	{
		return distance(v1.get(0),v1.get(1),v1.get(2),v2.get(0),v2.get(1),v2.get(2));
	}		
	
	public float length()
	{
		return distance(0f,0f,0f,this.get(0),this.get(1),this.get(2));		
	}
	
	public void normalize()
	{
		float d = distance(mT[0],mT[1],mT[2],0.f,0.f,0.f);
		if (d != 0.f)
		{
			mT[0] = mT[0]/d;
			mT[1] = mT[1]/d;
			mT[2] = mT[2]/d;			
		}
	}
}
