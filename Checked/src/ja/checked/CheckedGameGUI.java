package ja.checked;

import java.awt.Dimension;
import javax.swing.JFrame;

public class CheckedGameGUI extends JFrame
{
	//private int spaceSize = 64;  //not currently used...but may be needed again.
		
	private static final long serialVersionUID = 1L;


	public CheckedGameGUI()
	{
		setTitle("Checked");		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final CheckedGameBoardPanel boardSurface = new CheckedGameBoardPanel();
		
		//Dimension boardSize = new Dimension(12*spaceSize,8*spaceSize);
		Dimension boardSize = new Dimension(1280,720);
		boardSurface.setPreferredSize(boardSize);
				
		add(boardSurface);
		
		setResizable(false);
		pack();
		
		
	}
	
	
	public static void main(String args[])
	{
		CheckedGameGUI gameWindow = new CheckedGameGUI();
		gameWindow.setVisible(true);
		
		/*CheckedGameBoard board1 = new CheckedGameBoard();
		board1.reset();
		CheckedGameBoard board2 = new CheckedGameBoard();
		board2.reset();
		CheckedGamePiece curPiece = board1.getPiece(12);
		System.out.println("board1 hashCode: "+ board1.hashCode());
		System.out.println("board2 hashCode: "+ board2.hashCode());
		
		curPiece.SetPos(800, 78);
		
		board2 = new CheckedGameBoard(board1);
				
		System.out.println(board1.equals(board2));
		System.out.println("board1 hashCode: "+ board1.hashCode());
		System.out.println("board2 hashCode: "+ board2.hashCode());*/
		
		
		
	}

}
