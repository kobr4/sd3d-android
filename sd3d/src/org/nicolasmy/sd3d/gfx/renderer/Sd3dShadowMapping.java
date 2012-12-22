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
	int fb [] = new int[1];
	int depthRb[] = new int[1];
	int renderTex[] = new int[1];	
	IntBuffer texBuffer;
	private String vertexShader = "";
	private String fragmentShader = "";
	
	private float ratio = 1.0f;
	public Sd3dShader shader;
	public void init(int screenWidth, int screenHeight)
	{
		if (Sd3dConfig.getString("shadowmap_ratio") != null)
			ratio = Float.parseFloat(Sd3dConfig.getString("shadowmap_ratio"));
		
		shadowMapWidth = (int) (ratio * screenWidth);
		shadowMapHeight = (int) (ratio * screenHeight);
		

    	this.vertexShader = Sd3dRessourceManager.getManager().getText("shaders/shadow_vs.gl");
    	this.fragmentShader = Sd3dRessourceManager.getManager().getText("shaders/shadow_fs.gl");		
			
    	shader = new Sd3dShader(this.vertexShader,this.fragmentShader);
    	shader.register();
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
		

		
		shader.bind();

		//GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
	}
	
	public void onRenderToScreen(int screenWidth, int screenHeight)
	{
		GLES20.glCullFace(GLES20.GL_BACK); 
		
		GLES20.glViewport(0, 0, screenWidth, screenHeight);
		//GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fb[0]);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
	}
	
}
