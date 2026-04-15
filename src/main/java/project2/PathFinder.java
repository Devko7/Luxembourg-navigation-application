package project2;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class PathFinder {
    private static final Data data = Data.getInstance();
    private static final double WALKING_SPEED_KMH = 5.0;
    private static final double MAX_WALKING_DISTANCE_KM = 1.0;
    private static final DateTimeFormatter HHMM = DateTimeFormatter.ofPattern("HH:mm");
    
    // Finds a route between two points given a start time
    public static List<RouteStep> findRoute(Point from, Point to, LocalTime startTime) {
        Graph graph = data.getGraph();
        List<Stop> allStops = data.getStops();
        
        Stop startStop = findNearestStop(from, allStops);
        Stop endStop = findNearestStop(to, allStops);
        
        double directDistance = Haversine.distance(from, to);

        // Edge cases when no stops are found
        if (startStop == null && endStop == null) {
            int walkDuration = (int) Math.ceil((directDistance / WALKING_SPEED_KMH) * 60);
            return Arrays.asList(new RouteStep("walk", to, walkDuration, formatTime(startTime), null, null));
        } else if (startStop == null) {
            int walkDuration = (int) Math.ceil((Haversine.distance(from, to) / WALKING_SPEED_KMH) * 60);
            return Arrays.asList(new RouteStep("walk", to, walkDuration, formatTime(startTime), null, null));
        } else if (endStop == null) {
            int walkDuration = (int) Math.ceil((Haversine.distance(from, startStop.getPoint()) / WALKING_SPEED_KMH) * 60);
            return Arrays.asList(new RouteStep("walk", startStop.getPoint(), walkDuration, formatTime(startTime), startStop.getName(), null));
        }
        
        // Check if we can walk directly (under 500m)
        if (directDistance <= MAX_WALKING_DISTANCE_KM) {
            int walkDuration = (int) Math.ceil((directDistance / WALKING_SPEED_KMH) * 60);
            RouteStep walkStep = new RouteStep("walk", to, walkDuration, formatTime(startTime), null, null);
            return Arrays.asList(walkStep);
        }

        List<RouteStep> route = performAStar(from, to, startTime, startStop, endStop, graph, allStops);
        
        // If it didn't find a route, return a fallback route step
        if (route.isEmpty()) {
            double distance = Haversine.distance(from, startStop.getPoint());
            int duration = (int) Math.ceil((distance / WALKING_SPEED_KMH) * 60);
            return Arrays.asList(new RouteStep("walk", startStop.getPoint(), duration, formatTime(startTime), startStop.getName(), null));
        }

        return route;
    }

    // Finds a route given a new Graph (used in heatmap)
    public static List<RouteStep> findReroute(Point from, Point to, LocalTime startTime, Graph graph) {

        List<Stop> allStops = data.getStops();
        Stop startStop = findNearestStop(from, allStops);
        Stop endStop = findNearestStop(to, allStops)    ;


        double directDistance = Haversine.distance(from, to);

        // Edge cases when no stops are found
        if (startStop == null && endStop == null) {
            int walkDuration = (int) Math.ceil((directDistance / WALKING_SPEED_KMH) * 60);
            return Arrays.asList(new RouteStep("walk", to, walkDuration, formatTime(startTime), null, null));
        } else if (startStop == null) {
            int walkDuration = (int) Math.ceil((Haversine.distance(from, to) / WALKING_SPEED_KMH) * 60);
            return Arrays.asList(new RouteStep("walk", to, walkDuration, formatTime(startTime), null, null));
        } else if (endStop == null) {
            int walkDuration = (int) Math.ceil((Haversine.distance(from, startStop.getPoint()) / WALKING_SPEED_KMH) * 60);
            return Arrays.asList(new RouteStep("walk", startStop.getPoint(), walkDuration, formatTime(startTime), startStop.getName(), null));
        }

        // Check if we can walk directly (under 500m)
        if (directDistance <= MAX_WALKING_DISTANCE_KM) {
            int walkDuration = (int) Math.ceil((directDistance / WALKING_SPEED_KMH) * 60);
            RouteStep walkStep = new RouteStep("walk", to, walkDuration, formatTime(startTime), null, null);
            return Arrays.asList(walkStep);
        }

        List<RouteStep> route = performAStarReroute(from, to, startTime, startStop, endStop, graph, allStops);

        // If it didn't find a route, return a fallback route step
        if (route.isEmpty()) {
            double distance = Haversine.distance(from, startStop.getPoint());
            int duration = (int) Math.ceil((distance / WALKING_SPEED_KMH) * 60);
            return Arrays.asList(new RouteStep("walk", startStop.getPoint(), duration, formatTime(startTime), startStop.getName(), null));
        }

        return route;
    }
    
    private static List<RouteStep> performAStar(Point from, Point to, LocalTime startTime, Stop startStop, Stop endStop, Graph graph, List<Stop> allStops) {
        
        // Priority queue for A* (ordered by f-score: g + h)
        PriorityQueue<SearchNode> openSet = new PriorityQueue<>(Comparator.comparingInt(SearchNode::getFScore));
        
        Map<Stop, SearchNode> allNodes = new HashMap<>();
        Set<Stop> closedSet = new HashSet<>();
        
        SearchNode startNode = new SearchNode(startStop, startTime, 0, calculateHeuristic(startStop.getPoint(), to));
        startNode.addWalkToStop(from, startStop, startTime);
        
        openSet.add(startNode);
        allNodes.put(startStop, startNode);
        
        while (!openSet.isEmpty()) {
            SearchNode current = openSet.poll();
            
            if (current.stop.equals(endStop)) {
                // Found path to end stop, add final walk to destination
                current.addWalkFromStop(endStop, to);
                return current.reconstructPath();
            }
            
            closedSet.add(current.stop);
            
            // Explore all edges from current stop
            for (Edge edge : graph.getEdgesFrom(current.stop)) {

                if (edge instanceof RideEdge) {
                    if (edge.getDepartureTime().isBefore(startTime)) {
                        continue;
                    }
                }

                Stop neighbor = edge.getToStop();

                if (closedSet.contains(neighbor)) {
                    continue;
                }
                
                SearchNode neighborNode = exploreNeighbor(current, edge, to, allNodes);
                
                if (neighborNode != null && !openSet.contains(neighborNode)) {
                    openSet.add(neighborNode);
                }
            }
        }
        
        // No path found
        return Collections.emptyList();
    }

    // Performs A* algorithm accounting for closed stops
    private static List<RouteStep> performAStarReroute(Point from, Point to, LocalTime startTime, Stop startStop, Stop endStop, Graph graph, List<Stop> allStops) {

        // Priority queue for A* (ordered by f-score: g + h)
        PriorityQueue<SearchNode> openSet = new PriorityQueue<>(Comparator.comparingInt(SearchNode::getFScore));

        Map<Stop, SearchNode> allNodes = new HashMap<>();
        Set<Stop> closedSet = new HashSet<>();

        SearchNode startNode = new SearchNode(startStop, startTime, 0, calculateHeuristic(startStop.getPoint(), to));
        startNode.addWalkToStop(from, startStop, startTime);

        openSet.add(startNode);
        allNodes.put(startStop, startNode);

        while (!openSet.isEmpty()) {
            SearchNode current = openSet.poll();

            // Skip a path if it takes 31+ mins
            if (current.gScore > 31) {
                RouteStep cutoff = new RouteStep(
                        "cutoff",
                        current.stop.getPoint(),
                        31,
                        formatTime(current.arrivalTime),
                        null,
                        null
                );
                return Collections.singletonList(cutoff);
            }


            if (current.stop.equals(endStop)) {
                // Found path to end stop, add final walk to destination
                current.addWalkFromStop(endStop, to);
                return current.reconstructPath();
            }

            closedSet.add(current.stop);

            // Explore all edges from current stop
            for (Edge edge : graph.getEdgesFrom(current.stop)) {

                if (edge instanceof RideEdge) {
                    if (edge.getDepartureTime().isBefore(startTime)) {
                        continue;
                    }
                }

                Stop neighbor = edge.getToStop();

                if (closedSet.contains(neighbor)) {
                    continue;
                }

                SearchNode neighborNode = exploreNeighbor(current, edge, to, allNodes);

                if (neighborNode != null && !openSet.contains(neighborNode)) {
                    openSet.add(neighborNode);
                }
            }
        }

        // No path found
        return Collections.emptyList();
    }
    
    private static SearchNode exploreNeighbor(SearchNode current, Edge edge, Point destination, Map<Stop, SearchNode> allNodes) {
        Stop neighbor = edge.getToStop();

        int currentArrivalSecs = current.arrivalTime.toSecondOfDay();
        int schedDepSecs;
        int schedArrSecs;

        if (edge instanceof WalkEdge) {
            schedDepSecs = currentArrivalSecs;
            schedArrSecs = currentArrivalSecs + (edge.getDurationMinutes() * 60);
        } else {
            schedDepSecs = edge.getDepartureTime().toSecondOfDay();
            if (schedDepSecs < currentArrivalSecs) {
                schedDepSecs += 24 * 3600;
            }
            schedArrSecs = edge.getArrivalTime().toSecondOfDay();
            if (schedArrSecs < schedDepSecs) {
                schedArrSecs += 24 * 3600;
            }
        }

        int waitMins = (schedDepSecs - currentArrivalSecs) / 60;
        int travelMins = (schedArrSecs - schedDepSecs) / 60;
        int additionalCost = waitMins + travelMins;

        LocalTime newArrival = LocalTime.ofSecondOfDay(schedArrSecs % (24 * 3600));
        SearchNode existing = allNodes.get(neighbor);
        int tentativeG = current.gScore + additionalCost;

        if (existing == null || tentativeG < existing.gScore) {
            int h = calculateHeuristic(neighbor.getPoint(), destination);
            SearchNode node = new SearchNode(neighbor, newArrival, tentativeG, h);
            node.parent = current;
            node.parentEdge = edge;
            node.departureFromPrevious = LocalTime.ofSecondOfDay(schedDepSecs % (24 * 3600));
            allNodes.put(neighbor, node);
            return node;
        }

        return null;
    }
    
    // Heuristic - minimum time to reach destination by walking
    private static int calculateHeuristic(Point from, Point to) {
        double distance = Haversine.distance(from, to);
        return (int) Math.ceil((distance / WALKING_SPEED_KMH) * 60);
    }
    
    private static Stop findNearestStop(Point point, List<Stop> stops) {
        Stop nearest = null;
        double minDistance = Double.MAX_VALUE;
        
        for (Stop stop : stops) {
            double distance = Haversine.distance(point, stop.getPoint());
            if (distance <= MAX_WALKING_DISTANCE_KM && distance < minDistance) {
                minDistance = distance;
                nearest = stop;
            }
        }
        
        return nearest;
    }
    
    private static class SearchNode {
        Stop stop;
        LocalTime arrivalTime;
        int gScore; // Actual cost from start
        int hScore; // Heuristic cost to goal
        SearchNode parent;
        Edge parentEdge;
        LocalTime departureFromPrevious;
        List<RouteStep> routeSteps;
        
        public SearchNode(Stop stop, LocalTime arrivalTime, int gScore, int hScore) {
            this.stop = stop;
            this.arrivalTime = arrivalTime;
            this.gScore = gScore;
            this.hScore = hScore;
            this.routeSteps = new ArrayList<>();
        }
        
        public int getFScore() {
            return gScore + hScore;
        }
        
        public void addWalkToStop(Point from, Stop to, LocalTime startTime) {
            double distance = Haversine.distance(from, to.getPoint());
            int duration = (int) Math.ceil((distance / WALKING_SPEED_KMH) * 60);
            
            RouteStep walkStep = new RouteStep("walk", to.getPoint(), duration, formatTime(startTime), to.getName(), null);
            routeSteps.add(walkStep);
        }
        
        public void addWalkFromStop(Stop from, Point to) {
            double distance = Haversine.distance(from.getPoint(), to);
            int duration = (int) Math.ceil((distance / WALKING_SPEED_KMH) * 60);
            
            RouteStep walkStep = new RouteStep("walk", to, duration, formatTime(arrivalTime), null, null);
            routeSteps.add(walkStep);
        }
        
        public List<RouteStep> reconstructPath() {
            List<RouteStep> fullPath = new ArrayList<>();
            List<SearchNode> pathNodes = new ArrayList<>();
            
            // Collect all nodes in the path
            SearchNode current = this;
            while (current != null) {
                pathNodes.add(current);
                current = current.parent;
            }
            
            // Reverse to get path from start to end
            Collections.reverse(pathNodes);
            
            // Build route steps
            for (int i = 0; i < pathNodes.size(); i++) {
                SearchNode node = pathNodes.get(i);
                
                if (i == 0) {
                    // Add initial walk to first stop
                    if (!node.routeSteps.isEmpty()) {
                        fullPath.addAll(node.routeSteps);
                    }
                } else {
                    // Add step based on the edge used to reach this node
                    Edge edge = node.parentEdge;
                    
                    if (edge instanceof RideEdge) {
                        RideEdge rideEdge = (RideEdge) edge;
                        
                        RouteStep rideStep = new RouteStep("ride", 
                            edge.getToStop().getPoint(),
                            edge.getDurationMinutes(),
                            formatTime(node.departureFromPrevious),
                            edge.getToStop().getName(),
                            rideEdge.getRouteInfo());
                        fullPath.add(rideStep);
                        
                    } else if (edge instanceof WalkEdge) {
                        RouteStep walkStep = new RouteStep("walk",
                            edge.getToStop().getPoint(),
                            edge.getDurationMinutes(),
                            formatTime(node.departureFromPrevious),
                            edge.getToStop().getName(),
                            null);
                        fullPath.add(walkStep);
                    }
                }
            }
            
            // Add final walk from last stop to destination
            SearchNode lastNode = pathNodes.get(pathNodes.size() - 1);
            for (int i = 0; i < lastNode.routeSteps.size(); i++) {
                fullPath.add(lastNode.routeSteps.get(i));
            }
            
            return fullPath;
        }
    }

    // Returns total travel time of journey
    public static int calculateTotalTimeInMins(List<RouteStep> route) {
        int total = 0;

        for (RouteStep rs : route) {
            total += rs.getDuration();
        }

        return total;
    }

    public static String formatTime(LocalTime time) {
        return time.format(HHMM);
    }
}
