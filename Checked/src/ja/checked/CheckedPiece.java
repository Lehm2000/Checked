/**
 * CheckedGamePiece
 * Base Class for the game pieces.  Specific game pieces are extended from this one.
 */


package ja.checked;

import java.util.ArrayList;



public abstract class CheckedPiece 
{
	private int owner;  					//which player owns this piece 0 or 1;
	private double x;						//what horizontal space located 0-7. should always be whole number but was easier to make it double for calculating stuff
	private double y;						//what vertical space located 0-7
	private double curX;					//current position while dragging or animating...not always whole number.
	private double curY;					//current position while dragging or animating
	
	private boolean highlighted;			//is piece highlighted
	private boolean moving;
	final static private double radius=0.4;	//what size is the game piece in board coords.  one square is 1 unit.
	
	ArrayList<CheckedMove> moveList = new ArrayList<CheckedMove>();  //list of moves the piece can make
	
	public CheckedPiece()
	{
		this.owner = 0;
		this.x = 0;  //default to first space
		this.y = 0;
		this.curX = x;
		this.curY = x;
		this.highlighted = false;		
	}
	
	public CheckedPiece(int owner,int x, int y)
	{
		this.owner = owner;
		this.x = x;
		this.y = y;
		this.curX = x;  //need to set these so it animates correctly for ai moves...otherwise it moves from 0,0
		this.curY = y;
		this.highlighted = false;		
	}
	
	// Generated hashCode and equals.
	//==============================================================
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + owner;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		CheckedPiece other = (CheckedPiece) obj;
		if (owner != other.owner)
			return false;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}
	
	//==============================================================

	public void GetOwner(int owner)
	{
		this.owner = owner;
	}
	
	public int GetOwner()
	{
		return owner;
	}
	
	public double GetX()  //returns the decimal position of the piece
	{
		return x;
	}
	
	public double GetY()	//returns the decimal position of the piece
	{
		return y;
	}
	
	public int GetSpaceX()  //returns the integer space number
	{
		return (int) Math.round(x);
	}
	
	public int GetSpaceY()	//returns the integer space number
	{
		return (int) Math.round(y);
	}
	
	public void SetPos(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	public double GetRadius()
	{
		return radius;
	}
	
	public boolean GetHighlighted()
	{
		return highlighted;
	}
	
	public void SetHighlighted(boolean highlighted)
	{
		this.highlighted = highlighted;
	}
	
	public boolean isMoving()
	{
		return moving;
	}
	
	public void SetMoving(boolean moving)
	{
		this.moving = moving;
	}
	
	public boolean PointInside(double x,double y)
	{
		//since all the peices in checkers are round we can use one function to detect if a point is inside.
		
		boolean inside = false;
		
		double mouseDist = Math.pow( Math.pow( (double) x - ((double)this.x +.5),2) + Math.pow( (double) y - ((double)this.y +.5),2),0.5);
		if ( mouseDist <=radius )
		{
			inside = true;
		}
		
		return inside;
	}
	
	public double getCurX() {
		return curX;
	}

	public void setCurX(double curX) {
		this.curX = curX;
	}

	public double getCurY() {
		return curY;
	}

	public void setCurY(double curY) {
		this.curY = curY;
	}
	
	public void setCurPos(double curX, double curY)
	{
		this.curX = curX;
		this.curY = curY;
	}
	
	//gets possible moves this piece can make.
	public ArrayList<CheckedMove> GetMoves()
	{
		return moveList;
	}
}
