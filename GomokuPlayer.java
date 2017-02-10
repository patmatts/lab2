import java.util.List;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class GomokuPlayer 
{

	private char player;
	private static char enemy;
	private static int boardSize;
	private char[][] currentBoard;
	
	private final int MAX_DEPTH = 3;
	
	//in and out protocol
	private DataOutputStream dOut;
	private BufferedReader in;
	
	//testing constructor
	public GomokuPlayer(char player, int size, char[][] board)
	{
		this.player = player;
		this.boardSize  = size;
		this.currentBoard = new char[size][size];
		this.currentBoard = board;
		
		if(player  == 'x')
			enemy = 'o';
		else
			enemy = 'x';
	}
	
	public GomokuPlayer(char player, int size, char[][] board, BufferedReader in, DataOutputStream dOut)
	{
		this.player = player;
		this.boardSize  = size;
		this.currentBoard = new char[size][size];
		this.currentBoard = board;
		
		if(player  == 'x')
			enemy = 'o';
		else
			enemy = 'x';
		
		this.in = in;
		this.dOut = dOut;
		
	}
	
	//purely for testing purposes
	public void testPlayer()
	{
		
		currentBoard[3][1] = ' ';
		currentBoard[1][2] = ' ';
		currentBoard[3][3] = ' ';
		currentBoard[1][4] = ' ';
		currentBoard[1][5] = ' ';
		
		currentBoard[1][1] = 'o';
		currentBoard[2][2] = 'o';
		currentBoard[3][3] = 'o';
		currentBoard[4][4] = ' ';
		currentBoard[5][5] = 'o';
		
		currentBoard[4][4] = 'o';
		currentBoard[4][5] = ' ';
		currentBoard[4][6] = ' ';
		currentBoard[4][7] = ' ';
		
		List<Move> moves = new ArrayList<Move>();
		
		printBoard(currentBoard);
		
		//getMoves(moves, currentBoard);
		
		//Move move = startSearch();
		//System.out.println(move.getX() + " " + move.getY());
		
		System.out.println(checkTerminal(currentBoard));
		
		//System.out.println(simpleHeuristic(currentBoard));
		//System.out.println(otherHeuristic(currentBoard));
		
	}
	
	//called from client, contains main player loop
	public String gomokuMain(char turn) throws IOException, UnknownHostException
	{
		String status = "continuing";
		
		System.out.println("player: " + player);
		
		
		// main loop for the program, keeps going as long as the game is not over
		do
		{
			
			//starts search, returns best move
			Move chosenMove = startSearch();
			
			//sends move to server
			makeMove(chosenMove);
			
			status = readServer();
			System.out.println(status);
			
		} while(status.equals("continuing")); //loop continues while server says to
		
		return status;
	}
	
	//reads in the board and status
	private String readServer() throws IOException, UnknownHostException
	{
		String line = in.readLine();
		String status = line;
		
		if(!line.equals("continuing"))
	    {
	    	return line;
	    }
		
	    line = in.readLine();
	    currentBoard[0] = line.toCharArray();
	    
	    for(int i = 1; i < boardSize; i++)
	    {
	    	line = in.readLine();
	    	currentBoard[i] = line.toCharArray();
	    }
	    
	    line = in.readLine();
	    return status;
	}
	
	//initalizes search and returns chosen move
	private Move startSearch()
	{
		//variables to start search depth is zero at this point
		int max = -1000000;
		Move maxMove;
		int depth = 0;
		
		// make and get current available moves
		List<Move> moves = new ArrayList<Move>();
		
		getMoves(moves, currentBoard);
		
		maxMove = moves.get(0);
		
		//finds all branches off of root and recursively searches each one
		for(int i = 1; i < moves.size(); i++)
		{
			Move curMove = moves.get(i);
			
			char[][] newBoard = copyBoard(currentBoard);
			newBoard[curMove.getX()][curMove.getY()] = player;
			moves.remove(curMove);
			
			int treeMax = search(newBoard, depth, other(player), moves, -10000000, 10000000);
			
			moves.add(i, curMove);
			
			if(treeMax > max)
			{
				max = treeMax;
				maxMove = moves.get(i);
			}
		    
		}
		
		return maxMove;
	}
	
	//recursive part of minimax search, contains alpha beta pruning
	// terminates at depth then evaluates board states and returns best state as a value
	private int search(char[][] board, int depth, char turn, List<Move> moves, int alpha, int beta)
	{
		int value;
		depth++;
		
		//checks if this is a terminal state, if so it returns the value of the state(theoretically infinity or negative infinity)
		value = checkTerminal(board);
		if(value != 0)
			return value;
		
		//initalizes value depending on if it is min or max, these values will mostly likely get overwritten
		if(turn == enemy)
			value = 100000000;
		else
			value = -100000000;
		
		// make and get current available moves
		
		
		if(depth >= MAX_DEPTH || moves.size() == 0)
			//return simpleHeuristic(board);
			return otherHeuristic(board);
		
		for(int i = 0; i < moves.size(); i++)
		{
			Move curMove = moves.get(i);
			
			char[][] newBoard = copyBoard(board);
			newBoard[moves.get(i).getX()][moves.get(i).getY()] = turn;
			moves.remove(curMove);
			
			int treeValue = search(newBoard, depth, other(turn), moves, alpha, beta);
			
			moves.add(i, curMove);
			
			
			if(treeValue < value && turn == enemy)
			{
				value = treeValue;
				beta = min(beta, value);
			}
			
			else if (treeValue > value && turn == player)
			{
				value = treeValue;
				alpha = max(alpha, value);
			}
			
			if(beta <= alpha)
				break;
			
		    
		}
		
		return value;
	}
	
	private void makeMove(Move m)  throws IOException, UnknownHostException
	{
		dOut.writeBytes(m.getX() + " " + m.getY() + "\n");
		dOut.flush(); 
	}
	
	private Move randomMove(List<Move> moves)
	{
		int ranMove = (int)(Math.random() * moves.size());
		
		return moves.get(ranMove);
	}
	
	private void getMoves(List<Move> moves, char[][] board)
	{
		Move move = null;
		
		for(int row = 0; row < boardSize; row++)
			for(int col = 0; col < boardSize; col++)
			{
				if(board[row][col] == ' ')
				{
					move = new Move(row, col);
					moves.add(move);
				}
				
			}
		
	}
	
	//returns minimum of 2 values
	public int min(int value1, int value2)
	{
		if(value1 > value2)
			return value2;
		else
			return value1;
	}
	
	//returns maximum of 2 values
	public int max(int value1, int value2)
	{
		if(value1 < value2)
			return value2;
		else
			return value1;
	}
	
	private char[][] copyBoard(char[][] oldBoard)
	{
		char[][] newBoard = new char[boardSize][boardSize];
		
		for(int row = 0; row < boardSize; row++)
			for(int col = 0; col < boardSize; col++)
			{
				newBoard[row][col] = oldBoard[row][col];
			}
		
		return newBoard;
	}
	
	private char other(char current)
	{
		if(current == 'x')
			return 'o';
		else
			return 'x';
	}
	
	private void printMoves(List<Move> moves)
	{
		for(int i = 0; i < moves.size(); i++)
		{
			System.out.println(moves.get(i).getX() + " " + moves.get(i).getY());
		}
	}
	
	private void printBoard(char[][] board)
	{
		for(int row = 0; row < boardSize; row++)
		{
			for(int col = 0; col < boardSize; col++)
			{
				System.out.print(board[row][col]);
			}
			System.out.println();
		}
	}
	
	private int checkTerminal(char[][] board)
	{
		int count = 0;
		boolean player;
		
		//count rows ahead for 5 in a row
		for(int row = 0; row < boardSize; row++)
		{
			for(int col = 0; col < boardSize; col++)
			{
				count = 0;
				
				//determine if spot is blank if so cant be terminal
				if(board[row][col] == ' ')
					continue;
				else if(board[row][col] == this.player)
					player = true;
				else
					player = false;
				
				//check row ahead
				for(int i = col; i < col + 5; i++)
				{
					if(i >= boardSize || board[row][i] == ' ')
						break;
					else if(player && board[row][i] == enemy)
						break;
					else if(!player && board[row][i] == this.player)
						break;
						
					count++;
				}
				
				if(count == 5 && player == true)
					return 10000000;
				else if(count == 5 && player == false)
					return -10000000;
				
				count = 0;
				
				for(int i = row; i < row + 5; i++)
				{
					if(i >= boardSize || board[i][col] == ' ')
						break;
					else if(player && board[i][col] == enemy)
						break;
					else if(!player && board[i][col] == this.player)
						break;
					
					count++;
				}
				
				if(count == 5 && player == true)
					return 10000000;
				else if(count == 5 && player == false)
					return -10000000;
				
				count = 0;
				
				
				int j = col;
				for(int i = row; i < row + 5; i++)
				{
					
					if(i >= boardSize || j >= boardSize || board[i][j] == ' ')
						break;
					else if(player && board[i][j] == enemy)
						break;
					else if(!player && board[i][j] == this.player)
						break;
					
					count++;
					j++;
				}
				
				if(count == 5 && player == true)
					return 10000000;
				else if(count == 5 && player == false)
					return -10000000;
				
				count = 0;
				
			}
		}
		
		return 0;
		
	}
	
	private int otherHeuristic(char[][] board)
	{
		double[][] boardValues = new double[boardSize][boardSize];
		double total;
		
		BoardHeuristic heuristic = new BoardHeuristic(player, boardSize, board);
		heuristic.createBoardValues();
		boardValues = heuristic.getBoardValues();
		total = heuristic.boardValueTotal(boardValues);
		
		int value = (int)(total * 100);
		
		return value;
	}
	
	//simple heuristic to analyze board for testing purposes
	private int simpleHeuristic(char[][] board)
	{
		int score = 0;
		
		
		for(int row = 0; row < boardSize; row++)
			for(int col = 0; col < boardSize; col++)
			{
				//using move just for general information passing
				Move spot = new Move(row, col);
				score += simpleHeuristicRow(board, spot);
			}
		return score;
	}
	
	//additional function to analyze
	private int simpleHeuristicRow(char[][] board, Move spot)
	{
		int score = 0;
		int countP = 0;
		int countE = 0;
		int countB = 0;
		
		//count rows behind for 
		for(int i = spot.getX(); i > spot.getX() - 5; i--)
		{
			if(i < 0)
				break;
			if(board[spot.getY()][i] == player)
				countP++;
			else if(board[spot.getY()][i] == enemy)
				countE++;
			else
				countB++;
		}
		
		if(countP == 4 && countB == 1)
			score += 5000;
		
		else if(countE == 4 && countB == 1)
			score -= 10000;
		
		/*countP = 0;
		countE = 0;
		countB = 0;
		
		for(int i = spot.getX(); i < spot.getX() + 5; i++)
		{
			if(i >= boardSize)
				break;
			if(board[spot.getY()][i] == player)
				countP++;
			else if(board[spot.getY()][i] == enemy)
				countE++;
			else
				countB++;
		}
		
		if(countP == 4 && countB == 1)
			score += 5000;
		
		else if(countE == 4 && countB == 1)
			score -= 10000;
		
		else if(countE == 5)
			score -= 1000000;
		
		else if(countP == 5)
			score += 1000000;*/
		
		return score;
	}
	
	
}
