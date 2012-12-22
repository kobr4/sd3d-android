package org.nicolasmy.sd3d.gfx.renderer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import android.opengl.GLES20;
import android.util.Log;

public class Sd3dShader {
	
	/* PUBLIC MEMBERS */
    /**
     * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
     * of being located at the center of the universe) to world space.
     */
    public float[] modelMatrix = new float[16];	
    public float[] normalMatrix = new float[16];
    /**
     * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
     * it positions things relative to our eye.
     */
    public float[] viewMatrix = new float[16];    
    public float[] projectionMatrix = new float[16];  
    public float[] projectionOrthoMatrix = new float[16];  
    /** Allocate storage for the final combined matrix. This will be passed into the shader program. */
    public float[] MVPMatrix = new float[16];    
    public float[] MVMatrix = new float[16];
    public FloatBuffer colorVector = FloatBuffer.allocate(4);
    public FloatBuffer camPos = FloatBuffer.allocate(4);
    public float[] shadowTextureMatrix = new float[16];
    
    //0 : Texture flag
    //1 : Color flag (0 : none;1 : color attrib;2 : color)
    //2 : Render light flag
    //3 : Nb pass
    public IntBuffer renderStateVector = IntBuffer.allocate(4);
    
	public static final int vertexPositionHandle = 1;
	public static final int vertexColorHandle = 2;
	public static final int vertexNormalHandle = 3;
	public static final int vertexTexCoordHandle = 4;	
    
    /* PRIVATE MEMBERS */
	private String vertexShader;
	private String fragmentShader;

	private int mVertexShaderHandle = 0;
	private int mFragmentShaderHandle = 0;
	private int mProgramHandle = 0;
	/** This will be used to pass in the transformation matrix. */
	private int mMVPMatrixHandle;
	/** This will be used to pass in the transformation matrix. */
	private int mMVMatrixHandle;
	/** This will be used to pass in the transformation matrix. */
	private int mNormalMatrixHandle;		
	/** This will be used to pass in the transformation matrix. */
	private int mShadowTextureMatrixHandle;	
	private int mColorVectorHandle;
	private int mRenderStateVectorHandle;
	
	public int getLightDirHandle() {
		return mLightDirHandle;
	}

	public void setLightDirHandle(int lightDirHandle) {
		this.mLightDirHandle = lightDirHandle;
	}

	public int getLightPosHandle() {
		return mLightPosHandle;
	}

	public void setLightPosHandle(int lightPosHandle) {
		this.mLightPosHandle = lightPosHandle;
	}

	public int getLightAmbientHandle() {
		return mLightAmbientHandle;
	}

	public void setLightAmbientHandle(int lightAmbientHandle) {
		this.mLightAmbientHandle = lightAmbientHandle;
	}


	private int mLightDirHandle;
	private int mLightPosHandle;
	private int mLightAmbientHandle;
	/** This will be used to pass in model position information. */
	//private int mPositionHandle;
	
	/** This will be used to pass in model position information. */
	//private int mTexCoordsHandle;
	 	
	/** This will be used to pass in model color information. */
	//private int mColorHandle;

	/** This will be used to pass in model color information. */
	//private int mNormalHandle;	
	

	
	
	public Sd3dShader(String vertex, String fragment)
	{
		vertexShader = vertex;
		fragmentShader = fragment;
	}
	
	public void register()
	{
		initShader();
		initAttributes();
	}
	
	public void unregister() {
		GLES20.glDeleteShader(mProgramHandle);
	}
	
	
	public void bind()
	{
    	GLES20.glUseProgram(mProgramHandle);
	}
	
	public void unbind()
	{
    	GLES20.glUseProgram(0);			
	}
	
	public int getProgramHandle()
	{
		return mProgramHandle;
	}
	
	public int getFramentShaderHandle()
	{
		return mFragmentShaderHandle;
	}	
	
	public int getVertexShaderHandle()
	{
		return mVertexShaderHandle;
	}	
	
	public int getMVPMatrixHandle() {
		return mMVPMatrixHandle;
	}

	public int getMVMatrixHandle() {
		return mMVMatrixHandle;
	}

	public int getNormalMatrixHandle() {
		return mNormalMatrixHandle;
	}

	public int getShadowTextureMatrixHandle() {
		return mShadowTextureMatrixHandle;
	}

	public int getRenderStateVectorHandle() {
		return mRenderStateVectorHandle;
	}	
	
	public int getColorVectorHandle() {
		return mColorVectorHandle;
	}	
	
/******************************************
 * 	            PRIVATE STUFF             *
 *****************************************/
	private void initAttributes()
	{
		if (mProgramHandle != 0)
		{	
			// Set program handles. These will later be used to pass in values to the program.
		    mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
		 
			if (GLES20.glGetError() != GLES20.GL_NO_ERROR)
			{
				throw new RuntimeException("initShaderAttributes() AFTER CALL 1: ERROR ON OPENGL ES CALL ");
			}	
		    
		    mMVMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVMatrix");	    
		    
		    mShadowTextureMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_ShadowTextureMatrix");	
		    
		    mNormalMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_NormalMatrix");	
		    
		    mRenderStateVectorHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_RenderStateVector");	
		    
		    mColorVectorHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_ColorVector");
		    //Log.d("Sd3dShader","Color vector handle= "+mColorVectorHandle);
		    mLightDirHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_LightDir");
		    
		    mLightPosHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_LightPos");
		    
		    mLightAmbientHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_LightAmbient");
		    
			if (GLES20.glGetError() != GLES20.GL_NO_ERROR)
			{
				throw new RuntimeException("initShaderAttributes() AFTER CALL 1: ERROR ON OPENGL ES CALL ");
			}	    	
		}
	}
	
	
	private void initShader()
	{
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
		
		if (mProgramHandle == 0)
		{
			mProgramHandle = GLES20.glCreateProgram();
			
		    // Bind the vertex shader to the program.
		    GLES20.glAttachShader(mProgramHandle, mVertexShaderHandle);
		 
		    // Bind the fragment shader to the program.
		    GLES20.glAttachShader(mProgramHandle, mFragmentShaderHandle);
		 
		    // Bind attributes
		    GLES20.glBindAttribLocation(mProgramHandle, vertexPositionHandle, "a_Position");
		    GLES20.glBindAttribLocation(mProgramHandle, vertexColorHandle, "a_Color");
		    GLES20.glBindAttribLocation(mProgramHandle, vertexTexCoordHandle, "a_Texcoords");
		    GLES20.glBindAttribLocation(mProgramHandle, vertexNormalHandle, "a_Normal");
		    
		    // Link the two shaders together into a program.
		    GLES20.glLinkProgram(mProgramHandle);
		 
		    // Get the link status.
		    final int[] linkStatus = new int[1];
		    GLES20.glGetProgramiv(mProgramHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
		 
		    // If the link failed, delete the program.
		    if (linkStatus[0] == 0)
		    {
		        GLES20.glDeleteProgram(mProgramHandle);
		        mProgramHandle = 0;
		    }
		    	    
		}
		
		if (mProgramHandle == 0)
		{
		    throw new RuntimeException("Error creating program.");
		}			
	}


}
