package org.nicolasmy.sd3d.gfx.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.opengles.GL11;

import org.nicolasmy.sd3d.gfx.Sd3dMaterial;
import org.nicolasmy.sd3d.gfx.Sd3dMesh;
import org.nicolasmy.sd3d.gfx.Sd3dObject;
import org.nicolasmy.sd3d.gfx.Sd3dScene;



import android.opengl.GLES20;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.util.Log;

/**
 * GLES1 rendering class
 * @author kobr4
 *
 */
@Deprecated
public class Sd3dRenderer implements Sd3dRendererInterface
{
	public enum ALIGN
	{
		TOP,
		BOTTOM,
		LEFT,
		RIGHT,
		CENTER
	}
	
	public GL11 mGl;
    private boolean mTranslucentBackground;
	public int screenWidth;
	public int screenHeight;
	private int top;
	private float m_Frustum[][] = {new float[4],new float[4],new float[4],new float[4],new float[4],new float[4]};
	
    
	
	private Sd3dRendererElement mRenderList[];
	private int mMaxRenderElement;
	private int mCountRenderElement;
	public Sd3dBmpFont mBmpFont;
	public float mLightVector[];
	
	/* (non-Javadoc)
	 * @see org.nicolasmy.sd3d.gfx.Sd3dRendererInterface#setGL11Context(javax.microedition.khronos.opengles.GL11)
	 */
	public void setGL11Context(GL11 gl)
	{
		this.mGl = gl;
	}

	
	public void regiserMesh(Sd3dRendererElement element, Sd3dMesh mesh)
	{
		//OpenGL stuffs
		IntBuffer buffer = IntBuffer.allocate(1);
		
		if (mesh.mVertices != null)
		{
			mGl.glGenBuffers(1,buffer);
			mGl.glBindBuffer(GL11.GL_ARRAY_BUFFER, buffer.get(0));
			mGl.glBufferData(GL11.GL_ARRAY_BUFFER,mesh.mVertices.capacity() * 4,mesh.mVertices,GL11.GL_STATIC_DRAW);
			element.mVertexBufferName = buffer.get(0);
		} else element.mVertexBufferName = 0;
		
		/*
		if (mesh.mNormals != null)
		{
			mGl.glGenBuffers(1,buffer);
			mGl.glBindBuffer(GL11.GL_ARRAY_BUFFER, buffer.get(0));
			mGl.glBufferData(GL11.GL_ARRAY_BUFFER,mesh.mNormals.capacity() * 4,mesh.mNormals,GL11.GL_STATIC_DRAW);
			element.mNormalBufferName = buffer.get(0);
		} else element.mNormalBufferName = 0;			
		*/
		
		if (mesh.mIndices != null)
		{
			mGl.glGenBuffers(1,buffer);
			mGl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, buffer.get(0));
			mGl.glBufferData(GL11.GL_ELEMENT_ARRAY_BUFFER,mesh.mIndices.capacity()*2,mesh.mIndices,GL11.GL_STATIC_DRAW);
			element.mIndiceBufferName = buffer.get(0);
			element.mIndiceCount = mesh.mIndices.capacity();
		} else element.mIndiceBufferName = 0;
		
		/*
		if (mesh.mTexCoords != null)
		{
			mGl.glGenBuffers(1,buffer);
			mGl.glBindBuffer(GL11.GL_ARRAY_BUFFER, buffer.get(0));
			mGl.glBufferData(GL11.GL_ARRAY_BUFFER,mesh.mTexCoords.capacity() * 4,mesh.mTexCoords,GL11.GL_STATIC_DRAW);
			element.mTexCoordBufferName = buffer.get(0);
		} else element.mTexCoordBufferName = 0;		
		*/
		
		element.mIsShadowVolume = mesh.mIsShadowVolume;
		element.mIsBillboard = mesh.mIsBillboard;
		element.mIsInScreenSpace = mesh.mIsInScreenSpace;
	}	
	
	public int registerMaterial(Sd3dRendererElement element, Sd3dMaterial material)
	{
		//OpenGL stuffs	
		IntBuffer buffer = IntBuffer.allocate(1);		
		
		if (material.mColors != null)
		{
			mGl.glGenBuffers(1,buffer);
			mGl.glBindBuffer(GL11.GL_ARRAY_BUFFER, buffer.get(0));
			mGl.glBufferData(GL11.GL_ARRAY_BUFFER,material.mColors.capacity() * 4,material.mColors,GL11.GL_STATIC_DRAW);
			//mColorBufferName = buffer.get(0);
			//material.mColorName = mColorBufferName;
			material.mColorName =  buffer.get(0);
		//} else mColorBufferName = 0;	
		} else material.mColorName = 0;
	
		if (material.mTextureData != null)
		{
			mGl.glGenTextures(1, buffer); 
			mGl.glBindTexture(GL11.GL_TEXTURE_2D, buffer.get(0)); 
			mGl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
			mGl.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA,material.mWidth, material.mHeight, 0, 
					GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, material.mTextureData);
			mGl.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR); 
			mGl.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR); 
			
			
			element.mTextureName = buffer.get(0);
			
		}
		
		return 0;
	}		
	
	public Sd3dRendererElement[] createRenderElement(Sd3dObject object)
	{
		Sd3dRendererElement element[] = new Sd3dRendererElement[object.mMesh.length];
		for (int i = 0; i < object.mMesh.length;i++)
		{
			if (object.mMesh[i] != null)
			{
				element[i] = new Sd3dRendererElement();
				if (object.mMesh[i] != null)
					regiserMesh(element[i],object.mMesh[i]);
				if (i < object.mMaterial.length)
				  if (object.mMaterial[i] != null)
					  registerMaterial(element[i],object.mMaterial[i]);	
		
				element[i].mObject = object;
				

				element[i].mIsPickable = object.mIsPickable;
				if (element[i].mIsPickable)
				{
					generatePickingColor(element[i]);
				}
				
				if (object.mPosition != null)
				{
					element[i].mPosition = object.mPosition;
				}
				
				if (object.mMesh[i].mMeshPosition != null)
				{
					element[i].mPosition = object.mMesh[i].mMeshPosition;	
				}
				
				if (i < object.mMaterial.length)
				if (object.mMaterial[i].useAlphaBlending())
				{
					element[i].mAlphaBlending = true;
				} else element[i].mAlphaBlending = false;
		
				if (i < object.mMaterial.length)
				if (object.mMaterial[i].isAlphaTest())
				{
					element[i].mAlphaTest = true;
				} else element[i].mAlphaTest = false;			
	
				if (i < object.mMaterial.length)
				if (object.mMaterial[i].isRenderLight())
				{
					element[i].mRenderLight = true;
				} else element[i].mRenderLight = false;					
				
				if (object.mRotation != null)
				{
					element[i].mOrientation = object.mRotation;
				} 
			}
		}
		return element;
	}
	
	public void destroyRenderElement(Sd3dObject object)
	{
		for (int i = 0;i < object.mRenderElement.length;i++)
		{
			if (object.mRenderElement[i] != null)					
			{
				unregisterMesh(object.mRenderElement[i]);
				unregisterMaterial(object.mRenderElement[i]);			
			}
		}
	}
	
	public void addObjectToRenderList(Sd3dObject object)
	{
		if (mCountRenderElement < mMaxRenderElement)
		{
			if (object.mRenderElement == null)
			{
				object.mRenderElement = createRenderElement(object);
			}
			
			for (int i = 0;i < object.mRenderElement.length;i++)
			{
				if (object.mRenderElement[i] != null)					
					if (object.mRenderElement[i].mDisable != true)
				{
					mRenderList[mCountRenderElement] = object.mRenderElement[i];
					mCountRenderElement++;
				}
			}
		}
	}
	
	public void unregisterMaterial(Sd3dMaterial material)
	{
		int buffer[] = new int[1];
		
		if (material.mColorName != 0)
		{
			buffer[0] = material.mColorName;
			mGl.glDeleteBuffers(1, buffer, 0);
			material.mColorName = 0;
		}	
	}
	
	public void unregisterMaterial(Sd3dRendererElement element)
	{
		int buffer[] = new int[1];
		
		if (element.mTextureName != 0)
		{
			buffer[0] = element.mTextureName;
			mGl.glDeleteBuffers(1, buffer, 0);
			element.mTextureName = 0;
		}
		
		
		if (element.mObject != null)
		{
			for (int i = 0;i < element.mObject.mMaterial.length;i++)
			{
				unregisterMaterial(element.mObject.mMaterial[i]);
			}
		}
	}

	public void unregisterMesh(Sd3dRendererElement element)
	{
		int buffer[] = new int[1];
		
		if (element.mVertexBufferName != 0)
		{
			buffer[0] = element.mVertexBufferName;
			mGl.glDeleteBuffers(1, buffer, 0);
			element.mVertexBufferName = 0;
		}	
		
		/*
		if (element.mNormalBufferName != 0)
		{
			buffer[0] = element.mNormalBufferName;
			mGl.glDeleteBuffers(1, buffer, 0);
			element.mNormalBufferName = 0;
		}	
		
		if (element.mIndiceBufferName != 0)
		{
			buffer[0] = element.mIndiceBufferName;
			mGl.glDeleteBuffers(1, buffer, 0);
			element.mIndiceBufferName = 0;
		}		
		
		if (element.mTexCoordBufferName != 0)
		{
			buffer[0] = element.mTexCoordBufferName;
			mGl.glDeleteBuffers(1, buffer, 0);
			element.mTexCoordBufferName = 0;
		}
		*/				
	}
	
	FloatBuffer vertexBuffer;
	private void drawShadowVolumeQuad()
	{		
		float vertices[] = {
				-10.0f, 10.0f,  -2.0f,        // V1 - first vertex (x,y,z)
				10.0f, 10.0f,  -2.0f,        // V2 - second vertex
				-10.0f,  -10.0f,  -2.0f,         // V3 - third vertex		
				10.0f, 10.0f,  -2.0f,        // V1 - first vertex (x,y,z)
				10.0f, -10.0f,  -2.0f,        // V2 - second vertex
				-10.0f,  -10.0f,  -2.0f         // V3 - third vertex						
		};		
		
		
		if (vertexBuffer == null)
		{
		  ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
		  vertexByteBuffer.order(ByteOrder.nativeOrder());
		
          // allocates the memory from the byte buffer
	      vertexBuffer = vertexByteBuffer.asFloatBuffer();
	    
		  // fill the vertexBuffer with the vertices
	      vertexBuffer.put(vertices);
	    
	      // set the cursor position to the beginning of the buffer	
	      vertexBuffer.position(0);		
		}
	    //mGl.glMatrixMode(GL11.GL_PROJECTION);
		//mGl.glLoadIdentity();
		//mGl.glOrthof(0, 100, 100, 0, -1, 1);
		//mGl.glClearColor(0.7, 0.7, 0.7, 1.0);
		//mGl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		//mGl.glMatrixMode(GL11.GL_MODELVIEW);
	    mGl.glPushMatrix();
		mGl.glLoadIdentity();
		
		mGl.glEnable(GL11.GL_BLEND);
		mGl.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		//glColor4f(0.0, 0.0, 0.0,0.3);		
		
		mGl.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		
		mGl.glColor4f(0.0f, 0.0f, 0.0f, 0.3f);
		
		mGl.glVertexPointer(3, GL11.GL_FLOAT, 0, vertexBuffer);
		
		mGl.glDrawArrays(GL11.GL_TRIANGLES, 0, 18);
		
		mGl.glDisableClientState(GL11.GL_VERTEX_ARRAY);	
		
		mGl.glDisable(GL11.GL_BLEND);
		mGl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		mGl.glPopMatrix();
		//vertexByteBuffer
	}
	
	
	private float matrix[] = new float[16];
	public void renderRenderElement(Sd3dRendererElement element)
	{
		//OpenGL stuffs	
		
		if (element.mRenderLight)
		{				
			setupLightVector(this.mLightVector[0],this.mLightVector[1],this.mLightVector[2]);
		}		
		
		mGl.glPushMatrix();
		
		if (element.mPosition != null)
		{
			mGl.glTranslatef(element.mPosition[0], element.mPosition[1], element.mPosition[2]);
		}
	
		if (element.mOrientation != null)
		{
			/*
			mGl.glRotatef(-element.mOrientation[0], 1, 0, 0);
			mGl.glRotatef(-element.mOrientation[1], 0, 1, 0);
			mGl.glRotatef(-element.mOrientation[2], 0, 0, 1);
			*/
			//mGl.glRotatef(element.mOrientation[0], 1, 0, 0);
			//float vect[] = {0, 0, 1, 0};
		
			Matrix.setRotateEulerM(matrix, 0, element.mOrientation[0], element.mOrientation[1], element.mOrientation[2]);
			//Matrix.multiplyMV(vect, 0, matrix, 0, vect, 0);
			//mGl.glRotatef(-element.mOrientation[2], vect[0], vect[1], vect[2]);
			mGl.glMultMatrixf(matrix, 0);
		}
				
		if (element.mIsBillboard)
		{
			this.billboardCheatSphericalBegin();
		}
        // draw using hardware buffers             
		if (element.mVertexBufferName != 0)
		{
			mGl.glBindBuffer(GL11.GL_ARRAY_BUFFER, element.mVertexBufferName);            
			mGl.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);         
		}
			
        // draw using hardware buffers    
		/*
		if (element.mNormalBufferName != 0)
		{
			
			mGl.glEnableClientState(GL11.GL_NORMAL_ARRAY); 			
			mGl.glBindBuffer(GL11.GL_ARRAY_BUFFER, element.mNormalBufferName);            
			mGl.glNormalPointer(GL11.GL_FLOAT, 0, 0);
			         
		} 
		else mGl.glDisableClientState(GL11.GL_NORMAL_ARRAY); 
		
		if (element.mTexCoordBufferName != 0)
		{
			mGl.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY); 
			mGl.glBindBuffer(GL11.GL_ARRAY_BUFFER, element.mTexCoordBufferName);             
			mGl.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);                          
		}
		else mGl.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY); 
		*/
		
		//if (element.mColorBufferName != 0)
		if (element.mObject.mMaterial[0].mColorName != 0)
		{		
			
			mGl.glEnableClientState(GL11.GL_COLOR_ARRAY);  
			//mGl.glBindBuffer(GL11.GL_ARRAY_BUFFER, element.mColorBufferName);      
			mGl.glBindBuffer(GL11.GL_ARRAY_BUFFER, element.mObject.mMaterial[0].mColorName);  
			mGl.glColorPointer(4, GL11.GL_FLOAT, 0, 0);  
					
		}
		else
		{	
			if (element.mObject.mMaterial[0].mColors != null)
			{
				Log.d("renderRenderElement()","HAS COLOR POINTER BUT NO COLOR NAME");
				registerMaterial(element,element.mObject.mMaterial[0]);
				mGl.glEnableClientState(GL11.GL_COLOR_ARRAY);  
				mGl.glColorPointer(4, GL11.GL_FLOAT, 0, 0); 
			}
			else
			{
			
			/*
			if (element.mNormalBufferName != 0)
			{
				mGl.glEnableClientState(GL11.GL_COLOR_ARRAY); 		
				mGl.glBindBuffer(GL11.GL_ARRAY_BUFFER, element.mNormalBufferName);            
				mGl.glColorPointer(4, GL11.GL_FLOAT, 0, 0);        
			} 
			else 
				
			*/
			mGl.glDisableClientState(GL11.GL_COLOR_ARRAY);  
			}
			//mGl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);             
			//mGl.glColorPointer(4, GL11.GL_FLOAT, 0, 0);
			//mGl.glDisableClientState(GL11.GL_COLOR_ARRAY);  	
			
		}
	 
	
		if (element.mTextureName != 0)
		{
			mGl.glBindTexture(GL11.GL_TEXTURE_2D, element.mTextureName);	
		} //else mGl.glBindTexture(GL11.GL_TEXTURE_2D, 0);	
		else mGl.glDisable(GL11.GL_TEXTURE_2D);
		
		
		if (element.mAlphaTest)
		{		
			//mGl.glEnable(GL11.GL_BLEND);
	        //mGl.glDisable(GL11.GL_DEPTH_TEST);			
			//mGl.glBlendFunc (GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			//mGl.glDepthMask(false);
			//mGl.glBlendFunc (GL11.GL_ONE,GL11.GL_ONE);
			mGl.glEnable(GL11.GL_ALPHA_TEST);
			mGl.glAlphaFunc(GL11.GL_GREATER,0.1f);
		}

		if (element.mAlphaBlending)
		{
			//mGl.glDisable(GL11.GL_DEPTH_TEST);		
			mGl.glDepthMask(false);
			mGl.glEnable (GL11.GL_BLEND);
			//mGl.glBlendFunc( GL11.GL_ONE, GL11.GL_SRC_ALPHA );
			mGl.glBlendFunc( GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_COLOR );
		}
		
		
		if (element.mIndiceBufferName != 0)
		{		
			mGl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, element.mIndiceBufferName);             
			mGl.glDrawElements(GL11.GL_TRIANGLES, element.mIndiceCount,GL11.GL_UNSIGNED_SHORT, 0);
		}	
		
		if (element.mRenderLight)
		{			
			mGl.glDisable(GL11.GL_LIGHTING);
		}
		
		if (element.mAlphaTest)
		{		
	        //mGl.glEnable(GL11.GL_DEPTH_TEST);			
			//mGl.glDisable(GL11.GL_BLEND);	
			
			mGl.glDisable(GL11.GL_ALPHA_TEST);			
			//mGl.glDepthMask(true);			
		}
		
		mGl.glDisableClientState(GL11.GL_NORMAL_ARRAY); 
		mGl.glDisableClientState(GL11.GL_COLOR_ARRAY);
		mGl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);             
		mGl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0); 	
		mGl.glBindTexture(GL11.GL_TEXTURE_2D, 0);	
		mGl.glEnable(GL11.GL_TEXTURE_2D);
		
		
		if (element.mAlphaBlending)
		{
			//mGl.glEnable(GL11.GL_DEPTH_TEST);		
			mGl.glDisable (GL11.GL_BLEND);
			mGl.glDepthMask(true);
		}
				
		
		if (element.mIsBillboard)
		{
			this.billboardEnd();
		}		
		
		mGl.glPopMatrix();		
	}
	
	
	public void renderRenderElementShadowVolume(Sd3dRendererElement element)
	{
		//OpenGL stuffs	
		
		mGl.glPushMatrix();
	
		if (element.mPosition != null)
		{
			mGl.glTranslatef(element.mPosition[0], element.mPosition[1], element.mPosition[2]);
		}
	
		/*
		if (element.mOrientation != null)
		{
			mGl.glRotatef(-element.mOrientation[0], 1, 0, 0);
			mGl.glRotatef(-element.mOrientation[1], 0, 1, 0);
			mGl.glRotatef(-element.mOrientation[2], 0, 0, 1);		
		}
		*/
		/*
		if (element.mIsBillboard)
		{
			this.billboardCheatSphericalBegin();
		}
		*/
        // draw using hardware buffers             
		if (element.mVertexBufferName != 0)
		{
			mGl.glBindBuffer(GL11.GL_ARRAY_BUFFER, element.mVertexBufferName);            
			mGl.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);         
		}
			
		//mGl.glDisableClientState(GL11.GL_NORMAL_ARRAY); 
		
		//mGl.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY); 
		
		mGl.glDisableClientState(GL11.GL_COLOR_ARRAY);  	
			
		//mGl.glDisable(GL11.GL_TEXTURE_2D);
		
		mGl.glDisable(GL11.GL_LIGHTING);
	
		mGl.glScalef(0.95f, 0.95f, 0.95f);
		//mGl.glColor4ub(element.mPickingColor[0],element.mPickingColor[1], element.mPickingColor[2], (byte)255);
		if (element.mIndiceBufferName != 0)
		{		
			mGl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, element.mIndiceBufferName);             
			
			mGl.glDrawElements(GL11.GL_TRIANGLES, element.mIndiceCount,GL11.GL_UNSIGNED_SHORT, 0);
		}	
		
		mGl.glDisableClientState(GL11.GL_NORMAL_ARRAY); 
		
		mGl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);             
		mGl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0); 	
		mGl.glBindTexture(GL11.GL_TEXTURE_2D, 0);	
		mGl.glEnable(GL11.GL_TEXTURE_2D);			
		
		/*
		if (element.mIsBillboard)
		{
			this.billboardEnd();
		}		
		*/
		mGl.glPopMatrix();		
		
		//mGl.glEnable(GL11.GL_TEXTURE_2D);
	}	

	
	public void renderRenderElementPickable(Sd3dRendererElement element)
	{
		//OpenGL stuffs	
		mGl.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		
		mGl.glPushMatrix();
	
		if (element.mPosition != null)
		{
			mGl.glTranslatef(element.mPosition[0], element.mPosition[1], element.mPosition[2]);
		}
	
		if (element.mOrientation != null)
		{
			mGl.glRotatef(-element.mOrientation[0], 1, 0, 0);
			mGl.glRotatef(-element.mOrientation[1], 0, 1, 0);
			mGl.glRotatef(-element.mOrientation[2], 0, 0, 1);		
		}
				
		if (element.mIsBillboard)
		{
			this.billboardCheatSphericalBegin();
		}
        // draw using hardware buffers             
		if (element.mVertexBufferName != 0)
		{
			mGl.glBindBuffer(GL11.GL_ARRAY_BUFFER, element.mVertexBufferName);            
			mGl.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);         
		}
			
		mGl.glDisableClientState(GL11.GL_NORMAL_ARRAY); 
		
		mGl.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY); 
		
		mGl.glDisableClientState(GL11.GL_COLOR_ARRAY);  	
			
		mGl.glDisable(GL11.GL_TEXTURE_2D);
		
		mGl.glDisable(GL11.GL_LIGHTING);
		
		mGl.glColor4ub(element.mPickingColor[0],element.mPickingColor[1], element.mPickingColor[2], (byte)255);
		if (element.mIndiceBufferName != 0)
		{		
			mGl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, element.mIndiceBufferName);             
			mGl.glDrawElements(GL11.GL_TRIANGLES, element.mIndiceCount,GL11.GL_UNSIGNED_SHORT, 0);
		}	
		
		mGl.glDisableClientState(GL11.GL_NORMAL_ARRAY); 
		mGl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);             
		mGl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0); 	
		mGl.glBindTexture(GL11.GL_TEXTURE_2D, 0);	
		mGl.glEnable(GL11.GL_TEXTURE_2D);			
		
		if (element.mIsBillboard)
		{
			this.billboardEnd();
		}		
		
		mGl.glPopMatrix();		
	}
	
	
	public void billboardCheatSphericalBegin()
	{
		float[] modelview = new float[16];
		int i,j;

		// save the current modelview matrix
		mGl.glPushMatrix();

		// get the current modelview matrix
		mGl.glGetFloatv(GL11.GL_MODELVIEW_MATRIX , modelview,0);

		// undo all rotations
		// beware all scaling is lost as well 
		for( i=0; i<3; i++ ) 
		    for( j=0; j<3; j++ ) {
			if ( i==j )
			    modelview[i*4+j] = 1.0f;
			else
			    modelview[i*4+j] = 0.0f;
		    }

		// set the modelview with no rotations
		mGl.glLoadMatrixf(modelview,0);		
	}
	
	public void billboardEnd()
	{
		// restore the previously 
		// stored modelview matrix
		mGl.glPopMatrix();		
	}
	
	public void renderRenderListShadowVolume()
	{
		for (int i = 0; i < mCountRenderElement;i++)
		{
			if (mRenderList[i].mIsShadowVolume)
			  renderRenderElementShadowVolume(mRenderList[i]);
		}			
	}
	
	
	public void renderRenderListPickable()
	{
		for (int i = 0; i < mCountRenderElement;i++)
		{
			if (mRenderList[i].mIsPickable)
			  renderRenderElementPickable(mRenderList[i]);
		}		
	}
	
	public void renderRenderInScreenSpaceList()
	{
		for (int i = 0; i < mCountRenderElement;i++)
		{
			if ((!mRenderList[i].mIsShadowVolume)&&(mRenderList[i].mIsInScreenSpace))
			{
			  //Log.d("renderRenderInScreenSpaceList()","RENDER SOMETHING "+mRenderList[i].mTextureName);
			  renderRenderElement(mRenderList[i]);
			}
			  //if (mRenderList[i].mIsPickable)
			    //renderRenderElementPickable(mRenderList[i]);
		}		
	}
	
	public void renderRenderList()
	{
		for (int i = 0; i < mCountRenderElement;i++)
		{
			if ((!mRenderList[i].mIsShadowVolume)&&(!mRenderList[i].mIsInScreenSpace))
			  renderRenderElement(mRenderList[i]);
			  //if (mRenderList[i].mIsPickable)
			    //renderRenderElementPickable(mRenderList[i]);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see org.nicolasmy.sd3d.gfx.Sd3dRendererInterface#renderShadowVolumeScene(org.nicolasmy.sd3d.gfx.Sd3dScene)
	 */
	public void renderShadowVolumeScene(Sd3dScene scene)
	{
		IntBuffer buffer = IntBuffer.allocate(1);
		mGl.glGetIntegerv(GL11.GL_STENCIL_BITS, buffer);
		if (buffer.get() == 8)
		{
			mCountRenderElement = 0;

			for (int i = 0;i < scene.mCountObject;i++)
			{
				this.addObjectToRenderList(scene.mObjectList[i]);
			}



			/*
			 * Now we're ready to draw some 3D objects
			 */

			this.setupShadowVolumeStep1();

			mGl.glMatrixMode(GL11.GL_MODELVIEW);

			mGl.glLoadIdentity();
			/////////


			float rot[] = scene.getCamera().getOrientation();       
			mGl.glRotatef(rot[0], 1, 0, 0);
			mGl.glRotatef(rot[1], 0, 1, 0);
			mGl.glRotatef(rot[2], 0, 0, 1);	        

			float pos[] = scene.getCamera().getPosition();
			mGl.glTranslatef(-pos[0], -pos[1], -pos[2]);

			this.setupShadowVolumeStep2();
			this.renderRenderListShadowVolume();

			this.setupShadowVolumeStep3();
			this.renderRenderListShadowVolume();        

			this.setupShadowVolumeStep4();

			drawShadowVolumeQuad();

			this.setupShadowVolumeStep5();
		}
	}	
	
	public void renderPickableScene(Sd3dScene scene)
	{
		mCountRenderElement = 0;
		
		for (int i = 0;i < scene.mCountObject;i++)
		{
			this.addObjectToRenderList(scene.mObjectList[i]);
		}
		
		
        mGl.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        mGl.glFrontFace(GL11.GL_CW);
        /*
         * Now we're ready to draw some 3D objects
         */

        mGl.glMatrixMode(GL11.GL_MODELVIEW);

        mGl.glLoadIdentity();
        /////////
        
        //mGl.glTranslatef(0, 0, -3.0f);
        
        float rot[] = scene.getCamera().getOrientation();       
		mGl.glRotatef(rot[0], 1, 0, 0);
		mGl.glRotatef(rot[1], 0, 1, 0);
		mGl.glRotatef(rot[2], 0, 0, 1);	        
        
        float pos[] = scene.getCamera().getPosition();
        mGl.glTranslatef(-pos[0], -pos[1], -pos[2]);
      
    
        
        //mGl.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);       
        //mGl.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        //mGl.glEnableClientState(GL11.GL_COLOR_ARRAY);        
        this.renderRenderListPickable();
        

        
        //Font rendering
        //mGl.glDisable(GL11.GL_DEPTH_TEST);
        //mGl.glLoadIdentity();
        //mGl.glOrthof(0, 800, 480, 0, -1, 1);
        //this.mBmpFont.mGl = mGl;
        //this.mBmpFont.renderText();
        //mGl.glEnable(GL11.GL_DEPTH_TEST);
			
        //this.mBmpFont.resetTextBuffer();
		
	}
	
	
	/* (non-Javadoc)
	 * @see org.nicolasmy.sd3d.gfx.Sd3dRendererInterface#renderScene(org.nicolasmy.sd3d.gfx.Sd3dScene)
	 */
	public void renderScene(Sd3dScene scene)
	{
	
		if (GLES20.glGetError() != GLES20.GL_NO_ERROR)
		{
			throw new RuntimeException("renderScene() ENTERING: ERROR ON OPENGL ES CALL");
		}
		
		mCountRenderElement = 0;
		
		for (int i = 0;i < scene.mCountObject;i++)
		{
			this.addObjectToRenderList(scene.mObjectList[i]);
		}

        mGl.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        mGl.glFrontFace(GL11.GL_CW);
        mGl.glCullFace(GL11.GL_BACK);
        /*
         * Now we're ready to draw some 3D objects
         */

        mGl.glMatrixMode(GL11.GL_MODELVIEW);

        /////////
		      
        //mGl.glTranslatef(0, 0, -3.0f);
		mGl.glLoadIdentity();
		
		if (scene.getCamera().getRotationMatrix() == null)
		{
			float rot[] = scene.getCamera().getOrientation();       
			mGl.glRotatef(rot[0], 1, 0, 0);
			mGl.glRotatef(rot[1], 0, 1, 0);
			mGl.glRotatef(rot[2], 0, 0, 1);	        
		}
		else
		{
			mGl.glLoadMatrixf(scene.getCamera().getRotationMatrix(), 0);
			GLU.gluLookAt(mGl, 0, 0, 2, 0, 0, 0, 0, 1, 0);
		}
			
        float pos[] = scene.getCamera().getPosition();
        mGl.glTranslatef(-pos[0], -pos[1], -pos[2]);
      
        //this.setupLightVector(0.f, 0.f, 0.f); 
        updateFrustumFaster();
        //mGl.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);       
        mGl.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        //mGl.glEnableClientState(GL11.GL_COLOR_ARRAY);        
        
        this.renderRenderList();
        

        
        //Font rendering
        mGl.glMatrixMode(GL11.GL_PROJECTION);
        mGl.glPushMatrix();
        mGl.glDisable(GL11.GL_DEPTH_TEST);
        mGl.glMatrixMode(GL11.GL_PROJECTION);
        mGl.glLoadIdentity();
        
        mGl.glOrthof(0, this.screenWidth, this.screenHeight, 0, -1, 1);
        mGl.glMatrixMode(GL11.GL_MODELVIEW);
        mGl.glLoadIdentity();
        this.renderRenderInScreenSpaceList();
        this.mBmpFont.mGl = mGl;
        this.mBmpFont.renderText();
        
        mGl.glEnable(GL11.GL_DEPTH_TEST);
        mGl.glMatrixMode(GL11.GL_PROJECTION);
        mGl.glPopMatrix();		
        this.mBmpFont.resetTextBuffer();

        
	}
	
	public void updateScreenResolution(int width,int height)
	{
		Log.d("updateScreenResolution()","RECEIVE HEIGHT="+height+" WIDTH="+width);
		this.screenHeight = height;
		this.screenWidth = width;		
		mBmpFont.setScreenProperties(width, height);
	}
	
    public Sd3dRenderer(boolean useTranslucentBackground,int maxRenderElement,int width,int height) {
        mTranslucentBackground = useTranslucentBackground;      
		this.mMaxRenderElement = maxRenderElement;
		
		this.screenHeight = height;
		this.screenWidth = width;
		mRenderList = new Sd3dRendererElement[mMaxRenderElement];
		
		mBmpFont = new Sd3dBmpFont("font.png",null);
		mBmpFont.setScreenProperties(width, height);
		mLightVector = new float[3];
    }		
	
    public int[] getConfigSpec() {
        // 32bpp + 16bit Z-buffer + 8bit stencil buffer
        int[] configSpec = {
                EGL11.EGL_RED_SIZE,      5,
                EGL11.EGL_GREEN_SIZE,    6,
                EGL11.EGL_BLUE_SIZE,     5,
                //EGL11.EGL_ALPHA_SIZE,    8,                		
        		EGL11.EGL_DEPTH_SIZE,   16,
        		EGL11.EGL_STENCIL_SIZE, 8,
        		EGL11.EGL_NONE
        };
        return configSpec;
    }

    public void setupShadowVolumeStep1()
    {
    	//Do not test against depth
    	mGl.glDepthMask(false);
    	
    	//Do not write in the color buffer
    	mGl.glColorMask(false, false, false, false);
    	
    	//Enable stencil buffer
    	mGl.glEnable(GL11.GL_STENCIL_TEST);
    	
    	mGl.glClear(GL11.GL_STENCIL_BUFFER_BIT);
    	
    	mGl.glStencilFunc(GL11.GL_ALWAYS, 0, ~0);	
    }

    public void setupShadowVolumeStep2()
    {
    	mGl.glFrontFace(GL11.GL_CW);
    	
    	mGl.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_INCR);
    }    
    
    public void setupShadowVolumeStep3()
    {
    	mGl.glFrontFace(GL11.GL_CCW);
    	
    	mGl.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_DECR);
    }       
    
    public void setupShadowVolumeStep4()
    {
    	mGl.glColorMask(true, true, true, true);
    	
    	mGl.glFrontFace(GL11.GL_CW);
    	
    	mGl.glStencilFunc(GL11.GL_NOTEQUAL, 0, ~0);
    	//mGl.glStencilFunc(GL11.GL_ALWAYS, 0, ~0);
    	
    	//glStencilFunc(GL_GREATER, 0, ~0);
    	mGl.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);    	
    }
    
    public void setupShadowVolumeStep5()
    {
    	mGl.glDepthMask(true);    	
    	
    	//Enable stencil buffer
    	mGl.glDisable(GL11.GL_STENCIL_TEST);
    }
        
    
    /* (non-Javadoc)
	 * @see org.nicolasmy.sd3d.gfx.Sd3dRendererInterface#sizeChanged(javax.microedition.khronos.opengles.GL11, int, int)
	 */
	public void sizeChanged(GL11 gl, int width, int height) {
         gl.glViewport(0, 0, width, height);

         /*
          * Set our projection matrix. This doesn't have to be done
          * each time we draw, but usually a new projection needs to
          * be set when the viewport is resized.
          */

         float ratio = (float) width / height;
         gl.glMatrixMode(GL11.GL_PROJECTION);
         gl.glLoadIdentity();
         gl.glFrustumf(-ratio, ratio, -1, 1, 1.5f, 1000);
         
         //this.updateScreenResolution(width, height);
    }

    /* (non-Javadoc)
	 * @see org.nicolasmy.sd3d.gfx.Sd3dRendererInterface#surfaceCreated(javax.microedition.khronos.opengles.GL11)
	 */
	public void surfaceCreated(GL11 gl) {
        /*
         * By default, OpenGL enables features that improve quality
         * but reduce performance. One might want to tweak that
         * especially on software renderer.
         */
        //gl.glDisable(GL11.GL_DITHER);

        /*
         * Some one-time OpenGL initialization can be made here
         * probably based on features of this particular context
         */
         gl.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT,
        		 GL11.GL_FASTEST);

         if (mTranslucentBackground) {
             gl.glClearColor(0,0,0,0);
         } else {
             gl.glClearColor(1,1,1,1);
             //gl.glClearColor(0,0,0,0);
         }
         
         gl.glEnable(GL11.GL_TEXTURE_2D);
         
         gl.glEnable(GL11.GL_CULL_FACE);
         
         //gl.glShadeModel(GL11.GL_FLAT);
         gl.glEnable(GL11.GL_DEPTH_TEST);
         //gl.glDisable(GL11.GL_AUTO_NORMAL);
    }	
    
    public void setupLightVector(float x,float y,float z)
    {
    	 float mat_specular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
    	 //float mat_shininess[] = { 50.0f };
    	  //mGl.glLoadIdentity();
 	 
         float noAmbient[] = {0.5f, 0.5f, 0.5f, 1.0f};  	
         float whiteDiffuse[] = {10.0f, 10.0f, -10.0f, 1.0f};
    	 //float light_position[] = { x, y, z, 0.0f };   	
    	 float light_position[] = { 10.f, -10.f, 10.f, 1.0f };   
    	 mGl.glShadeModel (GL11.GL_SMOOTH);
    	 
    	 mGl.glLightModelfv(GL11.GL_LIGHT_MODEL_AMBIENT,noAmbient,0); 
    	 //mGl.glLightModelf(GL11.GL_LIGHT_MODEL_TWO_SIDE,1.f); 
    	 mGl.glMaterialfv(GL11.GL_FRONT, GL11.GL_SPECULAR, mat_specular,0);
    	 
    	 //mGl.glLightfv(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, whiteDiffuse,0);
    	 //mGl.glMaterialfv(GL11.GL_FRONT, GL11.GL_SHININESS, mat_shininess,0);
    	 mGl.glMaterialfv(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE, noAmbient,0);
    	 mGl.glLightfv(GL11.GL_LIGHT0, GL11.GL_AMBIENT, noAmbient,0);
    	 mGl.glLightfv(GL11.GL_LIGHT0, GL11.GL_POSITION, light_position,0);   

    	 
    	 //mGl.glEnable(GL11.GL_NORMALIZE);
    	 
       	 mGl.glEnable(GL11.GL_LIGHTING);
       	 mGl.glEnable(GL11.GL_LIGHT0);  
       	//mGl.glEnable(GL11.GL_COLOR_MATERIAL);
    }
    
    public Sd3dObject getObjectFromPickingColor(byte r,byte g,byte b)
    {		
    	for (int i = 0; i < mCountRenderElement;i++)
    	{
    		if (mRenderList[i].mIsPickable)
    		{
    			if (mRenderList[i].mPickingColor != null)
    				if (mRenderList[i].mPickingColor[0] == r)
        				if (mRenderList[i].mPickingColor[1] == g)
            				if (mRenderList[i].mPickingColor[2] == b)
            				{
            					
            					return mRenderList[i].mObject;
            				}
    		}
    	}
    	return null;
    }
    
    
    private byte pickColorOffset = 1;
    public void generatePickingColor(Sd3dRendererElement element)
    {
    	element.mPickingColor = new byte[3];
    	pickColorOffset += 1;
    	element.mPickingColor[0] = pickColorOffset;
    	element.mPickingColor[1] = 0;    	
    	element.mPickingColor[2] = 0;      	
    }
    
    public Sd3dObject pickObject(int x,int y)
    {
    	java.nio.ByteBuffer bb = java.nio.ByteBuffer.allocate(4);
    	mGl.glGetError();
    	mGl.glReadPixels(x,screenHeight - y - top, 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, bb);
    	//mGl.glReadPixels(0,0, 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, bb);
    	/*
    	float r = bb.getFloat(0);
    	float g = bb.getFloat(4);    	
    	float b = bb.getFloat(8);
    	
    	int ri = bb.getInt(0);
    	int gi = bb.getInt(4); 
    	int bi = bb.getInt(8);
    	*/
    	byte r = bb.get(0);
    	byte g = bb.get(1);
    	byte b = bb.get(2); 
    	Log.d("TOTO","SCREEN HEIGHT="+screenHeight+" Y="+y);
    	Log.d("TOTO", "TOP="+this.getTop()+" COLOR="+r+" "+g+" "+b + "ERROR: "+mGl.glGetError());
    	return getObjectFromPickingColor(r,g,b);
    	
    }

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}
	
    private float clip[] = new float[16];
    private float proj[] = new float[16];
    private float modl[] = new float[16];
    private int[] mViewport = new int[16];	
	private void updateFrustumFaster()
	{

	    float t;
	 
	    // Get The Current PROJECTION Matrix From OpenGL
	    mGl.glGetFloatv( GL11.GL_PROJECTION_MATRIX, proj,0 );
	 
	    // Get The Current MODELVIEW Matrix From OpenGL
	    mGl.glGetFloatv( GL11.GL_MODELVIEW_MATRIX, modl,0 );
	    
	    
	    mGl.glGetIntegerv( GL11.GL_VIEWPORT, mViewport,0 );
	   
	    // Combine The Two Matrices (Multiply Projection By Modelview)
	    // But Keep In Mind This Function Will Only Work If You Do NOT
	    // Rotate Or Translate Your Projection Matrix
	    clip[ 0] = modl[ 0] * proj[ 0];
	    clip[ 1] = modl[ 1] * proj[ 5];
	    clip[ 2] = modl[ 2] * proj[10] + modl[ 3] * proj[14];
	    clip[ 3] = modl[ 2] * proj[11];
	 
	    clip[ 4] = modl[ 4] * proj[ 0];
	    clip[ 5] = modl[ 5] * proj[ 5];
	    clip[ 6] = modl[ 6] * proj[10] + modl[ 7] * proj[14];
	    clip[ 7] = modl[ 6] * proj[11];
	 
	    clip[ 8] = modl[ 8] * proj[ 0];
	    clip[ 9] = modl[ 9] * proj[ 5];
	    clip[10] = modl[10] * proj[10] + modl[11] * proj[14];
	    clip[11] = modl[10] * proj[11];
	 
	    clip[12] = modl[12] * proj[ 0];
	    clip[13] = modl[13] * proj[ 5];
	    clip[14] = modl[14] * proj[10] + modl[15] * proj[14];
	    clip[15] = modl[14] * proj[11];
	 
	    // Extract The Numbers For The RIGHT Plane
	    m_Frustum[0][0] = clip[ 3] - clip[ 0];
	    m_Frustum[0][1] = clip[ 7] - clip[ 4];
	    m_Frustum[0][2] = clip[11] - clip[ 8];
	    m_Frustum[0][3] = clip[15] - clip[12];
	 
	    // Normalize The Result
	    t = (float) (Math.sqrt( m_Frustum[0][0] * m_Frustum[0][0] + m_Frustum[0][1] * m_Frustum[0][1] + m_Frustum[0][2] * m_Frustum[0][2] ));
	    m_Frustum[0][0] /= t;
	    m_Frustum[0][1] /= t;
	    m_Frustum[0][2] /= t;
	    m_Frustum[0][3] /= t;
	 
	    // Extract The Numbers For The LEFT Plane
	    m_Frustum[1][0] = clip[ 3] + clip[ 0];
	    m_Frustum[1][1] = clip[ 7] + clip[ 4];
	    m_Frustum[1][2] = clip[11] + clip[ 8];
	    m_Frustum[1][3] = clip[15] + clip[12];
	 
	    // Normalize The Result
	    t = (float)(Math.sqrt( m_Frustum[1][0] * m_Frustum[1][0] + m_Frustum[1][1] * m_Frustum[1][1] + m_Frustum[1][2] * m_Frustum[1][2] ));
	    m_Frustum[1][0] /= t;
	    m_Frustum[1][1] /= t;
	    m_Frustum[1][2] /= t;
	    m_Frustum[1][3] /= t;
	 
	    // Extract The BOTTOM Plane
	    m_Frustum[2][0] = clip[ 3] + clip[ 1];
	    m_Frustum[2][1] = clip[ 7] + clip[ 5];
	    m_Frustum[2][2] = clip[11] + clip[ 9];
	    m_Frustum[2][3] = clip[15] + clip[13];
	 
	    // Normalize The Result
	    t = (float)(Math.sqrt( m_Frustum[2][0] * m_Frustum[2][0] + m_Frustum[2][1] * m_Frustum[2][1] + m_Frustum[2][2] * m_Frustum[2][2] ));
	    m_Frustum[2][0] /= t;
	    m_Frustum[2][1] /= t;
	    m_Frustum[2][2] /= t;
	    m_Frustum[2][3] /= t;
	 
	    // Extract The TOP Plane
	    m_Frustum[3][0] = clip[ 3] - clip[ 1];
	    m_Frustum[3][1] = clip[ 7] - clip[ 5];
	    m_Frustum[3][2] = clip[11] - clip[ 9];
	    m_Frustum[3][3] = clip[15] - clip[13];
	 
	    // Normalize The Result
	    t = (float)(Math.sqrt( m_Frustum[3][0] * m_Frustum[3][0] + m_Frustum[3][1] * m_Frustum[3][1] + m_Frustum[3][2] * m_Frustum[3][2] ));
	    m_Frustum[3][0] /= t;
	    m_Frustum[3][1] /= t;
	    m_Frustum[3][2] /= t;
	    m_Frustum[3][3] /= t;
	 
	    // Extract The FAR Plane
	    m_Frustum[4][0] = clip[ 3] - clip[ 2];
	    m_Frustum[4][1] = clip[ 7] - clip[ 6];
	    m_Frustum[4][2] = clip[11] - clip[10];
	    m_Frustum[4][3] = clip[15] - clip[14];
	 
	    // Normalize The Result
	    t = (float)(Math.sqrt( m_Frustum[4][0] * m_Frustum[4][0] + m_Frustum[4][1] * m_Frustum[4][1] + m_Frustum[4][2] * m_Frustum[4][2] ));
	    m_Frustum[4][0] /= t;
	    m_Frustum[4][1] /= t;
	    m_Frustum[4][2] /= t;
	    m_Frustum[4][3] /= t;
	 
	    // Extract The NEAR Plane
	    m_Frustum[5][0] = clip[ 3] + clip[ 2];
	    m_Frustum[5][1] = clip[ 7] + clip[ 6];
	    m_Frustum[5][2] = clip[11] + clip[10];
	    m_Frustum[5][3] = clip[15] + clip[14];
	 
	    // Normalize The Result
	    t = (float)(Math.sqrt( m_Frustum[5][0] * m_Frustum[5][0] + m_Frustum[5][1] * m_Frustum[5][1] + m_Frustum[5][2] * m_Frustum[5][2] ));
	    m_Frustum[5][0] /= t;
	    m_Frustum[5][1] /= t;
	    m_Frustum[5][2] /= t;
	    m_Frustum[5][3] /= t;
	    
	}	
	
	public boolean pointInFrustum(float x, float y, float z)
	{
	    int i;
	    // The Idea Behind This Algorithum Is That If The Point
	    // Is Inside All 6 Clipping Planes Then It Is Inside Our
	    // Viewing Volume So We Can Return True.
	    for(i = 0; i < 6; i++)
	    {
	        if(m_Frustum[i][0] * x + m_Frustum[i][1] * y + m_Frustum[i][2] * z + m_Frustum[i][3] <= 0)
	        {
	            return(false);
	        }
	    }
	    return(true);
	}	
	
	public void pointToScreen(float x,float y,float z,float res[])
	{
		GLU.gluProject(x, y, z, this.modl, 0, this.proj, 0, this.mViewport, 0, res, 0);
	}
	
	public void displayText(String text,Sd3dRenderer.ALIGN halign, Sd3dRenderer.ALIGN valign, float size)
	{
		this.mBmpFont.addTextToBuffer(text, halign, valign, size);
	}
	
	public void invalidateRendererElements()
	{
		this.mBmpFont.mTextureName = 0;
	}	
	
	public int getScreenWidth() {
		return screenWidth;
	}


	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}


	public int getScreenHeight() {
		return screenHeight;
	}


	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}


	@Override
	public Sd3dObject pickAt(int x, int y, Sd3dScene scene) {
		// TODO Auto-generated method stub
		return null;
	}
}
