import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;

public class GomokuPlayer {

	private static char player;
	private static int boardSize;
	private static char[][] board;
	
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
		String status = "continuing";
		
		//if(turn != player)
			//return "error";
		
		do
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
