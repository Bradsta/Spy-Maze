package com.spymaze.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.AudioManager;

import com.spymaze.enums.GameState;
import com.spymaze.enums.StateButton;
import com.spymaze.game.task.LevelTask;
import com.spymaze.game.task.MainMenu;
import com.spymaze.level.Level;
import com.spymaze.level.LevelHandler;
import com.spymaze.utility.GameVariable;
import com.spymaze.utility.Utility;

public class GameHandler extends Thread {
	
	public GameState currentState = GameState.MAIN_MENU;
	public GameTask gameTask = null;
	public final GameView gameView;
	
	public int levelNumber = 1;
    public int missionNumber = 1;
	
	//The options menu/help menu will be accessible to the in game task as well.
	
	public String soundButtonText = "Music : on";

	public static RectF soundButtonRect = new RectF(320, 180, 483, 210);
	
	public static RectF miniMenuRect = new RectF(278, 120, 523, 420);
	public static RectF helpMenuRect = new RectF(5, 5, 795, 475);
	
	public static float miniMenuTitleFontSize;
	public static float soundButtonFontSize;
	
	public static Point optionsTitleLoc;
	public static Point helpMenuLoc;
	
	public Rect bounds = new Rect();
	public Point point = new Point();
	public String cachedString = null;
	
	public int currentX;
	public int currentY;
	
	//End of options menu/help menu related variables.
	
	public GameHandler(GameView gameView) {
		this.gameView = gameView;
	}
	
	@Override
	public void run() {
		System.out.println("Game handler thread started");
		
		while (gameView.active) {
			
			switch (currentState) {
			case LEVEL_SELECTED:
				Level selectedLevel = Level.loadAssetLevel(GameVariable.assetManager,
						"m" + missionNumber + "l" + levelNumber,
                        missionNumber,
						GameVariable.screenWidth,
						GameVariable.screenHeight,
						GameVariable.imgSideLength,
						GameVariable.imgSideLength);
				
				if (selectedLevel != null) {
					LevelTask levelTask = new LevelTask(this);					
					levelTask.levelHandler = new LevelHandler(selectedLevel);
					
					gameTask = levelTask;
				}
				
				break;
			case MAIN_MENU:
				gameTask = new MainMenu(this);
				break;
			default:
				gameTask = new MainMenu(this);
				break;
			}
			
			gameTask.run();
			
			System.gc();
		}
		
		System.out.println("Game handler stopped");
	}
	
	
	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * DRAWING METHODS FOLLOW
	 * 
	 * 
	 * 
	 * 
	 */
	
	public void drawHelpMenu(Canvas canvas, Paint paint) {
		drawBackground(canvas, paint, helpMenuRect);
		
		canvas.drawBitmap(GameVariable.HELP_MENU.bitmap, helpMenuLoc.x, helpMenuLoc.y, paint);
	}
	
	public void drawOptionsMenu(Canvas canvas, Paint paint, float currentX, float currentY) {
		drawBackground(canvas, paint, miniMenuRect);
		
		paint.setTextSize(miniMenuTitleFontSize);
		paint.setTypeface(Typeface.create(GameVariable.broadwayBT, Typeface.BOLD));
		
		paint.getTextBounds("OPTIONS", 0, 7, bounds);
		
		canvas.drawText("OPTIONS", optionsTitleLoc.x, optionsTitleLoc.y, paint);
		canvas.drawLine(optionsTitleLoc.x, optionsTitleLoc.y + 3, optionsTitleLoc.x + bounds.width(), optionsTitleLoc.y + 3, paint);
		
		paint.setTextSize(soundButtonFontSize);
		paint.setTypeface(Typeface.create(GameVariable.broadwayBT, Typeface.BOLD));
		
		//Color should still be white

		if (soundButtonRect.contains(currentX, currentY)) {
			paint.setStyle(Style.FILL);
			
			canvas.drawRoundRect(soundButtonRect, 5, 5, paint);
			paint.setColor(Color.BLACK);
			
			if (soundButtonText.contains("on")) {
				paint.getTextBounds("Music : off", 0, 11, bounds);
				point = Utility.getStringCentered(soundButtonRect, bounds);
				
				canvas.drawText("Music : off", point.x, point.y, paint);
			} else {
				paint.getTextBounds("Music : on", 0, 10, bounds);
				point = Utility.getStringCentered(soundButtonRect, bounds);
				
				canvas.drawText("Music : on", point.x, point.y, paint);
			}
		} else {
			paint.getTextBounds(soundButtonText, 0, soundButtonText.length(), bounds);
			point = Utility.getStringCentered(soundButtonRect, bounds);
			
			canvas.drawRoundRect(soundButtonRect, 5, 5, paint);
			canvas.drawText(soundButtonText, point.x, point.y, paint);
		}
	}
	
	public void drawStateButtons(Canvas canvas, Paint paint, float currentX, float currentY) {
		for (StateButton b : StateButton.values()) {				
			paint.setTextSize(b.fontSize);
			paint.setTypeface(Typeface.create(GameVariable.broadwayBT, b.fontStyle));
			paint.getTextBounds(b.buttonText, 0, b.buttonText.length(), bounds);
			point = Utility.getStringCentered(b.buttonRect, bounds);
			
			paint.setColor(Color.WHITE);
			if (currentState == b.currentState) {
				if (b.buttonRect.contains(currentX, currentY)) { //Because mission select buttons are picutres

                    if (currentState == GameState.MISSION_SELECTION) {
                        paint.setARGB(100, 255, 255, 255); //This adds some feed back to mission select buttons but doesn't fill it all with white
                    }

                    paint.setStyle(Style.FILL);
					canvas.drawRoundRect(b.buttonRect, 5, 5, paint);
					paint.setColor(b.buttonBackgroundColor);
					canvas.drawText(b.buttonText, point.x, point.y, paint);
				} else {
                    if (currentState != GameState.MISSION_SELECTION) {
                        paint.setColor(b.buttonBackgroundColor);
                        paint.setStyle(Style.FILL);
                        canvas.drawRoundRect(b.buttonRect, 5, 5, paint);
                    }
					
					paint.setColor(Color.WHITE);
					paint.setStyle(Style.STROKE);
					canvas.drawRoundRect(b.buttonRect, 5, 5, paint);
					canvas.drawText(b.buttonText, point.x, point.y, paint);
				}
			}
		}
	}
	
	public boolean doButtonClick(float currentX, float currentY) {
		for (int i=0; i<StateButton.values().length; i++) {
			if (currentState == StateButton.values()[i].currentState && StateButton.values()[i].buttonRect.contains(currentX, currentY)) {
                StateButton selectedButton = StateButton.values()[i];

				if (selectedButton == StateButton.OPTIONS_BUTTON) {
					StateButton.BACK_BUTTON_OPTIONS.nextState = GameState.MAIN_MENU;
				} else if (selectedButton == StateButton.HELP_BUTTON) {
					StateButton.BACK_BUTTON_HELP.nextState = GameState.MAIN_MENU;
				} else if (selectedButton == StateButton.GAME_OPTIONS_OPTIONS) {
					StateButton.BACK_BUTTON_OPTIONS.nextState = GameState.GAME_OPTIONS;
				} else if (selectedButton == StateButton.GAME_OPTIONS_HELP) {
					StateButton.BACK_BUTTON_HELP.nextState = GameState.GAME_OPTIONS;
				} else if (selectedButton == StateButton.MISSION_ONE) {
                    missionNumber = 1;
                } else if (selectedButton == StateButton.MISSION_TWO) {
                    missionNumber = 2;
                }

                if (selectedButton != StateButton.MISSION_TWO
                        || GameVariable.starValues[0][15] > 0) { //Checks if level 16 is done before you can go to mission 2
				    currentState = selectedButton.nextState;
                }
				
				//Pausing
				if (selectedButton == StateButton.GAME_OPTIONS) {
					((LevelTask) gameTask).levelHandler.paused = true;
				} else if (selectedButton == StateButton.BACK_BUTTON_GAME_OPTIONS) {
					((LevelTask) gameTask).levelHandler.paused = false;
				} else if (selectedButton == StateButton.BACK_BUTTON_HELP
						&& StateButton.BACK_BUTTON_HELP.nextState == GameState.IN_LEVEL) { // level one
					((LevelTask) gameTask).levelHandler.paused = false;
					StateButton.BACK_BUTTON_HELP.buttonText = "BACK";
				}
				
				if (GameVariable.clickSound != null) GameVariable.clickSound.start();
				
				return true;
			}
		}
		
		if (currentState == GameState.OPTIONS_MENU
				&& soundButtonRect.contains(currentX, currentY)) {
			if (soundButtonText.contains("on")) {
				soundButtonText = "Music : off";
				GameVariable.audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
				
				return true;
			} else {
				soundButtonText = "Music : on";
				GameVariable.audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
				
				return true;
			}
		}
		
		return false;
	}
	
	public void drawBackground(Canvas canvas, Paint paint, RectF rectf) {
		paint.setARGB(225, 0, 0, 0);
		paint.setStyle(Style.FILL);
		canvas.drawRoundRect(rectf, 5, 5, paint);
		
		paint.setColor(Color.WHITE);
		paint.setStyle(Style.STROKE);
		canvas.drawRoundRect(rectf, 5, 5, paint);
	}

    public static void setup() {
        for (int i=0; i<StateButton.values().length; i++) {
            Utility.resizeRectangle(StateButton.values()[i].buttonRect);

            StateButton.values()[i].fontSize = Utility.getXRatio(StateButton.values()[i].fontSize);
        }

        Utility.resizeRectangle(soundButtonRect);
        Utility.resizeRectangle(miniMenuRect);
        Utility.resizeRectangle(helpMenuRect);

        GameVariable.HELP_MENU.bitmap = Utility.getRatioedBitmap(GameVariable.HELP_MENU.bitmap, 760, 440);

        miniMenuTitleFontSize = Utility.getXRatio(30);
        soundButtonFontSize = Utility.getXRatio(17);

        optionsTitleLoc = Utility.getConfiguredPoint(328, 155);
        helpMenuLoc = Utility.getConfiguredPoint(20, 10);
    }

}
