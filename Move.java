
public class Move {
	int amazon_id;
	int row;
	int col;
	int arrow_row;
	int arrow_col;
	
	public Move(int amazon, int row, int col, int arrow_row, int arrow_col) {
		this.amazon_id = amazon;
		this.row = row;
		this.col = col;
		this.arrow_row = arrow_row;
		this.arrow_col = arrow_col;
	}
	
	/**Return an int array representing the amazon's move.
	//int[0] = amazon id being moved;
	//int[1] = row to move amazon to;
	//int[2] = column to move amazon to;
	 * 
	 * @return int[3]
	 */
	public int[] getAmazonMove() {
		int[] thisMove = new int[3];
		thisMove[0] = amazon_id;
		thisMove[1] = row;
		thisMove[2] = col;
		
		return thisMove;
	}
	
	/**Return an int array representing where to shoot an arrow
	//int[0] = row of arrow
	//int[1] = column of arrow
	 * 
	 * @return int[2]
	 */
	public int[] getArrowMove() {
		int[] arrowShot = new int[2];
		arrowShot[0] = arrow_row;
		arrowShot[1] = arrow_col;
		
		return arrowShot;
	}
}
