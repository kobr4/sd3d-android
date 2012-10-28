package org.nicolasmy.sd3d.gfx.renderer;

import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL11;

import org.nicolasmy.sd3d.gfx.Sd3dObject;
import org.nicolasmy.sd3d.interfaces.Sd3dRendererElementInterface;

import android.opengl.GLES20;

public class Sd3dRendererElement {
	public int mVertexBufferName;
	//public int mNormalBufferName;		
	public int mIndiceBufferName;
	public int mIndiceCount;
	//public int mTexCoordBufferName;	
	//public int mColorBufferName;
	public int mTextureName;
	public float mScale;
	public float mPosition[];
	public float mOrientation[];
	public boolean mDisable;
	public boolean mAlphaBlending;
	public boolean mAlphaTest;
	public boolean mRenderLight;
	public boolean mIsBillboard;
	public boolean mIsPickable;
	public boolean mIsInScreenSpace;
	public byte mPickingColor[];
	public float mTransformMatrix[];
	public boolean mIsShadowVolume;
	public Sd3dObject mObject;		
	public Sd3dRendererElementInterface mRendererInterface;
}
