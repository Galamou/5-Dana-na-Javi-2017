package gm.workwork;

public class ConnectionVisit extends Connection {

	private boolean visited;
	
	public ConnectionVisit(int airportID_to, int distance, boolean visited) {
		super(airportID_to, distance);
		this.visited = false;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}	
	
}
