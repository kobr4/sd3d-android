package org.nicolasmy.sd3d.interfaces;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public interface Sd3dRendererElementInterface {

	public void register();

	public void unregister();

	public void prerender(FloatBuffer camPosVector,float[] mVMatrix,float[] mVPMatrix,float[] projectionMatrix, float[] normalMatrix,IntBuffer renderStateVector);
	
	public void postrender();

}