package org.nicolasmy.sd3d.gfx;

import java.nio.FloatBuffer;

public class Sd3dLight {
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
			
			normal.set(mesh.mNormals, a);
			
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
}
