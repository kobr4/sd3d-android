package org.nicolasmy.sd3d.gfx;

import java.nio.FloatBuffer;

import org.nicolasmy.sd3d.gfx.entity.Sd3dGameEntity;
import org.nicolasmy.sd3d.math.Sd3dVector;

public class Sd3dLight extends Sd3dGameEntity{
	public enum LightType
	{
		DIRECTION,
		POINT,
		AMBIENT
	}
	
	private LightType mLighType;
	private float mRGBA[] = new float[4];
	
	public static Sd3dLight createLight(LightType t, float v[], float color[])
	{
		Sd3dLight light = new Sd3dLight();
		light.setLighType(t);
		light.mPosition = new float[3];
		light.setPosition(v[0], v[1], v[2]);
		
		if (color != null)
		{
			light.setRGB(color[0], color[1], color[2], color[3]);
		}
		
		return light;
	}
	
	public static void computeLighting(Sd3dVector vlight,Sd3dMesh mesh,Sd3dMaterial material)
	{
		int trianglecount = mesh.mIndices.capacity()/3;
		Sd3dVector normal = new Sd3dVector();
		
		if (material.mColors == null)
			material.mColors = FloatBuffer.allocate(mesh.mVertices.capacity()/3 * 4);
		
		for (int i = 0;i < trianglecount;i++)
		{
			int a,b,c;
			float res;
			float ambient = 0.5f;
			a = mesh.mIndices.get(i*3);
			b = mesh.mIndices.get(i*3+1);
			c = mesh.mIndices.get(i*3+2);
			
			normal.setFromNormal(mesh.mVertices, a);
			
			res = Sd3dVector.dot(vlight, normal);
			
			if (res < 0.f)
				res = 0.f;
			res = ambient + res;
			if (res > 1.f)
				res = 1.f;
			
			material.mColors.put(a*4,res);
			material.mColors.put(a*4+1,res);
			material.mColors.put(a*4+2,res);
			material.mColors.put(a*4+3,1.f);
			
			material.mColors.put(b*4,res);
			material.mColors.put(b*4+1,res);
			material.mColors.put(b*4+2,res);
			material.mColors.put(b*4+3,1.f);
			
			material.mColors.put(c*4,res);
			material.mColors.put(c*4+1,res);
			material.mColors.put(c*4+2,res);
			material.mColors.put(c*4+3,1.f);
			
		}
	}

	public LightType getLighType() {
		return mLighType;
	}

	public void setLighType(LightType mLighType) {
		this.mLighType = mLighType;
	}

	public float[] getRGBA() {
		return mRGBA;
	}

	public void setRGB(float mRGBA[]) {
		this.mRGBA = mRGBA;
	}
	
	public void setRGB(float r,float g,float b,float a) {
		this.mRGBA[0] = r;
		this.mRGBA[1] = g;
		this.mRGBA[2] = b;
		this.mRGBA[3] = a;
	}	
}
