package project2;

import java.time.LocalTime;
import java.util.List;

public class GraphBuilder {
    
    public static Graph build(Data data) {
        Graph graph = new Graph();
        List<Trip> trips = data.getTrips();
        List<Stop> stops = data.getStops();

        // Add RideEdges from trips
        for (Trip trip : trips) {
            List<StopTime> stopTimes = trip.getStopTimes();
            for (int i = 0; i < stopTimes.size() - 1; i++) {
                StopTime fromStopTime = stopTimes.get(i);
                StopTime toStopTime = stopTimes.get(i + 1);
                RideEdge rideEdge = new RideEdge(
                    fromStopTime.getStop(),
                    toStopTime.getStop(),
                    fromStopTime.getDepartureTime(),
                    toStopTime.getArrivalTime(),
                    trip.getRouteInfo()
                );
                graph.addEdge(fromStopTime.getStop(), rideEdge);
            }
        }

        // Add WalkEdges (transfers)
        addWalkEdges(graph, stops);
        return graph;
    }

    // Add WalkEdges between stops within 1.0 km
    private static void addWalkEdges(Graph graph, List<Stop> stops) {
        double WALKING_THRESHOLD_KM = 1.0;
        for (Stop from : stops) {
            for (Stop to : stops) {
                if (from.equals(to)) continue;
                double distance = Haversine.distance(from.getPoint(), to.getPoint());
                if (distance <= WALKING_THRESHOLD_KM) {
                    int duration = (int) ((distance / 5.0) * 60);
                    WalkEdge walkEdge = new WalkEdge(
                        from, 
                        to, 
                        LocalTime.MIN, // Base departure time
                        LocalTime.MIN.plusMinutes(duration), // Base arrival time
                        duration
                    );
                    graph.addEdge(from, walkEdge);
                }
            }
        }
    }
}
