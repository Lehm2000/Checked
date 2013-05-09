package ja.checked;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

public class CheckedPlayerAI extends CheckedPlayer{
	
	CheckedPlayerAI()
	{
		//color = new Color(128,128,128);
	}
	
	CheckedPlayerAI(Color color, int playerNum)
	{
		super(color,playerNum);
		setAI(true);
	}
	
	@Override
	public CheckedMove getMove(CheckedGameBoard inBoard)
	{
		ArrayList<CheckedMove> moveList = new ArrayList<CheckedMove>();
				
		//System.out.println("moves");
		
		//first get all the moves that this player can make.
		
		//start by determining if the player can jump
		if (inBoard.CanPlayerJump(this.getPlayerNum()))
		{
			for (int i = 0;i<inBoard.getNumGamePieces();i++)
			{
				if (inBoard.getPiece(i).GetOwner()==this.getPlayerNum() )
				{
					//get possible moves
					ArrayList<CheckedMove> workingList;
					workingList = inBoard.FindJumps(inBoard.getPiece(i));
					//add the piece number to the output
					for (int j = 0 ;j<workingList.size();j++)
					{
						(workingList.get(j)).setPiece(i); //set the piece number to the current piece
					}
					//System.out.println(workingList);	
					moveList.addAll(workingList);
				}
			}		
		}
		else //otherwise do a regular move
		{
			for (int i = 0;i<inBoard.getNumGamePieces();i++)
			{
				if (inBoard.getPiece(i).GetOwner()==this.getPlayerNum() )
				{
					//get possible moves
					ArrayList<CheckedMove> workingList;
					workingList = inBoard.AllowedMoves(inBoard.getPiece(i));
					//add the piece number to the output
					for (int j = 0 ;j<workingList.size();j++)
					{
						(workingList.get(j)).setPiece(i); //set the piece number to the current piece
					}
					//System.out.println(workingList);	
					moveList.addAll(workingList);
				}
			}		
		}
		
		//at this point we have a list of moves the ai can make.
		
		//now we need to evaluate each move and determine which is best.
		for (int i = 0; i<moveList.size();i++)
		{
			scoreMove(new CheckedGameBoard(inBoard), moveList.get(i), 5);
		}
		
		
		//for testing we'll just have it pick a random one.
		Random rndGen = new Random();
		return moveList.get( (int) (rndGen.nextDouble()*moveList.size()) );
	}
	
	//take a copy of the board, the move to be made, and how many moves to look into the future
	int scoreMove(CheckedGameBoard inBoard, CheckedMove inMove, int maxMoves)
	{
		return scoreMove(new CheckedGameBoard(inBoard),inMove,maxMoves,0,this.getPlayerNum());		
	}
	
	//take a copy of the board, the move to be made, how many moves to look into the future, and how many moves have been made already
	int scoreMove(CheckedGameBoard inBoard, CheckedMove inMove, int maxMoves, int madeMoves, int playerTurn)
	{
		int score = 0;
		
		//first execute the move on the passed board
		inBoard.beginMovePiece(inMove);
		int result = inBoard.finishMovePiece();
		
		//next evaluate the result.
		
		return score;
		
	}

}
