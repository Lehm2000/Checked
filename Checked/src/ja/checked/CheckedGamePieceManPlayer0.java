package ja.checked;

public class CheckedGamePieceManPlayer0 extends CheckedGamePiece {

	public CheckedGamePieceManPlayer0(int owner, int x, int y) 
	{
		super(owner,x,y);
		moveList.add(new CheckedMove(-1,1));
		moveList.add(new CheckedMove(1,1));
	}	

}
