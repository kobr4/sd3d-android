package org.nicolasmy.sd3d;

import java.io.IOException;

import java.util.*;
import org.nicolasmy.sd3d.gfx.Sd3dRessourceManager;

import android.util.Log;

public class Sd3dConfig {

	private static Properties config;
	private static Object lockObj = new Object();
	public static String getString(String key)
	{
		synchronized(lockObj)
		{
			if (config == null)
			{
			
				config = new Properties();	
				try 
				{
					config.load(Sd3dRessourceManager.getManager().getRessource("config/sd3d.properties"));
					Log.d("sd3d","config/sd3d.properties LOADED "+config.toString());
				}
				catch (IOException ex)
				{
					Log.e("sd3d","error while loading sd3d configuration file : "+ex.toString());
					Log.e("sd3d","A config/sd3d.properties should be placed in the asset directory");
				}
			}
		}
		return config.getProperty(key);
	}
}
