
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class GomokuClient {
	
	private final static int GOMOKUPORT = 17033;
	private final static String HOST = "localhost";
	
	private static DataOutputStream dOut = null;
	private static DataInputStream dIn = null;
	private static BufferedReader in = null;
	private static Scanner reader;
	
	private static int boardSize;
	private static char[][] board;
	private static char turn;
    
    private static Socket gridSocket;
	
	public static void main(String[] args)
	{
		//holds agent status for finishing condition
		reader = new Scanner(System.in);
		turn = 'n';
		
		try
		{
			if(socketConnect(HOST, GOMOKUPORT) != 0)
			{
				System.out.println("Unable to acquire agent number from host");
				return;
			}
			
			System.out.println("Board Size: " + board.length);
			System.out.println("Turn: "  + turn);
			
			GomokuPlayer player = new GomokuPlayer(turn, boardSize, board, in, dOut);
			player.gomokuMain(turn);
			
			//ReflexAgent refAgent = new ReflexAgent(in, dOut, agentNums[0]);
			
			//agentStatus = refAgent.reflexMain();
			
			
			//closes in and out protocol and socket
			dIn.close();
			dOut.close();
			gridSocket.close();
		}
		//exception handling
		catch(UnknownHostException e) {
			System.out.println("Unknown Host Exception from MaedenClient");
			return;
		} 
		catch(IOException e) {
			System.out.println("IOException from MaedenClient");
			return;
		}
		
	}
	
	//connects to socket and receives agent number and sends request to be a normal agent
	public static int socketConnect(String host, int port)
	{
		try
		{
			System.out.print("Connecting to: " + host + " Port number: " + port + "\n");
		    gridSocket = new Socket(host, port);
		    System.out.println("Socket creation successful");
		    
		    dOut = new DataOutputStream(gridSocket.getOutputStream());
		    
		    dIn = new DataInputStream(gridSocket.getInputStream());
		    
		    in = new BufferedReader(new InputStreamReader(dIn));
		    
		    if(!in.readLine().equals("continuing"))
		    {
		    	System.out.println("Connection failed, exiting program.");
		    	return -1;
		    }
		    
		    String line = in.readLine();
		    
		    boardSize = line.length();
		    System.out.println(line);
		    
		    board = new char[boardSize][boardSize];
		    
		    board[0] = line.toCharArray();
		    
		    for(int i = 1; i < boardSize; i++)
		    {
		    	line = in.readLine();
		    	System.out.println(line);
		    	board[i] = line.toCharArray();
		    }
		    
		    turn = in.readLine().charAt(0);
			
		    
			return 0;
		}
		catch(UnknownHostException e) {
			System.out.println("Unknown Host Exception.");
			return -1;
		} 
		catch(IOException e) {
			System.out.println("IOException ");
			return -1;
		}
	}

}
