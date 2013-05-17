package ja.checked;

import java.awt.Dimension;
import java.util.Random;

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
		
		//setResizable(false);
		pack();
		
		
	}
	
	
	public static void main(String args[])
	{
		CheckedGameGUI gameWindow = new CheckedGameGUI();
		gameWindow.setVisible(true);
		
		
		//some stuff for testing
/*		int[] weightArray = {1,1,100,2};
		
		int arraySum = 0;
		//sum the array
		for (int i = 0; i<weightArray.length;i++)
		{
			arraySum += weightArray[i];
		}
		
		Random theRnd = new Random();
		
		for (int i = 0 ; i<10; i++)
		{
			double choosenVal = theRnd.nextDouble()*(double)arraySum;
			System.out.println(choosenVal);
			
			//find what index the selection belongs to
			int arrayTotal = 0;
			int j;
			for ( j = 0; j<weightArray.length && arrayTotal < choosenVal;j++)
			{
				arrayTotal+=weightArray[j];
			}
			System.out.println(j);
		}*/
		
		
		
	}

}
