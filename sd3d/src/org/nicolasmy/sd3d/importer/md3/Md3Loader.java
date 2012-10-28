package org.nicolasmy.sd3d.importer.md3;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.util.Log;


public class Md3Loader {
	public static String TAG_TORSO = "tag_torso";
	public static String TAG_HEAD = "tag_head";
	private float scale = 0.1f;	
	private static String skinFiles[] = {"head_default.skin", "lower_default.skin", "upper_default.skin"};
	private TreeMap<Integer,Method> map = new TreeMap<Integer,Method>();
	private HashMap <String, String> skinMap = new HashMap<String, String>();
	
	private int unsignedByteToInt(byte b) {
		return (int) b & 0xFF;
	}		
	private int read16(byte buffer[], int offset)
	{
		int res;
		int res2;
		res = unsignedByteToInt(buffer[offset]);
		res2 = unsignedByteToInt(buffer[offset+1]) << 8;
		res = res | res2;
		return res;	
	}
	
	private int signedread16(byte buffer[], int offset)
	{
		int res = read16(buffer, offset);
		
		if (res > 0x7fff)
		{
			//res = -(res ^ 0xffff);
			res = -(0xffff - res);		}
		return res;	
	}
	
	
	private int read32(byte buffer[], int offset)
	{
		int res = 0;
		res = unsignedByteToInt(buffer[offset]);
		res = res | (unsignedByteToInt(buffer[offset+1]) << 8);
		res = res | (unsignedByteToInt(buffer[offset+2]) << 16);
		res = res | (unsignedByteToInt(buffer[offset+3]) << 24);			
		return res;	
	}	
	private String getString(byte[] input)
	{
		String s = "";
		try {
			s = new String( input , "ASCII" );
			if (s.indexOf(0) != -1)
			{
				s = s.substring(0,s.indexOf(0));
			}
		} catch (UnsupportedEncodingException e) {
			System.out.println("Error while decoding array "+e.toString());
		}	
		
		return s;
	}
	
	private int getInt(byte[] input)
	{
		int v = 0;
		v = read32(input,0);
		return v;
	}
	
	private void readData(InputStream is, byte[] arg)
	{
		try {
			/*
			while (is.available() < arg.length)
			{
				System.out.println("Not engough available data");
			}
			*/
			for (int i = 0;i < arg.length;i++)
				arg[i] = (byte)is.read();
			/*
			int len = is.read(arg,0,arg.length);
			if (len != arg.length)
			{
				System.out.println("Error while reading data "+len+" "+arg.length
						+" "+is.available());
				System.exit(0);
				
			}
			*/
			readCount += arg.length;
		} catch (IOException e) {
			System.out.println("Error while reading file "+e.toString());
		}		
	}
	
	private void skip(InputStream is,int count)
	{
		//System.out.println("Skipping data: "+ count);
		for (int i = 0; i < count;i++)
		{
			readData(is,read8);
		}
	}
	
//
//Data Type	Name	Description
//-	MD3_START	Offset of MD3 object. Usually 0, but not guaranteed.
//S32	IDENT	Magic number. As a string of 4 octets, reads "IDP3"; as unsigned little-endian 860898377 (0x33504449); as unsigned big-endian 1229213747 (0x49445033).
//S32	VERSION	MD3 version number, latest known is 15, but use the constant MD3_VERSION.
//U8 * 64	NAME	MD3 name, usually its pathname in the PK3. ASCII character string, NULL-terminated (C-style).
//S32	FLAGS	 ???
//S32	NUM_FRAMES	Number of Frame objects, with a maximum of MD3_MAX_FRAMES. Current value of MD3_MAX_FRAMES is 1024.
//S32	NUM_TAGS	Number of Tag objects, with a maximum of MD3_MAX_TAGS. Current value of MD3_MAX_TAGS is 16. There is one set of tags per frame so the total number of tags to read is (NUM_TAGS * NUM_FRAMES).
//S32	NUM_SURFACES	Number of Surface objects, with a maximum of MD3_MAX_SURFACES. Current value of MD3_MAX_SURFACES is 32.
//S32	NUM_SKINS	Number of Skin objects. I should note that I have not seen an MD3 using this particular field for anything; this appears to be an artifact from the Quake 2 MD2 format. Surface objects have their own Shader field.
//S32	OFS_FRAMES	Relative offset from start of MD3 object where Frame objects start. The Frame objects are written sequentially, that is, when you read one Frame object, you do not need to seek() for the next object.
//S32	OFS_TAGS	Relative offset from start of MD3 where Tag objects start. Similarly written sequentially.
//S32	OFS_SURFACES	Relative offset from start of MD3 where Surface objects start. Again, written sequentially.
//S32	OFS_EOF	Relative offset from start of MD3 to the end of the MD3 object. Note there is no offset for Skin objects.
// !	(Frame)	The array of Frame objects usually starts immediately afterwards, but OFS_FRAMES should be used.
// !	(Tag)	The array of Tag objects usually starts immediately after FRAMES, but OFS_TAGS should be used.
// !	(Surface)	The array of Surface objects usually start after TAGS, but OFS_SURFACES should be used.
//-	MD3_END	End of MD3 object. Should match OFS_EOF.
//	
	
	private void loadHeader(InputStream input,Md3LoaderAdapterInterface md3loader)
	{
		readCount = 0;
		
		//Read magic
		readData(input,read32);
		
		//Should be IDP3
		System.out.println("MAGIC:"+getString(read32));
		
		//Read version
		readData(input,read32);
		System.out.println("VERSION:"+getInt(read32));
		
		//Read name
		readData(input,readName);
		System.out.println("NAME:"+getString(readName));
		
		//Read flags
		readData(input,read32);
		
		//Read frames number
		readData(input,read32);
		System.out.println("FRAME NUMBER:"+getInt(read32));		
		frameNumber = getInt(read32);
		md3loader.setFrameNumberHeader(frameNumber);
		
		//Read tags
		readData(input,read32);
		System.out.println("TAGS NUMBER:"+getInt(read32));		
		tagNumber = getInt(read32);
		md3loader.setTagNumber(tagNumber);
		
		//Read surfaces
		readData(input,read32);
		System.out.println("SURFACES NUMBER:"+getInt(read32));	
		surfaceNumber = getInt(read32);
		md3loader.setSurfaceNumber(surfaceNumber);
		
		//Read skins
		readData(input,read32);
		System.out.println("SKINS NUMBER:"+getInt(read32));	
		
		//Read offset frames
		readData(input,read32);
		System.out.println("Frame Offset:"+getInt(read32));
		
		frameOffset = getInt(read32);
	
		//Read offset tags
		readData(input,read32);
		System.out.println("tags Offset:"+getInt(read32));			
		tagOffset = getInt(read32);
		
		//Read offset surfaces
		readData(input,read32);
		System.out.println("Surface Offset:"+getInt(read32));		
		
		surfaceOffset = getInt(read32);
		
		//Skipping following data to frame data
		this.skip(input, frameOffset - readCount);
		
		this.skip(input, tagOffset - readCount);
		loadTags(input,md3loader);
		

	}
	
	private void loadFrame(InputStream input)
	{
		this.skip(input, surfaceOffset - readCount);
	}
	
	private void loadTags(InputStream input,Md3LoaderAdapterInterface md3loader)
	{
		for (int k = 0;k < frameNumber;k++)
		{
			for (int i = 0;i < tagNumber;i++)
			{
				readData(input,readName);
				System.out.println("NAME:"+getString(readName));		
				
				readData(input,read32);
				float x = Float.intBitsToFloat(getInt(read32));
				
				readData(input,read32);
				float y = Float.intBitsToFloat(getInt(read32));
				
				readData(input,read32);
				float z = Float.intBitsToFloat(getInt(read32));		
				
				System.out.println("TAG "+i+": "+x+" "+y+" "+z);
				
				for (int j = 0;j < 9;j++)
				{
					readData(input,read32);
					rotMatrix[j] = Float.intBitsToFloat(getInt(read32));
					//Log.d("sd3d","MATRIX"+j+" "+Float.intBitsToFloat(getInt(read32)));
				}	
				
				md3loader.addTag(k,i,getString(readName),scale*x,scale*y,scale*z, rotMatrix);
			}
		}
	}
	

//Data Type	Name	Description
//-	SURFACE_START	Offset relative to start of MD3 object.
//S32	IDENT	Magic number. As a string of 4 octets, reads "IDP3"; as unsigned little-endian 860898377 (0x33504449); as unsigned big-endian 1229213747 (0x49445033).
//U8 * 64	NAME	Name of Surface object. ASCII character string, NUL-terminated (C-style).
//S32	FLAGS	 ???
//S32	NUM_FRAMES	Number of animation frames. This should match NUM_FRAMES in the MD3 header.
//S32	NUM_SHADERS	Number of Shader objects defined in this Surface, with a limit of MD3_MAX_SHADERS. Current value of MD3_MAX_SHADERS is 256.
//S32	NUM_VERTS	Number of Vertex objects defined in this Surface, up to MD3_MAX_VERTS. Current value of MD3_MAX_VERTS is 4096.
//S32	NUM_TRIANGLES	Number of Triangle objects defined in this Surface, maximum of MD3_MAX_TRIANGLES. Current value of MD3_MAX_TRIANGLES is 8192.
//S32	OFS_TRIANGLES	Relative offset from SURFACE_START where the list of Triangle objects starts.
//S32	OFS_SHADERS	Relative offset from SURFACE_START where the list of Shader objects starts.
//S32	OFS_ST	Relative offset from SURFACE_START where the list of ST objects (s-t texture coordinates) starts.
//S32	OFS_XYZNORMAL	Relative offset from SURFACE_START where the list of Vertex objects (X-Y-Z-N vertices) starts.
//S32	OFS_END	Relative offset from SURFACE_START to where the Surface object ends.
// !	(Shader)	List of Shader objects usually starts immediate after the Surface header, but use OFS_SHADERS (or rather, OFS_SHADERS+SURFACE_START for files).
// !	(Triangle)	List of Triangle objects usually starts immediately after the list of Shader objects, but use OFS_TRIANGLES (+ SURFACE_START).
// !	(ST)	List of ST objects usually starts immediately after the list of Triangle objects, but use OFS_ST (+ SURFACE_START).
// !	(XYZNormal)	List of Vertex objects usually starts immediate after the list of St objects, but use OFS_XYZNORMALS (+ SURFACE_START). The total number of objects is (NUM_FRAMES * NUM_VERTS). One set of NUM_VERTS Vertex objects describes the Surface in one frame of animation; the first NUM_VERTS Vertex objects describes the Surface in the first frame of animation, the second NUM_VERTEX Vertex objects describes the Surface in the second frame of animation, and so forth.
//-	SURFACE_END	End of Surface object. Should match OFS_END.	
	private void loadSurface(InputStream input,Md3LoaderAdapterInterface md3loader)
	{
		
		
		for (int surfaceId = 0; surfaceId < this.surfaceNumber;surfaceId++)
		{
			System.out.println("Loading Surface "+surfaceId);
			
			int surfaceStart = readCount;
			
			readData(input,read32);
			System.out.println("MAGIC SURFACE:"+getString(read32));
			
			//Read name
			readData(input,readName);
			
			System.out.println("NAME:"+getString(readName));		
			md3loader.setSurfaceName(surfaceId, getString(readName));
			
			//Read flags
			readData(input,read32);	
			
			//Read frames number
			readData(input,read32);
			System.out.println("FRAME NUMBER:"+getInt(read32));	
			frameNumber = getInt(read32);
			md3loader.setFrameNumber(surfaceId,frameNumber);
			
			//Read shaders number
			readData(input,read32);
			System.out.println("SHADER NUMBER:"+getInt(read32));	
			shaderNumber = getInt(read32);
			md3loader.setShaderNumber(surfaceId, shaderNumber);
			
			//Read vertices number
			readData(input,read32);
			System.out.println("VERTICES NUMBER:"+getInt(read32));
			verticesNumber = getInt(read32);
			md3loader.setVerticeNumber(surfaceId, verticesNumber);
			
			//Read triangles number
			readData(input,read32);
			System.out.println("TRIANGLES NUMBER:"+getInt(read32));		
			triangleNumber = getInt(read32);
			md3loader.setTriangleNumber(surfaceId, triangleNumber);
			
			//Read triangle offset
			readData(input,read32);
			System.out.println("TRIANGLES OFFSET:"+getInt(read32));		
			triangleOffset = getInt(read32) + surfaceStart;
			
			//Read shader offset
			readData(input,read32);		
			System.out.println("Shader OFFSET:"+getInt(read32));	
			shaderOffset = getInt(read32) + surfaceStart;
			
			//Read st offset
			readData(input,read32);
			System.out.println("ST OFFSET:"+getInt(read32));
			stOffset = getInt(read32) + surfaceStart;
			
			//Read xyz normal offset
			readData(input,read32);
			System.out.println("XYZ NORMAL OFFSET:"+getInt(read32));			
			xyzNormalOffset = getInt(read32) + surfaceStart;	
	
			//Read end surface offset
			readData(input,read32);
			System.out.println("END OFFSET:"+getInt(read32));		
			endSurfaceOffset = getInt(read32) + surfaceStart;
			
			this.map.clear();
			
			try {
				Method loadTriangleMethod = this.getClass().getMethod("loadTriangles", InputStream.class, Md3LoaderAdapterInterface.class, int.class);
				Method loadShader = this.getClass().getMethod("loadShader", InputStream.class, Md3LoaderAdapterInterface.class, int.class);
				Method loadSt = this.getClass().getMethod("loadSt", InputStream.class, Md3LoaderAdapterInterface.class, int.class);
				
				this.map.put(triangleOffset, loadTriangleMethod);
				this.map.put(shaderOffset, loadShader);
				this.map.put(stOffset, loadSt);
				
				for(Entry e : this.map.entrySet())
				{
					Method m = (Method)e.getValue();
					Log.d("sd3d","Calling: "+m.getName());
					Integer i = (Integer)e.getKey();
					skip(input, i - readCount);
					try {
						m.invoke(this, input,md3loader,surfaceId);
					} catch (IllegalArgumentException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IllegalAccessException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (InvocationTargetException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/*
			this.skip(input, triangleOffset - readCount);
			loadTriangles(input,md3loader,surfaceId);
		
			this.skip(input, shaderOffset - readCount);
			loadShader(input,md3loader,surfaceId);			
				
			this.skip(input, stOffset - readCount);
			loadSt(input,md3loader,surfaceId);
			*/
			
			
			this.skip(input, xyzNormalOffset - readCount);
			
			for (int frameId = 0;frameId < frameNumber; frameId++)
				loadXYZNormal(input,md3loader,surfaceId,frameId);
			
			this.skip(input, endSurfaceOffset - readCount);
		}
	}
	
	public void loadShader(InputStream input,Md3LoaderAdapterInterface md3loader, int surfaceId)
	{
		for (int i = 0;i < shaderNumber;i++)
		{
			readData(input,readName);
			readData(input,read32);	
			
			System.out.println("SHADER:"+getString(readName));		
			md3loader.setShader(surfaceId,getString(readName));
		}
	}
	
	
	public void loadTriangles(InputStream input,Md3LoaderAdapterInterface md3loader, int surfaceId)
	{
		for (int i = 0;i < triangleNumber;i++)
		{
		
			readData(input,read32);
			int a = getInt(read32);
			
			readData(input,read32);
			int b = getInt(read32);			
			
			readData(input,read32);
			int c = getInt(read32);
		
			//System.out.println("triangle: "+a+" - "+b+" - "+c);
			md3loader.addTriangleIndices(surfaceId, i,a,b,c);
		}
	}
	
	public void loadSt(InputStream input,Md3LoaderAdapterInterface md3loader, int surfaceId)
	{
		for (int i = 0;i < verticesNumber;i++)
		{
			readData(input,read32);
			float u = Float.intBitsToFloat(getInt(read32));
			readData(input,read32);
			float v = Float.intBitsToFloat(getInt(read32));
			
			//System.out.println("texcoords["+u+","+v+"]");
			
			md3loader.addTexCoords(surfaceId, i, u, v);
		}		
	}
	
	private float extractFloat(byte[] data,float factor)
	{
		int val = signedread16(read16,0);
		//System.out.println("VAL="+(int)((char)val));
		return factor * (float)val;
	}
	
	private float cos(float v)
	{
		return (float)java.lang.Math.cos((double)v);
	}
	
	private float sin(float v)
	{
		return (float)java.lang.Math.sin((double)v);
	}	
	
	private void loadXYZNormal(InputStream input,Md3LoaderAdapterInterface md3loader, int surfaceId, int frameId)
	{
		float factor = 1.0f/64.0f;
		float x;
		float y;
		float z;
		float xn;
		float yn;
		float zn;
		for (int i = 0;i< verticesNumber;i++)
		{
			readData(input,read16);
			x = extractFloat(read16,factor);
			readData(input,read16);	
			y = extractFloat(read16,factor);
			readData(input,read16);	
			z = extractFloat(read16,factor);
			//System.out.println("Coords = "+x+":"+y+":"+z);
			readData(input,read8);
			int zenith = unsignedByteToInt(read8[0]); 
			readData(input,read8);
			int azimuth = unsignedByteToInt(read8[0]); 
			float lat = (float)zenith * (2 * (float)java.lang.Math.PI ) / 255.0f;
			float lng = (float)azimuth * (2 * (float)java.lang.Math.PI) / 255.0f;
			
			xn = cos ( lng ) * sin ( lat );
			yn = sin ( lng ) * sin ( lat );
			zn = cos ( lat );
			
			//System.out.println("Normal Coords = "+xn+":"+yn+":"+zn);

			md3loader.addVertexCoords(surfaceId, frameId, i, x * scale, y * scale, z * scale);
			md3loader.addNormalCoords(surfaceId, frameId, i, xn, yn, zn);
		}
	}
	
	//Parsing data
	private byte readName[] = new byte[64];
	private byte read32[] = new byte[4];
	private byte read16[] = new byte[2];
	private byte read8[] = new byte[1];	
	private float rotMatrix[] = new float[9];
	private int readCount = 0;
	
	
	//MD3 Header data
	private int frameOffset = 0;
	private int surfaceOffset = 0;
	private int surfaceNumber = 0;
	private int tagOffset = 0;
	private int tagNumber = 0;
	
	//MD3 Surface data
	private int frameNumber = 0;
	private int verticesNumber = 0;
	private int triangleNumber = 0;
	private int triangleOffset = 0;
	private int stOffset = 0;
	private int xyzNormalOffset = 0;
	private int endSurfaceOffset = 0;
	private int shaderNumber = 0;
	private int shaderOffset = 0;
	
	public void loadMD3(String filePath,Md3LoaderAdapterInterface md3loader)
	{
		File file = new File(filePath);
		InputStream input = null;
		try {
			input = new BufferedInputStream(new FileInputStream(file));
			loadHeader(input,md3loader);
			loadFrame(input);
			loadSurface(input,md3loader);
			
			
			input.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("Impossible to open file "+e.toString());
		} catch (IOException e) {
			System.out.println("Error while reading file "+e.toString());
		}
		
		
	}
	
	
	public void loadMD3FromPK3(String filePath,Md3LoaderAdapterInterface md3loader)
	{
		File file = new File(filePath);
		InputStream input;
		try {
			
			input = new BufferedInputStream(new FileInputStream(file));
			loadMD3FromPK3(input,md3loader);
	
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}	
	
	public void loadMD3FromPK3(InputStream input,Md3LoaderAdapterInterface md3loader)
	{
		try {


			ZipInputStream zis = new ZipInputStream(input);
			ZipEntry e;
			while ((e = zis.getNextEntry()) != null)
			{
				
				if (e.getName().toUpperCase().contains("UPPER.MD3") || e.getName().toUpperCase().contains("LOWER.MD3")
						|| e.getName().toUpperCase().contains("HEAD.MD3")
						)
				if (e.getName().toUpperCase().endsWith("MD3"))
				{
					md3loader.setMd3FileName(e.getName());
					System.out.println("Loading: "+e.getName());
					loadHeader(zis,md3loader);
					loadFrame(zis);
					loadSurface(zis,md3loader);				
					zis.closeEntry();
				}
				
			}

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}				
	}

	public void loadSkinDataFromPk3(InputStream input)
	{
		
	}
	
	public HashMap <String, String> loadSkinData(InputStream input)
	{
		try 
		{
			ZipInputStream zis = new ZipInputStream(input);
			ZipEntry e;
			while ((e = zis.getNextEntry()) != null)
			{	
				
				boolean read = false;
				for (int i = 0;i < skinFiles.length;i++)
				{
					if (e.getName().endsWith(skinFiles[i]))
						read = true;
				}
				
				if (read)
				{
					Log.d("sd3d","Loading skin file "+e.getName());
					BufferedReader br = new BufferedReader(new InputStreamReader(zis));
					String strLine;
					while ((strLine = br.readLine()) != null)   {
						String skindata[] = strLine.split(",");
						if (skindata.length == 2)
						{
							skinMap.put(skindata[0],skindata[1]);
						}
					}
					
					zis.closeEntry();
				}
				
			}
		}catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		
		return this.skinMap;
	}
	
	/*
	public static void main(String argv[])
	{
		System.out.println(argv[0]);
		Md3Loader md3Loader =new Md3Loader();
		md3Loader.loadMD3FromPK3(argv[0], new Md3LoaderAdapterInterface(){
			@Override
			public void setSurfaceNumber(int surfaceNumber) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setVerticeNumber(int surfaceId, int verticeNumber) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setTriangleNumber(int surfaceId, int triangleNumber) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void addTexCoords(int surfaceId, float u, float v) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void addTriangleIndices(int surfaceId, int a, int b, int c) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setFrameNumber(int surfaceId, int frameNumber) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void addVertexCoords(int surfaceId, int frameId, float x,
					float y, float z) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void addNormalCoords(int surfaceId, int frameId, float xn,
					float yn, float zn) {
				// TODO Auto-generated method stub
				
			}});
		
	}
	*/
}