package com.spymaze.sprite;



public class CharacterSprite implements Comparable<CharacterSprite>, Cloneable {

	public Direction direction;
	
	public int xLoc;
	public int yLoc;
	public float xOffset;
	public float yOffset;
	
	public Sprite sprite;
	
	public int cachedXLoc = -1; //Cached locs are used for the "guy" when loading tiles
	public int cachedYLoc = -1;
	
	public CharacterSprite loadedCharSprite; //Used for the guy and for loading the enemy sprites

	public CharacterSprite(Sprite sprite, Direction direction, int xLoc, int yLoc, float xOffset, float yOffset) {
		this.sprite = sprite;
		
		this.direction = direction;
		this.xLoc = xLoc;
		this.yLoc = yLoc;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	@Override
	public int compareTo(CharacterSprite another) {
		if (direction != null
				&& another.direction != null) {
			return (direction.val - another.direction.val);
		}
		
		return 0;
	}
	
	@Override
	public CharacterSprite clone() {
		CharacterSprite cloned = new CharacterSprite(this.sprite, this.direction, this.xLoc, this.yLoc, this.xOffset, this.yOffset);
		
		cloned.cachedXLoc = this.cachedXLoc;
		cloned.cachedYLoc = this.cachedYLoc;
		
		cloned.sprite = this.sprite;
		
		return cloned;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof CharacterSprite) {
			return (((CharacterSprite)o).xLoc == this.xLoc && ((CharacterSprite)o).yLoc == this.yLoc);
		}
		
		return false;
	}
	
}
