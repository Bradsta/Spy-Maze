package com.spymaze.game.task;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.spymaze.enums.GameState;
import com.spymaze.enums.StateButton;
import com.spymaze.game.GameHandler;
import com.spymaze.game.GameTask;
import com.spymaze.level.LevelHandler;
import com.spymaze.sprite.CharacterSprite;
import com.spymaze.utility.GameVariable;
import com.spymaze.utility.Utility;

public class MainMenu implements GameTask {
	
	private final GameHandler gameHandler;
	private final Paint paint;
	private final LevelHandler levelHandler;
	
	public static float titleTextSize;
	private static Point titleTextLoc;
	
	private static RectF levelSelectRect_1 = new RectF(30, 20, 770, 460); //Background
    private static Point levelSelectTitleLoc;
	private static float levelSelectTitleSize;
	
	private static RectF levelSelectRect_2 = new RectF(110, 75, 690, 405); //Inside rect

    private static Point missionSelectTitleLoc;
    private static Point completeMissionOneTextLoc;
    private static float completeMissionOneTextSize;
    private static Point missionOnePicLoc;
    private static Point missionTwoPicLoc;

	private static RectF previousPageRect = new RectF(34, 216, 105, 260);
	private static RectF nextPageRect = new RectF(696, 216, 767, 260);
	
	private static float[] levelSelectorXs = { 110, 255, 400, 545 };
	private static float[] levelSelectorYs = { 74, 239.5F };
	private static RectF[] levelSelectRects = new RectF[8]; //8 Rects generated from Xs/Ys
	private static float levelSelectNumbSize;

	private static Bitmap filledStar;
	private static Bitmap emptyStar;
	private static int beforeCenter; //For star alignment
	
	private String levelNumb; //caching purposes
	private int selectedLevel; //May be different from GameHandler.levelNumb
	
	private int currentPage = 0;
	
	private int numOfPages = 1; //2 pages, must update if adding more pages
	
	public MainMenu(GameHandler gameHandler) {
		this.gameHandler = gameHandler;
		this.paint = new Paint();
		
		GameVariable.mainMenuLevel.reset();
		
		levelHandler = new LevelHandler(GameVariable.mainMenuLevel);
		
		levelHandler.start();
	}
	
	@Override
	public void run() {
		System.out.println("Main menu started");
		
		GameVariable.mainMenuSong.seekTo(0);
		GameVariable.mainMenuSong.start();
		
		currentPage = 0;
		
		try {
			while (gameHandler.gameView.active
					&& gameHandler.currentState.stateNumb <= 5) {
				Thread.sleep(100);
			}
			
			GameVariable.mainMenuSong.stop();
			GameVariable.mainMenuSong.prepareAsync();
			
			Thread.sleep(1000);
			
			System.out.println("Main menu stopped");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		levelHandler.doLevel = false;
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (canvas != null
				&& paint != null) {
             if (gameHandler.currentState.stateNumb <= 5) {

                 paint.setAntiAlias(true);

                 if (levelHandler != null) {
                     for (int x=0; x < levelHandler.level.tileSprites.length; x++) {
                         for (int y=0; y < levelHandler.level.tileSprites[x].length; y++) {
                             canvas.drawBitmap(levelHandler.level.tileSprites[x][y].sprite.menuBitmap, (x * GameVariable.imgMenuLengthX), (y * GameVariable.imgMenuLengthY), paint);
                         }
                     }

                     for (CharacterSprite cs : levelHandler.level.characterSprites) {
                        canvas.drawBitmap(cs.sprite.menuBitmap, (cs.xLoc * GameVariable.imgMenuLengthX) + cs.xOffset, (cs.yLoc * GameVariable.imgMenuLengthY) + cs.yOffset, paint);
                     }
                 }

                 paint.setTypeface(GameVariable.broadwayBT);
                 paint.setColor(Color.WHITE);

                 paint.setTextSize(titleTextSize);

                 paint.getTextBounds("SpyMaze", 0, 7, gameHandler.bounds); //Title

                 canvas.drawText("SpyMaze", titleTextLoc.x, titleTextLoc.y, paint);
                 canvas.drawLine(titleTextLoc.x, titleTextLoc.y + 3, (titleTextLoc.x + gameHandler.bounds.width()), titleTextLoc.y + 3, paint);

                 if (gameHandler.currentState == GameState.OPTIONS_MENU) {
                     gameHandler.drawOptionsMenu(canvas, paint, gameHandler.currentX, gameHandler.currentY);
                 } else if (gameHandler.currentState == GameState.HELP_MENU) {
                     gameHandler.drawHelpMenu(canvas, paint);
                 } else if (gameHandler.currentState == GameState.MISSION_SELECTION) {
                     drawMissionSelect(canvas, paint);
                 } else if (gameHandler.currentState == GameState.LEVEL_SELECTION) {
                     drawLevelSelection(canvas, paint);
                 }

                 paint.setColor(Color.WHITE);


//    		 	 paint.setTextSize(15);
//    	 		 canvas.drawText("(" + gameHandler.currentX + ", " + gameHandler.currentY + ")", 10, 50, paint);

                 gameHandler.drawStateButtons(canvas, paint, gameHandler.currentX, gameHandler.currentY);
            } else {
                 canvas.drawColor(Color.BLACK);

                 paint.setColor(Color.WHITE);
                 paint.setTextSize(titleTextSize);

                 gameHandler.cachedString = "Level " + selectedLevel;

                 paint.getTextBounds(gameHandler.cachedString, 0, gameHandler.cachedString.length(), gameHandler.bounds);
                 gameHandler.point = Utility.getStringCentered(GameVariable.entireScreenRect, gameHandler.bounds);

                 canvas.drawText(gameHandler.cachedString, gameHandler.point.x, gameHandler.point.y, paint);
            }
        }
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gameHandler.currentX = (int) event.getX();
		gameHandler.currentY = (int) event.getY();
		
		if (event.getAction() == MotionEvent.ACTION_UP) {
			
			if (!gameHandler.doButtonClick(gameHandler.currentX, gameHandler.currentY)
					&& gameHandler.currentState == GameState.LEVEL_SELECTION) {
				if (previousPageRect.contains(gameHandler.currentX, gameHandler.currentY)
						&& currentPage > 0) {
					currentPage--;
				}
				
				if (nextPageRect.contains(gameHandler.currentX, gameHandler.currentY)
						&& currentPage < numOfPages) {
					currentPage++;
				}
				
				for (int i=0; i<levelSelectRects.length; i++) {
					if (levelSelectRects[i].contains(gameHandler.currentX, gameHandler.currentY)
							&& ((currentPage * 8) + (i + 1)) <= (gameHandler.missionNumber == 2 ? GameVariable.unlockedLevel-15 : GameVariable.unlockedLevel)) {
						gameHandler.currentState = GameState.LEVEL_SELECTED;
						gameHandler.levelNumber = selectedLevel = (currentPage * 8) + (i + 1);
					}
				}
			}
			
			gameHandler.currentX = -1;
			gameHandler.currentY = -1;
		}
		
		return true;
	}
	
	private void drawLevelSelection(Canvas canvas, Paint paint) {
		paint.setARGB(225, 0, 0, 0);
		paint.setStyle(Style.FILL);
		canvas.drawRoundRect(levelSelectRect_1, 5, 5, paint);
		paint.setColor(Color.WHITE);
		paint.setStyle(Style.STROKE);
		canvas.drawRoundRect(levelSelectRect_1, 5, 5, paint);
		
		paint.setTextSize(levelSelectTitleSize);
		
		paint.getTextBounds("Select a Level", 0, 14, gameHandler.bounds);
		
		canvas.drawText("Select a Level", levelSelectTitleLoc.x, levelSelectTitleLoc.y, paint);
		canvas.drawLine(levelSelectTitleLoc.x, levelSelectTitleLoc.y + 3, (levelSelectTitleLoc.x + gameHandler.bounds.width()), levelSelectTitleLoc.y + 3, paint);
		
		paint.setColor(Color.WHITE);
		paint.setStyle(Style.STROKE);
		canvas.drawRect(levelSelectRect_2, paint);
		
		paint.setTextSize(StateButton.values()[0].fontSize); //General state button font size
		
		paint.getTextBounds("<-", 0, 2, gameHandler.bounds);
		if (previousPageRect.contains(gameHandler.currentX, gameHandler.currentY)) {
			paint.setStyle(Style.FILL);
			canvas.drawRoundRect(previousPageRect, 5, 5, paint);
			paint.setColor(Color.BLACK);
		} else {
			canvas.drawRoundRect(previousPageRect, 5, 5, paint);
		}
		gameHandler.point = Utility.getStringCentered(previousPageRect, gameHandler.bounds);
		canvas.drawText("<-", gameHandler.point.x, gameHandler.point.y, paint);
		
		paint.setColor(Color.WHITE);
		paint.setStyle(Style.STROKE);
		
		paint.getTextBounds("->", 0, 2, gameHandler.bounds);
		if (nextPageRect.contains(gameHandler.currentX, gameHandler.currentY)) {
			paint.setStyle(Style.FILL);
			canvas.drawRoundRect(nextPageRect, 5, 5, paint);
			paint.setColor(Color.BLACK);
		} else {
			canvas.drawRoundRect(nextPageRect, 5, 5, paint);
		}
		gameHandler.point = Utility.getStringCentered(nextPageRect, gameHandler.bounds);
		canvas.drawText("->", gameHandler.point.x, gameHandler.point.y, paint);
		//End of level selection arrows
		
		for (int i=0; i<levelSelectRects.length; i++) {
			levelNumb = Integer.toString((currentPage * 8) + (i + 1));
			
			if (Integer.parseInt(levelNumb) <= (gameHandler.missionNumber == 2 ? GameVariable.unlockedLevel-15 : GameVariable.unlockedLevel)
					&& levelSelectRects[i].contains(gameHandler.currentX, gameHandler.currentY)) {
				paint.setStyle(Style.FILL);
			} else {
				paint.setStyle(Style.STROKE);
			}
			
			paint.setColor(Color.WHITE);
			canvas.drawRect(levelSelectRects[i], paint);
			
			paint.setTextSize(levelSelectNumbSize);
			
			paint.getTextBounds(levelNumb, 0, levelNumb.length(), gameHandler.bounds);
			
			gameHandler.point = Utility.getStringCentered(levelSelectRects[i], gameHandler.bounds);
			
			if (Integer.parseInt(levelNumb) <= (gameHandler.missionNumber == 2 ? GameVariable.unlockedLevel-15 : GameVariable.unlockedLevel)
					&& !levelSelectRects[i].contains(gameHandler.currentX, gameHandler.currentY)) {
				paint.setColor(Color.WHITE);
			} else if (Integer.parseInt(levelNumb) <= (gameHandler.missionNumber == 2 ? GameVariable.unlockedLevel-15 : GameVariable.unlockedLevel)
						&& levelSelectRects[i].contains(gameHandler.currentX, gameHandler.currentY)) {
				paint.setColor(Color.BLACK);
			} else {
				paint.setColor(Color.GRAY);
			}
			
			canvas.drawText(levelNumb, gameHandler.point.x, gameHandler.point.y, paint);
			
			for (int c=0; c<3; c++) {
				if (c+1 <= GameVariable.starValues[levelHandler.level.missionNumber-1][Integer.parseInt(levelNumb)-1]) {
					canvas.drawBitmap(filledStar, (levelSelectRects[i].centerX() - beforeCenter + (c * (filledStar.getWidth() + LevelTask.spaceBetweenStars))),
							levelSelectRects[i].bottom-(LevelTask.spaceBetweenStars + filledStar.getHeight()), paint);
				} else {
					canvas.drawBitmap(emptyStar, (levelSelectRects[i].centerX() - beforeCenter + (c * (emptyStar.getWidth() + LevelTask.spaceBetweenStars))),
							levelSelectRects[i].bottom-(LevelTask.spaceBetweenStars + emptyStar.getHeight()), paint);
				}
			}
		}
	}

    private void drawMissionSelect(Canvas canvas, Paint paint) {
        paint.setARGB(225, 0, 0, 0);
        paint.setStyle(Style.FILL);
        canvas.drawRoundRect(levelSelectRect_1, 5, 5, paint);
        paint.setColor(Color.WHITE);
        paint.setStyle(Style.STROKE);
        canvas.drawRoundRect(levelSelectRect_1, 5, 5, paint);

        paint.setTextSize(levelSelectTitleSize);

        paint.getTextBounds("Select a Mission", 0, 16, gameHandler.bounds);

        canvas.drawText("Select a Mission", missionSelectTitleLoc.x, missionSelectTitleLoc.y, paint);
        canvas.drawLine(missionSelectTitleLoc.x, missionSelectTitleLoc.y + 3, (missionSelectTitleLoc.x + gameHandler.bounds.width()), missionSelectTitleLoc.y + 3, paint);

        canvas.drawBitmap(GameVariable.M1_AD.bitmap, missionOnePicLoc.x, missionOnePicLoc.y, paint);
        canvas.drawBitmap(GameVariable.M2_AD.bitmap, missionTwoPicLoc.x, missionTwoPicLoc.y, paint);

        if (GameVariable.starValues[0][15] == 0) {
            paint.setColor(Color.RED);
            paint.setTextSize(completeMissionOneTextSize);

            canvas.drawText("Complete mission one first", completeMissionOneTextLoc.x, completeMissionOneTextLoc.y, paint);
        }
    }
	
	public static void setup() {
		titleTextSize = Utility.getXRatio(60);
		titleTextLoc = Utility.getConfiguredPoint(250, 80);

        missionSelectTitleLoc = Utility.getConfiguredPoint(255, 60);

        GameVariable.M1_AD.bitmap = Utility.getRatioedBitmap(GameVariable.M1_AD.bitmap, 310, 240);
        GameVariable.M2_AD.bitmap = Utility.getRatioedBitmap(GameVariable.M2_AD.bitmap, 310, 240);

        completeMissionOneTextLoc = Utility.getConfiguredPoint(434, 385);
        completeMissionOneTextSize = Utility.getXRatio(20);

        missionOnePicLoc = Utility.getConfiguredPoint(60, 120);
        missionTwoPicLoc = Utility.getConfiguredPoint(430, 120);

		levelSelectTitleLoc = Utility.getConfiguredPoint(275, 60);
		levelSelectTitleSize = Utility.getXRatio(32);

        Utility.resizeRectangle(levelSelectRect_1);
        Utility.resizeRectangle(levelSelectRect_2);
		
		for (int y=0; y<levelSelectorYs.length; y++) {
			for (int x=0; x<levelSelectorXs.length; x++) {
				levelSelectRects[(y * 4) + x] = new RectF(Utility.getXRatio(levelSelectorXs[x]),
						Utility.getYRatio(levelSelectorYs[y]),
						Utility.getXRatio(levelSelectorXs[x] + 145),
						Utility.getYRatio(levelSelectorYs[y] + 165.5F));
			}
		}
		
		levelSelectNumbSize = Utility.getXRatio(72);

        Utility.resizeRectangle(previousPageRect);
        Utility.resizeRectangle(nextPageRect);

		filledStar = Utility.getRatioedBitmap(GameVariable.FILLED_STAR.bitmap, 30, 30);
		emptyStar = Utility.getRatioedBitmap(GameVariable.EMPTY_STAR.bitmap, 30, 30);
		beforeCenter = (int) (levelSelectRects[0].centerX() - Utility.getXRatio(128));
	}
	
}
