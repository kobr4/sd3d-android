package org.nicolasmy.sd3d.math;

/**
 * 3x3 Matrix class, also contains some pre-computed cos and sin lookup table
 * and other math stuff that should be put elswhere.
 * @author kobr4
 */
public class Sd3dMatrix {
	public static float cosd[];
	public static float sind[];
	private float mT[];
	
	public Sd3dMatrix()
	{
		mT = new float[9];
	}
	/*
	public Sd3dMatrix(float mat[])
	{
		mT = new float[9];
		
		for (int i = 0;i < 9;i++)
			mT[i] = mat[i];		
	}
	*/
	
	public Sd3dMatrix clone()
	{
		return new Sd3dMatrix(this.mT);
	}
	
	public Sd3dMatrix(float mat[])
	{
		this(mat,0);
	}	
	
	public Sd3dMatrix(float mat[], int offset)
	{
		mT = new float[9];
		
		for (int i = 0;i < 9;i++)
			mT[i] = mat[i + offset];		
	} 
	
	public static int init()
	{
		cosd = new float[361];
		sind = new float[361];
		for (int i = 0;i < 361;i++)
		{
			cosd[i] = (float)java.lang.Math.cos(java.lang.Math.toRadians((double)i));
			sind[i] = (float)java.lang.Math.sin(java.lang.Math.toRadians((double)i));
			
		}
		return 0;
	}	
	
	public static float getCos(float a)
	{
		if (cosd == null)
			init();
		if (a >= 0.)
		{
			return cosd[((int)a)%360];
		}
		else return cosd[-((int)a%360)];
	}
	
	public static float getSin(float a)
	{
		if (sind == null)
			init();		
		if (a >= 0.)
		{
			return sind[((int)a)%360];
		}
		else return -sind[-((int)a)%360];
	}	
	
	public static Sd3dMatrix get_identitymatrix() {
		float f[] = {1.f, 0.f, 0.f, 0.f, 1.f, 0.f, 0.f, 0.f, 1.f};
		return new Sd3dMatrix(f);
	}
	
	public float get(int i,int j) {
		return mT[i+j*3];
	}
	
	public void set(int i,int j,float value) {
		mT[i+j*3] = value;
	}	

	public float  get(int i) {
		return mT[i];
	}
	
	public void  set(int i,float value) {
		mT[i] = value;
	}		
	
	public static void add(Sd3dMatrix result, Sd3dMatrix m1, Sd3dMatrix m2) {
		for (int i = 0;i < 9;i++)
			result.set(i,m1.get(i) + m2.get(i));
	}

	public static void sub(Sd3dMatrix result, Sd3dMatrix m1, Sd3dMatrix m2) {
		for (int i = 0;i < 9;i++)
			result.set(i,m1.get(i) - m2.get(i));
	}

	public static void mul(Sd3dMatrix result, Sd3dMatrix m1, Sd3dMatrix m2) {
		for (int i = 0;i < 3;i++)
			for (int j = 0;j < 3;j++) {
				result.set(i, j,m1.get(i, 0) * m2.get(0, j) + m1.get(i, 1) * m2.get(1, j) + m1.get(i, 2) * m2.get(2, j));
			}
	}	
	
	public static void mul(float result[],Sd3dMatrix m,float v[])
	{
		float a,b,c;
		a = m.get(0, 0) * v[0] + m.get(1, 0) * v[1] + m.get(2, 0) * v[2];
		b = m.get(0, 1) * v[0] + m.get(1, 1) * v[1] + m.get(2, 1) * v[2];
		c = m.get(0, 2) * v[0] + m.get(1, 2) * v[1] + m.get(2, 2) * v[2];
		result[0] = a;
		result[1] = b;
		result[2] = c;
	}
	
	public static Sd3dMatrix getRotationMatrixExact(float ax, float ay, float az) {
		float cosax = (float)java.lang.Math.cos(java.lang.Math.toRadians(ax));
		float sinax = (float)java.lang.Math.sin(java.lang.Math.toRadians(ax));
		float cosay = (float)java.lang.Math.cos(java.lang.Math.toRadians(ay));
		float sinay = (float)java.lang.Math.sin(java.lang.Math.toRadians(ay));
		float cosaz = (float)java.lang.Math.cos(java.lang.Math.toRadians(az));
		float sinaz = (float)java.lang.Math.sin(java.lang.Math.toRadians(az));
		float tx[] = {1, 0, 0, 0, cosax, -sinax, 0, sinax, cosax};
		float ty[] = {cosay, 0, sinay, 0, 1.f, 0.f, -sinay, 0, cosay};
		float tz[] = {cosaz, -sinaz, 0, sinaz, cosaz, 0, 0, 0, 1};
		Sd3dMatrix Rx = new Sd3dMatrix(tx);
		Sd3dMatrix Ry = new Sd3dMatrix(ty);
		Sd3dMatrix Rz = new Sd3dMatrix(tz);
		Sd3dMatrix result = new Sd3dMatrix();
		Sd3dMatrix tmpresult = new Sd3dMatrix();
		Sd3dMatrix.mul(tmpresult, Rx, Ry);
		Sd3dMatrix.mul(result, tmpresult, Rz);
		return result;
	}
	
	
	public static Sd3dMatrix getRotationMatrix(float ax, float ay, float az) {
		float cosax = getCos(ax);
		float sinax = getSin(ax);
		float cosay = getCos(ay);
		float sinay = getSin(ay);
		float cosaz = getCos(az);
		float sinaz = getSin(az);
		float tx[] = {1, 0, 0, 0, cosax, -sinax, 0, sinax, cosax};
		float ty[] = {cosay, 0, sinay, 0, 1.f, 0.f, -sinay, 0, cosay};
		float tz[] = {cosaz, -sinaz, 0, sinaz, cosaz, 0, 0, 0, 1};
		Sd3dMatrix Rx = new Sd3dMatrix(tx);
		Sd3dMatrix Ry = new Sd3dMatrix(ty);
		Sd3dMatrix Rz = new Sd3dMatrix(tz);
		Sd3dMatrix result = new Sd3dMatrix();
		Sd3dMatrix tmpresult = new Sd3dMatrix();
		Sd3dMatrix.mul(tmpresult, Rx, Ry);
		Sd3dMatrix.mul(result, tmpresult, Rz);
		return result;
	}	
	
	public static float distance2d(float x1,float y1,float x2,float y2)
	{
		return distance(x1,y1,0.f,x2,y2,0.f);
	}	
	
	public static float distance(float x1,float y1,float z1,float x2,float y2,float z2)
	{
		return (float)java.lang.Math.sqrt( (x2-x1)*(x2-x1)+(y2-y1)*(y2-y1)+(z2-z1)*(z2-z1));
	}	
	
	public static boolean isOnTriange(float x1,float y1,float x2,float y2,float x3,float y3,float x,float y)
	{
		float a;
		float b;
		boolean s;
		boolean s2;
		//----------------------------
		if (x2-x1 != 0.f)
		{		
			a=(y2-y1)/(x2-x1);
			b = y1 - a*x1;
			
			if (a*x3+b > y3)
				s = true;
			else s = false;
		

			if (a*x+b > y)
				s2= true;
			else s2 = false;
		
			if ((s!=s2)&&(a*x+b != y))
				return false;
			
		}
		else
		{
			if (x1 > x3)
				s = true;
			else s = false;			
		
			if (x1 > x)
				s2 = true;
			else s2 = false;		
		
			if ((s!=s2)&&(x1 != x))
				return false;				
		}
			
		//-----------------------------
		if (x3-x2 != 0.f)
		{			
			a=(y3-y2)/(x3-x2);
			b = y2 - a*x2;
		
			if (a*x1+b > y1)
				s = true;
			else s = false;
		
			
			if (a*x+b > y)
				s2= true;
			else s2 = false;
		
			if ((s!=s2)&&(a*x+b != y))
				return false;		
			
		}
		else
		{
			if (x2 > x1)
				s = true;
			else s = false;			
		
			if (x2 > x)
				s2 = true;
			else s2 = false;		
		
			if ((s!=s2)&&(x1 != x))
				return false;				
		}
		
		//-----------------------------		
		if (x1-x3 != 0.f)
		{
			a=(y1-y3)/(x1-x3);
			b = y3 - a*x3;
		
			if (a*x2+b > y2)
				s = true;
			else s = false;
	
			
			
			if (a*x+b > y)
				s2= true;
			else s2 = false;
		
			if ((s!=s2)&&(a*x+b != y))
				return false;	
			
		}
		else
		{
			if (x1 > x2)
				s = true;
			else s = false;			
			
			if (x1 > x)
				s2 = true;
			else s2 = false;		
			
			if ((s!=s2)&&(x1 != x))
				return false;				
		}
		return true;
		
	}	
	
	public static float[] convert33to44(float m33[], int offset)
	{
		float m44[] = new float[16];
		
		m44[0] = m33[0 + offset];
		m44[1] = m33[1 + offset];
		m44[2] = m33[2 + offset];
		
		m44[4] = m33[3 + offset];
		m44[5] = m33[4 + offset];
		m44[6] = m33[5 + offset];
		
		m44[8] = m33[6 + offset];
		m44[9] = m33[7 + offset];
		m44[10] = m33[8 + offset];		
		
		m44[15] = 1.0f;
		return m44;
	}
	
	public static void setRotateEulerM(float[] rm, int rmOffset, float x,
			float y, float z) {
		x = x * 0.01745329f;
		y = y * 0.01745329f;
		z = z * 0.01745329f;
		float sx = (float) Math.sin(x);
		float sy = (float) Math.sin(y);
		float sz = (float) Math.sin(z);
		float cx = (float) Math.cos(x);
		float cy = (float) Math.cos(y);
		float cz = (float) Math.cos(z);
		float cxsy = cx * sy;
		float sxsy = sx * sy;

		rm[rmOffset + 0] = cy * cz;
		rm[rmOffset + 1] = -cy * sz;
		rm[rmOffset + 2] = sy;
		rm[rmOffset + 3] = 0.0f;

		rm[rmOffset + 4] = sxsy * cz + cx * sz;
		rm[rmOffset + 5] = -sxsy * sz + cx * cz;
		rm[rmOffset + 6] = -sx * cy;
		rm[rmOffset + 7] = 0.0f;

		rm[rmOffset + 8] = -cxsy * cz + sx * sz;
		rm[rmOffset + 9] = cxsy * sz + sx * cz;
		rm[rmOffset + 10] = cx * cy;
		rm[rmOffset + 11] = 0.0f;

		rm[rmOffset + 12] = 0.0f;
		rm[rmOffset + 13] = 0.0f;
		rm[rmOffset + 14] = 0.0f;
		rm[rmOffset + 15] = 1.0f;
	}		
}
