package org.nicolasmy.sd3d.gfx.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL11;

import org.nicolasmy.sd3d.Sd3dConfig;
import org.nicolasmy.sd3d.gfx.Sd3dRessourceManager;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

public class Sd3dSampling {
	private int fb [] = new int[1];
	private int depthRb[] = new int[1];
	private int renderTex[] = new int[1];
	private float ratio = 1.0f;	
	private int samplerWidth = 1024;
	private int samplerHeight = 1024;
	private IntBuffer texBuffer;	
	private static int FLOAT_SIZE_BYTES = 4;	
	private FloatBuffer vertexBuffer;	
	public void init(int screenWidth, int screenHeight)
	{
		if (Sd3dConfig.getString("sampling_ratio") != null)
			ratio = Float.parseFloat(Sd3dConfig.getString("sampling_ratio"));
		
		samplerWidth = (int) (ratio * screenWidth);
		samplerHeight = (int) (ratio * screenHeight);
		    	
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
	
		texBuffer = ByteBuffer.allocateDirect(samplerWidth * samplerHeight * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asIntBuffer();
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, samplerWidth, samplerHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, texBuffer);//GLES20.GL_UNSIGNED_SHORT_5_6_5, texBuffer);
		
		// create render buffer and bind 16-bit depth buffer
		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthRb[0]);
		GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, samplerWidth, samplerHeight);	
		
		// check status
		int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
		if (status != GLES20.GL_FRAMEBUFFER_COMPLETE)
		{
			Log.d("sd3d","Error : FRAMEBUFFER UNAVAILABLE");

			throw new RuntimeException("Error : FRAMEBUFFER UNAVAILABLE");

		}
	}
	
	public void onRenderScene()
	{
		GLES20.glViewport(0, 0, samplerWidth, samplerHeight);
		
		// bind the generated framebuffer
		//GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fb[0]);
		
		//GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_TEXTURE_2D, renderTex[0], 0);
		
		// specify texture as color attachment
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, renderTex[0], 0);
		
		// attach render buffer as depth buffer
		GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, depthRb[0]);
	}
	
	public void onRenderToScreen(int screenWidth, int screenHeight)
	{
		GLES20.glViewport(0, 0, screenWidth, screenHeight);
		//GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fb[0]);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
	}	
	
	public void drawSampler(int screenWidth, int screenHeight,Sd3dShader shader)
	{
		float vertices[] = {
				//0f, (float)this.screenHeight, 0f,        					  // V1 - first vertex (x,y,z)
				//(float)this.screenWidth, (float)this.screenHeight, 0f,        // V2 - second vertex
				0f,  0f,  0f,                                                 // V3 - third vertex		
				(float)screenWidth, (float)screenHeight, 0f,        // V2 - second vertex	
				0f, (float)screenHeight, 0f,        					  // V1 - first vertex (x,y,z)
				
				//(float)this.screenWidth, (float)this.screenHeight, 0f,        // V1 - first vertex (x,y,z)
				//(float)this.screenWidth, 0f,  0f,                             // V2 - second vertex
				0f,  0f,  0f,                                                  // V3 - third vertex			
				(float)screenWidth, 0f,  0f,                             // V2 - second vertex		
				(float)screenWidth, (float)screenHeight, 0f        // V1 - first vertex (x,y,z)				
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
		Matrix.setIdentityM(shader.modelMatrix, 0);
		//Matrix.setIdentityM(mViewMatrix, 0);
		
		shader.renderStateVector.put(2, 0);//NO LIGHT	
		shader.renderStateVector.put(1, 2);//HAS COLOR UNIFORM
		shader.renderStateVector.put(0, 0);//NO TEXTURE
		
		//Colors 
		shader.colorVector.put(0,0f);
		shader.colorVector.put(1,0f);
		shader.colorVector.put(2,0f);
		shader.colorVector.put(3,0.3f);
		
	    // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
	    // (which currently contains model * view).
	    //Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
	    //Matrix.multiplyMM(mMVMatrix, 0, mModelMatrix, 0,mViewMatrix, 0);
	 
	    // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
	    // (which now contains model * view * projection).
	    Matrix.multiplyMM(shader.MVPMatrix, 0, shader.projectionOrthoMatrix, 0, shader.modelMatrix, 0);			
	  

	    GLES20.glUseProgram(shader.getProgramHandle());
	    

	    GLES20.glUniformMatrix4fv(shader.getMVMatrixHandle(), 1, false, shader.MVMatrix, 0);
	    GLES20.glUniformMatrix4fv(shader.getMVPMatrixHandle(), 1, false, shader.MVPMatrix, 0);
	    GLES20.glUniform4iv(shader.getRenderStateVectorHandle(), 1, shader.renderStateVector);		
	    GLES20.glUniform4fv(shader.getColorVectorHandle(), 1, shader.colorVector);	
	    
		
		GLES20.glEnable(GL11.GL_BLEND);
		GLES20.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GLES20.glVertexAttribPointer(Sd3dShader.vertexPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
		GLES20.glEnableVertexAttribArray(Sd3dShader.vertexPositionHandle);
		
		
		GLES20.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
		
		GLES20.glDisableVertexAttribArray(Sd3dShader.vertexPositionHandle);
		
		GLES20.glDisable(GL11.GL_BLEND);	
	}
}
