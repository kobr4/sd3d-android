package org.nicolasmy.sd3d.gfx.entity;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Random;

import javax.microedition.khronos.opengles.GL11;

import org.nicolasmy.sd3d.gfx.Sd3dMaterial;
import org.nicolasmy.sd3d.gfx.Sd3dMesh;
import org.nicolasmy.sd3d.gfx.Sd3dObject;
import org.nicolasmy.sd3d.gfx.Sd3dRessourceManager;
import org.nicolasmy.sd3d.gfx.entity.Sd3dParticlesEffectEntity.Sd3dParticle;
//import org.nicolasmy.sd3d.gfx.entity.Sd3dParticlesEffectEntity.Sd3dParticle;
import org.nicolasmy.sd3d.interfaces.Sd3dRendererElementInterface;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

public class Sd3dParticlesEffectHPEntity extends Sd3dGameEntity implements Sd3dRendererElementInterface {
	public class Sd3dParticle
	{
		float x;
		float y;
		float z;
		float xspeed;
		float yspeed;
		float zspeed;
		int lifetime;
		boolean isActive;
	}
	
	public Sd3dParticle mParticlesArray[];
	private float mXSource;
	private float mYSource;
	private float mZSource;	
	
	private float mXSpeedMin;
	private float mYSpeedMin;
	private float mZSpeedMin;
	
	private float mXSpeedMax;
	private float mYSpeedMax;
	private float mZSpeedMax;	
	
	private float mCurrentTime;
	
	private int mMaxParticles;
	
	Random mRandom;
	
	private int mEmitingSpeed;
	private int mLastEmission;
	
	
    FloatBuffer mParticlesDataBuffer;	
    ByteBuffer mBbParticlesDataBuffer;
    
    FloatBuffer mSourcePositionBuffer;
	
	
	private final int vertexPositionHandle = 1;
	private final int vertexColorHandle = 2;
	private final int vertexNormalHandle = 3;
	private final int vertexTexCoordHandle = 4;	
	//private final int vertexLiftetimeHandle = 5;
	private int mRenderStateVectorHandle;
	
	private String vertexShader;
	private String fragmentShader;	
	private int mVertexShaderHandle = 0;
	private int mFragmentShaderHandle = 0;
	private int mShaderProgramHandle = 0;	
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
	private int mNormalHandle;		
	
	private int mCamPosHandle;
	
	private int mLiftetimeHandle;
	
	private int mProjectionMatrixHandle;
	
	private int mCurrentTimeHandle  = 0;	
	
	private int mSourcePositionHandle;
	//private Sd3dObject mBackupObject;
	
	
	public Sd3dParticlesEffectHPEntity(int maxParticles)
	{
		mParticlesArray = new Sd3dParticle[maxParticles];
		for (int i = 0;i < maxParticles;i++)
		{
			mParticlesArray[i] = new Sd3dParticle();
		}		
	
		setupObject(maxParticles);

		this.mOrientation = new float[3];
		this.mPosition = new float[3];
		
		this.isActive = true;		
		this.hasOnProcessFrame = true;
		this.hasObject = true;
		
		mRandom = new Random();
		
    	this.vertexShader = Sd3dRessourceManager.getManager().getText("shaders/particles_vs.gl");
    	this.fragmentShader = Sd3dRessourceManager.getManager().getText("shaders/particles_fs.gl");
    	
    	mMaxParticles = maxParticles;
    	mSourcePositionBuffer = FloatBuffer.allocate(4);
    	
    	
	}
	
	public void setSource(float x,float y,float z,int emitingSpeed)
	{
		mXSource = x;
		mYSource = y;
		mZSource = z;
		mEmitingSpeed = emitingSpeed;
		
		
		mSourcePositionBuffer.position(0);
		mSourcePositionBuffer.put(x);
		mSourcePositionBuffer.put(y);
		mSourcePositionBuffer.put(z);
		mSourcePositionBuffer.position(0);
	}
	
	public static float generateFloat(Random rand,float fmin,float fmax)
	{
		float finterval = fmax - fmin;
		float frand = rand.nextFloat();
		return fmin + frand * finterval;
	}
	
	public void setParticleSpeedInterval(float xmin,float xmax,float ymin,float ymax,float zmin,float zmax)
	{
		mXSpeedMin = xmin;
		mYSpeedMin = ymin;
		mZSpeedMin = zmin;
		
		mXSpeedMax = xmax;
		mYSpeedMax = ymax;
		mZSpeedMax = zmax;			
	}
	
	private void initShaderAttributes()
	{
		if (mShaderProgramHandle != 0)
		{	
			if (GLES20.glGetError() != GLES20.GL_NO_ERROR)
			{
				throw new RuntimeException("initShaderAttributes() AFTER CALL 1: ERROR ON OPENGL ES CALL ");
			}				
			
			// Set program handles. These will later be used to pass in values to the program.
		    mMVPMatrixHandle = GLES20.glGetUniformLocation(mShaderProgramHandle, "u_MVPMatrix");
		 
			if (GLES20.glGetError() != GLES20.GL_NO_ERROR)
			{
				throw new RuntimeException("initShaderAttributes() AFTER CALL 1: ERROR ON OPENGL ES CALL ");
			}	
		    
		    mMVMatrixHandle = GLES20.glGetUniformLocation(mShaderProgramHandle, "u_MVMatrix");	    
		    
		    mProjectionMatrixHandle = GLES20.glGetUniformLocation(mShaderProgramHandle, "u_ProjectionMatrix");	
		    
		    mNormalMatrixHandle = GLES20.glGetUniformLocation(mShaderProgramHandle, "u_NormalMatrix");	
		    
		    //mRenderStateVectorHandle = GLES20.glGetUniformLocation(mShaderProgramHandle, "u_RenderStateVector");	
		    
		    mCamPosHandle = GLES20.glGetUniformLocation(mShaderProgramHandle, "u_CamPosVector");	
		    
		    mColorVectorHandle = GLES20.glGetUniformLocation(mShaderProgramHandle, "u_ColorVector");	
		    
		    mCurrentTimeHandle = GLES20.glGetUniformLocation(mShaderProgramHandle, "u_CurrentTime");	
		    
		    mSourcePositionHandle = GLES20.glGetUniformLocation(mShaderProgramHandle, "u_SourcePosition");	
		    
		    

			
			/*
		    mPositionHandle = GLES20.glGetAttribLocation(mShaderProgramHandle, "a_Position");
		    mColorHandle = GLES20.glGetAttribLocation(mShaderProgramHandle, "a_Color");
		    mNormalHandle = GLES20.glGetAttribLocation(mShaderProgramHandle, "a_Normal");
		    mTexCoordsHandle = GLES20.glGetAttribLocation(mShaderProgramHandle, "a_Texcoords");
		    */
		    mLiftetimeHandle = GLES20.glGetAttribLocation(mShaderProgramHandle, "a_Lifetime");
		    
			if (GLES20.glGetError() != GLES20.GL_NO_ERROR)
			{
				throw new RuntimeException("initShaderAttributes() AFTER CALL 1: ERROR ON OPENGL ES CALL ");
			}		 
		   
	    	// Tell OpenGL to use this program when rendering.
	    	GLES20.glUseProgram(mShaderProgramHandle);
	    	
		}
	}
	
	
	private void initShader()
	{
		mVertexShaderHandle = 0;
		mFragmentShaderHandle = 0;
		mShaderProgramHandle = 0;
		
		if (mVertexShaderHandle == 0)
		{
			mVertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
			Log.d("initDefaultShaders()", "mDefaultVertexShaderHandle="+mVertexShaderHandle);
			if (mVertexShaderHandle != 0)
			{
				// Pass in the shader source.
				GLES20.glShaderSource(mVertexShaderHandle, vertexShader);
		 
				// Compile the shader.
				GLES20.glCompileShader(mVertexShaderHandle);
		 
			    // Get the compilation status.
			    final int[] compileStatus = new int[1];
			    GLES20.glGetShaderiv(mVertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
			 
			    // If the compilation failed, delete the shader.
			    if (compileStatus[0] == 0)
			    {
			    	Log.e("initDefaultShaders()", "Failed to compile vertex shader");
			    	Log.d("initDefaultShaders()",GLES20.glGetShaderInfoLog(mVertexShaderHandle));
			        GLES20.glDeleteShader(mVertexShaderHandle);
			        mVertexShaderHandle = 0;
			    }
			}
		 
			if (mVertexShaderHandle == 0)
			{
				
				throw new RuntimeException("Error creating vertex shader.");
			}	
		}
		
		if (mFragmentShaderHandle == 0)
		{
			mFragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
			Log.d("initDefaultShaders()", "mDefaultFragmentShaderHandle="+mFragmentShaderHandle);
			if (mFragmentShaderHandle != 0)
			{
				// Pass in the shader source.
				GLES20.glShaderSource(mFragmentShaderHandle, fragmentShader);
		 
				// Compile the shader.
				GLES20.glCompileShader(mFragmentShaderHandle);
		 
			    // Get the compilation status.
			    final int[] compileStatus = new int[1];
			    GLES20.glGetShaderiv(mFragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
			 
			    // If the compilation failed, delete the shader.
			    if (compileStatus[0] == 0)
			    {
			    	Log.e("initDefaultShaders()", "Failed to compile fragment shader");
			    	Log.d("initDefaultShaders()",GLES20.glGetShaderInfoLog(mFragmentShaderHandle));			    	
			        GLES20.glDeleteShader(mFragmentShaderHandle);
			        mFragmentShaderHandle = 0;
			    }
			}
		 
			if (mFragmentShaderHandle == 0)
			{
				throw new RuntimeException("Error creating fragment shader.");
			}	
		}
		
		if (mShaderProgramHandle == 0)
		{
			mShaderProgramHandle = GLES20.glCreateProgram();
			
		    // Bind the vertex shader to the program.
		    GLES20.glAttachShader(mShaderProgramHandle, mVertexShaderHandle);
		 
		    // Bind the fragment shader to the program.
		    GLES20.glAttachShader(mShaderProgramHandle, mFragmentShaderHandle);
		 
		    // Bind attributes
		    
		    GLES20.glBindAttribLocation(mShaderProgramHandle, this.vertexPositionHandle, "a_Position");
		    GLES20.glBindAttribLocation(mShaderProgramHandle, this.vertexColorHandle, "a_Color");
		    GLES20.glBindAttribLocation(mShaderProgramHandle, this.vertexTexCoordHandle, "a_Texcoords");
		    GLES20.glBindAttribLocation(mShaderProgramHandle, this.vertexNormalHandle, "a_Normal");
		    GLES20.glBindAttribLocation(mShaderProgramHandle, 5, "a_Lifetime");
		    
		    //GLES20.glBindAttribLocation(mShaderProgramHandle, this.vertexLiftetimeHandle, "a_Lifetime");
		    
		    // Link the two shaders together into a program.
		    GLES20.glLinkProgram(mShaderProgramHandle);
		 
		    // Get the link status.
		    final int[] linkStatus = new int[1];
		    GLES20.glGetProgramiv(mShaderProgramHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
		 
		    // If the link failed, delete the program.
		    if (linkStatus[0] == 0)
		    {
		        GLES20.glDeleteProgram(mShaderProgramHandle);
		        mShaderProgramHandle = 0;
		    }
		    	    
		}
		
		if (mShaderProgramHandle == 0)
		{
		    throw new RuntimeException("Error creating program.");
		}			
	}	
	
	/* (non-Javadoc)
	 * @see org.nicolasmy.sd3d.gfx.entity.Sd3dRendererElementInterface#register()
	 */
	public void register()
	{
		this.initShader();
		this.initShaderAttributes();
	}
	
	/* (non-Javadoc)
	 * @see org.nicolasmy.sd3d.gfx.entity.Sd3dRendererElementInterface#unregister()
	 */
	public void unregister()
	{
		
	}
	
	int bufferName = 0;
	
	/* (non-Javadoc)
	 * @see org.nicolasmy.sd3d.gfx.entity.Sd3dRendererElementInterface#render()
	 */
	public void prerender(FloatBuffer camPosVector, float[] mVMatrix,float[] mVPMatrix,float[] projectionMatrix, float[] normalMatrix,IntBuffer renderStateVector)
	{
		//ALPHA BLENDING
		GLES20.glDepthMask(false);
		GLES20.glEnable (GL11.GL_BLEND);
		GLES20.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GLES20.glUseProgram(this.mShaderProgramHandle);	
	   	
		IntBuffer buffer = IntBuffer.allocate(1);	
		
		GLES20.glGenBuffers(1,buffer);
		bufferName = buffer.get(0);
		GLES20.glBindBuffer(GL11.GL_ARRAY_BUFFER, buffer.get(0));
		
		mParticlesDataBuffer.position(0);
		GLES20.glBufferData(GL11.GL_ARRAY_BUFFER,this.mParticlesDataBuffer.capacity()*4,mParticlesDataBuffer,GL11.GL_STATIC_DRAW);	    
	
				
		
		
		//Log.d("","TOTO");
		
		//this.mParticlesDataBuffer.position(0);
	    //GLES20.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);  
		//GLES20.glVertexAttribPointer(mLiftetimeHandle, 4, GLES20.GL_FLOAT, false, 0, this.mParticlesDataBuffer);
	    //GLES20.glVertexAttribPointer(mLiftetimeHandle, 4, GLES20.GL_FLOAT, false, 0, mBbParticlesDataBuffer);
		
		GLES20.glVertexAttribPointer(mLiftetimeHandle, 4, GLES20.GL_FLOAT, false, 0, 0);
		
		GLES20.glEnableVertexAttribArray(mLiftetimeHandle);			
			    
	    /*
	    for(int i=0; i<3; i++ ) 
	    	for(int j=0; j<3; j++ ) {
	    		if ( i==j )
	    			mVMatrix[i*4+j] = 1.0f;
	    		else
	    			mVMatrix[i*4+j] = 0.0f;
	    	}	    
	    */
	    
	    /*
	    for(int i=0; i<3; i+=2 ) 
	    	for(int j=0; j<3; j++ ) {
	    		if ( i==j )
	    			mVMatrix[i*4+j] = 1.0f;
	    		else
	    			mVMatrix[i*4+j] = 0.0f;
	    	}	    
	    */
	    //Matrix.multiplyMM(mVPMatrix, 0, projectionMatrix, 0, mVMatrix, 0);	
	    
	    GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mVMatrix, 0);
	
	    
	    
	    
	    GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mVPMatrix, 0);
	    GLES20.glUniformMatrix4fv(mNormalMatrixHandle, 1, false, normalMatrix, 0);
	      
	    
	    //GLES20.glUniform4iv(mRenderStateVectorHandle, 1, renderStateVector);
	    
 
	    
	    GLES20.glUniform4fv(mSourcePositionHandle, 1, mSourcePositionBuffer);
	    GLES20.glUniformMatrix4fv(this.mProjectionMatrixHandle, 1, false, projectionMatrix, 0);
	    GLES20.glUniform4fv(this.mCamPosHandle, 1, camPosVector);
	    
	    GLES20.glUniform1f(mCurrentTimeHandle, this.mCurrentTime);
	    
	    
	    
	}
	
	public void postrender()
	{
		
		int buffer[] = new int[1];
		buffer[0] = bufferName;
		GLES20.glDeleteBuffers(1, buffer, 0);
		
		GLES20.glDisableVertexAttribArray(mLiftetimeHandle);		
		GLES20.glUseProgram(0);	
		
		//ALPHA BLENDING
		GLES20.glDisable (GL11.GL_BLEND);
		GLES20.glDepthMask(true);		
	}
	
	
	private void setupObject(int maxParticles)
	{
		float one = 0.4f;	
		
		float vertices[] = {
                //-one, -one, -one,
                //one, -one, -one,
                //one,  one, -one,
                //-one,  one, -one,
                -one, -one,  0,
                0,0,0,
                1.f, 0.f,
                one, -one,  0,
                0,0,0,
                1.f, 1.f,
                one,  one,  0,
                0,0,0,
                0.f, 1.f, 
                -one,  one,  0,
                0,0,0,
                0.f, 0.f, 
        };

		float colors[] = {
                1.f,    1.f,    1.f,  1.f,
                1.f,    1.f,    1.f,  1.f,
                1.f,    1.f,    1.f,  1.f,
                1.f,    1.f,    1.f,  1.f,
                //1.f,    1.f,    1.f,  1.f,
                //1.f,    1.f,    1.f,  1.f,
                //1.f,    1.f,    1.f,  1.f,
                //1.f,    1.f,    1.f,  1.f,
        };		
		

        char indices[] = {
        		
                //0, 4, 5,    0, 5, 1,
                //1, 5, 6,    1, 6, 2,
                //2, 6, 7,    2, 7, 3,
                //3, 7, 4,    3, 4, 0,
                //4, 7, 6,    4, 6, 5,
                
        		//3, 0, 1,    3, 1, 2
        		1, 0, 3,	2, 1, 3
        };
		/*
        float texcoords[] = {
        		1.f, 0.f,
        		1.f, 1.f,
        		0.f, 1.f, 
        		0.f, 0.f,          		
        };				
        */
        
        
        
		mObject = new Sd3dObject();
		mObject.mMesh = new Sd3dMesh[1];
		mObject.mMaterial = new Sd3dMaterial[1];		

		mObject.mMesh[0] = new Sd3dMesh();
		//mObject.mMesh[0].mVertices = FloatBuffer.allocate(maxParticles * vertices.length);
		//mObject.mMesh[0].mTexCoords = FloatBuffer.allocate(maxParticles * texcoords.length);
				
	        
			/*
			mObject.mMesh[i].mTexCoords = FloatBuffer.allocate(texcoords.length);
			mObject.mMesh[i].mTexCoords.put(texcoords);
			mObject.mMesh[i].mTexCoords.position(0);	
			*/
			
		mObject.mMesh[0].mIndices = CharBuffer.allocate(indices.length * maxParticles);
		
		for (int i = 0;i < maxParticles;i++)
		{
			mObject.mMesh[0].mVertices.put(vertices);
			//mObject.mMesh[0].mTexCoords.put(texcoords);
			for (int j = 0;j < indices.length; j++)
			{
				mObject.mMesh[0].mIndices.put((char)(indices[j] + i*4));
			}
		}			
		mObject.mMesh[0].mVertices.position(0);	
		
		mParticlesDataBuffer = FloatBuffer.allocate(4 * 4 * maxParticles);
		//mBbParticlesDataBuffer =  ByteBuffer.allocateDirect(4 * 4 * 4 * maxParticles);
		//mBbParticlesDataBuffer = ByteBuffer.allocateDirect(maxParticles);
		
		//mParticlesDataBuffer = mBbParticlesDataBuffer.asFloatBuffer();
		mParticlesDataBuffer.position(0);
		///Log.d("setupObject()",mBbParticlesDataBuffer.capacity() + " " + mParticlesDataBuffer.capacity());
		while (mParticlesDataBuffer.position() < mParticlesDataBuffer.capacity())
		{
			mParticlesDataBuffer.put(1.0f);
		}
		mParticlesDataBuffer.position(0);
		
		
		mObject.mMesh[0].mIndices.position(0);

		//mObject.mMesh[0].mTexCoords.position(0);
		
		mObject.mMesh[0].setMeshPosition(0, 0, 0);
		
		mObject.mMesh[0].mRendererElementInterface = this;
			
		mObject.mMaterial[0] = new Sd3dMaterial();
		
	
		try {
			mObject.mMaterial[0].loadTexture("textures/circle.png");
		} catch (IOException e) {
			Log.d("","");
		}		
		
		
		mObject.mMaterial[0].mColors = FloatBuffer.allocate(colors.length * maxParticles);
		for (int i = 0;i < maxParticles;i++)
		{

			mObject.mMaterial[0].mColors.put(colors);
			
		}
		mObject.mMaterial[0].mColors.position(0);
		
		
			
	}
	
	
	public void onProcessFrame(int elapsedtime)
	{
		if (elapsedtime == 0)
		 return;
		
		float felapsedtime = elapsedtime /1000.f;		
		mLastEmission += elapsedtime;
		int emitingCount = 0;
		
		
		mCurrentTime += felapsedtime;
		
//		for (int i = 0;i < mParticlesArray.length;i++)
//		{
//			if (mParticlesArray[i].isActive)
//			{
//				if (mParticlesArray[i].lifetime < elapsedtime)
//				{
//					mParticlesArray[i].isActive = false;
//					
//					if (mObject.mRenderElement != null)
//						mObject.mRenderElement[i].mDisable = true;						
//				} else mParticlesArray[i].lifetime = mParticlesArray[i].lifetime - elapsedtime;
//				
//				mParticlesArray[i].x += mParticlesArray[i].xspeed * felapsedtime;
//				mParticlesArray[i].y += mParticlesArray[i].yspeed * felapsedtime;
//				mParticlesArray[i].z += mParticlesArray[i].zspeed * felapsedtime;
//				
//				mObject.mMesh[i].setMeshPosition(mParticlesArray[i].x, mParticlesArray[i].y, mParticlesArray[i].z);
//							
//			}
//			else
//			{
//				if (mObject.mRenderElement != null)
//					mObject.mRenderElement[i].mDisable = true;			
//				
				//if ((emitingCount < mEmitingSpeed)&&(mLastEmission > 200))
					if ((emitingCount < mEmitingSpeed))
				{
					//Spawn particle
					int count = 0;
					boolean bExit = false;
					mParticlesDataBuffer.position(0);
					
					while ( count < mMaxParticles && !bExit)
					{
						float f = mParticlesDataBuffer.get();
						if (this.mCurrentTime - f > 2.0)
						{
							
							mParticlesDataBuffer.position(mParticlesDataBuffer.position() - 1);
							
							float fXspeed = generateFloat(mRandom,mXSpeedMin,mXSpeedMax);
							float fYspeed = generateFloat(mRandom,mYSpeedMin,mYSpeedMax);
							float fZspeed = generateFloat(mRandom,mZSpeedMin,mZSpeedMax);

							//Log.d("PARTICLES","onProcessFrame() " + (this.mCurrentTime-f) + " f="+f + " xspeed="+fXspeed+ " yspeed="+fYspeed+ " zspeed="+fZspeed+" count="+mParticlesDataBuffer.position());
							for (int i = 0; i < 4;i++)
							{							
								mParticlesDataBuffer.put(this.mCurrentTime);
								mParticlesDataBuffer.put(fXspeed);
								mParticlesDataBuffer.put(fYspeed);
								mParticlesDataBuffer.put(fZspeed);
							}
							bExit = true;
						}
						
						else
						{
							mParticlesDataBuffer.position(mParticlesDataBuffer.position() - 1);
							
							for (int i = 0; i < 4;i++)
							{
								mParticlesDataBuffer.get();
								mParticlesDataBuffer.get();
								mParticlesDataBuffer.get();
								mParticlesDataBuffer.get();
							}
						}
						
						
						count++;
					}
					
					mParticlesDataBuffer.position(0);
					emitingCount++;
					mLastEmission = 0;
					
					
	/*		
					mParticlesArray[i].x = mXSource;
					mParticlesArray[i].y = mYSource;		
					mParticlesArray[i].z = mZSource;	
					
					mParticlesArray[i].xspeed = generateFloat(mRandom,mXSpeedMin,mXSpeedMax);
					mParticlesArray[i].yspeed = generateFloat(mRandom,mYSpeedMin,mYSpeedMax);
					mParticlesArray[i].zspeed = generateFloat(mRandom,mZSpeedMin,mZSpeedMax);
					mParticlesArray[i].lifetime = 1000;
					emitingCount++;
					mParticlesArray[i].isActive = true;	
					
					if (mObject.mRenderElement != null)
						mObject.mRenderElement[i].mDisable = false;
					
					mObject.mMesh[i].setMeshPosition(mParticlesArray[i].x, mParticlesArray[i].y, mParticlesArray[i].z);
					
					mLastEmission = 0;
				}					
			}
	*/
		}
	}
}
