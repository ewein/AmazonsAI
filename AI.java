package ubco.ai.games;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

public class AI {

	GameBoard board = null;

	public AI(GameBoard b)
	{
		this.board = b;
	}

	public MoveAndBoard getNextMove(GameBoard b) {
		this.board = b;
		//MoveAndBoard nextMove = GreedyBestSearch();
		MoveAndBoard nextMove = minimaxSearch(500, 1);
		return nextMove;
	}

	// currently not using
	private MoveAndBoard GreedyBestSearch() {
		//we need to check to see if any of our amazons are almost blocked in (having a freedom of 1 or 2). If so then we need to move them immediately and not worry about anything else
		Amazon current = checkForDireAmazon();

		ArrayList<MoveAndBoard> searchResults = new ArrayList<MoveAndBoard>();
		// if we found an amazon that needs to be moved, only look at its possible moves.
		if (current != null)
		{
			//get only moves from the amazon
			searchResults.addAll(board.getPossibleMoves(current.id));
			if(searchResults.size() == 0)
			{
				for(int i=0; i<4; i++) 
				{
					searchResults.addAll(board.getPossibleMoves(i));
				}
			}
		}
		else
		{
			searchResults = new ArrayList<MoveAndBoard>();
			for(int i=0; i<4; i++) 
			{
				searchResults.addAll(board.getPossibleMoves(i));
			}
		}

		MoveAndBoard newState = defaultHeuristic(searchResults);

		return newState;
	}


	/**
	 * 
	 * @param timer - Amount of seconds to restrict the search to.
	 * @param depth - The maximum depth to expand within the amount of time.
	 * @return Recommended next move.
	 */
	private MoveAndBoard minimaxSearch(int timer, int depth) {
		SearchNode initialNode = new SearchNode(new MoveAndBoard(board, null));
		long endtime = System.currentTimeMillis()+timer*1000;
		return minimax(initialNode, true, endtime, depth).state;
	}

	private SearchNode minimax(SearchNode node, boolean max, long endtime, int depth) {
		if(System.currentTimeMillis()>=endtime || depth<=0) return new SearchNode(node.state, evaluateHeuristic(node.state.newBoard));
		int alpha = max?Integer.MIN_VALUE:Integer.MAX_VALUE;
		ArrayList<SearchNode> children = getChildren(node, max);
		SearchNode nextBestNode = null;
		for(int i=0; i<children.size(); i++) {
			nextBestNode = minimax(children.get(i), !max, endtime, depth-1);
			alpha = max?Math.max(alpha, nextBestNode.heuristic):Math.min(alpha, nextBestNode.heuristic);
			nextBestNode.heuristic = alpha;
		}
		return nextBestNode;
	}

	private ArrayList<SearchNode> getChildren(SearchNode node, boolean ourTurn) {
		ArrayList<SearchNode> children = new ArrayList<SearchNode>();
		for(int i=0; i<4; i++) {
			int j = ourTurn?i:i+4;
			ArrayList<MoveAndBoard> amazonMoves = node.state.newBoard.getPossibleMoves(j);
			for(int k=0; k<amazonMoves.size(); k++) {
				children.add(new SearchNode(amazonMoves.get(k), evaluateHeuristic(amazonMoves.get(k).newBoard)));
			}
		}
		return children;
	}

	private Amazon checkForDireAmazon() {
		Amazon dire = null;
		for(int j = 0; j < 4; j++){
			if(board.getOurFreedom(board.Amazons[j]) <= 2){
				dire = board.Amazons[j];
			}
		}
		return dire;
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
}
