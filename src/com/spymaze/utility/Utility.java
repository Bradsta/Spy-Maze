package com.spymaze.utility;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;


public class Utility {

	public static String encryptString(String text, char key) {
		StringBuilder enc = new StringBuilder();
		
		for (int j = 0; j < text.toCharArray().length; j++) {
			char current = text.charAt(j);
			
			enc.append(current ^= key);
		}
		
		return enc.toString();
	}

    /**
     * Returns a re-configured point based on the original location of the point in the android emulator
     *
     * @param originalX Original x point
     * @param originalY Original y point
     */
    public static Point getConfiguredPoint(float originalX, float originalY) {
        return new Point((int) getXRatio(originalX), (int) getYRatio(originalY));
    }
	
	/**
	 * Returns a point where a string should be drawn.
	 * 
	 * @param buttonRect Entire rect to paint string within.
	 * @param stringRect Bounds of the string to paint.
	 * @return           The point where the string should be drawn.
	 */
	public static Point getStringCentered(RectF buttonRect, Rect stringRect) {
		int x = (int) (buttonRect.centerX() - (stringRect.width() / 2));
		int y = (int) (buttonRect.centerY() + (stringRect.height() / 2));

		return new Point(x, y);
	}
	
	/**
	 * Returns a re-sized version of the passed bitmap
	 * 
	 * @param bitmap Bitmap to resize
	 * @param newX   New x dimension
	 * @param newY   New y dimension
	 * @return       New scaled bitmap
	 */
	public static Bitmap getResizedBitmap(Bitmap bitmap, int newX, int newY) {
	    return Bitmap.createScaledBitmap(bitmap, newX, newY, false);
	}

    public static Bitmap getRatioedBitmap(Bitmap bitmap, float originalX, float originalY) {
        return Bitmap.createScaledBitmap(bitmap, (int) ((originalX/800.0F) * GameVariable.screenWidth), (int) ((originalY/480.0F) * GameVariable.screenHeight), false);
    }

    /**
     * Returns a re-sized x-axis font/ratio number based on the original size of the font/ratio in the android emulator
     *
     * @param originalX Original size of font or ratio
     */
    public static float getXRatio(float originalX) {
        return ((originalX/800.0F) * GameVariable.screenWidth);
    }

    /**
     * Returns a re-sized y-axis font/ratio number based on the original size of the font/ratio in the android emulator
     *
     * @param originalY Original size of font or ratio
     */
    public static float getYRatio(float originalY) {
        return ((originalY/480.0F) * GameVariable.screenHeight);
    }
	
	/**
	 * Returns the next even number, for example, <code>nextEven(11.5F)</code> would return 12.
	 * 
	 * @param f Inputted number
	 * @return  Next even number
	 */
	public static int nextEven(float f) {
		return (f > (int) f) ? (int) (f + (2 - f%2)) : (int) f;
	}
	
	/**
	 * Returns the next number above the inputted float if it is greater than its integer representation.
	 * 
	 * @param f Inputted number
	 * @return  Rounded number
	 */
	public static int roundUp(float f) {
		return (f > (int) f) ? ((int) f) + 1 : (int) f; 
	}

    /**
     * Re-configures a RectF based on the original size/location of the RectF in the android emulator
     *
     * @param rectangle Original RectF
     */
    public static void resizeRectangle(RectF rectangle) {
        rectangle.left = (((float)rectangle.left/800.0F) * GameVariable.screenWidth);
        rectangle.right = (((float)rectangle.right/800.0F) * GameVariable.screenWidth);
        rectangle.top = (((float)rectangle.top/480.0F) * GameVariable.screenHeight);
        rectangle.bottom = (((float)rectangle.bottom/480.0F) * GameVariable.screenHeight);
    }
	
	/**
	 * Returns the string between the inputted String s and String e in String mixed.
	 * 
	 * @param s     Start of index to parse
	 * @param e     End limit to parse
	 * @param mixed Entire string
	 * @return      Parsed string
	 */
	public static String parse(String s, String e, String mixed) {
		try {
			return mixed.substring(mixed.indexOf(s) + s.length(), mixed.indexOf(e, mixed.indexOf(s) + s.length()));
		} catch(Exception ee) {
			ee.printStackTrace();
			return null;
		}
	}

}
