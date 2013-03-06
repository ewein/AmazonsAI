import ubco.ai.GameRoom;
import ubco.ai.connection.ServerMessage;
import ubco.ai.games.GameClient;
import ubco.ai.games.GameMessage;
import ubco.ai.games.GamePlayer; 
import net.n3.nanoxml.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class AIplayer implements GamePlayer {

	GameClient gameClient = null;
	ArrayList<GameRoom> roomList = null;
	GameBoard board = null;
	
	public AIplayer(String name, String passwd) {
		  
	  /*  gameClient = new GameClient(name, passwd, this);
	    
	    roomList = gameClient.getRoomLists();  
	    for(int i=0; i<roomList.size(); i++) {
	    	System.out.println(roomList.get(i).toString());
	    }
	    */
	    
	    //TODO
	    boolean whitePlayer = true; //Get colour of our player and set this boolean accordingly.  
	    board = new GameBoard(whitePlayer);
	    
	    
	    
	    board.moveAmazon(0, 1, 0);
	    
	    board.fireArrow(2, 0);
	    board.fireArrow(1, 1);
	    board.fireArrow(1, 2);
	    board.fireArrow(2, 1);
	    
	    System.out.println(board);
	    
	    System.out.println(GreedyBestSearch());
	}
	
	public void sendToServer(String msgType, int roomID){
		String actionMsg = "<action type='" +  GameMessage.MSG_GAME + "'>";
		
		//this is not the correct message format for the Amazons game!!
		actionMsg = actionMsg + "<X value='" + 10 + "'>" + "</X>";
		actionMsg = actionMsg + "<Y value='" + 20 + "'>" + "</Y>";
		
		 
		actionMsg = actionMsg + "</action>";

		String msg = ServerMessage.compileGameMessage(msgType, roomID, actionMsg);
	   	
	    gameClient.sendToServer(msg);	
	}
	
	public boolean handleMessage(GameMessage arg0) throws Exception {
		System.out.println("[COSC322TestA: Server Said =]  " + arg0.toString());
		return true;
	}
 
	
	public boolean handleMessage(String arg0) throws Exception {
		 
		return true;
	}
	
	public Move getNextMove() {
		MoveAndBoard nextMove = GreedyBestSearch();
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
		 AIplayer client = new AIplayer(args[0], args[1]); 
	}

}
