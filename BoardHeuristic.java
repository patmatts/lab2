public class BoardHeuristic
{
	private final String UL = "UL";
	private final String U = "U";
	private final String UR = "UR";
	private final String R = "R";
	private final String DR = "DR";
	private final String D = "D";
	private final String DL = "DL";
	private final String L = "L";
	private final String[] DIRECTIONS = {UL, U, UR, R, DR, D, DL, L};
	
	//Controls the numbers.
	private final double ENEMY_VALUE = -.02; //How much the value increases by having an enemy token next to space.
	private final double PLAYER_VALUE = .02; //How much the value increases by having a player token next to space.
	private final double NEUTRAL_VALUE = .5; //Base value of the space.
	private final double ENEMY_MULT = 2; //Just in case.
	private final double PLAYER_MULT = 2; //Just in case.
	
	private char player;
	private int boardSize;
	private char[][] currentBoard;
	private double[][] boardValues;
	private int playerCount;
	private int enemyCount;
	private double totalValue;
	
	//Constructor.
	public BoardHeuristic(char player, int size, char[][] currentBoard)
	{
		this.player = player;
		this.boardSize = size;
		this.currentBoard = new char[size][size];
		this.currentBoard = currentBoard;
		boardValues = new double[size][size];
		totalValue = 0;
	}
	
	public double checkAllDirections(int row, int column)//Needs the location as an arg.
	{
		//Counts for all in DIRECTIONS until it runs into the opposite player or whitespace and comes up with a value.
		double value = NEUTRAL_VALUE;
		for(String direction : DIRECTIONS)
		{
			playerCount = 0;
			enemyCount = 0;
			char current = ' ';
			char savedChar = ' ';
			int count = 0;
			while(count < 4)
			{
				try
				{
					switch(direction){
					case "UL": //Up Left
						current = currentBoard[row-count-1][column-count-1];
						break;
					case "U": //Up
						current = currentBoard[row-count-1][column];
						break;
					case "UR": //Up Right
						current = currentBoard[row-count-1][column+count+1];
						break;
					case "R": //Right
						current = currentBoard[row][column+count+1];
						break;
					case "DR": //Down Right
						current = currentBoard[row+count+1][column+count+1];
						break;
					case "D": //Down
						current = currentBoard[row+count+1][column];
						break;
					case "DL": //Down Left
						current = currentBoard[row+count+1][column-count-1];
						break;
					case "L": //Left
						current = currentBoard[row][column-count-1];
						break;
					default:
						break;
					}
				} catch (ArrayIndexOutOfBoundsException e){ count = 4;} //Exit the direction loop if you try to go out of bounds.
				
				if(count == 0)
					savedChar = current; //Save the first char you run into, so you can count the chars in a row.
				
				switch(current)
				{
				case ' ':
					count = 4; //End loop if you run into an empty space.
					break;
				case 'x':
					if(savedChar != current) //End loop if you run into the opposite piece.
						count = 4;
					else
					{
						if(player == current)
							playerCount++;
						else
							enemyCount++;
					}
					break;
				case 'o':
					if(savedChar != current) //End loop if you run into the opposite piece.
						count = 4;
					else
					{
						if(player == current)
							playerCount++;
						else
							enemyCount++;
					}
					break;
				default:
					break;
				}
				count++;
			}
			//Where the value of that space is calculated.
			//May want to add multiplier.
			//May want to change counter to be one less 
			//so that it doesn't start counting until there are 2 pieces next to eachother.
			value += ((ENEMY_VALUE * enemyCount) + (PLAYER_VALUE * playerCount)); 
			
		}
		return value;
	}
	
	public void createBoardValues()
	{
		int row = 0;
		int column = 0;
		//Check directions for all empty spaces.
		for(row = 0; row < boardSize; row++)
		{
			for(column = 0; column < boardSize; column++)
			{
				if(currentBoard[row][column] == ' ')
				{
					boardValues[row][column] = checkAllDirections(row, column); //Where there are movable spaces, there is a value.
				}
				else
					boardValues[row][column] = 0; //Where there are pieces, the value is 0.
			}
		}
	}
	
	public double[][] getBoardValues() //Just returns the board value array for testing purposes.
	{
		return boardValues;
	}
	
	public double boardValueTotal(double[][] boardValues) //Returns the total value for the state.
	{
		int row = 0;
		int column = 0;
		for(row = 0; row < boardSize; row++)
		{
			for(column = 0; column < boardSize; column++)
				totalValue += boardValues[row][column];
		}
		return totalValue;
	}
}