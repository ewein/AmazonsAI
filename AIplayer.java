import ubco.ai.GameRoom;
import ubco.ai.connection.ServerMessage;
import ubco.ai.games.GameClient;
import ubco.ai.games.GameMessage;
import ubco.ai.games.GamePlayer; 
import net.n3.nanoxml.*;
import java.util.ArrayList;

public class AIplayer implements GamePlayer {

	GameClient gameClient = null;
	ArrayList<GameRoom> roomList = null;
	GameBoard board = null;
	
	public AIplayer(String name, String passwd) {
		  
	    gameClient = new GameClient(name, passwd, this);
	    
	    roomList = gameClient.getRoomLists();  
	    for(int i=0; i<roomList.size(); i++) {
	    	System.out.println(roomList.get(i).toString());
	    }
	    
	    //TODO
	    boolean whitePlayer = true; //Get colour of our player and set this boolean accordingly.  
	    board = new GameBoard(whitePlayer);
	    System.out.println(board.toString());
	    
	    board.moveAmazon(4, 1, 0);
	    board.fireArrow(2, 0);
	    board.fireArrow(1, 1);
	    board.fireArrow(1, 2);
	    board.fireArrow(2, 1);
	    
	    System.out.println(board.Amazons[4].getPossibleMoves(board));
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
	
	public static void main(String[] args) {
		 AIplayer client = new AIplayer(args[0], args[1]); 
	}

}
