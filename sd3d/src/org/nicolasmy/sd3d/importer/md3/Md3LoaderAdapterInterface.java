package org.nicolasmy.sd3d.importer.md3;

/**
 * Implement this interface to load MD3 data.
 * Basically these events are triggered when corresponding data are
 * fetched from parsing any md3 file in the pk3 archive
 */
public interface Md3LoaderAdapterInterface {
	/**
	 * Called first at the beginning of a md3 file loading
	 * @param filename name and full path of the md3 file
	 */
	public void setMd3FileName(String filename);
	
	/**
	 * Called when parsing surface descriptor header
	 * @param surfaceNumber
	 */
	public void setSurfaceNumber(int surfaceNumber);
	public void setFrameNumberHeader(int frameNumber);
	public void setFrameNumber(int surfaceId, int frameNumber);
	public void setTagNumber(int tagNumber);
	public void setSurfaceName(int surfaceId, String name);
	public void setShader(int surfaceId, String shader);
	public void setShaderNumber(int surfaceId, int shaderNumber);
	public void setVerticeNumber(int surfaceId, int verticeNumber);
	public void setTriangleNumber(int surfaceId, int triangleNumber);
	public void addTag(int frameId, int tagId, String tagName, float x, float y, float z, float rotMatrix[]);
	public void addVertexCoords(int surfaceId, int frameId, int vertexId, float x ,float y, float z);
	public void addNormalCoords(int surfaceId, int frameId, int vertexId, float xn ,float yn, float zn);
	public void addTexCoords(int surfaceId, int vertexId, float u, float v);
	public void addTriangleIndices(int surfaceId, int triangleId,int a, int b, int c);
}
