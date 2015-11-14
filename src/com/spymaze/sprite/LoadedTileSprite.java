package com.spymaze.sprite;


public class LoadedTileSprite extends TileSprite {
	
	public int xLoc;
	public int yLoc;
	
	public LoadedTileSprite(Sprite sprite, int xLoc, int yLoc) {
		super(sprite);
		
		this.xLoc = xLoc;
		this.yLoc = yLoc;
	}

}
