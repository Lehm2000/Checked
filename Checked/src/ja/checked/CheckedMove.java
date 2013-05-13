package ja.checked;

public class CheckedMove {
	
	//don't yet know what this class needs...just making some guesses here.
	private int sourceX;	//where is it from
	private int sourceY;
	private int moveX;		//where is it going
	private int moveY;
	private int piece;  //used for ai so we know which piece it wants to move...should be -1 the rest of the time
	
	//move type constants
	public final static int MOVE = 0;
	public final static int JUMP = 1;
	public final static int MULTIJUMP = 2;  //beginning of a multijump
	
	public CheckedMove()
	{
		sourceX = 0;
		sourceY = 0;
		moveX = 0;
		moveY = 0;
		setPiece(-1);
		
	}
	
	public CheckedMove(int moveX, int moveY)
	{
		this.sourceX = 0;
		this.sourceY = 0;
		this.moveX = moveX;
		this.moveY = moveY;
		this.setPiece(-1);
		
	}
	
	public CheckedMove(int sourceX, int sourceY, int moveX, int moveY, int piece)  //if we are specifying piece need source
	{
		this.sourceX = sourceX;
		this.sourceY = sourceY;
		this.moveX = moveX;
		this.moveY = moveY;
		this.setPiece(piece);
		
	}
	
	//copy constructor
	public CheckedMove(CheckedMove sourceMove) 
	{
		this.sourceX = sourceMove.getSourceX();
		this.sourceY = sourceMove.getSourceY();
		this.moveX = sourceMove.getMoveX();
		this.moveY = sourceMove.getMoveY();
		this.setPiece(sourceMove.getPiece());
	}
	
	// Generated hashCode and equals.
	//==============================================================
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + moveX;
		result = prime * result + moveY;
		result = prime * result + piece;
		result = prime * result + sourceX;
		result = prime * result + sourceY;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CheckedMove other = (CheckedMove) obj;
		if (moveX != other.moveX)
			return false;
		if (moveY != other.moveY)
			return false;
		if (piece != other.piece)
			return false;
		if (sourceX != other.sourceX)
			return false;
		if (sourceY != other.sourceY)
			return false;
		return true;
	}
	//==============================================================

	public int getSourceX() {
		return sourceX;
	}

	public int getSourceY() {
		return sourceY;
	}

	public int getMoveX() {
		return moveX;
	}

	public int getMoveY() {
		return moveY;
	}

	public int getPiece() {
		return piece;
	}

	public void setPiece(int piece) {
		this.piece = piece;
	}
	
	@Override 
	public String toString()
	{
		return "source:(" + sourceX +"," + sourceY+  "), move:(" + moveX +"," + moveY+ "), piece:" + this.getPiece();
	
	}

}
