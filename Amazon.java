import java.util.ArrayList;

public class Amazon {
	public int row;
	public int column;
	public int id;
	
	public Amazon(int row, int column, int id) {
		this.row = row;
		this.column = column;
		this.id = id;
	}
	
	public void updateCoords(int x, int y) {
		this.row=x;
		this.column=y;
	}
	
	public ArrayList<GameBoard> getPossibleMoves(GameBoard current) {
		ArrayList<GameBoard> successors = new ArrayList<GameBoard>();
		
		//Moves Right
		for(int i=column+1; i < 10; i++) {
			if(current.board[row][i]==0) {	//If the spot is empty
				GameBoard successor = current;
				successor.moveAmazon(this.id, row, i);
				ArrayList<int[]> arrows = getPossibleArrows(successor);
				for(int j=0; j<arrows.size(); j++) {
					successor.fireArrow(arrows.get(j)[0], arrows.get(j)[1]);
					successors.add(successor);
				}
			}
		}
		
		//Moves Down
		for(int i=row+1; i < 10; i++) {
			if(current.board[i][column]==0) {	//If the spot is empty
				GameBoard successor = current;
				successor.moveAmazon(this.id, i, column);
				ArrayList<int[]> arrows = getPossibleArrows(successor);
				for(int j=0; j<arrows.size(); j++) {
					successor.fireArrow(arrows.get(j)[0], arrows.get(j)[1]);
					successors.add(successor);
				}
			}
		}
		
		//Moves Left
		for(int i=column-1; i >= 0; i--) {
			if(current.board[row][i]==0) {	//If the spot is empty
				GameBoard successor = current;
				successor.moveAmazon(this.id, row, i);
				ArrayList<int[]> arrows = getPossibleArrows(successor);
				for(int j=0; j<arrows.size(); j++) {
					successor.fireArrow(arrows.get(j)[0], arrows.get(j)[1]);
					successors.add(successor);
				}
			}
		}
		
		//Moves Up
		for(int i=row-1; i >= 0; i--) {
			if(current.board[i][column]==0) {	//If the spot is empty
				GameBoard successor = current;
				successor.moveAmazon(this.id, i, column);
				ArrayList<int[]> arrows = getPossibleArrows(successor);
				for(int j=0; j<arrows.size(); j++) {
					successor.fireArrow(arrows.get(j)[0], arrows.get(j)[1]);
					successors.add(successor);
				}
			}
		}
		
		//Moves Down-Right
		for(int i=column+1, j=row+1; i < 10 && j < 10; i++, j++) {
			if(current.board[j][i]==0) {	//If the spot is empty
				GameBoard successor = current;
				successor.moveAmazon(this.id, j, i);
				ArrayList<int[]> arrows = getPossibleArrows(successor);
				for(int k=0; k<arrows.size(); k++) {
					successor.fireArrow(arrows.get(k)[0], arrows.get(k)[1]);
					successors.add(successor);
				}
			}
		}
		
		//Moves Down-Left
		for(int i=column-1, j=row+1; i >= 0 && j < 10; i--, j++) {
			if(current.board[j][i]==0) {	//If the spot is empty
				GameBoard successor = current;
				successor.moveAmazon(this.id, j, i);
				ArrayList<int[]> arrows = getPossibleArrows(successor);
				for(int k=0; k<arrows.size(); k++) {
					successor.fireArrow(arrows.get(k)[0], arrows.get(k)[1]);
					successors.add(successor);
				}
			}
		}
		
		//Moves Up-Left
		for(int i=column-1, j=row-1; i >= 0 && j >= 0; i--, j--) {
			if(current.board[j][i]==0) {	//If the spot is empty
				GameBoard successor = current;
				successor.moveAmazon(this.id, j, i);
				ArrayList<int[]> arrows = getPossibleArrows(successor);
				for(int k=0; k<arrows.size(); k++) {
					successor.fireArrow(arrows.get(k)[0], arrows.get(k)[1]);
					successors.add(successor);
				}
			}
		}
		
		//Moves Up-Right
		for(int i=column+1, j=row-1; i < 10 && j >= 0; i++, j--) {
			if(current.board[j][i]==0) {	//If the spot is empty
				GameBoard successor = current;
				successor.moveAmazon(this.id, j, i);
				ArrayList<int[]> arrows = getPossibleArrows(successor);
				for(int k=0; k<arrows.size(); k++) {
					successor.fireArrow(arrows.get(k)[0], arrows.get(k)[1]);
					successors.add(successor);
				}
			}
		}
		
		return successors;
	}
	
	public ArrayList<int[]> getPossibleArrows(GameBoard current) {
		ArrayList<int[]> arrows = new ArrayList<int[]>();
		
		//Arrows Right
		for(int i=column+1; i < 10; i++) {
			if(current.board[row][i]==0) {
				arrows.add(new int[] {row, i});
			} else break;
		}
		
		//Arrows Down
		for(int i=row+1; i < 10; i++) {
			if(current.board[i][column]==0) {
				arrows.add(new int[] {i, column});
			} else break;
		}		
		
		//Arrows Left
		for(int i=column-1; i >= 0; i--) {
			if(current.board[row][i]==0) {
				arrows.add(new int[] {row, i});
			} else break;
		}
		
		//Arrows Up
		for(int i=row-1; i >= 0; i--) {
			if(current.board[i][column]==0) {
				arrows.add(new int[] {i, column});
			} else break;
		}
		
		//Arrows Down-Right
		for(int i=column+1, j=row+1; i < 10 && j < 10; i++, j++) {
			if(current.board[j][i]==0) {
				arrows.add(new int[] {j, i});
			} else break;
		}
		
		//Arrows Down-Left
		for(int i=column-1, j=row+1; i >= 0 && j < 10; i--, j++) {
			if(current.board[j][i]==0) {
				arrows.add(new int[] {j, i});
			} else break;
		}
		
		//Arrows Up-Left
		for(int i=column-1, j=row-1; i >= 0 && j >= 0; i--, j--) {
			if(current.board[j][i]==0) {
				arrows.add(new int[] {j, i});
			} else break;
		}
		
		//Arrows Up-Right
		for(int i=column+1, j=row-1; i < 10 && j >= 0; i++, j--) {
			if(current.board[j][i]==0) {
				arrows.add(new int[] {j, i});
			} else break;
		}
		
		return arrows;
	}
}
