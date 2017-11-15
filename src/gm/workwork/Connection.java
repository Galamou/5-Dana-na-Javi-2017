package gm.workwork;

public class Connection {
	
	private int airportID_to, distance;

	public Connection(int airportID_to, int distance) {
		this.airportID_to = airportID_to;
		this.distance = distance;
	}

	public int getAirportID_to() {
		return airportID_to;
	}

	public void setAirportID_to(int airportID_to) {
		this.airportID_to = airportID_to;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	@Override
	public String toString() {
		return "Connection [airportID_to=" + airportID_to + ", distance="
				+ distance + "]\n";
	}
	
}
