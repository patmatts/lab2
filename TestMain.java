
public class TestMain {
	//For testing the heuristic only.
	public static void main(String[] args)
	{
		
		int boardSize = 11;
		
		char[][] board = new char[boardSize][boardSize];
		double[][] boardValues = new double[boardSize][boardSize];
		double total;
		
		for(int row = 0; row < boardSize; row++)
		{
			for(int col = 0; col < boardSize; col++)
			{
				board[row][col] = ' ';
			}
		}
		
		board[1][1] = 'x';
		board[1][2] = 'o';
		board[1][3] = 'o';
		board[1][4] = 'o';
		board[1][5] = 'o';
		
		board[4][4] = 'x';
		board[4][5] = 'x';
		board[4][6] = 'x';
		
		BoardHeuristic heuristic = new BoardHeuristic('x', boardSize, board);
		heuristic.createBoardValues();
		boardValues = heuristic.getBoardValues();
		
		//Prints the board.
		System.out.println("The Board:");
		for(int row = 0; row < boardSize; row++)
		{
			for(int col = 0; col < boardSize; col++)
			{
				System.out.print("[" + board[row][col]+ "]");
			}
			System.out.println();
		}
		
		//Prints the board values.
		System.out.println("The Board Values:");
		for(int row = 0; row < boardSize; row++)
		{
			for(int col = 0; col < boardSize; col++)
			{
				System.out.print("[" + boardValues[row][col] + "]");
			}
			System.out.println();
		}
		
		//Gets total.
		total = heuristic.boardValueTotal(boardValues);
		
		System.out.println("Total value of the board: " + total);
		
	}
	
}
