// Move.java: Stores values for a specific move on the board

// CS455 Lab #:2
// Name: Patrick Matts, Levi Sinclair, Austen Herrick
// Date: 2/13/17

public class Move {

	private int x;
	private int y;
	private int value;
	
	public Move()
	{
		this.x = -1;
		this.y = -1;
	}
	
	public Move(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public void setX(int x)
	{
		this.x = x;
	}
	
	public void setY(int y)
	{
		this.y = y;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getValue()
	{
		return value;
	}
	
	public void setValue(int val)
	{
		value = val;
	}
}
