package org.nicolasmy.sd3d.gfx;


public class Sd3dVector2d {
	float mT[];
	public Sd3dVector2d(){
		mT = new float[2];
	}
	
	public Sd3dVector2d(float x, float y){
		this();
		set(x,y);
	}	
	
	public void set(float x, float y)
	{
		mT[0] = x;
		mT[1] = y;
	}
	
	public float getX()
	{
		return mT[0];
	}
	
	public float getY()
	{
		return mT[1];
	}
	
	public Sd3dVector2d clone()
	{
		return new Sd3dVector2d(this.getX(),this.getY());
	}
	
	public void mul(float f)
	{
		mT[0] = mT[0] * f;
	    mT[1] = mT[1] * f;
	}
	
	public void sub(Sd3dVector2d v)
	{
		this.set(this.getX() - v.getX(), this.getY() - v.getY());
	}
	
	public void add(Sd3dVector2d v)
	{
		this.set(this.getX() + v.getX(), this.getY() + v.getY());
	}
	
	public float length()
	{
		return (float)java.lang.Math.sqrt(this.getX()*this.getX()+(this.getY()*this.getY()));
	}
	
	public static float distance(Sd3dVector2d v1,Sd3dVector2d v2)
	{
		return (float)java.lang.Math.sqrt( (v2.getX()-v1.getX())*(v2.getX()-v1.getX())+(v2.getY()-v1.getY())*(v2.getY()-v1.getY()));
	}		
	
	public static float dotProduct(Sd3dVector2d v1,Sd3dVector2d v2)
	{
		return v1.getX()*v2.getX()+v1.getY()*v2.getY();
	}
}
