package project2;

import java.time.LocalTime;

// Represents an edge with mode "ride"
public class RideEdge extends Edge {
    private final RouteInfo routeInfo;
    
    public RideEdge(Stop fromStop, Stop toStop, LocalTime departure, LocalTime arrival, RouteInfo routeInfo) {
        super(fromStop, toStop, departure, arrival);
        this.routeInfo = routeInfo;
    }

    public RouteInfo getRouteInfo() {
        return this.routeInfo;
    }
}
