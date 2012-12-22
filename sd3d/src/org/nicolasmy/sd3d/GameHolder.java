package org.nicolasmy.sd3d;


public class GameHolder {
	public static Sd3dGame mGame; 
	
	public static void startAsyncLoad(Runnable runnable){
		Thread t = new Thread(runnable);
		t.start();
	}
}
