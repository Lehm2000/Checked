package ja.checked;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class CheckedGameBoardPanel extends JPanel implements MouseListener, MouseMotionListener, ActionListener

{
	private static final long serialVersionUID = 1L;

	final static boolean DEBUG = false;
	
	private int gameScreen = CheckedGameScreen.MAIN;  //what game screen are we on?
	
	private CheckedGameScreen mainScreen;
	private CheckedGameScreen endScreen;
	
	private CheckedGame game = new CheckedGame();  //instantiate game class.  correct place?
	
	private int mouseDownPosX;  //where did mouse down event occur...used for dragging pieces
	private int mouseDownPosY;	//where did mouse down event occur...used for dragging pieces
	private int mousePosX;	//where is the mouse currently
	private int mousePosY;
	
	private double spaceSize = 90.0;
	
	private ArrayList<Cursor> cursors = new ArrayList<Cursor>();
	
	Timer gameTimer;
	
	public CheckedGameBoardPanel() 
	{
		//constructor
		mousePosX = 0;
		mousePosY = 0;
		cursors.add(new Cursor(Cursor.HAND_CURSOR));
		cursors.add(new Cursor(Cursor.DEFAULT_CURSOR));
		
		//setup the main screen
		mainScreen = new CheckedGameScreen();
		mainScreen.addButton(new CheckedGameButton(400, 50, new Point(640,640), "Play Game", new Font("Arial",Font.BOLD,36),CheckedGameAction.PLAYGAME));
		
		//load image...in three steps so its easier to debug.
		//is this the best way to load the image?  Investigate further
		URL url = getClass().getResource("checkedMainScreen.png");
		Image tempImage = new ImageIcon(url).getImage();  
		mainScreen.background = tempImage;
		
		//setup the end screen
		endScreen = new CheckedGameScreen();
		endScreen.addButton(new CheckedGameButton(400, 50, new Point(640,620), "Play Again", new Font("Arial",Font.BOLD,36),CheckedGameAction.PLAYGAME));
		endScreen.addButton(new CheckedGameButton(400, 50, new Point(640,680), "Exit", new Font("Arial",Font.BOLD,36),CheckedGameAction.EXIT));
		
		url = getClass().getResource("checkedMainScreen.png");
		tempImage = new ImageIcon(url).getImage();  
		endScreen.background = tempImage;
		
		
		//set the start screen
		gameScreen = CheckedGameScreen.MAIN;
		
		this.addMouseListener(this); 
        this.addMouseMotionListener(this);
        
        //add a timer to control animated stuff
        gameTimer = new Timer(1000/60, this);
        gameTimer.start();
	}
	
	public void paintComponent (Graphics g)
	{
		super.paintComponent(g);
		
		Graphics2D cG = (Graphics2D) g;
		
		cG.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);  //turn on antialiasing
		
		if (gameScreen == CheckedGameScreen.MAIN)
		{
			paintMainScreen(cG);
		}
		else if (gameScreen == CheckedGameScreen.GAME)
		{
			paintGameScreen(cG);
		}
		else if (gameScreen == CheckedGameScreen.END)
		{
			paintEndScreen(cG);
		}
		
		
		//some debug stuff
		if (DEBUG)
		{
			cG.setColor(new Color(255,255,255));
			cG.drawString("Panel Coords: " + mousePosX + ", " + mousePosY, 8,16);
			//cG.drawString("Board Coords: " + ConvertCoordsPanel2BoardX(mousePosX) + ", " + ConvertCoordsPanel2BoardY(mousePosY), 8,32);
			cG.drawString(String.format("Board Coords: %.2f, %.2f", ConvertCoordsPanel2BoardX(mousePosX), ConvertCoordsPanel2BoardY(mousePosY)), 8,32);
			//cG.drawString("Piece 1 Mouse Distance: " + gameBoard.getPiece(0).mouseDist, 8,48);
			cG.drawString("GameState: " + game.GetGameState(), 8,48);
			cG.drawString("Mouse Down Coords: " + (mouseDownPosX) + ", " + (mouseDownPosY), 8,64);
			//cG.drawString("Frame Time: "+ (1000000000.0/(double)game.GetFrameTime()), 8, 80);
			cG.drawString(String.format("FPS: %.2f", (1000000000.0/(double)game.GetFrameTime())), 8, 80);
		}
	}
	
	private void drawButton(Graphics2D cG, CheckedGameButton curButton)
	{
		//set the button color...currently hardcoded..maybe have color as a button member
		cG.setColor(new Color(255,255,255));
		
		//save current font
		Font tempFont = cG.getFont();
				
		Point buttonPos = curButton.getPosition();
		
		//draw button outline
		//cG.drawRect(buttonPos.x-(curButton.getWidth()/2), buttonPos.y-(curButton.getHeight()/2), curButton.getWidth(), curButton.getHeight());
		cG.drawRoundRect(buttonPos.x-(curButton.getWidth()/2), buttonPos.y-(curButton.getHeight()/2), curButton.getWidth(), curButton.getHeight(),20,20);
		
		//draw buttonText;
		cG.setFont(curButton.getFont());
		//
		FontMetrics fontInfo = cG.getFontMetrics();
		Rectangle2D textRect = fontInfo.getStringBounds(curButton.getCaption(), null);
		cG.drawString(curButton.getCaption(), buttonPos.x - ( (int)textRect.getWidth()/2 ), buttonPos.y + ( (int)textRect.getHeight()/2 ) - fontInfo.getMaxDescent());
		//cG.getFontMetrics().stringWidth(curButton.getCaption());
		
		//restore font.
		cG.setFont(tempFont);	
	}
	
	private void paintMainScreen(Graphics2D cG)
	{
		cG.setColor(new Color(255, 255, 255));
		cG.fillRect(0,0,this.getWidth(),this.getHeight());
		
		//draw screen background;
		cG.drawImage(mainScreen.background, 0, 0,null);
		
		
		
		//draw buttons
		for (int i = 0; i<mainScreen.numButtons();i++)
		{
			CheckedGameButton curButton = mainScreen.getButton(i);
			
			drawButton(cG,curButton);			
		}
		
			
	}
	
	private void paintEndScreen(Graphics2D cG)
	{
		//save current font
		Font tempFont = cG.getFont();
				
		//draw screen background;
		cG.drawImage(endScreen.background, 0, 0,null);
		
		cG.setColor(new Color(0,0,0,128));
		cG.fillRect(0, 0,1280, 720);
		
		cG.setColor(new Color(255,255,255));
		
		
		//draw some words
		cG.setFont(new Font("Arial",Font.ITALIC,90));  //TODO create standard fonts for program to use when program loads instead of constantly recreating them
		
		String tempString = "Game Over!";
		int stringWidth = cG.getFontMetrics().stringWidth(tempString);  //TODO create function to find string center?
		cG.drawString(tempString, 640-(stringWidth/2),160);
		
		cG.setFont(new Font("Arial",Font.BOLD,110)); //TODO create standard font
		
		tempString = game.getWinner() == 0 ? "Player 1 Has Won" : "Player 2 Has Won";
		stringWidth = cG.getFontMetrics().stringWidth(tempString);
		cG.drawString(tempString, 640-(stringWidth/2), 280);
		
		//draw buttons
		for (int i = 0; i<endScreen.numButtons();i++)
		{
			CheckedGameButton curButton = endScreen.getButton(i);
					
			drawButton(cG,curButton);			
		}
		
		cG.setFont(tempFont);
	}
	
	private void paintGameScreen (Graphics2D cG)
	{
		//int xOffset = (int) (spaceSize*2);
		
		//draw background..currently just draw grey rect filling whole panel
		cG.setColor(new Color(128, 128, 128));
		cG.fillRect(0,0,this.getWidth(),this.getHeight());
		//cG.drawLine(0, 0, 64*12, 64*8);
				
		CheckedGameBoard gameBoard = game.getGameBoard();  //get current game board.
				
		//draw board background
		for (int i = 0; i< 64 ;i++)
		{
			if (i%2 == (i/8)%2)
			{
				cG.setColor(new Color(229, 192, 136));
			}
			else
				cG.setColor(new Color(148, 104, 79));
					
			cG.fillRect(ConvertCoordsBoard2PanelX(i%8), ConvertCoordsBoard2PanelY(i/8), (int)spaceSize, (int)spaceSize);
		}
				
		//when piece being moved...highlight move locations
		if (game.GetGameState() == CheckedGameStates.MOVING) //if gamestate dragging
		{
			cG.setColor(new Color(255,255,255));
			cG.setStroke(new BasicStroke(3));
			CheckedGamePiece selectedPiece = game.getGameBoard().getPiece(game.getSelectedPiece());
			ArrayList<CheckedMove> highlightSpaces = game.AllowedMoves(selectedPiece);  //make duplicate of pieces possible moves...we'll probably be altering it and don't want to alter original
					
			for (int i = 0; i < highlightSpaces.size();i++)
			{
				cG.drawRect( ConvertCoordsBoard2PanelX( highlightSpaces.get(i).getMoveX() ), ConvertCoordsBoard2PanelY( highlightSpaces.get(i).getMoveY() ), (int)spaceSize, (int)spaceSize);
			}			
		}
						
		//draw pieces
		for(int i = 0;i<gameBoard.getNumGamePieces();i++)  //draw all the pieces plus 1...draws the selected peice a second time...
		{
			CheckedGamePiece currentPiece;
			//code so that it draws the selected piece last...so that if its being moved it always shows on top.
			if (i==gameBoard.getNumGamePieces()-1)
			{
				currentPiece = gameBoard.getPiece(game.getSelectedPiece());
			}
			else if (i<game.getSelectedPiece())
			{
				currentPiece = gameBoard.getPiece(i);
			}
			else
			{
				currentPiece = gameBoard.getPiece(i+1);
			}
					
							
			Color playerColor = game.getPlayer(currentPiece.GetOwner()).getColor();
					
			//change piece color if mouseover.
			if (currentPiece.GetHighlighted() && ( game.GetGameState() == CheckedGameStates.IDLE || game.GetGameState() == CheckedGameStates.MULTIJUMP) )
			{
				playerColor = new Color( (playerColor.getRed() + 255)/2 ,(playerColor.getGreen() + 255)/2,(playerColor.getBlue() + 255)/2);
			}
					
					
			Rectangle drawArea;
			//get coords for drawing piece.
			if ( currentPiece.isMoving() )
			{
				//drawArea = new Rectangle(xOffset + (int) (( currentPiece.getCurX() )*64.0), (int) ( currentPiece.getCurY()*64.0 ), 64, 64); //this is the size of a board sqaure.
				drawArea = new Rectangle(ConvertCoordsBoard2PanelX( currentPiece.getCurX() ), ConvertCoordsBoard2PanelY( currentPiece.getCurY() ), (int)spaceSize, (int)spaceSize); //this is the size of a board sqaure.
			}
			else
			{
				//drawArea = new Rectangle(xOffset + (int) ( (currentPiece.GetX() )*64), (int) ( currentPiece.GetY() )*64, 64, 64); //this is the size of a board sqaure.
				drawArea = new Rectangle(ConvertCoordsBoard2PanelX( currentPiece.GetX() ), ConvertCoordsBoard2PanelY( currentPiece.GetY() ), (int)spaceSize, (int)spaceSize); //this is the size of a board sqaure.
			}
					
			//shrink draw area to the circle radius...seems kind of ugly...better way?
			drawArea.grow( (int)  ( 32.0-(currentPiece.GetRadius()*64.0) ) *-1, (int) ( 32.0-(currentPiece.GetRadius()*64.0) ) *-1);
			cG.setColor( new Color(0,0,0) );
					
			//set border color...based on if piece can be moved.
			if (game.CanPieceMove(currentPiece))
			{
				cG.setColor( new Color(255,255,255) );
			}
			else
			{
				cG.setColor( new Color(0,0,0) );
			}
			cG.fillOval(drawArea.x, drawArea.y, drawArea.width, drawArea.height);
					
			cG.setColor( playerColor );
			drawArea.grow(-2,-2);  //magic numbers but leave since its being replace with bitmaps eventually
			cG.fillOval(drawArea.x, drawArea.y, drawArea.width, drawArea.height);
					
			//temp draw k for King...eventually will just be different bitmaps.
			if (currentPiece instanceof CheckedGamePieceKing)
			{
				Font tempFont = cG.getFont();
						
				cG.setColor(new Color (0,0,0));
				cG.setFont( new Font("Arial",Font.BOLD,24));
				cG.drawString("K", drawArea.x+16, drawArea.y+32);
						
				cG.setFont(tempFont);				
			}
			
		}
		
	}
	
	public double ConvertCoordsPanel2BoardX(int panelCoordX)
	{
		int leftMargin = (this.getWidth() - (int)(spaceSize * 8))/2;
		
		return ((double)(panelCoordX-leftMargin)/spaceSize);
	}
	
	public double ConvertCoordsPanel2BoardY(int panelCoordY)
	{
		return (double)panelCoordY/spaceSize;
	}
	
	public int ConvertCoordsBoard2PanelX(double boardCoordX)
	{
		int leftMargin = (this.getWidth() - (int)(spaceSize * 8))/2;
		
		return (int) ((boardCoordX)*spaceSize)+leftMargin;  //hopefully the cast is okay here.
	}
	
	public int ConvertCoordsBoard2PanelY(double boardCoordY)
	{
		return (int) (boardCoordY*spaceSize);	//hopefully the cast is okay here.
	}
	
	public void performAction(int action)
	{
		if (action == CheckedGameAction.NOTHING)
		{
			//do nothing
		}
		else if (action == CheckedGameAction.PLAYGAME)
		{
			game.InitGame();  //reset game
			gameScreen = CheckedGameScreen.GAME;  //change to game screen.
		}
		else if (action == CheckedGameAction.ENDGAME)
		{
			gameScreen = CheckedGameScreen.END;
		}
		else if (action == CheckedGameAction.EXIT)
		{
			shutdown();
		}
	}
	
	public void shutdown()
	{
		//put any future clean up here.
		
		//then exit.
		System.exit(0);
	}
	
	public void SetMousePos(int mouseX,int mouseY)
	{
		//set mouse position
		mousePosX = mouseX;
		mousePosY = mouseY;
		
		boolean overClickable = false;
		
		if (gameScreen == CheckedGameScreen.MAIN)
		{
			//go through all buttons to see if its inside one
			for (int i = 0;i<mainScreen.numButtons();i++)
			{
				CheckedGameButton curButton = mainScreen.getButton(i);
								
				if (curButton.pointInside(mouseX,mouseY))
				{
					overClickable = true;
				}
				
			}
		}
		else if (gameScreen == CheckedGameScreen.END)
		{
			//go through all buttons to see if its inside one
			for (int i = 0;i<endScreen.numButtons();i++)
			{
				CheckedGameButton curButton = endScreen.getButton(i);
								
				if (curButton.pointInside(mouseX,mouseY))
				{
					overClickable = true;
				}
				
			}
		}
		else if (gameScreen == CheckedGameScreen.GAME)
		{		
			//do mouse position stuff for game...maybe create function inside CheckedGame?  
			
			//check if we are over something.
			CheckedGameBoard tempBoard = game.getGameBoard();
			
			
			//check if over game piece
			if (game.GetGameState() == CheckedGameStates.IDLE)
			{
				for (int i = 0;i<tempBoard.getNumGamePieces();i++)
				{
					CheckedGamePiece curPiece = tempBoard.getPiece(i);
					boolean isInsidePiece = curPiece.PointInside( ConvertCoordsPanel2BoardX(mouseX), ConvertCoordsPanel2BoardY(mouseY) );
					
					if (isInsidePiece && game.CanPieceMove(curPiece))
					{
						overClickable = isInsidePiece || overClickable; //if already true don't set back to false
						curPiece.SetHighlighted(true);
					}
					else
					{
						curPiece.SetHighlighted(false);
					}	
				}
			}
			else if (game.GetGameState() == CheckedGameStates.MULTIJUMP)
			{
				CheckedGamePiece curPiece = tempBoard.getPiece(game.getSelectedPiece());
				boolean isInsidePiece = curPiece.PointInside( ConvertCoordsPanel2BoardX(mouseX), ConvertCoordsPanel2BoardY(mouseY) );
				
				if (isInsidePiece && game.CanPieceMove(curPiece))
				{
					overClickable = isInsidePiece || overClickable; //if already true don't set back to false
					curPiece.SetHighlighted(true);
				}
				else
				{
					curPiece.SetHighlighted(false);
				}					
			}			
		}
		
		if (overClickable )
		{
			setCursor(cursors.get(0));
		}
		else
			setCursor(cursors.get(1));
				
	}
	
	//updates the position of the moving piece using the provided mouse coords...
	//somehow move to the CheckedGame class?
	public void UpdateMovingPiecePos(int mouseX, int mouseY)  
	{
		CheckedGamePiece movingPiece = game.getGameBoard().getPiece( game.getSelectedPiece() );
		
		double startX = movingPiece.GetX();  //where did the piece start? (what board space is it currently assigned to)
		double startY = movingPiece.GetY();
		
		double mouseChangeX = ConvertCoordsPanel2BoardX(mouseX)-ConvertCoordsPanel2BoardX(mouseDownPosX); //how much has the mouse changed.
		double mouseChangeY = ConvertCoordsPanel2BoardY(mouseY)-ConvertCoordsPanel2BoardY(mouseDownPosY);
		
		movingPiece.setCurPos(mouseChangeX+startX, mouseChangeY+startY);
		
	}
	
	public void MouseClick(int mouseX, int mouseY)  //replaced by mousepressed...needed anymore?
	{
		//currently doesn't care which mouse button;
		//eventually need a play button...for now just start playing if the player clicked.
		if (gameScreen == CheckedGameScreen.MAIN)
		{
			//go through each button and find out if it was clicked on.
			int numButtons = mainScreen.numButtons();
			for (int i = 0;i<numButtons;i++)
			{
				CheckedGameButton curButton = mainScreen.getButton(i);
				if (curButton.pointInside(mouseX, mouseY))
				{
					performAction(curButton.getAction());
					break;  //only do one action...just in case overlapping buttons.
				}
			}
		}
		else if (gameScreen == CheckedGameScreen.END)
		{
			//go through each button and find out if it was clicked on.
			int numButtons = endScreen.numButtons();
			for (int i = 0;i<numButtons;i++)
			{
				CheckedGameButton curButton = endScreen.getButton(i);
				if (curButton.pointInside(mouseX, mouseY))
				{
					performAction(curButton.getAction());
					break;  //only do one action...just in case overlapping buttons.
				}
			}
		}
	}
	
	public void MousePress(int mouseX, int mouseY)
	{
		if (gameScreen == CheckedGameScreen.GAME)
		{		
			CheckedGameBoard tempBoard = game.getGameBoard();
			
			if (game.GetGameState() == CheckedGameStates.IDLE)  //game currently doing nothing...waiting for player input.  //need some kind of global constant or enums for the gamestates??
			{			
				//see if player clicked on piece that belongs to player
				
				for (int i = 0;i<tempBoard.getNumGamePieces();i++)
				{
					CheckedGamePiece curPiece = tempBoard.getPiece(i);
					boolean isInsidePiece = curPiece.PointInside( ConvertCoordsPanel2BoardX(mouseX), ConvertCoordsPanel2BoardY(mouseY) );
					if (isInsidePiece && game.CanPieceMove(curPiece))
					{					
						game.SetGameState(CheckedGameStates.MOVING);
						curPiece.SetMoving(true);
						game.setSelectedPiece(i);
						mouseDownPosX = mouseX;  
						mouseDownPosY = mouseY;
						
						curPiece.setCurPos(curPiece.GetX(), curPiece.GetY());					
					}				
				}		
			}
			else if (game.GetGameState() == CheckedGameStates.MULTIJUMP)
			{
				CheckedGamePiece curPiece = tempBoard.getPiece(game.getSelectedPiece());
				boolean isInsidePiece = curPiece.PointInside( ConvertCoordsPanel2BoardX(mouseX), ConvertCoordsPanel2BoardY(mouseY) );
				if (isInsidePiece && game.CanPieceMove(curPiece))
				{
					
					game.SetGameState(CheckedGameStates.MOVING);
					curPiece.SetMoving(true);
					//game.setSelectedPiece(i);
					mouseDownPosX = mouseX;  
					mouseDownPosY = mouseY;
					
					curPiece.setCurPos(curPiece.GetX(), curPiece.GetY());
					
				}		
				
			}
		}
	}
	
	public void MouseDragged(int mouseX, int mouseY)
	{	
		if(gameScreen==CheckedGameScreen.GAME)
		{
			if(game.GetGameState() == CheckedGameStates.MOVING)
			{			
				UpdateMovingPiecePos(mouseX, mouseY);
			}
		}			
	}
	
	public void MouseRelease(int mouseX,int mouseY)
	{
		if (gameScreen == CheckedGameScreen.GAME)
		{
			if (game.GetGameState() == CheckedGameStates.MOVING) //mouse released after dragging piece.
			{	
				CheckedGamePiece movingPiece = game.getGameBoard().getPiece( game.getSelectedPiece() );  //get the piece being moved.
				
				//do final move based on new mouse coords
				UpdateMovingPiecePos(mouseX, mouseY);
				
				//find out what square the center of the piece is over...
				int newX = (int) (movingPiece.getCurX()+0.5);  //this should be the centerX of the piece...which when converted to int should give us the space.
				int newY = (int) (movingPiece.getCurY()+0.5);
				
				//now that we have the space that the center of the piece is over...find out if it was a valid move.
				
				boolean validMove = false;
				
				//get list of allowed moves
				
				ArrayList<CheckedMove> allowedMoves = game.AllowedMoves(movingPiece);
				
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
					movingPiece.SetMoving(false); //and selected piece is not moving.
					
					//calc move distance...used to determine if made jump
					int oldX = movingPiece.GetSpaceX();  //get the space the piece is currently assigned to which is where it used to be...because we haven't updated its assigned space yet.
					int oldY = movingPiece.GetSpaceY();
					int movedX =  newX - oldX;
					//int movedY =  newY - oldY;	
					
					movingPiece.SetPos(newX, newY); //assign the piece to its new space on the board (move the piece)
					
					//king me...change piece type if at opponents 'base'.
					
					if (newY == 7  && movingPiece instanceof CheckedGamePieceManPlayer0)  
					{
						game.getGameBoard().KingPiece(game.getSelectedPiece());
					}
					
					else if (newY == 0  && movingPiece instanceof CheckedGamePieceManPlayer1)  
					{
						game.getGameBoard().KingPiece(game.getSelectedPiece());
					}
					
					if (Math.abs(movedX)==2)  //if it was a jump
					{
						//have a jump on our hands...do something about it.
						//start by figuring out coords of space in between.
						int jumpedX = ( (newX + oldX) /2);
						int jumpedY = ( (newY + oldY) /2);
						
						//next find the piece that was at that position.
						game.CapturePiece(jumpedX, jumpedY);
						
						//next figure out if this peice can jump again.
						if (game.FindJumps(movingPiece).size()!=0)  //if findjumps returns some then it can jump
						{												
							game.SetGameState(CheckedGameStates.MULTIJUMP); //set mode to multi=jump
						}
						else  
						{						
							game.ChangePlayerTurn();
							game.SetGameState(CheckedGameStates.IDLE); //set mode to idle
						}
					}
					else  //if not
					{
						//change turns.
						game.ChangePlayerTurn();
						game.SetGameState(CheckedGameStates.IDLE); //set mode to idle
					}	
					
					//see if this move ended the game
					if (game.gameOver())
					{
						performAction(CheckedGameAction.ENDGAME);
					}
					
				}
				else
				{
					//not valid move
					//restore previous state
					game.SetGameState(CheckedGameStates.ANIMATING);
					
				}
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		
		SetMousePos(arg0.getX(), arg0.getY());
		MouseDragged(arg0.getX(), arg0.getY());
		//this.repaint();		
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		SetMousePos(arg0.getX(), arg0.getY());				
		//this.repaint();
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		//System.out.println("clicked! X:" + arg0.getX() + " Y:"+arg0.getY());
		MouseClick(arg0.getX(), arg0.getY());
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		MousePress(arg0.getX(), arg0.getY());
		//this.repaint();
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		MouseRelease(arg0.getX(),arg0.getY());
		//this.repaint();
		
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		//this is for the timer.
		game.UpdateTime(System.nanoTime());
		
		if (game.GetGameState() == CheckedGameStates.ANIMATING)
		{
			//do animation stuff...currently only moves the selected piece to where its supposed to be.
			//might do other things later.
			
			//really rough code...needs to be refined.
			
			double tempMoveSpeed = 20.0;  //temporarily here.  probably should be a constant or part of the class that is being animated.
			
			double moveDist = tempMoveSpeed * ( game.GetFrameTime() / 1000000000.0 );
			
			CheckedGamePiece curPiece = game.getGameBoard().getPiece(game.getSelectedPiece());
			//curPiece.setCurX(curPiece.getCurX()+moveDist);
			double angle = Math.atan2(curPiece.GetY()-curPiece.getCurY(), curPiece.GetX()-curPiece.getCurX());
			
			curPiece.setCurPos(curPiece.getCurX()+(Math.cos(angle)*moveDist), curPiece.getCurY()+(Math.sin(angle)*moveDist));
			
			if ( Math.pow( Math.pow(curPiece.GetX()-curPiece.getCurX(), 2) + Math.pow(curPiece.GetY()-curPiece.getCurY(), 2), 0.5) <=0.2)
			{
				curPiece.SetMoving(false);
				game.RestorePrevGameState();
			}
		}
		
		this.repaint();
		
		
	}
	
	
	
}
