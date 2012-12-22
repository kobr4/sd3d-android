package org.nicolasmy.sd3d.importer.obj;

import java.nio.CharBuffer;
import java.nio.FloatBuffer;

import org.nicolasmy.sd3d.gfx.Sd3dMesh;

public class Sd3dObjLoaderAdapter implements ObjLoaderAdapterInterface {
	Sd3dMesh mesh = new Sd3dMesh();
	float scale = 0.01f;
	
	@Override
	public void setVertexNumber(int n) {
		mesh.mVertices = FloatBuffer.allocate(n * Sd3dMesh.nbFloatPerVertex);
	}

	@Override
	public void setFaceNumber(int n) {
		mesh.mIndices = CharBuffer.allocate(n * 3);
	}

	@Override
	public void addVertex(float x, float y, float z) {
		mesh.mVertices.put(x * scale);
		mesh.mVertices.put(y * scale);
		mesh.mVertices.put(z * scale);
	}

	@Override
	public void addNormal(float xn, float yn, float zn) {
		mesh.mVertices.put(xn);
		mesh.mVertices.put(yn);
		mesh.mVertices.put(zn);
	}

	@Override
	public void addTexCoords(float u, float v) {
		mesh.mVertices.put(u);
		mesh.mVertices.put(v);
	}

	@Override
	public void addFace(char a, char b, char c) {
		mesh.mIndices.put(a);
		mesh.mIndices.put(b);
		mesh.mIndices.put(c);
	}

	
	public Sd3dMesh getMesh() {
		mesh.mVertices.position(0);
		mesh.mIndices.position(0);
		return mesh;
	}
}
