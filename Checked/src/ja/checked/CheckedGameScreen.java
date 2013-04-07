package ja.checked;

import java.awt.Image;
import java.util.ArrayList;

public class CheckedGameScreen {
	
	public final static int MAIN = 0; 
	public final static int GAME = 1;
	public final static int END = 2;
	
	private ArrayList<CheckedGameButton> buttons = new ArrayList<CheckedGameButton>();  //arrayList best to use here?  re-evaluate later, works for now.
	
	public Image background; //background image. Public...for now.
	
	public CheckedGameScreen()
	{
		
	}
	
	public void addButton(CheckedGameButton inButton)
	{
		buttons.add(inButton);
	}
	
	public int numButtons()
	{
		return buttons.size();
	}
	
	public CheckedGameButton getButton(int index)
	{
		return buttons.get(index);
	}
}
