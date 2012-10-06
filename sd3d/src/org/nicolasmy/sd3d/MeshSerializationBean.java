package org.nicolasmy.sd3d;

import java.io.Serializable;

public class MeshSerializationBean implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4032601181262942050L;
	private float vertices[];
	private char indices[];
	private float normals[];
	private float meshPostion[];	
	public float[] getVertices() {
		return vertices;
	}
	public void setVertices(float[] vertices) {
		this.vertices = vertices;
	}
	public char[] getIndices() {
		return indices;
	}
	public void setIndices(char[] indices) {
		this.indices = indices;
	}
	public float[] getNormals() {
		return normals;
	}
	public void setNormals(float normals[]) {
		this.normals = normals;
	}
	public float[] getMeshPostion() {
		return meshPostion;
	}
	public void setMeshPostion(float meshPostion[]) {
		this.meshPostion = meshPostion;
	} 
	
}
