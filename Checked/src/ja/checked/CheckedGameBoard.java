package ja.checked;

import java.util.ArrayList;

public class CheckedGameBoard 
{
	private ArrayList<CheckedGamePiece> gamePieces= new ArrayList<CheckedGamePiece>();
	private int selectedPiece;  //used for mouse dragging and jumping
	CheckedMove currentMove;  //stores what the current move is during move animations
	
	//do we need to keep track of player turn in here for ai??
	
	//constructor.
	public CheckedGameBoard()
	{
		//reset();
	}
	
	//copy constructor
	public CheckedGameBoard(CheckedGameBoard sourceBoard) 
	{
		//create an exact duplication of the provided board
		
		//start by making sure there are no gamePieces currently in the list
		this.gamePieces.clear();
		
		for (int i = 0; i< sourceBoard.getNumGamePieces();i++)
		{
			//make a copy of each piece
			CheckedGamePiece curPiece = sourceBoard.getPiece(i);
			CheckedGamePiece copyPiece = null;
			
			//first find what type it is and then create a copy 
			if (curPiece instanceof CheckedGamePieceManPlayer0 )
			{
				copyPiece = new CheckedGamePieceManPlayer0(curPiece);
			}
			else if (curPiece instanceof CheckedGamePieceManPlayer1 )
			{
				copyPiece = new CheckedGamePieceManPlayer1(curPiece);
			}
			else if (curPiece instanceof CheckedGamePieceKing )
			{
				copyPiece = new CheckedGamePieceKing(curPiece);
			}
			else
			{
				///something totally wrong...what to do.
			}
			
			//and add it to the ArrayList
			this.gamePieces.add(copyPiece);
		}
	}
	
	public int getSelectedPiece() 
	{
		return selectedPiece;
	}

	public void setSelectedPiece(int selectedPiece) 
	{
		this.selectedPiece = selectedPiece;
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
	
	public boolean CanPlayerJump(int playerNum)
	{
		boolean canJump = false;
		
		int numPieces = getNumGamePieces();
		
		for (int i = 0; i<numPieces;i++)
		{
			CheckedGamePiece curPiece = getPiece(i);
			
			if (curPiece.GetOwner() == playerNum && FindJumps(curPiece).size() != 0)
			{
				canJump = canJump || true;
			}
		}
		
		return canJump;
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
					CheckedMove newMove = new CheckedMove(inPiece.GetSpaceX(),inPiece.GetSpaceY(), curMove.getMoveX()+inPiece.GetSpaceX() , curMove.getMoveY()+inPiece.GetSpaceY(), -1 );  
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
		
		CheckedGamePiece curPiece = gamePieces.get(move.getPiece());
		setSelectedPiece(move.getPiece());
		
		int oldX = curPiece.GetSpaceX();  //get the space the piece is currently assigned to which is where it used to be...because we haven't updated its assigned space yet.
		int oldY = curPiece.GetSpaceY();
		int movedX =  move.getMoveX() - oldX;
		
		//curPiece.setCurPos(oldX, oldY);
		curPiece.SetPos(move.getMoveX(), move.getMoveY());
		curPiece.SetMoving(true);  //needed for the animation to work.
		currentMove = move;  //remember what move we are doing so finishMovePiece will know
			
		//return result;
	}
	
	public int finishMovePiece()
	{
		int result;
		
		CheckedGamePiece curPiece = gamePieces.get(currentMove.getPiece());
		
		curPiece.SetMoving(false);
		curPiece.setCurPos(currentMove.getMoveX(), currentMove.getMoveY());
		
		//retrive the move info so we know what it did
		int oldX = currentMove.getSourceX();
		int oldY = currentMove.getSourceY();
		int movedX =  currentMove.getMoveX() - oldX;
		
		//check for kinging
		if (currentMove.getMoveY() == 7  && curPiece instanceof CheckedGamePieceManPlayer0)  
		{
			KingPiece(currentMove.getPiece());
		}		
		else if (currentMove.getMoveY() == 0  && curPiece instanceof CheckedGamePieceManPlayer1)  
		{
			KingPiece(currentMove.getPiece());
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
			if (FindJumps(curPiece).size()!=0)  //if findjumps returns some then it can jump
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
			//change turns.
			result = CheckedMove.MOVE;  //regular move turn ended
		}	
		
		return result;
	}
	
	public boolean CapturePiece(int spaceX, int spaceY)
	{
		boolean success = false;
			
		int numPieces = getNumGamePieces();
			
		for (int i = 0; i<numPieces; i++)
		{
			CheckedGamePiece curPiece = getPiece(i);
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
