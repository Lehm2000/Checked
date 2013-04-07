package ja.checked;

public class CheckedMove {
	
	//don't yet know what this class needs...just making some guesses here.
	private int moveX;
	private int moveY;
	
	
	public CheckedMove()
	{
		moveX = 0;
		moveY = 0;
		
	}
	
	public CheckedMove(int moveX, int moveY)
	{
		this.moveX = moveX;
		this.moveY = moveY;
		
	}

	public int getMoveX() {
		return moveX;
	}

	public int getMoveY() {
		return moveY;
	}

}
