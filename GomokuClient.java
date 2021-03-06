// GomokuClient.java: makes initial contect with the Gomoku server
// to start a game

// CS455 Lab #:2
// Name: Patrick Matts, Levi Sinclair, Austen Herrick
// Date: 2/7/17

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class GomokuClient {
	
	private final static int GOMOKUPORT = 17033;
	private final static String HOST = "localhost";
	
	private static DataOutputStream dOut = null;
	private static DataInputStream dIn = null;
	private static BufferedReader in = null;
	
	private static int boardSize;
	private static char[][] board;
	private static char turn;
    
    private static Socket gridSocket;
	
	public static void main(String[] args)
	{
		//holds agent status for finishing condition
		turn = 'n';
		
		try
		{
			if(socketConnect(HOST, GOMOKUPORT) != 0)
			{
				System.out.println("Unable to acquire information from host");
				return;
			}
			
			System.out.println("Board Size: " + board.length);
			
			GomokuPlayer player = new GomokuPlayer(turn, boardSize, board, in, dOut);
			player.gomokuMain(turn);
			
			//closes in and out protocol and socket
			dIn.close();
			dOut.close();
			gridSocket.close();
		}
		//exception handling
		catch(UnknownHostException e) {
			System.out.println("Unknown Host Exception from GomokuClient");
			return;
		} 
		catch(IOException e) {
			System.out.println("IOException from GomokuClient");
			return;
		}
		
	}
	
	//connects to socket and receives agent number and sends request to join game
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
