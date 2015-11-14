package com.spymaze.enums;


import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.Typeface;

public enum StateButton {
	
	//Main menu buttons
	START_BUTTON(new RectF(275, 210, 525, 265), Color.argb(150, 0, 0, 0), "START", 36, Typeface.ITALIC, GameState.MAIN_MENU, GameState.MISSION_SELECTION),
	OPTIONS_BUTTON(new RectF(275, 280, 525, 335), Color.argb(150, 0, 0, 0), "OPTIONS", 36, Typeface.ITALIC, GameState.MAIN_MENU, GameState.OPTIONS_MENU),
	HELP_BUTTON(new RectF(275, 350, 525, 405),  Color.argb(150, 0, 0, 0), "HELP", 36, Typeface.ITALIC, GameState.MAIN_MENU, GameState.HELP_MENU),
	BACK_BUTTON_OPTIONS(new RectF(303, 370, 498, 410), Color.argb(225, 0, 0, 0), "BACK", 36, Typeface.ITALIC, GameState.OPTIONS_MENU, GameState.MAIN_MENU),
	BACK_BUTTON_HELP(new RectF(303, 425, 498, 465), Color.argb(225, 0, 0, 0), "BACK", 36, Typeface.ITALIC, GameState.HELP_MENU, GameState.MAIN_MENU),
    BACK_BUTTON_MISSION_SELECT(new RectF(275, 415, 530, 450), Color.argb(225, 0, 0, 0), "BACK", 36, Typeface.ITALIC, GameState.MISSION_SELECTION, GameState.MAIN_MENU),
	BACK_BUTTON_LEVEL_SELECT(new RectF(275, 415, 530, 450), Color.argb(225, 0, 0, 0), "BACK", 36, Typeface.ITALIC, GameState.LEVEL_SELECTION, GameState.MISSION_SELECTION),
	
	//In game buttons
	GAME_OPTIONS(new RectF(734, 444, 796, 476), Color.argb(225, 0, 0, 0), "Menu", 16, Typeface.BOLD, GameState.IN_LEVEL, GameState.GAME_OPTIONS),
	GAME_OPTIONS_OPTIONS(new RectF(303, 180, 498, 210), Color.argb(225, 0, 0, 0), "SETTINGS", 20, Typeface.ITALIC, GameState.GAME_OPTIONS, GameState.OPTIONS_MENU),
	GAME_OPTIONS_HELP(new RectF(303, 220, 498, 250), Color.argb(225, 0, 0, 0), "HELP", 20, Typeface.ITALIC, GameState.GAME_OPTIONS, GameState.HELP_MENU),
	GAME_OPTIONS_RESTART(new RectF(303, 260, 498, 290), Color.argb(225, 0, 0, 0), "RESTART", 20, Typeface.ITALIC, GameState.GAME_OPTIONS, GameState.LEVEL_SELECTED),
	MAIN_MENU_BUTTON(new RectF(303, 300, 498, 330), Color.argb(225, 0, 0, 0), "MAIN MENU", 20, Typeface.ITALIC, GameState.GAME_OPTIONS, GameState.MAIN_MENU),
	BACK_BUTTON_GAME_OPTIONS(new RectF(303, 370, 498, 410), Color.argb(225, 0, 0, 0), "BACK", 36, Typeface.ITALIC, GameState.GAME_OPTIONS, GameState.IN_LEVEL),
	
	//Losing screen buttons
	MAIN_MENU_LOST(new RectF(303, 370, 498, 410), Color.argb(225, 0, 0, 0), "MAIN MENU", 24, Typeface.ITALIC, GameState.LOST, GameState.MAIN_MENU),
	RESTART_LOST(new RectF(303, 320, 498, 360), Color.argb(225, 0, 0, 0), "RESTART", 24, Typeface.ITALIC, GameState.LOST, GameState.LEVEL_SELECTED),
	
	//Winning screen buttons
	MAIN_MENU_WON(new RectF(303, 370, 498, 410), Color.argb(225, 0, 0, 0), "MAIN MENU", 24, Typeface.ITALIC, GameState.WON, GameState.MAIN_MENU),
	RESTART_WON(new RectF(303, 320, 498, 360), Color.argb(225, 0, 0, 0), "RESTART", 24, Typeface.ITALIC, GameState.WON, GameState.LEVEL_SELECTED),
	NEXT_LEVEL_WON(new RectF(303, 270, 498, 310), Color.argb(225, 0, 0, 0), "NEXT LEVEL", 24, Typeface.ITALIC, GameState.WON, GameState.NEXT_LEVEL),

    //Mission select buttons
    MISSION_ONE(new RectF(60, 120, 370, 360), Color.argb(225, 0, 0, 0), "", 0, Typeface.NORMAL, GameState.MISSION_SELECTION, GameState.LEVEL_SELECTION),
    MISSION_TWO(new RectF(430, 120, 740, 360), Color.argb(225, 0, 0, 0), "", 0, Typeface.NORMAL, GameState.MISSION_SELECTION, GameState.LEVEL_SELECTION);
	
	public RectF buttonRect;
	public int buttonBackgroundColor;
	public String buttonText;
	public float fontSize;
	public final int fontStyle;
	public final GameState currentState;
	public GameState nextState;
	
	private StateButton(RectF buttonRect, int buttonBackgroundColor, String buttonText, float fontSize, int fontStyle, GameState currentState, GameState nextState) {
		this.buttonRect = buttonRect;
        this.buttonBackgroundColor = buttonBackgroundColor;
		this.buttonText = buttonText;
		this.fontSize = fontSize;
		this.fontStyle = fontStyle;
		this.currentState = currentState;
		this.nextState = nextState;
	}

}
