import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class FlightScheduler {
    private Map<String, Integer> weekdayList;
    private Map<String, Location> locations;
    ArrayList<String> locationArrayList;
    private Map<Integer, Flight> flights;
    private static FlightScheduler instance;
    private int nextFlightId;
    private int nextLocationId;

    public static void main(String[] args) {
        instance = new FlightScheduler(args);
        instance.run();
    }

    public static FlightScheduler getInstance() {
        return instance;
    }

    private String getLocations() {
        String ans = "Locations (";
        locationArrayList.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        ans += locationArrayList.size()+"):\n";
        for (int i = 0; i < locationArrayList.size(); i++) {
            if(i != 0) ans += ", ";
            ans += locationArrayList.get(i);
        }
        return ans;
    }

    public FlightScheduler(String[] args) {
        nextFlightId = 0;
        nextLocationId = 0;
    }


    public void run() {
        Scanner in = new Scanner(System.in);
        // Do not use System.exit() anywhere in your code,
        // otherwise it will also exit the auto test suite.
        // Also, do not use static attributes otherwise
        // they will maintain the same values between testcases.
        String input;
        flights = new HashMap<>();
        locations = new HashMap<>();
        weekdayList = new HashMap<>();
        locationArrayList = new ArrayList<>();
        weekdayList.put("Monday", 1);
        weekdayList.put("Tuesday", 2);
        weekdayList.put("Wednesday", 3);
        weekdayList.put("Thursday", 4);
        weekdayList.put("Friday", 5);
        weekdayList.put("Saturday", 6);
        weekdayList.put("Sunday", 7);
        int cnt = 0;
        while (true) {
            if (cnt++ > 0) System.out.println("");
            System.out.print("User: ");
            input = in.nextLine();
            String[] s = input.split(" ");
            try {
                s[0] = s[0].toLowerCase(Locale.ROOT);
            }catch (Exception e){
                continue;
            }
            try {
                s[1] = s[1].toLowerCase(Locale.ROOT);
            } catch (Exception e) {
                ;
            }
            if (s[0].compareTo("exit") == 0) {
                System.out.println("Application closed.");
                break;
            }
            if (s[0].compareTo("flight") == 0) {
                if (s.length == 1) {
                    System.out.println("Usage:\nFLIGHT <id> [BOOK/REMOVE/RESET] " +
                            "[num]\nFLIGHT ADD <departure time> " +
                            "<from> <to> <capacity>\nFLIGHT " +
                            "IMPORT/EXPORT <filename>");
                    continue;
                }
                if (s[1].compareTo("add") == 0) {
                    if (s.length < 7) {
                        System.out.println(" Usage: FLIGHT ADD <departure time>" +
                                "<from> <to> <capacity>\nExample: FLIGHT" +
                                "ADD Monday 18:00 Sydney Melbourne 120 ");
                        continue;
                    }
                    Location arrival = locations.get(s[5]), departure = locations.get(s[4]);
                    if (departure == null) {
                        System.out.println("Invalid starting location.");
                        continue;
                    }
                    if (arrival == null) {
                        System.out.println("Invalid ending location.");
                    }
                    try {
                        Integer.valueOf(s[6]);
                    } catch (Exception e) {
                        System.out.println("Invalid positive integer capacity.");
                        continue;
                    }
                    String[] ts = s[3].split(":");
                    String hour = ts[0], minute = ts[1];
                    Flight flight = new Flight(arrival, departure, s[2], Integer.valueOf(hour), Integer.valueOf(minute), weekdayList.get(weekdayList), Integer.valueOf(s[6]), nextFlightId);
                    if (flight.getArrival().getName().compareTo(flight.getDeparture().getName()) == 0) {
                        System.out.println("Source and destination cannot be the same place.");
                        continue;
                    }

                    List<Flight> dDFlights = departure.getDepartureFlights();
                    List<Flight> dAFlights = departure.getArrivalFlights();
                    List<Flight> aDFlights = arrival.getDepartureFlights();
                    List<Flight> aAFlights = arrival.getArrivalFlights();
                    Flight cFlight = null;
                    int flag = 0;

                    for (Flight flight1 : dDFlights) {
                        if (flag > 0) break;
                        if (flight1.reduce(flight) <= 60 && flight1.reduce(flight) >= -60) {
                            flag = 1;
                            cFlight = flight1;
                        }
                    }
                    for (Flight flight1 : dAFlights) {
                        if (flag > 0) break;
                        if (flight1.reduceA(flight) <= 60 && flight1.reduceA(flight) >= -60) {
                            flag = 3;
                            cFlight = flight1;
                        }
                    }
                    for (Flight flight1 : aDFlights) {
                        if (flag > 0) break;
                        if (flight1.reduce(flight) <= 60 && flight1.reduce(flight) >= -60) {
                            flag = 2;
                            cFlight = flight1;
                        }
                    }
                    for (Flight flight1 : aAFlights) {
                        if (flag > 0) break;
                        if (flight1.reduceA(flight) <= 60 && flight1.reduceA(flight) >= -60) {
                            flag = 4;
                            cFlight = flight1;
                        }
                    }

                    switch (flag) {
                        case 0:
                            flights.put(nextFlightId++, flight);
                            System.out.println("Successfully added Flight " + flight.getId() + ".");
                            flight.getDeparture().getDepartureFlights().add(flight);
                            flight.getArrival().getArrivalFlights().add(flight);
                            break;
                        case 1:
                        case 2:
                            System.out.println("Scheduling conflict! This flight clashes with" +
                                    "Flight " + locationArrayList.indexOf(cFlight) + " departing at " + cFlight.getDeparture().getName() + " on " +
                                    cFlight.getWeekNday() + " " + cFlight.getHour() + ":" + cFlight.getMinute() + ".");
                            break;
                        case 3:
                        case 4:
                            System.out.println("Scheduling conflict! This flight clashes with" +
                                    "Flight " + locationArrayList.indexOf(cFlight) + " arriving at " + cFlight.getDeparture().getName() + " on " +
                                    weekdayList.get(cFlight.getaWeekNday()) + " " + cFlight.getaHour() + ":" + cFlight.getaMinute() + ".");
                            break;
                    }
                    continue;
                }
                int id = -1;
                try {
                    id = Integer.valueOf(s[1]);
                } catch (Exception e) {
                    ;
                }
                if (id >= 0) {
                    Flight flight;
                    try {
                        flight = flights.get(id);
                    } catch (Exception e) {
                        System.out.println("Invalid Flight ID.");
                        continue;
                    }
                    if (flight == null) {
                        System.out.println("Invalid Flight ID.");
                        continue;
                    }
                    //todo 航班的操作加在这里
                    if (s.length == 2) {
                        System.out.println(flight.toString());
                        continue;
                    }

                    s[2] = s[2].toLowerCase(Locale.ROOT);
                    if (s[2].compareTo("book") == 0) {
                        try {
                            int add = Integer.valueOf(s[3]);
                            double cost = flight.book(add);
                            //飞机已满
                            if (cost < 0) {
                                System.out.println("Flight is now full.");
                                continue;
                            }
                            //订票成功
                            DecimalFormat decimalFormat = new DecimalFormat("######.00");
                            System.out.println("Booked " + add + " passengers on flight " + id + " for a total cost of $" + decimalFormat.format(cost));
                            continue;
                        } catch (Exception e) {
                            //非整数
                            System.out.println("Invalid number of passengers to book.");
                            continue;
                        }
                    }
                    if (s[2].compareTo("reset") == 0) {
                        flight.reset();
                        System.out.println("Reset passengers booked to 0 for Flight " + flight.getId() + ", " + flight.getDespartTime() + " " + flight.getDeparture().getName() + " --> " +
                                "" + flight.getArrival().getName() + ".");
                        continue;
                    }
                    if (s[2].compareTo("remove") == 0) {
                        // TODO 移除航班
                        flights.remove(id);
                        continue;
                    }
                }

                //todo debug
                if (s[1].compareTo("import") == 0) {
                    try {
                        File csv = new File(s[2]);
                        BufferedReader textFile = new BufferedReader(new FileReader(csv));
                        String lineDta = "";
                        int ccnt = 0;
                        while ((lineDta = textFile.readLine()) != null) {
                            try {
                                String[] data = lineDta.split(",");
                                String[] rt = data[0].split(" ");
                                data[1] = data[1].toLowerCase(Locale.ROOT);
                                data[2] = data[2].toLowerCase(Locale.ROOT);
                                Location departure = locations.get(data[1]);
                                Location arrival = locations.get(data[2]);
                                Flight flight = new Flight(locations.get(data[2]), locations.get(data[1]),
                                        rt[0], Integer.valueOf(rt[1].split(":")[0]), Integer.valueOf(rt[1].split(":")[1]),
                                        weekdayList.get(rt[0]), Integer.valueOf(data[3]), nextFlightId);


                                List<Flight> dDFlights = departure.getDepartureFlights();
                                List<Flight> dAFlights = departure.getArrivalFlights();
                                List<Flight> aDFlights = arrival.getDepartureFlights();
                                List<Flight> aAFlights = arrival.getArrivalFlights();

                                if (flight.getArrival().getName().compareTo(flight.getDeparture().getName()) == 0) {
                                    continue;
                                }

                                int flag = 0;

                                for (Flight flight1 : dDFlights) {
                                    if (flag > 0) break;
                                    if (flight1.reduce(flight) <= 60 && flight1.reduce(flight) >= -60) {
                                        flag = 1;
                                    }
                                }
                                for (Flight flight1 : dAFlights) {
                                    if (flag > 0) break;
                                    if (flight1.reduceA(flight) <= 60 && flight1.reduceA(flight) >= -60) {
                                        flag = 3;
                                    }
                                }
                                for (Flight flight1 : aDFlights) {
                                    if (flag > 0) break;
                                    if (flight1.reduce(flight) <= 60 && flight1.reduce(flight) >= -60) {
                                        flag = 2;
                                    }
                                }
                                for (Flight flight1 : aAFlights) {
                                    if (flag > 0) break;
                                    if (flight1.reduceA(flight) <= 60 && flight1.reduceA(flight) >= -60) {
                                        flag = 4;
                                    }
                                }
                                if (flag == 0) {
                                        flights.put(nextFlightId++, flight);
                                }
                                ccnt++;
                                departure.getDepartureFlights().add(flight);
                                arrival.getArrivalFlights().add(flight);
                            }catch (Exception e){
                                ;
                            }

                        }
                        System.out.println("Imported "+ccnt+" flights.");
                        continue;

                    } catch (Exception e) {
                        ;
                    }
                }


                //没有匹配到任何参数
                System.out.println("Usage:\nFLIGHT <id> [BOOK/REMOVE/RESET] " +
                        "[num]\nFLIGHT ADD <departure time> " +
                        "<from> <to> <capacity>\nFLIGHT " +
                        "IMPORT/EXPORT <filename>");
                continue;
            }
            if(s[0].compareTo("flights") == 0){
                System.out.println("------------------------------------------------------- \n" +
                        "ID Departure Arrival Source --> Destination \n" +
                        "-------------------------------------------------------");
                if(flights.size() == 0){
                    System.out.println("(None)");
                    continue;
                }
                for(Map.Entry<Integer,Flight> entry : flights.entrySet()){
                    Flight flight = entry.getValue();
                    System.out.printf(" %d %s %s %s --> %s\n",entry.getKey()
                            ,flight.getDepartTimeAtString(),flight.getArrivalTimeAtString(),
                            flight.getDeparture().getName(),flight.getArrival().getName());
                }
                continue;
            }
            if (s[0].compareTo("location") == 0) {
                if (s.length == 1) {
                    System.out.println("Usage:\nLOCATION <name>\nLOCATION ADD " +
                            "<name> <latitude> <longitude> " +
                            "<demand_coefficient>\nLOCATION " +
                            "IMPORT/EXPORT <filename>");
                    continue;
                }
                s[1] = s[1].toLowerCase(Locale.ROOT);
                if (s[1].compareTo("add") == 0) {
                    if (s.length < 6) {
                        System.out.println("Usage: LOCATION ADD <name> <lat> <long> " +
                                "<demand_coefficient>\nExample: LOCATION " +
                                "ADD Sydney -33.847927 150.651786 0.2");
                        continue;
                    }
                    String t =new String(s[2]);
                    t = t.toLowerCase(Locale.ROOT);
                    Location location = locations.get(t);
                    if (location == null) {
                        double lat = Double.valueOf(s[3]);
                        double lon = Double.valueOf(s[4]);
                        double demand = Double.valueOf(s[5]);
                        if (Math.abs(lat) > 85) {
                            System.out.println("Invalid latitude. It must be a number of " +
                                    "degrees between -85 and +85.");
                            continue;
                        }
                        if (Math.abs(lon) > 180) {
                            System.out.println("Invalid longitude. It must be a number of " +
                                    "degrees between -180 and +180.");
                            continue;
                        }
                        if (Math.abs(demand) > 1) {
                            System.out.println("Invalid demand coefficient. It must be a " +
                                    "number between -1 and +1.");
                            continue;
                        }
                        System.out.println("Successfully added location " + s[2] + ".");
                        locations.put(t, new Location(s[2], lat, lon, demand));
                        locationArrayList.add(s[2]);
                        continue;
                    } else {
                        System.out.println("This location already exists.");
                    }

                }
                if (s[1].compareTo("import") == 0) {
                    int ccnt = 0;
                    try {
                        File csv = new File(s[2]);
                        BufferedReader textFile = new BufferedReader(new FileReader(csv));
                        String lineDta = "";
                        while ((lineDta = textFile.readLine()) != null) {
                            try {
                                String []data = lineDta.split(",");
                                Location location = locations.get(data[0]);
                                if (location == null) {
                                    double lat = Double.valueOf(data[1]);
                                    double lon = Double.valueOf(data[2]);
                                    double demand = Double.valueOf(data[3]);
                                    if (Math.abs(lat) > 85) {
                                        continue;
                                    }
                                    if (Math.abs(lon) > 180) {
                                        continue;
                                    }
                                    if (Math.abs(demand) > 1) {
                                        continue;
                                    }
                                    String t = new String(data[0]);
                                    t = t.toLowerCase(Locale.ROOT);
                                    locations.put(t, new Location(data[0], lat, lon, demand));
                                    locationArrayList.add(data[0]);
                                    ccnt++;
                                    continue;
                                }
                            } catch (Exception e) {
                                ;
                            }
                        }
                        System.out.println("Imported "+ccnt+" locations.");
                    } catch (Exception e) {
                        System.out.println("Error reading file.");
                    }

                    continue;
                }
                if (s[1].compareTo("export") == 0) {
                    try {
                        File file = new File(s[2]);
                    } catch (Exception e) {
//                        TODO 完成到处航班操作
                        System.out.println("Error writing file.");
                    }
                    continue;
                }
                s[1] = s[1].toLowerCase(Locale.ROOT);
                if (!locations.containsKey(s[1])) {
                    System.out.println("Invalid location name.");
                    continue;
                } else {
//                    TODO 完成location查询功能
                }
            }
            if (s[0].compareTo("locations") == 0) {
                System.out.println(getLocations());
            }
            if (s[0].compareTo("help") == 0) {
                System.out.println("FLIGHTS - list all available flights ordered by departure time, then departure location name \n" +
                        "FLIGHT ADD <departure time> <from> <to> <capacity> - add a flight \n" +
                        "FLIGHT IMPORT/EXPORT <filename> - import/export flights to csv file \n" +
                        "FLIGHT <id> - view information about a flight (from->to, departure arrival times, current ticket price, \n" +
                        "capacity, passengers booked) \n" +
                        "FLIGHT <id> BOOK <num> - book a certain number of passengers for the flight at the current ticket price, \n" +
                        "and then adjust the ticket price to reflect the reduced capacity remaining. If no number is given, book 1 \n" +
                        "passenger. If the given number of bookings is more than the remaining capacity, only accept bookings \n" +
                        "until the capacity is full. \n" +
                        "FLIGHT <id> REMOVE - remove a flight from the schedule \n" +
                        "FLIGHT <id> RESET - reset the number of passengers booked to 0, and the ticket price to its original state. \n" +
                        "LOCATIONS - list all available locations in alphabetical order \n" +
                        "LOCATION ADD <name> <lat> <long> <demand_coefficient> - add a location \n" +
                        "LOCATION <name> - view details about a location (it’s name, coordinates, demand coefficient) \n" +
                        "LOCATION IMPORT/EXPORT <filename> - import/export locations to csv file \n" +
                        "SCHEDULE <location_name> - list all departing and arriving flights, in order of the time they arrive/depart \n" +
                        "DEPARTURES <location_name> - list all departing flights, in order of departure time \n" +
                        "ARRIVALS <location_name> - list all arriving flights, in order of arrival time \n" +
                        "TRAVEL <from> <to> [sort] [n] - list the nth possible flight route between a starting location and \n" +
                        "destination, with a maximum of 3 stopovers. Default ordering is for shortest overall duration. If n is not \n" +
                        "provided, display the first one in the order. If n is larger than the number of flights available, display the \n" +
                        "last one in the ordering. \n" +
                        "can have other orderings: \n" +
                        "TRAVEL <from> <to> cost - minimum current cost \n" +
                        "TRAVEL <from> <to> duration - minimum total duration \n" +
                        "TRAVEL <from> <to> stopovers - minimum stopovers \n" +
                        "TRAVEL <from> <to> layover - minimum layover time \n" +
                        "TRAVEL <from> <to> flight_time - minimum flight time \n" +
                        "HELP – outputs this help string. \n" +
                        "EXIT – end the program.");
            }
        }

        // START YOUR CODE HERE
    }
}
