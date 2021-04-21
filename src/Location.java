import java.util.ArrayList;
import java.util.Comparator;

public class Location {
	private String name;
	private double lat;
	private double lon;
	private double demand;
	private ArrayList<Flight> arrivalFlights;
	private ArrayList<Flight> departureFlights;
	public Location(String name, double lat, double lon, double demand) {
		departureFlights = new ArrayList<>();
		arrivalFlights = new ArrayList<>();
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.demand = demand;
	}

	@Override
	public String toString() {
		return "Location{}";
	}

	public ArrayList<Flight> getArrivalFlights() {
		return arrivalFlights;
	}

	public void setArrivalFlights(ArrayList<Flight> arrivalFlights) {
		this.arrivalFlights = arrivalFlights;
	}

	public ArrayList<Flight> getDepartureFlights() {
		return departureFlights;
	}

	public void setDepartureFlights(ArrayList<Flight> departureFlights) {
		this.departureFlights = departureFlights;
	}

	//Implement the Haversine formula - return value in kilometres
    public static double distance(Location l1, Location l2) {
		double a = l1.lat - l2.lat, b = l1.lon - l2.lon;
		return 2 * Math.asin(Math.sqrt(Math.sin(a/2)*Math.sin(a/2) + Math.cos(l1.lat)*Math.cos(l2.lat)*Math.sin(b/2)*Math.sin(b/2)))* 6378.137 * 1000;
    }


    public void addArrival(Flight f) {
		arrivalFlights.add(f);
		arrivalFlights.sort(new Comparator<Flight>() {
			@Override
			public int compare(Flight o1, Flight o2) {
				if(o1.getWeekNday() != o2.getWeekNday()) return (o1.getWeekNday() - o2.getWeekNday())*24*60;
				return o1.getHour()*60+o1.getMinute() - o2.getHour()*60-o2.getMinute();
			}
		});
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getDemand() {
		return demand;
	}

	public void setDemand(double demand) {
		this.demand = demand;
	}

	public void addDeparture(Flight f) {
		departureFlights.add(f);
		departureFlights.sort(new Comparator<Flight>() {
			@Override
			public int compare(Flight o1, Flight o2) {
				if(o1.getWeekNday() != o2.getWeekNday()) return (o1.getWeekNday() - o2.getWeekNday())*24*60;
				return o1.getHour()*60+o1.getMinute() - o2.getHour()*60-o2.getMinute();
			}
		});
	}
	
	/**
	 * Check to see if Flight f can depart from this location.
	 * If there is a clash, the clashing flight string is returned, otherwise null is returned.
	 * A conflict is determined by if any other flights are arriving or departing at this location within an hour of this flight's departure time.
	 * @param f The flight to check.
	 * @return "Flight <id> [departing/arriving] from <name> on <clashingFlightTime>". Return null if there is no clash.
	 */
	public String hasRunwayDepartureSpace(Flight f) {
		return null;
    }

    /**
	 * Check to see if Flight f can arrive at this location.
	 * A conflict is determined by if any other flights are arriving or departing at this location within an hour of this flight's arrival time.
	 * @param f The flight to check.
	 * @return String representing the clashing flight, or null if there is no clash. Eg. "Flight <id> [departing/arriving] from <name> on <clashingFlightTime>"
	 */
	public String hasRunwayArrivalSpace(Flight f) {
		return null;
    }
}
