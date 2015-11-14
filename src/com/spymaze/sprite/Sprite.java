package com.spymaze.sprite;

import com.spymaze.utility.Utility;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Sprite {

	public Bitmap bitmap;
	public Bitmap menuBitmap;
	
	public final int drawableID;
	
	public Sprite(int drawableID) {
		this.drawableID = drawableID;
	}
	
	public void setBitmap(Resources res) {
		bitmap = BitmapFactory.decodeResource(res, drawableID);
	}
	
	public void setBitmap(Resources res, float sideLength) {
		bitmap = Utility.getResizedBitmap(BitmapFactory.decodeResource(res, drawableID), (int) sideLength, (int) sideLength);
	}
	
	public void setBitmap(Resources res, float sideLength, float menuLengthX, float menuLengthY) {
		bitmap = Utility.getResizedBitmap(BitmapFactory.decodeResource(res, drawableID), (int) sideLength, (int) sideLength);
		menuBitmap = Utility.getResizedBitmap(BitmapFactory.decodeResource(res, drawableID), (int) menuLengthX, (int) menuLengthY);
	}

}
