package org.nicolasmy.sd3d.gfx.renderer;

import javax.microedition.khronos.opengles.GL11;

import org.nicolasmy.sd3d.gfx.Sd3dObject;
import org.nicolasmy.sd3d.gfx.Sd3dScene;

public interface Sd3dRendererInterface {

	public abstract void setGL11Context(GL11 gl);
	
	public abstract void renderScene(Sd3dScene scene);

	public abstract void sizeChanged(GL11 gl, int width, int height);

	public abstract void surfaceCreated(GL11 gl);
	
    public abstract int[] getConfigSpec();	
    
    public abstract void updateScreenResolution(int width, int height);
    
    public abstract void setTop(int top);
    
    public abstract void displayText(String text,Sd3dRenderer.ALIGN halign, Sd3dRenderer.ALIGN valign, float size);
    
    public abstract void invalidateRendererElements();
    
    public abstract void destroyRenderElement(Sd3dObject object);
    
    public abstract void pointToScreen(float x,float y,float z,float res[]);
    
    public abstract boolean pointInFrustum(float x, float y, float z);
    
    public abstract int getScreenWidth();
    
    public abstract int getScreenHeight(); 
    
    public abstract Sd3dObject pickAt(int x, int y, Sd3dScene scene);
}