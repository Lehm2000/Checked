package ja.checked;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CheckedPlayerAI extends CheckedPlayer{
	
	private int difficulty  = 1; 
	
	private Thread chooseThread;
	private ChooseMoveTask chooseTask;
	
	CheckedPlayerAI()
	{
		//color = new Color(128,128,128);
	}
	
	CheckedPlayerAI(Color color, int playerNum, int difficulty)
	{
		super(color,playerNum);
		setAI(true);
		chooseThread = null;
		chooseTask = null;
		this.difficulty = difficulty;
	}
	
	/**Returns move if there is one chosen and sets the chosen move to null
	 * 
	 */
	@Override
	public CheckedMove getMove()
	{
		
		return chooseTask.getChoosenMove();  //if the task has finished it will pass that move otherwise null.
		//return null;
		
	}
	
	//starts thread for ai to calculate a move based on the board it is passed.
	public void CalcMove(CheckedGameBoard inBoard)
	{
		//do we need to make sure the thread is not running?
		chooseTask = new ChooseMoveTask(inBoard, this.getPlayerNum());  //pass the board and the player num to the task.
		chooseThread = new Thread(chooseTask);
		chooseThread.start();			
		
	}
	
	
	
	
	private class ChooseMoveTask implements Runnable
	{
		
		private Map<CheckedGameBoard,Double> moveScores;
		private int scoredMoves;  //mostly for debug to see how many moves the ai checks
		private CheckedGameBoard inBoard;
		private CheckedMove choosenMove;
		private int playerNum;  //wish this didn't have to be passed an could just get it from the player class
		
		//constants
		final private static int mapSize = 100000;  //initial size for the map. Very large because it needs to calculate a large number of moves.
		
		public ChooseMoveTask()
		{
			this.scoredMoves = 0;
			this.choosenMove = null;
			this.moveScores = null;
			this.inBoard = null;
			this.playerNum = -1;
		}
		
		public ChooseMoveTask(CheckedGameBoard inBoard, int playerNum)
		{
			this.scoredMoves = 0;
			this.choosenMove = null;
			//this.moveScores  = new HashMap<CheckedGameBoard,Double>(); 
			this.inBoard = new CheckedGameBoard(inBoard);  //make copy of board supplied.
			this.playerNum = playerNum;
		}
		
		/** Method returns the move chosen and clears it so no move is now chosen.
		 * 
		 * @return 
		 */
		public CheckedMove getChoosenMove() {
			CheckedMove returnMove = choosenMove;
			choosenMove = null;
			return returnMove;
		}

		@Override
		public void run() {
			
			choosenMove = null;  //clear the choosenmove just in case.
			
			ArrayList<CheckedMove> moveList;
			moveList = GetPlayerMoves(inBoard);
			
			//at this point we have a list of moves the ai can make.
			
			double[] scoreArray = new double[moveList.size()];
			
			scoredMoves = 0;  
			moveScores  = new HashMap<CheckedGameBoard,Double>(mapSize); //clear the old data by creating a new map in its place
			/*TODO find way to reuse old data for new calculations?  
			 * Most of the data would be the same.  Just need to cull 
			 * the moves not used...and add a new depth to the remaining.
			 */
			
			//moveScores.clear();
			
			long startTime = System.nanoTime();
			
			//now we need to evaluate each move and determine which is best.
			for (int i = 0; i<moveList.size();i++)
			{
				scoreArray[i] = scoreMove(new CheckedGameBoard(inBoard), moveList.get(i), difficulty);
			}	
			
			//find out which had the highest score
			double maxScore = scoreArray[0];			
			
			for (int i = 0;i<scoreArray.length;i++)
			{
				//Find the maximum score
				if (scoreArray[i]>maxScore)
					maxScore = scoreArray[i];
			}
			
			//create a new list just for the moves that have the highest score...most of the time will likely only have one move in it.
			ArrayList<CheckedMove> maxMoves = new ArrayList<CheckedMove>();
			for (int i = 0;i<scoreArray.length;i++)
			{
				//Find the maximum score
				if (scoreArray[i]==maxScore)
					maxMoves.add(moveList.get(i));//current move equals the max score add it to the list of possibilites.
			}
			
			//pick a move based on score weights...currently the result of this is not used..ends up in erratic behavior.
			double arraySum = 0;
			//sum the array
			for (int i = 0; i<scoreArray.length;i++)
			{
				arraySum += scoreArray[i];
			}
			
			Random theRnd = new Random();
			
			double choosenVal = theRnd.nextDouble()*arraySum;
				
				
			//find what index the selection belongs to
			double arrayTotal = 0;
			int j;
			for ( j = 0; j<scoreArray.length && arrayTotal < choosenVal;j++)
			{
				arrayTotal+=scoreArray[j];
			}
			
			if (j>=scoreArray.length)  //seems kind of hacky to avoid index out of bounds
				j = scoreArray.length - 1;
			
			long endTime = System.nanoTime();
			
			System.out.println("Processed Moves: " + scoredMoves + " Computed in: " + (endTime - startTime)/1000000000.0);
			
			//for testing we'll just have it pick a random one.
			//Random rndGen = new Random();
			//return moveList.get( (int) (rndGen.nextDouble()*moveList.size()) );
			
			//choosenMove = moveList.get(maxIndex);
			choosenMove = maxMoves.get(theRnd.nextInt(maxMoves.size()));
			
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
			double scoreMulti = inBoard.getPlayerTurn() == playerNum ? 1.0: -1.0;  //will make the scores negative if the opponent is the one scoring...aka bad thing
			
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
			else if(result == CheckedMove.KING)
			{
				inBoard.ChangePlayerTurn();
				score = 2.0 * scoreMulti;  
				/*kinging worth 2 points...
				if the ai was fast enough to allow more iterations this
				probably wouldn't be necessary.  It would likely 
				calculate that king pieces are better...but it would be
				too slow to do depth that far down.*/
			}
			
			//adjust score by move depth.
			//score = score / Math.pow(10, madeMoves-1);
			
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
					if (result == CheckedMove.MULTIJUMP)
					{
						//if a multi-jump only look for moves for the current piece.
						moveList = inBoard.FindJumps(inBoard.getPiece(inBoard.getSelectedPiece()));
						
						//Add the piece number to the returned list
						//TODO investigate changing FindJumps so the input is the peice number instead of
						//a reference to the piece itself.  Would simplify some things but mean quite a bit of
						//recoding.
						for (int j = 0 ;j<moveList.size();j++)
						{
							(moveList.get(j)).setPiece(inBoard.getSelectedPiece()); //set the piece number to the current piece
						}
						
					}
					else
					{
						moveList = GetPlayerMoves(inBoard);
					}
					
					for (int i = 0; i<moveList.size();i++)
					{
						score += scoreMove(new CheckedGameBoard(inBoard), moveList.get(i),maxMoves,madeMoves)/(double)moveList.size();
					}				
				}
				//move now evaluated...add to list of already made moves
				moveScores.put(new CheckedGameBoard(inBoard), score);
			}			
			
			return score;
			
		}
		
	}

	

}
