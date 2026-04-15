package project2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Represents the whole city as a graph with stops for vertices and edges for trips
public class Graph {
    private final Map<Stop, List<Edge>> adjacencyList = new HashMap<>();

    public void addEdge(Stop fromStop, Edge edge) {
        adjacencyList.computeIfAbsent(fromStop, k -> new ArrayList<>()).add(edge);
    }

    public List<Edge> getEdgesFrom(Stop stop) {
        return adjacencyList.getOrDefault(stop, Collections.emptyList());
    }

    public Set<Stop> getAllStops() {
        return adjacencyList.keySet();
    }
}