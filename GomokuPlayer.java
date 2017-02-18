// GomokuPlayer.java: implements the actual decision making to play a Gomoku game

// CS455 Lab #:2
// Name: Patrick Matts, Levi Sinclair, Austen Herrick
// Date: 2/7/17


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class GomokuPlayer 
{
	//general use class variables
	private char player;
	private char enemy;
	private int boardSize;
	private char[][] currentBoard;
	HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
	
	//class variables to keep track of current bestMove and time
	//for iterative deepening
	private long startTime;
	private Move bestMove;
	private boolean madeMove;
	
	//constant values
	private final int INFINITY = 10000000;
	private final int NEG_INFINITY = -10000000;
	private final int MAX_DEPTH = 10;
	private final int WIN = 5;
	private final double MAX_TIME = 1.75;
	
	//different directions in terms of 
	Move[] DIRECTIONS = {
			new Move(1, 1), // top left to bottom right
			new Move(0, 1), //top to bottom
			new Move(-1, 1), //top right to bottom left
			new Move(1, 0), //left to right
			new Move(0, -1), //bottom to top
			new Move(-1, 0), // right to left
			new Move(-1, -1), //bottom right to top left
			new Move(1, -1) //bottom left to top right
			};
	
	//in and out protocol
	private DataOutputStream dOut;
	private BufferedReader in;
	
	//testing constructor for when program doesn't need to send output to a server
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
	
	//general constructor for initalizing the GomokuPlayer
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
	
	
	//called from client, contains main player loop
	public String gomokuMain(char turn) throws IOException, UnknownHostException
	{
		String status = "continuing";
		
		System.out.println("player: " + player);
		
		
		// main loop for the program, keeps going as long as the game is not over
		do
		{
			startTime = System.nanoTime();
			
			//starts search, returns best move
			Move chosenMove = iterativeDeepening();
			//sends move to server
			if(madeMove == false)
			{
				makeMove(chosenMove);
				madeMove = true;
			}
			
			//reads server status then prints
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
	
	//implements iterative deepening to stay under imposed time limit and not have useless moves
	private Move iterativeDeepening() throws IOException, UnknownHostException
	{
		//assigns start time to global variable
		int currentDepth = 1;
		madeMove = false;
		
		//does a preliminary search to get at least a 1 depth move
		bestMove = startSearch(currentDepth);
		Move newChosenMove = new Move(-1, -1);
		hashMap.clear(); //clears history or else AI mixes up data
		
		while((System.nanoTime() - startTime) / 1000000000.0 <= MAX_TIME && currentDepth <= MAX_DEPTH)
		{
			currentDepth++;
			bestMove = newChosenMove;
			newChosenMove = startSearch(currentDepth);
			hashMap.clear();
		}
		
		//dont decrement depth because real depth is defined slightly differently
		System.out.println("Finished depth: " + currentDepth + " Move-depth: " + (double)currentDepth / 2);
		//returns best move from highest finished search depth
		return bestMove;
	}
	
	//initalizes search and returns chosen move
	private Move startSearch(int maxDepth) throws IOException, UnknownHostException
	{
		int alpha = NEG_INFINITY;
		int beta = INFINITY;
		//variables to start search depth is zero at this point
		int max = -100000000;
		Move maxMove;
		// make and get current available moves
		List<Move> moves = new ArrayList<Move>();
		
		//find moves for the currentBoard
		getMoves(moves, currentBoard);
		//sort moves by heuristic value in order to help AB pruning
		moveHSort(moves, copyBoard(currentBoard), player);
		
		maxMove = moves.get(0);
		
		//finds all branches off of root and recursively searches each one
		for(int i = 1; i < moves.size(); i++)
		{
			
			Move curMove = moves.get(i);
			
			char[][] newBoard = copyBoard(currentBoard);
			newBoard[curMove.getX()][curMove.getY()] = player;
			moves.remove(curMove);
			
			int treeMax = search(newBoard, maxDepth, other(player), new ArrayList<Move>(moves), alpha, beta); 
			
			moves.add(i, curMove);
			
			if(treeMax > max)
			{
				max = treeMax;
				maxMove = moves.get(i);
				alpha = max(alpha, max);
			}
			
			if(beta <= alpha)
				break;
			
		    
		}
		
		
		return maxMove;
	}
	
	//recursive part of minimax search, contains alpha beta pruning
	// terminates at depth then evaluates board states and returns best state as a value
	private int search(char[][] board, int depth, char turn, List<Move> moves, int alpha, int beta) throws IOException, UnknownHostException
	{
		if((System.nanoTime() - startTime) / 1000000000.0 >= MAX_TIME)
		{
			//System.out.println((System.nanoTime() - startTime) / 1000000000.0 + " seconds.");
			if(madeMove == false)
			{
				makeMove(bestMove);
				madeMove = true;
			}
			return 0;
		}
		
		int value;
		depth--;
		int[] hashValue = new int[1];
		
		//long startTime = System.nanoTime();
		//passing by reference using an array(I shoulda done this program in C)
		if(hashLook(board, hashValue))
		{
			return hashValue[0];
		}
		
		//checks if this is a terminal state, if so it returns the value of the state(theoretically infinity or negative infinity)
		value = checkTerminal(board, depth);
		if(value != 0)
			return value;
		
		
		//initalizes value depending on if it is min or max, these values will mostly likely get overwritten
		if(turn == enemy)
			value = 100000;
		else
			value = -100000;
		
		//returns game state value if max depth is reached or there are no more moves
		if(depth <= 0 || moves.size() == 0)
		{
			value = lineHeuristic(board);
		}
		else
		{
			//moveHSort(moves, copyBoard(board), turn);
			
			for(int i = 0; i < moves.size(); i++)
			{
				Move curMove = moves.get(i);
				
				board[moves.get(i).getX()][moves.get(i).getY()] = turn;
				moves.remove(curMove);
				
				//depending on if node is enemy or player it acts as min or max
				//in this case dont need two functions
				if(turn == enemy)
				{
					value = min(value, search(board, depth, other(turn), new ArrayList<Move>(moves), alpha, beta));
					beta = min(beta, value);
				}
				
				else if (turn == player)
				{
					value = max(value, search(board, depth, other(turn), new ArrayList<Move>(moves), alpha, beta));
					alpha = max(alpha, value);
				}
				
				
				moves.add(i, curMove);
				board[moves.get(i).getX()][moves.get(i).getY()] = ' ';
				
				
				if(beta <= alpha)
					break;
				
			    
			}
		}
		
		storeHash(board, value);
		return value;
	}
	
	//sends move to the server
	private void makeMove(Move m)  throws IOException, UnknownHostException
	{
		dOut.writeBytes(m.getX() + " " + m.getY() + "\n");
		dOut.flush(); 
	}
	
	/*
	 * makes a random move, not useful anymore
	private Move randomMove(List<Move> moves)
	{
		int ranMove = (int)(Math.random() * moves.size());
		
		return moves.get(ranMove);
	}
	*/
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
	
	private void moveHSort(List<Move> moves, char[][] board, char turn)
	{
		
		for(int i = 0; i < moves.size(); i++)
		{
			board[moves.get(i).getY()][moves.get(i).getX()] = turn;
			moves.get(i).setValue(lineHeuristic(board));
			board[moves.get(i).getY()][moves.get(i).getX()] = ' ';
		}
		
		moveSort(moves, turn);
		
	}
	
	/*
	 * experimental move sort function to be used with minimax
	 * did not end up being faster so function was scrapped
	private void moveLSort(List<Move> moves, char[][] board, char turn)
	{
		int row, col, score;
		
		for(int i = 0; i < moves.size(); i++)
		{
			score = 0;
			row = moves.get(i).getX();
			col = moves.get(i).getY();
			
			if(row > 0 && col > 0 && row < boardSize - 1 && col < boardSize - 1)
				for(int j = 0; j < DIRECTIONS.length; j++)
				{
					if(board[row + DIRECTIONS[j].getX()][col + DIRECTIONS[j].getY()] == other(turn))
						score++;
				}
			
		}
		
		moveSort(moves, turn);
	}
	*/
	
	//does the actual sorting of the Move array list with the values of the moves
	private void moveSort(List<Move> moves, char turn)
	{
		Collections.sort(moves, new Comparator<Move>(){
		     public int compare(Move obj1, Move obj2){
		         if(obj1.getValue() == obj2.getValue())
		             return 0;
		         if(turn == player)
		        	 return obj1.getValue() > obj2.getValue() ? -1 : 1;
		         else
		        	 return obj1.getValue() < obj2.getValue() ? -1 : 1;
		     }
		});
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
	
	//prints the available moves in the supplied array list
	private void printMoves(List<Move> moves)
	{
		for(int i = 0; i < moves.size(); i++)
		{
			System.out.println(moves.get(i).getX() + " " + moves.get(i).getY());
		}
	}
	
	//prints the board when given a 2D array of chars
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
	
	//converts the 2D array for board into string to be used in the hashmap
	private String boardToString(char[][] board)
	{
		StringBuilder builder = new StringBuilder();
		
		for(int row = 0; row < boardSize; row++)
			for(int col = 0; col < boardSize; col++)
				builder.append(board[row][col]);
		
		return builder.toString();
				
	}
	
	//store board as a hash value;
	private void storeHash(char[][] board, int value)
	{
		//int boardHash = Arrays.deepHashCode(board);
		hashMap.put(boardToString(board), value);
	}
	
	private boolean hashLook(char[][] board, int[] hashValue)
	{
		//using integer class for null
		//int boardHash = Arrays.deepHashCode(board);
		Integer value = hashMap.get(boardToString(board));
		
		//if get doesn't return null set hashValue to value
		if(value != null)
		{
			hashValue[0] = value;
			return true;
		}
		
		
		return false;
	}
	
	//checks for a game winning state(not draws)
	private int checkTerminal(char[][] board, int depth)
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
				
				//if it counts up to 5 then return the infinite value
				if(count == 5 && player == true)
					return INFINITY - depth;
				else if(count == 5 && player == false)
					return NEG_INFINITY + depth;
				
				count = 0;
				
				//repeat for down
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
					return INFINITY - depth;
				else if(count == 5 && player == false)
					return NEG_INFINITY + depth;
				
				count = 0;
				
				//repeat for diagonal top left to bottom right
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
					return INFINITY - depth;
				else if(count == 5 && player == false)
					return NEG_INFINITY + depth;
				
				count = 0;
				
				//repeat for top right to bottom left
				j = col;
				for(int i = row; i < row + 5; i++)
				{
					
					if(i >= boardSize || j < 0 || board[i][j] == ' ')
						break;
					else if(player && board[i][j] == enemy)
						break;
					else if(!player && board[i][j] == this.player)
						break;
					
					count++;
					j--;
				}
				
				if(count == 5 && player == true)
					return INFINITY - depth;
				else if(count == 5 && player == false)
					return NEG_INFINITY + depth;
				
				count = 0;
				
			}
		}
		
		return 0;
		
	}
	
	//main game state evaluation heuristic
	private int lineHeuristic(char[][] board)
	{
		int score = 0;
		Move spot;
		
		//for every spot on the board
		for(int row = 0; row < boardSize; row++)
			for(int col = 0; col < boardSize; col++)
			{
				spot = new Move(col, row);
				
				//evaluate the 4 directions(dont need to do others cause they are covered by these
				//if you go to every space
				score += lineEval(board, spot, DIRECTIONS[0]);
				score += lineEval(board, spot, DIRECTIONS[1]);
				score += lineEval(board, spot, DIRECTIONS[2]);
				score += lineEval(board, spot, DIRECTIONS[3]);
			}
		
		return score;

	}
	
	// used by lineHeuristic(char) to evaluate lines in the four relevant directions
	private int lineEval(char[][] board, Move spot, Move direction)
	{
		int count = 0;
		char piece = board[spot.getY()][spot.getX()];
		char evalSpot;
		
		//check to see if fives spaces away is off the board
		//if it is return
		if(spot.getY() + direction.getY() * WIN - 1 >= boardSize || spot.getY() + direction.getY() * WIN - 1 < 0)
			return 0;
		else if(spot.getX() + direction.getX() * WIN - 1 >= boardSize || spot.getX() + direction.getX() * WIN - 1 < 0)
			return 0;
		
		for(int i = 0; i < WIN; i++)
		{
			evalSpot = board[spot.getY() + direction.getY() * i][spot.getX() + direction.getX() * i];
			
			if(evalSpot == ' ')
				continue;
			else if(piece == evalSpot)
				count++;
			else if(piece != evalSpot && piece == ' ')
			{
				piece = evalSpot;
				count++;
			}
			else
			{
				return 0;
			}
		}
		
		
		if(piece == player)
			if(count == 3)
				return (int)Math.pow(5, count) * 2;
			else
				return (int)Math.pow(5, count);
		else if(piece == enemy)
			if(count == 3)
				return -4 * (int)Math.pow(5, count);
			else
				return -2 * (int)Math.pow(5, count);
		else
			return 0;
	}
	
	//implements levi's Heuristic
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
	
	/*
	 * Old heuristic functions
	 * replaced by line heuristic
	
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
		
		else if(countE == 4 && countB == 1)
			score -= 10000;
		
		else if(countE == 5)
			score -= 1000000;
		
		else if(countP == 5)
			score += 1000000;
		
		return score;
	}
	*/
	
	
}
