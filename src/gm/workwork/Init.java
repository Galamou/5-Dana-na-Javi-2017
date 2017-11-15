package gm.workwork;

import java.io.FileReader;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.apache.commons.cli.*;

/**
 * @author Marko Males
 *
 */

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
		
	}
	
}
