package ja.checked;

import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;

public class CheckedButton 
{	
	private int width;
	private int height;
	private Point position;
	private String caption;
	private Font font;
	private int action;  //set to a member of CheckedGameAction
	
	CheckedButton()
	{
		width = 100;
		height = 20;
		position.x = 0;
		position.y = 0;
		caption = "nothing";
		font = new Font("Arial",Font.PLAIN,12);
		action = CheckedAction.NOTHING;
	}
	
	CheckedButton(int width, int height, Point position, String caption, Font font,int action)
	{
		this.width = width;
		this.height = height;
		this.position = position;
		this.caption = caption;
		this.font = font;
		this.action = action;
	}
	
	boolean pointInside(int mouseX, int mouseY)
	{
		
		Rectangle tempRect= new Rectangle(position.x-(width/2), position.y-(height/2), width, height);
		
		return tempRect.contains(mouseX, mouseY);
	}
	
	//generated getters and setters.
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}
	
	

}
