package ja.checked;

import java.util.ArrayList;

public class CheckedGamePieceKing extends CheckedGamePiece{
	
	public CheckedGamePieceKing(int owner, int x, int y) 
	{
		super(owner,x,y);
		moveList.add(new CheckedMove(-1,-1));
		moveList.add(new CheckedMove(1,-1));
		moveList.add(new CheckedMove(-1,1));
		moveList.add(new CheckedMove(1,1));
	}

	public CheckedGamePieceKing(CheckedGamePiece sourcePiece) 
	{
		super(sourcePiece.GetOwner(),sourcePiece.GetSpaceX(),sourcePiece.GetSpaceY());
		
		ArrayList<CheckedMove> copyMoveList = sourcePiece.GetMoves();
		
		//make copies of all moves.
		for (int i= 0; i <copyMoveList.size();i++)
		{
			moveList.add(new CheckedMove( copyMoveList.get(i) ) );
		}
	}

}
