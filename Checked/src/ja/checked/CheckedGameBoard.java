package ja.checked;

import java.util.ArrayList;

public class CheckedGameBoard 
{
	private ArrayList<CheckedGamePiece> gamePieces= new ArrayList<CheckedGamePiece>();
	//private ArrayList<CheckedGamePiece> capturedPieces;  //in case needed
	
	//constructor.
	public CheckedGameBoard()
	{
		//reset();
	}
	
	public void reset()
	{
		//clear the board before adding the pieces back.
		gamePieces.clear();
		
		//player 1's pieces
		gamePieces.add(new CheckedGamePieceManPlayer0(0,1,0));
		gamePieces.add(new CheckedGamePieceManPlayer0(0,3,0));
		gamePieces.add(new CheckedGamePieceManPlayer0(0,5,0));
		gamePieces.add(new CheckedGamePieceManPlayer0(0,7,0));
		
		gamePieces.add(new CheckedGamePieceManPlayer0(0,0,1));
		gamePieces.add(new CheckedGamePieceManPlayer0(0,2,1));
		gamePieces.add(new CheckedGamePieceManPlayer0(0,4,1));
		gamePieces.add(new CheckedGamePieceManPlayer0(0,6,1));
		
		gamePieces.add(new CheckedGamePieceManPlayer0(0,1,2));
		gamePieces.add(new CheckedGamePieceManPlayer0(0,3,2));
		gamePieces.add(new CheckedGamePieceManPlayer0(0,5,2));
		gamePieces.add(new CheckedGamePieceManPlayer0(0,7,2));
		
		
		//player 2's pieces
		gamePieces.add(new CheckedGamePieceManPlayer1(1,0,5));
		gamePieces.add(new CheckedGamePieceManPlayer1(1,2,5));
		gamePieces.add(new CheckedGamePieceManPlayer1(1,4,5));
		gamePieces.add(new CheckedGamePieceManPlayer1(1,6,5));
				
		gamePieces.add(new CheckedGamePieceManPlayer1(1,1,6));
		gamePieces.add(new CheckedGamePieceManPlayer1(1,3,6));
		gamePieces.add(new CheckedGamePieceManPlayer1(1,5,6));
		gamePieces.add(new CheckedGamePieceManPlayer1(1,7,6));
				
		gamePieces.add(new CheckedGamePieceManPlayer1(1,0,7));
		gamePieces.add(new CheckedGamePieceManPlayer1(1,2,7));
		gamePieces.add(new CheckedGamePieceManPlayer1(1,4,7));
		gamePieces.add(new CheckedGamePieceManPlayer1(1,6,7));
		
				
	}
	
	
	public CheckedGamePiece getPiece(int spaceNum)
	{
		return gamePieces.get(spaceNum);
	}
	
	public int getNumGamePieces()
	{
		return gamePieces.size();
		
	}
	
	public int SpaceOccupied(int spaceX,int spaceY)  //returns -1 if not, player num (0/1) if true
	{
		int isOccupied = -1;
		
		for (int i = 0; i<gamePieces.size();i++)
		{
			CheckedGamePiece curPiece = gamePieces.get(i);
			if ( curPiece.GetSpaceX() == spaceX && curPiece.GetSpaceY() == spaceY)
			{
				isOccupied = curPiece.GetOwner();
			}
		}
		
		return isOccupied;
	}
	
	public boolean SpaceOnBoard(int spaceX, int spaceY)
	{
		return (spaceX >= 0 && spaceX <= 7 && spaceY >= 0 && spaceY <= 7);
	}
	
	public void CapturePiece(int index)
	{
		gamePieces.remove(index);
		
		//return success??
		//add piece to captured list??
	}
	
	public void KingPiece(int inPiece)
	{
		//change this peice to a King piece.
		CheckedGamePiece currentPiece = gamePieces.get(inPiece);
		gamePieces.set(inPiece, new CheckedGamePieceKing(currentPiece.GetOwner(), currentPiece.GetSpaceX(), currentPiece.GetSpaceY()));
	}
	
	

}
