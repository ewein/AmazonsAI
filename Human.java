package ubco.ai.games;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

public class Human {
	GameBoard board = null;
	boolean whitePlayer = true;
	chessBoard gameBoard;
	AI ai = null;
	
	public Human()
	{
		this.gameBoard = new chessBoard();
		this.board = new GameBoard(false);
		this.ai = new AI(board);
		
		gameBoard.writeWhiteAndBlack(whitePlayer);
		System.out.println("Game Started!");
	}
	
	public void getOpponentMove()
	{
		int qX = 0;
		int qY = 0;
		int qfX = 0;
		int qfY = 0;
		int aX = 0;
		int aY = 0;
		
		// TODO: replace this scanner code with a chessBoard object that returns the coordinates of the from, to and arrow coordinates
		Scanner scan = new Scanner(System.in);
		System.out.print("Move amazon: ");
		String from = scan.nextLine();
		System.out.print("To Sqaure: ");	
		scan = new Scanner(System.in);
		String to = scan.nextLine();
		System.out.print("Shoot arrow to: ");
		scan = new Scanner(System.in);
		String arrowTo = scan.nextLine();
		
		char c = from.charAt(0);
		char xFrom = c;
		qfX = 9 - (c - 97); 
		qfY = Integer.parseInt(from.substring(1));
	
		c = to.charAt(0);
		char xTo = c;
		qX = 9 - (c - 97);
		qY = Integer.parseInt(to.substring(1));
		
		c = arrowTo.charAt(0);
		char arrowX = c;
		aX = 9 - (c - 97);
		aY = Integer.parseInt(arrowTo.substring(1));
		
		gameBoard.writeMove("white", "White player moved from " + xFrom + qfY + " to " + xTo + qY + " and fired arrow to " + arrowX + aY);
		// move the piece on the board
		int amazonId = board.getAmazonId(qfX, qfY);
		
		// move opponent amazon and fire arrow
		board.moveOpponent(amazonId, qfX, qfY, qX, qY, true);
		board.fireArrow(aX, aY);
	
		// move piece on GUI
		moveGamePieceGui(amazonId, qX, qY, aX, aY, true);
		
		// call AI move
		getAIMove();
	}
	
	public void getAIMove()
	{
		// get our move based on the opponents move	
		MoveAndBoard nextMove = ai.getNextMove(this.board);
		int y = nextMove.newMove.col;
		int x = (nextMove.newMove.row);
		Character c = new Character((char) (97 + (9-x)));
		char toX = c;
		int arrowY = nextMove.newMove.arrow_col;
		int arrowX = (nextMove.newMove.arrow_row);
		c = new Character((char) (97 + (9-arrowX)));
		char aX = c;
		int aId = nextMove.newMove.amazon_id;
		int fromX = (this.board.Amazons[aId].row);
		c = new Character((char) (97 + (9-fromX)));
		char xFrom = c;
		int fromY = this.board.Amazons[aId].column;
		this.board = nextMove.newBoard;
				
		// move piece on GUI
		moveGamePieceGui(aId, x, y, arrowX, arrowY, false);

		gameBoard.writeMove("black", "Black player moved from " + xFrom + fromY + " to " + toX + y + " and fired arrow to " + aX + arrowY);
		
		getOpponentMove();
	}
	
	private void moveGamePieceGui(int aId, int x, int y, int arrowX, int arrowY, boolean whitePlayer)
	{
		// move piece on GUI
		if(whitePlayer){
			gameBoard.moveOurPieces(aId-4, x, y);
		}
		else{
			gameBoard.moveTheirPieces(aId, x, y);
		}
		
		gameBoard.fireArrow(arrowX, arrowY);
	}
	
}
