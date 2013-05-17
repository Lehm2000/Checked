package ja.checked;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
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

	final static boolean DEBUG = true;
	
	private int gameScreen = CheckedGameScreen.MAIN;  //what game screen are we on?
	
	private CheckedGameScreen mainScreen;
	private CheckedGameScreen endScreen;
	
	private CheckedGame game = new CheckedGame();  //instantiate game class.  correct place?
	
	private int mouseDownPosX;  //where did mouse down event occur...used for dragging pieces
	private int mouseDownPosY;	//where did mouse down event occur...used for dragging pieces
	private int mousePosX;	//where is the mouse currently
	private int mousePosY;
	
	private ArrayList<Cursor> cursors = new ArrayList<Cursor>();
	
	//define some standard colors....best place for this?
	private Color stdBlack = new Color(0,0,0);
	private Color stdGray = new Color(128,128,128);
	private Color stdWhite = new Color(255,255,255);
	private Color boardColor1 =new Color(229, 192, 136);
	private Color boardColor2 =new Color(148, 104, 79);
	
	private Dimension defaultBoardDims = new Dimension(1280,720);  //all positions are based on default screensize of 720p.  Everything gets scaled based on this
	private Dimension currentBoardDims;
	//private Rectangle boardCoords = new Rectangle(280,0,720,720);  //where is the board on the screen.  currently hardcoded...might want to make variable.
	private Rectangle boardCoords = new Rectangle(330,50,620,620); 
	private double scaleFactor = 1.0;
	int offsetX = 0;
	int offsetY = 0;
	private double spaceSize = 90.0;
	
	//bitmaps...best place for these or hold them somewhere else
	Image boardBackground;
	Image[] gfxPlayer0 = new Image[2];
	Image[] gfxPlayer1 = new Image[2];
	Image gfxShadow;
	Image gfxHighlight;
	
	
	
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
		
		mainScreen.background = loadImage("checkedMainScreen.png");
		
		//setup the end screen
		endScreen = new CheckedGameScreen();
		endScreen.addButton(new CheckedGameButton(400, 50, new Point(640,620), "Play Again", new Font("Arial",Font.BOLD,36),CheckedGameAction.PLAYGAME));
		endScreen.addButton(new CheckedGameButton(400, 50, new Point(640,680), "Exit", new Font("Arial",Font.BOLD,36),CheckedGameAction.EXIT));
		
		//url = getClass().getResource("checkedMainScreen.png");
		//tempImage = new ImageIcon(url).getImage();  
		//endScreen.background = tempImage;
		
		endScreen.background = loadImage("checkedMainScreen.png");
		
		//set the start screen
		gameScreen = CheckedGameScreen.MAIN;
		
		this.addMouseListener(this); 
        this.addMouseMotionListener(this);
        
        //add a timer to control animated stuff
        gameTimer = new Timer(1, this);
        gameTimer.start();
        
        //load the game bitmaps...probably should do this in separate function.
        //url = getClass().getResource("gameBoard1.png");
		//tempImage = new ImageIcon(url).getImage();  
		boardBackground = loadImage("gameBoard1.png");
		gfxPlayer0[0] = loadImage("gamePieceWhite.png");
		gfxPlayer0[1] = loadImage("gamePieceWhiteKing.png");
		gfxPlayer1[0] = loadImage("gamePieceRed.png");
		gfxPlayer1[1] = loadImage("gamePieceRedKing.png");
		gfxShadow = loadImage("gamePieceShadow.png");
		gfxHighlight = loadImage("gamePieceHighlight.png");
	}
	
	Image loadImage(String path)
	{
		//load image...in three steps so its easier to debug.
		//TODO is this the best way to load the image?  Investigate further.
		URL url = getClass().getResource(path);
		Image tempImage = new ImageIcon(url).getImage();  
		return tempImage;
		
	}
	
	public void paintComponent (Graphics g)
	{
		super.paintComponent(g);
		
		Graphics2D cG = (Graphics2D) g;
		
		cG.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);  //turn on antialiasing
		cG.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
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
			cG.drawString("Player Turn: " + game.getGameBoard().getPlayerTurn(), 0, 96);
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
		cG.drawRoundRect( scaleAndOffsetX(buttonPos.x-(curButton.getWidth()/2) ), scaleAndOffsetY(buttonPos.y-(curButton.getHeight()/2)), scaleVal(curButton.getWidth()), scaleVal(curButton.getHeight()),scaleVal(20), scaleVal(20));
		
		//draw buttonText;
		Font curFont = curButton.getFont();
		//font = new Font("Arial",Font.PLAIN,12);
		cG.setFont(new Font(curFont.getFontName(),curFont.getStyle(),scaleVal(curFont.getSize() ) ));
		//
		FontMetrics fontInfo = cG.getFontMetrics();
		Rectangle2D textRect = fontInfo.getStringBounds(curButton.getCaption(), null);
		cG.drawString(curButton.getCaption(), scaleAndOffsetX( buttonPos.x - ( (int)textRect.getWidth()/2 ) ), scaleAndOffsetY( buttonPos.y + ( (int)textRect.getHeight()/2 ) - fontInfo.getMaxDescent()));
		//cG.getFontMetrics().stringWidth(curButton.getCaption());
		
		//restore font.
		cG.setFont(tempFont);	
	}
	
	private void drawScaledImage(Graphics2D cG,Image theImage,int x, int y, int width, int height)
	{
		cG.drawImage(theImage, scaleAndOffsetX(x),scaleAndOffsetY(y),(int)(width*scaleFactor),(int)(height*scaleFactor),null);
	}
	
	private void paintMainScreen(Graphics2D cG)
	{
		cG.setColor(new Color(255, 255, 255));
		cG.fillRect(0,0,this.getWidth(),this.getHeight());
		
		
		
		//draw screen background;
		//cG.drawImage(mainScreen.background, offsetX, offsetY,(int)(1280*scaleFactor),(int)(720*scaleFactor),null);
		drawScaledImage(cG, mainScreen.background, 0, 0,1280,720);
		
		
		
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
		
		tempString = game.getGameBoard().getWinner() == 0 ? "Player 1 Has Won" : "Player 2 Has Won";
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
		cG.setColor(stdGray);
		cG.fillRect(0,0,this.getWidth(),this.getHeight());
		cG.drawLine(0, 0, 64*12, 64*8);  //what is this?
				
		CheckedGameBoard gameBoard = game.getGameBoard();  //get current game board.
				
		//draw board background
		//draw screen background;
		//cG.drawImage(boardBackground, ConvertCoordsBoard2PanelX(0), ConvertCoordsBoard2PanelY(0),8*(int)spaceSize, 8*(int)spaceSize, null);
		drawScaledImage(cG,boardBackground, boardCoords.x, boardCoords.y,boardCoords.width, boardCoords.height);
		
		/*for (int i = 0; i< 64 ;i++)
		{
			if (i%2 == (i/8)%2)
			{
				cG.setColor(boardColor1);
			}
			else
				cG.setColor(boardColor2);
					
			cG.fillRect(ConvertCoordsBoard2PanelX(i%8), ConvertCoordsBoard2PanelY(i/8), (int)spaceSize, (int)spaceSize);
		}*/
				
		//when piece being moved...highlight move locations
		if (game.GetGameState() == CheckedGameStates.MOVING) //if gamestate dragging
		{
			cG.setColor(stdWhite);
			cG.setStroke(new BasicStroke(3));
			CheckedGamePiece selectedPiece = game.getGameBoard().getPiece(game.getGameBoard().getSelectedPiece());
			ArrayList<CheckedMove> highlightSpaces = game.getGameBoard().AllowedMoves(selectedPiece);  //make duplicate of pieces possible moves...we'll probably be altering it and don't want to alter original
					
			for (int i = 0; i < highlightSpaces.size();i++)
			{
				Rectangle hiRect = boardSpaceCoords(highlightSpaces.get(i).getMoveX(),highlightSpaces.get(i).getMoveY());
				cG.drawRect( scaleAndOffsetX(hiRect.x), scaleAndOffsetY(hiRect.y) , scaleVal(hiRect.width),  scaleVal(hiRect.height));
			}			
		}
		
		//draw shadows for pieces
		
		for (int i = 0;i<gameBoard.getNumGamePieces();i++)
		{
			CheckedGamePiece currentPiece = gameBoard.getPiece(i);
			
			Rectangle drawArea;
			//get coords for drawing piece.
			if ( currentPiece.isMoving() )
			{
				//drawArea = new Rectangle(xOffset + (int) (( currentPiece.getCurX() )*64.0), (int) ( currentPiece.getCurY()*64.0 ), 64, 64); //this is the size of a board sqaure.
				drawArea = boardSpaceCoords(currentPiece.getCurX() , currentPiece.getCurY());
			}
			else
			{
				//drawArea = new Rectangle(xOffset + (int) ( (currentPiece.GetX() )*64), (int) ( currentPiece.GetY() )*64, 64, 64); //this is the size of a board sqaure.
				drawArea = boardSpaceCoords(currentPiece.GetSpaceX() , currentPiece.GetSpaceY()); //this is the size of a board sqaure.
			}
			
			//cG.drawImage(gfxShadow, drawArea.x, drawArea.y,(int)spaceSize, (int)spaceSize, null);
			drawScaledImage(cG,gfxShadow,drawArea.x, drawArea.y,drawArea.width, drawArea.height);
			
		}
						
		//draw pieces
		for(int i = 0;i<gameBoard.getNumGamePieces();i++) 
		{
			
		
			CheckedGamePiece currentPiece;
			int peiceNum;  //keeps track of which peice number this is since we are modifying i
			
			//code so that it draws the selected piece last...so that if its being moved it always shows on top.
			if (i==gameBoard.getNumGamePieces()-1)
			{
				currentPiece = gameBoard.getPiece(game.getGameBoard().getSelectedPiece());
				peiceNum = game.getGameBoard().getSelectedPiece();
			}
			else if (i<game.getGameBoard().getSelectedPiece())
			{
				currentPiece = gameBoard.getPiece(i);
				peiceNum = i;
			}
			else
			{
				currentPiece = gameBoard.getPiece(i+1);
				peiceNum = i + 1;
			}
					
						
			/*Color playerColor = game.getPlayer(currentPiece.GetOwner()).getColor();
					
			//change piece color if mouseover.
			if (currentPiece.GetHighlighted() && ( game.GetGameState() == CheckedGameStates.IDLE || game.GetGameState() == CheckedGameStates.MULTIJUMP) )
			{
				playerColor = new Color( (playerColor.getRed() + 255)/2 ,(playerColor.getGreen() + 255)/2,(playerColor.getBlue() + 255)/2);
			}*/
					
						
			Rectangle drawArea;
			//get coords for drawing piece.
			//TODO: make function...we do it twice.
			if ( currentPiece.isMoving() )
			{
				//drawArea = new Rectangle(xOffset + (int) (( currentPiece.getCurX() )*64.0), (int) ( currentPiece.getCurY()*64.0 ), 64, 64); //this is the size of a board sqaure.
				drawArea = boardSpaceCoords(currentPiece.getCurX() , currentPiece.getCurY());
			}
			else
			{
				//drawArea = new Rectangle(xOffset + (int) ( (currentPiece.GetX() )*64), (int) ( currentPiece.GetY() )*64, 64, 64); //this is the size of a board sqaure.
				drawArea = boardSpaceCoords(currentPiece.GetSpaceX() , currentPiece.GetSpaceY()); //this is the size of a board sqaure.
			}
			
			//draw possible highlight
			if (!game.getPlayer(game.getGameBoard().getPlayerTurn()).isAI() && game.CanPieceMove(currentPiece) )
			{
				//cG.drawImage(gfxHighlight, drawArea.x, drawArea.y,(int)spaceSize, (int)spaceSize, null);
				drawScaledImage(cG, gfxHighlight, drawArea.x, drawArea.y, drawArea.width, drawArea.height);
			}
			
			//draw actual piece
			if (game.getPlayer(currentPiece.GetOwner()).getPlayerNum() == 0)
			{
				if (currentPiece instanceof CheckedGamePieceKing)
				{
				
					//cG.drawImage(gfxPlayer0[1], drawArea.x, drawArea.y,(int)spaceSize, (int)spaceSize, null);
					drawScaledImage(cG, gfxPlayer0[1], drawArea.x, drawArea.y, drawArea.width, drawArea.height);
				}
				else
				{
					//cG.drawImage(gfxPlayer0[0], drawArea.x, drawArea.y,(int)spaceSize, (int)spaceSize, null);
					drawScaledImage(cG, gfxPlayer0[0], drawArea.x, drawArea.y, drawArea.width, drawArea.height);
				}
			}
			else 
			{
				if (currentPiece instanceof CheckedGamePieceKing)
				{
				
					//cG.drawImage(gfxPlayer1[1], drawArea.x, drawArea.y,(int)spaceSize, (int)spaceSize, null);
					drawScaledImage(cG, gfxPlayer1[1], drawArea.x, drawArea.y, drawArea.width, drawArea.height);
				}
				else
				{
					//cG.drawImage(gfxPlayer1[0], drawArea.x, drawArea.y,(int)spaceSize, (int)spaceSize, null);
					drawScaledImage(cG, gfxPlayer1[0], drawArea.x, drawArea.y, drawArea.width, drawArea.height);
				}
			}
			
			
			
			/*		
			//shrink draw area to the circle radius...seems kind of ugly...better way?
			drawArea.grow( (int)  ( 32.0-(currentPiece.GetRadius()*64.0) ) *-1, (int) ( 32.0-(currentPiece.GetRadius()*64.0) ) *-1);
			
			
			
			//draw peice background
			cG.setColor( playerColor );
			cG.fillOval(drawArea.x, drawArea.y, drawArea.width, drawArea.height);
			
			//set border color...based on if piece can be moved.
			if (!game.getPlayer(game.getGameBoard().getPlayerTurn()).isAI() && game.CanPieceMove(currentPiece) )
			{
				cG.setColor( stdWhite );
			}
			else
			{
				cG.setColor( stdBlack );
			}
			
			//draw border for all pieces
			cG.setStroke(new BasicStroke(3));
			cG.drawOval(drawArea.x, drawArea.y, drawArea.width, drawArea.height);
			
			//draw inner circle for king peices.
			if (currentPiece instanceof CheckedGamePieceKing)
			{
				drawArea.grow(-8, -8);
				cG.drawOval(drawArea.x, drawArea.y, drawArea.width, drawArea.height);
			}		
			*/	
			//print piece numbers when debugging
			if (false)// (DEBUG)
			{
				Font tempFont = cG.getFont();
						
				cG.setColor( stdBlack );
				cG.setFont( new Font("Arial",Font.BOLD,24));
				cG.drawString(String.format("%d", peiceNum), drawArea.x+32, drawArea.y+48);
						
				cG.setFont(tempFont);//restore old font.				
			}
			
			
		}
		
		
	}
	
	public double ConvertCoordsPanel2BoardX(int panelCoordX)
	{
		//int leftMargin = (this.getWidth() - (int)(spaceSize * 8))/2;
		
		//return ((double)(panelCoordX-leftMargin)/spaceSize);
		
		//first unscale the input coord
		int newX = unscaleAndOffsetX(panelCoordX);
		//then move it to the origin of the board
		newX = newX - boardCoords.x;
		
		return (double)newX/ (boardCoords.width / 8);
	}
	
	public double ConvertCoordsPanel2BoardY(int panelCoordY)
	{
		int newY = unscaleAndOffsetY(panelCoordY);
		
		newY = newY - boardCoords.y;
		
		return (double)newY/ (boardCoords.height / 8);
		
		//return (double)panelCoordY/spaceSize;
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
	
	public int scaleVal(double inValue)
	{
		return (int)(inValue*scaleFactor);
	}
	
	Rectangle boardSpaceCoords(double x, double y)
	{
		double sSize = boardCoords.width / 8;
		
		return new Rectangle((int) ((x*sSize)+boardCoords.x), (int) ((y*sSize)+boardCoords.y) , (int)sSize,(int)sSize);
	}
	
	
	
	public int scaleAndOffsetX(double inValue)
	{
		return (int)((inValue*scaleFactor)+offsetX);
	}
	
	public int scaleAndOffsetY(double inValue)
	{
		return (int)((inValue*scaleFactor)+offsetY);
	}
	
	public int unscaleAndOffsetX(double inValue)
	{
		return (int)((inValue-offsetX)/scaleFactor);
	}
	
	public int unscaleAndOffsetY(double inValue)
	{
		return (int)((inValue-offsetY)/scaleFactor);
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
								
				if (curButton.pointInside(unscaleAndOffsetX(mouseX), unscaleAndOffsetY(mouseY) ))
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
								
				if (curButton.pointInside(unscaleAndOffsetX(mouseX), unscaleAndOffsetY(mouseY)))
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
			if (!game.getPlayer(game.getGameBoard().getPlayerTurn()).isAI())
			{
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
					CheckedGamePiece curPiece = tempBoard.getPiece(game.getGameBoard().getSelectedPiece());
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
		}
		
		if (overClickable )
		{
			setCursor(cursors.get(0));
		}
		else
			setCursor(cursors.get(1));
				
	}
	
	
	
	public void MouseClick(int mouseX, int mouseY)  
	{
		//this is for mouse clicks...currently only used for button clicking.  Piece dragging is in mousepress
		
		//currently doesn't care which mouse button;
		
		if (gameScreen == CheckedGameScreen.MAIN)
		{
			//go through each button and find out if it was clicked on.
			int numButtons = mainScreen.numButtons();
			for (int i = 0;i<numButtons;i++)
			{
				CheckedGameButton curButton = mainScreen.getButton(i);
				if (curButton.pointInside(unscaleAndOffsetX(mouseX), unscaleAndOffsetY(mouseY)))
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
				if (curButton.pointInside(unscaleAndOffsetX(mouseX), unscaleAndOffsetY(mouseY)))
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
			game.GameMousePress( ConvertCoordsPanel2BoardX(mouseX), ConvertCoordsPanel2BoardY(mouseY) );
		}
	}
	
	public void MouseDragged(int mouseX, int mouseY)
	{	
		if(gameScreen==CheckedGameScreen.GAME)
		{
			if(game.GetGameState() == CheckedGameStates.MOVING)
			{			
				game.UpdateMovingPiecePos( ConvertCoordsPanel2BoardX(mouseX), ConvertCoordsPanel2BoardY(mouseY) );
			}
		}			
	}
	
	public void MouseRelease(int mouseX,int mouseY)
	{
		if (gameScreen == CheckedGameScreen.GAME)
		{
			int result = game.GameMouseRelease( ConvertCoordsPanel2BoardX(mouseX), ConvertCoordsPanel2BoardY(mouseY));
			
			/*if (result != CheckedGameAction.NOTHING)  //mostly here to check for end game.
			{
				performAction(result);
			}*/
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
		//this is for the timer.  see if better way to handle this.
		//seems odd to call it actionPerformed.
		
		//update screenSize
		currentBoardDims = this.getSize();
		scaleFactor = currentBoardDims.getWidth() / defaultBoardDims.getWidth() < currentBoardDims.getHeight() / defaultBoardDims.getHeight() ? currentBoardDims.getWidth() / defaultBoardDims.getWidth() : currentBoardDims.getHeight() / defaultBoardDims.getHeight();
		offsetX = (int) ( currentBoardDims.getWidth()-(defaultBoardDims.getWidth()*scaleFactor) )/2;
		offsetY = (int) ( currentBoardDims.getHeight()-(defaultBoardDims.getHeight()*scaleFactor) )/2;
		
		if (gameScreen == CheckedGameScreen.GAME)
		{
			int result = game.UpdateGame();
			
			/*if (game.getGameBoard().gameOver())
			{
				performAction(CheckedGameAction.ENDGAME);					
				//resultAction = CheckedGameAction.ENDGAME;
			}*/
			if (result == CheckedGameAction.ENDGAME)
			{
				performAction(CheckedGameAction.ENDGAME);
			}
		
			//seems weird to check this every frame.
			
		}
		
		this.repaint();
		
		
	}
	
	
	
}
