package org.nicolasmy.sd3d.gfx.renderer;

import org.nicolasmy.sd3d.utils.Sd3dLogger;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class Sd3dBitmapQuadFont {
	Bitmap backingBitmap;
	Canvas backingCanvas;
	Paint backingPaint;
	int backingPixels[];
	int currentWidth = 0;
	int currentHeight = 0;
	
	public synchronized void init(int width, int height) {
		Sd3dLogger.log("Sd3dBitmapQuadFont Init W="+width+" H="+height);
		
		if ((currentWidth != width)&&(currentHeight != height)){
			if (backingBitmap != null) {
				backingBitmap.recycle();
			}
			
			backingBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			backingCanvas = new Canvas(backingBitmap);
			backingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			backingPixels = new int[width*height];
			
			currentWidth = width;
			currentHeight = height;	
		}
	}
	
	public synchronized void drawText(String text, int x, int y) {
		backingPaint.setColor(Color.WHITE); // Text Color
		backingPaint.setStrokeWidth(70); // Text Size
		backingPaint.setTextSize(20);
		backingPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
		backingCanvas.drawText(text, x, y, backingPaint);
	}
	
	public synchronized void frameEnd() {
		Paint paint = new Paint();
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		backingCanvas.drawPaint(paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
	}
	
	public synchronized int[] getPixels() {	
		backingBitmap.getPixels(backingPixels, 0, backingBitmap.getWidth(), 
				0, 0, backingBitmap.getWidth(), backingBitmap.getHeight());
		return backingPixels;
	}
}
