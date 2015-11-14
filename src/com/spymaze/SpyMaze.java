package com.spymaze;

import android.app.Activity;
import android.content.Context;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.spymaze.enums.GameState;
import com.spymaze.game.GameHandler;
import com.spymaze.game.GameView;
import com.spymaze.game.task.LevelTask;
import com.spymaze.game.task.MainMenu;
import com.spymaze.level.Level;
import com.spymaze.utility.Utility;

import java.io.FileInputStream;

import static com.spymaze.utility.GameVariable.*;

public class SpyMaze extends Activity {

    /**
     * Version 1.1 of Spy Maze
     *
     * - Mission 2 debut
     * - Change main menu to Mission 2 sprites
     * - Make in game menu bigger
     * - Add selection of mission 2 in main menu
     */
	
	private final Context context = this;
	private AdView adView;
	private GameView gameView;
	private AdRequest adRequest;
	
	private RelativeLayout.LayoutParams lay;
	private RelativeLayout rl;
	
	private static boolean setup = false;
	private boolean active = false;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        active = true;
        
        adView = new AdView(this, AdSize.BANNER, "a150a0270607d46");
        adRequest = new AdRequest();
        adRequest.addTestDevice("32ECE9357E60A665ECFDC253076F40FA");
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_game); //Loading screen

		assetManager = getAssets();
		
		new GameLoader().execute(null, null, null); //Also starts the game
	}
	
	@Override
	public void onDestroy() {
		active = false;
		adView.destroy();
	    super.onDestroy();
	}
	
	private class GameLoader extends AsyncTask<Void, Void, Void> {

		@SuppressWarnings("deprecation")
		@Override
		protected Void doInBackground(Void... params) {
			System.out.println("Loading spy maze");
			
			if (!setup) {
				WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
				Display display = wm.getDefaultDisplay();
				
				broadwayBT = Typeface.createFromAsset(getAssets(), "fonts/TT0131M.TTF");

		        imgMenuLengthX = (screenWidth = display.getWidth()) / 15;
		        imgMenuLengthY = (screenHeight = display.getHeight()) / 10;

		        imgSideLength = Utility.roundUp(((float)screenHeight / 7.0F)); //Player will be able to see 7 tiles from up to down on the screen

		        mainMenuLevel = Level.loadAssetLevel(assetManager, "mainmenu", 2, screenWidth, screenWidth, imgMenuLengthX, imgMenuLengthY);
		        
		        setBitmaps(getResources());
		        
		        entireScreenRect = new RectF(0, 0, screenWidth, screenHeight);
			    
			    GameHandler.setup();
			    MainMenu.setup();
			    LevelTask.setup();
			    
			    mainMenuSong = MediaPlayer.create(context, R.raw.mainmenu);
			    mission1Song = MediaPlayer.create(context, R.raw.mission1song);
                mission2Song = MediaPlayer.create(context, R.raw.mission2song);
			    losingSound = MediaPlayer.create(context, R.raw.lostsound);
			    winningSound = MediaPlayer.create(context, R.raw.winsound);
			    clickSound = MediaPlayer.create(context, R.raw.buttonscroll);
			    audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			    
			    setup = true;
			}
		    
		    try {
				FileInputStream fis = context.openFileInput("levelsettings");
				
				byte[] buffer = new byte[fis.available()];
				
				fis.read(buffer);
				
				String buffered = new String(buffer);
				
				unlockedLevel = Integer.parseInt(buffered.split("\r\n")[0]);
				
				String starVals = buffered.split("\r\n")[1];
				
				for (int i=0; i<starValues.length; i++) {
                    for (int j=0; j<starValues[i].length; j++) {
					    starValues[i][j] = Integer.parseInt(starVals.split(",")[(i * 16) + j]);
                    }
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		    
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		    
			return null;
		}
		
		@Override
		protected void onPostExecute(Void v) {
			gameView = new GameView(context);
			
			rl = new RelativeLayout(context);
			
			lay = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			lay.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			
			rl.addView(gameView);
			//rl.addView(adView, lay);
			
			setContentView(rl);
			
			adView.loadAd(adRequest);
			
			adHandler.start();
		}
		
	}
	
	private Thread adHandler = new Thread() {
		
		private boolean added = false;
		
		@Override
		public void run() {
			Runnable adViewRunnable = new Runnable() {

				@Override
				public void run() {
					if (gameView != null && gameView.gameHandler != null) {
						if (gameView.gameHandler.currentState == GameState.IN_LEVEL
								&& !added) {
							rl.addView(adView, lay);
							added = true;
						} else if (gameView.gameHandler.currentState != GameState.IN_LEVEL
								&& added) {
							rl.removeView(adView);
							added = false;
						}
					}
				}
				
			};
			
			while (active) {
				runOnUiThread(adViewRunnable);
				
				try {
					Thread.sleep(2000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	};

}
