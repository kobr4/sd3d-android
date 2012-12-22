package org.nicolasmy.sd3d.gfx.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import android.opengl.GLES20;
import android.util.Log;

public class Sd3dFrameBuffer {
	private int fb [] = new int[1];
	private int depthRb[] = new int[1];
	private int renderTex[] = new int[1];
	private static int FLOAT_SIZE_BYTES = 4;
	private int width;
	private int height;
	//private int screenWidth;
	//private int screenHeight;
	
	private IntBuffer texBuffer = null;
	
	public void release() {
		
		GLES20.glDeleteRenderbuffers(1, IntBuffer.wrap(depthRb));
		GLES20.glDeleteFramebuffers(1, IntBuffer.wrap(fb));
		GLES20.glDeleteTextures(1, IntBuffer.wrap(renderTex));
		
	}
	
	public void updateScreen(int width, int height) {
		//this.screenWidth = width;
		//this.screenHeight = height;		
	}
	
	public void init(int width, int height)
	{
		//this.screenWidth = width;
		//this.screenHeight = height;
		
		this.width = width;
		this.height = height;		
		
		GLES20.glGenFramebuffers(1, fb, 0);
		
		GLES20.glGenRenderbuffers(1, depthRb, 0);
		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthRb[0]);
		Log.d("Sd3dFrameBuffer","fb="+fb[0]+" rb="+depthRb[0]);
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
		
	
		
		texBuffer = ByteBuffer.allocateDirect( this.width * this.height * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asIntBuffer();
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, this.width, this.height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, texBuffer);//GLES20.GL_UNSIGNED_SHORT_5_6_5, texBuffer);
		//GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, this.width, this.height, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5, texBuffer);//GLES20.GL_UNSIGNED_SHORT_5_6_5, texBuffer);
		
		// create render buffer and bind 16-bit depth buffer
		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthRb[0]);
		GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, this.width, this.height);	
		
		// check status
		int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
		if (status != GLES20.GL_FRAMEBUFFER_COMPLETE)
		{
			throw new RuntimeException("Error : FRAMEBUFFER UNAVAILABLE");

		}		
	}
	
	public void bind() {
		GLES20.glViewport(0, 0, width, height);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fb[0]);
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, renderTex[0], 0);
		GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, depthRb[0]);	
		
		// check status
		int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
		if (status != GLES20.GL_FRAMEBUFFER_COMPLETE)
		{
			throw new RuntimeException("Error : FRAMEBUFFER UNAVAILABLE");

		}			
	}
	
	public void unbind(int screenWidth, int screenHeight){
		GLES20.glViewport(0, 0, screenWidth, screenHeight);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
	}	
}
