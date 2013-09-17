package org.nicolasmy.sd3d.gfx.entity;

import java.io.IOException;

import org.nicolasmy.sd3d.factories.Sd3dMaterialFactory;
import org.nicolasmy.sd3d.factories.Sd3dMeshReverseSphereFactory;
import org.nicolasmy.sd3d.gfx.Sd3dMaterial;
import org.nicolasmy.sd3d.gfx.Sd3dMesh;
import org.nicolasmy.sd3d.gfx.Sd3dObject;

public class Sd3dSkySphereEntity extends Sd3dGameEntity {
	public Sd3dSkySphereEntity(int nbSlices,int nbStairs,float radius, String filename) {
		super();
		
		this.mObject = new Sd3dObject();
		this.mObject.mMesh = new Sd3dMesh[1];
		this.mObject.mMesh[0] = Sd3dMeshReverseSphereFactory.createSphere(nbSlices, nbStairs, radius);
		
		//this.mObject.mMesh[0].mergeVertexIndices();
		
		
		//Sd3dMatrix rot = Sd3dMatrix.getRotationMatrix(90f, 0, 0);
		//this.mObject.mMesh[0].applyMatrix(rot);
		
		this.mObject.mMaterial = new Sd3dMaterial[1];
		
		this.mObject.mRotation = this.mOrientation;
		this.mPosition = new float[3];
		this.mObject.mPosition = this.mPosition;
		try {
			this.mObject.mMaterial[0] = Sd3dMaterialFactory.fromFile(filename);
			this.mObject.mMaterial[0].setshaderName("textureonly");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.mObject.mMesh[0].generateNormalsTangentsBinormals();
		//this.mObject.mMesh[0].generateNormals();

		this.hasObject = true;
	}
}
