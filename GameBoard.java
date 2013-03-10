package ubco.ai.games;

import java.util.HashSet;

public class GameBoard {
	//int array to store the state of the board.
	//value at (x,y) cooresponds to:
		//-1 : occupied by our player
		//0  : Empty
		//1  : occupied by other player
		//2  : occupied by an arrow
	//The first array stores the row #, second array is the column #
	//The board is indexed starting at (0,0) at the TOP LEFT CORNER.
	//Incrementing the row, moves a player down.
	//Incrementing the column, moves a player right.
	public int[][] board;
	
	
	public Amazon[] Amazons;	//An array of all the amazons on the board. 
								//Indexes 0-3 store the positions of our amazon players.
								//Indexes 4-7 store the positions of the opponents players.
	
	public GameBoard(boolean firstPlayer) {
		board = new int[10][10];
		if(firstPlayer) {
			board[6][0] = -1;
			board[9][3] = -1;
			board[9][6] = -1;
			board[6][9] = -1;
			board[3][0] = 1;
			board[0][3] = 1;
			board[0][6] = 1;
			board[3][9] = 1;
			
			Amazons = new Amazon[8];
			Amazons[0] = new Amazon(6, 0, 0);
			Amazons[1] = new Amazon(9, 3, 1);
			Amazons[2] = new Amazon(9, 6, 2);
			Amazons[3] = new Amazon(6, 9, 3);
			Amazons[4] = new Amazon(3, 0, 4);
			Amazons[5] = new Amazon(0, 3, 5);
			Amazons[6] = new Amazon(0, 6, 6);
			Amazons[7] = new Amazon(3, 9, 7);
			
		} else {
			board[6][0] = 1;
			board[9][3] = 1;
			board[9][6] = 1;
			board[6][9] = 1;
			board[3][0] = -1;
			board[0][3] = -1;
			board[0][6] = -1;
			board[3][9] = -1;
			
			Amazons = new Amazon[8];
			Amazons[0] = new Amazon(3, 0, 0);
			Amazons[1] = new Amazon(0, 3, 1);
			Amazons[2] = new Amazon(0, 6, 2);
			Amazons[3] = new Amazon(3, 9, 3);
			Amazons[4] = new Amazon(6, 0, 4);
			Amazons[5] = new Amazon(9, 3, 5);
			Amazons[6] = new Amazon(9, 6, 6);
			Amazons[7] = new Amazon(6, 9, 7);
		}
		
	}
	
	public GameBoard(int[][] initialBoard, Amazon[] amazons) {
		this.board = initialBoard;
		this.Amazons = amazons;
	}
	
	public void moveOpponent(int fromX, int fromY, int toX, int toY, boolean whitePlayer)
	{
		board[fromX][fromY] = 0;
		if(whitePlayer)
			board[toX][toY] = -1;
		else
			board[toX][toY] = 1;
	}
	
	public void moveAmazon(int amazon, int row, int column) {
		board[Amazons[amazon].row][Amazons[amazon].column]=0;
		board[row][column] = (amazon<4)?-1:1;
		Amazons[amazon].updateCoords(row, column);
	}
	
	public void fireArrow(int x, int y) {
		board[x][y]=2;
	}
	
	public String toString() {
		String toReturn = "";
		for(int i=0; i<10; i++) {
			toReturn += "[";
			for(int j=0; j<10; j++) {
				toReturn+=board[i][j];
				if(j!=9) toReturn+="\t";
			}
			toReturn += "]\n";
		}
		return toReturn;
	}
	
	public GameBoard copyOf() {
		int[][] newBoard = new int[10][10];
		Amazon[] newAmazons = new Amazon[8];
		for(int i=0; i<10; i++) {
			for(int j=0; j<10; j++) {
				newBoard[i][j] = this.board[i][j];
			}
		}
		for(int i=0; i<8; i++) {
			newAmazons[i] = new Amazon(this.Amazons[i].row, this.Amazons[i].column, this.Amazons[i].id);
		}
		return new GameBoard(newBoard, newAmazons);
	}
	
	public HashSet<Integer> ourSpaces() {
		HashSet<Integer> spaces = new HashSet<Integer>();
		for(int i=0; i<4; i++) {
			spaces.addAll(moveableSpaces(Amazons[i]));
		}
		
		return spaces;
	}
	
	public HashSet<Integer> theirSpaces() {
		HashSet<Integer> spaces = new HashSet<Integer>();
		for(int i=4; i<8; i++) {
			spaces.addAll(moveableSpaces(Amazons[i]));
		}
		
		return spaces;
	}
	
	private HashSet<Integer> moveableSpaces(Amazon A) {
		HashSet<Integer> spaces = new HashSet<Integer>();

		int column = A.column;
		int row = A.row;
		
		//Moves Right
		for(int i=column+1; i < 10; i++) {
			if(board[row][i]==0) {	//If the spot is empty
				spaces.add(row*10+i);
			} else break;
		}
		
		//Moves Down
		for(int i=row+1; i < 10; i++) {
			if(board[i][column]==0) {	//If the spot is empty
				spaces.add(i*10+column);
			} else break;
		}
		
		//Moves Left
		for(int i=column-1; i >= 0; i--) {
			if(board[row][i]==0) {	//If the spot is empty
				spaces.add(row*10+i);
			} else break;
		}
		
		//Moves Up
		for(int i=row-1; i >= 0; i--) {
			if(board[i][column]==0) {	//If the spot is empty
				spaces.add(i*10+column);
			} else break;
		}
		
		//Moves Down-Right
		for(int i=column+1, j=row+1; i < 10 && j < 10; i++, j++) {
			if(board[j][i]==0) {	//If the spot is empty
				spaces.add(j*10+i);
			} else break;
		}
		
		//Moves Down-Left
		for(int i=column-1, j=row+1; i >= 0 && j < 10; i--, j++) {
			if(board[j][i]==0) {	//If the spot is empty
				spaces.add(j*10+i);
			} else break;
		}
		
		//Moves Up-Left
		for(int i=column-1, j=row-1; i >= 0 && j >= 0; i--, j--) {
			if(board[j][i]==0) {	//If the spot is empty
				spaces.add(j*10+i);
			} else break;
		}
		
		//Moves Up-Right
		for(int i=column+1, j=row-1; i < 10 && j >= 0; i++, j--) {
			if(board[j][i]==0) {	//If the spot is empty
				spaces.add(j*10+i);
			} else break;
		}
		
		return spaces;
	}
	
}
