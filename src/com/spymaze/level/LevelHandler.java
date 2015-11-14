package com.spymaze.level;

import com.spymaze.sprite.CharacterSprite;
import com.spymaze.sprite.Direction;
import com.spymaze.utility.GameVariable;


public class LevelHandler extends Thread {
	
	public final Level level;
	
	public CharacterSprite guy;
	
	public Direction currentDirection;
	public Direction lastDirection;
	public Direction nextDirection;
	
	public boolean doLevel = true;
	public boolean paused = false;
	public boolean lost = false;
	public boolean won = false;
	
	private final int time = 700;
	
	private final float nextOffsetX;
	private final float nextOffsetY;
	private final float nextSpriteValX;
	private final float nextSpriteValY;
	private final int offsetResetValX;
	private final int offsetResetValY;
	
	public LevelHandler(Level level) {
		this.level = level;
		
		this.nextOffsetX = (float) ((int)level.imgLengthX/32.0F);
		this.offsetResetValX = level.imgLengthX;
		this.nextSpriteValX = (float) (this.offsetResetValX/4.0F);
		
		this.nextOffsetY = (float) ((int)level.imgLengthY/32.0F);
		this.offsetResetValY = level.imgLengthY;
		this.nextSpriteValY = (float) (this.offsetResetValY/4.0F);

		GUY: 
		for (int x=0; x<level.tileSprites.length; x++) {
			for (int y=0; y<level.tileSprites[x].length; y++) {
				if (level.tileSprites[x][y].equals(GameVariable.START_TILE[level.missionNumber-1])) {
					this.guy = new CharacterSprite(GameVariable.GUY[level.missionNumber-1][0], null, x, y, 0, 0);
					break GUY;
				}
			}
		}
		
		if (this.guy != null) GuyThread.start();
	}
	
	@Override
	public void run() {
		try {
			while (doLevel) {
				if (!paused) {
					for (int i=0; i<level.characterSprites.size(); i++) {
						
						CharacterSprite cs = level.characterSprites.get(i);
						
						if (cs.direction == Direction.NORTH 
								&& (cs.yLoc == 0 || !level.tileSprites[cs.xLoc][cs.yLoc-1].equals(GameVariable.TILE[level.missionNumber-1]))) {
							cs.sprite = GameVariable.ENEMY[level.missionNumber-1][GameVariable.STRAIGHT_1];
							cs.direction = Direction.SOUTH;
						} else if (cs.direction == Direction.SOUTH
								&& (cs.yLoc == (level.tileSprites[0].length-1) || !level.tileSprites[cs.xLoc][cs.yLoc+1].equals(GameVariable.TILE[level.missionNumber-1]))) {
							cs.sprite = GameVariable.ENEMY[level.missionNumber-1][GameVariable.BACK_1];
							cs.direction = Direction.NORTH;
						} else if (cs.direction == Direction.WEST 
								&& (cs.xLoc == 0 || !level.tileSprites[cs.xLoc-1][cs.yLoc].equals(GameVariable.TILE[level.missionNumber-1]))) {
							cs.sprite = GameVariable.ENEMY[level.missionNumber-1][GameVariable.RIGHT_1];
							cs.direction = Direction.EAST;
						} else if (cs.direction == Direction.EAST 
								&& (cs.xLoc == (level.tileSprites.length-1) || !level.tileSprites[cs.xLoc+1][cs.yLoc].equals(GameVariable.TILE[level.missionNumber-1]))) {
							cs.sprite = GameVariable.ENEMY[level.missionNumber-1][GameVariable.LEFT_1];
							cs.direction = Direction.WEST;
						}
						
						switch (cs.direction) {
						case NORTH:
							cs.yOffset -= nextOffsetY;
							
							if (cs.yOffset % nextSpriteValY == 0) { //Reaches every 8 offset sets because we divide by 32 :)
								if (!cs.sprite.equals(GameVariable.ENEMY[level.missionNumber-1][GameVariable.BACK_1])) {
									cs.sprite = GameVariable.ENEMY[level.missionNumber-1][GameVariable.BACK_1];
								} else {
									cs.sprite = GameVariable.ENEMY[level.missionNumber-1][GameVariable.BACK_2];
								}
							}
							
							if (cs.yOffset % offsetResetValY == 0) {
								cs.yOffset = 0;
								cs.yLoc--;
							}
							
							LOST: if (guy != null
									&& guy.xLoc == cs.xLoc
									&& cs.yLoc >= guy.yLoc
									&& cs.yLoc - guy.yLoc <= 2) {
								for (int y=guy.yLoc; y<=cs.yLoc; y++) {
									if (level.tileSprites[guy.xLoc][y] == GameVariable.UNREACHABLE[level.missionNumber-1]) {
										break LOST;
									}
								}
								
								doLevel = false;
								lost = true;
							}
							
							break;
						case SOUTH:
							cs.yOffset += nextOffsetY;
							
							if (cs.yOffset % nextSpriteValY == 0) {
								if (!cs.sprite.equals(GameVariable.ENEMY[level.missionNumber-1][GameVariable.STRAIGHT_1])) {
									cs.sprite = GameVariable.ENEMY[level.missionNumber-1][GameVariable.STRAIGHT_1];
								} else {
									cs.sprite = GameVariable.ENEMY[level.missionNumber-1][GameVariable.STRAIGHT_2];
								}
							}
							
							if (cs.yOffset % offsetResetValY == 0) {
								cs.yOffset = 0;
								cs.yLoc++;
							}
							
							LOST: if (guy != null
									&& guy.xLoc == cs.xLoc
									&& guy.yLoc >= cs.yLoc
									&& guy.yLoc - cs.yLoc <= 2) {
								for (int y=guy.yLoc; y>=cs.yLoc; y--) {
									if (level.tileSprites[guy.xLoc][y] == GameVariable.UNREACHABLE[level.missionNumber-1]) {
										break LOST;
									}
								}
								
								doLevel = false;
								lost = true;
							}
							
							break;
						case EAST:
							cs.xOffset += nextOffsetX;
							
							if (cs.xOffset % nextSpriteValX == 0) {
								if (!cs.sprite.equals(GameVariable.ENEMY[level.missionNumber-1][GameVariable.RIGHT_1])) {
									cs.sprite = GameVariable.ENEMY[level.missionNumber-1][GameVariable.RIGHT_1];
								} else {
									cs.sprite = GameVariable.ENEMY[level.missionNumber-1][GameVariable.RIGHT_2];
								}
							}
							
							if (cs.xOffset % offsetResetValX == 0) {
								cs.xOffset = 0;
								cs.xLoc++;
							}
							
							LOST: if (guy != null
									&& guy.yLoc == cs.yLoc
									&& guy.xLoc >= cs.xLoc
									&& guy.xLoc - cs.xLoc <= 2) {
								for (int x=guy.xLoc; x>=cs.xLoc; x--) {
									if (level.tileSprites[x][guy.yLoc] == GameVariable.UNREACHABLE[level.missionNumber-1]) {
										break LOST;
									}
								}
								
								doLevel = false;
								lost = true;
							}
	
							break;
						case WEST:
							cs.xOffset -= nextOffsetX;
							
							if (cs.xOffset % nextSpriteValX == 0) {
								if (!cs.sprite.equals(GameVariable.ENEMY[level.missionNumber-1][GameVariable.LEFT_1])) {
									cs.sprite = GameVariable.ENEMY[level.missionNumber-1][GameVariable.LEFT_1];
								} else {
									cs.sprite = GameVariable.ENEMY[level.missionNumber-1][GameVariable.LEFT_2];
								}
							}
							
							if (cs.xOffset % offsetResetValX == 0) {
								cs.xOffset = 0;
								cs.xLoc--;
							}
							
							LOST: if (guy != null
									&& cs.yLoc == guy.yLoc
									&& cs.xLoc >= guy.xLoc
									&& cs.xLoc - guy.xLoc <= 2) {
								for (int x=guy.xLoc; x<=cs.xLoc; x++) {
									if (level.tileSprites[x][guy.yLoc] == GameVariable.UNREACHABLE[level.missionNumber-1]) {
										break LOST;
									}
								}
								
								doLevel = false;
								lost = true;
							}
	
							break;
						}
						
					}
					
					Thread.sleep(time/32);
				}
			}
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}
	
	private Thread GuyThread = new Thread() {
		
		@Override
		public void run() {
			try {
				while (doLevel) {
					if (!paused) {
						if ((currentDirection != null || (lastDirection != null && (guy.yOffset != 0 || guy.xOffset != 0)))
								|| (guy.xOffset == 0 && guy.yOffset == 0 && currentDirection == null && nextDirection != null)) {
								
							if (guy.xOffset == 0 && guy.yOffset == 0 && currentDirection == null) {
								currentDirection = lastDirection = nextDirection;
							}
							
							Direction key = (currentDirection == null) ? lastDirection : currentDirection;
							
							switch (key) {
							case NORTH:
								if (guy.yLoc == 0
									|| (guy.yOffset == 0 && level.tileSprites[guy.xLoc][guy.yLoc-1].equals(GameVariable.UNREACHABLE[level.missionNumber-1]))) break;
								
								guy.yOffset -= nextOffsetY;
								
								if (guy.yOffset % nextSpriteValY == 0) {
									if (!guy.sprite.equals(GameVariable.GUY[level.missionNumber-1][GameVariable.BACK_1])) {
										guy.sprite = GameVariable.GUY[level.missionNumber-1][GameVariable.BACK_1];
									} else {
										guy.sprite = GameVariable.GUY[level.missionNumber-1][GameVariable.BACK_2];
									}
								}
								
								if (guy.yOffset % offsetResetValY == 0) {
									guy.yLoc--;
									guy.yOffset = 0;
								}
								
								break;
							case SOUTH:
								if (guy.yLoc == (level.tileSprites[0].length-1) 
									|| (guy.yOffset == 0 && level.tileSprites[guy.xLoc][guy.yLoc+1].equals(GameVariable.UNREACHABLE[level.missionNumber-1]))) break;
								
								guy.yOffset += nextOffsetY;
								
								if (guy.yOffset % nextSpriteValY == 0) {
									if (!guy.sprite.equals(GameVariable.GUY[level.missionNumber-1][GameVariable.STRAIGHT_1])) {
										guy.sprite = GameVariable.GUY[level.missionNumber-1][GameVariable.STRAIGHT_1];
									} else {
										guy.sprite = GameVariable.GUY[level.missionNumber-1][GameVariable.STRAIGHT_2];
									}
								}
								
								if (guy.yOffset % offsetResetValY == 0) {
									guy.yLoc++;
									guy.yOffset = 0;
								}
								
								break;
							case EAST:
								if (guy.xLoc == (level.tileSprites.length-1)
									|| (guy.xOffset == 0 && level.tileSprites[guy.xLoc+1][guy.yLoc].equals(GameVariable.UNREACHABLE[level.missionNumber-1]))) break;
								
								guy.xOffset += nextOffsetX;
								
								if (guy.xOffset % nextSpriteValX == 0) {
									if (!guy.sprite.equals(GameVariable.GUY[level.missionNumber-1][GameVariable.RIGHT_1])) {
										guy.sprite = GameVariable.GUY[level.missionNumber-1][GameVariable.RIGHT_1];
									} else {
										guy.sprite = GameVariable.GUY[level.missionNumber-1][GameVariable.RIGHT_2];
									}
								}
								
								if (guy.xOffset % offsetResetValX == 0) {
									guy.xLoc++;
									guy.xOffset = 0;
								}
								
								break;
							case WEST:
								if (guy.xLoc == 0
									|| (guy.xOffset == 0 && level.tileSprites[guy.xLoc-1][guy.yLoc].equals(GameVariable.UNREACHABLE[level.missionNumber-1]))) break;
								guy.xOffset -= nextOffsetX;
								
								if (guy.xOffset % nextSpriteValX == 0) {
									if (!guy.sprite.equals(GameVariable.GUY[level.missionNumber-1][GameVariable.LEFT_1])) {
										guy.sprite = GameVariable.GUY[level.missionNumber-1][GameVariable.LEFT_1];
									} else {
										guy.sprite = GameVariable.GUY[level.missionNumber-1][GameVariable.LEFT_2];
									}
								}
								
								if (guy.xOffset % offsetResetValX == 0) {
									guy.xLoc--;
									guy.xOffset = 0;
								}
								
								break;
							}
						}
						
						if (level.tileSprites[guy.xLoc][guy.yLoc] == GameVariable.END_TILE[level.missionNumber-1]) {
							doLevel = false;
							won = true;
						}
						
						Thread.sleep(time/38); // Just a tad quicker than the enemies
					}
				}
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	};

}
