package ubco.ai.games;
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
	
}
