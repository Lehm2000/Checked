package ja.checked;

import java.awt.Color;
import java.util.ArrayList;

public class CheckedGame 
{
	
	//constants
	final static int ONEPLAYER = 1;
	final static int TWOPLAYERLOCAL = 2;
	final static int TWOPLAYERNETWORK = 3;
	
	private CheckedGameBoard gameBoard = new CheckedGameBoard();
	private CheckedPlayer[] players = new CheckedPlayer[2];
	
	private int gameState;  //0 - idle, 1 - player moving - regular move, 2 - Player moving - jumping, 3 - animating/interpolating.
	private int prevGameState;
	
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
		players[0] = new CheckedPlayerHuman(new Color(160,160,160), 0);
		//players[0] = new CheckedPlayerAI(new Color(160,160,160), 0);
		players[1] = new CheckedPlayerAI(new Color(192,0,0), 1, 3);
		gameState = 0;
		gameBoard.reset();
		
		//if player 1 is an ai tell it to figure out a move.
		if (players[0].isAI())
		{
			((CheckedPlayerAI)players[0]).CalcMove(new CheckedGameBoard(gameBoard));
		}
		
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
	
	


	public CheckedGameBoard getGameBoard()
	{
		return gameBoard;  //seems kind of dangerous to return a reference to actual game board....pass back copy?
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
		
		if (!players[tempBoard.getPlayerTurn()].isAI())//don't allow clicking if current player is ai
		{
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
	}
	
	public boolean CanPieceMove(CheckedGamePiece inPiece)  //I think this should be in GameBoard...but it uses the GameState which it doesn't have access to.
	{
		boolean canMove = false;
		
		if (GetGameState() == CheckedGameStates.IDLE || GetGameState() == CheckedGameStates.ANIMATING)  //if game is waiting for input
		{			
			if (inPiece.GetOwner() == gameBoard.getPlayerTurn() )  //and its this pieces owner's turn.
			{
				if (gameBoard.CanPlayerJump(gameBoard.getPlayerTurn())) //see if any of this players pieces can jump
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
	
	public void ChangePlayerTurn()
	{
		gameBoard.ChangePlayerTurn();
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
				
				//gameBoard.beginMovePiece(new CheckedMove(oldX,oldY,newX,newY,gameBoard.getSelectedPiece()));
				//SetGameState(CheckedGameStates.ANIMATING);
				CheckedPlayer currentPlayer = players[gameBoard.getPlayerTurn()];
				if (currentPlayer instanceof CheckedPlayerHuman)
				{
					((CheckedPlayerHuman) currentPlayer).setChoosenMove(new CheckedMove(oldX,oldY,newX,newY,gameBoard.getSelectedPiece()));
				}
				SetGameState(CheckedGameStates.IDLE);
				
				
				//movingPiece.SetPos(newX, newY); //assign the piece to its new space on the board (move the piece)
				
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
	
	public int UpdateGame()
	{
		UpdateTime(System.nanoTime());
		
		int result2 = CheckedGameAction.NOTHING;
		
		if (GetGameState() == CheckedGameStates.IDLE  || GetGameState() == CheckedGameStates.MULTIJUMP)
		{
			//game is currently idle.  see if a player made a move.
			CheckedMove move = players[gameBoard.getPlayerTurn()].getMove();
			if (move != null)
			{
				CheckedGamePiece movePiece = getGameBoard().getPiece(move.getPiece());
				//movePiece.setCurPos(movePiece.GetX(), movePiece.GetY());
				getGameBoard().beginMovePiece(move); //make the move		
				SetGameState(CheckedGameStates.ANIMATING);
			}
		}
		
		else if (GetGameState() == CheckedGameStates.ANIMATING || GetGameState() == CheckedGameStates.RETURNING)
		{
			
			//do animation stuff...currently only moves the selected piece to where its supposed to be.
			//might do other things later.
			
			//really rough code...needs to be refined.
			
			double tempMoveSpeed = 5.0;  //temporarily here.  probably should be a constant or part of the class that is being animated.
			
			double moveDist = tempMoveSpeed * ( GetFrameTime() / 1000000000.0 );
			
			CheckedGamePiece curPiece = getGameBoard().getPiece(getGameBoard().getSelectedPiece());
			//curPiece.setCurX(curPiece.getCurX()+moveDist);
			
			if (Math.pow( Math.pow(curPiece.GetX()-curPiece.getCurX(), 2) + Math.pow(curPiece.GetY()-curPiece.getCurY(), 2), 0.5) < moveDist)
			{
				//finish moving the piece
				//System.out.println("done");
				//curPiece.setCurPos(curPiece.GetX(), curPiece.GetY());
				//curPiece.SetMoving(false);
				
				if (GetGameState() == CheckedGameStates.ANIMATING)
				{
					int result = getGameBoard().finishMovePiece();
					
					if(result == CheckedMove.MOVE || result == CheckedMove.JUMP || result == CheckedMove.KING)
					{
						ChangePlayerTurn();
						SetGameState(CheckedGameStates.IDLE);
					}
					else if(result == CheckedMove.MULTIJUMP)
					{
						SetGameState(CheckedGameStates.MULTIJUMP); //set mode to multi=jump
					}
					//game.RestorePrevGameState();
					
					//see if this move ended the game
								
					if(!gameBoard.gameOver())
					{
						//if it did not end the game...get move from player if AI.
						if ( getPlayer(gameBoard.getPlayerTurn()).isAI() )
						{
							/*CheckedMove aiMove = getPlayer(gameBoard.getPlayerTurn()).getMove( new CheckedGameBoard(getGameBoard()) ) ; //send copy of the gameboard to the ai...I don't think the AI needs a reference to the real one.
							CheckedGamePiece movePiece = getGameBoard().getPiece(aiMove.getPiece());
							movePiece.setCurPos(movePiece.GetX(), movePiece.GetY());
							getGameBoard().beginMovePiece(aiMove); //make the move		
							SetGameState(CheckedGameStates.ANIMATING);*/
							((CheckedPlayerAI) getPlayer(gameBoard.getPlayerTurn())).CalcMove( new CheckedGameBoard(getGameBoard()) );
						}
					}
					else
					{
						result2 = CheckedGameAction.ENDGAME;
					}
				}
				else
				{
					//peice returning
					curPiece.SetMoving(false);
					SetGameState(CheckedGameStates.IDLE);
				}
			}
			else
			{
				//move it the moveDist
				//System.out.println(curPiece.GetX()+","+curPiece.getCurX()+","+curPiece.GetY()+","+curPiece.getCurY());
				double angle = Math.atan2(curPiece.GetY()-curPiece.getCurY(), curPiece.GetX()-curPiece.getCurX());
				
				curPiece.setCurPos(curPiece.getCurX()+(Math.cos(angle)*moveDist), curPiece.getCurY()+(Math.sin(angle)*moveDist));			
				
			}
		}
		return result2;
	}

	
}
