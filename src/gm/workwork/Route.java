package gm.workwork;

public class Route {

	private String fromAirport, toAirport;

	public Route(String fromAirport, String toAirport) {
		this.fromAirport = fromAirport;
		this.toAirport = toAirport;
	}

	public String getFromAirport() {
		return fromAirport;
	}

	public void setFromAirport(String fromAirport) {
		this.fromAirport = fromAirport;
	}

	public String getToAirport() {
		return toAirport;
	}

	public void setToAirport(String toAirport) {
		this.toAirport = toAirport;
	}
	
}
