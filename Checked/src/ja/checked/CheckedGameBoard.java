package ja.checked;

import java.util.ArrayList;

public class CheckedGameBoard 
{
	private int playerTurn;  //whose turn is it 0 or 1;
	private ArrayList<CheckedPiece> gamePieces = new ArrayList<CheckedPiece>();
	private ArrayList<CheckedPiece> capturedPieces = new ArrayList<CheckedPiece>();
	
	private int selectedPiece;  //used for mouse dragging and jumping
	CheckedMove currentMove;  //stores what the current move is during move animations
	private int winner; //-1 no one (game ongoing), 0 player-1, 1 player-2
	
	//constructor.
	public CheckedGameBoard()
	{
		//reset();
	}
	
	//copy constructor
	public CheckedGameBoard(CheckedGameBoard sourceBoard) 
	{
		//create an exact duplication of the provided board
		
		//set variables
		this.playerTurn = sourceBoard.getPlayerTurn();
		this.selectedPiece = sourceBoard.getSelectedPiece();
		this.winner = sourceBoard.getWinner();
		if (sourceBoard.currentMove == null)
		{
			this.currentMove = null;
		}
		else
		{
			this.currentMove = new CheckedMove(sourceBoard.currentMove);
		}
		
		//make sure there are no gamePieces currently in the list
		this.gamePieces.clear();
		
		//copy all pieces from the source board to the new one
		for (int i = 0; i< sourceBoard.getNumGamePieces();i++)
		{
			//make a copy of each piece
			CheckedPiece curPiece = sourceBoard.getPiece(i);
			CheckedPiece copyPiece = null;
			
			//first find what type it is and then create a copy 
			if (curPiece instanceof CheckedPieceManPlayer0 )
			{
				copyPiece = new CheckedPieceManPlayer0(curPiece);
			}
			else if (curPiece instanceof CheckedPieceManPlayer1 )
			{
				copyPiece = new CheckedPieceManPlayer1(curPiece);
			}
			else if (curPiece instanceof CheckedPieceKing )
			{
				copyPiece = new CheckedPieceKing(curPiece);
			}
			else
			{
				///something totally wrong...what to do.
			}
			
			//and add it to the ArrayList
			this.gamePieces.add(copyPiece);
		}
		
		//clear the capturedPieces too
		capturedPieces.clear();
	}
	
	// Generated hashCode and equals.
	//==============================================================
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		//result = prime * result
		//		+ ((currentMove == null) ? 0 : currentMove.hashCode());
		result = prime * result
				+ ((gamePieces == null) ? 0 : gamePieces.hashCode());
		result = prime * result + playerTurn;
		//result = prime * result + selectedPiece;
		result = prime * result + winner;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CheckedGameBoard other = (CheckedGameBoard) obj;
		/*if (currentMove == null) {
			if (other.currentMove != null)
				return false;
		} else if (!currentMove.equals(other.currentMove))
			return false;*/
		if (gamePieces == null) 
		{
			if (other.gamePieces != null)
				return false;		
		} 
		else if (!gamePieces.equals(other.gamePieces))
			return false;
		
		if (playerTurn != other.playerTurn)
			return false;
		//if (selectedPiece != other.selectedPiece)
			//return false;
		if (winner != other.winner)
			return false;
		return true;
	}
	
	//==============================================================

	public int getSelectedPiece() 
	{
		return selectedPiece;
	}

	public void setSelectedPiece(int selectedPiece) 
	{
		this.selectedPiece = selectedPiece;
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

	public void reset()
	{
		//clear the board before adding the pieces back.
		gamePieces.clear();
		
		//player 1's pieces
		gamePieces.add(new CheckedPieceManPlayer0(0,1,0));
		gamePieces.add(new CheckedPieceManPlayer0(0,3,0));
		gamePieces.add(new CheckedPieceManPlayer0(0,5,0));
		gamePieces.add(new CheckedPieceManPlayer0(0,7,0));
		
		gamePieces.add(new CheckedPieceManPlayer0(0,0,1));
		gamePieces.add(new CheckedPieceManPlayer0(0,2,1));
		gamePieces.add(new CheckedPieceManPlayer0(0,4,1));
		gamePieces.add(new CheckedPieceManPlayer0(0,6,1));
		
		gamePieces.add(new CheckedPieceManPlayer0(0,1,2));
		gamePieces.add(new CheckedPieceManPlayer0(0,3,2));
		gamePieces.add(new CheckedPieceManPlayer0(0,5,2));
		gamePieces.add(new CheckedPieceManPlayer0(0,7,2));
		
		
		//player 2's pieces
		gamePieces.add(new CheckedPieceManPlayer1(1,0,5));
		gamePieces.add(new CheckedPieceManPlayer1(1,2,5));
		gamePieces.add(new CheckedPieceManPlayer1(1,4,5));
		gamePieces.add(new CheckedPieceManPlayer1(1,6,5));
				
		gamePieces.add(new CheckedPieceManPlayer1(1,1,6));
		gamePieces.add(new CheckedPieceManPlayer1(1,3,6));
		gamePieces.add(new CheckedPieceManPlayer1(1,5,6));
		gamePieces.add(new CheckedPieceManPlayer1(1,7,6));
				
		gamePieces.add(new CheckedPieceManPlayer1(1,0,7));
		gamePieces.add(new CheckedPieceManPlayer1(1,2,7));
		gamePieces.add(new CheckedPieceManPlayer1(1,4,7));
		gamePieces.add(new CheckedPieceManPlayer1(1,6,7));
		
		//test config
		/*gamePieces.add(new CheckedGamePieceManPlayer0(0,3,0));
		gamePieces.add(new CheckedGamePieceManPlayer1(1,0,1));*/
		
		playerTurn = 0;  //set player turn to 1.	
	}
	
	
	public CheckedPiece getPiece(int spaceNum)
	{
		return gamePieces.get(spaceNum);
	}
	
	public CheckedMove getCurrentMove() {
		return currentMove;
	}

	public int getNumGamePieces()
	{
		return gamePieces.size();
		
	}
	
	public int getNumCapturedPieces()
	{
		return capturedPieces.size();
		
	}
	
	public int SpaceOccupied(int spaceX,int spaceY)  //returns -1 if not, player num (0/1) if true
	{
		int isOccupied = -1;
		
		for (int i = 0; i<gamePieces.size();i++)
		{
			CheckedPiece curPiece = gamePieces.get(i);
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
	
	public boolean CanPlayerJump(int playerNum)
	{
		boolean canJump = false;
		
		int numPieces = getNumGamePieces();
		
		for (int i = 0; i<numPieces;i++)
		{
			CheckedPiece curPiece = getPiece(i);
			
			if (curPiece.GetOwner() == playerNum && CanPieceJump(curPiece))
			{
				canJump = true;
				break;
			}
		}
		
		return canJump;
	}
	
	public boolean CanPlayerMove(int playerNum)
	{
		int numPieces = getNumGamePieces();
		
		for (int i = 0; i<numPieces;i++)
		{
			CheckedPiece curPiece = getPiece(i);
			
			if (curPiece.GetOwner() == playerNum)
			{
				if(AllowedMoves(curPiece).size() != 0)
				{
					//the play must be able to make one move.
					return true;
				}
			}
			
		}
		
		return false;
	}
	
	public boolean CanPieceJump(CheckedPiece gamePiece)  //similar to findjumps...but aborts as soon as one is found
	{
		//TODO there is a problem with the code here.
		//During multijumps should only return jumps for selected Piece.
		//we have access to selected peice...but not game mode. So we 
		//don't currently know if we are multijumping or not.  How do we let it know?
		
		ArrayList<CheckedMove> foundMoves = new ArrayList<CheckedMove>();  //allowed jumps in board coords.
		ArrayList<CheckedMove> possibleMoves = new ArrayList<CheckedMove>(gamePiece.GetMoves());  //get possible moves from piece...this is relative 
		
		int piecePosX = gamePiece.GetSpaceX(); 
		int piecePosY = gamePiece.GetSpaceY();
		
		for (int i = 0;i<possibleMoves.size();i++)
		{
			//first see that the space is occupied...and it is occupied by an opponents piece.
			CheckedMove curMove = possibleMoves.get(i);
			int occupied = SpaceOccupied(piecePosX + curMove.getMoveX(), piecePosY + curMove.getMoveY());
			
			if (occupied != -1 && occupied != gamePiece.GetOwner() && SpaceOnBoard(piecePosX + curMove.getMoveX(), piecePosY + curMove.getMoveY())) //space is occupied and the peice belongs to the enemy.
			{
				//now see if the space on the other side is open
				int occupied2 = SpaceOccupied(piecePosX + ( curMove.getMoveX() *2 ), piecePosY + ( curMove.getMoveY() * 2) );
				if (occupied2 == -1 && SpaceOnBoard(piecePosX + ( curMove.getMoveX() *2 ),piecePosY + ( curMove.getMoveY() * 2)) )
				{
					//if it is we have a jump.
					return true;
					
				}
			}
		}
		
		return false;
	}
	
	public boolean CanPieceMove(CheckedPiece inPiece)  //similar to allowed moves...except aborts as soon as it finds something...more efficient
	{
		ArrayList<CheckedMove> allowedMoves = new ArrayList<CheckedMove>(); //create empty arraylist...this will be returned...this is absolute board positions the piece can move to.
		ArrayList<CheckedMove> possibleMoves = new ArrayList<CheckedMove>(inPiece.GetMoves());  //get possible moves from piece...this is relative 
			
		//check jumps first.
		if (CanPieceMove(inPiece))
			return true;
			
		//then check if any regular moves work.
		for (int i = 0; i < possibleMoves.size();i++)
		{
			CheckedMove curMove = possibleMoves.get(i);
			if (IsMoveValid( inPiece,curMove ))
			{
				return true;
			}			
		}
		
		return false;
	}
	
	public ArrayList<CheckedMove> FindJumps(CheckedPiece gamePiece)
	{
		ArrayList<CheckedMove> foundMoves = new ArrayList<CheckedMove>();  //allowed jumps in board coords.
		ArrayList<CheckedMove> possibleMoves = new ArrayList<CheckedMove>(gamePiece.GetMoves());  //get possible moves from piece...this is relative 
		
		int piecePosX = gamePiece.GetSpaceX(); 
		int piecePosY = gamePiece.GetSpaceY();
		
		for (int i = 0;i<possibleMoves.size();i++)
		{
			//first see that the space is occupied...and it is occupied by an opponents piece.
			CheckedMove curMove = possibleMoves.get(i);
			int occupied = SpaceOccupied(piecePosX + curMove.getMoveX(), piecePosY + curMove.getMoveY());
			
			if (occupied != -1 && occupied != gamePiece.GetOwner() && SpaceOnBoard(piecePosX + curMove.getMoveX(), piecePosY + curMove.getMoveY())) //space is occupied and the peice belongs to the enemy.
			{
				//now see if the space on the other side is open
				int occupied2 = SpaceOccupied(piecePosX + ( curMove.getMoveX() *2 ), piecePosY + ( curMove.getMoveY() * 2) );
				if (occupied2 == -1 && SpaceOnBoard(piecePosX + ( curMove.getMoveX() *2 ),piecePosY + ( curMove.getMoveY() * 2)) )
				{
					//if it is we have a possible jump.
					
					//add it to the list
					foundMoves.add(new CheckedMove(piecePosX, piecePosY, piecePosX + ( curMove.getMoveX() *2 ),piecePosY + ( curMove.getMoveY() * 2),-1 ) );	//have to set the negative one because can't determine what peice num is from inside here...will be changed by the function that called it				
				}
			}
		}
		
		return foundMoves;
	}
	
	//takes the piece and using its move list compares it to the board to find all places that piece can go.
	public ArrayList<CheckedMove> AllowedMoves(CheckedPiece inPiece)
	{
		ArrayList<CheckedMove> allowedMoves = new ArrayList<CheckedMove>(); //create empty arraylist...this will be returned...this is absolute board positions the piece can move to.
		ArrayList<CheckedMove> possibleMoves = new ArrayList<CheckedMove>(inPiece.GetMoves());  //get possible moves from piece...this is relative 
			
		//check jumps first.
		allowedMoves.addAll( FindJumps(inPiece) );
		
			
		if (allowedMoves.isEmpty())
		{
			//then check if any regular moves work.
			for (int i = 0; i < possibleMoves.size();i++)
			{
				CheckedMove curMove = possibleMoves.get(i);
				if (IsMoveValid( inPiece,curMove ))
				{
					//convert from relative to absolute
					CheckedMove newMove = new CheckedMove(inPiece.GetSpaceX(),inPiece.GetSpaceY(), curMove.getMoveX()+inPiece.GetSpaceX() , curMove.getMoveY()+inPiece.GetSpaceY(), -1 );  
					//add the move to the list of allowed moves
					allowedMoves.add(newMove); 
				}			
			}
		}
			
		return allowedMoves;
	}
		
	public boolean IsMoveValid(CheckedPiece inPiece, CheckedMove inMove)
	{
		boolean isValid = false;
			
		int startX = inPiece.GetSpaceX(); 
		int startY = inPiece.GetSpaceY();
		int endX = startX + inMove.getMoveX();
		int endY = startY + inMove.getMoveY();
			
		//is piece on the board.
		if ( SpaceOnBoard(endX, endY) )
		{
			//next check if the space is occupied.
			if ( SpaceOccupied(endX, endY) == -1 )
			{
				isValid = true;
			}
		}
			
		return isValid;		
	}		
		
	
		
	public void beginMovePiece(CheckedMove move)
	{
		//int result;  //what is passed back...need constants
		
		CheckedPiece curPiece = gamePieces.get(move.getPiece());
		setSelectedPiece(move.getPiece());
		
		//curPiece.setCurPos(oldX, oldY);
		curPiece.SetPos(move.getMoveX(), move.getMoveY());
		curPiece.SetMoving(true);  //needed for the animation to work.
		currentMove = move;  //remember what move we are doing so finishMovePiece will know
			
		//return result;
	}
	
	public int finishMovePiece()
	{
		int result;
		
		CheckedPiece curPiece = gamePieces.get(currentMove.getPiece());
		
		curPiece.SetMoving(false);
		curPiece.setCurPos(currentMove.getMoveX(), currentMove.getMoveY());
		
		//retrive the move info so we know what it did
		int oldX = currentMove.getSourceX();
		int oldY = currentMove.getSourceY();
		int movedX =  currentMove.getMoveX() - oldX;
		
		//check for kinging
		boolean kinged = false;
		if (!(curPiece instanceof CheckedPieceKing)) //don't king more than once.
		{
			if (currentMove.getMoveY() == 7  && curPiece instanceof CheckedPieceManPlayer0)  
			{
				KingPiece(currentMove.getPiece());
				kinged = true;
			}		
			else if (currentMove.getMoveY() == 0  && curPiece instanceof CheckedPieceManPlayer1)  
			{
				KingPiece(currentMove.getPiece());
				kinged = true;
			}
		}
		
		
		if (Math.abs(movedX)==2)  //if it was a jump
		{
			//have a jump on our hands...do something about it.
			//start by figuring out coords of space in between.
			int jumpedX = ( (currentMove.getMoveX() + oldX) /2);
			int jumpedY = ( (currentMove.getMoveY() + oldY) /2);
					
			//next find the piece that was at that position.
			CapturePiece(jumpedX, jumpedY);
					
			//next figure out if this peice can jump again.
			if (kinged)
			{
				result = CheckedMove.KING;
			}
			else if (FindJumps(curPiece).size()!=0)  //if findjumps returns some then it can jump
			{												
				result = CheckedMove.MULTIJUMP;  //multijump
			}
			else  
			{						
				result = CheckedMove.JUMP; //regular jump turn ended
			}
		}
		else  //if not
		{
			if (kinged)
			{
				result = CheckedMove.KING;
			}
			else
			{
				result = CheckedMove.MOVE;  //regular move turn ended
			}
		}	
		
		return result;
	}
	
	public boolean CapturePiece(int spaceX, int spaceY)
	{
		boolean success = false;
			
		int numPieces = getNumGamePieces();
			
		for (int i = 0; i<numPieces; i++)
		{
			CheckedPiece curPiece = getPiece(i);
			if (curPiece.GetSpaceX() == spaceX && curPiece.GetSpaceY() == spaceY)
			{
				CapturePiece(i);
					
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
	
	public void CapturePiece(int index)
	{
		//TODO check for index out of bounds...how to handle that?
		capturedPieces.add(gamePieces.get(index));
		gamePieces.remove(index);
		
		//return success??		
	}
	
	public void KingPiece(int inPiece)
	{
		//change this peice to a King piece.
		CheckedPiece currentPiece = gamePieces.get(inPiece);
		gamePieces.set(inPiece, new CheckedPieceKing(currentPiece.GetOwner(), currentPiece.GetSpaceX(), currentPiece.GetSpaceY()));
	}
	
	public boolean gameOver()  //return -1 if not game over.  0 if player1 won, 1 if player2.
	{
		boolean found0 = false;  //keeps track if we found a player0 piece
		boolean found1 = false;	//keeps track if we found a player1 piece
		
		//as long as one piece from each team remains 
		
		for (int i = 0;i<getNumGamePieces();i++)
		{
			CheckedPiece curPiece = getPiece(i);
			if (curPiece.GetOwner()==0)
				found0 = true;
			
			if (curPiece.GetOwner()==1)
				found1 = true;
		}
		
		//return !(found0 && found1);  
		
		if (found0 && found1)//if found at least 1 piece from each team.
		{
			//if there are pieces from both players still active see if the current player can make a move
			if(CanPlayerMove(playerTurn))
			{
				return false;
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
	
	

}
