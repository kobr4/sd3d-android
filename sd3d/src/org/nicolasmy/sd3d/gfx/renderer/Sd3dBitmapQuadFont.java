package org.nicolasmy.sd3d.gfx.renderer;

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
	
	public void init(int width, int height) {
		backingBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		backingCanvas = new Canvas(backingBitmap);
		backingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		backingPixels = new int[width*height];
	}
	
	public void drawText(String text, int x, int y) {
		backingPaint.setColor(Color.WHITE); // Text Color
		backingPaint.setStrokeWidth(70); // Text Size
		backingPaint.setTextSize(20);
		backingPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
		backingCanvas.drawText(text, x, y, backingPaint);
	}
	
	public void frameEnd() {
		Paint paint = new Paint();
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		backingCanvas.drawPaint(paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
	}
	
	public int[] getPixels() {	
		backingBitmap.getPixels(backingPixels, 0, backingBitmap.getWidth(), 
				0, 0, backingBitmap.getWidth(), backingBitmap.getHeight());
		return backingPixels;
	}
}
