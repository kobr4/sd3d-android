package org.nicolasmy.sd3d.factories;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.nicolasmy.sd3d.gfx.Sd3dMaterial;
import org.nicolasmy.sd3d.gfx.Sd3dMesh;
import org.nicolasmy.sd3d.gfx.Sd3dObject;
import org.nicolasmy.sd3d.gfx.Sd3dObjectFrameAnimator;
import org.nicolasmy.sd3d.gfx.Sd3dRessourceManager;
import org.nicolasmy.sd3d.importer.md3.Md3Loader;
import org.nicolasmy.sd3d.importer.md3.Sd3dMd3LoaderAdapter;
import org.nicolasmy.sd3d.math.Sd3dMatrix;

import android.util.Log;

public class Sd3dObjectFrameAnimatorFactory {
	public static Sd3dObjectFrameAnimator[] createFrameAnimatorFromPK3(String pk3file)
	{
		
		Sd3dMd3LoaderAdapter adapter = new Sd3dMd3LoaderAdapter();
		ArrayList<Sd3dObjectFrameAnimator> animatorList = new  ArrayList<Sd3dObjectFrameAnimator>();
		Md3Loader md3loader = new Md3Loader();
		Sd3dMatrix rotationMatrix = Sd3dMatrix.getRotationMatrix(-90f, 0f, 0f);
		Sd3dMatrix transformMatrix;
		InputStream is;
		Sd3dObjectFrameAnimator frameAnimatorArray[] = null;
		
		try {
			is = Sd3dRessourceManager.getManager().getRessource(pk3file);
			md3loader.loadMD3FromPK3(is, adapter);
			
			frameAnimatorArray = new Sd3dObjectFrameAnimator[adapter.md3Map.keySet().size()];

			for(Object o : adapter.md3Map.keySet())
			{
				Sd3dObjectFrameAnimator frameAnimator = new Sd3dObjectFrameAnimator();
				animatorList.add(frameAnimator);
				String s = (String)o;
				
				if (s.toUpperCase().contains("LOWER"))
				{
					frameAnimatorArray[0] = frameAnimator;
				}
		
				if (s.toUpperCase().contains("UPPER"))
				{
					frameAnimatorArray[1] = frameAnimator;
				}		
				
				if (s.toUpperCase().contains("HEAD"))
				{
					frameAnimatorArray[2] = frameAnimator;
				}				
				
				//ArrayList<ArrayList<Sd3dObject>> surfaceList  = (ArrayList<ArrayList<Sd3dObject>> )adapter.md3Map.get(s);
				
				//Log.d("sd3d","NB FRAMES= "+surfaceList.get(0).size());
				
				//totalMesh += surfaceList.size();
			}		

			int animatorId = 0;
			for(Object o : adapter.md3Map.keySet())
			{
				String s = (String)o;					
				ArrayList<ArrayList<Sd3dObject>> surfaceList  = (ArrayList<ArrayList<Sd3dObject>> )adapter.md3Map.get(s);
				int nbFrames = surfaceList.get(0).size();
				for (int frameId = 0; frameId < nbFrames;frameId++)
				{
					Log.d("Sd3d","LOADING: "+s);
					
				
					Sd3dObject obj = new Sd3dObject();	
					obj.mMesh = new Sd3dMesh[surfaceList.size()];
					obj.mMaterial = new Sd3dMaterial[surfaceList.size()];							
					
					for (int j = 0;j < surfaceList.size();j++)
					{
						
						transformMatrix = rotationMatrix.clone();

						
						obj.mMesh[j] = surfaceList.get(j).get(frameId).mMesh[0];
						obj.mMaterial[j] = surfaceList.get(j).get(frameId).mMaterial[0];
						/*
						if (s.toUpperCase().contains("UPPER"))
						{
							if (adapter.tagMap.get(Md3Loader.TAG_TORSO) != null)
							{
								float tagdata[][] = adapter.tagMap.get(Md3Loader.TAG_TORSO);
								
								
								Sd3dMatrix rotMat = new Sd3dMatrix(tagdata, 3);
								obj.mMesh[j].applyMatrix(rotMat);
								
								obj.mMesh[j].applyVector(tagdata);
							}
						}
						
						if (s.toUpperCase().contains("HEAD"))
						{
							if (adapter.tagMap.get(Md3Loader.TAG_HEAD) != null)
							{
								float h[] = adapter.tagMap.get(Md3Loader.TAG_HEAD);
								
								float t[] = adapter.tagMap.get(Md3Loader.TAG_TORSO);
								
								//Apply rotation matrix to torso and then head
								Sd3dMatrix rotMat = new Sd3dMatrix(t, 3);
								obj.mMesh[j].applyMatrix(rotMat);
			
								Sd3dMatrix rotMat2 = new Sd3dMatrix(h, 3);
								obj.mMesh[j].applyMatrix(rotMat2);								
							
								obj.mMesh[j].applyVector(t);
								
								float res[] = new float[3];
								Sd3dMatrix.mul(res, rotMat, h);
								
								obj.mMesh[j].applyVector(res);
							}
						}			
						
						obj.mMesh[j].applyMatrix(transformMatrix);
						*/
						
					}
					
					float tagdata[][] = null;
					if (s.toUpperCase().contains("LOWER"))
						if (adapter.tagMap.get(Md3Loader.TAG_TORSO) != null)
						{
							tagdata = adapter.tagMap.get(Md3Loader.TAG_TORSO);
						}					
					
					
					if (s.toUpperCase().contains("UPPER"))
						if (adapter.tagMap.get(Md3Loader.TAG_TORSO) != null)
						{
							tagdata = adapter.tagMap.get(Md3Loader.TAG_HEAD);
						}						
					
					if (tagdata != null)
						animatorList.get(animatorId).addFrame(obj, 100, tagdata[frameId]);
					else
						animatorList.get(animatorId).addFrame(obj, 100, null);
				}
				
				animatorId++;
			}
			
			for (String s : adapter.materialMap.keySet())
			{
				System.out.println("Loading: "+s);
				Sd3dMaterial material = adapter.materialMap.get(s);
				material.loadTGATextureFromZip(pk3file, s);
				material.renderLight = true;
			}
			
			is = Sd3dRessourceManager.getManager().getRessource(pk3file);
			HashMap <String, String> skinData = md3loader.loadSkinData(is);
			is.close();
			Log.d("sd3d","Loading skin data "+adapter.shaderMap.keySet().size());
			for (String s : adapter.shaderMap.keySet())
			{
				Sd3dMaterial material = adapter.shaderMap.get(s);
				Log.d("sd3d",s+" "+skinData.get(s));
				material.loadTGATextureFromZip(pk3file, skinData.get(s));
				material.renderLight = true;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		
		return frameAnimatorArray;
	}
}
