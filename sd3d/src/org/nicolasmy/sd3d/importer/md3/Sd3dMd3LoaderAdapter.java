package org.nicolasmy.sd3d.importer.md3;

import java.nio.CharBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.nicolasmy.sd3d.gfx.Sd3dMaterial;
import org.nicolasmy.sd3d.gfx.Sd3dMesh;
import org.nicolasmy.sd3d.gfx.Sd3dObject;

import android.util.Log;

public class Sd3dMd3LoaderAdapter implements Md3LoaderAdapterInterface{
	
	
	public HashMap md3Map = new HashMap();
	public HashMap<String,float[][]> tagMap = new HashMap<String,float[][]>();
	public HashMap<String, Sd3dMaterial> materialMap = new HashMap<String, Sd3dMaterial>();
	private ArrayList<ArrayList<Sd3dObject>> surfaceList;
	private String currentSurfaceName = null;
	public HashMap<String,Sd3dMaterial> shaderMap = new HashMap<String,Sd3dMaterial>();
	private String fileName = "";
	private int currentFrameNumber;
	//private Sd3dMaterial currentMaterial = null;
	private int currentVerticeNumber = 0;
	private int currentTriangleNumber = 0;
	
	@Override
	public void setSurfaceNumber(int surfaceNumber) {
		for (int i = 0;i < surfaceNumber;i++)
		{
			surfaceList.add(new ArrayList<Sd3dObject>());
		}
	}

	@Override
	public void setFrameNumberHeader(int frameNumber) {
		currentFrameNumber = frameNumber;		
	}
	
	@Override
	public void setFrameNumber(int surfaceId, int frameNumber) {

		ArrayList<Sd3dObject> frameList = surfaceList.get(surfaceId);
		 
		for (int i = 0;i < frameNumber;i++)
		{
			Sd3dObject obj = new Sd3dObject();
			obj.mRotation = new float[3];
			obj.mMesh = new Sd3dMesh[1];
			obj.mMesh[0] = new Sd3dMesh();
			frameList.add(obj);
		}
	}

	@Override
	public void setVerticeNumber(int surfaceId, int verticeNumber) {
		currentVerticeNumber = verticeNumber;
		
		ArrayList<Sd3dObject> frameList = surfaceList.get(surfaceId);
		
		for (int i = 0;i < frameList.size();i++)
		{
			Sd3dObject obj = frameList.get(i);
			obj.mMesh[0].mVertices = FloatBuffer.allocate(Sd3dMesh.nbFloatPerVertex * verticeNumber);
			obj.mMesh[0].mVertices.position(0);
		}
	}

	@Override
	public void setTriangleNumber(int surfaceId, int triangleNumber) {
		currentTriangleNumber = triangleNumber;
	
		ArrayList<Sd3dObject> frameList = surfaceList.get(surfaceId);
		
		for (int i = 0;i < frameList.size();i++)
		{
			Sd3dObject obj = frameList.get(i);
			obj.mMesh[0].mIndices = CharBuffer.allocate(triangleNumber * 3);
			obj.mMesh[0].mIndices.position(0);
		}		
		
	}

	@Override
	public void addVertexCoords(int surfaceId, int frameId, int vertexId, float x, float y,
			float z) {
		
		ArrayList<Sd3dObject> frameList = surfaceList.get(surfaceId);
		

		Sd3dObject obj = frameList.get(frameId);
		
		obj.mMesh[0].mVertices.put(vertexId*Sd3dMesh.nbFloatPerVertex    , x);
		obj.mMesh[0].mVertices.put(vertexId*Sd3dMesh.nbFloatPerVertex + 1, y);
		obj.mMesh[0].mVertices.put(vertexId*Sd3dMesh.nbFloatPerVertex + 2, z);

	}

	@Override
	public void addNormalCoords(int surfaceId, int frameId, int vertexId, float xn, float yn,
			float zn) {
		ArrayList<Sd3dObject> frameList = surfaceList.get(surfaceId);
		

		Sd3dObject obj = frameList.get(frameId);
		
		obj.mMesh[0].mVertices.put(vertexId*Sd3dMesh.nbFloatPerVertex + 3, xn);
		obj.mMesh[0].mVertices.put(vertexId*Sd3dMesh.nbFloatPerVertex + 4, yn);
		obj.mMesh[0].mVertices.put(vertexId*Sd3dMesh.nbFloatPerVertex + 5, zn);
		
	}

	@Override
	public void addTexCoords(int surfaceId, int vertexId, float u, float v) {
		ArrayList<Sd3dObject> frameList = surfaceList.get(surfaceId);
		
		for (int i = 0;i < frameList.size();i++)
		{
			Sd3dObject obj = frameList.get(i);
			obj.mMesh[0].mVertices.put(vertexId*Sd3dMesh.nbFloatPerVertex + 6, u);
			obj.mMesh[0].mVertices.put(vertexId*Sd3dMesh.nbFloatPerVertex + 7, v);			
		}
	}

	@Override
	public void addTriangleIndices(int surfaceId, int triangleId, int a, int b, int c) {
		ArrayList<Sd3dObject> frameList = surfaceList.get(surfaceId);
		
		for (int i = 0;i < frameList.size();i++)
		{
			Sd3dObject obj = frameList.get(i);
			obj.mMesh[0].mIndices.put(triangleId * 3, (char)a);
			obj.mMesh[0].mIndices.put(triangleId * 3 + 1, (char)b);
			obj.mMesh[0].mIndices.put(triangleId * 3 + 2, (char)c);
		}		
	}

	@Override
	public void setMd3FileName(String filename) {
		surfaceList = new ArrayList<ArrayList<Sd3dObject>>();
		md3Map.put(filename, surfaceList);
		fileName = filename;
	}

	@Override
	public void setTagNumber(int tagNumber) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addTag(int frameId, int tagId, String tagName, float x, float y, float z, float rotMatrix[]) {
		
		/* Copy data packed into a linear array of float to the tagMap */
		float f[] = new float[12];
		f[0] = x;
		f[1] = y;
		f[2] = z;
		
		
		for (int i = 0; i < 9;i++)
		{
			f[i+3] = rotMatrix[i];
		}
		
		
		
		
		if (((fileName.toUpperCase().contains("LOWER"))
			&&tagName.equals(Md3Loader.TAG_TORSO))
			||
			((fileName.toUpperCase().contains("UPPER"))
					&&tagName.equals(Md3Loader.TAG_HEAD)))
		{
			float v[][];
			if (tagMap.get(tagName) == null)
			{
				v = new float[currentFrameNumber][];
				tagMap.put(tagName, v);
			}
			else
			{
				v = tagMap.get(tagName);
				v[frameId] = f;
			}
			
		}
		
		Log.d("sd3d",tagName+" : "+x+" "+y+" "+z);
	}

	@Override
	public void setShader(int surfaceId, String shader) {
		//Use in-built shader data
		if (shader.length() > 0)
		{
			if (materialMap.get(shader) == null)
			{
				Sd3dMaterial material = new Sd3dMaterial();
				materialMap.put(shader, material);
			}
			ArrayList<Sd3dObject> frameList = surfaceList.get(surfaceId);		
			
			for (Sd3dObject o : frameList)
			{
				o.mMaterial = new Sd3dMaterial[1];
				o.mMaterial[0] = materialMap.get(shader);				
			}
		}
		else
		{
			//Use .skin file
			if (shaderMap.get(currentSurfaceName) == null)
			{
				Sd3dMaterial material = new Sd3dMaterial();
				shaderMap.put(currentSurfaceName, material);
			}
			ArrayList<Sd3dObject> frameList = surfaceList.get(surfaceId);		
			
			for (Sd3dObject o : frameList)
			{
				
				o.mMaterial = new Sd3dMaterial[1];
				o.mMaterial[0] = shaderMap.get(currentSurfaceName);
			}			
		}
	}

	@Override
	public void setSurfaceName(int surfaceId, String name) {
		currentSurfaceName = name;
		
	}

	@Override
	public void setShaderNumber(int surfaceId, int shaderNumber) {
		//Handle the case where there is no shader for
		//the current surface
		if (shaderNumber == 0)
		{
			//Use .skin file
			if (shaderMap.get(currentSurfaceName) == null)
			{
				Sd3dMaterial material = new Sd3dMaterial();
				shaderMap.put(currentSurfaceName, material);
			}
			ArrayList<Sd3dObject> frameList = surfaceList.get(surfaceId);		
			
			for (Sd3dObject o : frameList)
			{
				
				o.mMaterial = new Sd3dMaterial[1];
				o.mMaterial[0] = shaderMap.get(currentSurfaceName);
			}				
		}
		
	}

}
