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
	
	public AIplayer(String name, String passwd) {
		  
		this.name = name;
		this.passwd = passwd;
		gameClient = new GameClient(name, passwd, this);
	    
		// setup up the board 
		board = new GameBoard(whitePlayer);
	    board.moveAmazon(0, 1, 0);
	    
	    board.fireArrow(2, 0);
	    board.fireArrow(1, 1);
	    board.fireArrow(1, 2);
	    board.fireArrow(2, 1);
		
		// join a open room
	 /*   roomList = gameClient.getRoomLists();  
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
	     */	    	  
	}
		
	public void sendToServer(String action, int roomID, int posX, int posY, int arow, int acol, int qfr, int qfc)
	{
		String actionMsg = "<action type='" +  action + "'>";
		Character c = new Character((char) (97 + qfr));
		actionMsg = actionMsg + "<queen move='" + c.charValue() + String.valueOf(qfc) + "-";  
		c = new Character((char)(97 + posX)); 
		actionMsg = actionMsg + c.charValue() + String.valueOf(posY) + "'>" + "</queen> ";
		c = new Character((char) (97 + arow));
		actionMsg = actionMsg + "<arrow move='" + c.charValue() + String.valueOf(acol) + "'>" + "</arrow>";
		actionMsg = actionMsg + "</action>";
		System.out.println(actionMsg);
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
		qX = c - 97; 
		qY = Integer.parseInt(qmove.substring(4,5));
		
		int qfX = 0;
		int qfY = 0;
		c = qmove.charAt(0);
		qfX = c - 97;
		qfY = Integer.parseInt(qmove.substring(1, 2));
		
		int aX = 0;
		int aY = 0;
		c = amove.charAt(0);
		aX = c - 97;
		aY = Integer.parseInt(amove.substring(1, amove.length()));
		
		int amazonId = board.getAmazonId(qfX, qfY);
		
		// move opponent amazon and fire arrow
		board.moveOpponent(amazonId, qfX, qfY, qX, qY, whitePlayer);
		board.fireArrow(aX, aY);
	}
	
	public Move getNextMove() {
		MoveAndBoard nextMove = GreedyBestSearch();
		// get the new position of the amazon and the arrow
		int y = nextMove.newMove.col;
		int x = nextMove.newMove.col;
		int arrowY = nextMove.newMove.arrow_col;
		int arrowX = nextMove.newMove.arrow_row;
		int amazonId = nextMove.newMove.amazon_id;
		int fromX = nextMove.newBoard.Amazons[amazonId].row;
		int fromY = nextMove.newBoard.Amazons[amazonId].column;
		
		// send move to server
		sendToServer(GameMessage.ACTION_MOVE, roomID, x, y, arrowX, arrowY, fromX, fromY);
		
		return nextMove.newMove;
	}
	
	private MoveAndBoard GreedyBestSearch() {
		
		ArrayList<MoveAndBoard> searchResults = new ArrayList<MoveAndBoard>();
		for(int i=0; i<4; i++) {
			searchResults.addAll(board.Amazons[i].getPossibleMoves(board));
		}
		
		MoveAndBoard newState = defaultHeuristic(searchResults);
		
		return newState;
	}
	
	private MoveAndBoard defaultHeuristic(ArrayList<MoveAndBoard> moves) {
		ArrayList<Integer> evaluations = new ArrayList<Integer>(moves.size());
		for(int i=0; i<moves.size(); i++) {
			evaluations.add(i, new Integer(evaluateHeuristic(moves.get(i).newBoard)));
		}
		Integer max = Collections.max(evaluations);
		return moves.get(evaluations.indexOf(max));
		
	}
	
	private int evaluateHeuristic(GameBoard board) {
		//Set containing all the board squares movable to by our Amazon players.
		HashSet<Integer> A = new HashSet<Integer>();
		A = board.ourSpaces();
		
		//Set containing all the board squares movable to by the opponents players.
		HashSet<Integer> B = new HashSet<Integer>();
		B = board.theirSpaces();
		
		HashSet<Integer> C = new HashSet<Integer>(A);
		A.removeAll(B);
		B.removeAll(C);
		
		return A.size()-B.size();
	}
	
	public static void main(String[] args) {
		 AIplayer client = new AIplayer("10", "10"); 
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
    			System.out.println("Game on!");	
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
    			}
    			else{
    				this.whitePlayer = false;
    			}
    			
    			// setup up the board 
    			board = new GameBoard(whitePlayer);
    		    board.moveAmazon(0, 1, 0);
    		    
    		    board.fireArrow(2, 0);
    		    board.fireArrow(1, 1);
    		    board.fireArrow(1, 2);
    		    board.fireArrow(2, 1);
    		}
        	
        	System.out.println("Game Start: " + msg.msg);
        }
        else if(type.equals(GameMessage.ACTION_MOVE))		// handle an opponent move
        {
            this.handleOpponentMove(xml); 
        }        
 
		return true;
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
