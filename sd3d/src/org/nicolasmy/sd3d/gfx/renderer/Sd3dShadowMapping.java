package org.nicolasmy.sd3d.gfx.renderer;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ByteOrder;

import org.nicolasmy.sd3d.Sd3dConfig;
import org.nicolasmy.sd3d.gfx.Sd3dRessourceManager;

import android.opengl.GLES20;
import android.util.Log;

public class Sd3dShadowMapping {
	//glGenFramebuffers();
	//android.opengl.GLES20.
	private int shadowMapWidth = 1024;
	private int shadowMapHeight = 1024;
	public static int FLOAT_SIZE_BYTES = 4;
	public int shadowShaderHandle = 0;
	public int shadowVertexShaderHandle = 0;
	public int shadowFragmentShaderHandle = 0;
	public int shadowShaderProgramHandle = 0;
	int fb [] = new int[1];
	int depthRb[] = new int[1];
	int renderTex[] = new int[1];	
	IntBuffer texBuffer;
	public float viewMatrix[] = new float[16]; 
	public float mvMatrix[] = new float[16];
	private String vertexShader = "";
	private String fragmentShader = "";
	
	private final int vertexPositionHandle = 1;
	private final int vertexColorHandle = 2;
	private final int vertexNormalHandle = 3;
	private final int vertexTexCoordHandle = 4;
	public int mMVPMatrixHandle = 0;
	private float ratio = 1.0f;
	public void init(int screenWidth, int screenHeight)
	{
		if (Sd3dConfig.getString("shadowmap_ratio") != null)
			ratio = Float.parseFloat(Sd3dConfig.getString("shadowmap_ratio"));
		
		shadowMapWidth = (int) (ratio * shadowMapWidth);
		shadowMapHeight = (int) (ratio * shadowMapHeight);
		

    	this.vertexShader = Sd3dRessourceManager.getManager().getText("shaders/shadow_vs.gl");
    	this.fragmentShader = Sd3dRessourceManager.getManager().getText("shaders/shadow_fs.gl");		
		
    	initShadowShaders();
        
 		if (GLES20.glGetError() != GLES20.GL_NO_ERROR)
 		{
 			throw new RuntimeException("surfaceCreated() AFTER initDefaultShaders(): ERROR ON OPENGL ES CALL");
 		}            
        
        initShaderAttributes();
        
		if (GLES20.glGetError() != GLES20.GL_NO_ERROR)
		{
			throw new RuntimeException("surfaceCreated() EXITING: ERROR ON OPENGL ES CALL");
		}	
    	
		// generate
		GLES20.glGenFramebuffers(1, fb, 0);
		//GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fb[0]);
		
		GLES20.glGenRenderbuffers(1, depthRb, 0);
		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthRb[0]);
		
		GLES20.glGenTextures(1, renderTex, 0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, renderTex[0]);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
				GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
				GLES20.GL_NEAREST);

		// create it 
		// create an empty intbuffer first?
		//int[] buf = new int[shadowMapWidth * shadowMapHeight];
		
		texBuffer = ByteBuffer.allocateDirect(shadowMapWidth * shadowMapHeight * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asIntBuffer();
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, shadowMapWidth, shadowMapHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, texBuffer);//GLES20.GL_UNSIGNED_SHORT_5_6_5, texBuffer);
		
		// create render buffer and bind 16-bit depth buffer
		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthRb[0]);
		GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, shadowMapWidth, shadowMapHeight);	
	
		
		/*
		// We'll use a depth texture to store the depths in the shadow map
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_DEPTH_COMPONENT, shadowMapWidth, shadowMapHeight, 0,
				GLES20.GL_DEPTH_COMPONENT, GLES20.GL_UNSIGNED_INT, null);
 
        // Attach the depth texture to FBO depth attachment point
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,GLES20.GL_TEXTURE_2D, renderTex[0], 0);	
		*/
		
		// check status
		int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
		if (status != GLES20.GL_FRAMEBUFFER_COMPLETE)
		{
			Log.d("BallJump3D","Error : FRAMEBUFFER UNAVAILABLE");

			throw new RuntimeException("Error : FRAMEBUFFER UNAVAILABLE");

		}		
	}
	
	public void onRenderToDepthTexture()
	{
		// Cull front faces for shadow generation
		GLES20.glCullFace(GLES20.GL_FRONT); 
		
		GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

		
		// much bigger viewport?
		//Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 0.5f, 10);
		
		GLES20.glViewport(0, 0, shadowMapWidth, shadowMapHeight);
		
		// bind the generated framebuffer
		//GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fb[0]);
		
		//GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_TEXTURE_2D, renderTex[0], 0);
		
		// specify texture as color attachment
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, renderTex[0], 0);
		
		// attach render buffer as depth buffer
		GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, depthRb[0]);
		

		
		
		GLES20.glUseProgram(shadowShaderProgramHandle);
		
		//GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
	}
	
	public void onRenderToScreen(int screenWidth, int screenHeight)
	{
		GLES20.glCullFace(GLES20.GL_BACK); 
		
		GLES20.glViewport(0, 0, screenWidth, screenHeight);
		//GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fb[0]);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
	}
	
	private void initShadowShaders()
	{
		if (shadowShaderHandle == 0)
		{
			shadowVertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
			Log.d("initDefaultShaders()", "mDefaultVertexShaderHandle="+shadowVertexShaderHandle);
			if (shadowVertexShaderHandle != 0)
			{
				// Pass in the shader source.
				GLES20.glShaderSource(shadowVertexShaderHandle, vertexShader);
		 
				// Compile the shader.
				GLES20.glCompileShader(shadowVertexShaderHandle);
		 
			    // Get the compilation status.
			    final int[] compileStatus = new int[1];
			    GLES20.glGetShaderiv(shadowVertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
			 
			    // If the compilation failed, delete the shader.
			    if (compileStatus[0] == 0)
			    {
			    	Log.e("initDefaultShaders()", "Failed to compile vertex shader");
			    	Log.d("initDefaultShaders()",GLES20.glGetShaderInfoLog(shadowVertexShaderHandle));
			        GLES20.glDeleteShader(shadowVertexShaderHandle);
			        shadowVertexShaderHandle = 0;
			    }
			}
		 
			if (shadowVertexShaderHandle == 0)
			{
				throw new RuntimeException("Error creating vertex shader.");
			}	
		}
		
		if (shadowFragmentShaderHandle == 0)
		{
			shadowFragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
			Log.d("initDefaultShaders()", "mDefaultFragmentShaderHandle="+shadowFragmentShaderHandle);
			if (shadowFragmentShaderHandle != 0)
			{
				// Pass in the shader source.
				GLES20.glShaderSource(shadowFragmentShaderHandle, fragmentShader);
		 
				// Compile the shader.
				GLES20.glCompileShader(shadowFragmentShaderHandle);
		 
			    // Get the compilation status.
			    final int[] compileStatus = new int[1];
			    GLES20.glGetShaderiv(shadowFragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
			 
			    // If the compilation failed, delete the shader.
			    if (compileStatus[0] == 0)
			    {
			    	Log.e("initDefaultShaders()", "Failed to compile fragment shader");
			    	Log.d("initDefaultShaders()",GLES20.glGetShaderInfoLog(shadowFragmentShaderHandle));			    	
			        GLES20.glDeleteShader(shadowFragmentShaderHandle);
			        shadowFragmentShaderHandle = 0;
			    }
			}
		 
			if (shadowFragmentShaderHandle == 0)
			{
				throw new RuntimeException("Error creating fragment shader.");
			}	
		}
		
		if (shadowShaderProgramHandle == 0)
		{
			shadowShaderProgramHandle = GLES20.glCreateProgram();
			
		    // Bind the vertex shader to the program.
		    GLES20.glAttachShader(shadowShaderProgramHandle, shadowVertexShaderHandle);
		 
		    // Bind the fragment shader to the program.
		    GLES20.glAttachShader(shadowShaderProgramHandle, shadowFragmentShaderHandle);
		 
		    // Bind attributes
		    GLES20.glBindAttribLocation(shadowShaderProgramHandle, this.vertexPositionHandle, "a_Position");
		    GLES20.glBindAttribLocation(shadowShaderProgramHandle, this.vertexColorHandle, "a_Color");
		    GLES20.glBindAttribLocation(shadowShaderProgramHandle, this.vertexTexCoordHandle, "a_Texcoords");
		    GLES20.glBindAttribLocation(shadowShaderProgramHandle, this.vertexNormalHandle, "a_Normal");
		    
		    // Link the two shaders together into a program.
		    GLES20.glLinkProgram(shadowShaderProgramHandle);
		 
		    // Get the link status.
		    final int[] linkStatus = new int[1];
		    GLES20.glGetProgramiv(shadowShaderProgramHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
		 
		    // If the link failed, delete the program.
		    if (linkStatus[0] == 0)
		    {
		        GLES20.glDeleteProgram(shadowShaderProgramHandle);
		        shadowShaderProgramHandle = 0;
		        throw new RuntimeException("Unable to link program.");
		    }
		    	    
		}
		
		if (shadowShaderProgramHandle == 0)
		{
		    throw new RuntimeException("Error creating program.");
		}			
	}	
	
	private void initShaderAttributes()
	{
		if (shadowShaderProgramHandle != 0)
		{	
			mMVPMatrixHandle = GLES20.glGetUniformLocation(shadowShaderProgramHandle, "u_MVPMatrix");
			
			if (GLES20.glGetError() != GLES20.GL_NO_ERROR)
			{
				throw new RuntimeException("initShaderAttributes() AFTER CALL 1: ERROR ON OPENGL ES CALL ");
			}			    		
	   	}
	}	
}
