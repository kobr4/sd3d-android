package org.nicolasmy.sd3d.factories;

import java.nio.CharBuffer;
import java.nio.FloatBuffer;

import org.nicolasmy.sd3d.gfx.Sd3dMesh;
import org.nicolasmy.sd3d.math.Sd3dVector;

public class Sd3dMeshReverseSphereFactory {
	public static Sd3dMesh createSphere(int nbSlices,int nbStairs, float radius) {
		Sd3dMesh mesh = new Sd3dMesh();
		
		int triangleCount = nbSlices * (nbStairs) * 2;
		
		mesh.mVertices = FloatBuffer.allocate(Sd3dMesh.nbFloatPerVertex * 3 * triangleCount);
		mesh.mIndices = CharBuffer.allocate(triangleCount * 3);
		
		int indice = 0;
        for (int i = 0; i < nbStairs; i++) {
            for (int j = 0; j < nbSlices; j++) {
                float alpha = (float)Math.PI / (nbStairs) * i;
                float beta = (float)Math.PI*2 / nbSlices * j;
                float alpha1 = (float)Math.PI / (nbStairs) * ((i + 1));
//                if (i == nbStairs-1)
//                	alpha1 = 0.f;
                float beta1 = (float)Math.PI*2 / nbSlices * ((j + 1));
                if (j == nbSlices-1)
                	beta1 = 0.f;                

                Sd3dVector a = new Sd3dVector(radius * (float)Math.sin(alpha) * (float)Math.cos(beta), radius * (float)Math.cos(alpha), radius * (float)Math.sin(alpha) * (float)Math.sin(beta));
                Sd3dVector b = new Sd3dVector(radius * (float)Math.sin(alpha) * (float)Math.cos(beta1), radius * (float)Math.cos(alpha), radius * (float)Math.sin(alpha) * (float)Math.sin(beta1));
                Sd3dVector c = new Sd3dVector(radius * (float)Math.sin(alpha1) * (float)Math.cos(beta1), radius * (float)Math.cos(alpha1), radius * (float)Math.sin(alpha1) * (float)Math.sin(beta1));
                Sd3dVector d = new Sd3dVector(radius * (float)Math.sin(alpha1) * (float)Math.cos(beta), radius * (float)Math.cos(alpha1), radius * (float)Math.sin(alpha1) * (float)Math.sin(beta));
    
                
                mesh.mVertices.put(a.get(0));
                mesh.mVertices.put(a.get(1));
                mesh.mVertices.put(a.get(2));
                
                //normal not computed here
                mesh.mVertices.put(a.get(0) / radius);
                mesh.mVertices.put(a.get(1) / radius);
                mesh.mVertices.put(a.get(2) / radius);                
                
                mesh.mVertices.put((float)(j + 0) / (float)nbSlices);
                mesh.mVertices.put((float)(i + 0) / (float)nbStairs);                          
                
          
                mesh.mVertices.put(b.get(0));
                mesh.mVertices.put(b.get(1));
                mesh.mVertices.put(b.get(2));
        
                //normal not computed here
                mesh.mVertices.put(b.get(0)/ radius);
                mesh.mVertices.put(b.get(1)/ radius);
                mesh.mVertices.put(b.get(2)/ radius);              
                
                mesh.mVertices.put((float)(j + 1) / (float)nbSlices);
                mesh.mVertices.put((float)(i + 0) / (float)nbStairs);
                
                mesh.mVertices.put(c.get(0));
                mesh.mVertices.put(c.get(1));
                mesh.mVertices.put(c.get(2));
                   
                //normal not computed here
                mesh.mVertices.put(c.get(0) / radius);
                mesh.mVertices.put(c.get(1) / radius);
                mesh.mVertices.put(c.get(2) / radius);
               
                mesh.mVertices.put((float)(j + 1) / (float)nbSlices);
                mesh.mVertices.put((float)(i + 1) / (float)nbStairs);

                mesh.mVertices.put(d.get(0));
                mesh.mVertices.put(d.get(1));
                mesh.mVertices.put(d.get(2));
                
                //normal not computed here
                mesh.mVertices.put(d.get(0) / radius);
                mesh.mVertices.put(d.get(1) / radius);
                mesh.mVertices.put(d.get(2) / radius);
                
                mesh.mVertices.put((float)(j + 0) / (float)nbSlices);
                mesh.mVertices.put((float)(i + 1) / (float)nbStairs);
      
                mesh.mIndices.put((char)indice);
                mesh.mIndices.put((char)(indice+1));
                mesh.mIndices.put((char)(indice+2));		
        		
        		
                mesh.mIndices.put((char)(indice+2));
                mesh.mIndices.put((char)(indice+3));
                mesh.mIndices.put((char)indice);	
                
//                mesh.mIndices.put((char)(indice+2));
//                mesh.mIndices.put((char)(indice+1));
//                mesh.mIndices.put((char)(indice+0));		
//      		
//      		
//                mesh.mIndices.put((char)(indice+0));
//                mesh.mIndices.put((char)(indice+3));
//                mesh.mIndices.put((char)(indice+2));	               
                
                
                indice = mesh.mVertices.position() /Sd3dMesh.nbFloatPerVertex;
            }
            
		}
		
        mesh.mIndices.position(0);
        mesh.mVertices.position(0);
        
        
		return mesh;
	}
}
