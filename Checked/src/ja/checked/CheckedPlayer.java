/**
 * CheckedPlayer
 * Base Class for the Player types.  Specific player classes (human, ai, etc) will be 
 * extended from this class
 */

package ja.checked;

import java.awt.Color;

public class CheckedPlayer 
{
	
	private Color color;	//what color are the players pieces?
	private int playerNum;
	private boolean AI;		//is the player a computer opponent.
	
	
	public CheckedPlayer()
	{
		color = new Color(128,128,128);
		this.setPlayerNum(-1);
		this.setAI(false);
	}
	
	public CheckedPlayer(Color color,int playerNum)
	{
		this.color = color;
		this.setPlayerNum(playerNum);
		this.setAI(false);
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public boolean isAI()
	{
		return AI;
	}
	
	public void setAI(boolean isAI)
	{
		AI = isAI;
	}
	
	public CheckedMove getMove(CheckedGameBoard inBoard)
	{
		//human player...don't return anything.
		return null;
	}

	public int getPlayerNum() {
		return playerNum;
	}

	public void setPlayerNum(int playerNum) {
		this.playerNum = playerNum;
	}

}
