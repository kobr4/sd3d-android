package org.nicolasmy.sd3d.factories;

import java.io.IOException;

import org.nicolasmy.sd3d.gfx.Sd3dMaterial;

public class Sd3dMaterialFactory {
	public static Sd3dMaterial fromFile(String file) throws IOException {
		if (file.toUpperCase().endsWith(".TGA")) {
			Sd3dMaterial material = new Sd3dMaterial();
			material.loadTGATexture(file);
			return material;
		} else if (file.toUpperCase().endsWith(".PNG")) {
			Sd3dMaterial material = new Sd3dMaterial();
			material.loadTexture(file);
			return material;
		}
		return null;
	}
}
