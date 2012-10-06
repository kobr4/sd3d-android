package org.nicolasmy.sd3d.interfaces;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public interface Sd3dRendererElementInterface {

	public abstract void register();

	public abstract void unregister();

	public abstract void prerender(FloatBuffer camPosVector,float[] mVMatrix,float[] mVPMatrix,float[] projectionMatrix, float[] normalMatrix,IntBuffer renderStateVector);
	
	public abstract void postrender();

}