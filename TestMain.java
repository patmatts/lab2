// GomokuPlayer.java: Testing class, not vital to actual running of the program

// CS455 Lab #:2
// Name: Patrick Matts, Levi Sinclair, Austen Herrick
// Date: 2/13/17

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


public class TestMain {
	
	public static void main(String[] args) throws IOException, UnknownHostException
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
		
		//player.testPlayer();
	}
	
}

/*
 * Testing function for use in Gomoku Client
 * Put here just for referencing and completeness' sake
 *

//purely for testing purposes
	public void testPlayer() throws IOException, UnknownHostException
	{
		
		currentBoard[3][1] = ' ';
		currentBoard[3][2] = ' ';
		currentBoard[5][3] = ' ';
		currentBoard[6][5] = ' ';
		currentBoard[6][4] = ' ';
		
		currentBoard[1][1] = ' ';
		currentBoard[6][3] = 'o';
		currentBoard[8][3] = 'o';
		currentBoard[7][3] = 'o';
		currentBoard[9][3] = ' ';
		
		currentBoard[4][4] = ' ';
		currentBoard[5][4] = ' ';
		currentBoard[4][7] = ' ';
		
		List<Move> moves = new ArrayList<Move>();
		
		printBoard(currentBoard);
		
		
		//getMoves(moves, currentBoard);
		
		
		long startTime2 = System.nanoTime();
		startTime = System.nanoTime();
		//Move move = iterativeDeepening();
		Move move = startSearch(4);
		System.out.println(move.getX() + " " + move.getY());
		long endTime = System.nanoTime();
		
		long duration = (endTime - startTime2);  //divide by 1000000 to get milliseconds.
		System.out.println("Search took " + duration / 1000000000.0 + " seconds.");
		
		System.out.println("Test time took " + time / 1000000000.0 + " seconds.");
		
		//System.out.println(checkTerminal(currentBoard));
		
		//System.out.println(simpleHeuristic(currentBoard));
		//System.out.println(otherHeuristic(currentBoard));
		//System.out.println(lineHeuristic(currentBoard));
		
	}

*/