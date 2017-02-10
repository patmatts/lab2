import java.util.List;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class GomokuPlayer 
{

	private static char player;
	private static char enemy;
	private static int boardSize;
	private char[][] currentBoard;
	
	private final int MAX_DEPTH = 2;
	
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
		
		currentBoard[1][1] = 'x';
		currentBoard[1][2] = 'o';
		currentBoard[1][3] = 'o';
		currentBoard[1][4] = 'o';
		currentBoard[1][5] = 'o';
		
		currentBoard[4][4] = 'x';
		currentBoard[4][5] = 'x';
		currentBoard[4][6] = 'x';
		
		List<Move> moves = new ArrayList<Move>();
		
		printBoard(currentBoard);
		
		getMoves(moves, currentBoard);
		
		Move move = startSearch();
		System.out.println(move.getX() + " " + move.getY());
		
		//System.out.println(simpleHeuristic(currentBoard));
		
	}
	
	public String gomokuMain(char turn) throws IOException, UnknownHostException
	{
		String status = "continuing";
		
		//if(turn != player)
			//return "error";
		
		do
		{
			//List<Move> moves = new ArrayList<Move>();
			
			//getMoves(moves, currentBoard);
			
			Move chosenMove = startSearch();
			
			makeMove(chosenMove);
			
			status = readServer();
			System.out.println(status);
			
		} while(status.equals("continuing"));
		
		return status;
	}
	
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
	    	//System.out.println(line);
	    	currentBoard[i] = line.toCharArray();
	    }
	    
	    line = in.readLine();
	    return status;
	}
	
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
		
		for(int i = 1; i < moves.size(); i++)
		{
			char[][] newBoard = copyBoard(currentBoard);
			newBoard[moves.get(i).getX()][moves.get(i).getY()] = player;
			
			int treeMax = search(newBoard, depth, other(player));
			
			if(treeMax > max)
			{
				max = treeMax;
				maxMove = moves.get(i);
			}
		    
		}
		
		return maxMove;
	}
	
	private int search(char[][] board, int depth, char turn)
	{
		int min = 1000000;
		depth++;
		
		// make and get current available moves
		List<Move> moves = new ArrayList<Move>();
				
		getMoves(moves, board);
		
		if(depth > MAX_DEPTH || moves.size() == 0)
			return simpleHeuristic(board);
		
		for(int i = 0; i < moves.size(); i++)
		{
			char[][] newBoard = copyBoard(board);
			newBoard[moves.get(i).getX()][moves.get(i).getY()] = turn;
			
			int treeMin = search(newBoard, depth, other(turn));
			
			if(treeMin < min)
			{
				min = treeMin;
			}
		    
		}
		
		return min;
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
		
		countP = 0;
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
		
		else if(countP == 4 && countB == 1)
			score -= 10000;
		
		return score;
	}
	
	
}
