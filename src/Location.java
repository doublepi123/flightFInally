import java.util.*;

public class Location {
	private String name;
	private double lat;
	private double lon;
	private double demand;
	private ArrayList<Flight> arrivalFlights;
	private ArrayList<Flight> departureFlights;
	public Location(String name, double lat, double lon, double demand) {
		this.lon = 0;
		this.lat = 0;
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

	public static double radians(double d) {
		return d * Math.PI / 180.0;
	}
	public List getSchedule(){
		List<Node> list = new ArrayList<>();
		for(Flight flight : departureFlights){
			list.add(new Node(flight.getDepartTimeAtString(), flight.getDMinute(), flight.getId(),1,flight.getArrival().getName()));
		}
		for(Flight flight: arrivalFlights){
			list.add(new Node(flight.getArrivalTimeAtString(),flight.getAMinute(), flight.getId(), 2,flight.getDeparture().getName()));
		}
		Collections.sort(list, new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				return o1.getMinute() - o2.getMinute();
			}
		});
		return list;
	}

	public List getDe(){
		List<Node> list = new ArrayList<>();
		for(Flight flight : departureFlights){
			list.add(new Node(flight.getDepartTimeAtString(), flight.getDMinute(), flight.getId(),1,flight.getArrival().getName()));
		}
		Collections.sort(list, new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				return o1.getMinute() - o2.getMinute();
			}
		});
		return list;
	}

	public List getAR(){
		List<Node> list = new ArrayList<>();
		for(Flight flight: arrivalFlights){
			list.add(new Node(flight.getArrivalTimeAtString(),flight.getAMinute(), flight.getId(), 2,flight.getDeparture().getName()));
		}
		Collections.sort(list, new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				return o1.getMinute() - o2.getMinute();
			}
		});
		return list;
	}

	public static double getDistanceFrom2LngLat(double lng1, double lat1, double lng2, double lat2) {
		//将角度转化为弧度
		double radLng1 = radians(lng1);
		double radLat1 = radians(lat1);
		double radLng2 = radians(lng2);
		double radLat2 = radians(lat2);

		double a = radLat1 - radLat2;
		double b = radLng1 - radLng2;

		return 2 * Math.asin(Math.sqrt(Math.sin(a / 2) * Math.sin(a / 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.sin(b / 2) * Math.sin(b / 2))) * 6371;
	}

	//Implement the Haversine formula - return value in kilometres
	public static double distance(Location l1, Location l2) {
		if (l1 == null || l2 == null) return 0;
		return getDistanceFrom2LngLat(l1.lon,l1.lat,l2.lon,l2.lat);
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
class Node {
	private String time;
	private int minute;
	private int id;


	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}


	private int type;

	public Node(String time, int minute, int id, int type, String city) {
		this.time = time;
		this.minute = minute;
		this.id = id;
		this.type = type;
		this.city = city;
	}

	private String city;
}
