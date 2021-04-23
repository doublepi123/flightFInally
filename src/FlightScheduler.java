import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;

public class FlightScheduler {
    private Map<String, Integer> weekdayList;
    private Map<Integer, String> listWeekday;
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

    private int checkAddFlight(Flight flight) {
        Flight cFlight = null;
        Location departure = flight.getDeparture();
        Location arrival = flight.getArrival();
        List<Flight> dDFlights = departure.getDepartureFlights();
        List<Flight> dAFlights = departure.getArrivalFlights();
        List<Flight> aDFlights = arrival.getDepartureFlights();
        List<Flight> aAFlights = arrival.getArrivalFlights();
        int flag = 0;

        for (Flight flight1 : dDFlights) {
            if (flag > 0) break;
            if (Math.abs(flight.getDMinute() - flight1.getDMinute()) < 60) {
                flag = 1;
                cFlight = flight1;
            }
        }
        for (Flight flight1 : dAFlights) {
            if (flag > 0) break;
            if (Math.abs(flight1.getAMinute() - flight.getDMinute()) < 60) {
                flag = 3;
                cFlight = flight1;
            }
        }
        for (Flight flight1 : aDFlights) {
            if (flag > 0) break;
            if (Math.abs(flight1.getDMinute() - flight.getAMinute()) < 60) {
                flag = 2;
                cFlight = flight1;
            }
        }
        for (Flight flight1 : aAFlights) {
            if (flag > 0) break;
            if (Math.abs(flight.getAMinute() - flight1.getAMinute()) < 60) {
                flag = 4;
                cFlight = flight1;
            }
        }
        return flag > 0 ? flag + cFlight.getId() * 10 : flag;
    }

    private String getLocations() {
        String ans = "Locations (";
        locationArrayList.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        ans += locationArrayList.size() + "):\n";
        for (int i = 0; i < locationArrayList.size(); i++) {
            if (i != 0) ans += ", ";
            ans += locationArrayList.get(i);
        }
        if (locations.size() > 0) ans += "\n";
        return ans;
    }

    public FlightScheduler(String[] args) {
        nextFlightId = 0;
        nextLocationId = 0;
    }


    public void run() {
        //debug
        try {
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
            listWeekday = new HashMap<>();
            weekdayList.put("Monday", 1);
            weekdayList.put("Tuesday", 2);
            weekdayList.put("Wednesday", 3);
            weekdayList.put("Thursday", 4);
            weekdayList.put("Friday", 5);
            weekdayList.put("Saturday", 6);
            weekdayList.put("Sunday", 7);
            listWeekday.put(Integer.valueOf(1), "Monday");
            listWeekday.put(Integer.valueOf(2), "Tuesday");
            listWeekday.put(Integer.valueOf(3), "Wednesday");
            listWeekday.put(Integer.valueOf(4), "Thursday");
            listWeekday.put(Integer.valueOf(5), "Friday");
            listWeekday.put(Integer.valueOf(6), "Saturday");
            listWeekday.put(Integer.valueOf(7), "Sunday");
            int cnt = 0;
            while (true) {
                if (cnt++ > 0) System.out.println("");
                System.out.print("User: ");
                input = in.nextLine();
                ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
                Scanner iin = new Scanner(inputStream);
                String[] pppp = new String[100];
                int ppppint = 0;
                for (int i = 0; iin.hasNext(); i++) {
                    pppp[i] = iin.next();
                    ppppint++;
                }
                String[] s = new String[ppppint];
                for (int i = 0; i < ppppint; i++) {
                    s[i] = pppp[i];
                }
                try {
                    s[0] = s[0].toLowerCase(Locale.ROOT);
                } catch (Exception e) {
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
                            System.out.println("Usage:   FLIGHT ADD <departure time> " +
                                    "<from> <to> <capacity>\nExample: FLIGHT " +
                                    "ADD Monday 18:00 Sydney Melbourne 120");
                            continue;
                        }
                        s[4] = s[4].toLowerCase(Locale.ROOT);
                        s[5] = s[5].toLowerCase(Locale.ROOT);
                        Location arrival = locations.get(s[5]), departure = locations.get(s[4]);
                        if (departure == null) {
                            System.out.println("Invalid starting location.");
                            continue;
                        }
                        if (arrival == null) {
                            System.out.println("Invalid ending location.");
                            continue;
                        }
                        try {
                            Integer.valueOf(s[6]);
                        } catch (Exception e) {
                            System.out.println("Invalid positive integer capacity.");
                            continue;
                        }

                        String[] ts = s[3].split(":");
                        String hour = ts[0], minute = ts[1];
                        s[2] = s[2].substring(0, 1).toUpperCase() + s[2].substring(1).toLowerCase(Locale.ROOT);
                        Flight flight = new Flight(arrival, departure, s[2], Integer.valueOf(hour), Integer.valueOf(minute), weekdayList.get(s[2]), Integer.valueOf(s[6]), 0, nextFlightId);
                        if (flight.getArrival().getName().compareTo(flight.getDeparture().getName()) == 0) {
                            System.out.println("Source and destination cannot be the same place.");
                            continue;
                        }
                        Flight cFlight = null;
                        int flag = checkAddFlight(flight);
                        if (flag > 0) {
                            cFlight = flights.get(flag / 10);
                            flag %= 10;
                        }
                        // todo add
                        String de = "", ar = "";
                        try {
                            de = cFlight.getDepartTimeAtString();
                            ar = cFlight.getArrivalTimeAtString();
                            de = listWeekday.get(flight.getWeekNday()) + " " + de.split(" ")[1];
                            ar = listWeekday.get(flight.getaWeekNday()) + " " + ar.split(" ")[1];
                        } catch (Exception e) {
                            ;
                        }
                        ;

                        if (flag == 0) {
                            flights.put(nextFlightId++, flight);
                            System.out.println("Successfully added Flight " + flight.getId() + ".");
                            flight.getDeparture().getDepartureFlights().add(flight);
                            flight.getArrival().getArrivalFlights().add(flight);
                        } else if (flag == 1 || flag == 2) {
                            System.out.println("Scheduling conflict! This flight clashes with" +
                                    " Flight " + cFlight.getId() + " departing from " + cFlight.getDeparture().getName() + " on " +
                                    de + ".");
                        } else if (flag == 3 || flag == 4) {
                            System.out.println("Scheduling conflict! This flight clashes with" +
                                    " Flight " + cFlight.getId() + " arriving at " + cFlight.getArrival().getName() + " on " +
                                    ar + ".");
                        }
                        continue;
                    }
                    int id = (int) -1e9 - 10;
                    try {
                        id = Integer.valueOf(s[1]);
                    } catch (Exception e) {
                        double d = -1e9 - 10;
                        try {
                            d = Double.valueOf(s[1]);
                        } catch (Exception ee) {
                            if (d > -1e9) {
                                System.out.println("Invalid Flight ID.");
                                continue;
                            }
                        }
                    }
                    if (id > -1e9) {
                        Flight flight;
                        try {
                            flight = flights.get(id);
                            if (flight == null) {
                                System.out.println("Invalid Flight ID.");
                                continue;
                            }
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
                            try {
                                Flight flight1 = flights.get(id);
                                flights.remove(id);
                                flight1.getArrival().getArrivalFlights().remove(flight1);
                                flight1.getDeparture().getDepartureFlights().remove(flight1);
                                System.out.printf("Removed Flight %d, %s %s --> %s, from the flight \n" +
                                        "schedule.",flight1.getId(),flight1.getDepartTimeAtString(),flight1.getDeparture().getName(),flight1.getArrival().getName());
                            } catch (Exception e) {
                                System.out.println("Invalid Flight ID.");
                            }
                            continue;
                        }
                    }

                    //todo debug
                    if (s[1].compareTo("export") == 0) {
                        try {
                            BufferedWriter out = new BufferedWriter(new FileWriter(s[2]));
                            String str = "";
                            for (Map.Entry entry : flights.entrySet()) {
                                Flight flight = (Flight) entry.getValue();
                                str += listWeekday.get(flight.getWeekday()) + " " + flight.getDepartTimeAtString().split(" ")[1] + "," + flight.getDeparture().getName() +
                                        "," + flight.getArrival().getName() + "," + flight.getCapacity() + "," + flight.getBooked() + "\n";
                            }
                            out.write(str);
                            System.out.printf("Exported %d flights.\n", locations.size());
                        } catch (Exception e) {
                            System.out.println("Error writing file.");
                        }
                        continue;
                    }
                    if (s[1].compareTo("import") == 0) {
                        try {
                            File csv = new File(s[2]);
                            BufferedReader textFile = new BufferedReader(new FileReader(csv));
                            String lineDta = "";
                            int pp = 0;
                            int ccnt = 0;
                            while ((lineDta = textFile.readLine()) != null) {
                                try {
                                    pp++;
                                    String[] data = lineDta.split(",");
                                    String[] rt = data[0].split(" ");
                                    data[1] = data[1].toLowerCase(Locale.ROOT);
                                    data[2] = data[2].toLowerCase(Locale.ROOT);
                                    Location departure = locations.get(data[1]);
                                    Location arrival = locations.get(data[2]);
                                    Flight flight = new Flight(locations.get(data[2]), locations.get(data[1]),
                                            rt[0], Integer.valueOf(rt[1].split(":")[0]), Integer.valueOf(rt[1].split(":")[1]),
                                            weekdayList.get(rt[0]), Integer.valueOf(data[3]), Integer.valueOf(data[4]), nextFlightId);


                                    List<Flight> dDFlights = departure.getDepartureFlights();
                                    List<Flight> dAFlights = departure.getArrivalFlights();
                                    List<Flight> aDFlights = arrival.getDepartureFlights();
                                    List<Flight> aAFlights = arrival.getArrivalFlights();

                                    if (flight.getArrival().getName().compareTo(flight.getDeparture().getName()) == 0) {
                                        continue;
                                    }
                                    Flight cFlight = null;
                                    int flag = checkAddFlight(flight);
                                    if (flag > 0) {
                                        cFlight = flights.get(flag / 10);
                                        flag %= 10;
                                    }
                                    if (flag == 0) {
                                        flights.put(nextFlightId++, flight);
                                    }
                                    ccnt++;
                                    departure.getDepartureFlights().add(flight);
                                    arrival.getArrivalFlights().add(flight);
                                } catch (Exception e) {
                                    ;
                                }

                            }
                            System.out.println("Imported " + ccnt + " flights.");
                            if (pp - ccnt > 0) {
                                if (pp - ccnt == 1) {
                                    System.out.println("1 line was invalid.");
                                } else
                                    System.out.println((pp - ccnt) + " lines were invalid.");
                            }
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
                if (s[0].compareTo("flights") == 0) {
                    System.out.println("Flights");
                    System.out.println("-------------------------------------------------------\n" +
                            "ID   Departure   Arrival     Source --> Destination\n" +
                            "-------------------------------------------------------");
                    int ppppp = 0;
                    for (Map.Entry<Integer, Flight> entry : flights.entrySet()) {
                        ppppp++;
                        Flight flight = entry.getValue();
                        System.out.printf("   %d %s   %s   %s --> %s\n", entry.getKey()
                                , flight.getDepartTimeAtString(), flight.getArrivalTimeAtString(),
                                flight.getDeparture().getName(), flight.getArrival().getName());
                    }
                    if (ppppp == 0) System.out.println("(None)");
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
                            System.out.println("Usage:   LOCATION ADD <name> <lat> <long> " +
                                    "<demand_coefficient>\nExample: LOCATION " +
                                    "ADD Sydney -33.847927 150.651786 0.2");
                            continue;
                        }
                        String t = new String(s[2]);
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
                        int pp = 0;
                        try {
                            File csv = new File(s[2]);
                            BufferedReader textFile = new BufferedReader(new FileReader(csv));
                            String lineDta = "";
                            while ((lineDta = textFile.readLine()) != null) {
                                try {
                                    pp++;
                                    String[] data = lineDta.split(",");
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
                            System.out.println("Imported " + ccnt + " locations.");
                        } catch (Exception e) {
                            System.out.println("Error reading file.");
                        }
                        if (pp - ccnt == 1) System.out.println("1 line was invalid.");
                        else if (pp - ccnt > 1) System.out.println((pp - ccnt) + " lins were invalid.");
                        continue;
                    }
                    if (s[1].compareTo("export") == 0) {
                        try {
                            BufferedWriter out = new BufferedWriter(new FileWriter(s[2]));
                            String str = "";
                            for (Map.Entry entry : locations.entrySet()) {
                                Location location = (Location) entry.getValue();
                                str += location.getName() + "," + location.getLat() + "," + location.getLon() + "," + location.getDemand() + "\n";
                            }
                            out.write(str);
                            System.out.printf("Exported %d locations.\n", locations.size());
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
                    System.out.print(getLocations());
                    if (locationArrayList.size() == 0) {
                        System.out.println("(None)");
                    }
                }

                if (s[0].compareTo("schedule") == 0) {
                    try {
                        s[1] = s[1].toLowerCase(Locale.ROOT);
                        Location location = locations.get(s[1]);
                        System.out.println("Sydney\n" +
                                "-------------------------------------------------------\n" +
                                "ID   Time        Departure/Arrival to/from Location\n" +
                                "-------------------------------------------------------\n");
                        List<Node> list = location.getSchedule();
                        String[] choice = {
                                "", "Departure to", "Arrival from",
                        };
                        for (Node node : list) {
                            System.out.printf(" %d %s %s %s\n", node.getId(), node.getTime(), choice[node.getType()], node.getCity());
                        }
                    } catch (Exception e) {
                        System.out.println("This location does not exist in the system.");
                        continue;
                    }
                }

                if (s[0].compareTo("arrivals") == 0) {
                    try {
                        s[1] = s[1].toLowerCase(Locale.ROOT);
                        Location location = locations.get(s[1]);
                        System.out.println("" + location.getName() + " \n" +
                                "-------------------------------------------------------\n" +
                                "ID     Time         Departure/Arrival to/from Location \n" +
                                "-------------------------------------------------------\n");
                        List<Node> list = location.getAR();
                        String[] choice = {
                                "", "Departure to", "Arrival from",
                        };
                        for (Node node : list) {
                            System.out.printf(" %d %s %s %s\n", node.getId(), node.getTime(), choice[node.getType()], node.getCity());
                        }
                    } catch (Exception e) {
                        System.out.println("This location does not exist in the system.");
                        continue;
                    }
                }

                if (s[0].compareTo("departures") == 0) {
                    try {
                        s[1] = s[1].toLowerCase(Locale.ROOT);
                        Location location = locations.get(s[1]);
                        System.out.println("" + location.getName() + " \n" +
                                "-------------------------------------------------------\n" +
                                "ID     Time         Departure/Arrival to/from Location \n" +
                                "-------------------------------------------------------\n");
                        List<Node> list = location.getDe();
                        String[] choice = {
                                "", "Departure to", "Arrival from",
                        };
                        for (Node node : list) {
                            System.out.printf(" %d %s %s %s\n", node.getId(), node.getTime(), choice[node.getType()], node.getCity());
                        }
                    } catch (Exception e) {
                        System.out.println("This location does not exist in the system.");
                        continue;
                    }
                }
                if (s[0].compareTo("help") == 0) {
                    System.out.println("FLIGHTS - list all available flights ordered by departure time, then departure location name\n" +
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
                    continue;
                }
//                System.out.println("Invalid command. Type 'help' for a list of commands.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // START YOUR CODE HERE
    }
}
