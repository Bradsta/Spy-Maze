package com.spymaze.utility;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.spymaze.R;
import com.spymaze.level.Level;
import com.spymaze.sprite.Sprite;
import com.spymaze.sprite.TileSprite;

public class GameVariable {

    public static final int START = 0;
    public static final int BACK_1 = 1;
    public static final int BACK_2 = 2;
    public static final int LEFT_1 = 3;
    public static final int LEFT_2 = 4;
    public static final int RIGHT_1 = 5;
    public static final int RIGHT_2 = 6;
    public static final int STRAIGHT_1 = 7;
    public static final int STRAIGHT_2 = 8;

    public static final Sprite[][] ENEMY = {
            { new Sprite(R.drawable.m1enemy),
            new Sprite(R.drawable.m1enemyback1),
            new Sprite(R.drawable.m1enemyback2),
            new Sprite(R.drawable.m1enemyleft1),
            new Sprite(R.drawable.m1enemyleft2),
            new Sprite(R.drawable.m1enemyright1),
            new Sprite(R.drawable.m1enemyright2),
            new Sprite(R.drawable.m1enemystraight1),
            new Sprite(R.drawable.m1enemystraight2) },
            { new Sprite(R.drawable.m2enemy),
              new Sprite(R.drawable.m2enemyback1),
              new Sprite(R.drawable.m2enemyback2),
              new Sprite(R.drawable.m2enemyleft1),
              new Sprite(R.drawable.m2enemyleft2),
              new Sprite(R.drawable.m2enemyright1),
              new Sprite(R.drawable.m2enemyright2),
              new Sprite(R.drawable.m2enemystraight1),
              new Sprite(R.drawable.m2enemystraight2) }
    };

    public static final Sprite[][] GUY = {
            { new Sprite(R.drawable.m1guy),
            new Sprite(R.drawable.m1guyback1),
            new Sprite(R.drawable.m1guyback2),
            new Sprite(R.drawable.m1guyleft1),
            new Sprite(R.drawable.m1guyleft2),
            new Sprite(R.drawable.m1guyright1),
            new Sprite(R.drawable.m1guyright2),
            new Sprite(R.drawable.m1guystraight1),
            new Sprite(R.drawable.m1guystraight2) },
            { new Sprite(R.drawable.m2guy),
              new Sprite(R.drawable.m2guyback1),
              new Sprite(R.drawable.m2guyback2),
              new Sprite(R.drawable.m2guyleft1),
              new Sprite(R.drawable.m2guyleft2),
              new Sprite(R.drawable.m2guyright1),
              new Sprite(R.drawable.m2guyright2),
              new Sprite(R.drawable.m2guystraight1),
              new Sprite(R.drawable.m2guystraight2) }
    };
	
	public static final TileSprite[] TILE = { new TileSprite(new Sprite(R.drawable.m1tile)), new TileSprite(new Sprite(R.drawable.m2tile)) };
	public static final TileSprite[] UNREACHABLE = { new TileSprite(new Sprite(R.drawable.m1unreachabletile)), new TileSprite(new Sprite(R.drawable.m2unreachabletile)) };
	public static final TileSprite[] START_TILE = { new TileSprite(new Sprite(R.drawable.m1starttile)), new TileSprite(new Sprite(R.drawable.m2starttile)) };
	public static final TileSprite[] END_TILE = { new TileSprite(new Sprite(R.drawable.m1endtile)), new TileSprite(new Sprite(R.drawable.m2endtile))};
	
	public static final Sprite HELP_MENU = new Sprite(R.drawable.helpmenu);
	
	public static final Sprite FILLED_STAR = new Sprite(R.drawable.filledstar);
	public static final Sprite EMPTY_STAR = new Sprite(R.drawable.emptystar);
	
	public static final Sprite M1_ENEMY_CARTOON = new Sprite(R.drawable.m1enemycartoon);
    public static final Sprite M2_ENEMY_CARTOON = new Sprite(R.drawable.m2enemycartoon);

    public static final Sprite M1_AD = new Sprite(R.drawable.m1ad);
    public static final Sprite M2_AD = new Sprite(R.drawable.m2ad);
	
	//these values are in seconds, two stars are these values + 10 seconds
	public static final int[][] THREE_STAR_VALUES = {
		{ 10, //level 1
		  15, //level 2... ect
		  25,
		  20,
		  40,
		  40,
		  45,
		  30,
		  30,
		  40,
		  50,
		  40,
		  45,
		  110,
		  95,
		  115 /*level 16*/ },
		{ 45,
	      35,
	      45,
	      50,
	      35,
	      40,
	      50,
	      70,
	      70,
	      80,
	      75,
	      100,
	      60,
	      70,
	      30,
	      100 }
	};
	
	public static MediaPlayer mainMenuSong;
	public static MediaPlayer mission1Song;
    public static MediaPlayer mission2Song;
	public static MediaPlayer losingSound;
	public static MediaPlayer winningSound;
	public static MediaPlayer clickSound;
	public static AudioManager audioManager;
	
	public static Typeface broadwayBT = null;
	
	public static AssetManager assetManager;
	
	public static int screenWidth;
	public static int screenHeight;
	public static RectF entireScreenRect;
	
	public static int[][] starValues = new int[2][16]; //1-3 stars, if star = 0 then level hasn't been unlocked yet
	public static int unlockedLevel = 1;
	public static int levelAmount = 16;
	public static int imgSideLength = 64; //Default 64
	
	public static int imgMenuLengthX;
	public static int imgMenuLengthY;
	public static Level mainMenuLevel;
	
	public static void setBitmaps(Resources res) {
		for (int i=0; i<ENEMY.length; i++) {
            for (int j=0; j<ENEMY[i].length; j++) {
                ENEMY[i][j].setBitmap(res, imgSideLength, imgMenuLengthX, imgMenuLengthY);
            }
        }

        for (int i=0; i<GUY.length; i++) {
            for (int j=0; j<GUY[i].length; j++) {
                GUY[i][j].setBitmap(res, imgSideLength, imgMenuLengthX, imgMenuLengthY);
            }
        }

        for (int i=0; i<TILE.length; i++) {
            TILE[i].sprite.setBitmap(res, imgSideLength, imgMenuLengthX, imgMenuLengthY);
        }

        for (int i=0; i<UNREACHABLE.length; i++) {
            UNREACHABLE[i].sprite.setBitmap(res, imgSideLength, imgMenuLengthX, imgMenuLengthY);
        }

        for (int i=0; i<START_TILE.length; i++) {
            START_TILE[i].sprite.setBitmap(res, imgSideLength);
        }

        for (int i=0; i<END_TILE.length; i++) {
            END_TILE[i].sprite.setBitmap(res, imgSideLength);
        }
		
		HELP_MENU.setBitmap(res);
		
		FILLED_STAR.setBitmap(res);
		EMPTY_STAR.setBitmap(res);
		
		M1_ENEMY_CARTOON.setBitmap(res);
        M2_ENEMY_CARTOON.setBitmap(res);

        M1_AD.setBitmap(res);
        M2_AD.setBitmap(res);
	}

}
