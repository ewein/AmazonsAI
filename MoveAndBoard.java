package ubco.ai.games;

public class MoveAndBoard {
	GameBoard newBoard;
	Move newMove;
	
	public MoveAndBoard(GameBoard board, Move move) {
		this.newBoard = board;
		this.newMove = move;
	}
	
	public String toString() {
		String toReturn = "";
		toReturn += "Move Amazon ID: " + newMove.amazon_id + " to ROW: " + newMove.row + "\tCOLUMN: " + newMove.col + "\n";
		toReturn += "Shot Arrow to ROW: " + newMove.arrow_row + "\tCOLUMN: " + newMove.arrow_col + "\n";
		toReturn += "New Board:\n" + newBoard;
		return toReturn;
	}
}
