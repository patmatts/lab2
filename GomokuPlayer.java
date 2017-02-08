import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;

public class GomokuPlayer {

	private final static int JOINT  = 3; //Magic Number for secret weapon
	private static boolean cont = true;
	private static boolean phase1 = true;
	private static boolean phase2 = false;
	private static boolean phase3 = false;
	private static int secretX = 0;
	private static int secretY; //DEPENDS on size of board
	private static char player;
	private static int boardSize;
	private static char[][] board;
	private static int[][] currMove;
	
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
		secretY = boardSize - JOINT - 1;
		String status = "continuing";
		int i;
		int j;
		int xCount = 0;
		int oCount = 0;
		int xSize = boardSize * boardSize;
		int oSize = boardSize * boardSize;
		System.out.println("xSize = " + xSize);
		System.out.println("oSize = " + oSize);
		int [] xFound;
		int [] oFound;
		xFound = new int[xSize];
		oFound = new int[xSize];
		do {
			for (i=0; i < boardSize; i++) {
				for (j=0; j < boardSize; j++) {
					if (board[i][j] == 'x') {
						System.out.println(i + " " + j + " " + "Found " + board[i][j]);
						if (xCount < (xSize / 2)) {
							xFound[xCount] = i;
							xCount++;
							xFound[xCount] = j;
							xCount++;
							System.out.println(i + " " + j);
						}
					}
					else if (board[i][j] == 'o') {
						System.out.println(i + " " + j + " " + "Found " + board[i][j]);
						if (oCount < (xSize / 2)) {
							oFound[oCount] = i;
							oCount++;
							oFound[oCount] = j;
							oCount++;
							System.out.println(i + " " + j);
						}
					}
				}
			}
			if (cont) {
				System.out.println("Secret Mode");
				if (phase1 || phase2) {
					System.out.println("Beginning phases Secret Mode");
					makeMove(secretY, secretX);
				}
				if (phase2) {
					secretY--;
				}
				if (secretX == JOINT -1) {
					phase1 = false;
					phase2 = true;
					secretX = JOINT;
					secretY = boardSize - 1;
				}
				if (secretY == JOINT -1) {
					phase2 = false;
					secretY = JOINT;
				}
				if (phase1) {
					secretX++;
				}
				if (!phase1 && !phase2) {
					System.out.println("Completing Secret mode");
					makeMove(secretY, secretX);
					phase3 = true;
				}
				if (phase3) {
					System.out.println("Winning in Secret mode");
					if (board[secretY + 1][secretX] == ' ') {
						makeMove(secretX + 1, secretY);
					}
					else if (board[secretY][secretX + 1] == ' ') {
						makeMove(secretY, secretX + 1);
					}
					else {
						System.out.println("Shouldn't have made it thus far!!!");
					}
				}
			}
			status = readServer();
			System.out.println(status);
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
