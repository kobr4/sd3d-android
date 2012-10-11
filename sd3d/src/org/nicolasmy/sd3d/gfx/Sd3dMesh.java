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

import android.util.Log;

public class Sd3dMesh
{
	public static HashMap<String, Object> meshCache = new HashMap<String,Object>();
	public final static int nbFloatPerVertex = 3 + 3 + 2;
	public FloatBuffer mVertices;
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
			char a = (char) (mIndices.get(i));
			char b = (char) (mIndices.get(i+1));
			char c = (char) (mIndices.get(i+2));
			
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
			char a = (char) (mIndices.get(i));
			char b = (char) (mIndices.get(i+1));
			char c = (char) (mIndices.get(i+2));
			
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
		
		mIndices.put((char)indice);
		mIndices.put((char)(indice+1));
		mIndices.put((char)(indice+2));		
		
		
		mIndices.put((char)(indice+2));
		mIndices.put((char)(indice+3));
		mIndices.put((char)indice);		
		
		/*
		putQuad(x1,y1,z1,x2,y2,z2,
				x3,y3,z3,x4,y4,z4);		
		
		mTexCoords.put(x1t);
		mTexCoords.put(y1t);
		
		mTexCoords.put(x2t);
		mTexCoords.put(y2t);		
	
		mTexCoords.put(x3t);
		mTexCoords.put(y3t);
		
		mTexCoords.put(x4t);
		mTexCoords.put(y4t);		
		/*
		mTexCoords.position(0);
		mIndices.position(0);
		mVertices.position(0);
		*/
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
	
	public float getNormal(int indice, int element)
	{
		return mVertices.get(indice * Sd3dMesh.nbFloatPerVertex + 3 + element);
	}
	
	
	public void putTriangle(float x1,float y1,float z1,
			float x2,float y2,float z2,
			float x3,float y3,float z3)
	{
		int indice = mVertices.position()/Sd3dMesh.nbFloatPerVertex;
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
				
				float x = mVertices.get(indice * 3);
				float y = mVertices.get(indice * 3 + 1);
				float z = mVertices.get(indice * 3 + 2);
				
				float x2 = mVertices.get(indice2 * 3);
				float y2 = mVertices.get(indice2 * 3 + 1);
				float z2 = mVertices.get(indice2 * 3 + 2);
				
				if ((x == x2)&&(y==y2)&&(z==z2))
				{
					mIndices.put(i, indice2);
					countmerge++;
				}
			}
		}
		Log.d("mergeVertexIndices()","Vertex merged: "+countmerge);
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
			
			res.normalize();

			/*
			this.mNormals.put(a*3,this.mNormals.get(a*3)+res.get(0)/3.f);
			this.mNormals.put(a*3+1,this.mNormals.get(a*3+1)+res.get(1)/3.f);
			this.mNormals.put(a*3+2,this.mNormals.get(a*3+2)+res.get(2)/3.f);
			
			this.mNormals.put(b*3,this.mNormals.get(b*3)+res.get(0)/3.f);
			this.mNormals.put(b*3+1,this.mNormals.get(b*3+1)+res.get(1)/3.f);
			this.mNormals.put(b*3+2,this.mNormals.get(b*3+2)+res.get(2)/3.f);
			
			this.mNormals.put(c*3,this.mNormals.get(c*3)+res.get(0)/3.f);
			this.mNormals.put(c*3+1,this.mNormals.get(c*3+1)+res.get(1)/3.f);
			this.mNormals.put(c*3+2,this.mNormals.get(c*3+2)+res.get(2)/3.f);	
			*/
			/*
			this.addNormal(a,res.get(0),res.get(1),res.get(2));
			this.addNormal(b,res.get(0),res.get(1),res.get(2));
			this.addNormal(c,res.get(0),res.get(1),res.get(2));		
			*/	
			
			this.addNormal(a,getNormal(a,0)+res.get(0)/3.f,getNormal(a,1)+res.get(1)/3.f,getNormal(a,2)+res.get(2)/3.f);
			this.addNormal(b,getNormal(b,0)+res.get(0)/3.f,getNormal(b,1)+res.get(1)/3.f,getNormal(b,2)+res.get(2)/3.f);
			this.addNormal(c,getNormal(c,0)+res.get(0)/3.f,getNormal(c,1)+res.get(1)/3.f,getNormal(c,2)+res.get(2)/3.f);			
			
		}
		
		this.mIndices.position(0);
		//this.mNormals.position(0);
		this.mVertices.position(0);
	}
}