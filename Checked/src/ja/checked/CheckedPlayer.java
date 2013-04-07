/**
 * CheckedPlayer
 * Base Class for the Player types.  Specific player classes (human, ai, etc) will be 
 * extended from this class
 */

package ja.checked;

import java.awt.Color;

public class CheckedPlayer 
{
	
	private Color color;  //what color are the players pieces?
	
	public CheckedPlayer()
	{
		color = new Color(128,128,128);
	}
	
	public CheckedPlayer(Color color)
	{
		this.color = color;
	}
	
	public Color getColor()
	{
		return color;
	}

}
