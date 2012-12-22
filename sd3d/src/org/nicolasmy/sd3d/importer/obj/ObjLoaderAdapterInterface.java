package org.nicolasmy.sd3d.importer.obj;

public interface ObjLoaderAdapterInterface {
	void setVertexNumber(int n);
	void setFaceNumber(int n);
	void addVertex(float x, float y, float z);
	void addNormal(float xn, float yn, float zn);
	void addTexCoords(float u, float v);
	void addFace(char a, char b, char c);
}
