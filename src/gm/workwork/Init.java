package gm.workwork;

import java.io.FileReader;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.apache.commons.cli.*;

public class Init {

	public static void main(String[] args) {

		ArrayList<Airport> airports = new ArrayList<Airport>();
		ArrayList<Route> routes = new ArrayList<Route>();
		
		/**
		 * Loading command line input.
		 */
		
		Options options = new Options();

        Option input_airports = new Option("a", "airports", true, "airports file");
        input_airports.setRequired(false);
        options.addOption(input_airports);

        Option input_routes = new Option("r", "routes", true, "routes file");
        input_routes.setRequired(false);
        options.addOption(input_routes);
        
        Option input_start = new Option("s", "start", true, "starting IATA");
        input_start.setRequired(true);
        options.addOption(input_start);
        
        Option input_end = new Option("e", "end", true, "end IATA");
        input_end.setRequired(true);
        options.addOption(input_end);
        
        Option input_via = new Option("via", "via", true, "via IATA");
        input_via.setRequired(false);
        options.addOption(input_via);
        
        Option input_avoid = new Option("avoid", "avoid", true, "avoid IATAs");
        input_avoid.setArgs(Option.UNLIMITED_VALUES);
        input_avoid.setRequired(false);
        options.addOption(input_avoid);

        CommandLineParser cmd_parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = cmd_parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
            return;
        }

        String s = cmd.getOptionValue("start"), e = cmd.getOptionValue("end"), via = cmd.getOptionValue("via");
		String [] avoid = cmd.getOptionValues("avoid");

		String s_airports = cmd.getOptionValue("airports");
		String s_routes = cmd.getOptionValue("routes");
		
		if(s_airports == null) s_airports = "airports.json";
		if(s_routes == null) s_routes = "routes.json";
		
		/**
		 * Loading JSON file(s) data.
		 */
		
		JSONParser parser = new JSONParser();
		
		try {
			
			JSONArray array = (JSONArray) parser.parse(new FileReader(s_airports));
			
			for(Object o : array) {
					
				JSONObject airport = (JSONObject) o;
				airports.add(
						new Airport(
								
								((Number) airport.get("airportID")).intValue(),
								(String) airport.get("name"),
								(String) airport.get("city"),
								(String) airport.get("country"),
								(String) airport.get("iata"),
								(double) airport.get("latitude"),
								(double) airport.get("longitude"),
								(new ArrayList<Connection>())
								
						)
				);
				
			}
			
			array = (JSONArray) parser.parse(new FileReader(s_routes));
			
			for(Object o : array) {
				
				JSONObject route = (JSONObject) o;
				routes.add(
						new Route(
								
								(String) route.get("fromAirport"),
								(String) route.get("toAirport")
						)
				);
	
			}
			
		} catch (Exception e1) {
			System.out.println("Greska pri ucitavanju!");
			e1.printStackTrace();
		}
		
		/**
		 * Calculating distances.
		 */
		
		for(int i = 0; i < routes.size(); i++) {
			
			int index_from = 0, index_to = 0;
			while(!airports.get(index_from).getIata().equals(routes.get(i).getFromAirport())) index_from++;
			while(!airports.get(index_to).getIata().equals(routes.get(i).getToAirport())) index_to++;

			double p = 0.017453292519943295;
			
			double a = 0.5 - Math.cos((airports.get(index_to).getLatitude() - airports.get(index_from).getLatitude()) * p)/2 + 
					  Math.cos(airports.get(index_from).getLatitude() * p) * Math.cos(airports.get(index_to).getLatitude() * p) * 
			          (1 - Math.cos((airports.get(index_to).getLongitude() - airports.get(index_from).getLongitude()) * p))/2;

			double c = 12742 * Math.asin(Math.sqrt(a));
			
			airports.get(index_from).addConnection(airports.get(index_to).getAirportID(), (int) (Math.abs(c)));
			
		}
		
		Dijkstra dij = new Dijkstra(airports, routes);
		
		int sum = 0;
		if(via == null) {
			sum += dij.startDijkstra(s, e, avoid, false);
		} else {
			sum += dij.startDijkstra(s, via, avoid, true);
			sum += dij.startDijkstra(via, e, avoid, false);
		}
		
		System.out.println("Ukupno " + sum);
		
//		/**
//		 * Initializing the start of Dijkstra algorithm.
//		 */
//		
//		int start_index = 0;
//		while(!airports.get(start_index).getIata().equals(s)) start_index++;
//
//		ArrayList<ConnectionVisit> distances_from_start = new ArrayList<ConnectionVisit>();
//		for(int i = 0; i < airports.size(); i++) 
//			distances_from_start.add(
//					new ConnectionVisit(
//							airports.get(i).getAirportID(),
//							Integer.MAX_VALUE,
//							false
//					)
//			);
//		distances_from_start.get(start_index).setDistance(0);
//		
//		ArrayList<Integer> airportIDs_unchecked = new ArrayList<Integer>();
//		airportIDs_unchecked.add(distances_from_start.get(start_index).getAirportID_to());
//		
//		/**
//		 * Dijkstra algorithm.
//		 */
//
//		try {
//				
//			do {
//				
//				start_index = 0;
//				while(airportIDs_unchecked.get(0) != airports.get(start_index).getAirportID()) start_index++;
//				
//				ArrayList<Connection> temp_links = airports.get(start_index).getLinks();
//				for(int i = 0; i < temp_links.size(); i++) {
//				
//					int index = 0;
//					while(temp_links.get(i).getAirportID_to() != distances_from_start.get(index).getAirportID_to()) index++;
//					
//					boolean cancer = false;
//					
//					int temp_index = 0;
//					while(airports.get(temp_index).getAirportID() != distances_from_start.get(index).getAirportID_to()) temp_index++;
//					
//					String current_name = airports.get(temp_index).getIata();
//					if(avoid != null) {
//						for(int j = 0; j < avoid.length; j++) {
//							if(avoid[j].equals(current_name)) {
//								cancer = true;
//								break;
//							}
//						}
//					}
//					
//					if(
//						!cancer
//						&&
//						!distances_from_start.get(index).isVisited() 
//						&& 
//						(distances_from_start.get(index).getDistance() 
//						> 
//						(distances_from_start.get(start_index).getDistance() 
//						+ 
//						temp_links.get(i).getDistance())
//						)
//					) {
//						distances_from_start.get(index).setDistance(
//								distances_from_start.get(start_index).getDistance() 
//								+ 
//								temp_links.get(i).getDistance()
//						);
//						
//						if(!airportIDs_unchecked.contains(distances_from_start.get(index).getAirportID_to()))
//							airportIDs_unchecked.add(distances_from_start.get(index).getAirportID_to());
//						
//					}
//					
//				}
//				
//				distances_from_start.get(start_index).setVisited(true);
//				
//				airportIDs_unchecked.remove((Integer) distances_from_start.get(start_index).getAirportID_to());			
//							
//			} while(airportIDs_unchecked.size() != 0);
//						
//		} catch(Exception e1) {
//			System.out.println("Greska pri izvrsavanju Dijkstra algoritma.");
//			e1.printStackTrace();
//		}
//		
//		/**
//		 * Backtracking.
//		 */
//		
//		int end_index = 0;
//		while(!airports.get(end_index).getIata().equals(e)) end_index++;
//		
//		start_index = 0;
//		while(!airports.get(start_index).getIata().equals(s)) start_index++;
//		
//		for(int i = 0; i < airports.size(); i++) {
//			
//			ArrayList<Integer> revLinks = new ArrayList<Integer>();
//			
//			for(int j = 0; j < airports.size(); j++) {
//				for(int k = 0; k < airports.get(j).getLinks().size(); k++) {
//					if(i != j && airports.get(j).getLinks().get(k).getAirportID_to() == airports.get(i).getAirportID()) {
//						revLinks.add(airports.get(j).getAirportID());
//						break;
//					}
//				}
//			}			
//			
//			airports.get(i).setReverseLinks(revLinks);
//		
//		}
//		
//		ArrayList<String> from = new ArrayList<String>();
//		ArrayList<String> to = new ArrayList<String>();
//		ArrayList<Integer> distance = new ArrayList<Integer>();
//		do {
//			
//			ArrayList<Integer> temp_links = airports.get(end_index).getReverseLinks();
//
//			int index_of_closest = -1;
//			for(int i = 0; i < temp_links.size(); i++) {
//				
//				int index = 0;
//				while(temp_links.get(i) != distances_from_start.get(index).getAirportID_to()) index++;
//			
//				if(index_of_closest == -1 && distances_from_start.get(index).getDistance() != Integer.MAX_VALUE)
//					index_of_closest = index;
//				else if(index_of_closest != -1 && airports.get(index).getIata().equals(via)) {
//					index_of_closest = index;
//					break;
//				} else if(index_of_closest != -1 && distances_from_start.get(index_of_closest).getDistance()
//						> distances_from_start.get(index).getDistance()) index_of_closest = index;
//				
//			}
//
//			int index = 0;
//			while(airports.get(index).getAirportID() != distances_from_start.get(index_of_closest).getAirportID_to()) index++;
//			
//			from.add(airports.get(index).getCity());
//			to.add(airports.get(end_index).getCity());
//			
//			double p = 0.017453292519943295;
//			
//			double a = 0.5 - Math.cos((airports.get(end_index).getLatitude() - airports.get(index_of_closest).getLatitude()) * p)/2 + 
//					  Math.cos(airports.get(index_of_closest).getLatitude() * p) * Math.cos(airports.get(end_index).getLatitude() * p) * 
//			          (1 - Math.cos((airports.get(end_index).getLongitude() - airports.get(index_of_closest).getLongitude()) * p))/2;
//
//			double c = 12742 * Math.asin(Math.sqrt(a));
//			
//			distance.add(Math.abs((int) c));
//			
//			end_index = index_of_closest;
//					
//		} while(end_index != start_index);
//		
//		/**
//		 * Print.
//		 */
//		
//		for(int i = from.size() - 1; i > -1; i--) {
//
//			System.out.print(from.get(i));
//			System.out.print(" " + to.get(i));
//			System.out.print(" " + distance.get(i));
//			System.out.println();
//			
//		}
//		
//		int sum = 0;
//		for(int i = 0; i < distance.size(); i++) sum += distance.get(i);
//		
//		System.out.println("Ukupno " + sum);
//		
	}
	
}
