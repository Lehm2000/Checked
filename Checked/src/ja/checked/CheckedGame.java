package ja.checked;

import java.awt.Color;
import java.util.ArrayList;

public class CheckedGame 
{
	
	private CheckedGameBoard gameBoard = new CheckedGameBoard();
	private CheckedPlayer[] players = new CheckedPlayer[2];
	private int playerTurn;  //whose turn is it 0 or 1;
	private int gameState;  //0 - idle, 1 - player moving - regular move, 2 - Player moving - jumping, 3 - animating/interpolating.
	private int prevGameState;
	
	private int winner; //-1 no one (game ongoing), 0 player-1, 1 player-2
	private double mouseDownPosBoardX;  //where did mouse down event occur...used for dragging pieces (in board coord space)
	private double mouseDownPosBoardY;	//where did mouse down event occur...used for dragging pieces (in board coord space)
	
	
	private long curFrameTime;
	private long lastFrameTime;
	
	
	
	//public CheckedGameStates STATES;
	
	public CheckedGame()
	{
		InitGame();
		
	}
	
	public void InitGame()
	{
		players[0] = new CheckedPlayer(new Color(160,160,160), 0);
		players[1] = new CheckedPlayerAI(new Color(192,0,0), 1);
		playerTurn = 0;
		gameState = 0;
		gameBoard.reset();
		winner = -1;
	}
	
	public static void PlayGame()  //might not be needed after all
	{
		
	}
	
	public void UpdateTime(long newTime)
	{
		lastFrameTime = curFrameTime;
		curFrameTime = newTime;		
	}
	
	public long GetFrameTime()
	{
		return curFrameTime - lastFrameTime;
	}
	
	public boolean gameOver()  //return -1 if not game over.  0 if player1 won, 1 if player2.
	{
		boolean found0 = false;  //keeps track if we found a player0 piece
		boolean found1 = false;	//keeps track if we found a player1 piece
		
		//as long as one piece from each team remains 
		
		for (int i = 0;i<gameBoard.getNumGamePieces();i++)
		{
			CheckedGamePiece curPiece = gameBoard.getPiece(i);
			if (curPiece.GetOwner()==0)
				found0 = true;
			
			if (curPiece.GetOwner()==1)
				found1 = true;
		}
		
		//return !(found0 && found1);  
		
		if (found0 && found1)//if found at least 1 piece from each team.
		{
			//if there are pieces from both players still active see if the current player can make a move
			for (int i = 0; i< gameBoard.getNumGamePieces();i++)
			{
				CheckedGamePiece curPiece = gameBoard.getPiece(i);
				if (curPiece.GetOwner() == playerTurn && CanPieceMove(curPiece))
				{
					return false;  //the current player can move so the game continues.
				}				
			}
			//didn't find a valid move for this player...
			//so the game must be over because none of the 
			//current players pieces can move
			//the other player is the winner
			if (playerTurn == 0)
			{
				winner = 1;
				
				return true;
			}
			else
			{
				winner = 0;
				return true;				
			}
		}
		else //only found pieces for one player...game over dude.
		{
			if (found0)
			{
				winner = 0;
				
				return true;
			}
			else
			{
				winner = 1;
				
				return true;
			}
		}		
	}
	
	public int getWinner() {
		return winner;
	}


	public CheckedGameBoard getGameBoard()
	{
		return gameBoard;  //seems kind of dangerous to return a reference to actual game board....pass back copy?
	}
	
	public int getPlayerTurn()
	{
		return playerTurn;
	}
	
	public void ChangePlayerTurn()
	{
		if (playerTurn == 0)
			playerTurn = 1;
		else
			playerTurn = 0;
	}
	
	public CheckedPlayer getPlayer(int i)
	{
		return players[i];
	}

	public int GetGameState() 
	{
		return gameState;
	}

	public void SetGameState(int i) 
	{
		if (CheckedGameStates.ANIMATING != i)
		{
			//don't modify the prevGameSate if we're setting it to animating...so we know what the prevprev state was.
			prevGameState = this.gameState;
		}
		this.gameState = i;
	}
	
	public void RestorePrevGameState()
	{
		gameState = prevGameState;
	}

	//updates the position of the moving piece using the provided mouse coords...
	//somehow move to the CheckedGame class?
	public void UpdateMovingPiecePos(double mouseBoardX, double mouseBoardY)  
	{
		CheckedGamePiece movingPiece = getGameBoard().getPiece( gameBoard.getSelectedPiece() );
			
		double startX = movingPiece.GetX();  //where did the piece start? (what board space is it currently assigned to)
		double startY = movingPiece.GetY();
			
		double mouseChangeX = mouseBoardX-mouseDownPosBoardX; //how much has the mouse changed.
		double mouseChangeY = mouseBoardY-mouseDownPosBoardY;
			
		movingPiece.setCurPos(mouseChangeX+startX, mouseChangeY+startY);
			
	}
	
	public void GameMousePress(double mouseBoardX,double mouseBoardY)
	{
		CheckedGameBoard tempBoard = getGameBoard();
		
		if (GetGameState() == CheckedGameStates.IDLE)  //game currently doing nothing...waiting for player input.  //need some kind of global constant or enums for the gamestates??
		{			
			//see if player clicked on piece that belongs to player
			
			for (int i = 0;i<tempBoard.getNumGamePieces();i++)
			{
				CheckedGamePiece curPiece = tempBoard.getPiece(i);
				boolean isInsidePiece = curPiece.PointInside( mouseBoardX, mouseBoardY );
				if (isInsidePiece && CanPieceMove(curPiece))
				{					
					SetGameState(CheckedGameStates.MOVING);
					curPiece.SetMoving(true);
					gameBoard.setSelectedPiece(i);
					mouseDownPosBoardX = mouseBoardX;  
					mouseDownPosBoardY = mouseBoardY;
					
					curPiece.setCurPos(curPiece.GetX(), curPiece.GetY());					
				}				
			}		
		}
		else if (GetGameState() == CheckedGameStates.MULTIJUMP)
		{
			CheckedGamePiece curPiece = tempBoard.getPiece(gameBoard.getSelectedPiece());
			boolean isInsidePiece = curPiece.PointInside( mouseBoardX, mouseBoardY );
			if (isInsidePiece && CanPieceMove(curPiece))
			{
				
				SetGameState(CheckedGameStates.MOVING);
				curPiece.SetMoving(true);
				//game.setSelectedPiece(i);
				mouseDownPosBoardX = mouseBoardX;  
				mouseDownPosBoardY = mouseBoardY;
				
				curPiece.setCurPos(curPiece.GetX(), curPiece.GetY());
				
			}		
			
		}
	}
	
	public boolean CanPieceMove(CheckedGamePiece inPiece)  //I think this should be in GameBoard...but it uses the GameState which it doesn't have access to.
	{
		boolean canMove = false;
		
		if (GetGameState() == CheckedGameStates.IDLE || GetGameState() == CheckedGameStates.ANIMATING)  //if game is waiting for input
		{			
			if (inPiece.GetOwner() == getPlayerTurn() )  //and its this pieces owner's turn.
			{
				if (gameBoard.CanPlayerJump(getPlayerTurn())) //see if any of this players pieces can jump
				{
					//if so find if this is a piece that can jump.
					canMove = (gameBoard.FindJumps(inPiece).size() != 0);
				}
				else //player can't make a jump so if this piece has any avail moves.
				{
					canMove = (gameBoard.AllowedMoves(inPiece).size() != 0); // and if there is an allowed move then the piece can move.
				}
			}
		}
		else if (GetGameState() ==CheckedGameStates.MULTIJUMP)  //if doing a multi-jump
		{
			if (inPiece == gameBoard.getPiece(gameBoard.getSelectedPiece() ) )  //if this peice is the piece doing the multi-jump
			{
				canMove = true;
			}
		}
		
		return canMove;
	}

	public int GameMouseRelease(double mouseBoardX,double mouseBoardY)
	{
		int resultAction = CheckedGameAction.NOTHING;
		
		if (GetGameState() == CheckedGameStates.MOVING) //mouse released after dragging piece.
		{	
			CheckedGamePiece movingPiece = getGameBoard().getPiece( gameBoard.getSelectedPiece() );  //get the piece being moved.
			
			//do final move based on new mouse coords (in board coords)
			UpdateMovingPiecePos(mouseBoardX, mouseBoardY);
			
			//find out what square the center of the piece is over...
			int newX = (int) (movingPiece.getCurX()+0.5);  //this should be the centerX of the piece...which when converted to int should give us the space.
			int newY = (int) (movingPiece.getCurY()+0.5);
			
			//now that we have the space that the center of the piece is over...find out if it was a valid move.
			
			boolean validMove = false;
			
			//get list of allowed moves
			
			ArrayList<CheckedMove> allowedMoves = gameBoard.AllowedMoves(movingPiece);
			
			//go through list and see if one matches where player put their piece.
			for (int i = 0; i < allowedMoves.size(); i++)
			{
				if (newX == (int)allowedMoves.get(i).getMoveX() && newY == (int)allowedMoves.get(i).getMoveY() )
				{
					validMove = true;
				}
			}		
			
			if (validMove)
			{					
				//calc move distance...used to determine if made jump
				int oldX = movingPiece.GetSpaceX();  //get the space the piece is currently assigned to which is where it used to be...because we haven't updated its assigned space yet.
				int oldY = movingPiece.GetSpaceY();
				int movedX =  newX - oldX;
				//int movedY =  newY - oldY;	
				
				//movingPiece.SetPos(newX, newY); //assign the piece to its new space on the board (move the piece)
				gameBoard.beginMovePiece(new CheckedMove(oldX,oldY,newX,newY,gameBoard.getSelectedPiece()));
				SetGameState(CheckedGameStates.ANIMATING);
				//king me...change piece type if at opponents 'base'.
				
				
				/*if (result == 2)  //if findjumps returns some then it can jump
				{												
					SetGameState(CheckedGameStates.MULTIJUMP); //set mode to multi=jump
				}
				else if(result == 1)
				{						
					ChangePlayerTurn();
					SetGameState(CheckedGameStates.IDLE); //set mode to idle
					SetGameState(CheckedGameStates.ANIMATING);
				}*/
								
								
			}
			else
			{
				//not valid move
				//move peice back
				SetGameState(CheckedGameStates.RETURNING);  //this will animate the piece back to where it came from
				
			}
		}
		
		return resultAction;
	}

	
}
