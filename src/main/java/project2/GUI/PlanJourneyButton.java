package project2.GUI;

import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import javax.swing.*;

import org.jxmapviewer.viewer.GeoPosition;
import project2.PathFinder;
import project2.Point;
import project2.RouteStep;

import static project2.GUI.MainFrame.layeredPane;
import static project2.GUI.MainFrame.mapViewer;

public class PlanJourneyButton extends JButton
{
    private Point startPoint;
    private Point endPoint;
    private JourneyPlannerPanel panel;
    private JourneyDisplay journeyDisplay;
    private JPanel previousStartPoint;
    private JPanel previousEndPoint;
    private final Image backgroundImage = new ImageIcon("src/main/resources/Plan Journey Button.png").getImage();
    private int fixedWidth = 220;
    private int fixedHeight = 64;

    public PlanJourneyButton(JourneyPlannerPanel panel, JourneyDisplay journeyDisplay)
    {
        this.panel = panel;
        this.journeyDisplay = journeyDisplay;

        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setPreferredSize(new Dimension(fixedWidth, fixedHeight));

        this.addActionListener(e -> handleJourneyButtonClick());
    }

    public void handleJourneyButtonClick()
    {
        try {
            String[] fromCoords = panel.getFromText().trim().split("\\s+");
            String[] toCoords = panel.getToText().trim().split("\\s+");
            String timeText = panel.getStartTime().trim();

            double fromLat = Double.parseDouble(fromCoords[0]);
            double fromLon = Double.parseDouble(fromCoords[1]);
            double toLat = Double.parseDouble(toCoords[0]);
            double toLon = Double.parseDouble(toCoords[1]);

            startPoint = new Point(fromLat, fromLon);
            endPoint = new Point(toLat, toLon);

            if (timeText.isEmpty() || timeText.equals("Start Time:")) {
                JOptionPane.showMessageDialog(null, "Please enter a valid start time in format: HH:mm");
                return;
            }

            LocalTime startTime = LocalTime.parse(timeText);
            updateMarkers(fromLat, fromLon, toLat, toLon);

            mapViewer.setZoom(8);
            mapViewer.setAddressLocation(new GeoPosition(fromLat, fromLon));

            calculateAndDisplay(startPoint, endPoint, startTime);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Please enter coordinates in format: lat lon (e.g. 49.599132 6.131420)");
            ex.printStackTrace();
        }
    }

    public void updateMarkers(double fromLat, double fromLon, double toLat, double toLon)
    {
        if (previousStartPoint != null)
        {
            layeredPane.remove(previousStartPoint);
            layeredPane.repaint();
        }

        if (previousEndPoint != null)
        {
            layeredPane.remove(previousEndPoint);
            layeredPane.repaint();
        }

        GeoPosition start = new GeoPosition(fromLat, fromLon);
        PointDrawer startDrawer = new PointDrawer(mapViewer, start, true);
        GeoPosition end = new GeoPosition(toLat, toLon);
        PointDrawer endDrawer = new PointDrawer(mapViewer, end, false);

        previousStartPoint = startDrawer.getMarkerPanel();
        previousEndPoint = endDrawer.getMarkerPanel();
    }

    public void calculateAndDisplay(Point startPoint, Point endPoint, LocalTime startTime)
    {
        List<RouteStep> fullRoute = PathFinder.findRoute(startPoint, endPoint, startTime);

        journeyDisplay.clearJourneyDisplay();
        journeyDisplay.setJourneyDisplay(fullRoute, startPoint, endPoint);

        JFrame stepsFrame = new JFrame("Your Journey Steps");
        stepsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        stepsFrame.setSize(400, 600);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        for (RouteStep step : fullRoute) {
            JPanel stepPanel = new JPanel(new BorderLayout());
            stepPanel.setBackground(new Color(240, 248, 255));
            stepPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.GRAY, 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

            String stepTitle = switch (step.getMode()) {
                case "walk" -> "Walk";
                case "ride" -> "Ride " + step.getRoute().getShortName() + " (" + step.getRoute().getHeadSign() + ")";
                default -> step.getMode();
            };

            JLabel titleLabel = new JLabel(stepTitle);
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

            JLabel detailLabel = new JLabel("Start: " + step.getStartTime() +
                    " | Duration: " + step.getDuration() + " min" +
                    (step.getStop() != null ? " | Stop: " + step.getStop() : "")
            );
            detailLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

            stepPanel.add(titleLabel, BorderLayout.NORTH);
            stepPanel.add(detailLabel, BorderLayout.SOUTH);
            contentPanel.add(stepPanel);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        stepsFrame.getContentPane().add(scrollPane);
        JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (mainFrame != null) {
            int x = mainFrame.getX() + mainFrame.getWidth() - 420;
            int y = mainFrame.getY() + 100;
            stepsFrame.setLocation(x, y);
        } else {
            stepsFrame.setLocationRelativeTo(null);
        }

        stepsFrame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, fixedWidth, fixedHeight, this);
    }
}