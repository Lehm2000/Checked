package ja.checked;

public class CheckedGamePieceKing extends CheckedGamePiece{
	
	public CheckedGamePieceKing(int owner, int x, int y) 
	{
		super(owner,x,y);
		moveList.add(new CheckedMove(-1,-1));
		moveList.add(new CheckedMove(1,-1));
		moveList.add(new CheckedMove(-1,1));
		moveList.add(new CheckedMove(1,1));
	}

}
