package com.spymaze.sprite;

public enum Direction {
	
	NORTH(1),
	SOUTH(4),
	EAST(2),
	WEST(3);
	
	public final int val; //Used for sorting specific directions
	
	private Direction(int val) {
		this.val = val;
	}

}
