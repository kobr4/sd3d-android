package org.nicolasmy.sd3d.gfx;


public class Sd3dObject
{
	public Sd3dRendererElement mRenderElement[];
	public Sd3dMesh mMesh[];
	public Sd3dMaterial mMaterial[];
	public int mMeshCount;
	public float mScale;
	public float mRotation[];
	public float mPosition[];
	public Sd3dMaterial pickedMaterial;
	public Sd3dMaterial prepickedMaterial;
	public Sd3dMaterial unpickedMaterial;	
	public boolean mIsPickable;
	
	
	public static void merge(Sd3dObject objtomerge,float tx,float ty,float bx,float by)
	{
		Sd3dObject newObj = new Sd3dObject();
		newObj.mMesh = new Sd3dMesh[1];
		newObj.mMesh[0] = new Sd3dMesh();
		
		objtomerge.mMesh[0].mIndices.position(0);
		
		char a,b,c;
		float x,y,z;
		a = objtomerge.mMesh[0].mIndices.get();
		b = objtomerge.mMesh[0].mIndices.get();
		c = objtomerge.mMesh[0].mIndices.get();
		
		x = objtomerge.mMesh[0].mVertices.get(a*3);
		y = objtomerge.mMesh[0].mVertices.get(a*3+1);
		z = objtomerge.mMesh[0].mVertices.get(a*3+2);
	}
	
	
	//Ugly quick fix for removing buildings from the track
	public static void trackCleaner(Sd3dMesh trackmesh,Sd3dMaterial trackmaterial,Sd3dMesh mesh)
	{
		int triangleCount = trackmesh.mIndices.capacity()/3;
		int meshTriangleCount = mesh.mIndices.capacity()/3;
		char a,b,c;
		float red;
		float x1,y1,x2,y2,x3,y3;
		float tmpx;
		float tmpy;
		trackmesh.mIndices.position(0);
		mesh.mIndices.position(0);
		for (int i = 0;i < triangleCount;i++)
		{
			a = trackmesh.mIndices.get(i*3);
			b = trackmesh.mIndices.get(i*3+1);
			c = trackmesh.mIndices.get(i*3+2);
			red = trackmaterial.mColors.get(a*4);
			
		
			if (red == 1.f)
			{
				
				x1 = trackmesh.mVertices.get(a*3);
				y1 = trackmesh.mVertices.get(a*3+2);		
				
				x2 = trackmesh.mVertices.get(b*3);
				y2 = trackmesh.mVertices.get(b*3+2);
				
				x3 = trackmesh.mVertices.get(c*3);
				y3 = trackmesh.mVertices.get(c*3+2);
				
				for (int j = 0;j < meshTriangleCount;j++)
				{
					a = mesh.mIndices.get(j*3);
					b = mesh.mIndices.get(j*3+1);
					c = mesh.mIndices.get(j*3+2);	
					boolean isOnTrack = false;
					
					tmpx = mesh.mVertices.get(a*3);
					tmpy = mesh.mVertices.get(a*3+2);					

					isOnTrack = isOnTrack | Sd3dMatrix.isOnTriange(x1, y1, x2, y2, x3, y3, tmpx, tmpy);					
					
					tmpx = mesh.mVertices.get(b*3);
					tmpy = mesh.mVertices.get(b*3+2);					

					isOnTrack = isOnTrack | Sd3dMatrix.isOnTriange(x1, y1, x2, y2, x3, y3, tmpx, tmpy);
				
					tmpx = mesh.mVertices.get(c*3);
					tmpy = mesh.mVertices.get(c*3+2);					

					isOnTrack = isOnTrack | Sd3dMatrix.isOnTriange(x1, y1, x2, y2, x3, y3, tmpx, tmpy);
					
					if (isOnTrack)
					{						
						mesh.mVertices.put(a*3,0.f);		
						mesh.mVertices.put(a*3+2,0.f);	
						
						mesh.mVertices.put(b*3,0.f);		
						mesh.mVertices.put(b*3+2,0.f);	
						
						mesh.mVertices.put(c*3,0.f);		
						mesh.mVertices.put(c*3+2,0.f);		
									
					}
					
				}
			}
			trackmesh.mIndices.position(0);
			mesh.mIndices.position(0);			
		}
		
	}
}
