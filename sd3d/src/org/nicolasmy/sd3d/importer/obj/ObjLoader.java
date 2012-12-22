package org.nicolasmy.sd3d.importer.obj;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.util.Log;

public class ObjLoader {
	ObjLoaderAdapterInterface mObjLoaderAdapter = new Sd3dObjLoaderAdapter();
	ArrayList<float []> vertices = new ArrayList<float []>();
	ArrayList<float []> normals = new ArrayList<float []>();
	ArrayList<float []> texcoords = new ArrayList<float []>();
	char vertCounter = 0;
	
	public ObjLoader(ObjLoaderAdapterInterface objloader) {
		mObjLoaderAdapter = objloader;
	}
	
	private void addVertexData(String vertexDesc) {
		String tokens[] = vertexDesc.split("/");
		int vIndice = Integer.parseInt(tokens[0]) - 1;
		int tIndice = Integer.parseInt(tokens[1]) - 1;
		int nIndice = Integer.parseInt(tokens[2]) - 1;
		
		float vert[] = vertices.get(vIndice);
		mObjLoaderAdapter.addVertex(vert[0], vert[1], vert[2]);
		float norm[] = normals.get(nIndice);
		mObjLoaderAdapter.addNormal(norm[0], norm[1], norm[2]);
		float text[] = texcoords.get(tIndice);
		mObjLoaderAdapter.addTexCoords(text[0], 1.0f - text[1]);
		vertCounter++;
	}
	
	private void addfaceData(int vertCount) {
//		if (vertCount == 4) {
//			mObjLoaderAdapter.addFace((char)(vertCounter-4), (char)(vertCounter-3), (char)(vertCounter-2));
//			mObjLoaderAdapter.addFace((char)(vertCounter-4), (char)(vertCounter-2), (char)(vertCounter-1));
//		} else if (vertCount == 3) {
//			mObjLoaderAdapter.addFace((char)(vertCounter-3), (char)(vertCounter-2), (char)(vertCounter-1));
//		}
		if (vertCount == 4) {
			mObjLoaderAdapter.addFace((char)(vertCounter-2),(char)(vertCounter-3),(char)(vertCounter-4));
			mObjLoaderAdapter.addFace((char)(vertCounter-1),(char)(vertCounter-2),(char)(vertCounter-4));
		} else if (vertCount == 3) {
			mObjLoaderAdapter.addFace( (char)(vertCounter-1), (char)(vertCounter-2),(char)(vertCounter-3));
		}				
	}

	public void forwardCountFromStream(InputStream is) {
		int faceCounter = 0;
		int vertexCounter = 0;
		try {		
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader input =  new BufferedReader(isr);	
			String line = null;
			while ((line = input.readLine()) != null) {
				String tokens[] = line.split(" ");
				if (tokens.length > 0) {
					if (tokens[0].equals("f")) {
						if (tokens.length == 5) {
							vertexCounter += 4;
							faceCounter += 2;
						} else if (tokens.length == 4) {
							vertexCounter += 3;
							faceCounter += 1;
						}
					}
				}
			}
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}		

		mObjLoaderAdapter.setVertexNumber(vertexCounter);
		mObjLoaderAdapter.setFaceNumber(faceCounter);
	}	
	
	public void loadFromFile(InputStream is) {
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader input =  new BufferedReader(isr);
			String line = null;
			while ((line = input.readLine()) != null) {
				String tokens[] = line.split(" +");
				Log.d("ObjLoader", "Parsing data line :"+line);
				if (tokens.length > 0) {
					if ((tokens[0].length() > 0)&&tokens[0].charAt(0)=='v') {
						float v [] = new float[3];
						v[0] = Float.parseFloat(tokens[1]);
						v[1] = Float.parseFloat(tokens[2]);
						v[2] = Float.parseFloat(tokens[3]);
						vertices.add(v);
					}
					
					if (tokens[0].equals("vn")) {
						float v [] = new float[3];
						v[0] = Float.parseFloat(tokens[1]);
						v[1] = Float.parseFloat(tokens[2]);
						v[2] = Float.parseFloat(tokens[3]);
						normals.add(v);
					}					
					
					if (tokens[0].equals("vt")) {
						float v [] = new float[2];
						v[0] = Float.parseFloat(tokens[1]);
						v[1] = Float.parseFloat(tokens[2]);
						texcoords.add(v);
					}	
					
					if (tokens[0].equals("f")) {
						if (tokens.length == 5) {
							addVertexData(tokens[1]);
							addVertexData(tokens[2]);
							addVertexData(tokens[3]);
							addVertexData(tokens[4]);
							addfaceData(4);
						} else if (tokens.length == 4) {
							addVertexData(tokens[1]);
							addVertexData(tokens[2]);
							addVertexData(tokens[3]);	
							addfaceData(3);
						}
					}
				}
			}
			input.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}		
	
	private void forwardCount(String file) {
		
		int faceCounter = 0;
		int vertexCounter = 0;
		try {		
			BufferedReader input =  new BufferedReader(new FileReader(file));	
			String line = null;
			while ((line = input.readLine()) != null) {
				String tokens[] = line.split(" ");
				if (tokens.length > 0) {
					if (tokens[0].equals("f")) {
						if (tokens.length == 5) {
							vertexCounter += 4;
							faceCounter += 2;
						} else if (tokens.length == 4) {
							vertexCounter += 3;
							faceCounter += 1;
						}
					}
				}
			}
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		mObjLoaderAdapter.setVertexNumber(vertexCounter);
		mObjLoaderAdapter.setFaceNumber(faceCounter);
		
	}
	
	public void loadFromFile(String file) {
		forwardCount(file);
		try {
			BufferedReader input =  new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = input.readLine()) != null) {
				String tokens[] = line.split(" ");
				if (tokens.length > 0) {
					if (tokens[0].charAt(0)=='v') {
						float v [] = new float[3];
						v[0] = Float.parseFloat(tokens[1]);
						v[1] = Float.parseFloat(tokens[2]);
						v[2] = Float.parseFloat(tokens[3]);
						vertices.add(v);
					}
					
					if (tokens[0].equals("vn")) {
						float v [] = new float[3];
						v[0] = Float.parseFloat(tokens[1]);
						v[1] = Float.parseFloat(tokens[2]);
						v[2] = Float.parseFloat(tokens[3]);
						vertices.add(v);
					}					
					
					if (tokens[0].equals("vt")) {
						float v [] = new float[3];
						v[0] = Float.parseFloat(tokens[1]);
						v[1] = Float.parseFloat(tokens[2]);
						texcoords.add(v);
					}	
					
					if (tokens[0].equals("f")) {
						if (tokens.length == 5) {
							addVertexData(tokens[1]);
							addVertexData(tokens[2]);
							addVertexData(tokens[3]);
							addVertexData(tokens[4]);
							addfaceData(4);
						} else if (tokens.length == 4) {
							addVertexData(tokens[1]);
							addVertexData(tokens[2]);
							addVertexData(tokens[3]);	
							addfaceData(3);
						}
					}
				}
			}
			input.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
