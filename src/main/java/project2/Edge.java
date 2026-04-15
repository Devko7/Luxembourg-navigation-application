package project2;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

// Represents a distance from one stop to another with an arrival and departure time
public abstract class Edge {
    protected final Stop fromStop;
    protected final Stop toStop;
    protected final LocalTime departureTime;
    protected final LocalTime arrivalTime;
    
    public Edge(Stop fromStop, Stop toStop, LocalTime departureTime, LocalTime arrivalTime) {
        this.fromStop = fromStop;
        this.toStop = toStop;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    public Stop getFromStop() {
        return this.fromStop;
    }

    public Stop getToStop() {
        return this.toStop;
    }

    public LocalTime getDepartureTime() {
        return this.departureTime;
    }

    public LocalTime getArrivalTime() {
        return this.arrivalTime;
    }

    public int getDurationMinutes() {
        return (int) ChronoUnit.MINUTES.between(departureTime, arrivalTime);
    }
}
