package com.spymaze.game;

import android.graphics.Canvas;
import android.view.MotionEvent;

public interface GameTask {

	public void onDraw(Canvas canvas);
	
	public boolean onTouchEvent(MotionEvent event);
	
	public void run();
	
}
