package project2.GUI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import project2.Point;
import project2.RouteStep;

public class JourneyDisplay extends JPanel {
    private Point start;
    private Point end;
    private List<RouteStep> routeSteps;
    private List<TransferCircle> transferCircles;
    private JXMapViewer mapViewer;

    public JourneyDisplay(JXMapViewer mapViewer) {
        this.mapViewer = mapViewer;
        setOpaque(false);
    }

    public void setJourneyDisplay(List<RouteStep> routeSteps, Point start, Point end) {
        this.routeSteps = routeSteps;
        this.start = start;
        this.end = end;
        this.transferCircles = new ArrayList<>();

        // Displays bus taken in the terminal
        // DELETE LATER!!!!
        String lastBusName = null;
        for (RouteStep step : routeSteps) {
            if ("ride".equals(step.getMode()) && step.getRoute() != null) {
                String currentBus = step.getRoute().getShortName();
                if (!currentBus.equals(lastBusName)) {
                    System.out.println("Bus taken: " + currentBus);
                    lastBusName = currentBus;
                }
            }
        }
        repaint();
    }

    public void clearJourneyDisplay() {
        this.routeSteps = null;
        if (transferCircles != null) {
            for (TransferCircle circles : transferCircles) {
                circles.remove();
            }
            transferCircles.clear();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (routeSteps == null || routeSteps.isEmpty()) {
            return;
        }

        Graphics2D graphics2D = (Graphics2D) g;
        Point previousPoint = start;
        RouteStep previousStep = null;

        BasicStroke solid = new BasicStroke(7f);
        BasicStroke dashed = new BasicStroke(7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f,
                new float[] { 15 },
                0);

        for(int i = 0; i < routeSteps.size(); i++){
            RouteStep step = routeSteps.get(i);
            
            int[] p1 = CoordinatesConverter.convertGeoToPixel(mapViewer, previousPoint.getLat(),
                    previousPoint.getLon());
            int[] p2 = CoordinatesConverter.convertGeoToPixel(mapViewer, step.getPoint().getLat(), step.getPoint().getLon());

            int x1 = p1[0];
            int y1 = p1[1];
            int x2 = p2[0];
            int y2 = p2[1];

            if (step.getMode().equals("walk")) {
                graphics2D.setColor(Color.BLUE);
                graphics2D.setStroke(dashed);
            } else if (step.getMode().equals("ride")) {
                graphics2D.setColor(Color.RED);
                graphics2D.setStroke(solid);
            }

            graphics2D.drawLine(x1, y1, x2, y2);

            // check for transfer
            if (previousStep != null && i < routeSteps.size()){
            boolean modeChanged = !previousStep.getMode().equals(step.getMode());
            boolean buschanged = previousStep.getMode().equals("ride") 
                                    && step.getMode().equals("ride") 
                                    && previousStep.getRoute() != null 
                                    && step.getRoute() != null 
                                    && !previousStep.getRoute().getShortName().equals(step.getRoute().getShortName());
            
            

                if (modeChanged || buschanged) {
                    GeoPosition transferPosition = new GeoPosition(previousPoint.getLat(), previousPoint.getLon());
                    TransferCircle circle = new TransferCircle(mapViewer, transferPosition);
                    transferCircles.add(circle);
                }
            }
            previousPoint = step.getPoint();
            previousStep = step;
        }
        graphics2D.dispose();
    }
}
