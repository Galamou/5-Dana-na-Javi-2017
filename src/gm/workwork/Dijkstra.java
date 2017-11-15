package gm.workwork;

import java.util.ArrayList;

public class Dijkstra {

	private ArrayList<Airport> airports;
	@SuppressWarnings("unused")
	private ArrayList<Route> routes;
	
	public Dijkstra(ArrayList<Airport> airports, ArrayList<Route> routes) {
		this.airports = airports;
		this.routes = routes;
	}
	
	public int startDijkstra(String s, String e, String [] avoid, boolean via) {
		
		int sum = 0;
		try {
		
			/**
			 * Initializing the start of Dijkstra algorithm.
			 */
			
			int start_index = 0;
			while(!airports.get(start_index).getIata().equals(s)) start_index++;
	
			ArrayList<ConnectionVisit> distances_from_start = new ArrayList<ConnectionVisit>();
			for(int i = 0; i < airports.size(); i++) 
				distances_from_start.add(
						new ConnectionVisit(
								airports.get(i).getAirportID(),
								Integer.MAX_VALUE,
								false
						)
				);
			distances_from_start.get(start_index).setDistance(0);
			
			ArrayList<Integer> airportIDs_unchecked = new ArrayList<Integer>();
			airportIDs_unchecked.add(distances_from_start.get(start_index).getAirportID_to());
			
			/**
			 * Dijkstra algorithm.
			 */
	
			try {
					
				do {
					
					start_index = 0;
					while(airportIDs_unchecked.get(0) != airports.get(start_index).getAirportID()) start_index++;
					
					ArrayList<Connection> temp_links = airports.get(start_index).getLinks();
					for(int i = 0; i < temp_links.size(); i++) {
					
						int index = 0;
						while(temp_links.get(i).getAirportID_to() != distances_from_start.get(index).getAirportID_to()) index++;
						
						boolean cancer = false;
						
						int temp_index = 0;
						while(airports.get(temp_index).getAirportID() != distances_from_start.get(index).getAirportID_to()) temp_index++;
						
						String current_name = airports.get(temp_index).getIata();
						if(avoid != null) {
							for(int j = 0; j < avoid.length; j++) {
								if(avoid[j].equals(current_name)) {
									cancer = true;
									break;
								}
							}
						}
						
						if(
							!cancer
							&&
							!distances_from_start.get(index).isVisited() 
							&& 
							(distances_from_start.get(index).getDistance() 
							> 
							(distances_from_start.get(start_index).getDistance() 
							+ 
							temp_links.get(i).getDistance())
							)
						) {
							distances_from_start.get(index).setDistance(
									distances_from_start.get(start_index).getDistance() 
									+ 
									temp_links.get(i).getDistance()
							);
							
							if(!airportIDs_unchecked.contains(distances_from_start.get(index).getAirportID_to()))
								airportIDs_unchecked.add(distances_from_start.get(index).getAirportID_to());
							
						}
						
					}
					
					distances_from_start.get(start_index).setVisited(true);
					
					airportIDs_unchecked.remove((Integer) distances_from_start.get(start_index).getAirportID_to());			
								
				} while(airportIDs_unchecked.size() != 0);
							
			} catch(Exception e1) {
				System.out.println("Greska pri izvrsavanju Dijkstra algoritma.");
				e1.printStackTrace();
			}
			
			/**
			 * Backtracking.
			 */
			
			int end_index = 0;
			while(!airports.get(end_index).getIata().equals(e)) end_index++;
			
			start_index = 0;
			while(!airports.get(start_index).getIata().equals(s)) start_index++;
			
			for(int i = 0; i < airports.size(); i++) {
				
				ArrayList<Integer> revLinks = new ArrayList<Integer>();
				
				for(int j = 0; j < airports.size(); j++) {
					for(int k = 0; k < airports.get(j).getLinks().size(); k++) {
						if(i != j && airports.get(j).getLinks().get(k).getAirportID_to() == airports.get(i).getAirportID()) {
							revLinks.add(airports.get(j).getAirportID());
							break;
						}
					}
				}			
				
				airports.get(i).setReverseLinks(revLinks);
			
			}
			
			ArrayList<String> from = new ArrayList<String>();
			ArrayList<String> to = new ArrayList<String>();
			ArrayList<Integer> distance = new ArrayList<Integer>();
			do {
				
				ArrayList<Integer> temp_links = airports.get(end_index).getReverseLinks();
	
				int index_of_closest = -1;
				for(int i = 0; i < temp_links.size(); i++) {
					
					int index = 0;
					while(temp_links.get(i) != distances_from_start.get(index).getAirportID_to()) index++;
				
					if(index_of_closest == -1 && distances_from_start.get(index).getDistance() != Integer.MAX_VALUE)
						index_of_closest = index;
					else if(index_of_closest != -1 && distances_from_start.get(index_of_closest).getDistance()
							> distances_from_start.get(index).getDistance()) index_of_closest = index;
					
				}
	
				int index = 0;
				while(airports.get(index).getAirportID() != distances_from_start.get(index_of_closest).getAirportID_to()) index++;
				
				from.add(airports.get(index).getCity());
				to.add(airports.get(end_index).getCity());
				
				double p = 0.017453292519943295;
				
				double a = 0.5 - Math.cos((airports.get(end_index).getLatitude() - airports.get(index_of_closest).getLatitude()) * p)/2 + 
						  Math.cos(airports.get(index_of_closest).getLatitude() * p) * Math.cos(airports.get(end_index).getLatitude() * p) * 
				          (1 - Math.cos((airports.get(end_index).getLongitude() - airports.get(index_of_closest).getLongitude()) * p))/2;
	
				double c = 12742 * Math.asin(Math.sqrt(a));
				
				distance.add(Math.abs((int) c));
				
				end_index = index_of_closest;
						
			} while(end_index != start_index);
			
			/**
			 * Print.
			 */
			
			for(int i = from.size() - 1; i > -1; i--) {

				System.out.print(from.get(i));
				System.out.print(" " + to.get(i));
				System.out.print(" " + distance.get(i));
				System.out.println();
				
			}

			for(int i = 0; i < distance.size(); i++) sum += distance.get(i);
			
		} catch(Exception ex) {
			System.out.println("Greska pri unosu ili racunanju algoritma.\nProverite ulazne datoteke i ulazne argumente!");
			return -1;
		}
		
		return sum;
		
	}
	
}
