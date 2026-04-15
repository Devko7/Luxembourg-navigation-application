package project2;

import java.util.List;

// Represents a trip with its sequence of stops and times
public class Trip {
    private final String tripId;
    private final RouteInfo routeInfo;
    private final List<StopTime> stopTimes;

    public Trip(String tripId, RouteInfo routeInfo, List<StopTime> stopTimes) {
        this.tripId = tripId;
        this.routeInfo = routeInfo;
        this.stopTimes = stopTimes;
    }

    public String getTripId() { 
        return tripId; 
    }

    public RouteInfo getRouteInfo() {
        return routeInfo; 
    }

    public List<StopTime> getStopTimes() {
        return stopTimes; 
    }
}
