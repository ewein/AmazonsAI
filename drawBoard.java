import java.io.IOException;
import java.net.UnknownHostException;


public class drawBoard {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public static void main(String[] args){
		chessBoard board = new chessBoard();
		board.moveOurPieces(0, 9, 0);
		board.fireArrow(9, 9);
	}

}
