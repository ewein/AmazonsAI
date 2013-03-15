package ubco.ai.games;

import ubco.ai.GameRoom;
import ubco.ai.connection.ServerMessage;
import ubco.ai.games.GameClient;
import ubco.ai.games.GameMessage;
import ubco.ai.games.GamePlayer; 
import net.n3.nanoxml.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

public class AIplayer implements GamePlayer {

	GameClient gameClient = null;
	ArrayList<GameRoom> roomList = null;
	GameRoom currentRoom = null; 
	int roomID = 0;
	GameBoard board = null;
	boolean whitePlayer = true;
	boolean gameStarted = false;
	String name = "";
	String passwd = "";
	chessBoard gameBoard;
	AI ai = null;

	public AIplayer(String name, String passwd) 
	{	  
		this.gameBoard = new chessBoard();
		this.name = name;
		this.passwd = passwd;
		this.ai = new AI(board);
		// setup server game
		gameClient = new GameClient(name, passwd, this);
		roomList = gameClient.getRoomLists();

		// join a particular room (room 0)
		//gameClient.roomList.get(0);
		
		// join the first open room
		for(int i=0; i<roomList.size(); i++) 
		{
		  	if(roomList.get(i).userCount < 2)
		   	{
		   		currentRoom = gameClient.roomList.get(i); 
		   		roomID = currentRoom.roomID;
		   		gameClient.joinGameRoom(currentRoom.roomName);
		    	System.out.println("Joined Room: " + roomList.get(i).roomName);
		  		break;
		  	}
		 }
			  
	}
		
	public void sendToServer(String action, int roomID, int posX, int posY, int arow, int acol, int qfr, int qfc){
		/*try {
		    Thread.sleep(4000);
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}*/
	
		String actionMsg = "<action type='" +  action + "'>";
		Character c = new Character((char) (97 + qfr));
		char xFrom = c;
		actionMsg = actionMsg + "<queen move='" + c.charValue() + String.valueOf(qfc) + "-";  
		c = new Character((char)(97 + posX));
		char xTo = c;
		actionMsg = actionMsg + c.charValue() + String.valueOf(posY) + "'>" + "</queen> ";
		c = new Character((char) (97 + arow));
		char arrowX = c;
		actionMsg = actionMsg + "<arrow move='" + c.charValue() + String.valueOf(acol) + "'>" + "</arrow>";
		actionMsg = actionMsg + "</action>";
		System.out.println(actionMsg);
		
		if(whitePlayer)
		{
			gameBoard.writeMove("white", "White player moved from " + xFrom + qfc + " to " + xTo + posY + " and fired arrow to " + arrowX + acol);
		}
		else
		{
			gameBoard.writeMove("black", "Black player moved from " + xFrom + qfc + " to " + xTo + posY + " and fired arrow to " + arrowX + acol);
		}
		
		String msg = ServerMessage.compileGameMessage(GameMessage.MSG_GAME, roomID, actionMsg);
		gameClient.sendToServer(msg, true); 
	}
	
	private void handleOpponentMove(IXMLElement xml){
		System.out.println("Opp Move");
    	
        IXMLElement c1 = xml.getFirstChildNamed("queen");
        String qmove = c1.getAttribute("move", "default");
        
        IXMLElement c2 = xml.getFirstChildNamed("arrow");
        String amove = c2.getAttribute("move", "defalut");
              		
		int qX = 0;
		int qY = 0;
				
		char c = qmove.charAt(3);
		char xTo = c;
		qX = 9 - (c - 97); 
		qY = Integer.parseInt(qmove.substring(4,5));
	
		int qfX = 0;
		int qfY = 0;
		c = qmove.charAt(0);
		char xFrom = c;
		qfX = 9 - (c - 97);
		qfY = Integer.parseInt(qmove.substring(1, 2));
		
		int aX = 0;
		int aY = 0;
		c = amove.charAt(0);
		char arrowX = c;
		aX = 9 - (c - 97);
		aY = Integer.parseInt(amove.substring(1, amove.length()));
		
		int amazonId = board.getAmazonId(qfX, qfY);
		
		// move opponent amazon and fire arrow
		board.moveOpponent(amazonId, qfX, qfY, qX, qY, whitePlayer);
		if(whitePlayer)
		{
			gameBoard.writeMove("black", "Black player moved from " + xFrom + qfY + " to " + xTo + qY + " and fired arrow to " + arrowX + aY);
		}
		else
		{
			gameBoard.writeMove("white", "White player moved from " + xFrom + qfY + " to " + xTo + qY + " and fired arrow to " + arrowX + aY);
		}
		
		board.fireArrow(aX, aY);
		if(whitePlayer){
			gameBoard.moveTheirPieces(amazonId-4, qX, qY);
		}
		else{
			gameBoard.moveOurPieces(amazonId-4, qX, qY);
		}
		gameBoard.fireArrow(aX, aY);
		
		moveOurAmazon();
	}
	
	public void moveOurAmazon()
	{
		// get our move based on the opponents move	
		MoveAndBoard nextMove = ai.getNextMove(this.board);
		int y = nextMove.newMove.col;
		int x = (nextMove.newMove.row);
		int arrowY = nextMove.newMove.arrow_col;
		int arrowX = (nextMove.newMove.arrow_row);
		int aId = nextMove.newMove.amazon_id;
		int fromX = (this.board.Amazons[aId].row);
		int fromY = this.board.Amazons[aId].column;
		this.board = nextMove.newBoard;
		moveGamePieceGui(aId, x, y, arrowX, arrowY);
	
		// send move to server
		sendToServer(GameMessage.ACTION_MOVE, roomID, 9-x, y, 9-arrowX, arrowY, 9-fromX, fromY);
	}
	
	public static void main(String[] args) {
		System.out.println("Do you want to play against the computer? (Yes or No): ");
		Scanner scan = new Scanner(System.in);
		String input = scan.nextLine();
		if(input.equals("Yes"))
		{
			Human humanPlayer = new Human();
			humanPlayer.getOpponentMove();
		}
		else
		{
			AIplayer client = new AIplayer("FooGoos", "amazons"); 
		}
	}

	@Override
	public boolean handleMessage(GameMessage msg)
	{
        IXMLElement xml = ServerMessage.parseMessage(msg.msg); 
        String type = xml.getAttribute("type", "WRONG!");
        System.out.println(msg);
        
        if(type.equals(GameMessage.ACTION_ROOM_JOINED))
        {
        	onJoinRoom(xml);
        }
        else if (type.equals(GameMessage.ACTION_GAME_START))		// handle game start
        {
        	this.gameStarted = true;
        	
    		IXMLElement usrlist = xml.getFirstChildNamed("usrlist");
    		int ucount = usrlist.getAttribute("ucount", -1);
    		
    		Enumeration ch = usrlist.enumerateChildren();
    		// start the game and set up board
    		while(ch.hasMoreElements())
    		{	
    			IXMLElement usr = (IXMLElement)ch.nextElement();
    			int id = usr.getAttribute("id", -1); 
    			String name = usr.getAttribute("name", "nnn");
    			
    			if(!name.equalsIgnoreCase(this.name)){
    				continue;
    			}
    			
    			// decide if we are white or black
    			String role = usr.getAttribute("role", "W");
    			if(role.equalsIgnoreCase("W")){
    				this.whitePlayer = true;
    				
        			// setup up the board 
        			this.board = new GameBoard(whitePlayer);
        			gameBoard.writeWhiteAndBlack(whitePlayer);
        			System.out.println("Game Start: " + msg.msg);
        			moveOurAmazon();
    			}
    			else{
    				this.whitePlayer = false;
        			// setup up the board 
        			this.board = new GameBoard(whitePlayer);
        			gameBoard.writeWhiteAndBlack(whitePlayer);
        			System.out.println("Game Start: " + msg.msg);
    			}

    		}
    		
        	System.out.println("Game Start: " + msg.msg);
        }
        else if(type.equals(GameMessage.ACTION_MOVE))		// handle an opponent move
        {
			//System.out.println("GOT OPPONENT MOVE");
            this.handleOpponentMove(xml); 
        }        
 
		return true;
	}
	
	private void moveGamePieceGui(int aId, int x, int y, int arrowX, int arrowY)
	{
		// move piece on GUI
		if(whitePlayer){
			gameBoard.moveOurPieces(aId, x, y);
		}
		else{
			gameBoard.moveTheirPieces(aId, x, y);
		}
		gameBoard.fireArrow(arrowX, arrowY);
	}
	
	//handle the response of joining a room
	private void onJoinRoom(IXMLElement xml){
		IXMLElement usrlist = xml.getFirstChildNamed("usrlist");
		int ucount = usrlist.getAttribute("ucount", -1);
		
		Enumeration ch = usrlist.enumerateChildren();
		while(ch.hasMoreElements()){
			IXMLElement usr = (IXMLElement)ch.nextElement();
			int id = usr.getAttribute("id", -1); 
			String name = usr.getAttribute("name", "NO!");  
		}
	 
	}

	@Override
	public boolean handleMessage(String arg0) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

}
