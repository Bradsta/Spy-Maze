package com.spymaze.level;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.res.AssetManager;

import com.spymaze.sprite.CharacterSprite;
import com.spymaze.sprite.Direction;
import com.spymaze.sprite.LoadedTileSprite;
import com.spymaze.sprite.TileSprite;
import com.spymaze.utility.GameVariable;
import com.spymaze.utility.Utility;

/**
 * Within every SpyMaze level, there is a 2 dimensional tile array, and a one dimensional character sprite array list.
 * Character sprites only include enemies and not the main guy because the main guy is a movable entity.
 * <p>
 * 
 * @author Brad
 */
public class Level {

	public final TileSprite[][] tileSprites;
	public final ArrayList<CharacterSprite> characterSprites;
	
	public final int xMax;
	public final int yMax;
	
	//public final int imgSideLength;
	
	public final int imgLengthX;
	public final int imgLengthY;

    public final int missionNumber;
	
	private int tilesWidth;
	private int tilesHeight;
	
	public boolean unlocked = false;
	
	private final ArrayList<CharacterSprite> originalCharSprites;
	
	private Level(TileSprite[][] tileSprites, ArrayList<CharacterSprite> characterSprites, int missionNumber, int xMax, int yMax, int imgLengthX, int imgLengthY) {
		this.tileSprites = tileSprites;
		this.characterSprites = characterSprites;
		
		this.originalCharSprites = new ArrayList<CharacterSprite>();
		
		for (CharacterSprite cs : this.characterSprites) this.originalCharSprites.add(cs.clone());

        this.missionNumber = missionNumber;
		
		this.xMax = xMax;
		this.yMax = yMax;
		
		this.imgLengthX = imgLengthX;
		this.imgLengthY = imgLengthY;
		
		tilesWidth = Utility.nextEven((float) xMax / (float) imgLengthX); //Originally 64.0F
		tilesHeight = Utility.nextEven((float) yMax / (float) imgLengthY);
	}
	
	/**
	 * Just parses lightly encrypted level data from an asset file.
	 * The encryption does not of course supply any high-tech protection whatsoever.
	 * <p>
	 */
	public static Level loadAssetLevel(AssetManager am, String levelName, int missionNumber, int xMax, int yMax, int imgLengthX, int imgLengthY) {
		try {
			TileSprite[][] ts = null;
			ArrayList<CharacterSprite> cs = new ArrayList<CharacterSprite>();
				
			InputStream is = am.open("levels/" + levelName + ".aml");
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String nextLine = null;
			
			while ((nextLine = br.readLine()) != null) {
				String dec = Utility.encryptString(nextLine, '1');
				
				if (dec.contains("Dimensions")) {
					ts = new TileSprite[Integer.parseInt(dec.replace("Dimensions:", "").split(" ")[0])]
		                                [Integer.parseInt(dec.replace("Dimensions:", "").split(" ")[1])];
				} else if (dec.contains("tile")) {
					TileSprite used = null;
					
					if (dec.contains("\\tile.png")) used = GameVariable.TILE[missionNumber-1];
					else if (dec.contains("\\unreachabletile.png")) used = GameVariable.UNREACHABLE[missionNumber-1];
					else if (dec.contains("\\developmentTile.png")) used = GameVariable.UNREACHABLE[missionNumber-1]; //No dev tiles
					else if (dec.contains("\\starttile.png")) {
						used = GameVariable.START_TILE[missionNumber-1];
					} else {
						used = GameVariable.END_TILE[missionNumber-1];
					}
					
					ts[Integer.parseInt(Utility.parse("x:", " ", dec))][Integer.parseInt(Utility.parse("y:", " ", dec))] = used;
				} else if (dec.contains("char")) {
					Direction dir = null;
					String directionParsed = Utility.parse("dir:", " ", dec);
					
					for (Direction d : Direction.values()) {
						if (d.toString().equals(directionParsed)) {
							dir = d;
							break;
						}
					}
					
					cs.add(new CharacterSprite(GameVariable.ENEMY[missionNumber-1][0], dir, Integer.parseInt(Utility.parse("x:", " ", dec)),
							Integer.parseInt(Utility.parse("y:", " ", dec)), 0, 0));
				}
			}
			
			is.close();
			isr.close();
			br.close();
			
			return new Level(ts, cs, missionNumber, xMax, yMax, imgLengthX, imgLengthY);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null; //:(
	}
	
	/**
	 * Resets characterSprites to the original location, tile sprite isn't modified.
	 */
	public void reset() {
		this.characterSprites.clear();
		
		for (CharacterSprite cs : this.originalCharSprites) {
			this.characterSprites.add(cs.clone());
		}
	}
	
	/**
	 * Returns the tiles that should be drawn on the screen currently to avoid any need to draw the entire map.
	 * <p>
	 * Does not loop through all tiles on the map, but rather the tiles that should be loaded for more efficiency.
	 * <p>
	 * 
	 * @param guy        The main guy.
	 * @param lastLoaded The last loaded tile sprites.
	 * @param deltaX     The change of the guy since we last loaded the tiles on the x-axis. (-1 <= deltaX <= 1)
	 * @param deltaY     The change of the guy since we last loaded the tiles on the y-axis. (-1 <= deltaY <= 1)
	 * @return           The loaded two dimensional tile sprite array.
	 */
	public ArrayList<LoadedTileSprite> getLoadedTiles(CharacterSprite guy, ArrayList<LoadedTileSprite> lastLoaded, int deltaX, int deltaY) {
		//Offsets +-1 to draw 1 tile off screen
		int loadedTilesStartX = ((guy.xLoc-(tilesWidth/2)-1) < 0) ? 0 : (guy.xLoc-(tilesWidth/2)-1);
		int loadedTilesEndX = (this.tileSprites.length < (guy.xLoc+(tilesWidth/2)+1)) ? this.tileSprites.length : (guy.xLoc+(tilesWidth/2)+1);
		int loadedTilesStartY = ((guy.yLoc-(tilesHeight/2)-1) < 0) ? 0 : (guy.yLoc-(tilesHeight/2)-1);
		int loadedTilesEndY = (this.tileSprites[0].length < (guy.yLoc+(tilesHeight/2)+1)) ? this.tileSprites[0].length : (guy.yLoc+(tilesHeight/2)+1);
		
		ArrayList<LoadedTileSprite> lts = (lastLoaded == null) ? new ArrayList<LoadedTileSprite>() : lastLoaded;
		
		int add = 0;
		int remove = 0;
		
		if (lts.size() == 0) {
			for (int x=loadedTilesStartX; x < loadedTilesEndX; x++) {
				for (int y=loadedTilesStartY; y < loadedTilesEndY; y++) {
					lts.add(new LoadedTileSprite(this.tileSprites[x][y].sprite, x - (guy.xLoc-(tilesWidth/2)), y - (guy.yLoc-(tilesHeight/2))));
				}
			}
		} else if (deltaX != 0) {
			add = (deltaX == 1) ? guy.xLoc+(tilesWidth/2) : guy.xLoc-(tilesWidth/2)-1;
			remove = (deltaX == 1) ? -1 : tilesWidth;
			
			for (int index=0; index < lts.size(); index++) {
				if (lts.get(index).xLoc == remove) {
					lts.remove(index);
					
					index--;
					
					continue;
				}
				
				lts.get(index).xLoc -= deltaX;
			}
			
			if (add >= 0 && add < this.tileSprites.length) {
				for (int y=loadedTilesStartY; y < loadedTilesEndY; y++) {
					lts.add(new LoadedTileSprite(this.tileSprites[add][y].sprite, add - (guy.xLoc-(tilesWidth/2)), y - (guy.yLoc-(tilesHeight/2))));
				}
			}
		} else if (deltaY != 0) {
			add = (deltaY == 1) ? guy.yLoc+(tilesHeight/2) : guy.yLoc-(tilesHeight/2)-1;
			remove = (deltaY == 1) ? -1 : tilesHeight;
			
			for (int index=0; index < lts.size(); index++) {
				if (lts.get(index).yLoc == remove) {
					lts.remove(index);
					
					index--;
					
					continue;
				}
				
				lts.get(index).yLoc -= deltaY;
			}
			
			if (add >= 0 && add < this.tileSprites[0].length) {
				for (int x=loadedTilesStartX; x < loadedTilesEndX; x++) {
					lts.add(new LoadedTileSprite(this.tileSprites[x][add].sprite, x - (guy.xLoc-(tilesWidth/2)), add - (guy.yLoc-(tilesHeight/2))));
				}
			}
		}

		return lts;
	}
	
	/**
	 * Returns the enemies that should be drawn on the screen currently to avoid any need of drawing all of them.
	 * <p>
	 * 
	 * @param guy
	 * @param character 
	 * @return
	 */
	public CharacterSprite getLoadedChar(CharacterSprite guy, CharacterSprite character) {
		if ((character.xLoc >= (guy.xLoc-(tilesWidth/2)-1) && character.xLoc <= (guy.xLoc+(tilesWidth/2)+1))
				&& (character.yLoc >= (guy.yLoc-(tilesHeight/2)-1) && character.yLoc <= (guy.yLoc+(tilesHeight/2)+1))) {
			if (character.loadedCharSprite == null) {
				character.loadedCharSprite = character.clone();
			} else {
				//Not creating a new instance, but just updating current values.
				character.loadedCharSprite.xOffset = character.xOffset;
				character.loadedCharSprite.yOffset = character.yOffset;
				
				character.loadedCharSprite.sprite = character.sprite;
			}
			
			character.loadedCharSprite.xLoc = character.xLoc - (guy.xLoc-(tilesWidth/2));
			character.loadedCharSprite.yLoc = character.yLoc - (guy.yLoc-(tilesHeight/2));
		} else {
			character.loadedCharSprite = null;
		}
		
		return character.loadedCharSprite;
	}
	
	/**
	 * Returns where the guy is supposed to be painted.
	 * <p>
	 * 
	 * @param guy  The main guy.
	 * @return     Where the guy is supposed to be painted at in the loaded tile sprite 2D array.
	 */
	public CharacterSprite getLoadedGuy(CharacterSprite guy) {
		CharacterSprite loadedGuy = new CharacterSprite(guy.sprite, guy.direction, tilesWidth/2, tilesHeight/2, guy.xOffset, guy.yOffset);
		
		loadedGuy.sprite = guy.sprite;
		
		return loadedGuy;
	}
	
}
