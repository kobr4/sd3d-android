package org.nicolasmy.sd3d.gfx.renderer;

//import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

//import javax.imageio.ImageIO;
//import javax.media.opengl.GL2;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import org.nicolasmy.sd3d.gfx.Sd3dMaterial;
import org.nicolasmy.sd3d.gfx.Sd3dMesh;
import org.nicolasmy.sd3d.gfx.Sd3dObject;
import org.nicolasmy.sd3d.gfx.Sd3dRessourceManager;


//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;

public class Sd3dBmpFont extends Sd3dObject{
	private float charTexCoords[][];
	public  GL11 mGl;
	public int mTextureName;
	private ByteBuffer bbVertices;
	private ByteBuffer bbIndices;
	private ByteBuffer bbTexCoords;	
	private ByteBuffer bbColors;
	private FloatBuffer fbTexCoords;
	private int screenHeight;
	private int screenWidth;
	
	public ByteBuffer getBbVertices() {
		return bbVertices;
	}

	public void setVertices(ByteBuffer bbVertices) {
		this.bbVertices = bbVertices;
	}

	public ByteBuffer getIndices() {
		return bbIndices;
	}

	public void setIndices(ByteBuffer bbIndices) {
		this.bbIndices = bbIndices;
	}

	public ByteBuffer getTexCoords() {
		return bbTexCoords;
	}

	public void setTexCoords(ByteBuffer bbTexCoords) {
		this.bbTexCoords = bbTexCoords;
	}

	public ByteBuffer getColors() {
		return bbColors;
	}

	public void setColors(ByteBuffer bbColors) {
		this.bbColors = bbColors;
	}	
	
	public void setScreenProperties(int screenWidth,int screenHeight)
	{
		this.screenHeight = screenHeight;
		this.screenWidth = screenWidth;
	}
	
	Sd3dBmpFont(String filename, GL11 gl)
	{
		charTexCoords = new float[256][];
		
		charTexCoords['-'] = new float[4];
		charTexCoords['-'][0] =(float) 0.f;
		charTexCoords['-'][1] =(float) 0.f;
		charTexCoords['-'][2] =(float) 15./320.f;
		charTexCoords['-'][3] =(float) 16./52.f;

		charTexCoords['.'] = new float[4];
		charTexCoords['.'][0] =(float) 16./320.f;
		charTexCoords['.'][1] =(float) 0./52.f;
		charTexCoords['.'][2] =(float) 30./320.f;
		charTexCoords['.'][3] =(float) 16./52.f;

		charTexCoords['!'] = new float[4];
		charTexCoords['!'][0] =(float) 32./320.f;
		charTexCoords['!'][1] =(float) 0./52.f;
		charTexCoords['!'][2] =(float) 47./320.f;
		charTexCoords['!'][3] =(float) 16./52.f;

		charTexCoords['0'] = new float[4];
		charTexCoords['0'][0] =(float) 48./320.f;
		charTexCoords['0'][1] =(float) 0./52.f;
		charTexCoords['0'][2] =(float) 63./320.f;
		charTexCoords['0'][3] =(float) 16./52.f;

		charTexCoords['1'] = new float[4];
		charTexCoords['1'][0] =(float) 64./320.f;
		charTexCoords['1'][1] =(float) 0./52.f;
		charTexCoords['1'][2] =(float) 79./320.f;
		charTexCoords['1'][3] =(float) 16./52.f;

		charTexCoords['2'] = new float[4];
		charTexCoords['2'][0] =(float) 80./320.f;
		charTexCoords['2'][1] =(float) 0./52.f;
		charTexCoords['2'][2] =(float) 95./320.f;
		charTexCoords['2'][3] =(float) 16./52.f;

		charTexCoords['3'] = new float[4];		
		charTexCoords['3'][0] =(float) 96./320.f;
		charTexCoords['3'][1] =(float) 0./52.f;
		charTexCoords['3'][2] =(float) 111./320.f;
		charTexCoords['3'][3] =(float) 16./52.f;

		charTexCoords['4'] = new float[4];
		charTexCoords['4'][0] =(float) 112./320.f;
		charTexCoords['4'][1] =(float) 0./52.f;
		charTexCoords['4'][2] =(float) 128./320.f;
		charTexCoords['4'][3] =(float) 16./52.f;

		charTexCoords['5'] = new float[4];
		charTexCoords['5'][0] =(float) 129./320.f;
		charTexCoords['5'][1] =(float) 0./52.f;
		charTexCoords['5'][2] =(float) 143./320.f;
		charTexCoords['5'][3] =(float) 16./52.f;

		charTexCoords['6'] = new float[4];
		charTexCoords['6'][0] =(float) 144./320.f;
		charTexCoords['6'][1] =(float) 0./52.f;
		charTexCoords['6'][2] =(float) 159./320.f;
		charTexCoords['6'][3] =(float) 16./52.f;

		charTexCoords['7'] = new float[4];
		charTexCoords['7'][0] =(float) 160./320.f;
		charTexCoords['7'][1] =(float) 0./52.f;
		charTexCoords['7'][2] =(float) 175./320.f;
		charTexCoords['7'][3] =(float) 16./52.f;

		charTexCoords['8'] = new float[4];
		charTexCoords['8'][0] =(float) 176./320.f;
		charTexCoords['8'][1] =(float) 0./52.f;
		charTexCoords['8'][2] =(float) 191./320.f;
		charTexCoords['8'][3] =(float) 16./52.f;

		charTexCoords['9'] = new float[4];
		charTexCoords['9'][0] =(float) 192./320.f;
		charTexCoords['9'][1] =(float) 0./52.f;
		charTexCoords['9'][2] =(float) 207./320.f;
		charTexCoords['9'][3] =(float) 16./52.f;
		
		charTexCoords[':'] = new float[4];
		charTexCoords[':'][0] =(float) 208./320.f;
		charTexCoords[':'][1] =(float) 0./52.f;
		charTexCoords[':'][2] =(float) 223./320.f;
		charTexCoords[':'][3] =(float) 16./52.f;

		charTexCoords['\''] = new float[4];		
		charTexCoords['\''][0] =(float) 224./320.f;
		charTexCoords['\''][1] =(float) 0./52.f;
		charTexCoords['\''][2] =(float) 239./320.f;
		charTexCoords['\''][3] =(float) 16./52.f;

		charTexCoords['('] = new float[4];
		charTexCoords['('][0] =(float) 240./320.f;
		charTexCoords['('][1] =(float) 0./52.f;
		charTexCoords['('][2] =(float) 255./320.f;
		charTexCoords['('][3] =(float) 16./52.f;

		charTexCoords[')'] = new float[4];
		charTexCoords[')'][0] =(float) 256./320.f;
		charTexCoords[')'][1] =(float) 0./52.f;
		charTexCoords[')'][2] =(float) 271./320.f;
		charTexCoords[')'][3] =(float) 16./52.f;

		charTexCoords[','] = new float[4];
		charTexCoords[','][0] =(float) 272./320.f;
		charTexCoords[','][1] =(float) 0./52.f;
		charTexCoords[','][2] =(float) 287./320.f;
		charTexCoords[','][3] =(float) 16./52.f;

		charTexCoords['?'] = new float[4];
		charTexCoords['?'][0] =(float) 288./320.f;
		charTexCoords['?'][1] =(float) 0./52.f;
		charTexCoords['?'][2] =(float) 303./320.f;
		charTexCoords['?'][3] =(float) 16./52.f;

		charTexCoords[' '] = new float[4];
		charTexCoords[' '][0] =(float) 304./320.f;
		charTexCoords[' '][1] =(float) 0./52.f;
		charTexCoords[' '][2] =(float) 320./320.f;
		charTexCoords[' '][3] =(float) 16./52.f;

		charTexCoords['A'] = new float[4];
		charTexCoords['A'][0] =(float) 0./320.f;
		charTexCoords['A'][1] =(float) 17./52.f;
		charTexCoords['A'][2] =(float) 15./320.f;
		charTexCoords['A'][3] =(float) 32./52.f;

		charTexCoords['B'] = new float[4];
		charTexCoords['B'][0] =(float) 16./320.f;
		charTexCoords['B'][1] =(float) 17./52.f;
		charTexCoords['B'][2] =(float) 30./320.f;
		charTexCoords['B'][3] =(float) 32./52.f;

		charTexCoords['C'] = new float[4];
		charTexCoords['C'][0] =(float) 32./320.f;
		charTexCoords['C'][1] =(float) 17./52.f;
		charTexCoords['C'][2] =(float) 47./320.f;
		charTexCoords['C'][3] =(float) 32./52.f;

		charTexCoords['D'] = new float[4];
		charTexCoords['D'][0] =(float) 48./320.f;
		charTexCoords['D'][1] =(float) 17./52.f;
		charTexCoords['D'][2] =(float) 63./320.f;
		charTexCoords['D'][3] =(float) 32./52.f;

		charTexCoords['E'] = new float[4];
		charTexCoords['E'][0] =(float) 64./320.f;
		charTexCoords['E'][1] =(float) 17./52.f;
		charTexCoords['E'][2] =(float) 79./320.f;
		charTexCoords['E'][3] =(float) 32./52.f;

		charTexCoords['F'] = new float[4];
		charTexCoords['F'][0] =(float) 80./320.f;
		charTexCoords['F'][1] =(float) 17./52.f;
		charTexCoords['F'][2] =(float) 95./320.f;
		charTexCoords['F'][3] =(float) 32./52.f;

		charTexCoords['G'] = new float[4];
		charTexCoords['G'][0] =(float) 96./320.f;
		charTexCoords['G'][1] =(float) 17./52.f;
		charTexCoords['G'][2] =(float) 111./320.f;
		charTexCoords['G'][3] =(float) 32./52.f;

		charTexCoords['H'] = new float[4];
		charTexCoords['H'][0] =(float) 112./320.f;
		charTexCoords['H'][1] =(float) 17./52.f;
		charTexCoords['H'][2] =(float) 128./320.f;
		charTexCoords['H'][3] =(float) 32./52.f;

		charTexCoords['I'] = new float[4];
		charTexCoords['I'][0] =(float) 129./320.f;
		charTexCoords['I'][1] =(float) 17./52.f;
		charTexCoords['I'][2] =(float) 143./320.f;
		charTexCoords['I'][3] =(float) 32./52.f;

		charTexCoords['J'] = new float[4];
		charTexCoords['J'][0] =(float) 144./320.f;
		charTexCoords['J'][1] =(float) 17./52.f;
		charTexCoords['J'][2] =(float) 159./320.f;
		charTexCoords['J'][3] =(float) 32./52.f;

		charTexCoords['K'] = new float[4];
		charTexCoords['K'][0] =(float) 160./320.f;
		charTexCoords['K'][1] =(float) 17./52.f;
		charTexCoords['K'][2] =(float) 175./320.f;
		charTexCoords['K'][3] =(float) 32./52.f;

		charTexCoords['L'] = new float[4];
		charTexCoords['L'][0] =(float) 176./320.f;
		charTexCoords['L'][1] =(float) 17./52.f;
		charTexCoords['L'][2] =(float) 191./320.f;
		charTexCoords['L'][3] =(float) 32./52.f;

		charTexCoords['M'] = new float[4];
		charTexCoords['M'][0] =(float) 192./320.f;
		charTexCoords['M'][1] =(float) 17./52.f;
		charTexCoords['M'][2] =(float) 207./320.f;
		charTexCoords['M'][3] =(float) 32./52.f;

		charTexCoords['N'] = new float[4];
		charTexCoords['N'][0] =(float) 208./320.f;
		charTexCoords['N'][1] =(float) 17./52.f;
		charTexCoords['N'][2] =(float) 223./320.f;
		charTexCoords['N'][3] =(float) 32./52.f;

		charTexCoords['O'] = new float[4];
		charTexCoords['O'][0] =(float) 224./320.f;
		charTexCoords['O'][1] =(float) 17./52.f;
		charTexCoords['O'][2] =(float) 239./320.f;
		charTexCoords['O'][3] =(float) 32./52.f;

		charTexCoords['P'] = new float[4];
		charTexCoords['P'][0] =(float) 240./320.f;
		charTexCoords['P'][1] =(float) 17./52.f;
		charTexCoords['P'][2] =(float) 255./320.f;
		charTexCoords['P'][3] =(float) 32./52.f;

		charTexCoords['Q'] = new float[4];
		charTexCoords['Q'][0] =(float) 256./320.f;
		charTexCoords['Q'][1] =(float) 17./52.f;
		charTexCoords['Q'][2] =(float) 271./320.f;
		charTexCoords['Q'][3] =(float) 32./52.f;

		charTexCoords['R'] = new float[4];
		charTexCoords['R'][0] =(float) 272./320.f;
		charTexCoords['R'][1] =(float) 17./52.f;
		charTexCoords['R'][2] =(float) 287./320.f;
		charTexCoords['R'][3] =(float) 32./52.f;

		charTexCoords['S'] = new float[4];
		charTexCoords['S'][0] =(float) 288./320.f;
		charTexCoords['S'][1] =(float) 17./52.f;
		charTexCoords['S'][2] =(float) 303./320.f;
		charTexCoords['S'][3] =(float) 32./52.f;

		charTexCoords['T'] = new float[4];
		charTexCoords['T'][0] =(float) 304./320.f;
		charTexCoords['T'][1] =(float) 17./52.f;
		charTexCoords['T'][2] =(float) 320./320.f;
		charTexCoords['T'][3] =(float) 32./52.f;

		charTexCoords['U'] = new float[4];
		charTexCoords['U'][0] =(float) 0./320.f;
		charTexCoords['U'][1] =(float) 34./52.f;
		charTexCoords['U'][2] =(float) 15./320.f;
		charTexCoords['U'][3] =(float) 50./52.f;

		charTexCoords['V'] = new float[4];
		charTexCoords['V'][0] =(float) 16./320.f;
		charTexCoords['V'][1] =(float) 34./52.f;
		charTexCoords['V'][2] =(float) 30./320.f;
		charTexCoords['V'][3] =(float) 50./52.f;

		charTexCoords['W'] = new float[4];
		charTexCoords['W'][0] =(float) 32./320.f;
		charTexCoords['W'][1] =(float) 34./52.f;
		charTexCoords['W'][2] =(float) 47./320.f;
		charTexCoords['W'][3] =(float) 50./52.f;

		charTexCoords['X'] = new float[4];
		charTexCoords['X'][0] =(float) 48./320.f;
		charTexCoords['X'][1] =(float) 34./52.f;
		charTexCoords['X'][2] =(float) 63./320.f;
		charTexCoords['X'][3] =(float) 50./52.f;

		charTexCoords['Y'] = new float[4];
		charTexCoords['Y'][0] =(float) 64./320.f;
		charTexCoords['Y'][1] =(float) 34./52.f;
		charTexCoords['Y'][2] =(float) 79./320.f;
		charTexCoords['Y'][3] =(float) 50./52.f;

		charTexCoords['Z'] = new float[4];
		charTexCoords['Z'][0] =(float) 80./320.f;
		charTexCoords['Z'][1] =(float) 34./52.f;
		charTexCoords['Z'][2] =(float) 95./320.f;
		charTexCoords['Z'][3] =(float) 50./52.f;
	
		mMeshCount = 1;
		mMesh = new Sd3dMesh[1];
		mMesh[0] = new Sd3dMesh();
		
		mMaterial = new Sd3dMaterial[1];
		mMaterial[0] = new Sd3dMaterial();		
		

		bbIndices = ByteBuffer.allocateDirect(2*6*200);
		bbIndices.order(ByteOrder.nativeOrder());
		mMesh[0].mIndices = bbIndices.asCharBuffer();
		//mMesh[0].mIndices = CharBuffer.allocate(6*200);
		
		bbVertices = ByteBuffer.allocateDirect(4*4*3*200);
		bbVertices.order(ByteOrder.nativeOrder());
		//mMesh[0].mVertices = FloatBuffer.allocate(4*3*200);
		mMesh[0].mVertices = bbVertices.asFloatBuffer();
		
		bbTexCoords = ByteBuffer.allocateDirect(4*4*2*200);
		bbTexCoords.order(ByteOrder.nativeOrder());
	    //mMesh[0].mTexCoords = bbTexCoords.asFloatBuffer();
		fbTexCoords = bbTexCoords.asFloatBuffer();
		//mMesh[0].mTexCoords = FloatBuffer.allocate(4*2*200);
		
		bbColors = ByteBuffer.allocateDirect(4*4*4*200);
		bbColors.order(ByteOrder.nativeOrder());
		mMaterial[0].mColors = bbColors.asFloatBuffer();
		
		//mMaterial[0].mColors = FloatBuffer.allocate(4*4*200);
		
		mGl = gl;
		
		mMesh[0].mIndices.position(0);
		mMesh[0].mVertices.position(0);
		//mMesh[0].mTexCoords.position(0);
		fbTexCoords.position(0);
		mMaterial[0].mColors.position(0);
		
		try {
			loadTexture(filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void resetTextBuffer()
	{
		/*
		mMesh[0].mIndices.clear();
		mMesh[0].mVertices.clear();
		mMesh[0].mTexCoords.clear();
		mMaterial[0].mColors.clear();
		*/
		
		mMesh[0].mIndices.position(0);
		mMesh[0].mVertices.position(0);
		fbTexCoords.position(0);
		mMaterial[0].mColors.position(0);
	}
	
	public void addQuad(float ptQuad[], char c)
	{
		float height = 0.f;

		
		//0
		mMesh[0].mVertices.put(ptQuad[0]);
		
		mMesh[0].mVertices.put(ptQuad[1]);
		mMesh[0].mVertices.put(height);
		//Normals
		//mMesh[0].mVertices.put(0f);
		//mMesh[0].mVertices.put(0f);
		//mMesh[0].mVertices.put(0f);		
		//TexCoords
		fbTexCoords.put(charTexCoords[c][0]);
		fbTexCoords.put(charTexCoords[c][1]);
		
		mMaterial[0].mColors.put(1.f);
		mMaterial[0].mColors.put(1.f);
		mMaterial[0].mColors.put(0.f);
		mMaterial[0].mColors.put(1.f);				

		//1
		mMesh[0].mVertices.put(ptQuad[2]);
		mMesh[0].mVertices.put(ptQuad[3]);		
		mMesh[0].mVertices.put(height);		

		//mMesh[0].mTexCoords.put(charTexCoords[c][0]);
		//mMesh[0].mTexCoords.put(charTexCoords[c][3]);						
		
		fbTexCoords.put(charTexCoords[c][2]);
		fbTexCoords.put(charTexCoords[c][1]);
		
		mMaterial[0].mColors.put(1.f);
		mMaterial[0].mColors.put(1.f);
		mMaterial[0].mColors.put(0.f);
		mMaterial[0].mColors.put(1.f);				
		
		//2
		mMesh[0].mVertices.put(ptQuad[4]);		
		mMesh[0].mVertices.put(ptQuad[5]);
		mMesh[0].mVertices.put(height);		

		//mMesh[0].mTexCoords.put(charTexCoords[c][2]);
		//mMesh[0].mTexCoords.put(charTexCoords[c][3]);
		fbTexCoords.put(charTexCoords[c][2]);
		fbTexCoords.put(charTexCoords[c][3]);		
			
		mMaterial[0].mColors.put(1.0f);
		mMaterial[0].mColors.put(0.5f);
		mMaterial[0].mColors.put(0.f);
		mMaterial[0].mColors.put(1.f);			
		
		//3
		mMesh[0].mVertices.put(ptQuad[6]);
		
		mMesh[0].mVertices.put(ptQuad[7]);
		mMesh[0].mVertices.put(height);		

		//mMesh.mTexCoords.put(charTexCoords[c][2]);
		//mMesh.mTexCoords.put(charTexCoords[c][1]);
		fbTexCoords.put(charTexCoords[c][0]);
		fbTexCoords.put(charTexCoords[c][3]);		
			
		mMaterial[0].mColors.put(1.f);
		mMaterial[0].mColors.put(0.5f);
		mMaterial[0].mColors.put(0.0f);
		mMaterial[0].mColors.put(1.f);			
		
		
		char pos = (char)((mMesh[0].mVertices.position()) / 3);
		
		mMesh[0].mIndices.put((char)(pos-4));//6	
		mMesh[0].mIndices.put((char)(pos-3));//3		
		mMesh[0].mIndices.put((char)(pos-2));//0
		
		/*
		mMesh[0].mIndices.put((char)(pos-2));//0
		mMesh[0].mIndices.put((char)(pos-3));//3
		mMesh[0].mIndices.put((char)(pos-4));//6
		*/
		/*
		mMesh.mIndices.put((char)(pos-2));//0
		mMesh.mIndices.put((char)(pos-3));//3
		mMesh.mIndices.put((char)(pos-4));//6		
		*/
	
		/*
		mMesh[0].mIndices.put((char)(pos-4));//6			
		mMesh[0].mIndices.put((char)(pos-1));//3	
		mMesh[0].mIndices.put((char)(pos-2));//9		
		*/
		
		mMesh[0].mIndices.put((char)(pos-2));//9	
		mMesh[0].mIndices.put((char)(pos-1));//3		
		mMesh[0].mIndices.put((char)(pos-4));//6	
		
	}	
	
	public void loadTexture(String filename) throws IOException
	{
		if (mMaterial != null)
		{
			//Bitmap bmp = BitmapFactory.decodeFile (filename);
			/*
			BufferedImage bmp = null;
			try
			{
				bmp= ImageIO.read(new File(filename));
	        }
	        catch (Exception ex) {
	        	
	        	System.out.println("Error could not load texture:"+filename);
	        }	
	        */	
			//AssetManager am = Speedroid3D.getContext().getAssets();
			//InputStream fis = am.open(filename);		
			InputStream fis = Sd3dRessourceManager.getManager().getRessource(filename);
			Bitmap bmp = BitmapFactory.decodeStream(fis);	
			
			
			mMaterial[0].mTextureData = ByteBuffer.allocate(bmp.getHeight() * bmp.getWidth() * 4);
			
			mMaterial[0].mTextureData.order(ByteOrder.BIG_ENDIAN); 
			IntBuffer ib = mMaterial[0].mTextureData.asIntBuffer(); 
			
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
				} 
			} 
							
			
			
			//mObject.mMaterial.mTextureData.put(textureData);
			mMaterial[0].mTextureData.position(0);	
			mMaterial[0].mWidth = bmp.getWidth();
			mMaterial[0].mHeight = bmp.getHeight();
			
		}
	}	
	
	public void addTextToBuffer(String text,Sd3dRenderer.ALIGN halign, Sd3dRenderer.ALIGN valign, float size)
	{
		float x = 0.f;
		float y = 0.f;
		switch(halign)
		{
		case LEFT:
			x = 0f;
			break;
		case RIGHT:
			x = (float)this.screenWidth - text.length() * size;
			break;
		case CENTER:
			x = ((float)this.screenWidth - text.length() * size)/2.f; 
			break;
		}

		switch(valign)
		{
		case TOP:
			y = 0f;
			break;
		case BOTTOM:
			y = (float)this.screenHeight - size;
			break;
		case CENTER:
			y = ((float)this.screenHeight/2.f); 
			break;
		}		
		
		this.addTextToBuffer(text, y, x, size);
	}
	
	public void addTextToBuffer(String text,float x, float y,float size)
	{
		
		float ptQuad[] = new float[8];
		for (int i = 0;i < text.length();i++)
		{
			char c = text.charAt(i);
			
			
			ptQuad[0] = y + i * size;
			ptQuad[1] = x;
			ptQuad[2] = y + (i+1) * size;
			ptQuad[3] = x;

			ptQuad[4] = y + (i+1) * size;
			ptQuad[5] = x+size;
			ptQuad[6] = y + i * size;
			ptQuad[7] = x+size;
					
			addQuad(ptQuad, c);
		}
	}
	
	public void registerTexture()
	{
		//OpenGL stuffs	
		IntBuffer buffer = IntBuffer.allocate(1);		
		
		if (mMaterial[0].mTextureData != null)
		{
			mGl.glGenTextures(1, buffer); 
			mGl.glBindTexture(GL11.GL_TEXTURE_2D, buffer.get(0)); 
			mGl.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA,mMaterial[0].mWidth, mMaterial[0].mHeight, 0, 
					GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, mMaterial[0].mTextureData);
			mGl.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST); 
			mGl.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST); 	
			
			mTextureName = buffer.get(0);
			
			System.out.println("BMP font texture: "+mTextureName);
		}		
	}
	
	public void renderText()
	{
		if (mTextureName == 0)
			registerTexture();
		
		if (mMesh[0].mVertices.position() != 0)
		{
			mGl.glEnable (GL11.GL_BLEND);
			//mGl.glBlendFunc (GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_COLOR);
			mGl.glBlendFunc (GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			//mGl.glEnable (GL11.GL_ALPHA_TEST);
			//mGl.glAlphaFunc(GL11.GL_NOTEQUAL, 0.f);
			
			mGl.glEnable(GL11.GL_TEXTURE_2D);
			
			
			
			//OpenGL stuff
	        /*		
			IntBuffer buffer = IntBuffer.allocate(1);
			
			GLES20.glGenBuffers(1,buffer);
			GLES20.glBindBuffer(GL11.GL_ARRAY_BUFFER, buffer.get(0));
			GLES20.glBufferData(GL11.GL_ARRAY_BUFFER, mMesh[0].mVertices.capacity() * 4,mMesh[0].mVertices,GLES20.GL_STATIC_DRAW);
				//element.mVertexBufferName = buffer.get(0);
				*/
			
	        mGl.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);  
	        mGl.glEnableClientState(GL11.GL_VERTEX_ARRAY);
	        mGl.glEnableClientState(GL11.GL_COLOR_ARRAY);  			
			
			mGl.glEnableClientState(GL11.GL_COLOR_ARRAY); 
			mGl.glActiveTexture(GL11.GL_TEXTURE0);
			mGl.glBindTexture(GL11.GL_TEXTURE_2D, mTextureName);
			
		
			mGl.glVertexPointer(3, GL11.GL_FLOAT, 0, bbVertices);
			//bbVertices.position(3*4);
			mGl.glTexCoordPointer(2, GL11.GL_FLOAT, 0, bbTexCoords);
			  
			mGl.glColorPointer(4, GL11.GL_FLOAT, 0, bbColors);

			
			
			
			
			mGl.glDrawElements(GL11.GL_TRIANGLES, mMesh[0].mIndices.position(),GL11.GL_UNSIGNED_SHORT, bbIndices);	
			
			
			//GLES20.glDeleteBuffers(1, buffer.array(), 0);
			//mGl.glDisable (GL11.GL_ALPHA_TEST);
			mGl.glDisable (GL11.GL_BLEND);
					
		}
	}
}
