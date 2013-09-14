package org.nicolasmy.sd3d.gfx;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;

import org.nicolasmy.sd3d.MeshSerializationBean;
import org.nicolasmy.sd3d.interfaces.Sd3dRendererElementInterface;
import org.nicolasmy.sd3d.math.Sd3dMatrix;
import org.nicolasmy.sd3d.math.Sd3dVector;
import org.nicolasmy.sd3d.math.Sd3dVector2d;

import android.util.Log;

public class Sd3dMesh
{
	public static HashMap<String, Object> meshCache = new HashMap<String,Object>();
	public final static int nbFloatPerVertex = 3 + 3 + 2;
	public FloatBuffer mVertices;
	public FloatBuffer mTangentsBinormals;
	//public FloatBuffer mNormals;
	public CharBuffer mIndices;
	//public FloatBuffer mTexCoords;
	
	public boolean mHasNormals;
	public boolean mHasTexCoords;
	public String mMeshName;
	public float mMeshPosition[]; 
	public boolean mIsBillboard;
	public boolean mIsShadowVolume;
	public boolean mIsInScreenSpace;
	public Sd3dRendererElementInterface mRendererElementInterface;
	
	
	public void init(Sd3dMesh mesh)
	{
		mVertices = FloatBuffer.allocate(mesh.mVertices.capacity());
		mIndices = CharBuffer.allocate(mesh.mIndices.capacity());
	}
	
	public void init(int trianglecount)
	{
		mVertices = FloatBuffer.allocate(trianglecount * Sd3dMesh.nbFloatPerVertex * 3);
		mIndices = CharBuffer.allocate(trianglecount * 3);
	}
		
	public void init(float vertices[],float normals[], float texcoords[],char indices[])
	{
		int nbVertex = vertices.length / 3;
	    mVertices = FloatBuffer.allocate(nbVertex * Sd3dMesh.nbFloatPerVertex);
		//mVertices.put(vertices);
		
		for (int i = 0;i < nbVertex;i++)
		{
			float px = vertices[i*3]; 
			float py = vertices[i*3 + 1]; 
			float pz = vertices[i*3 + 2]; 
			float nx = 0f;
			float ny = 0f;
			float nz = 0f;
			if (normals != null)
			{
				nx = normals[i*3]; 
				ny = normals[i*3 + 1]; 
				nz = normals[i*3 + 2]; 
			}
			float tx = 0f;
			float ty = 0f;			
			if (texcoords != null)
			{
				tx = texcoords[i*2]; 
				ty = texcoords[i*2 + 1]; 
			}	
			
			this.addPoint(px, py, pz, nx, ny, nz, tx, ty);
		}
		
		mVertices.position(0);		
		
		mIndices = CharBuffer.allocate(indices.length);
		mIndices.put(indices);
		mIndices.position(0);	
		
	
	}
	
	public void writeToFile(String filename)
	{
		MeshSerializationBean  bean = new MeshSerializationBean();
		//bean.setVertices(this.mVertices.array());
		//bean.setIndices(this.mIndices.array());
		
		this.mVertices.position(0);
		this.mIndices.position(0);
		float vertices[] = new float[this.mVertices.capacity()];
		this.mVertices.get(vertices);
		bean.setVertices(vertices);
		char indices[] = new char[this.mIndices.capacity()];
		this.mIndices.get(indices);
		bean.setIndices(indices);		
		this.mVertices.position(0);
		this.mIndices.position(0);	
		/*
		if (this.mNormals != null)
		{
			this.mNormals.position(0);
			float normals[] = new float[this.mNormals.capacity()];
			this.mVertices.get(normals);
			bean.setNormals(normals);
			this.mNormals.position(0);
		}
		*/
		bean.setMeshPostion(this.mMeshPosition);
		
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(filename);
			try {
				ObjectOutputStream oos = new ObjectOutputStream(fos);				
				oos.writeObject(bean);
				oos.close();
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			Log.d("writeToFile()","Cannot access "+filename);
		}

	}
	
	public static boolean fileExists(String filename)
	{
		java.io.File file = new java.io.File(filename);
		return file.exists();
	}
	
	public void loadFromFile(String filename)
	{
		//FileInputStream fis;
		MeshSerializationBean  bean;

		try {
			//FileInputStream fis = new FileInputStream(filename);
			//ObjectInputStream ois = new ObjectInputStream(fis);
		  ObjectInputStream ois = new ObjectInputStream(Sd3dRessourceManager.getManager().getRessource(filename));
		  try {
		    bean = (MeshSerializationBean)ois.readObject();
			ois.close();
					
			//fis.close();
					//this.mVertices = FloatBuffer.allocate(bean.getVertices().length);
			
	        ByteBuffer vbb = ByteBuffer.allocateDirect(bean.getVertices().length * 4);
	        vbb.order(ByteOrder.nativeOrder());
	        FloatBuffer	mFVertexBuffer = vbb.asFloatBuffer();			
	        mFVertexBuffer.put(bean.getVertices());
			/*
	        if (bean.getNormals() != null)
	        {
		        ByteBuffer nbb = ByteBuffer.allocateDirect(bean.getNormals().length * 4);
		        nbb.order(ByteOrder.nativeOrder());	
		        FloatBuffer normalBuffer = nbb.asFloatBuffer();
		        normalBuffer.put(bean.getNormals());
		        this.mNormals = normalBuffer;
		        this.mNormals.position(0);
	        }
	        */
	        this.mMeshPosition = bean.getMeshPostion();
	        
	        //this.mVertices = FloatBuffer.wrap(bean.getVertices());
	        this.mVertices = mFVertexBuffer;
			this.mVertices.position(0);
					//this.mIndices = CharBuffer.allocate(bean.getIndices().length);
					//this.mIndices.put(bean.getIndices());	
			this.mIndices = CharBuffer.wrap(bean.getIndices());
			this.mIndices.position(0);	
		  } catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
		} catch (StreamCorruptedException e) {
		  // TODO Auto-generated catch block
		  e.printStackTrace();
		} catch (IOException e) {
		  // TODO Auto-generated catch block
		  e.printStackTrace();
		}		
		
	}
	
	public Sd3dMesh buildShadowVolume(Sd3dVector lightVector)
	{
		//Sd3dMesh result[] = new Sd3dMesh[2];
		
		int nbface = this.mIndices.capacity() / 3;
		//int lightedtrianglecount = 0;
		int trianglecount = 0;
		Sd3dMesh shadowMesh = new Sd3dMesh();
		/*
		backMesh.init(this.mIndices.capacity()/3);
		backMesh.mIsShadowVolume = true;
		Sd3dMesh frontMesh = new Sd3dMesh(); 
		frontMesh.init(this.mIndices.capacity()/3);
		frontMesh.mIsShadowVolume = true;
		*/
		//result[0] = backMesh;
		//result[1] = frontMesh;
		
		//Log.d("buildShadowVolume()","Nb de face:"+(this.mIndices.capacity()/3));
		boolean faceToLight[] = new boolean[nbface];
		
		Sd3dVector v0 = new Sd3dVector();
		Sd3dVector v1 = new Sd3dVector();
		Sd3dVector v2 = new Sd3dVector();
		Sd3dVector vm1 = new Sd3dVector();
		Sd3dVector vm2 = new Sd3dVector();
		Sd3dVector res = new Sd3dVector();
		Sd3dVector vf = new Sd3dVector();
		for (int i = 0; i < this.mIndices.capacity(); i=i+3)
		{
			int a = (int) (mIndices.get(i));
			int b = (int) (mIndices.get(i+1));
			int c = (int) (mIndices.get(i+2));
			
			v0.setFromVertice(mVertices, a);
			v1.setFromVertice(mVertices, b);
			v2.setFromVertice(mVertices, c);
			
			Sd3dVector.sub(vm1, v1, v0);
			Sd3dVector.sub(vm2, v2, v0);
			Sd3dVector.cross(res,vm1,vm2);
			res.normalize();
			
			Sd3dVector.add(vf, v0, v1);
			Sd3dVector.add(vf, vf, v2);
			
			float scalar = Sd3dVector.dot(res, lightVector);
			
			if (scalar > 0.0)
			{
				faceToLight[i/3] = true;
			}
			else
			{
				/*
				backMesh.putTriangle(mVertices.get(a*3), mVertices.get(a*3+1), mVertices.get(a*3+2), 
						mVertices.get(b*3), mVertices.get(b*3+1), mVertices.get(b*3+2), 
						mVertices.get(c*3), mVertices.get(c*3+1), mVertices.get(c*3+2));
				*/
				trianglecount++;
			}
		}
		//Log.d("buildShadowVolume()","TOTO2");
		for (int i = 0; i < this.mIndices.capacity(); i=i+3)
		{
			if (faceToLight[i/3])
			{
				for (int k = 0;k < 3;k++)
				{
					int a = mIndices.get(i+k);
					int b = mIndices.get(i+(k+1)%3);
					
					for (int l = 0; l < this.mIndices.capacity(); l=l+3)
					{
						if (!faceToLight[l/3])
						{
						
							for(int z = 0; z < 3;z++)
							{
								int a2 = mIndices.get(l+z);
								int b2 = mIndices.get(l+(z+1)%3);	
								
								if (((a==a2)&&(b==b2))||((a==b2)&&(b==a2)))
								{
									/*
									Sd3dVector lightVectP = lightVector.clone();
									lightVectP.mul(10000.f);
																		
									
									Sd3dVector vt1 = new Sd3dVector();
									vt1.set(mVertices, a2);
									Sd3dVector vt1p = new Sd3dVector();
									Sd3dVector.sub(vt1p,vt1,lightVectP);

									Sd3dVector vt2 = new Sd3dVector();
									vt2.set(mVertices, b2);
									Sd3dVector vt2p = new Sd3dVector();
									Sd3dVector.sub(vt2p,vt2,lightVectP);
									*/									
									/*
									frontMesh.putTriangle(mVertices.get(a*3), mVertices.get(a*3+1), mVertices.get(a*3+2), 
											mVertices.get(b*3), mVertices.get(b*3+1), mVertices.get(b*3+2), 
											vt2p.get(0), vt2p.get(1), vt2p.get(2));									
		
									frontMesh.putTriangle( vt2p.get(0), vt2p.get(1), vt2p.get(2),
											vt1p.get(0), vt1p.get(1), vt1p.get(2),
											mVertices.get(a*3), mVertices.get(a*3+1), mVertices.get(a*3+2));										
									*/
									trianglecount++;
									trianglecount++;
								}
								
							}
							
						}
					}
				}
				
			}
			
		}	
		
		shadowMesh.init(trianglecount);
		
		for (int i = 0; i < this.mIndices.capacity(); i=i+3)
		{
			int a = (int) (mIndices.get(i));
			int b = (int) (mIndices.get(i+1));
			int c = (int) (mIndices.get(i+2));
			
			v0.setFromVertice(mVertices, a);
			v1.setFromVertice(mVertices, b);
			v2.setFromVertice(mVertices, c);
			
			Sd3dVector.sub(vm1, v1, v0);
			Sd3dVector.sub(vm2, v2, v0);
			Sd3dVector.cross(res,vm1,vm2);
			res.normalize();
			
			Sd3dVector.add(vf, v0, v1);
			Sd3dVector.add(vf, vf, v2);
			
			float scalar = Sd3dVector.dot(res, lightVector);
			
			if (scalar > 0.0)
			{
				faceToLight[i/3] = true;
			}
			else
			{
				
				shadowMesh.putTriangle(mVertices.get(a*nbFloatPerVertex), mVertices.get(a*nbFloatPerVertex+1), mVertices.get(a*nbFloatPerVertex+2), 
						mVertices.get(b*nbFloatPerVertex), mVertices.get(b*nbFloatPerVertex+1), mVertices.get(b*nbFloatPerVertex+2), 
						mVertices.get(c*nbFloatPerVertex), mVertices.get(c*nbFloatPerVertex+1), mVertices.get(c*nbFloatPerVertex+2));
				
				//trianglecount++;
			}
		}
		//Log.d("buildShadowVolume()","TOTO2");
		for (int i = 0; i < this.mIndices.capacity(); i=i+3)
		{
			if (faceToLight[i/3])
			{
				for (int k = 0;k < 3;k++)
				{
					int a = mIndices.get(i+k);
					int b = mIndices.get(i+(k+1)%3);
					
					for (int l = 0; l < this.mIndices.capacity(); l=l+3)
					{
						if (!faceToLight[l/3])
						{
						
							for(int z = 0; z < 3;z++)
							{
								int a2 = mIndices.get(l+z);
								int b2 = mIndices.get(l+(z+1)%3);	
								
								if (((a==a2)&&(b==b2))||((a==b2)&&(b==a2)))
								{
									Sd3dVector lightVectP = lightVector.clone();
									lightVectP.mul(10000.f);
																		
									
									Sd3dVector vt1 = new Sd3dVector();
									vt1.setFromVertice(mVertices, a2);
									Sd3dVector vt1p = new Sd3dVector();
									Sd3dVector.sub(vt1p,vt1,lightVectP);

									Sd3dVector vt2 = new Sd3dVector();
									vt2.setFromVertice(mVertices, b2);
									Sd3dVector vt2p = new Sd3dVector();
									Sd3dVector.sub(vt2p,vt2,lightVectP);									
									
									shadowMesh.putTriangle(mVertices.get(a*nbFloatPerVertex), mVertices.get(a*nbFloatPerVertex+1), mVertices.get(a*nbFloatPerVertex+2), 
											mVertices.get(b*nbFloatPerVertex), mVertices.get(b*nbFloatPerVertex+1), mVertices.get(b*nbFloatPerVertex+2), 
											vt2p.get(0), vt2p.get(1), vt2p.get(2));									
		

									
									shadowMesh.putTriangle( vt2p.get(0), vt2p.get(1), vt2p.get(2),
											vt1p.get(0), vt1p.get(1), vt1p.get(2),
											mVertices.get(a*nbFloatPerVertex), mVertices.get(a*nbFloatPerVertex+1), mVertices.get(a*nbFloatPerVertex+2));										
									
								
								}
								
							}
							
						}
					}
				}
				
			}
			
		}			

		shadowMesh.mVertices.position(0);
		shadowMesh.mIndices.position(0);
		return shadowMesh;
	}
	
	
	public void setTexturedQuad(float x1,float y1,float z1,
			float x2,float y2,float z2,
			float x3,float y3,float z3,
			float x4,float y4,float z4,
			float x1t,float y1t,
			float x2t,float y2t,
			float x3t,float y3t,
			float x4t,float y4t)
	{
		int trianglecount = 2;
		int verticecount = 4;
		init(trianglecount,verticecount);
		putTexturedQuad( x1, y1, z1,
			 x2, y2, z2,
			 x3, y3, z3,
			 x4, y4, z4,
			 x1t, y1t,
			 x2t, y2t,
			 x3t, y3t,
			 x4t, y4t);
		
	}
	
	private void addPoint(float px,float py,float pz,float nx,float ny,float nz,float tx,float ty)
	{
		mVertices.put(px);
		mVertices.put(py);
		mVertices.put(pz);
		mVertices.put(nx);
		mVertices.put(ny);
		mVertices.put(nz);
		mVertices.put(tx);
		mVertices.put(ty);
		
		
	}
	
	
	public void putTexturedQuad(float x1,float y1,float z1,
			float x2,float y2,float z2,
			float x3,float y3,float z3,
			float x4,float y4,float z4,
			float x1t,float y1t,
			float x2t,float y2t,
			float x3t,float y3t,
			float x4t,float y4t)
	{
		int indice = mVertices.position()/Sd3dMesh.nbFloatPerVertex;
		
		addPoint(x1,y1,z1,0f,0f,0f,x1t,y1t);
		addPoint(x2,y2,z2,0f,0f,0f,x2t,y2t);
		addPoint(x3,y3,z3,0f,0f,0f,x3t,y3t);
		addPoint(x4,y4,z4,0f,0f,0f,x4t,y4t);
		
		mIndices.put((char)(indice+0));
		mIndices.put((char)(indice+1));
		mIndices.put((char)(indice+2));		
		
		
		mIndices.put((char)(indice+2));
		mIndices.put((char)(indice+3));
		mIndices.put((char)indice);		
		
	}
	
	public void addTexCoords(int indice,float xt,float yt)
	{
		mVertices.put(indice * Sd3dMesh.nbFloatPerVertex + 6,xt);
		mVertices.put(indice * Sd3dMesh.nbFloatPerVertex + 7,yt);
	}
	
	public void addNormal(int indice,float nx,float ny,float nz)
	{
		mVertices.put(indice * Sd3dMesh.nbFloatPerVertex + 3,nx);
		mVertices.put(indice * Sd3dMesh.nbFloatPerVertex + 4,ny);
		mVertices.put(indice * Sd3dMesh.nbFloatPerVertex + 5,nz);
	}	

	public void addTangent(int indice,float nx,float ny,float nz)
	{
		mTangentsBinormals.put(indice * 6,nx);
		mTangentsBinormals.put(indice * 6 + 1,ny);
		mTangentsBinormals.put(indice * 6 + 2,nz);
	}

	public void addBinormal(int indice,float nx,float ny,float nz)
	{
		mTangentsBinormals.put(indice * 6 + 3,nx);
		mTangentsBinormals.put(indice * 6 + 4,ny);
		mTangentsBinormals.put(indice * 6 + 5,nz);
	}
	
	public float getTangent(int indice, int element)
	{
		return mTangentsBinormals.get(indice * 6 + element);
	}
	
	public float getBinormal(int indice, int element)
	{
		return mTangentsBinormals.get(indice * 6 + 3 + element);
	}	
	
	public float getNormal(int indice, int element)
	{
		return mVertices.get(indice * Sd3dMesh.nbFloatPerVertex + 3 + element);
	}
	
	
	public void putTriangle(float x1,float y1,float z1,
			float x2,float y2,float z2,
			float x3,float y3,float z3)
	{
		//System.out.println("A= "+x1+" "+y1+" "+z1);
		//System.out.println("B= "+x2+" "+y2+" "+z2);
		//System.out.println("C= "+x3+" "+y3+" "+z3);
		
		int indice = mVertices.position()/Sd3dMesh.nbFloatPerVertex;
		//int indice = mVertices.position();
		mVertices.put(x1);
		mVertices.put(y1);
		mVertices.put(z1);
		
		mVertices.put(0f);
		mVertices.put(0f);
		mVertices.put(0f);
		mVertices.put(0f);
		mVertices.put(0f);
		
		mVertices.put(x2);
		mVertices.put(y2);
		mVertices.put(z2);
		
		mVertices.put(0f);
		mVertices.put(0f);
		mVertices.put(0f);
		mVertices.put(0f);
		mVertices.put(0f);		
		
		mVertices.put(x3);
		mVertices.put(y3);
		mVertices.put(z3);		
		
		mVertices.put(0f);
		mVertices.put(0f);
		mVertices.put(0f);
		mVertices.put(0f);
		mVertices.put(0f);		
		
		mIndices.put((char)indice);
		mIndices.put((char)(indice+1));
		mIndices.put((char)(indice+2));		
	}
	
	/*
	public void putQuad(float x1,float y1,float z1,
			float x2,float y2,float z2,
			float x3,float y3,float z3,
			float x4,float y4,float z4)
	{
		int indice = mVertices.position()/3;
		
		//System.out.println("x1:"+x1+" y1:"+y1+" z1:"+z1+" indice:"+indice);
		//System.out.println("x2:"+x2+" y2:"+y2+" z2:"+z2+" indice:"+(indice+1));
		//System.out.println("x3:"+x3+" y3:"+y3+" z3:"+z3+" indice:"+(indice+2));
		//System.out.println("x4:"+x4+" y4:"+y4+" z4:"+z4+" indice:"+(indice+3));	
		
		mVertices.put(x1);
		mVertices.put(y1);
		mVertices.put(z1);
		
		mVertices.put(x2);
		mVertices.put(y2);
		mVertices.put(z2);
		
		mVertices.put(x3);
		mVertices.put(y3);
		mVertices.put(z3);		
		
		mVertices.put(x4);
		mVertices.put(y4);
		mVertices.put(z4);			
		
	
		mIndices.put((char)indice);
		mIndices.put((char)(indice+1));
		mIndices.put((char)(indice+2));		
		
		
		mIndices.put((char)(indice+2));
		mIndices.put((char)(indice+3));
		mIndices.put((char)indice);
				
	}	
	*/

	public void setMeshPosition(float x,float y,float z)
	{
		if (mMeshPosition == null)
		{
			mMeshPosition = new float[3];
		}
		
		mMeshPosition[0] = x;
		mMeshPosition[1] = y;
		mMeshPosition[2] = z;		
	}
	
	public int unsignedByteToInt(byte b) {
		return (int) b & 0xFF;
	}		
	private int read16(byte buffer[], int offset)
	{
		int res;
		int res2;
		res = unsignedByteToInt(buffer[offset]);
		res2 = unsignedByteToInt(buffer[offset+1]) << 8;
		res = res | res2;
		return res;	
	}
	
	private int read32(byte buffer[], int offset)
	{
		int res = 0;
		res = unsignedByteToInt(buffer[offset]);
		res = res | (unsignedByteToInt(buffer[offset+1]) << 8);
		res = res | (unsignedByteToInt(buffer[offset+2]) << 16);
		res = res | (unsignedByteToInt(buffer[offset+3]) << 24);			
		return res;	
	}		
	
	  static final String HEXES = "0123456789ABCDEF";
	  public String getHex( byte [] raw) {
	    if ( raw == null ) {
	      return null;
	    }
	    final StringBuilder hex = new StringBuilder( 2 * raw.length );
	    for ( final byte b : raw ) {
	      hex.append(HEXES.charAt((b & 0xF0) >> 4))
	         .append(HEXES.charAt((b & 0x0F)));
	    }
	    return hex.toString();
	  }		
	private int currentcount;
	
    private float buffVerts[] = null;
    private char indices[] = null;
    private float texCoords[] = null;
	private int eatChunk(byte buffer[],int offset, int count,float angle_y,float z_adjust,float scale)
	{
		int chunkid;
		int chunklength;
	    int     i = 0;// index into current chunk
	    int     j;	
	    chunkid = read16(buffer,i+offset);
	    byte buf[] = new byte[2];
	    buf[0] = buffer[i+offset];
	    buf[1] = buffer[i+offset+1];		    
	    chunklength = read32(buffer,i+2+offset);
	    i = 6;
	    
	    float tmpV[] = new float[3];
	    float tmpRes[] = new float[3];	    
	    ;
	    Sd3dMatrix m = Sd3dMatrix.getRotationMatrix(-90,angle_y,0);

		float v1[] = new float[4];
		float v2[] = new float[4];
	    
	    int numVerts = 0;

	    
	    switch (chunkid)
	    {
	    case 0x4D4D:    // main file
	        while ((read16(buffer,i+offset)!= 0x3D3D) &&
	            (read16(buffer,i+offset)!= 0xB000))
	            i += 2;
	        break;
	    case 0x3D3D:    // editor data
	        break;
	    //case 0x4000:    // object description
	    case 0x4000:    // object description    
	        while (buffer[i+offset] != 0)
	        	i++;   // get past string description
	        i++;
	        currentcount++;
	        Log.d("eatChunk","currentcount = "+currentcount);
	        break;
	    case 0x4100:    // triangular polygon list
	        break;
	    case 0x4110:    // vertex list
	    	
	        if ((numVerts == 0)&&(currentcount == count))
	        {
	        	numVerts = read16(buffer,i+offset);
	            i+=2;
	            buffVerts = new float[numVerts*3];            
	            for (j=0;j<numVerts;j++)
	            {
	            	v1[0] = Float.intBitsToFloat(read32(buffer,i+offset));
	            	i+=4;
	            	v1[1] = Float.intBitsToFloat(read32(buffer,i+offset));
	            	i+=4;
	            	v1[2] = Float.intBitsToFloat(read32(buffer,i+offset));
	            	i+=4;
	            	v1[3] = 0.f;

	            	tmpV[0] = v1[0];
	            	tmpV[1] = v1[1];
	            	tmpV[2] = v1[2];
	            	
	            	Sd3dMatrix.mul(tmpRes, m, tmpV);
	            	
	            	buffVerts[j*3] = tmpRes[0] * scale;
	            	buffVerts[j*3+1] = tmpRes[1] * scale;
	            	buffVerts[j*3+2] = tmpRes[2] * scale;	            	
	         
	            	
	            	this.mMeshPosition[0] = this.mMeshPosition[0] + (buffVerts[j*3] / (float)numVerts);
	            	this.mMeshPosition[1] = this.mMeshPosition[1] + (buffVerts[j*3+1] / (float)numVerts);
	            	this.mMeshPosition[2] = this.mMeshPosition[2] + (buffVerts[j*3+2] / (float)numVerts);
	            		            	
	            }
	            
	            
	            for (j = 0;j < numVerts;j++)
	            {
	            	buffVerts[j*3] = buffVerts[j*3] -  this.mMeshPosition[0];
	            	buffVerts[j*3+1] = buffVerts[j*3+1] - this.mMeshPosition[1] + z_adjust;
	            	buffVerts[j*3+2] = buffVerts[j*3+2] - this.mMeshPosition[2];
	            }
	            
	            
		        //ByteBuffer vbb = ByteBuffer.allocateDirect(numVerts*3 * 4);
		        //vbb.order(ByteOrder.nativeOrder());
		        
		        //FloatBuffer	mFVertexBuffer = vbb.asFloatBuffer();			
		        //mFVertexBuffer.put(buffVerts);	      
		        //mVertices =  mFVertexBuffer;
	            
		        /*
	            mVertices = FloatBuffer.allocate(numVerts*3);
				mVertices.put(buffVerts);
				mVertices.position(0);		
	            *
	            */
				//System.out.println("NumVertices: "+mVertices.capacity());		

	        }
	        else
	        {
	        	
	            i = chunklength;
	        }
	        break;
	        
	    case 0x4120:
	    	
	        if ((indices == null)&&(currentcount == count))
	        {
	           int numpolys = read16(buffer,i+offset);            
	            i+=2;
	            indices = new char[numpolys*3];
	            for (j=0;j<numpolys;j++)
	            {
	            	indices[j*3+2] = (char)read16(buffer,i+offset);
	                i+=2;
	                indices[j*3+1] = (char)read16(buffer,i+offset);
	                i+=2;
	                indices[j*3] = (char)read16(buffer,i+offset);
	                i+=2;
	                i+=2;   // skip face info
	            }
	            
	            this.init(buffVerts, null, texCoords, indices);
		        //ByteBuffer vbb = ByteBuffer.allocateDirect(numpolys*3*2);
		        //vbb.order(ByteOrder.nativeOrder());
		        
		        
		        
		        //CharBuffer	charBuffer = vbb.asCharBuffer();			
		        //charBuffer.put(indices);	  	            
		        //mIndices = charBuffer;
		        /*
				mIndices = CharBuffer.allocate(numpolys*3);
				//System.out.println("Indices: "+mIndices.capacity()/3);
				mIndices.put(indices);
				*/
				//mIndices.position(0);			            
	        }
	        else
	            i = chunklength;
	        break;
	        
	    case 0x4140:
	    	if (currentcount == count)
	    	{
	    	int numuvmaps = read16(buffer,i+offset); 	
	            i+=2;
	            
	            texCoords = new float[numuvmaps*2];
	            for (j=0;j<numuvmaps;j++)
	            {
	            	texCoords[j*2] = Float.intBitsToFloat(read32(buffer,i+offset));
	            	//float xt = Float.intBitsToFloat(read32(buffer,i+offset));
	            	
	                i+=4;
	                texCoords[j*2+1]  = 1.f - Float.intBitsToFloat(read32(buffer,i+offset));
	                //float yt = 1.f - Float.intBitsToFloat(read32(buffer,i+offset));
	                i+=4;
	                //this.addTexCoords(j, xt, yt);
	            } 
	            /*
				mTexCoords = FloatBuffer.allocate(numuvmaps*2);
				mTexCoords.put(texCoords);
				mTexCoords.position(0);	
				
				System.out.println("TexCoords: "+mTexCoords.capacity());
				*/
	    	}
	    	
	        i = chunklength;
	    	
	        break;    
	    	
	    default:
	        i = chunklength;    // skips over rest of chunk (ignores it)
	        break;
	    }

	    // eat child chunks
	    while (i < chunklength)
	    {
	    	
	        i += eatChunk(buffer,i+offset,count,angle_y,z_adjust,scale);		
	    }
	    return chunklength;
	}		

	public static void storeMesh(String name,Sd3dMesh mesh)
	{
		Sd3dMesh.meshCache.put(name, mesh);
	}
	
	public static Sd3dMesh getMesh(String name)
	{
		return (Sd3dMesh)meshCache.get(name);
	}
	
	public void load3ds(String filename,int count,float angle_y,float z_adjust,float scale) throws IOException
	{
		Log.d("Sd3dMesh","Loading : "+filename);
		if (meshCache.get(filename+(""+angle_y)+count) == null)
		{
			InputStream is = Sd3dRessourceManager.getManager().getRessource(filename);
			byte buffer[] = new byte[is.available()];			
			is.read(buffer);			
			/*
			FileInputStream fis = new FileInputStream(filename);
			byte buffer[] = new byte[fis.available()];			
			fis.read(buffer);
			 */
	
			this.mMeshPosition = new float[3];
			currentcount = 0;
			eatChunk(buffer,0,count,angle_y,z_adjust,scale);
			is.close();
			//fis.close();
			this.mMeshName = filename;
			this.mMeshPosition = null;
			Sd3dMesh.storeMesh(filename+(""+angle_y)+count, this);
			
		}
		else 
		{
			this.copyReference(Sd3dMesh.getMesh(filename+(""+angle_y)+count));
		}
		Log.d("Sd3dMesh","Loading : "+filename+" DONE CAP="+this.mIndices.capacity());
	}	

	public void copyReference(Sd3dMesh mesh)
	{
		mIndices = mesh.mIndices;
		//mTexCoords = mesh.mTexCoords;
		mVertices = mesh.mVertices;
		mMeshPosition = mesh.mMeshPosition;
		
	}	
	
	public void mergeVertexIndices()
	{
		int countmerge = 0;
		for (int i = 0; i < mIndices.capacity();i++)
		{
			Log.d("mergeVertexIndices()","i="+i+" vertice merged="+countmerge);
			int indice = mIndices.get(i);
			for (int j = 0;j < i;j++)
			{
				char indice2 = mIndices.get(j);
				
				float x = mVertices.get(indice * Sd3dMesh.nbFloatPerVertex);
				float y = mVertices.get(indice * Sd3dMesh.nbFloatPerVertex + 1);
				float z = mVertices.get(indice * Sd3dMesh.nbFloatPerVertex + 2);
				
				float x2 = mVertices.get(indice2 * Sd3dMesh.nbFloatPerVertex);
				float y2 = mVertices.get(indice2 * Sd3dMesh.nbFloatPerVertex + 1);
				float z2 = mVertices.get(indice2 * Sd3dMesh.nbFloatPerVertex + 2);
				
				if ((x == x2)&&(y==y2)&&(z==z2))
				{
					mIndices.put(i, indice2);
					countmerge++;
				}
			}
		}
		Log.d("mergeVertexIndices()","Vertex merged: "+countmerge);
	}
	
	public void mergeNormals() {
		FloatBuffer normalAccumulationBuffer = FloatBuffer.allocate(mVertices.capacity());
		for (int i = 0; i < mIndices.capacity();i++)
		{
			int indice = mIndices.get(i);
			int offset_indice = indice * Sd3dMesh.nbFloatPerVertex;
			
			for (int j = 0;j < mIndices.capacity();j++)
			{
				char indice2 = mIndices.get(j);
				int offset_indice2 = indice2 * Sd3dMesh.nbFloatPerVertex;
				float x = mVertices.get(indice * Sd3dMesh.nbFloatPerVertex);
				float y = mVertices.get(indice * Sd3dMesh.nbFloatPerVertex + 1);
				float z = mVertices.get(indice * Sd3dMesh.nbFloatPerVertex + 2);
				
				float x2 = mVertices.get(indice2 * Sd3dMesh.nbFloatPerVertex);
				float y2 = mVertices.get(indice2 * Sd3dMesh.nbFloatPerVertex + 1);
				float z2 = mVertices.get(indice2 * Sd3dMesh.nbFloatPerVertex + 2);
				
				
				
				if ((x == x2)&&(y==y2)&&(z==z2))
				{
					//mIndices.put(i, indice2);
					//countmerge++;
					normalAccumulationBuffer.put(offset_indice2 + 3, normalAccumulationBuffer.get(offset_indice2 + 3) + mVertices.get(offset_indice + 3));
					normalAccumulationBuffer.put(offset_indice2 + 4, normalAccumulationBuffer.get(offset_indice2 + 4) + mVertices.get(offset_indice + 4));
					normalAccumulationBuffer.put(offset_indice2 + 5, normalAccumulationBuffer.get(offset_indice2 + 5) + mVertices.get(offset_indice + 5));					
				}				
			}
		}
		
		Sd3dVector normal = new Sd3dVector();
		for (int i = 0; i < mIndices.capacity();i++)
		{
			int indice = mIndices.get(i);
			int offset_indice = indice * Sd3dMesh.nbFloatPerVertex;
			normal.set(0,normalAccumulationBuffer.get(offset_indice + 3));
			normal.set(1,normalAccumulationBuffer.get(offset_indice + 4));
			normal.set(2,normalAccumulationBuffer.get(offset_indice + 5));
			normal.normalize();
			mVertices.put(offset_indice + 3,normal.get(0));
			mVertices.put(offset_indice + 4,normal.get(1));
			mVertices.put(offset_indice + 5,normal.get(2));		
		}
	}
	
	public void mergeTangents() {
		
		FloatBuffer normalAccumulationBuffer = FloatBuffer.allocate(mTangentsBinormals.capacity());
		for (int i = 0; i < mIndices.capacity();i++)
		{
			int indice = mIndices.get(i);
			int offset_indice = indice * 6;
			
			for (int j = 0;j < mIndices.capacity();j++)
			{
				char indice2 = mIndices.get(j);
				int offset_indice2 = indice2 * 6;
				float x = mVertices.get(indice * Sd3dMesh.nbFloatPerVertex);
				float y = mVertices.get(indice * Sd3dMesh.nbFloatPerVertex + 1);
				float z = mVertices.get(indice * Sd3dMesh.nbFloatPerVertex + 2);
				
				float x2 = mVertices.get(indice2 * Sd3dMesh.nbFloatPerVertex);
				float y2 = mVertices.get(indice2 * Sd3dMesh.nbFloatPerVertex + 1);
				float z2 = mVertices.get(indice2 * Sd3dMesh.nbFloatPerVertex + 2);
				
				
				
				if ((x == x2)&&(y==y2)&&(z==z2))
				{
					//mIndices.put(i, indice2);
					//countmerge++;
					normalAccumulationBuffer.put(offset_indice2 + 0, normalAccumulationBuffer.get(offset_indice2 + 0) + mTangentsBinormals.get(offset_indice + 0));
					normalAccumulationBuffer.put(offset_indice2 + 1, normalAccumulationBuffer.get(offset_indice2 + 1) + mTangentsBinormals.get(offset_indice + 1));
					normalAccumulationBuffer.put(offset_indice2 + 2, normalAccumulationBuffer.get(offset_indice2 + 2) + mTangentsBinormals.get(offset_indice + 2));		
					
					normalAccumulationBuffer.put(offset_indice2 + 3, normalAccumulationBuffer.get(offset_indice2 + 3) + mTangentsBinormals.get(offset_indice + 3));
					normalAccumulationBuffer.put(offset_indice2 + 4, normalAccumulationBuffer.get(offset_indice2 + 4) + mTangentsBinormals.get(offset_indice + 4));
					normalAccumulationBuffer.put(offset_indice2 + 5, normalAccumulationBuffer.get(offset_indice2 + 5) + mTangentsBinormals.get(offset_indice + 5));						
				}				
			}
		}
		
		Sd3dVector normal = new Sd3dVector();
		for (int i = 0; i < mIndices.capacity();i++)
		{
			int indice = mIndices.get(i);
			int offset_indice = indice * 6;
			normal.set(0,normalAccumulationBuffer.get(offset_indice + 0));
			normal.set(1,normalAccumulationBuffer.get(offset_indice + 1));
			normal.set(2,normalAccumulationBuffer.get(offset_indice + 2));
			normal.normalize();
			mTangentsBinormals.put(offset_indice + 0,normal.get(0));
			mTangentsBinormals.put(offset_indice + 1,normal.get(1));
			mTangentsBinormals.put(offset_indice + 2,normal.get(2));		
			
			normal.set(0,normalAccumulationBuffer.get(offset_indice + 3));
			normal.set(1,normalAccumulationBuffer.get(offset_indice + 4));
			normal.set(2,normalAccumulationBuffer.get(offset_indice + 5));
			normal.normalize();
			mTangentsBinormals.put(offset_indice + 3,normal.get(0));
			mTangentsBinormals.put(offset_indice + 4,normal.get(1));
			mTangentsBinormals.put(offset_indice + 5,normal.get(2));				
			
		}
	}	
	
	
	public void copy(Sd3dMesh mesh)
	{
		mIndices = CharBuffer.allocate(mesh.mIndices.position());
		//mTexCoords = FloatBuffer.allocate(mesh.mTexCoords.position());
		mVertices = FloatBuffer.allocate(mesh.mVertices.position());
		
		for (int j = 0; j < mIndices.capacity();j++)
			mIndices.put(mesh.mIndices.get(j));
		
		//for (int j = 0; j < mTexCoords.capacity();j++)
		//	mTexCoords.put(mesh.mTexCoords.get(j));	
		
		for (int j = 0; j < mVertices.capacity();j++)
			mVertices.put(mesh.mVertices.get(j));
		
		mIndices.position(0);
		//mTexCoords.position(0);
		mVertices.position(0);
	}
	
	public void init(int triangleCount,int verticeCount)
	{
		mIndices = CharBuffer.allocate(triangleCount*3);
		//mTexCoords = FloatBuffer.allocate(verticeCount*2);
		mVertices = FloatBuffer.allocate(verticeCount*3);		
	}
	
	public void generateNormalsPerVertex()
	{
		Sd3dVector res = new Sd3dVector();
		Sd3dVector vm1 = new Sd3dVector();		
		Sd3dVector vm2 = new Sd3dVector();
		Sd3dVector v0 = new Sd3dVector();
		Sd3dVector v1 = new Sd3dVector();
		Sd3dVector v2 = new Sd3dVector();
		
		int trianglecount = this.mIndices.capacity()/3;
		FloatBuffer normals = FloatBuffer.allocate(trianglecount*3);

		//this.mNormals = FloatBuffer.allocate(this.mVertices.capacity());		
		
		this.mIndices.position(0);
		for (int i = 0;i < trianglecount;i++)
		{
			int a,b,c;
			a = this.mIndices.get();
			b = this.mIndices.get();
			c = this.mIndices.get();
			
			v0.setFromVertice(this.mVertices,a);
			v1.setFromVertice(this.mVertices,b);
			v2.setFromVertice(this.mVertices,c);
			
			Sd3dVector.sub(vm1, v1, v0);
			Sd3dVector.sub(vm2, v2, v0);
			Sd3dVector.cross(res, vm1, vm2);
			
			res.normalize();
			
			normals.put(res.get(0));
			normals.put(res.get(1));
			normals.put(res.get(2));
			
			
					
		}
		
		
		for (int i = 0;i < this.mVertices.capacity()/3;i++)
		{
			float xv,yv,zv;
			int count = 0;
			xv = this.mVertices.get(i*3);
			yv = this.mVertices.get(i*3+1);
			zv = this.mVertices.get(i*3+2);				
			
			/*
			this.mNormals.put(i*3,0f);
			this.mNormals.put(i*3+1,0f);			
			this.mNormals.put(i*3+2,0f);
			*/
			addNormal(i, 0f, 0f, 0f);
			for (int k = 0;k < trianglecount;k++)
			{
				int a,b,c;
				a = this.mIndices.get(k*3);
				b = this.mIndices.get(k*3+1);
				c = this.mIndices.get(k*3+2);
				
				float x,y,z;
				x = this.mVertices.get(a*3);
				y = this.mVertices.get(a*3+1);
				z = this.mVertices.get(a*3+2);
	
				float xnormal,ynormal,znormal;				
				xnormal = normals.get(k*3);
				ynormal = normals.get(k*3+1);
				znormal = normals.get(k*3+2);				
				
				if ((x == xv)&&(y==yv)&&(z==zv))
				{
				
					
					count++;
					/*
					this.mNormals.put(i*3,xnormal+this.mNormals.get(i*3));
					this.mNormals.put(i*3+1,ynormal+this.mNormals.get(i*3+1));
					this.mNormals.put(i*3+2,znormal+this.mNormals.get(i*3+2));
					*/	
					this.addNormal(i,xnormal+getNormal(i,0),ynormal+getNormal(i,1),znormal+getNormal(i,2));
					
				}
				
				x = this.mVertices.get(b*3);
				y = this.mVertices.get(b*3+1);
				z = this.mVertices.get(b*3+2);
				
				if ((x == xv)&&(y==yv)&&(z==zv))
				{			
					
					count++;
					this.addNormal(i,xnormal+getNormal(i,0),ynormal+getNormal(i,1),znormal+getNormal(i,2));				
				}
				
				x = this.mVertices.get(c*3);
				y = this.mVertices.get(c*3+1);
				z = this.mVertices.get(c*3+2);
				
				if ((x == xv)&&(y==yv)&&(z==zv))
				{				
					
					count++;
					//this.mNormals.put(i*3,xnormal+this.mNormals.get(i*3));
					//this.mNormals.put(i*3+1,ynormal+this.mNormals.get(i*3+1));
					//this.mNormals.put(i*3+2,znormal+this.mNormals.get(i*3+2));	
					this.addNormal(i,xnormal+getNormal(i,0),ynormal+getNormal(i,1),znormal+getNormal(i,2));
				}				
			}
			
			if (count > 0)
			{
				//System.out.println("count="+count);
				//this.mNormals.put(i*3,this.mNormals.get(i*3)/count);
				//this.mNormals.put(i*3+1,this.mNormals.get(i*3+1)/count);			
				//this.mNormals.put(i*3+2,this.mNormals.get(i*3+2)/count);	
				this.addNormal(i,getNormal(i,0)/count,getNormal(i,1)/count,getNormal(i,2)/count);
			}// else 				System.out.println("count="+count);
			
		}
		
		
		
		this.mIndices.position(0);
		//this.mNormals.position(0);
		this.mVertices.position(0);
	}	
	
	public void generateNormals()
	{
		Sd3dVector res = new Sd3dVector();
		Sd3dVector vm1 = new Sd3dVector();		
		Sd3dVector vm2 = new Sd3dVector();
		Sd3dVector v0 = new Sd3dVector();
		Sd3dVector v1 = new Sd3dVector();
		Sd3dVector v2 = new Sd3dVector();
		
		int trianglecount = this.mIndices.capacity()/3;
		//this.mNormals = FloatBuffer.allocate(this.mVertices.capacity());
		
		this.mIndices.position(0);
		for (int i = 0;i < trianglecount;i++)
		{
			int a,b,c;
			a = this.mIndices.get();
			b = this.mIndices.get();
			c = this.mIndices.get();
			
			v0.setFromVertice(this.mVertices,a);
			v1.setFromVertice(this.mVertices,b);
			v2.setFromVertice(this.mVertices,c);
			
			Sd3dVector.sub(vm1, v1, v0);
			Sd3dVector.sub(vm2, v2, v0);
			Sd3dVector.cross(res, vm1, vm2);
			if (res.length() == 0.f) {
				Log.d("generateNormals()","normal is null !");
			}
			res.normalize();

			this.addNormal(a,getNormal(a,0)+res.get(0),getNormal(a,1)+res.get(1),getNormal(a,2)+res.get(2));
			
			Sd3dVector.sub(vm1, v2, v1);
			Sd3dVector.sub(vm2, v0, v1);
			Sd3dVector.cross(res, vm1, vm2);			
			res.normalize();
			
			this.addNormal(b,getNormal(b,0)+res.get(0),getNormal(b,1)+res.get(1),getNormal(b,2)+res.get(2));
			
			Sd3dVector.sub(vm1, v0, v2);
			Sd3dVector.sub(vm2, v1, v2);
			Sd3dVector.cross(res, vm1, vm2);			
			res.normalize();			
			
			this.addNormal(c,getNormal(c,0)+res.get(0),getNormal(c,1)+res.get(1),getNormal(c,2)+res.get(2));				
		}
		
		
		Sd3dVector normal = new Sd3dVector();
		for (int i = 0;i < this.mVertices.capacity() / Sd3dMesh.nbFloatPerVertex;i++)
		{		
			normal.set(0, this.mVertices.get(i * Sd3dMesh.nbFloatPerVertex + 3));
			normal.set(1, this.mVertices.get(i * Sd3dMesh.nbFloatPerVertex + 4));
			normal.set(2, this.mVertices.get(i * Sd3dMesh.nbFloatPerVertex + 5));		
			normal.normalize();
			this.mVertices.put(i * Sd3dMesh.nbFloatPerVertex + 3, normal.get(0));
			this.mVertices.put(i * Sd3dMesh.nbFloatPerVertex + 4, normal.get(1));
			this.mVertices.put(i * Sd3dMesh.nbFloatPerVertex + 5, normal.get(2));
		}
		this.mIndices.position(0);
		//this.mNormals.position(0);
		this.mVertices.position(0);
	}
	
	
	public void generateNormalsTangentsBinormals()
	{
		//generateNormals();
		//mergeNormals();
		
		this.mTangentsBinormals = FloatBuffer.allocate(this.mVertices.capacity() / Sd3dMesh.nbFloatPerVertex * 6);
		
		int trianglecount = this.mIndices.capacity()/3;		
		this.mIndices.position(0);
		
		Sd3dVector normal = new Sd3dVector();
		Sd3dVector tangent = new Sd3dVector();
		Sd3dVector binormal = new Sd3dVector();
		Sd3dVector2d texCoords0 = new Sd3dVector2d();
		Sd3dVector2d texCoords1 = new Sd3dVector2d();
		Sd3dVector2d texCoords2 = new Sd3dVector2d();
		Sd3dVector vm1 = new Sd3dVector();		
		Sd3dVector vm2 = new Sd3dVector();
		Sd3dVector2d t1 = new Sd3dVector2d();		
		Sd3dVector2d t2 = new Sd3dVector2d();		
		Sd3dVector v0 = new Sd3dVector();
		Sd3dVector v1 = new Sd3dVector();
		Sd3dVector v2 = new Sd3dVector();		
		float coef;
		for (int i = 0;i < trianglecount;i++)
		{
			int a,b,c;			
			a = this.mIndices.get();
			b = this.mIndices.get();
			c = this.mIndices.get();			

			v0.setFromVertice(this.mVertices,a);
			v1.setFromVertice(this.mVertices,b);
			v2.setFromVertice(this.mVertices,c);			

			texCoords0.setFromTexCoords(this.mVertices, a);
			texCoords1.setFromTexCoords(this.mVertices, b);
			texCoords2.setFromTexCoords(this.mVertices, c);			
			
			// 1 side 				
			Sd3dVector.sub(vm1, v1, v0);
			Sd3dVector.sub(vm2, v2, v0);
			
			Sd3dVector2d.sub(t1, texCoords1, texCoords0);
			Sd3dVector2d.sub(t2, texCoords2, texCoords0);
		
			vm1.normalize();
			vm2.normalize();
			t1.normalize();
			t2.normalize();					
			
			coef = 1/ (t1.getX() * t2.getY() - t2.getX() * t1.getY());
			
			tangent.set(0, coef * (vm1.get(0) * t2.getY() + vm2.get(0) * -t1.getY()));
			tangent.set(1, coef * (vm1.get(1) * t2.getY() + vm2.get(1) * -t1.getY()));
			tangent.set(2, coef * (vm1.get(2) * t2.getY() + vm2.get(2) * -t1.getY()));
			
			tangent.normalize();		
			
			normal.setFromNormal(this.mVertices,a);
			Sd3dVector.cross(binormal, normal, tangent);
			binormal.normalize();
			
			addTangent(a,getTangent(a,0)+tangent.get(0),getTangent(a,1)+tangent.get(1),getTangent(a,2)+tangent.get(2));
			addBinormal(a,getBinormal(a,0)+binormal.get(0),getBinormal(a,1)+binormal.get(1),getBinormal(a,2)+binormal.get(2));
			
			// 2 side 			
			Sd3dVector.sub(vm1, v2, v1);
			Sd3dVector.sub(vm2, v0, v1);		

			Sd3dVector2d.sub(t1, texCoords2, texCoords1);
			Sd3dVector2d.sub(t2, texCoords0, texCoords1);
			
			vm1.normalize();
			vm2.normalize();
			t1.normalize();
			t2.normalize();				
			
			coef = 1/ (t1.getX() * t2.getY() - t2.getX() * t1.getY());
			
			tangent.set(0, coef * (vm1.get(0) * t2.getY() + vm2.get(0) * -t1.getY()));
			tangent.set(1, coef * (vm1.get(1) * t2.getY() + vm2.get(1) * -t1.getY()));
			tangent.set(2, coef * (vm1.get(2) * t2.getY() + vm2.get(2) * -t1.getY()));
			
			tangent.normalize();		
			
			normal.setFromNormal(this.mVertices,b);			
			Sd3dVector.cross(binormal, normal, tangent);
			binormal.normalize();
			
			addTangent(b,getTangent(b,0)+tangent.get(0),getTangent(b,1)+tangent.get(1),getTangent(b,2)+tangent.get(2));
			addBinormal(b,getBinormal(b,0)+binormal.get(0),getBinormal(b,1)+binormal.get(1),getBinormal(b,2)+binormal.get(2));			
			
			// 3 side 			
			Sd3dVector.sub(vm1, v0, v2);
			Sd3dVector.sub(vm2, v1, v2);				

			Sd3dVector2d.sub(t1, texCoords0, texCoords2);
			Sd3dVector2d.sub(t2, texCoords1, texCoords2);
			
			vm1.normalize();
			vm2.normalize();
			t1.normalize();
			t2.normalize();				
			
			coef = 1/ (t1.getX() * t2.getY() - t2.getX() * t1.getY());

			tangent.set(0, coef * (vm1.get(0) * t2.getY() + vm2.get(0) * -t1.getY()));
			tangent.set(1, coef * (vm1.get(1) * t2.getY() + vm2.get(1) * -t1.getY()));
			tangent.set(2, coef * (vm1.get(2) * t2.getY() + vm2.get(2) * -t1.getY()));
			
			tangent.normalize();
					
			normal.setFromNormal(this.mVertices,c);			
			Sd3dVector.cross(binormal, normal, tangent);
			binormal.normalize();
			
			addTangent(c,getTangent(c,0)+tangent.get(0),getTangent(c,1)+tangent.get(1),getTangent(c,2)+tangent.get(2));
			addBinormal(c,getBinormal(c,0)+binormal.get(0),getBinormal(c,1)+binormal.get(1),getBinormal(c,2)+binormal.get(2));				
		}
		
		Sd3dVector tmp = new Sd3dVector();
		for (int i = 0;i < this.mTangentsBinormals.capacity() / 6;i++)
		{		
			tmp.set(0, this.mTangentsBinormals.get(i * 6));
			tmp.set(1, this.mTangentsBinormals.get(i * 6 + 1));
			tmp.set(2, this.mTangentsBinormals.get(i * 6 + 2));		
			tmp.normalize();
			this.mTangentsBinormals.put(i * 6 + 0, tmp.get(0));
			this.mTangentsBinormals.put(i * 6 + 1, tmp.get(1));
			this.mTangentsBinormals.put(i * 6 + 2, tmp.get(2));
			
			tmp.set(0, this.mTangentsBinormals.get(i * 6 + 3));
			tmp.set(1, this.mTangentsBinormals.get(i * 6 + 4));
			tmp.set(2, this.mTangentsBinormals.get(i * 6 + 5));		
			tmp.normalize();
			this.mTangentsBinormals.put(i * 6 + 3, tmp.get(0));
			this.mTangentsBinormals.put(i * 6 + 4, tmp.get(1));
			this.mTangentsBinormals.put(i * 6 + 5, tmp.get(2));			
		}		
		

		mergeTangents();		
		
		this.mIndices.position(0);
		this.mTangentsBinormals.position(0);
	}
	
	/**
	 * Apply a 3x3 matrix to mesh vertices position and normal
	 * @param matrix
	 */
	public void applyMatrix(Sd3dMatrix matrix)
	{
		float position[] = new float[3];
		float normal[] = new float[3];	
		float result[] = new float[3];
		int nbVert = this.mVertices.capacity() / Sd3dMesh.nbFloatPerVertex;

		for(int i = 0;i < nbVert;i++)
		{
			position[0] = this.mVertices.get(i * Sd3dMesh.nbFloatPerVertex + 0);
			position[1] = this.mVertices.get(i * Sd3dMesh.nbFloatPerVertex + 1);
			position[2] = this.mVertices.get(i * Sd3dMesh.nbFloatPerVertex + 2);
			
			normal[0] = this.mVertices.get(i * Sd3dMesh.nbFloatPerVertex + 3);
			normal[1] = this.mVertices.get(i * Sd3dMesh.nbFloatPerVertex + 4);
			normal[2] = this.mVertices.get(i * Sd3dMesh.nbFloatPerVertex + 5);
			
			Sd3dMatrix.mul(result,matrix,position);
			
			this.mVertices.put(i * Sd3dMesh.nbFloatPerVertex + 0, result[0]);
			this.mVertices.put(i * Sd3dMesh.nbFloatPerVertex + 1, result[1]);
			this.mVertices.put(i * Sd3dMesh.nbFloatPerVertex + 2, result[2]);
			
			Sd3dMatrix.mul(result,matrix,normal);
			
			this.mVertices.put(i * Sd3dMesh.nbFloatPerVertex + 3, result[0]);
			this.mVertices.put(i * Sd3dMesh.nbFloatPerVertex + 4, result[1]);
			this.mVertices.put(i * Sd3dMesh.nbFloatPerVertex + 5, result[2]);			
		}
	}
	
	public void applyVector(float v[])
	{
		int nbVert = this.mVertices.capacity() / Sd3dMesh.nbFloatPerVertex;
		float position[] = new float[3];
		for(int i = 0;i < nbVert;i++)
		{		
			position[0] = this.mVertices.get(i * Sd3dMesh.nbFloatPerVertex + 0);
			position[1] = this.mVertices.get(i * Sd3dMesh.nbFloatPerVertex + 1);
			position[2] = this.mVertices.get(i * Sd3dMesh.nbFloatPerVertex + 2);
			
			position[0] += v[0];
			position[1] += v[1];
			position[2] += v[2];
			
			this.mVertices.put(i * Sd3dMesh.nbFloatPerVertex + 0, position[0]);
			this.mVertices.put(i * Sd3dMesh.nbFloatPerVertex + 1, position[1]);
			this.mVertices.put(i * Sd3dMesh.nbFloatPerVertex + 2, position[2]);			
		}
		
		
	}
	
}