package gm.workwork;

import java.util.ArrayList;

public class Airport {
	
	private int airportID;
	private String name, city, country, iata;
	private double latitude, longitude;
	
	private ArrayList<Connection> links;
	private ArrayList<Integer> reverseLinks;
	
	public Airport(int airportID, String name, String city, String country,
			String iata, double latitude, double longitude, ArrayList<Connection> links) {
		this.airportID = airportID;
		this.name = name;
		this.city = city;
		this.country = country;
		this.iata = iata;
		this.latitude = latitude;
		this.longitude = longitude;
		this.links = links;
	}
	
	public int getAirportID() {
		return airportID;
	}
	public void setAirportID(int airportID) {
		this.airportID = airportID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getIata() {
		return iata;
	}
	public void setIata(String iata) {
		this.iata = iata;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public ArrayList<Connection> getLinks() {
		return links;
	}
	public void setLinks(ArrayList<Connection> links) {
		this.links = links;
	}
	public void addConnection(int airportID, int distance) {
		this.links.add(new Connection(airportID, distance));
	}
	public void setReverseLinks(ArrayList<Integer> revLinks) {
		this.reverseLinks = revLinks;
	}
	public ArrayList<Integer> getReverseLinks() {
		return reverseLinks;
	}
	@Override
	public String toString() {
		return "Airport [airportID=" + airportID + ", name=" + name + ", city="
				+ city + ", country=" + country + ", iata=" + iata
				+ ", latitude=" + latitude + ", longitude=" + longitude
				+ ", links=" + links + "]";
	}
	
}
