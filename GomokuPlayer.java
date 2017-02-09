import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;

public class GomokuPlayer {
	/* Whole game */
	private static int boardSize;
	private static char[][] board;
	private static int[][] currMove;
	private static int count = 0;
	private static String status = "continuing";
	/*secret weapon*/
	private final static int Sarr[] = {7, 0, 7, 1, 7, 4, 10, 3, 9, 3, 6, 3, 7, 3, 7, 4, 6, 3, 7, 3};
	private final static int JOINT  = 3; //Magic Number for secret weapon
	private static boolean cont = true;
	private static int sRow;
	private static int sCol = 0; //DEPENDS on size of board
	private static char player;
	//in and out protocol
	private DataOutputStream dOut;
	private BufferedReader in;
	
	public GomokuPlayer(char player, int size, char[][] board, BufferedReader in, DataOutputStream dOut)
	{
		this.player = player;
		this.boardSize  = size;
		this.board = new char[size][size];
		this.board = board;
		
		this.in = in;
		this.dOut = dOut;
	}
	
	public String gomokuMain(char turn) throws IOException, UnknownHostException
	{	
		sRow = boardSize - JOINT - 1;
		do {
		//Will accept heuristic return in future, Heuristic will return a false boolean if secret weapon is not optimal
		boolean heuristic = true;
		heuristic(); // will perform alpha beta pruning and set global variables based on best results
		System.out.println(status);
		if (heuristic)
			if (secretWeapon()) {
				if (board[7][2] == ' ')
					makeMove(7, 2);
				else
					makeMove(8, 3);
			}
		status = readServer();
		} while (status.equals("continuing"));
		/*do
		{
			boolean random = true;
			
			do
			{
				int row = 0 + (int)(Math.random() * (boardSize - 1));
				int col = 0 + (int)(Math.random() * (boardSize - 1));
				
				if(board[row][col] == ' ')
				{
					makeMove(row, col);
					random = false;
				}
				
			} while(random);
			
			status = readServer();
			System.out.println(status);
			
		} while(status.equals("continuing"));*/

		return status;
	}
	
	private void heuristic() {
		int i;
		int j;
		int xCount = 0;
		int oCount = 0;
		int xSize = boardSize * boardSize;
		int oSize = boardSize * boardSize;
		int [] xFound  = new int[xSize];
		int [] oFound = new int[xSize];
		System.out.println("xSize = " + xSize);
		System.out.println("oSize = " + oSize);
		for (i=0; i < boardSize; i++) {
			for (j=0; j < boardSize; j++) {
				if (board[i][j] == 'x') {
					System.out.println(i + " " + j + " " + "Found " + board[i][j]);
					if (xCount < (xSize / 2)) {
						System.out.println("Our array holds " + (xSize/2) + " x's");
						xFound[xCount] = i;
						xCount++;
						xFound[xCount] = j;
						xCount++;
						System.out.println(i + " " + j);
						System.out.println("This is the " + (xCount/2)  + " x we've encountered");
					}
				}
				else if (board[i][j] == 'o') {
					if (oCount < (xSize / 2)) {
						oFound[oCount] = i;
						oCount++;
						oFound[oCount] = j;
						oCount++;
						System.out.println(i + " " + j);
						System.out.println("This is the " + (oCount/2)  + " o we've encountered");
					}
				}
			}
		}
	}
	
	private boolean secretWeapon() {
		if (board[7][3] == player) {
			return true;//Reached goal
		}
		else {
			System.out.println("At board position " + sRow + " " + sCol);
			if (count < Sarr.length) {
				System.out.println("Lenght of array: " + Sarr.length + "vs. " + count);
			sRow = Sarr[count];
			count++;
			sCol = Sarr[count];
			count++;
			}
			try {
				makeMove(sRow, sCol);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
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
	    board[0] = line.toCharArray();
	    
	    for(int i = 1; i < boardSize; i++)
	    {
	    	line = in.readLine();
	    	//System.out.println(line);
	    	board[i] = line.toCharArray();
	    }
	    
	    line = in.readLine();
	    return status;
	}
	
	private void makeMove(int row, int col)  throws IOException, UnknownHostException
	{
		dOut.writeBytes(row + " " + col + "\n");
		dOut.flush(); 
	}
	
}
