package com.spymaze.game.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.view.MotionEvent;

import com.spymaze.enums.DirectionalButton;
import com.spymaze.enums.GameState;
import com.spymaze.enums.StateButton;
import com.spymaze.game.GameHandler;
import com.spymaze.game.GameTask;
import com.spymaze.level.LevelHandler;
import com.spymaze.sprite.CharacterSprite;
import com.spymaze.sprite.LoadedTileSprite;
import com.spymaze.utility.GameVariable;
import com.spymaze.utility.Timer;
import com.spymaze.utility.Utility;

public class LevelTask implements GameTask {
	
	private final GameHandler gameHandler;
	private final Paint paint;
	
	public LevelHandler levelHandler; //Modified before run() is called.
	
	private ArrayList<CharacterSprite> loadedChars = new ArrayList<CharacterSprite>();
	private ArrayList<LoadedTileSprite> loadedTiles;
	private CharacterSprite cachedGuy;
	
	private Timer gameTimer;
	private int starsObtained;
	
	public LevelTask(GameHandler gameHandler) {
		this.gameHandler = gameHandler;
		this.paint = new Paint();
	}

	@Override
	public void run() {
		gameTimer = new Timer(System.currentTimeMillis());
		
		gameHandler.currentState = GameState.IN_LEVEL;
		
		if (gameHandler.levelNumber == 1) {
			gameHandler.currentState = GameState.HELP_MENU;
			StateButton.BACK_BUTTON_HELP.nextState = GameState.IN_LEVEL;
			StateButton.BACK_BUTTON_HELP.buttonText = "PLAY";
			levelHandler.paused = true;
		}
		
		levelHandler.start();

        MediaPlayer currentSong = gameHandler.missionNumber == 1 ? GameVariable.mission1Song : GameVariable.mission2Song;

        currentSong.seekTo(0);
        currentSong.start();
        currentSong.setLooping(true);
		
		try {
			while (gameHandler.gameView.active
					&& (levelHandler.doLevel || (levelHandler.won || levelHandler.lost))
					&& Arrays.binarySearch(GameState.nonGameStateNumbs, gameHandler.currentState.stateNumb) < 0) {
				if (levelHandler.lost) {
					if (currentSong.isPlaying()) {
                        currentSong.stop();
						GameVariable.losingSound.start();
					}
					
					gameHandler.currentState = GameState.LOST;
				} else if (levelHandler.won) {
					if (currentSong.isPlaying()) {
                        currentSong.stop();
						GameVariable.winningSound.start();
					}
					
					if (starsObtained == 0) {
						if (gameTimer.getTimeElapsed() <= ((GameVariable.THREE_STAR_VALUES[gameHandler.missionNumber-1][gameHandler.levelNumber-1] + 1) * 1000)) {
							starsObtained = 3;
						} else if (gameTimer.getTimeElapsed() <= ((GameVariable.THREE_STAR_VALUES[gameHandler.missionNumber-1][gameHandler.levelNumber-1] + 11) * 1000)) {
							starsObtained = 2;
						} else {
							starsObtained = 1;
						}
						
						if (GameVariable.starValues[gameHandler.missionNumber-1][gameHandler.levelNumber-1] < starsObtained) {
							GameVariable.starValues[gameHandler.missionNumber-1][gameHandler.levelNumber-1] = starsObtained;
						}
					}
					
					if ((gameHandler.missionNumber == 2 ? GameVariable.unlockedLevel-15 : GameVariable.unlockedLevel) <= gameHandler.levelNumber
							&& gameHandler.levelNumber != GameVariable.levelAmount) {
						GameVariable.unlockedLevel = (gameHandler.missionNumber == 2 ? gameHandler.levelNumber + 16 : gameHandler.levelNumber + 1);
					}
					
					gameHandler.currentState = GameState.WON;
				}
				
				if (gameTimer.isRunning()
						&& Arrays.binarySearch(GameState.pausedStateNumbs, gameHandler.currentState.stateNumb) > -1) {
					gameTimer.pause();
				} else if (gameTimer.isPaused()
						&& Arrays.binarySearch(GameState.pausedStateNumbs, gameHandler.currentState.stateNumb) < 0) {
					gameTimer.resume();
				}
				
				Thread.sleep(100);
			}
			
			if (gameHandler.currentState == GameState.NEXT_LEVEL) {
				if (gameHandler.levelNumber != GameVariable.levelAmount) {
					gameHandler.levelNumber++;
					
					gameHandler.currentState = GameState.LEVEL_SELECTED;
				} else {
					gameHandler.currentState = GameState.MAIN_MENU;
				}
			}
			
			if (currentSong.isPlaying()) currentSong.stop();
            currentSong.prepareAsync();

			levelHandler.doLevel = false;
			
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		
		if (canvas != null
				&& paint != null
				&& gameHandler.currentState != GameState.MAIN_MENU
				&& gameHandler.currentState != GameState.LEVEL_SELECTED) {			
			if (levelHandler != null
				&& levelHandler.level != null) {
				cachedGuy = levelHandler.guy.clone(); //Need to cache guy, otherwise there will be inconsistencies due to the multiple threading of the game.
				
				//So we don't have to update loaded tiles every time paint gets called
				if (cachedGuy.xLoc != cachedGuy.cachedXLoc || cachedGuy.yLoc != cachedGuy.cachedYLoc) {
					loadedTiles = levelHandler.level.getLoadedTiles(cachedGuy,
							loadedTiles,
							cachedGuy.xLoc - cachedGuy.cachedXLoc, cachedGuy.yLoc - cachedGuy.cachedYLoc);
					
					levelHandler.guy.cachedXLoc = cachedGuy.xLoc;
					levelHandler.guy.cachedYLoc = cachedGuy.yLoc;
				}
	
				if (loadedTiles != null) {
					for (LoadedTileSprite lts : loadedTiles) {
						canvas.drawBitmap(lts.sprite.bitmap, (lts.xLoc * GameVariable.imgSideLength) - cachedGuy.xOffset, (lts.yLoc * GameVariable.imgSideLength) - cachedGuy.yOffset, paint);
					}
				}
				
				loadedChars.clear();
				
				for (CharacterSprite cs : levelHandler.level.characterSprites) {
					CharacterSprite loadedCharSprite = levelHandler.level.getLoadedChar(cachedGuy, cs);
					
					if (loadedCharSprite != null) loadedChars.add(loadedCharSprite);
				}
				
				Collections.sort(loadedChars);
		
				for (CharacterSprite cs : loadedChars) {
					canvas.drawBitmap(cs.sprite.bitmap, ((cs.xLoc * GameVariable.imgSideLength) + cs.xOffset) - cachedGuy.xOffset, ((cs.yLoc * GameVariable.imgSideLength) + cs.yOffset) - cachedGuy.yOffset, paint);
				}
				
				cachedGuy.loadedCharSprite = levelHandler.level.getLoadedGuy(cachedGuy);
				
				canvas.drawBitmap(cachedGuy.loadedCharSprite.sprite.bitmap, (cachedGuy.loadedCharSprite.xLoc * GameVariable.imgSideLength), (cachedGuy.loadedCharSprite.yLoc * GameVariable.imgSideLength), paint);
				
				
				/************ END OF PAINTING OF LEVEL TILES AND SPRITES ************/
			
				paint.setTypeface(GameVariable.broadwayBT);
				
				/************ START OF PAINTING OF GAME TIMER *************/
				
				paint.setTextSize(timeSize);
				
				paint.setStyle(Style.STROKE);
				canvas.drawRect(timeRect, paint);
				paint.setARGB(150, 0, 0, 0);
				paint.setStyle(Style.FILL);
				canvas.drawRect(timeRect, paint);
				
				paint.setColor(Color.WHITE);
				canvas.drawText(Timer.timeElapsedToString(gameTimer.getTimeElapsed()), timeLoc.x, timeLoc.y, paint); //Time painting
				
				if (gameTimer.getTimeElapsed() <= ((GameVariable.THREE_STAR_VALUES[gameHandler.missionNumber-1][gameHandler.levelNumber-1] + 1) * 1000)) {
					canvas.drawText(Timer.timeElapsedToString(GameVariable.THREE_STAR_VALUES[gameHandler.missionNumber-1][gameHandler.levelNumber-1] * 1000) + " for 3 stars", timeGoalLoc.x, timeGoalLoc.y, paint);
				} else if (gameTimer.getTimeElapsed() <= ((GameVariable.THREE_STAR_VALUES[gameHandler.missionNumber-1][gameHandler.levelNumber-1] + 11) * 1000)) {
					canvas.drawText(Timer.timeElapsedToString((GameVariable.THREE_STAR_VALUES[gameHandler.missionNumber-1][gameHandler.levelNumber-1] + 10) * 1000) + " for 2 stars", timeGoalLoc.x, timeGoalLoc.y, paint);
				} else {
					canvas.drawText("Complete level for 1 star", timeGoalLoc.x, timeGoalLoc.y, paint);
				}
				
				/************ END OF PAINTING OF GAME TIMER *************/
				
				paint.setTextSize(DirectionalButton.arrowTextSize);
				
				for (int i=0; i<DirectionalButton.values().length; i++) {
					paint.setColor(Color.WHITE);
					paint.setStyle(Style.STROKE);
					
					canvas.drawRoundRect(DirectionalButton.values()[i].buttonRect, 5, 5, paint);
					
					if (DirectionalButton.values()[i].buttonRect.contains(gameHandler.currentX, gameHandler.currentY)
							&& !levelHandler.paused
							&& (!levelHandler.lost && !levelHandler.won)) {
						paint.setStyle(Style.FILL);
						canvas.drawRoundRect(DirectionalButton.values()[i].buttonRect, 5, 5, paint);
						paint.setColor(Color.BLACK);
					} else {
						paint.setColor(Color.BLACK);
						paint.setStyle(Style.FILL);
						paint.setARGB(150, 0, 0, 0);
						
						canvas.drawRoundRect(DirectionalButton.values()[i].buttonRect, 5, 5, paint);
						
						paint.setColor(Color.WHITE);
					}
					
					paint.getTextBounds(DirectionalButton.arrow, 0, 2, gameHandler.bounds);
					gameHandler.point = Utility.getStringCentered(DirectionalButton.values()[i].buttonRect, gameHandler.bounds);
					
					canvas.rotate(DirectionalButton.values()[i].rotation, DirectionalButton.values()[i].buttonRect.centerX(), DirectionalButton.values()[i].buttonRect.centerY());
					
					canvas.drawText(DirectionalButton.arrow, gameHandler.point.x, gameHandler.point.y, paint);
					
					canvas.rotate(-DirectionalButton.values()[i].rotation, DirectionalButton.values()[i].buttonRect.centerX(), DirectionalButton.values()[i].buttonRect.centerY());
				}
				
				if (gameHandler.currentState == GameState.GAME_OPTIONS) {
					gameHandler.drawBackground(canvas, paint, GameHandler.miniMenuRect);
					
					paint.setTextSize(GameHandler.miniMenuTitleFontSize);
					paint.setTypeface(Typeface.create(GameVariable.broadwayBT, Typeface.BOLD));
					
					paint.getTextBounds("OPTIONS", 0, 7, gameHandler.bounds);
					
					canvas.drawText("OPTIONS", GameHandler.optionsTitleLoc.x, GameHandler.optionsTitleLoc.y, paint);
					canvas.drawLine(GameHandler.optionsTitleLoc.x, GameHandler.optionsTitleLoc.y + 3,
							GameHandler.optionsTitleLoc.x + gameHandler.bounds.width(), GameHandler.optionsTitleLoc.y + 3, paint);
				} else if (gameHandler.currentState == GameState.OPTIONS_MENU) {
					gameHandler.drawOptionsMenu(canvas, paint, gameHandler.currentX, gameHandler.currentY);
				} else if (gameHandler.currentState == GameState.HELP_MENU) {
					gameHandler.drawHelpMenu(canvas, paint);
				}
				
				if (levelHandler.lost) {
					drawLosingMenu(canvas, paint);
				} else if (levelHandler.won) {
					drawWinningMenu(canvas, paint);
				}
				
				if (gameHandler.currentState != GameState.MAIN_MENU) gameHandler.drawStateButtons(canvas, paint, gameHandler.currentX, gameHandler.currentY);
			}
		} else if (gameHandler.currentState == GameState.LEVEL_SELECTED) {
			canvas.drawColor(Color.BLACK);
			
			paint.setColor(Color.WHITE);
			paint.setTextSize(MainMenu.titleTextSize);
			
			gameHandler.cachedString = "Level " + gameHandler.levelNumber;
			
			paint.getTextBounds(gameHandler.cachedString, 0, gameHandler.cachedString.length(), gameHandler.bounds);
			gameHandler.point = Utility.getStringCentered(GameVariable.entireScreenRect, gameHandler.bounds);
			
			canvas.drawText(gameHandler.cachedString, gameHandler.point.x, gameHandler.point.y, paint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gameHandler.currentX = (int) event.getX();       
		gameHandler.currentY = (int) event.getY();

		if (event.getAction() == MotionEvent.ACTION_UP) {
			gameHandler.doButtonClick(gameHandler.currentX, gameHandler.currentY);
			
			if (levelHandler != null
					&& levelHandler.level != null
					&& levelHandler.doLevel) {
				levelHandler.currentDirection = null;
				levelHandler.nextDirection = null;
			}
		
			gameHandler.currentX = -1;
			gameHandler.currentY = -1;
		} else if (event.getAction() == MotionEvent.ACTION_DOWN
				|| event.getAction() == MotionEvent.ACTION_MOVE) {
			if (levelHandler != null
					&& levelHandler.level != null
					&& levelHandler.doLevel) {
				for (int i=0; i<DirectionalButton.values().length; i++) {
					if (DirectionalButton.values()[i].buttonRect.contains(gameHandler.currentX, gameHandler.currentY)) {
						if (levelHandler.currentDirection != DirectionalButton.values()[i].direction) {
							levelHandler.currentDirection = null;
							levelHandler.nextDirection = null;
						}
						
						if (levelHandler.currentDirection == null
								&& levelHandler.guy.xOffset == 0
								&& levelHandler.guy.yOffset == 0) {
							levelHandler.currentDirection = DirectionalButton.values()[i].direction;
							
							levelHandler.lastDirection = levelHandler.currentDirection;
						}
						
						levelHandler.nextDirection = DirectionalButton.values()[i].direction;
					}
				}
			}
		}
		
		return true;
	}

	private static Point losingEnemyLoc;
	
	private static Bitmap filledStar;
	private static Bitmap emptyStar;
	private static Point firstStarLoc;
	public static float spaceBetweenStars; //Used in main menu
	
	private static float losingTitleSize;
	private static float winningTitleSize;
	
	private static float timeSize;
	private static Point timeLoc;
	private static Point timeGoalLoc;
	private static RectF timeRect = new RectF(5, 5, 227, 40);
	
	private void drawLosingMenu(Canvas canvas, Paint paint) {
		gameHandler.drawBackground(canvas, paint, GameHandler.miniMenuRect);
		
		paint.setTextSize(losingTitleSize);
		paint.setTypeface(Typeface.create(GameVariable.broadwayBT, Typeface.BOLD));
		
		paint.getTextBounds("You've been spotted!", 0, 20, gameHandler.bounds);
		gameHandler.point = Utility.getStringCentered(GameHandler.miniMenuRect, gameHandler.bounds);
		
		canvas.drawText("You've been spotted!", gameHandler.point.x, GameHandler.miniMenuRect.top + gameHandler.bounds.height() + 20, paint);
		
		canvas.drawBitmap(gameHandler.missionNumber == 1 ? GameVariable.M1_ENEMY_CARTOON.bitmap : GameVariable.M2_ENEMY_CARTOON.bitmap, losingEnemyLoc.x, losingEnemyLoc.y, paint);
	}
	
	private void drawWinningMenu(Canvas canvas, Paint paint) {
		gameHandler.drawBackground(canvas, paint, GameHandler.miniMenuRect);
		
		paint.setTextSize(winningTitleSize);
		paint.setTypeface(Typeface.create(GameVariable.broadwayBT, Typeface.BOLD));
		
		paint.getTextBounds("Level completed!", 0, 16, gameHandler.bounds);
		gameHandler.point = Utility.getStringCentered(GameHandler.miniMenuRect, gameHandler.bounds);
		
		canvas.drawText("Level completed!", gameHandler.point.x, GameHandler.miniMenuRect.top + gameHandler.bounds.height() + 20, paint);
		
		for (int i=0; i<3; i++) {
			if (i+1 <= starsObtained) {
				canvas.drawBitmap(filledStar, firstStarLoc.x + (i * (filledStar.getWidth() + spaceBetweenStars)), firstStarLoc.y, paint);
			} else {
				canvas.drawBitmap(emptyStar, firstStarLoc.x + (i * (emptyStar.getWidth() + spaceBetweenStars)), firstStarLoc.y, paint);
			}
		}
	}

	public static void setup() {
		for (int i=0; i<DirectionalButton.values().length; i++) {
            Utility.resizeRectangle(DirectionalButton.values()[i].buttonRect);
			
			DirectionalButton.arrowTextSize = Utility.getXRatio(40);
		}
		
		winningTitleSize = Utility.getXRatio(20);
		losingTitleSize = Utility.getXRatio(18);
		timeSize = Utility.getXRatio(15);
		
		spaceBetweenStars = Utility.getXRatio(10);

        GameVariable.M1_ENEMY_CARTOON.bitmap = Utility.getRatioedBitmap(GameVariable.M1_ENEMY_CARTOON.bitmap, 128, 128);
        GameVariable.M2_ENEMY_CARTOON.bitmap = Utility.getRatioedBitmap(GameVariable.M2_ENEMY_CARTOON.bitmap, 128, 128);

		filledStar = Utility.getRatioedBitmap(GameVariable.FILLED_STAR.bitmap, 65, 65);
		emptyStar = Utility.getRatioedBitmap(GameVariable.EMPTY_STAR.bitmap, 65, 65);
		
		losingEnemyLoc = Utility.getConfiguredPoint(340, 170);
		timeLoc = Utility.getConfiguredPoint(10, 20);
		timeGoalLoc = Utility.getConfiguredPoint(10, 35);
		firstStarLoc = Utility.getConfiguredPoint(294, 170);

        Utility.resizeRectangle(timeRect);
	}
	
}
