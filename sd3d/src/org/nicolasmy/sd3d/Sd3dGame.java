package org.nicolasmy.sd3d;

import java.util.ArrayList;
import java.util.Random;

import javax.microedition.khronos.opengles.GL11;

import org.nicolasmy.sd3d.Sd3dGLSurfaceView;
import org.nicolasmy.sd3d.gfx.Sd3dLensFlareEntity;
import org.nicolasmy.sd3d.gfx.Sd3dLight;
import org.nicolasmy.sd3d.gfx.Sd3dObject;
import org.nicolasmy.sd3d.gfx.Sd3dRessourceManager;
import org.nicolasmy.sd3d.gfx.Sd3dScene;
import org.nicolasmy.sd3d.gfx.entity.Sd3dCloudsEntity;
import org.nicolasmy.sd3d.gfx.entity.Sd3dGameChaseCameraEntity;
import org.nicolasmy.sd3d.gfx.entity.Sd3dGameCameraEntity;
import org.nicolasmy.sd3d.gfx.entity.Sd3dGameEntity;
import org.nicolasmy.sd3d.gfx.entity.Sd3dGameLookAroundCameraEntity;
import org.nicolasmy.sd3d.gfx.entity.Sd3dGameMobileEntity;
import org.nicolasmy.sd3d.gfx.entity.Sd3dGameSkyBoxEntity;
import org.nicolasmy.sd3d.gfx.entity.Sd3dGameSkyBoxEntityPerFace;
import org.nicolasmy.sd3d.gfx.entity.Sd3dPlaneEntity;
import org.nicolasmy.sd3d.gfx.renderer.Sd3dRenderer;
import org.nicolasmy.sd3d.gfx.renderer.Sd3dRendererGl20;
import org.nicolasmy.sd3d.gfx.renderer.Sd3dRendererInterface;
import org.nicolasmy.sd3d.interfaces.Sd3dCollisionAgainstInterface;
import org.nicolasmy.sd3d.interfaces.Sd3dFrameProcessorInterface;
import org.nicolasmy.sd3d.math.Sd3dVector2d;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;


/*
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.view.MotionEvent;
*/
/*
import com.sd3d.entity.Sd3dGameChaseCameraEntity;
import com.sd3d.entity.Sd3dGameCubeEntity;
import com.sd3d.entity.Sd3dGameEntity;
import com.sd3d.entity.Sd3dGameLookAroundCameraEntity;
import com.sd3d.entity.Sd3dGameSkyBoxEntity;
import com.sd3d.entity.Sd3dGameTestEntity;
import com.sd3d.entity.Sd3dParticlesEffectEntity;
import com.sd3d.entity.Sd3dRandomMovingParticlesEffectEntity;
*/

public class Sd3dGame
{	
	public enum Sd3dInputEventType{ACCELEROMETER,TOUCH,KEYBOARD};
	public boolean invalidateRenderElements;
	private ArrayList<Sd3dFrameProcessorInterface> mArray = new ArrayList<Sd3dFrameProcessorInterface>();
	public class Sd3dInputEvent
	{
		public boolean isActive;
		public Sd3dInputEventType mType;
		public int key;
		public SensorEvent mEvent;
		public MotionEvent mTouchEvent;
		public int deviceRotation;
	}
	
	public void addFrameProcessor(Sd3dFrameProcessorInterface processor)
	{
		mArray.add(processor);
	}
	
	public Sd3dFrameProcessorInterface getFrameProcessor(int i)
	{
		return mArray.get(i);
	}
	
	public class Sd3dGameEntityManager
	{
		public Sd3dGameEntity mEntityList[];
		private int mMaxEntity;
		long mLastFrameTime = 0;
		long mElapsedTime = 0;
		
		
		Sd3dGameEntityManager(int maxEntity)
		{
			mMaxEntity = maxEntity;
			mEntityList = new Sd3dGameEntity[mMaxEntity];
		}
		
		public void addEntity(Sd3dGameEntity entity)
		{
			for (int i = 0;i < mMaxEntity;i++)
			{
				if (mEntityList[i] == null)
				{
					mEntityList[i] = entity;
					return;
				}
			}
		}
		
		public void reset()
		{
			for (int i = 0;i < mMaxEntity;i++)
			{
				mEntityList[i] = null;
			}
		}
		
		public void processFrame(Sd3dScene scene,Sd3dInputEvent keyboardEvent,Sd3dGame game)
		{	
			int currentTime = 0;
			long lcurrenT = 0;
			long elapsedTime = 0;
			do {
			if (mLastFrameTime == 0)
				mLastFrameTime = java.lang.System.currentTimeMillis();
			lcurrenT = java.lang.System.currentTimeMillis();
			elapsedTime = lcurrenT - mLastFrameTime;
			} while (elapsedTime < 16);
			mElapsedTime = elapsedTime;
			mLastFrameTime = lcurrenT;
			for (int i=0;i < mMaxEntity;i++)
			{
				if (mEntityList[i] != null)
					if (mEntityList[i].isMarkForDeletion)
					{
						mEntityList[i].release(game);
						mEntityList[i] = null;
					}
					else
					if(mEntityList[i].isActive)
					{
					if (mEntityList[i].hasOnProcessFrame)
					{
						mEntityList[i].onProcessFrame((int)mElapsedTime);
					}
					
					if (mEntityList[i].isCamera)
					{
						scene.setCamera(mEntityList[i].getPosition(),mEntityList[i].getOrientation(),((Sd3dGameCameraEntity)mEntityList[i]).rotationMatrix);
					}
					
					if (mEntityList[i].hasObject)
					{
						if (invalidateRenderElements)
						{
							for (int j = 0; j< mEntityList[i].mObject.mMesh.length;j++)
							  mEntityList[i].mObject.mRenderElement = null;
							for (int l = 0;l < mEntityList[i].mObject.mMaterial.length;l++)
							  mEntityList[i].mObject.mMaterial[l].mColorName = 0;
							if (mEntityList[i].mObject.mIsPickable)
							{
								mEntityList[i].mObject.pickedMaterial.mColorName = 0;
								mEntityList[i].mObject.prepickedMaterial.mColorName = 0;
								mEntityList[i].mObject.unpickedMaterial.mColorName = 0;
							}
						}
						scene.addObject(mEntityList[i].mObject);
					}
					
					if (mEntityList[i].hasProcessTimer)
					{
						if (mEntityList[i].mLastTimer + mEntityList[i].mNextTimer > currentTime)
						{
							mEntityList[i].onProcessTimer();
							mEntityList[i].mLastTimer = currentTime;
						}
					}

					
					if (mAccelEvent.isActive)
					{
						if (mEntityList[i].hasOnAccelerometerEvent)
						{
							if (mEntityList[i].isReceiveInput())
							{
								
								
								mEntityList[i].onAccelerometerEvent(mAccelEvent);
							}
						}						
					}
					if (mTouchEvent.isActive)
					{
						if (mEntityList[i].hasOnTouchEvent)
						{
							if (mEntityList[i].isReceiveInput())
							{							
								mEntityList[i].onTouchEvent();
							}
						}						
					}					
					
					if (keyboardEvent != null)
					{
						if (mEntityList[i].hasOnKeyboardEvent)
						{
							mEntityList[i].onKeyboardEvent(keyboardEvent);
						}						
					}
					
					if (mEntityList[i] instanceof Sd3dCollisionAgainstInterface)
					{
						Sd3dCollisionAgainstInterface collisioner = (Sd3dCollisionAgainstInterface)mEntityList[i];
						for (int j=0;j < mMaxEntity;j++)
						{
							if ((mEntityList[j] != null)&&(mEntityList[j].isActive))
								if (mEntityList[j] instanceof Sd3dGameMobileEntity)
									collisioner.collideAgainst((Sd3dGameMobileEntity)mEntityList[j]);
						}
					}
					
					if (mEntityList[i] instanceof Sd3dLight)
					{
						scene.addLight((Sd3dLight)mEntityList[i]);
					}
				}

			}
			
			mTouchEvent.isActive = false;			
			mAccelEvent.isActive = false;
			
			if (invalidateRenderElements == true)
			{
				game.mRenderer.invalidateRendererElements();
				invalidateRenderElements = false;
			}
		}
		
	}	
	Sd3dInputEvent mKeyboardEvent;
	Sd3dInputEvent mAccelEvent = new Sd3dInputEvent();
	Sd3dInputEvent mTouchEvent = new Sd3dInputEvent();
	private Sd3dScene mScene;
	public Sd3dRendererInterface mRenderer;
	private Sd3dGameEntityManager mGameEntityManager;
	private Sd3dGLSurfaceView mGLSurfaceView; 
	private int screenWidth;
	private int screenHeight;
	int mMaxScene = 5000;
	int nMaxEntity = 5000;
	private long mTick;
	private Sd3dGameSkyBoxEntityPerFace mSkybox;
	private Sd3dGameChaseCameraEntity mCamera;
	/*
	public void cameraZoomIn()
	{
		this.mCamera.zoomIn();
	}
	
	public void cameraZoomOut()
	{
		this.mCamera.zoomOut();	
	}	
	
	public void cameraRotateLeft()
	{
		this.mCamera.rotateLeft();		
	}		
	
	public void cameraRotateRight()
	{
		this.mCamera.rotateRight();	
	}	
	*/
	public void updateRenderer(Activity activity)
	{
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);	
        mDeviceRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		this.mRenderer.updateScreenResolution(dm.widthPixels,dm.heightPixels);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
	}
	
	public void init(SurfaceHolder holder)
	{
		Log.d("Sd3dGame","init from holder");	
		Rect rect = holder.getSurfaceFrame();
		this.mRenderer = new Sd3dRendererGl20(true,mMaxScene,rect.width(),rect.height());
		this.mScene = new Sd3dScene(mMaxScene);
		this.mGameEntityManager = new Sd3dGameEntityManager(nMaxEntity);
		this.mTick = java.lang.System.currentTimeMillis();
	}
	
	
	public void init(Activity activity)
	{
		Log.d("Sd3dGame","init()");
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);		
     
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);	
        mDeviceRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		
		this.mScene = new Sd3dScene(mMaxScene);
		//this.mRenderer = new Sd3dRenderer(true,mMaxScene,dm.widthPixels,dm.heightPixels);
		
		final ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
	    final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
	    final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
		if ((supportsEs2))
		{
			Log.d("Sd3dGame","Using OpenGL ES 2.0 renderer");
			this.mRenderer = new Sd3dRendererGl20(true,mMaxScene,dm.widthPixels,dm.heightPixels);
		}
		else
		{
			Log.d("Sd3dGame","Using OpenGL ES 1.1 renderer");
			this.mRenderer = new Sd3dRenderer(true,mMaxScene,dm.widthPixels,dm.heightPixels);
		}
		
		this.mGameEntityManager = new Sd3dGameEntityManager(nMaxEntity);

		//this.mTouchEvent = new Sd3dInputEvent();
		this.mTick = java.lang.System.currentTimeMillis();
	}
	
	public long lasttick = 0;
	public int count = 0;
	public int calculatedFps = 0;
	
	public boolean mHasPick = false;
	public boolean mHasPrePick = false;
	public int mPickCount = 0;
	public int mPickX;
	public int mPickY;	
	public int mDeviceRotation;
	//public boolean mToto = false;
	private boolean mShowFps = false;
	
	
	public boolean isShowFps() {
		return mShowFps;
	}

	public void setShowFps(boolean showFps) {
		this.mShowFps = showFps;
	}

	public void processFrame(GL11 gl)
	{
		//mRenderer.mGl = gl;
		mRenderer.setGL11Context(gl);
		mScene.initScene();
		
		mGameEntityManager.processFrame(mScene,mKeyboardEvent,this);
	
		if (lasttick == 0)
			lasttick = java.lang.System.currentTimeMillis();		
		else
		{
			if (count == 10)
			{
									
				lasttick = java.lang.System.currentTimeMillis() - lasttick;
				float fps = 10000.f/(float)lasttick;
				calculatedFps = (int)fps;			
				lasttick = java.lang.System.currentTimeMillis();
				count = 0;
			}
		}
		//mRenderer.mBmpFont.addTextToBuffer(""+calculatedFps, 0.0f, 760.0f, 20.f);
		
		if (mShowFps)
			mRenderer.displayText(""+calculatedFps, Sd3dRenderer.ALIGN.RIGHT, Sd3dRenderer.ALIGN.BOTTOM, 20.f);
		
		//mRenderer.mBmpFont.addTextToBuffer(""+calculatedFps, 0.f,0.f, 0.2f);
		count++;
		
		//mRenderer.mBmpFont.addTextToBuffer("TIME:"+this.getElapsedTimeString(), Sd3dRenderer.ALIGN.LEFT,Sd3dRenderer.ALIGN.TOP, 20.f);
		
		for(Sd3dFrameProcessorInterface processor : this.mArray)
		{
			processor.processFrame();
		}


		mRenderer.renderScene(mScene);	
		
		
		//mAccelEvent = null;
		//mTouchEvent = null;
		mKeyboardEvent = null;
	}

	public void processPickingFrame(GL11 gl)
	{
		//mRenderer.mGl = gl;
		mRenderer.setGL11Context(gl);
		//mRenderer.renderPickableScene(mScene);	
	}
	
	public void processPrePicking(GL11 gl)
	{
	}
	
	public void processPicking(GL11 gl)
	{
	}
	
	public Sd3dGLSurfaceView getMGLSurfaceView() {
		return mGLSurfaceView;
	}

	public void setMGLSurfaceView(Sd3dGLSurfaceView surfaceView) {
		mGLSurfaceView = surfaceView;
	}
	
    public void onPause() {
    	mGLSurfaceView.onPause();
    }

    /**
     * Inform the view that the activity is resumed.
     */

    public void onResume() {
    	mGLSurfaceView.onResume();
    }

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	public void onSensorChanged(SensorEvent event) {
		Log.d("onSensorChanged()","");
    	if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
    	{
    		//if (mAccelEvent == null)
    		{
    			//Sd3dInputEvent accel_event = new Sd3dInputEvent();
    			mAccelEvent.mEvent = event;
    			mAccelEvent.mType = Sd3dInputEventType.ACCELEROMETER;
    			mAccelEvent.isActive = true;
    			mAccelEvent.deviceRotation = this.mDeviceRotation;
    		}
    	}		
	}	
	
	public void addEntity(Sd3dGameEntity entity)
	{
		this.mGameEntityManager.addEntity(entity);
	}	
	
	
	public float mTouchDownX;
	public float mTouchDownY;
	public float mTouchMoveX;
	public float mTouchMoveY;	
	public float mTouchUpX;
	public float mTouchUpY;
	public float mTouchThreshold;
	public float mFingerOneMoveX;
	public float mFingerTwoMoveX;
	public float mFingerOneMoveY;
	public float mFingerTwoMoveY;	
	public float mFingerOneMoveOldX;
	public float mFingerTwoMoveOldX;
	public float mFingerOneMoveOldY;
	public float mFingerTwoMoveOldY;	
	public float mFingerDistanceOld;
	public Sd3dVector2d mFingerOldVector;
	public Sd3dVector2d mFingerCurrentVector = new Sd3dVector2d();
	
	public float distance(float x1, float y1, float x2, float y2)
	{
		return (float)Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
	}
	
	public void onTouch(MotionEvent event)
	{	
		Log.d("onTouch(MotionEvent event)","touch");
		if (!mTouchEvent.isActive)
		{
			//Sd3dInputEvent touch_event = new Sd3dInputEvent();
			mTouchEvent.isActive = true;
			mTouchEvent.mTouchEvent = event;
			mTouchEvent.mType = Sd3dInputEventType.TOUCH;
			//mTouchEvent = touch_event;
		}		
	}
	
	
	public void onKeyUp()
	{
		
		if (mKeyboardEvent == null)
		{		
			
			mKeyboardEvent = new Sd3dInputEvent();
			mKeyboardEvent.key = 1;
			mKeyboardEvent.mType = Sd3dInputEventType.KEYBOARD;		
		}
		
		//this.mGuiManager.onEvent("UP");
	}
	
	public void onKeyDown()
	{
		
		if (mKeyboardEvent == null)
		{		
			
			mKeyboardEvent = new Sd3dInputEvent();
			mKeyboardEvent.key = 3;
			mKeyboardEvent.mType = Sd3dInputEventType.KEYBOARD;		
		}
	}	
	
	public void onKeyRight()
	{
		if (mKeyboardEvent == null)
		{		
			mKeyboardEvent = new Sd3dInputEvent();
			mKeyboardEvent.key = 2;
			mKeyboardEvent.mType = Sd3dInputEventType.KEYBOARD;	
		}
	}
	
	public void onKeyLeft()
	{
		if (mKeyboardEvent == null)
		{
			mKeyboardEvent = new Sd3dInputEvent();
			mKeyboardEvent.key = 4;
			mKeyboardEvent.mType = Sd3dInputEventType.KEYBOARD;
		}
	}	
}



