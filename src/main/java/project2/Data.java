package project2;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Data {
    private List<Stop> stops;
    private Map<String,Stop> stopMap;
    private List<RouteInfo> routeInfos;
    private List<Trip> trips;
    private Graph graph;
    private static Data instance = null;

    private Data() {
        this.stops = GetDataFromDB.getStops();
        this.stopMap  = this.stops.stream().collect(Collectors.toMap(Stop::getId, s->s));
        this.trips = GetDataFromDB.getTrips(this.stopMap);
        this.routeInfos = GetDataFromDB.getRouteInfos();
        this.graph = GraphBuilder.build(this);
    }

    public static Data getInstance() {
        if (instance == null) {
            instance = new Data();
        }
        return instance;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public List<RouteInfo> getRouteInfos() {
        return routeInfos;
    }

    public List<Trip> getTrips() { 
        return trips; 
    }

    public Graph getGraph() { 
        return graph; 
    }

    // Call to build the graph
    public void initializeGraph() {
        this.graph = GraphBuilder.build(this);
    }

    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }

    public void setRouteInfos(List<RouteInfo> routeInfos) {
        this.routeInfos = routeInfos;
    }
}
