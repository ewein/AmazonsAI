package ubco.ai.games;
public class SearchNode {
	MoveAndBoard state;
	int heuristic;
	
	public SearchNode(MoveAndBoard state) {
		this.state = state;
		this.heuristic = 0;
	}
	
	public SearchNode(MoveAndBoard state, int heuristic) {
		this.state = state;
		this.heuristic = heuristic;
	}
	
	public MoveAndBoard getState() {
		return this.state;
	}
	
	public int heuristic() {
		return heuristic;
	}
}
