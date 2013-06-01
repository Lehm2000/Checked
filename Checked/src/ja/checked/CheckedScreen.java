package ja.checked;

import java.awt.Image;
import java.util.ArrayList;

public class CheckedScreen {
	
	//constants for gamescreens.  these match up to the index of the array that contains the gamescreens.
	public final static int MAIN = 0;
	public final static int GAME = 1;
	public final static int END = 2;
	
	private ArrayList<CheckedButton> buttons = new ArrayList<CheckedButton>();  //arrayList best to use here?  re-evaluate later, works for now.
	
	public Image background; //background image. Public...for now.
	private int parentScreen;  //what screen did this screen come from. So we can go back if necessary.
	
	public CheckedScreen()
	{
		
	}
	
	public void addButton(CheckedButton inButton)
	{
		buttons.add(inButton);
	}
	
	public int numButtons()
	{
		return buttons.size();
	}
	
	public CheckedButton getButton(int index)
	{
		return buttons.get(index);
	}
}
