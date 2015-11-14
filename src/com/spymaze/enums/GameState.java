package com.spymaze.enums;

public enum GameState {

	MAIN_MENU(1),
	OPTIONS_MENU(2),
	HELP_MENU(3),
    MISSION_SELECTION(4),
	LEVEL_SELECTION(5),
	LEVEL_SELECTED(6),
	IN_LEVEL(7),
	GAME_OPTIONS(8),
	LOST(9),
	WON(10),
	NEXT_LEVEL(11);
	
	public final int stateNumb; //Ordinal is more costly
	
	public static final int[] pausedStateNumbs = { 2, 3, 8, 9, 10 };
	public static final int[] nonGameStateNumbs = { 1, 4, 5, 6, 11 };
	
	private GameState(int stateNumb) {
		this.stateNumb = stateNumb;
	}

}
