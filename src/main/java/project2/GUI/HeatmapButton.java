package project2.GUI;

import project2.*;
import project2.Point;

import java.awt.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

public class HeatmapButton extends JButton {
    private final Image backgroundImage = new ImageIcon("src/main/resources/Heatmap Button.png").getImage();
    private final List<Stop> removedStops = new ArrayList<>();
    private final List<Stop> undoStack = new ArrayList<>();
    private Point origin;
    private int fixedWidth = 220;
    private int fixedHeight = 70;
    private final HeatmapPanel heatmapPanel;
    private final JourneyPlannerPanel journeyPlannerPanel;

    public HeatmapButton(HeatmapPanel heatmapPanel) {
        this.heatmapPanel = heatmapPanel;
        this.journeyPlannerPanel = heatmapPanel.journeyPlannerPanel;

        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setPreferredSize(new Dimension(fixedWidth, fixedHeight));

        addActionListener(e -> HandleHeatmapClick());
    }

    public void HandleHeatmapClick() {
        try {
            String fromText = getFromText();
            String[] fromCoords = fromText.trim().split("\\s+");

            String fromLatStr = fromCoords[0];
            String fromLonStr = fromCoords[1];

            double fromLat = Double.parseDouble(fromLatStr);
            double fromLon = Double.parseDouble(fromLonStr);
            MainFrame.statsPanel.setVisible(true);
            MainFrame.layeredPane.moveToFront(MainFrame.statsPanel);
            MainFrame.statsPanel.getParent().revalidate();
            MainFrame.statsPanel.getParent().repaint();
            createHeatMap(fromLat, fromLon);
            
            System.out.println("statsPanel visible: " + MainFrame.statsPanel.isVisible());
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Please enter a starting point in format: lat lon (e.g., 49.61 6.11)",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void createHeatMap(double latitude, double longitude) {
        origin = new Point(latitude, longitude);

        List<Stop> allStops = Data.getInstance().getStops();
        Map<Stop, Integer> travelTimes = new HashMap<>();

        String timeString = journeyPlannerPanel.getStartTime().trim();
        LocalTime startTime = LocalTime.parse(timeString);

        Graph originalGraph = Data.getInstance().getGraph();
        Graph filteredGraph = cloneGraphWithoutStops(originalGraph, removedStops);


        for (Stop stop : allStops) {
            if (stop.getPoint().equals(origin) || removedStops.contains(stop))
                continue;

            List<RouteStep> route = PathFinder.findReroute(origin, stop.getPoint(), startTime, filteredGraph);

            if (route == null || route.isEmpty()) {
                continue;
            }

            int totalTime = PathFinder.calculateTotalTimeInMins(route);
            travelTimes.put(stop, totalTime);
        }

        Map<String, List<Stop>> colorGroups = new HashMap<>();
        colorGroups.put("green", new ArrayList<>());
        colorGroups.put("yellow", new ArrayList<>());
        colorGroups.put("orange", new ArrayList<>());
        colorGroups.put("darkorange", new ArrayList<>());
        colorGroups.put("red", new ArrayList<>());

        for (Map.Entry<Stop, Integer> entry : travelTimes.entrySet()) {
            Stop stop = entry.getKey();
            int time = entry.getValue();
            double dx = origin.getLat() - stop.getPoint().getLat();
            double dy = origin.getLon() - stop.getPoint().getLon();
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (time <= 5) {
                // the points that are unattainble are green because it takes 0 mins
                // so to rmemove that bug if more than 500 meters, cant be achicved in 5 mins, so red
                if (distance > 0.03) {
                    colorGroups.get("red").add(stop);
                } else {
                    colorGroups.get("green").add(stop);
                }
            } else if (time <= 10) {
                colorGroups.get("yellow").add(stop);
            } else if (time <= 20) {
                colorGroups.get("orange").add(stop);
            } else if (time <= 30) {
                colorGroups.get("darkorange").add(stop);
            } else {
                colorGroups.get("red").add(stop);
            }
        }

        Map<String, Integer> statsMap = new HashMap<>();
        statsMap.put("green", colorGroups.get("green").size());
        statsMap.put("yellow", colorGroups.get("yellow").size());
        statsMap.put("orange", colorGroups.get("orange").size());
        statsMap.put("red", colorGroups.get("red").size());
        statsMap.put("darkorange", colorGroups.get("darkorange").size());
        MainFrame.statsPanel.updateStats(statsMap);

        //remove existing overlays
        for (Component comp : MainFrame.layeredPane.getComponents()) {
            if (comp instanceof Heatmap) {
                MainFrame.layeredPane.remove(comp);
            }
        }

        Heatmap hm = new Heatmap(MainFrame.mapViewer, travelTimes, colorGroups, origin, this);
        MainFrame.layeredPane.add(hm, Integer.valueOf(2));
        MainFrame.layeredPane.revalidate();
        MainFrame.layeredPane.repaint();

        SwingUtilities.invokeLater(() -> hm.requestFocusInWindow());

    }

    // create a new grapn without the stops that have been removed to pass into pathfinder
    private Graph cloneGraphWithoutStops(Graph original, List<Stop> removedStops) {
        Graph newGraph = new Graph();
        for (Stop from : original.getAllStops()) {
            if (removedStops.contains(from)) continue;

            for (Edge edge : original.getEdgesFrom(from)) {
                Stop to = edge.getToStop();
                if (removedStops.contains(to)) continue;
                newGraph.addEdge(from, edge);
            }
        }
        return newGraph;
    }

    public void removeStop(Stop stop) {
        // if stop is already removed, dont re-remove (shouldnt happe but JIC)
        if (removedStops.contains(stop)) return;

        // temporarly try to remove the stop
        removedStops.add(stop);
        Graph filteredGraph = cloneGraphWithoutStops(Data.getInstance().getGraph(), removedStops);

        // generate heatmap to count how many green stops this would make
        Map<Stop, Integer> times = new HashMap<>();
        List<Stop> allStops = Data.getInstance().getStops();

        String timeString = journeyPlannerPanel.getStartTime().trim();
        LocalTime startTime = LocalTime.parse(timeString);

        int greenCount = 0;
        for (Stop s : allStops) {
            if (s.getPoint().equals(origin) || removedStops.contains(s)) continue;

            List<RouteStep> route = PathFinder.findReroute(origin, s.getPoint(), startTime, filteredGraph);
            if (route == null || route.isEmpty())
                continue;

            int totalTime = PathFinder.calculateTotalTimeInMins(route);
            times.put(s, totalTime);

            if (totalTime <= 5)
                greenCount++;
        }

       // if there are many greens (which means it was too close to origin and messed up
       // the pathfinder, therefore 'unremovable' give error and remove from REMOVED list
        if (greenCount >= 50) {
            removedStops.remove(stop);
            JOptionPane.showMessageDialog(null,
                    "Stop " + stop.getName() + " is too close to the start point and cannot be removed!",
                    "Blocked Stop Removal", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // else add it to the undo stack and recalculate heatmap
        undoStack.add(stop);
        createHeatMap(origin.getLat(), origin.getLon());
    }

    // if there is a stop that has been removed, remove it from the removed list and
    // recalculate the heatmap
    public void undoRemove() {
        if (!undoStack.isEmpty()) {
            Stop last = undoStack.remove(undoStack.size() - 1);
            removedStops.remove(last);
            createHeatMap(origin.getLat(), origin.getLon());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, fixedWidth, fixedHeight, this);
    }

    public String getFromText() {
        return journeyPlannerPanel.getFromText();
    }
}