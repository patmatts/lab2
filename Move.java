
public class Move {

	private int x;
	private int y;
	
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
}
