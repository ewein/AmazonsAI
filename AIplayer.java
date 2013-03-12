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
		qX = 9 - (c - 97); 
		qY = Integer.parseInt(qmove.substring(4,5));
	
		int qfX = 0;
		int qfY = 0;
		c = qmove.charAt(0);
		qfX = 9 - (c - 97);
		qfY = Integer.parseInt(qmove.substring(1, 2));
		
		int aX = 0;
		int aY = 0;
		c = amove.charAt(0);
		aX = 9 - (c - 97);
		aY = Integer.parseInt(amove.substring(1, amove.length()));
		
		int amazonId = board.getAmazonId(qfX, qfY);
		
		// move opponent amazon and fire arrow
		board.moveOpponent(amazonId, qfX, qfY, qX, qY, whitePlayer);
		board.fireArrow(aX, aY);
		
		// get the new position of the amazon and the arrow		
		MoveAndBoard nextMove = getNextMove();
		int y = nextMove.newMove.col;
		int x = 9 - (nextMove.newMove.row);
		int arrowY = nextMove.newMove.arrow_col;
		int arrowX = 9 - (nextMove.newMove.arrow_row);
		int aId = nextMove.newMove.amazon_id;
		int fromX = 9 - (nextMove.newBoard.Amazons[aId].row);
		int fromY = nextMove.newBoard.Amazons[aId].column;
		
		// send move to server
		sendToServer(GameMessage.ACTION_MOVE, roomID, x, y, arrowX, arrowY, fromX, fromY);
	}
	
	public MoveAndBoard getNextMove() {
		MoveAndBoard nextMove = GreedyBestSearch();
		return nextMove;
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

	private MoveAndBoard randomHeuristic(ArrayList<MoveAndBoard> moves){
		ArrayList<Integer> evaluations = new ArrayList<Integer>(moves.size());
		for(int i=0; i<moves.size(); i++) {
			evaluations.add(i, new Integer(evaluateRandomHeuristic(moves.get(i).newBoard)));
		}
			
		Integer max = Collections.max(evaluations);
		return moves.get(evaluations.indexOf(max));
	}

	private int evaluateRandomHeuristic(GameBoard board){
		Random rand = new Random();
		return rand.nextInt(10000);
	}

	private int evaluateHeuristic(GameBoard board) {
		int sum = getSpaceConfiguration(board);
		sum += getOurFreedom(board);
		return sum;
	}

	/**
	* Returns the score of the board as determined by freedom. For a single amazon their freedom is (8 - the number of arrows/queens beside them). This calculates the freedom of each of our amazons.
	* @param board
	* @return Returns the overall freedom of our amazons. The bigger the better.
	*/
	private int getOurFreedom(GameBoard board){
		return board.getOurFreedoms();
	}

	/**
	* This will return how many of the spaces are ours minus the number of spaces that are theirs.
	* @param board
	* @return Number of spaces that are ours - spaces theirs. Larger the better
	*/
	private int getSpaceConfiguration(GameBoard board){
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
		 AIplayer client = new AIplayer("FooGoos", "amazons"); 
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
    			}
    			else{
    				this.whitePlayer = false;
    			}
    		}
    		
			// setup up the board 
			board = new GameBoard(whitePlayer);
        	
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
