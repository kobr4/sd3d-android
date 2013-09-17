package org.nicolasmy.sd3d.gfx;

//import java.awt.Image;
//import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.nicolasmy.sd3d.utils.TargaReader;
import org.nicolasmy.sd3d.math.Sd3dMatrix;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
//import javax.imageio.*;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class Sd3dMaterial
{
	public ByteBuffer mTextureData;
	public ByteBuffer mSecondaryTextureData;
	public int mWidth;
	public int mHeight;
	public int mTextureName[] = new int[2];
	public FloatBuffer mColors;	
	public boolean alphaBlending;
	public boolean alphaTest;
	public boolean renderLight;
	public int mColorName;
	private String mShaderName;
	
	
	public void setshaderName(String shaderName) {
		this.mShaderName = shaderName;
	}
	
	public String getShaderName() {
		return this.mShaderName;
	}
	
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
	
	
	public void loadTGATextureFromZip(String zip, String filename)
	{
		try {
			InputStream fis = Sd3dRessourceManager.getManager().getRessource(zip);

			ZipInputStream zis = new ZipInputStream(fis);
			ZipEntry e;
			while ((e = zis.getNextEntry()) != null)
			{
				//System.out.println("zip entry: " + e.getName().replaceAll("/", "").replaceAll("\\\\", ""));
				//System.out.println("zip entry: " + filename.replaceAll("/", "").replaceAll("\\\\", "") );
				if (e.getName().toUpperCase().replaceAll("/", "").replaceAll("\\\\", "").equals(filename.toUpperCase().replaceAll("/", "").replaceAll("\\\\", "")))
				{
					System.out.println("Texture: "+e.getName()+" Size: "+e.getSize());
					loadTGATexture(zis,(int)e.getSize());
					zis.closeEntry();
				}				
			}
			
			fis.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}			
	}
	
	public void loadTGATexture(InputStream is, int len) throws IOException
	{
		
		Bitmap bmp = TargaReader.getImage(is, len);
		//is.close();
		
        mTextureData = ByteBuffer.allocateDirect(bmp.getHeight() * bmp.getWidth() * 4);
		
		mTextureData.order(ByteOrder.BIG_ENDIAN); 
		IntBuffer ib = mTextureData.asIntBuffer(); 
				
		// Convert ARGB -> RGBA 
		
		//for (int y = bmp.getHeight() - 1; y > -1; y--) 
		
			for (int x = bmp.getHeight() - 1; x >= 0; x--) 
		{ 
				for (int y = 0; y < bmp.getWidth(); y++) 
			{ 
				int pix = bmp.getPixel(y, x); 
				//int pix = bmp.getRGB(x, bmp.getHeight() - y - 1); 
				
				int alpha = ((pix >> 24) & 0xFF); 
				int red = ((pix >> 16) & 0xFF); 
				int green = ((pix >> 8) & 0xFF); 
				int blue = ((pix) & 0xFF); 

				ib.put(red << 24 | green << 16 | blue << 8 | alpha); 
				//ib.put(0xffffffff);
				//ib.put(pix);
			} 
		} 
			
			
		//mTextureData.put(mTextureData);
		mTextureData.position(0);	
		mWidth = bmp.getWidth();
		mHeight = bmp.getHeight();						
	}
	
	
	public void loadTGATexture(String filename) throws IOException
	{
		InputStream fis = Sd3dRessourceManager.getManager().getRessource(filename);
		
		int len = 0;
		int r = 0;
		while (r != -1)
		{
			r = fis.read();
			len++;
		}
		fis.close();
		
		fis = Sd3dRessourceManager.getManager().getRessource(filename);
		Bitmap bmp = TargaReader.getImage(fis, len);
		fis.close();
		
        mTextureData = ByteBuffer.allocateDirect(bmp.getHeight() * bmp.getWidth() * 4);
		
		mTextureData.order(ByteOrder.BIG_ENDIAN); 
		IntBuffer ib = mTextureData.asIntBuffer(); 
				
		// Convert ARGB -> RGBA 
		
		//for (int y = bmp.getHeight() - 1; y > -1; y--) 
		
			for (int x = bmp.getWidth() - 1; x >= 0; x--) 
		{ 
				for (int y = 0; y < bmp.getHeight(); y++) 
			{ 
				int pix = bmp.getPixel(y, x); 
				//int pix = bmp.getRGB(x, bmp.getHeight() - y - 1); 
				
				int alpha = ((pix >> 24) & 0xFF); 
				int red = ((pix >> 16) & 0xFF); 
				int green = ((pix >> 8) & 0xFF); 
				int blue = ((pix) & 0xFF); 

				ib.put(red << 24 | green << 16 | blue << 8 | alpha); 
				//ib.put(0xffffffff);
				//ib.put(pix);
			} 
		} 
			
			
		//mTextureData.put(mTextureData);
		mTextureData.position(0);	
		mWidth = bmp.getWidth();
		mHeight = bmp.getHeight();						
		
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
	
	public void fromString(String text, int width, int height, float textSize, int textColor) {
	    Paint paint = new Paint();
	    paint.setTextSize(textSize);
	    paint.setColor(textColor);
	    paint.setTextAlign(Paint.Align.CENTER);
	    int text_width = (int) (paint.measureText(text) + 0.5f); // round
	    float baseline = -(int) (paint.ascent() + 0.5f);
	    int text_height = (int) (baseline + paint.descent() + 0.5f);
	    Log.d("TEXT","w="+text_width+" h="+text_height);
	    Bitmap bmp = Bitmap.createBitmap(text_width, text_height, Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(bmp);
	    canvas.drawText(text, text_width/2, baseline, paint);	
	    
	    
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

		mTextureData.position(0);	
		mWidth = bmp.getWidth();
		mHeight = bmp.getHeight();		    
	}	
}
