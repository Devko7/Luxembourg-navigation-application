package project2;

import java.time.LocalTime;

// Represents a stop’s arrival/departure time in a trip
public class StopTime {
    private final Stop stop;
    private final LocalTime arrivalTime;
    private final LocalTime departureTime;

    public StopTime(Stop stop, LocalTime arrivalTime, LocalTime departureTime) {
        this.stop = stop;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
    }

    public Stop getStop() { 
        return stop;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime; 
    }
    
    public LocalTime getDepartureTime() {
        return departureTime; 
    }
}
