package org.nicolasmy.sd3d.gfx;

//import java.awt.Image;
//import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.nicolasmy.sd3d.math.Sd3dMatrix;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
//import javax.imageio.*;

public class Sd3dMaterial
{
	public ByteBuffer mTextureData;
	public int mWidth;
	public int mHeight;
	public String mTextureName;
	public FloatBuffer mColors;	
	public boolean alphaBlending;
	public boolean alphaTest;
	public boolean renderLight;
	public int mColorName;
	
	
	
	
	public boolean isRenderLight() {
		return renderLight;
	}




	public void setRenderLight(boolean renderLight) {
		this.renderLight = renderLight;
	}




	public boolean isAlphaTest() {
		return alphaTest;
	}




	public void setAlphaTest(boolean alphaTest) {
		this.alphaTest = alphaTest;
	}




	public boolean useAlphaBlending() {
		return alphaBlending;
	}




	public void setAlphaBlending(boolean useAlphaBlending) {
		this.alphaBlending = useAlphaBlending;
	}




	public void generateCircleTexture(int radius,int width,byte r,byte g,byte b,byte a)
	{
		this.mWidth = width;
		this.mHeight = width;
		mTextureData = ByteBuffer.allocateDirect(width*width*4);
	
		mTextureData.order(ByteOrder.BIG_ENDIAN); 
		//IntBuffer ib = mTextureData.asIntBuffer();	
		
		
		float centerX = width/2.f;
		float centerY = width/2.f;		
		for (int i = 0;i < width;i++)
			for (int j = 0;j < width;j++)
			{
				if (Sd3dMatrix.distance2d(i, j, centerX, centerY) < radius)
				{
					mTextureData.put(r);
					mTextureData.put(g);
					mTextureData.put(b);
					mTextureData.put(a);
				}
				else
				{
					mTextureData.put((byte)0);
					mTextureData.put((byte)0);
					mTextureData.put((byte)0);
					mTextureData.put((byte)0);					
				}
			}
		
		mTextureData.position(0);
	}
	
	
	
	
	public void loadTexture(String filename) throws IOException
	{
		/*
		BufferedImage bmp = null;
		try
        {
		bmp= ImageIO.read(new File(filename));
		
        }
        catch (Exception ex) {}
        */
        
		//AssetManager am = Speedroid3D.getContext().getAssets();
		//InputStream fis = am.open(filename);		
		InputStream fis = Sd3dRessourceManager.getManager().getRessource(filename);
		Bitmap bmp = BitmapFactory.decodeStream(fis);
		//Bitmap bmp = BitmapFactory.decodeFile (filename);
       
        mTextureData = ByteBuffer.allocateDirect(bmp.getHeight() * bmp.getWidth() * 4);
			
		mTextureData.order(ByteOrder.BIG_ENDIAN); 
		IntBuffer ib = mTextureData.asIntBuffer(); 
		
		
		
		// Convert ARGB -> RGBA 
		
		for (int y = bmp.getHeight() - 1; y > -1; y--) 
		{ 
			for (int x = 0; x < bmp.getWidth(); x++) 
			{ 
				int pix = bmp.getPixel(x, bmp.getHeight() - y - 1); 
				//int pix = bmp.getRGB(x, bmp.getHeight() - y - 1); 
				int alpha = ((pix >> 24) & 0xFF); 
				int red = ((pix >> 16) & 0xFF); 
				int green = ((pix >> 8) & 0xFF); 
				int blue = ((pix) & 0xFF); 

				ib.put(red << 24 | green << 16 | blue << 8 | alpha); 
				//ib.put(0xffffffff);
			} 
		} 
			
			
		//mTextureData.put(mTextureData);
		mTextureData.position(0);	
		mWidth = bmp.getWidth();
		mHeight = bmp.getHeight();				
		
		
		fis.close();
	}	
	
	public void init(int verticeCount)
	{
		mColors = FloatBuffer.allocate(verticeCount*4);
	}
	
	public void setColor(float r,float g,float b,float a)
	{
		for(int i = 0;i < mColors.capacity()/4;i++)
		{
			
			mColors.put(r);
			mColors.put(g);
			mColors.put(b);
			mColors.put(a);
					
		}
		mColors.position(0);
	}
	
	public void copy(Sd3dMaterial material)
	{
		mColors = FloatBuffer.allocate(material.mColors.position());
				
		for (int j = 0; j < mColors.capacity();j++)
			mColors.put(material.mColors.get(j));	
		
		mColors.position(0);
		
		if (material.mTextureData != null)
		{
			this.mTextureData = ByteBuffer.allocate(material.mTextureData.capacity());
		
			for (int j = 0; j < mTextureData.capacity();j++)
				mTextureData.put(material.mTextureData.get(j));
		
			mTextureData.position(0);
		}
		
		this.mHeight = material.mHeight;
		
		this.mWidth = material.mWidth;
		
	}	
}
