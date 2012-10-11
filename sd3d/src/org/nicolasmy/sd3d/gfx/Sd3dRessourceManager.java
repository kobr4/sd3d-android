package org.nicolasmy.sd3d.gfx;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class Sd3dRessourceManager {

	private Activity activity;
	private AssetManager mAssetManager;

	private ActivityManager mActivityManager;
	
	private static Sd3dRessourceManager Manager;

	public Sd3dRessourceManager(Activity activity) {
		this.activity = activity;
		mActivityManager = (ActivityManager) this.activity.getSystemService(Context.ACTIVITY_SERVICE);
	}

	public Sd3dRessourceManager(Context context) {
		mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	}	
	
	public InputStream getRessource(String name) throws IOException
	{
		if (activity != null)
		{
			AssetManager am = activity.getBaseContext().getAssets();
			InputStream fis = am.open(name);
			return fis;
		}
		else
		{
			InputStream fis = mAssetManager.open(name);
			return fis;
		}
	}

	public String[] getRessourceList(String path) throws IOException
	{
		AssetManager am = activity.getBaseContext().getAssets();
		return am.list(path);
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	
	public String getText(String name)
	{
		String ret = null;
		try {
			InputStream is = this.getRessource(name);
			int length = is.available();
			byte[] data = new byte[length];
			is.read(data);
			ret = new String(data,"UTF-8");
			
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("Sd3dRessourceManager","Unable to load file: "+name);
		}
		return ret;
	}

	public ActivityManager getActivityManager() {
		return mActivityManager;
	}

	public void setActivityManager(ActivityManager mActivityManager) {
		this.mActivityManager = mActivityManager;
	}

	
	public AssetManager getAssetManager() {
		return mAssetManager;
	}

	public void setAssetManager(AssetManager assetManager) {
		this.mAssetManager = assetManager;
	}

	public static Sd3dRessourceManager getManager() {
		return Manager;
	}

	public static void setManager(Sd3dRessourceManager manager) {
		Manager = manager;
	}

	public static void init(Activity activity)
	{
		Manager = new Sd3dRessourceManager(activity);
	}
	
}
