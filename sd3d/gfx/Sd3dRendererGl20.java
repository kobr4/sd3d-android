package org.nicolasmy.sd3d.gfx;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.opengles.GL11;

import org.nicolasmy.sd3d.GameHolder;


import android.opengl.GLES20;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.util.Log;



public class Sd3dRendererGl20 implements Sd3dRendererInterface
{
	public enum ALIGN
	{
		TOP,
		BOTTOM,
		LEFT,
		RIGHT,
		CENTER
	}
	
	private String vertexShader;
	private String fragmentShader;
	
	private final int vertexPositionHandle = 1;
	private final int vertexColorHandle = 2;
	private final int vertexNormalHandle = 3;
	private final int vertexTexCoordHandle = 4;
	
	private int mDefaultVertexShaderHandle = 0;
	private int mDefaultFragmentShaderHandle = 0;
	private int mDefaultShaderProgramHandle = 0;
	
	/** This will be used to pass in the transformation matrix. */
	private int mMVPMatrixHandle;
	 
	/** This will be used to pass in the transformation matrix. */
	private int mMVMatrixHandle;	

	/** This will be used to pass in the transformation matrix. */
	private int mNormalMatrixHandle;		
	
	private int mColorVectorHandle;
	
	/** This will be used to pass in model position information. */
	//private int mPositionHandle;
	
	/** This will be used to pass in model position information. */
	//private int mTexCoordsHandle;
	 	
	/** This will be used to pass in model color information. */
	//private int mColorHandle;

	/** This will be used to pass in model color information. */
	//private int mNormalHandle;	
	
	private int mRenderStateVectorHandle;
	
    /**
     * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
     * of being located at the center of the universe) to world space.
     */
    private float[] mModelMatrix = new float[16];	
    
    private float[] mNormalMatrix = new float[16];    
	
    /**
     * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
     * it positions things relative to our eye.
     */
    private float[] mViewMatrix = new float[16];    
    
    private float[] mProjectionMatrix = new float[16];  
    
    private float[] mProjectionOrthoMatrix = new float[16];  
        
    /** Allocate storage for the final combined matrix. This will be passed into the shader program. */
    private float[] mMVPMatrix = new float[16];    
    
    private float[] mMVMatrix = new float[16];
    
    private FloatBuffer mColorVector = FloatBuffer.allocate(4);
    
    private FloatBuffer mCamPos = FloatBuffer.allocate(4);
    
    //0 : Texture flag
    //1 : Color flag (0 : none;1 : color attrib;2 : color)
    //2 : Render light flag
    private IntBuffer mRenderStateVector = IntBuffer.allocate(4);
    
    //private boolean mTranslucentBackground;
	private int screenWidth;
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

	private int screenHeight;
	private int top;
	private float m_Frustum[][] = {new float[4],new float[4],new float[4],new float[4],new float[4],new float[4]};
	
   
	private Sd3dRendererElement mRenderList[];
	private int mMaxRenderElement;
	private int mCountRenderElement;
	public Sd3dBmpFont mBmpFont;
	public float mLightVector[];
	
	
	public void setGL11Context(GL11 gl)
	{
	}
	
	
	public void regiserMesh(Sd3dRendererElement element, Sd3dMesh mesh)
	{
		if (GLES20.glGetError() != GLES20.GL_NO_ERROR)
		{
			//throw new RuntimeException("regiserMesh() ENTERING: ERROR ON OPENGL ES CALL");
		}			
		
		//OpenGL stuffs
		IntBuffer buffer = IntBuffer.allocate(1);
		
		if (mesh.mVertices != null)
		{
			GLES20.glGenBuffers(1,buffer);
			GLES20.glBindBuffer(GL11.GL_ARRAY_BUFFER, buffer.get(0));
			GLES20.glBufferData(GL11.GL_ARRAY_BUFFER,mesh.mVertices.capacity() * 4,mesh.mVertices,GL11.GL_STATIC_DRAW);
			element.mVertexBufferName = buffer.get(0);
		} else element.mVertexBufferName = 0;
		
		if (mesh.mNormals != null)
		{
			GLES20.glGenBuffers(1,buffer);
			GLES20.glBindBuffer(GL11.GL_ARRAY_BUFFER, buffer.get(0));
			GLES20.glBufferData(GL11.GL_ARRAY_BUFFER,mesh.mNormals.capacity() * 4,mesh.mNormals,GL11.GL_STATIC_DRAW);
			element.mNormalBufferName = buffer.get(0);
		} else element.mNormalBufferName = 0;			
		
		if (mesh.mIndices != null)
		{
			GLES20.glGenBuffers(1,buffer);
			GLES20.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, buffer.get(0));
			GLES20.glBufferData(GL11.GL_ELEMENT_ARRAY_BUFFER,mesh.mIndices.capacity()*2,mesh.mIndices,GL11.GL_STATIC_DRAW);
			element.mIndiceBufferName = buffer.get(0);
			element.mIndiceCount = mesh.mIndices.capacity();
		} else element.mIndiceBufferName = 0;
		
		if (mesh.mTexCoords != null)
		{
			GLES20.glGenBuffers(1,buffer);
			GLES20.glBindBuffer(GL11.GL_ARRAY_BUFFER, buffer.get(0));
			GLES20.glBufferData(GL11.GL_ARRAY_BUFFER,mesh.mTexCoords.capacity() * 4,mesh.mTexCoords,GL11.GL_STATIC_DRAW);
			element.mTexCoordBufferName = buffer.get(0);
		} else element.mTexCoordBufferName = 0;		
		
		if (GLES20.glGetError() != GLES20.GL_NO_ERROR)
		{
			throw new RuntimeException("regiserMesh() EXITING: ERROR ON OPENGL ES CALL");
		}		
		
		element.mIsShadowVolume = mesh.mIsShadowVolume;
		element.mIsBillboard = mesh.mIsBillboard;
		element.mIsInScreenSpace = mesh.mIsInScreenSpace;
	}	
	
	public int registerMaterial(Sd3dRendererElement element, Sd3dMaterial material)
	{
		if (GLES20.glGetError() != GLES20.GL_NO_ERROR)
		{
			throw new RuntimeException("registerMaterial() ENTERING: ERROR ON OPENGL ES CALL");
		}				
		
		//OpenGL stuffs	
		IntBuffer buffer = IntBuffer.allocate(1);		
		
		if (material.mColors != null)
		{
			GLES20.glGenBuffers(1,buffer);
			
			
			GLES20.glBindBuffer(GL11.GL_ARRAY_BUFFER, buffer.get(0));
			GLES20.glBufferData(GL11.GL_ARRAY_BUFFER,material.mColors.capacity() * 4,material.mColors,GL11.GL_STATIC_DRAW);
			//mColorBufferName = buffer.get(0);
			//material.mColorName = mColorBufferName;
			material.mColorName =  buffer.get(0);
		//} else mColorBufferName = 0;	
		} else material.mColorName = 0;
	
		if (material.mTextureData != null)
		{
			GLES20.glGenTextures(1, buffer); 
			GLES20.glBindTexture(GL11.GL_TEXTURE_2D, buffer.get(0)); 
			//GLES20.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
			GLES20.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA,material.mWidth, material.mHeight, 0, 
					GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, material.mTextureData);
			GLES20.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST); 
			GLES20.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST); 
			
			
			element.mTextureName = buffer.get(0);
		}
		
		if (GLES20.glGetError() != GLES20.GL_NO_ERROR)
		{
			throw new RuntimeException("registerMaterial() EXITING: ERROR ON OPENGL ES CALL");
		}		
		
		return 0;
	}	
	
	private void initShaderAttributes()
	{
		if (mDefaultShaderProgramHandle != 0)
		{	
			// Set program handles. These will later be used to pass in values to the program.
		    mMVPMatrixHandle = GLES20.glGetUniformLocation(mDefaultShaderProgramHandle, "u_MVPMatrix");
		 
			if (GLES20.glGetError() != GLES20.GL_NO_ERROR)
			{
				throw new RuntimeException("initShaderAttributes() AFTER CALL 1: ERROR ON OPENGL ES CALL ");
			}	
		    
		    mMVMatrixHandle = GLES20.glGetUniformLocation(mDefaultShaderProgramHandle, "u_MVMatrix");	    
		    
		    mNormalMatrixHandle = GLES20.glGetUniformLocation(mDefaultShaderProgramHandle, "u_NormalMatrix");	
		    
		    mRenderStateVectorHandle = GLES20.glGetUniformLocation(mDefaultShaderProgramHandle, "u_RenderStateVector");	
		    
		    mColorVectorHandle = GLES20.glGetUniformLocation(mDefaultShaderProgramHandle, "u_ColorVector");	
		    
			if (GLES20.glGetError() != GLES20.GL_NO_ERROR)
			{
				throw new RuntimeException("initShaderAttributes() AFTER CALL 1: ERROR ON OPENGL ES CALL ");
			}
			/*
		    mPositionHandle = GLES20.glGetAttribLocation(mDefaultShaderProgramHandle, "a_Position");
		    mColorHandle = GLES20.glGetAttribLocation(mDefaultShaderProgramHandle, "a_Color");
		    mNormalHandle = GLES20.glGetAttribLocation(mDefaultShaderProgramHandle, "a_Normal");
		    mTexCoordsHandle = GLES20.glGetAttribLocation(mDefaultShaderProgramHandle, "a_Texcoords");
	    	*/
			
	    	// Tell OpenGL to use this program when rendering.
	    	GLES20.glUseProgram(mDefaultShaderProgramHandle);
	    	
		}
	}
	
	
	private void initDefaultShaders()
	{
		if (mDefaultVertexShaderHandle == 0)
		{
			mDefaultVertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
			Log.d("initDefaultShaders()", "mDefaultVertexShaderHandle="+mDefaultVertexShaderHandle);
			if (mDefaultVertexShaderHandle != 0)
			{
				// Pass in the shader source.
				GLES20.glShaderSource(mDefaultVertexShaderHandle, vertexShader);
		 
				// Compile the shader.
				GLES20.glCompileShader(mDefaultVertexShaderHandle);
		 
			    // Get the compilation status.
			    final int[] compileStatus = new int[1];
			    GLES20.glGetShaderiv(mDefaultVertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
			 
			    // If the compilation failed, delete the shader.
			    if (compileStatus[0] == 0)
			    {
			    	Log.e("initDefaultShaders()", "Failed to compile vertex shader");
			    	Log.d("initDefaultShaders()",GLES20.glGetShaderInfoLog(mDefaultVertexShaderHandle));
			        GLES20.glDeleteShader(mDefaultVertexShaderHandle);
			        mDefaultVertexShaderHandle = 0;
			    }
			}
		 
			if (mDefaultVertexShaderHandle == 0)
			{
				
				throw new RuntimeException("Error creating vertex shader.");
			}	
		}
		
		if (mDefaultFragmentShaderHandle == 0)
		{
			mDefaultFragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
			Log.d("initDefaultShaders()", "mDefaultFragmentShaderHandle="+mDefaultFragmentShaderHandle);
			if (mDefaultFragmentShaderHandle != 0)
			{
				// Pass in the shader source.
				GLES20.glShaderSource(mDefaultFragmentShaderHandle, fragmentShader);
		 
				// Compile the shader.
				GLES20.glCompileShader(mDefaultFragmentShaderHandle);
		 
			    // Get the compilation status.
			    final int[] compileStatus = new int[1];
			    GLES20.glGetShaderiv(mDefaultFragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
			 
			    // If the compilation failed, delete the shader.
			    if (compileStatus[0] == 0)
			    {
			    	Log.e("initDefaultShaders()", "Failed to compile fragment shader");
			    	Log.d("initDefaultShaders()",GLES20.glGetShaderInfoLog(mDefaultFragmentShaderHandle));			    	
			        GLES20.glDeleteShader(mDefaultFragmentShaderHandle);
			        mDefaultFragmentShaderHandle = 0;
			    }
			}
		 
			if (mDefaultFragmentShaderHandle == 0)
			{
				throw new RuntimeException("Error creating fragment shader.");
			}	
		}
		
		if (mDefaultShaderProgramHandle == 0)
		{
			mDefaultShaderProgramHandle = GLES20.glCreateProgram();
			
		    // Bind the vertex shader to the program.
		    GLES20.glAttachShader(mDefaultShaderProgramHandle, mDefaultVertexShaderHandle);
		 
		    // Bind the fragment shader to the program.
		    GLES20.glAttachShader(mDefaultShaderProgramHandle, mDefaultFragmentShaderHandle);
		 
		    // Bind attributes
		    GLES20.glBindAttribLocation(mDefaultShaderProgramHandle, this.vertexPositionHandle, "a_Position");
		    GLES20.glBindAttribLocation(mDefaultShaderProgramHandle, this.vertexColorHandle, "a_Color");
		    GLES20.glBindAttribLocation(mDefaultShaderProgramHandle, this.vertexTexCoordHandle, "a_Texcoords");
		    GLES20.glBindAttribLocation(mDefaultShaderProgramHandle, this.vertexNormalHandle, "a_Normal");
		    
		    // Link the two shaders together into a program.
		    GLES20.glLinkProgram(mDefaultShaderProgramHandle);
		 
		    // Get the link status.
		    final int[] linkStatus = new int[1];
		    GLES20.glGetProgramiv(mDefaultShaderProgramHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
		 
		    // If the link failed, delete the program.
		    if (linkStatus[0] == 0)
		    {
		        GLES20.glDeleteProgram(mDefaultShaderProgramHandle);
		        mDefaultShaderProgramHandle = 0;
		    }
		    	    
		}
		
		if (mDefaultShaderProgramHandle == 0)
		{
		    throw new RuntimeException("Error creating program.");
		}			
	}
	
	public Sd3dRendererElement[] createRenderElement(Sd3dObject object)
	{
		Sd3dRendererElement element[] = new Sd3dRendererElement[object.mMesh.length];
		for (int i = 0; i < object.mMesh.length;i++)
		{
			if (object.mMesh[i] != null)
			{	
				element[i] = new Sd3dRendererElement();
				
				if (object.mMesh[i].mRendererElementInterface != null)
				{
					element[i].mRendererInterface = object.mMesh[i].mRendererElementInterface;
					element[i].mRendererInterface.register();
				}

					
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
				if (object.mRenderElement[i].mRendererInterface != null)
				{
					object.mRenderElement[i].mRendererInterface.unregister();
					object.mRenderElement[i].mRendererInterface = null;
				}
				
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
			GLES20.glDeleteBuffers(1, buffer, 0);
			material.mColorName = 0;
		}	
	}
	
	public void unregisterMaterial(Sd3dRendererElement element)
	{
		int buffer[] = new int[1];
		
		if (element.mTextureName != 0)
		{
			buffer[0] = element.mTextureName;
			GLES20.glDeleteBuffers(1, buffer, 0);
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
			GLES20.glDeleteBuffers(1, buffer, 0);
			element.mVertexBufferName = 0;
		}	
		
		if (element.mNormalBufferName != 0)
		{
			buffer[0] = element.mNormalBufferName;
			GLES20.glDeleteBuffers(1, buffer, 0);
			element.mNormalBufferName = 0;
		}	
		
		if (element.mIndiceBufferName != 0)
		{
			buffer[0] = element.mIndiceBufferName;
			GLES20.glDeleteBuffers(1, buffer, 0);
			element.mIndiceBufferName = 0;
		}		
		
		if (element.mTexCoordBufferName != 0)
		{
			buffer[0] = element.mTexCoordBufferName;
			GLES20.glDeleteBuffers(1, buffer, 0);
			element.mTexCoordBufferName = 0;
		}				
	}
	
	FloatBuffer vertexBuffer;
	private void drawShadowVolumeQuad()
	{		
		float vertices[] = {
				//0f, (float)this.screenHeight, 0f,        					  // V1 - first vertex (x,y,z)
				//(float)this.screenWidth, (float)this.screenHeight, 0f,        // V2 - second vertex
				0f,  0f,  0f,                                                 // V3 - third vertex		
				(float)this.screenWidth, (float)this.screenHeight, 0f,        // V2 - second vertex	
				0f, (float)this.screenHeight, 0f,        					  // V1 - first vertex (x,y,z)
				
				//(float)this.screenWidth, (float)this.screenHeight, 0f,        // V1 - first vertex (x,y,z)
				//(float)this.screenWidth, 0f,  0f,                             // V2 - second vertex
				0f,  0f,  0f,                                                  // V3 - third vertex			
				(float)this.screenWidth, 0f,  0f,                             // V2 - second vertex		
				(float)this.screenWidth, (float)this.screenHeight, 0f        // V1 - first vertex (x,y,z)				
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
		
		//OpenGL stuffs	
		Matrix.setIdentityM(mModelMatrix, 0);
		//Matrix.setIdentityM(mViewMatrix, 0);
		
		mRenderStateVector.put(2, 0);//NO LIGHT	
		mRenderStateVector.put(1, 2);//HAS COLOR UNIFORM
		mRenderStateVector.put(0, 0);//NO TEXTURE
		
		//Colors 
		mColorVector.put(0,0f);
		mColorVector.put(1,0f);
		mColorVector.put(2,0f);
		mColorVector.put(3,0.3f);
		
	    // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
	    // (which currently contains model * view).
	    //Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
	    //Matrix.multiplyMM(mMVMatrix, 0, mModelMatrix, 0,mViewMatrix, 0);
	 
	    // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
	    // (which now contains model * view * projection).
	    Matrix.multiplyMM(mMVPMatrix, 0, mProjectionOrthoMatrix, 0, mModelMatrix, 0);			
		
	    
	    GLES20.glUseProgram(this.mDefaultShaderProgramHandle);
	    

	    GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVMatrix, 0);
	    GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
	    GLES20.glUniform4iv(mRenderStateVectorHandle, 1, mRenderStateVector);		
	    GLES20.glUniform4fv(mColorVectorHandle, 1, mColorVector);	
	    
		
		GLES20.glEnable(GL11.GL_BLEND);
		GLES20.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GLES20.glVertexAttribPointer(this.vertexPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
		GLES20.glEnableVertexAttribArray(vertexPositionHandle);
		
		GLES20.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
		
		GLES20.glDisableVertexAttribArray(vertexPositionHandle);
		
		GLES20.glDisable(GL11.GL_BLEND);
		
	}
	
	
	private float matrix[] = new float[16];

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
	
	public void renderRenderElement(Sd3dRendererElement element)
	{	
		
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.setIdentityM(mNormalMatrix, 0);
		
		if (element.mRenderLight)
		{				
			mRenderStateVector.put(2, 1);//LIGHT	
		}		
		{
			mRenderStateVector.put(2, 0);//NO LIGHT	
		}

		if (!element.mIsShadowVolume)
		if (element.mOrientation != null)
		{
			Sd3dRendererGl20.setRotateEulerM(mNormalMatrix, 0, element.mOrientation[0], element.mOrientation[1], element.mOrientation[2]);
			Matrix.multiplyMM(mModelMatrix, 0, mNormalMatrix, 0, mModelMatrix,0);
		}
		
		if (element.mPosition != null)
		{
			Matrix.setIdentityM(matrix, 0);
			Matrix.translateM(matrix, 0, element.mPosition[0], element.mPosition[1], element.mPosition[2]);		
			Matrix.multiplyMM(mModelMatrix, 0, matrix, 0, mModelMatrix,0);
		}			
		
				
	
		if (element.mIsBillboard)
		{
			this.billboardCheatSphericalBegin();
		}
		
        // draw using hardware buffers             
		if (element.mVertexBufferName != 0)
		{
			GLES20.glBindBuffer(GL11.GL_ARRAY_BUFFER, element.mVertexBufferName);    
			//GLES20.glVertexAttribPointer(this.mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, 0);
			GLES20.glVertexAttribPointer(this.vertexPositionHandle, 3, GLES20.GL_FLOAT, false, 0, 0);
		    GLES20.glEnableVertexAttribArray(vertexPositionHandle);
		}

		if (element.mNormalBufferName != 0)
		{
			GLES20.glBindBuffer(GL11.GL_ARRAY_BUFFER, element.mNormalBufferName);            	
			//GLES20.glVertexAttribPointer(this.mNormalHandle, 3, GLES20.GL_FLOAT, false, 0, 0);
			GLES20.glVertexAttribPointer(this.vertexNormalHandle, 3, GLES20.GL_FLOAT, false, 0, 0);
		    GLES20.glEnableVertexAttribArray(vertexNormalHandle);	
		} 
		else GLES20.glDisableVertexAttribArray(vertexNormalHandle);	 
		
		if (element.mTexCoordBufferName != 0)
		{
			GLES20.glBindBuffer(GL11.GL_ARRAY_BUFFER, element.mTexCoordBufferName);             
			GLES20.glVertexAttribPointer(this.vertexTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, 0);
		    GLES20.glEnableVertexAttribArray(vertexTexCoordHandle);
		}
		else GLES20.glDisableVertexAttribArray(vertexTexCoordHandle);	 
		
		if (element.mObject.mMaterial[0].mColorName != 0)
		{		
			GLES20.glBindBuffer(GL11.GL_ARRAY_BUFFER, element.mObject.mMaterial[0].mColorName);  
			//GLES20.glVertexAttribPointer(this.mColorHandle, 4, GLES20.GL_FLOAT, false, 0, 0);
			GLES20.glVertexAttribPointer(this.vertexColorHandle, 4, GLES20.GL_FLOAT, false, 0, 0);
		    GLES20.glEnableVertexAttribArray(vertexColorHandle);
		    mRenderStateVector.put(1, 1);
		}
		else
		{	
			if (element.mObject.mMaterial[0].mColors != null)
			{
				Log.d("renderRenderElement()","HAS COLOR POINTER BUT NO COLOR NAME");
				registerMaterial(element,element.mObject.mMaterial[0]);
			}
			else
			{
				GLES20.glDisableVertexAttribArray(vertexColorHandle);
			}
			mRenderStateVector.put(1, 0);
		}
	
		if (element.mTextureName != 0)
		{
			GLES20.glBindTexture(GL11.GL_TEXTURE_2D, element.mTextureName);
			mRenderStateVector.put(0, 1);
		} 	
		else 
		{
			mRenderStateVector.put(0, 0);
		}
		
		
		if (element.mAlphaTest)
		{		
			GLES20.glEnable(GL11.GL_ALPHA_TEST);
		}

		if (element.mAlphaBlending)
		{		
			GLES20.glDepthMask(false);
			GLES20.glEnable (GL11.GL_BLEND);
			GLES20.glBlendFunc( GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_COLOR );
		}
		
		
	    // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
	    // (which currently contains model * view).    
		Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
	    				
		
	    // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
	    // (which now contains model * view * projection).
	    Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0);	
	    

	    
		if (element.mRenderLight)
		{			
			mRenderStateVector.put(2, 1);			
		}
		else
		{
			mRenderStateVector.put(2, 0);	
		}	 	    
	    

	
	    if (element.mRendererInterface != null)
	    {
	    	element.mRendererInterface.prerender(mCamPos, mMVMatrix,mMVPMatrix,mProjectionMatrix,mNormalMatrix,mRenderStateVector);
	    }
	    else
	    {
		    GLES20.glUseProgram(this.mDefaultShaderProgramHandle);
		    
		    GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVMatrix, 0);
		    GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		    GLES20.glUniformMatrix4fv(mNormalMatrixHandle, 1, false, mNormalMatrix, 0);
		    GLES20.glUniform4iv(mRenderStateVectorHandle, 1, mRenderStateVector);	    	
	    }
	    	
		
	    if (element.mIndiceBufferName != 0)
		{		
			GLES20.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, element.mIndiceBufferName);             
			GLES20.glDrawElements(GL11.GL_TRIANGLES, element.mIndiceCount,GL11.GL_UNSIGNED_SHORT, 0);
		}	
		
		if (element.mAlphaTest)
		{		
			GLES20.glDisable(GL11.GL_ALPHA_TEST);						
		}
	
	    if (element.mRendererInterface != null)
	    {
	    	element.mRendererInterface.postrender();
	    }
	    else
	    {
	    	GLES20.glDisableVertexAttribArray(vertexNormalHandle);
	    	GLES20.glDisableVertexAttribArray(vertexColorHandle);
	    }
	    	    
	    
	    
		GLES20.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);             
		GLES20.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0); 	
		GLES20.glBindTexture(GL11.GL_TEXTURE_2D, 0);	
		
		if (element.mAlphaBlending)
		{		
			GLES20.glDisable (GL11.GL_BLEND);
			GLES20.glDepthMask(true);
		}
				
		if (element.mIsBillboard)
		{
			this.billboardEnd();
		}			
					
				
	}
	
	
	public int registerFontMaterial()
	{
		if (GLES20.glGetError() != GLES20.GL_NO_ERROR)
		{
			throw new RuntimeException("registerMaterial() ENTERING: ERROR ON OPENGL ES CALL");
		}				
		
		//OpenGL stuffs	
		IntBuffer buffer = IntBuffer.allocate(1);		
		
		if (this.mBmpFont.mMaterial[0].mTextureData != null)
		{
			GLES20.glGenTextures(1, buffer); 
			GLES20.glBindTexture(GL11.GL_TEXTURE_2D, buffer.get(0)); 
			//GLES20.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_GENERATE_MIPMAP, GL11.GL_TRUE);
			GLES20.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA,this.mBmpFont.mMaterial[0].mWidth, this.mBmpFont.mMaterial[0].mHeight, 0, 
					GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, this.mBmpFont.mMaterial[0].mTextureData);
			GLES20.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR); 
			GLES20.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR); 
			
			
			this.mBmpFont.mTextureName = buffer.get(0);
		}
		
		
		if (GLES20.glGetError() != GLES20.GL_NO_ERROR)
		{
			throw new RuntimeException("registerMaterial() EXITING: ERROR ON OPENGL ES CALL");
		}		
		
		return 0;
	}	
	


	
	public void renderRenderElementPickable(Sd3dRendererElement element)
	{
	}
	
	
	public void billboardCheatSphericalBegin()
	{	
	}
	
	public void billboardEnd()
	{
		// restore the previously 
		// stored modelview matrix
		//mGl.glPopMatrix();		
	}
	
	public void renderRenderListShadowVolume()
	{
		for (int i = 0; i < mCountRenderElement;i++)
		{
			if (mRenderList[i].mIsShadowVolume)
			  renderRenderElement(mRenderList[i]);
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
			  renderRenderElement(mRenderList[i]);
			}
		}		
	}
	
	public void renderRenderList()
	{
		for (int i = 0; i < mCountRenderElement;i++)
		{
			if ((!mRenderList[i].mIsShadowVolume)&&(!mRenderList[i].mIsInScreenSpace))
			  renderRenderElement(mRenderList[i]);
		}
	}
	
	
	public void renderShadowVolumeScene(Sd3dScene scene)
	{
        //GLES20.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        //GLES20.glFrontFace(GL11.GL_CW);
        //GLES20.glCullFace(GL11.GL_BACK);

		Matrix.setIdentityM(mViewMatrix, 0);
		
		if (scene.getCamera().getRotationMatrix() == null)
		{
			float rot[] = scene.getCamera().getOrientation();           
			Matrix.rotateM(mViewMatrix, 0, rot[0], 1.0f, 0.0f, 0.0f);
			Matrix.rotateM(mViewMatrix, 0, rot[1], 0.0f, 1.0f, 0.0f);
			Matrix.rotateM(mViewMatrix, 0, rot[2], 0.0f, 0.0f, 1.0f);
		}
		else
		{
			//mGl.glLoadMatrixf(scene.getCamera().getRotationMatrix(), 0);
			
			//GLU.gluLookAt(mGl, 0, 0, 2, 0, 0, 0, 0, 1, 0);
			Matrix.setLookAtM(matrix, 0, 0, 0, 2, 0, 0, 0, 0, 1, 0);
			Matrix.multiplyMM(mViewMatrix, 0, scene.getCamera().getRotationMatrix(), 0, matrix, 0);
		}
			
        float pos[] = scene.getCamera().getPosition();

        Matrix.translateM(mViewMatrix, 0, -pos[0], -pos[1], -pos[2]);

        //Copy camera position
        mCamPos.position(0);
        mCamPos.put(pos[0]);
        mCamPos.put(pos[1]);
        mCamPos.put(pos[2]);
        mCamPos.put(0.f);
        
        this.setupShadowVolumeStep1();
		this.setupShadowVolumeStep2();
		this.renderRenderListShadowVolume();

		this.setupShadowVolumeStep3();
		this.renderRenderListShadowVolume();        

		this.setupShadowVolumeStep4();
		
		drawShadowVolumeQuad();

		this.setupShadowVolumeStep5();
       
        
        
	}	
	
	public void renderPickableScene(Sd3dScene scene)
	{
		
	}
	

	
	public void renderScene(Sd3dScene scene)
	{
		mCountRenderElement = 0;
		
		
		GLES20.glUseProgram(this.mDefaultShaderProgramHandle);
		for (int i = 0;i < scene.mCountObject;i++)
		{
			this.addObjectToRenderList(scene.mObjectList[i]);
		}

		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GLES20.glFrontFace(GL11.GL_CW);
        GLES20.glCullFace(GL11.GL_BACK);

		Matrix.setIdentityM(mViewMatrix, 0);
       
		if (scene.getCamera().getRotationMatrix() == null)
		{
			
			float rot[] = scene.getCamera().getOrientation();           
			Matrix.rotateM(mViewMatrix, 0, rot[0], 1.0f, 0.0f, 0.0f);
			Matrix.rotateM(mViewMatrix, 0, rot[1], 0.0f, 1.0f, 0.0f);
			Matrix.rotateM(mViewMatrix, 0, rot[2], 0.0f, 0.0f, 1.0f);
			
		}
		else
		{
			//mGl.glLoadMatrixf(scene.getCamera().getRotationMatrix(), 0);
			
			//GLU.gluLookAt(mGl, 0, 0, 2, 0, 0, 0, 0, 1, 0);
			Matrix.setLookAtM(matrix, 0, 0, 0, 2, 0, 0, 0, 0, 1, 0);
			//Matrix.multiplyMM(mViewMatrix, 0, scene.getCamera().getRotationMatrix(), 0, matrix, 0);
			Matrix.multiplyMM(mViewMatrix, 0, scene.getCamera().getRotationMatrix(), 0, matrix, 0);
		}
			
		float pos[] = scene.getCamera().getPosition();
		Matrix.translateM(mViewMatrix, 0, -pos[0], -pos[1], -pos[2]);

        
        //this.setupLightVector(0.f, 0.f, 0.f); 

        updateFrustumFaster();        
        this.renderRenderList();
        
        float[] tmp = mProjectionMatrix;
        this.mProjectionMatrix = this.mProjectionOrthoMatrix;
        Matrix.setIdentityM(mViewMatrix, 0);
        this.renderRenderInScreenSpaceList(); 
        
        mProjectionMatrix = tmp;
        this.renderText();
        
        this.mBmpFont.resetTextBuffer();
                      
	}
	
	public void renderText()
	{
		if (this.mBmpFont.mTextureName == 0)
			this.registerFontMaterial();
			//this.mBmpFont.registerTexture();
			
		
		if (this.mBmpFont.mMesh[0].mVertices.position() != 0)
		{			
			//OpenGL stuffs	
			Matrix.setIdentityM(mModelMatrix, 0);
			Matrix.setIdentityM(mViewMatrix, 0);
			
			mRenderStateVector.put(2, 0);//NO LIGHT	
			mRenderStateVector.put(1, 1);//HAS COLOR
			mRenderStateVector.put(0, 1);//HAS TEXTURE
			
		    // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
		    // (which currently contains model * view).
		    Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
		 
		    // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
		    // (which now contains model * view * projection).
		    Matrix.multiplyMM(mMVPMatrix, 0, mProjectionOrthoMatrix, 0, mMVMatrix, 0);		
		    
		    GLES20.glUseProgram(this.mDefaultShaderProgramHandle);
		    
		    GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVMatrix, 0);
		    GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		    GLES20.glUniform4iv(mRenderStateVectorHandle, 1, mRenderStateVector);			
			
			GLES20.glEnable (GL11.GL_BLEND);
			GLES20.glBlendFunc (GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);			
			
			
			GLES20.glVertexAttribPointer(this.vertexPositionHandle, 3, GLES20.GL_FLOAT, false, 0, this.mBmpFont.getBbVertices());
			GLES20.glEnableVertexAttribArray(vertexPositionHandle);
			
			GLES20.glVertexAttribPointer(this.vertexTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, this.mBmpFont.getTexCoords());
			GLES20.glEnableVertexAttribArray(vertexTexCoordHandle);

			GLES20.glVertexAttribPointer(this.vertexColorHandle, 4, GLES20.GL_FLOAT, false, 0, this.mBmpFont.getColors());
			GLES20.glEnableVertexAttribArray(vertexColorHandle);
			
			GLES20.glBindTexture(GL11.GL_TEXTURE_2D, this.mBmpFont.mTextureName);
			
			GLES20.glDrawElements(GL11.GL_TRIANGLES, this.mBmpFont.mMesh[0].mIndices.position(),GL11.GL_UNSIGNED_SHORT, this.mBmpFont.getIndices());	
			
			GLES20.glDisableVertexAttribArray(vertexPositionHandle);
			GLES20.glDisableVertexAttribArray(vertexTexCoordHandle);
			GLES20.glDisableVertexAttribArray(vertexColorHandle);
			
			GLES20.glDisable (GL11.GL_BLEND);
		}		
	}
	
	//@Overides
	public void updateScreenResolution(int width, int height)
	{
		Log.d("updateScreenResolution()","RECEIVE HEIGHT="+height+" WIDTH="+width);
		this.screenHeight = height;
		this.screenWidth = width;		
		mBmpFont.setScreenProperties(width, height);
	}
	
    public Sd3dRendererGl20(boolean useTranslucentBackground,int maxRenderElement,int width,int height) {
        //mTranslucentBackground = useTranslucentBackground;      
		this.mMaxRenderElement = maxRenderElement;
		
		this.screenHeight = height;
		this.screenWidth = width;
		mRenderList = new Sd3dRendererElement[mMaxRenderElement];
		
		mBmpFont = new Sd3dBmpFont("font.png",null);
		mBmpFont.setScreenProperties(width, height);
		mLightVector = new float[3];
    }		
    private static final int EGL_OPENGL_ES2_BIT = 4;
    public int[] getConfigSpec() {
        // 32bpp + 16bit Z-buffer + 8bit stencil buffer
        int[] configSpec = {
                EGL11.EGL_RED_SIZE,      5,
                EGL11.EGL_GREEN_SIZE,    6,
                EGL11.EGL_BLUE_SIZE,     5,
                //EGL11.EGL_ALPHA_SIZE,    8,                		
        		EGL11.EGL_DEPTH_SIZE,   16,
        		EGL11.EGL_STENCIL_SIZE, 8,
        		EGL11.EGL_RENDERABLE_TYPE,EGL_OPENGL_ES2_BIT,
        		EGL11.EGL_NONE
        };
        return configSpec;
    }

    public void setupShadowVolumeStep1()
    {
    	//Do not test against depth
    	GLES20.glDepthMask(false);
    	
    	//Do not write in the color buffer
    	GLES20.glColorMask(false, false, false, false);
    	
    	//Enable stencil buffer
    	GLES20.glEnable(GL11.GL_STENCIL_TEST);
    	
    	GLES20.glClear(GL11.GL_STENCIL_BUFFER_BIT);
    	
    	GLES20.glStencilFunc(GL11.GL_ALWAYS, 0, ~0);	
    }

    public void setupShadowVolumeStep2()
    {
    	GLES20.glFrontFace(GL11.GL_CW);
    	
    	GLES20.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_INCR);
    }    
    
    public void setupShadowVolumeStep3()
    {
    	GLES20.glFrontFace(GL11.GL_CCW);
    	
    	GLES20.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_DECR);
    }       
    
    public void setupShadowVolumeStep4()
    {
    	GLES20.glColorMask(true, true, true, true);
    	
    	GLES20.glFrontFace(GL11.GL_CW);
    	
    	GLES20.glStencilFunc(GL11.GL_NOTEQUAL, 0, ~0);

    	GLES20.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);    	
    }
    
    public void setupShadowVolumeStep5()
    {
    	GLES20.glDepthMask(true);    	
    	
    	GLES20.glDisable(GL11.GL_STENCIL_TEST);
    }
        
    
    public void sizeChanged(GL11 gl, int width, int height) {
         //gl.glViewport(0, 0, width, height);

         /*
          * Set our projection matrix. This doesn't have to be done
          * each time we draw, but usually a new projection needs to
          * be set when the viewport is resized.
          */
         /*
         float ratio = (float) width / height;
         gl.glMatrixMode(GL11.GL_PROJECTION);
         gl.glLoadIdentity();
         gl.glFrustumf(-ratio, ratio, -1, 1, 1.5f, 1000);
         */
         this.updateScreenResolution(width, height);
    	
    	 // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);
     
        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.5f;
        final float far = 1000.0f;
     
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);    	
        
        Log.d("Sd3dRendererGl20",this.screenWidth+" "+this.screenHeight);
        Matrix.orthoM(mProjectionOrthoMatrix, 0, 0, this.screenWidth, this.screenHeight, 0, -1, 1);
    }

    public void surfaceCreated(GL11 gl) {

    	GLES20.glEnable(GL11.GL_DEPTH_TEST);
    	GLES20.glEnable(GL11.GL_CULL_FACE);
    	
    	
    	this.vertexShader = Sd3dRessourceManager.Manager.getText("shaders/default_vs.gl");
    	this.fragmentShader = Sd3dRessourceManager.Manager.getText("shaders/default_fs.gl");
    	
    	this.mDefaultFragmentShaderHandle = 0;
    	this.mDefaultShaderProgramHandle = 0;
    	this.mDefaultVertexShaderHandle = 0;
    	
 		if (GLES20.glGetError() != GLES20.GL_NO_ERROR)
 		{
 			throw new RuntimeException("surfaceCreated() ENTERING: ERROR ON OPENGL ES CALL");
 		}    	
    	
         //Initialization of the default shaders
         this.initDefaultShaders();
         
  		if (GLES20.glGetError() != GLES20.GL_NO_ERROR)
  		{
  			throw new RuntimeException("surfaceCreated() AFTER initDefaultShaders(): ERROR ON OPENGL ES CALL");
  		}            
         
         this.initShaderAttributes();
         
 		if (GLES20.glGetError() != GLES20.GL_NO_ERROR)
 		{
 			throw new RuntimeException("surfaceCreated() EXITING: ERROR ON OPENGL ES CALL");
 		}
 		
 		GameHolder.mGame.invalidateRenderElements = true;
    }	
    
    public void setupLightVector(float x,float y,float z)
    {
    	/*
    	 float mat_specular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
    	 //float mat_shininess[] = { 50.0f };
    	  //mGl.glLoadIdentity();
 	 
         float noAmbient[] = {0.5f, 0.5f, 0.5f, 1.0f};  	
         float whiteDiffuse[] = {10.0f, 10.0f, -10.0f, 1.0f};
    	 //float light_position[] = { x, y, z, 0.0f };   	
    	 float light_position[] = { 10.f, -10.f, 10.f, 1.0f };
    	 */   
    	 /*
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
       	 
       	 */
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
    	GLES20.glGetError();
    	GLES20.glReadPixels(x,screenHeight - y - top, 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, bb);
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
    	return getObjectFromPickingColor(r,g,b);
    	
    }

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}
	
    private float clip[] = new float[16];
    //private float proj[] = new float[16];
    //private float modl[] = new float[16];
    private int[] mViewport = new int[16];	
	private void updateFrustumFaster()
	{

	    float t;
	 
	    // Get The Current PROJECTION Matrix From OpenGL
	    //GLES20.glGetFloatv( GL11.GL_PROJECTION_MATRIX, proj,0 );
	 
	    // Get The Current MODELVIEW Matrix From OpenGL
	    //GLES20.glGetFloatv( GL11.GL_MODELVIEW_MATRIX, modl,0 );
	    
	    GLES20.glGetIntegerv( GL11.GL_VIEWPORT, mViewport,0 );    
	   
	    // Combine The Two Matrices (Multiply Projection By Modelview)
	    // But Keep In Mind This Function Will Only Work If You Do NOT
	    // Rotate Or Translate Your Projection Matrix
	    clip[ 0] = this.mViewMatrix[ 0] * this.mProjectionMatrix[ 0];
	    clip[ 1] = this.mViewMatrix[ 1] * this.mProjectionMatrix[ 5];
	    clip[ 2] = this.mViewMatrix[ 2] * this.mProjectionMatrix[10] + this.mViewMatrix[ 3] * this.mProjectionMatrix[14];
	    clip[ 3] = this.mViewMatrix[ 2] * this.mProjectionMatrix[11];
	 
	    clip[ 4] = this.mViewMatrix[ 4] * this.mProjectionMatrix[ 0];
	    clip[ 5] = this.mViewMatrix[ 5] * this.mProjectionMatrix[ 5];
	    clip[ 6] = this.mViewMatrix[ 6] * this.mProjectionMatrix[10] + this.mViewMatrix[ 7] * this.mProjectionMatrix[14];
	    clip[ 7] = this.mViewMatrix[ 6] * this.mProjectionMatrix[11];
	 
	    clip[ 8] = this.mViewMatrix[ 8] * this.mProjectionMatrix[ 0];
	    clip[ 9] = this.mViewMatrix[ 9] * this.mProjectionMatrix[ 5];
	    clip[10] = this.mViewMatrix[10] * this.mProjectionMatrix[10] + this.mViewMatrix[11] * this.mProjectionMatrix[14];
	    clip[11] = this.mViewMatrix[10] * this.mProjectionMatrix[11];
	 
	    clip[12] = this.mViewMatrix[12] * this.mProjectionMatrix[ 0];
	    clip[13] = this.mViewMatrix[13] * this.mProjectionMatrix[ 5];
	    clip[14] = this.mViewMatrix[14] * this.mProjectionMatrix[10] + this.mViewMatrix[15] * this.mProjectionMatrix[14];
	    clip[15] = this.mViewMatrix[14] * this.mProjectionMatrix[11];
	 
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
		GLU.gluProject(x, y, z, this.mViewMatrix, 0, this.mProjectionMatrix, 0, this.mViewport, 0, res, 0);
	}
	
	public void displayText(String text,Sd3dRenderer.ALIGN halign, Sd3dRenderer.ALIGN valign, float size)
	{
		this.mBmpFont.addTextToBuffer(text, halign, valign, size);
	}	
	
	public void RendererElements()
	{
		this.mBmpFont.mTextureName = 0;
	}


	@Override
	public void invalidateRendererElements() {
		this.mBmpFont.mTextureName = 0;
	}	
}
