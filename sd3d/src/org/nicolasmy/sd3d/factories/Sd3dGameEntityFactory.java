package org.nicolasmy.sd3d.factories;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.nicolasmy.sd3d.gfx.Sd3dMaterial;
import org.nicolasmy.sd3d.gfx.Sd3dMesh;
import org.nicolasmy.sd3d.gfx.Sd3dObject;
import org.nicolasmy.sd3d.gfx.Sd3dObjectFrameAnimator;
import org.nicolasmy.sd3d.gfx.Sd3dRessourceManager;
import org.nicolasmy.sd3d.gfx.entity.Sd3dGameAnimatedEntity;
import org.nicolasmy.sd3d.gfx.entity.Sd3dGameMobileEntity;
import org.nicolasmy.sd3d.importer.md3.Md3Loader;
import org.nicolasmy.sd3d.importer.md3.Sd3dMd3LoaderAdapter;
import org.nicolasmy.sd3d.math.Sd3dMatrix;

import android.util.Log;

public class Sd3dGameEntityFactory {
	private static Sd3dGameMobileEntity buildMobile(HashMap map)
	{
		Sd3dGameMobileEntity entity = new Sd3dGameMobileEntity();
		return entity;
	}
	
	
	public static Sd3dGameMobileEntity createAnimatedEntityFromPK3(String pk3file)
	{
		Sd3dObjectFrameAnimator[] frameAnimatorList = Sd3dObjectFrameAnimatorFactory.createFrameAnimatorFromPK3(pk3file);
		Sd3dObjectFrameAnimator currentFrameAnimator = frameAnimatorList[0];
		for (int i = 1;i < frameAnimatorList.length;i++)
		{
			currentFrameAnimator.addChild(frameAnimatorList[i]);
			currentFrameAnimator = frameAnimatorList[i];
		}
		Sd3dGameAnimatedEntity entity = new Sd3dGameAnimatedEntity(frameAnimatorList[0]);
		entity.hasObject = true;
		return entity;
	}
	
	public static Sd3dGameMobileEntity createEntityFromPK3(String pk3file)
	{
		Sd3dGameMobileEntity entity = new Sd3dGameMobileEntity();
		
		Sd3dMd3LoaderAdapter adapter = new Sd3dMd3LoaderAdapter();
		
		Md3Loader md3loader = new Md3Loader();
		Sd3dMatrix rotationMatrix = Sd3dMatrix.getRotationMatrix(-90f, 0f, 0f);
		Sd3dMatrix transformMatrix;
		InputStream is;
		
		try {
			is = Sd3dRessourceManager.getManager().getRessource(pk3file);
			md3loader.loadMD3FromPK3(is, adapter);
			
			entity.mObject = new Sd3dObject();

			int totalMesh = 0;
			for(Object o : adapter.md3Map.keySet())
			{
				String s = (String)o;		
				ArrayList<ArrayList<Sd3dObject>> surfaceList  = (ArrayList<ArrayList<Sd3dObject>> )adapter.md3Map.get(s);
				totalMesh += surfaceList.size();
			}
			
			entity.mObject.mMesh = new Sd3dMesh[totalMesh];
			entity.mObject.mMaterial = new Sd3dMaterial[totalMesh];			
			
			int i = 0;
			for(Object o : adapter.md3Map.keySet())
			{
				String s = (String)o;
				Log.d("Sd3d","LOADING: "+s);
				ArrayList<ArrayList<Sd3dObject>> surfaceList  = (ArrayList<ArrayList<Sd3dObject>> )adapter.md3Map.get(s);
				
				for (int j = 0;j < surfaceList.size();j++)
				{
					transformMatrix = rotationMatrix.clone();
					entity.mObject.mMesh[i] = surfaceList.get(j).get(0).mMesh[0];
					entity.mObject.mMaterial[i] = surfaceList.get(j).get(0).mMaterial[0];
					/*
					if (s.toUpperCase().contains("UPPER"))
					{
						if (adapter.tagMap.get(Md3Loader.TAG_TORSO) != null)
						{
							float tagdata[] = adapter.tagMap.get(Md3Loader.TAG_TORSO);

							
							Sd3dMatrix rotMat = new Sd3dMatrix(tagdata, 3);
							entity.mObject.mMesh[i].applyMatrix(rotMat);
							
							entity.mObject.mMesh[i].applyVector(tagdata);
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
							entity.mObject.mMesh[i].applyMatrix(rotMat);
		
							Sd3dMatrix rotMat2 = new Sd3dMatrix(h, 3);
							entity.mObject.mMesh[i].applyMatrix(rotMat2);								
						
							entity.mObject.mMesh[i].applyVector(t);
							
							//entity.mObject.mMesh[i].applyVector(h);						
	
							
						
							float res[] = new float[3];
							Sd3dMatrix.mul(res, rotMat, h);
							
							entity.mObject.mMesh[i].applyVector(res);
						}
					}			
					
					entity.mObject.mMesh[i].applyMatrix(transformMatrix);
					*/
					i++;
				}
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
			
			entity.hasObject = true;
			
		
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return entity;
	}
	
}
