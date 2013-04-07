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
		
		
		
	}

}
