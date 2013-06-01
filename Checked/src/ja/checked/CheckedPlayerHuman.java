package ja.checked;

import java.awt.Color;

public class CheckedPlayerHuman extends CheckedPlayer {
	
	private CheckedMove choosenMove;
	
	CheckedPlayerHuman()
	{
		
	}
	
	CheckedPlayerHuman(Color color, int playerNum, String name)
	{
		super(color,playerNum,name);
		setAI(false);
		setChoosenMove(null);
	}

	public CheckedMove getChoosenMove() 
	{
		
		return choosenMove;
	}

	public void setChoosenMove(CheckedMove choosenMove) 
	{
		this.choosenMove = choosenMove;
	}
	
	@Override
	public CheckedMove getMove()
	{
		CheckedMove returnMove = choosenMove;
		choosenMove = null;  //clear the choosenmove 
		return returnMove;
	}

}
