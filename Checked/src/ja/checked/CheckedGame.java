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
	private int selectedPiece;  //used for mouse dragging.
	private int winner; //-1 no one (game ongoing), 0 player-1, 1 player-2
	
	private long curFrameTime;
	private long lastFrameTime;
	
	
	
	//public CheckedGameStates STATES;
	
	public CheckedGame()
	{
		InitGame();
		
	}
	
	public void InitGame()
	{
		players[0] = new CheckedPlayer(new Color(160,160,160));
		players[1] = new CheckedPlayer(new Color(192,0,0));
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

	public int getSelectedPiece() 
	{
		return selectedPiece;
	}

	public void setSelectedPiece(int selectedPiece) 
	{
		this.selectedPiece = selectedPiece;
	}
	
	//takes the piece and using its move list compares it to the board to find all places that piece can go.
	public ArrayList<CheckedMove> AllowedMoves(CheckedGamePiece inPiece)
	{
		ArrayList<CheckedMove> allowedMoves = new ArrayList<CheckedMove>(); //create empty arraylist...this will be returned...this is absolute board positions the piece can move to.
		ArrayList<CheckedMove> possibleMoves = new ArrayList<CheckedMove>(inPiece.GetMoves());  //get possible moves from piece...this is relative 
		
		//check jumps first.
		for (int i = 0; i < possibleMoves.size();i++)
		{
			allowedMoves.addAll( FindJumps(inPiece) );
		}
		
		if (allowedMoves.isEmpty())
		{
			//then check if any regular moves work.
			for (int i = 0; i < possibleMoves.size();i++)
			{
				CheckedMove curMove = possibleMoves.get(i);
				if (IsMoveValid( inPiece,curMove ))
				{
					//convert from relative to absolute
					CheckedMove newMove = new CheckedMove( curMove.getMoveX()+inPiece.GetSpaceX() , curMove.getMoveY()+inPiece.GetSpaceY() );  
					//add the move to the list of allowed moves
					allowedMoves.add(newMove); 
				}			
			}
		}
		
		return allowedMoves;
	}
	
	
	public boolean IsMoveValid(CheckedGamePiece inPiece, CheckedMove inMove)
	{
		boolean isValid = false;
		
		int startX = inPiece.GetSpaceX(); 
		int startY = inPiece.GetSpaceY();
		int endX = startX + inMove.getMoveX();
		int endY = startY + inMove.getMoveY();
		
		//is piece on the board.
		if ( gameBoard.SpaceOnBoard(endX, endY) )
		{
			//next check if the space is occupied.
			if (gameBoard.SpaceOccupied(endX, endY) == -1)
			{
				isValid = true;
			}
		}
		
		return isValid;		
	}
	
	public ArrayList<CheckedMove> FindJumps(CheckedGamePiece gamePiece)
	{
		ArrayList<CheckedMove> foundMoves = new ArrayList<CheckedMove>();  //allowed jumps in board coords.
		ArrayList<CheckedMove> possibleMoves = new ArrayList<CheckedMove>(gamePiece.GetMoves());  //get possible moves from piece...this is relative 
		
		int piecePosX = gamePiece.GetSpaceX(); 
		int piecePosY = gamePiece.GetSpaceY();
		
		for (int i = 0;i<possibleMoves.size();i++)
		{
			//first see that the space is occupied...and it is occupied by an opponents piece.
			CheckedMove curMove = possibleMoves.get(i);
			int occupied = gameBoard.SpaceOccupied(piecePosX + curMove.getMoveX(), piecePosY + curMove.getMoveY());
			
			if (occupied != -1 && occupied != gamePiece.GetOwner() && gameBoard.SpaceOnBoard(piecePosX + curMove.getMoveX(), piecePosY + curMove.getMoveY())) //space is occupied and the peice belongs to the enemy.
			{
				//now see if the space on the other side is open
				int occupied2 = gameBoard.SpaceOccupied(piecePosX + ( curMove.getMoveX() *2 ), piecePosY + ( curMove.getMoveY() * 2) );
				if (occupied2 == -1 && gameBoard.SpaceOnBoard(piecePosX + ( curMove.getMoveX() *2 ),piecePosY + ( curMove.getMoveY() * 2)) )
				{
					//if it is we have a possible jump.
					
					//add it to the list
					foundMoves.add(new CheckedMove(piecePosX + ( curMove.getMoveX() *2 ),piecePosY + ( curMove.getMoveY() * 2)) );					
				}
			}
		}
		
		return foundMoves;
	}
	
	public boolean CanPlayerJump(int playerNum)
	{
		boolean canJump = false;
		
		int numPieces = gameBoard.getNumGamePieces();
		
		for (int i = 0; i<numPieces;i++)
		{
			CheckedGamePiece curPiece = gameBoard.getPiece(i);
			
			if (curPiece.GetOwner() == playerNum && FindJumps(curPiece).size() != 0)
			{
				canJump = canJump || true;
			}
		}
		
		return canJump;
	}
	
	public boolean CanPieceMove(CheckedGamePiece inPiece)
	{
		boolean canMove = false;
		
		if (GetGameState() == 0)  //if game is waiting for input
		{			
			if (inPiece.GetOwner() == getPlayerTurn() )  //and its this pieces owner's turn.
			{
				if (CanPlayerJump(getPlayerTurn())) //see if any of this players pieces can jump
				{
					//if so find if this is a piece that can jump.
					canMove = (FindJumps(inPiece).size() != 0);
				}
				else //player can't make a jump so if this piece has any avail moves.
				{
					canMove = (AllowedMoves(inPiece).size() != 0); // and if there is an allowed move then the piece can move.
				}
			}
		}
		else if (GetGameState() == 2)  //if doing a multi-jump
		{
			if (inPiece == gameBoard.getPiece(selectedPiece))  //if this peice is the piece doing the multi-jump
			{
				canMove = true;
			}
		}
		
		return canMove;
	}
	
	public boolean CapturePiece(int spaceX, int spaceY)
	{
		boolean success = false;
		
		int numPieces = gameBoard.getNumGamePieces();
		
		for (int i = 0; i<numPieces; i++)
		{
			CheckedGamePiece curPiece = gameBoard.getPiece(i);
			if (curPiece.GetSpaceX() == spaceX && curPiece.GetSpaceY() == spaceY)
			{
				gameBoard.CapturePiece(i);
				
				//since the selected Piece is just an index of the gamepieces arraylist - must decrease it by one if the caputured peice is less than it or the index will be off.
				if ( i < selectedPiece )
				{
					selectedPiece--;
				}
				break;  //no need to continue
			}
		}
		
		return success;  //this function should always succeed...logic error if it fails.  Because its only called when jumping and the game should only allow that if there is a piece to jump.
	}

	
}
