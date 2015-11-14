package com.spymaze.enums;

import android.graphics.RectF;

import com.spymaze.sprite.Direction;

public enum DirectionalButton {
	
	NORTH_BUTTON(Direction.NORTH, new RectF(88, 312, 168, 392), 270.0F),
	SOUTH_BUTTON(Direction.SOUTH, new RectF(88, 396, 168, 476), 90.0F),
	EAST_BUTTON(Direction.EAST, new RectF(172, 354, 252, 434), 0.0F),
	WEST_BUTTON(Direction.WEST, new RectF(4, 354, 84, 434), 180.0F);

	public final Direction direction;
	public final RectF buttonRect;
	public final float rotation;

	public static float arrowTextSize;
	public static final String arrow = "->";
	
	private DirectionalButton(Direction direction, RectF buttonRect, float rotation) {
		this.direction = direction;
		this.buttonRect = buttonRect;
		this.rotation = rotation;
	}
	
}
