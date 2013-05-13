package ja.checked;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CheckedPlayerAI extends CheckedPlayer{
	
	private int scoredMoves;  //mostly for debug to see how many moves the ai checks
	
	Map<CheckedGameBoard,Double> moveScores = new HashMap<CheckedGameBoard,Double>();
	
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
		ArrayList<CheckedMove> moveList;
		moveList = GetPlayerMoves(inBoard);
		
		//at this point we have a list of moves the ai can make.
		
		double[] scoreArray = new double[moveList.size()];
		
		scoredMoves = 0;  
		moveScores.clear();
		
		long startTime = System.nanoTime();
		
		//now we need to evaluate each move and determine which is best.
		for (int i = 0; i<moveList.size();i++)
		{
			scoreArray[i] = scoreMove(new CheckedGameBoard(inBoard), moveList.get(i),7);
		}	
		
		//find out which had the highest score
		int maxIndex = 0;
		
		
		
		for (int i = 0;i<scoreArray.length;i++)
		{
			if (scoreArray[i]>scoreArray[maxIndex])
				maxIndex = i;
		}
		
		long endTime = System.nanoTime();
		
		System.out.println("Processed Moves: " + scoredMoves + " Computed in: " + (endTime - startTime)/1000000000.0);
		
		//for testing we'll just have it pick a random one.
		//Random rndGen = new Random();
		//return moveList.get( (int) (rndGen.nextDouble()*moveList.size()) );
		
		return moveList.get(maxIndex);
	}
	
	
	//get all the moves the current player can make
	public ArrayList<CheckedMove> GetPlayerMoves(CheckedGameBoard inBoard)
	{
		ArrayList<CheckedMove> moveList = new ArrayList<CheckedMove>();
		
		//first get all the moves that current player can make.
		
		//start by determining if the player can jump
		if (inBoard.CanPlayerJump(inBoard.getPlayerTurn()))
		{
			for (int i = 0;i<inBoard.getNumGamePieces();i++)
			{
				if (inBoard.getPiece(i).GetOwner()==inBoard.getPlayerTurn() )
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
				if (inBoard.getPiece(i).GetOwner()==inBoard.getPlayerTurn() )
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
		
		return moveList;
	}
	
	//take a copy of the board, the move to be made, and how many moves to look into the future
	double scoreMove(CheckedGameBoard inBoard, CheckedMove inMove, int maxMoves)
	{
		return scoreMove(new CheckedGameBoard(inBoard),inMove,maxMoves,0);		
	}
	
	//take a copy of the board, the move to be made, how many moves to look into the future, and how many moves have been made already
	double scoreMove(CheckedGameBoard inBoard, CheckedMove inMove, int maxMoves, int madeMoves)
	{
		double score = 0.0;
		double scoreMulti = inBoard.getPlayerTurn() == this.getPlayerNum() ? 1.0: -1.0;  //will make the scores negative if the opponent is the one scoring...aka bad thing
		
		//first execute the move on the passed board
		inBoard.beginMovePiece(inMove);
		int result = inBoard.finishMovePiece();
		madeMoves++;
		scoredMoves++;
		
		//otherwise evalute the move
		if(result == CheckedMove.MOVE)
		{
			inBoard.ChangePlayerTurn();
			score = 0.0;  //no points for regular move
		}
		else if(result == CheckedMove.JUMP)
		{
			inBoard.ChangePlayerTurn();
			score = 1.0 * scoreMulti;  //one point for jump
		}
		else if(result == CheckedMove.MULTIJUMP)
		{
			score = 1.0 * scoreMulti;	//currently 1 point...considering 2...but it will end up with higher score because it gets to jump again...so probably okay with 1
		}	
		
		//find if this board config has been scored before.
		if (moveScores.containsKey(inBoard))
		{
			//return the score contained in the map.
			//System.out.println("found board");
			score += moveScores.get(inBoard);
		}
		else
		{			
			//see if we should go to the next move.
			if(!inBoard.gameOver() && madeMoves < maxMoves)
			{
				//if the game is not over and we haven't reached the max moves.  we need to get the next set of moves.
				//now we need to evaluate each move and determine which is best.
				ArrayList<CheckedMove> moveList;
				moveList = GetPlayerMoves(inBoard);
				for (int i = 0; i<moveList.size();i++)
				{
					score += scoreMove(new CheckedGameBoard(inBoard), moveList.get(i),maxMoves,madeMoves);
				}				
			}
			//move now evaluated...add to list of already made moves
			moveScores.put(new CheckedGameBoard(inBoard), score);
		}
		
		return score;
		
	}

}
