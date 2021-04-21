import java.text.DecimalFormat;
import java.util.Map;

public class Flight {
    private int id;
    private Location arrival;
    private Location departure;
    private int booked;
    private int capacity;
    private String weekday;

    public int getId() {
        return id;
    }
    public void reset(){
        booked = 0;
    }

    public String getArrivalTimeAtString(){
        getArrivalTime();
        String sHour,sMinute;
        if(aHour >= 10){
            sHour = String.valueOf(aHour);
        }else{
            sHour = "0" + String.valueOf(aHour);
        }
        if(aMinute >= 10){
            sMinute = String.valueOf(aMinute);
        }else{
            sMinute = "0" + String.valueOf(aMinute);
        }
        String ans = weekdays[aWeekNday].substring(0,3)+" "+sHour+":"+sMinute;
        return ans;
    }
    public String getDepartTimeAtString(){
        String sHour,sMinute;
        if(hour >= 10){
            sHour = String.valueOf(hour);
        }else{
            sHour = "0" + String.valueOf(hour);
        }
        if(minute >= 10){
            sMinute = String.valueOf(minute);
        }else{
            sMinute = "0" + String.valueOf(minute);
        }
        String ans = weekdays[weekNday].substring(0,3)+" "+sHour+":"+sMinute;
        return ans;
    }


    static String weekdays[] = new String[]{
           " ", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday",
    };

    public String getDespartTime(){
        return weekday + " " + hour + ":" + minute;
    }

    @Override
    public String toString() {
        DecimalFormat decimalFormat = new DecimalFormat("######.00"),dis = new DecimalFormat("###,###");
        String price = decimalFormat.format(getTicketPrice());

        String str = "Flight " + id + " \n" +
                "Departure: " + getDepartTimeAtString()+ " " + departure.getName() + " \n" +
                "Arrival: " + getArrivalTimeAtString() + " " + arrival.getName() + " \n" +
                "Distance: "+dis.format(getDistance())+"km \n" +
                "Duration: "+getDuration()+ " \n" +
                "Ticket Cost: $"+price+" \n" +
                "Passengers: "+booked+"/"+capacity+"\n";
        return str;
    }

    public int getWeekNday() {
        return weekNday;
    }

    public void setWeekNday(int weekNday) {
        this.weekNday = weekNday;
    }

    private int weekNday;

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    private int hour;
    private int minute;

    private int aWeekNday;
    private int aHour;
    private int aMinute;


    public int getDMinute(){
        return (weekNday*24 + hour)*60+minute;
    }
    public int getAMinute(){
        return (aWeekNday*24 + aHour)*60+aMinute;
    }

    public int getaHour() {
        return aHour;
    }

    public void setaHour(int aHour) {
        this.aHour = aHour;
    }

    public int getaMinute() {
        return aMinute;
    }

    public void setaMinute(int aMinute) {
        this.aMinute = aMinute;
    }

    public void getArrivalTime() {
        int m = (int) (getDistance() / 720 * 60);
        aMinute = minute + m;
        aHour = aMinute / 60 + hour;
        aMinute %= 60;
        aWeekNday = aHour / 24 + weekNday;
        aHour %= 24;
        aWeekNday %= 7;
        if (aWeekNday == 7) {
            aWeekNday = 7;
        }
    }

    public int getaWeekNday() {
        return aWeekNday;
    }

    public void setaWeekNday(int aWeekNday) {
        this.aWeekNday = aWeekNday;
    }

    public Flight(Location arrival, Location departure, String weekday, int hour, int minute, int weekNday, int capacity,int id) {
        this.arrival = arrival;
        this.departure = departure;
        this.weekday = weekday;
        this.hour = hour;
        this.minute = minute;
        this.capacity = capacity;
        this.id = id;
        this.weekNday = weekNday;
        getArrivalTime();
    }



    public Location getArrival() {
        return arrival;
    }

    public void setArrival(Location arrival) {
        this.arrival = arrival;
    }

    public Location getDeparture() {
        return departure;
    }

    public void setDeparture(Location departure) {
        this.departure = departure;
    }

    //get the number of minutes this flight takes (round to nearest whole number)
    public String getDuration() {
        int m = (int) (getDistance() / 720 * 60);
        int h = m/60;
        m = m%60;
        return h+"h "+m+"m";
    }

    //implement the ticket price formula
    public double getTicketPrice() {
        double x = 1.0 * booked / capacity;
        double y = 0;
        if (x <= 0.5) y = -0.4 * x + 1;
        else if (x <= 0.7) y = x + 0.3;
        else y = 0.2 / Math.PI * Math.atan(20 * x - 14) + 1;
        return y * getDistance() / 100 * (30 + 4 * (arrival.getDemand() - departure.getDemand()));
    }

    //book the given number of passengers onto this flight, returning the total cost
    public double book(int num) {
        if (num + booked > capacity) return -1;
        double sum = 0;
        for (int i = 0; i < num; i++) {
            sum += getTicketPrice();
            booked++;
        }
        return sum;
    }

    //return whether or not this flight is full
    public boolean isFull() {
        return booked == capacity;
    }

    //get the distance of this flight in km
    public double getDistance() {
        return arrival.distance(arrival, departure);
    }

    //get the layover time, in minutes, between two flights
    public static int layover(Flight x, Flight y) {
        return 0;
    }
}
