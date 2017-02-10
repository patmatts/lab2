
public class TestMain {
	
	public static void main(String[] args)
	{
		
		int boardSize = 11;
		
		char[][] board = new char[boardSize][boardSize];
		
		for(int row = 0; row < boardSize; row++)
		{
			for(int col = 0; col < boardSize; col++)
			{
				board[row][col] = ' ';
			}
		}
		
		GomokuPlayer player = new GomokuPlayer('x', boardSize, board);
		
		player.testPlayer();
	}
	
}
