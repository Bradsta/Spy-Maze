package com.spymaze.game;

import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.spymaze.utility.GameVariable;


public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	
	public GameHandler gameHandler;
	
	public boolean active = false;
	
	private final Context context;

	public GameView(Context context) {
		super(context);
		
		this.context = context;
		
		getHolder().addCallback(this);
		
		setFocusable(true);
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (gameHandler.gameTask != null
				&& canvas != null) {
			gameHandler.gameTask.onDraw(canvas);
		}
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		return event != null && gameHandler.gameTask != null && gameHandler.gameTask.onTouchEvent(event);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		System.out.println("Surface changed");
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		System.out.println("Surface created");
		
		active = true;
		
		(gameHandler = new GameHandler(this)).start();
		
		new CanvasPainter().start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		System.out.println("Surface destroyed");
		
		active = false;
		
		GameVariable.audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
		
		try {
			FileOutputStream fos = context.openFileOutput("levelsettings", Context.MODE_PRIVATE);
			
			fos.write((GameVariable.unlockedLevel + "\r\n").getBytes());
			
			for (int i=0; i<GameVariable.starValues.length; i++) {
                for (int j=0; j<GameVariable.starValues[i].length; j++) {
                    fos.write((GameVariable.starValues[i][j] + ",").getBytes());
                }
			}
			
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private class CanvasPainter extends Thread {
		
		@Override
		public void run() {
			System.out.println("Repaint thread started");
			
			Canvas canvas = null;
			SurfaceHolder holder = getHolder();
			
			while (active) {
				canvas = null;
				
				try {
					canvas = holder.lockCanvas();
					
					synchronized (holder) {
						onDraw(canvas);
					}
				} finally {
					if (canvas != null) {
						holder.unlockCanvasAndPost(canvas);
					}
				}

			}
			
			System.out.println("Repaint stopped");
		}
		
	}

}
