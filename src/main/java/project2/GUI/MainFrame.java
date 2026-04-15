package project2.GUI;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.ImageIcon;

import org.jxmapviewer.JXMapViewer;

public class MainFrame {
    public static final int MAX_ZOOM = 18;
    public static final int MIN_ZOOM = 0;
    public static JLayeredPane layeredPane = new JLayeredPane();
    public static JXMapViewer mapViewer = CreateJMapViewer.createMapViewer();
    public static HeatmapStatistics statsPanel = new HeatmapStatistics();

    public MainFrame(String[] args) {
        // initializing the UI
        JFrame frame = new JFrame("Map");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());

        JPanel mainPanel = new JPanel(new BorderLayout());
        frame.setContentPane(mainPanel);
        mainPanel.add(layeredPane, BorderLayout.CENTER);

        // Map with zoom controls
        AddZoomControls.addZoomControls(mapViewer, mainPanel);
        mapViewer.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        layeredPane.add(mapViewer, Integer.valueOf(1));

        // Journey Display Panel (for drawing route lines)
        JourneyDisplay journeyDisplay = new JourneyDisplay(mapViewer);
        journeyDisplay.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        layeredPane.add(journeyDisplay, Integer.valueOf(3));

        // Journey Planner input field
        JourneyPlannerPanel plannerPanel = new JourneyPlannerPanel(journeyDisplay);
        plannerPanel.setBounds(20, 20, 600, 400);
        layeredPane.add(plannerPanel, Integer.valueOf(2));
        // Heatmap Key
        ImageIcon heatmapKeyImage = new ImageIcon("src/main/resources/Heatmap_KeyV2.png");
        Image scaledImage = heatmapKeyImage.getImage().getScaledInstance(301, 147, Image.SCALE_SMOOTH);
        ImageIcon resizedHeatmapKey = new ImageIcon(scaledImage);
        JLabel heatmapKeyLabel = new JLabel(resizedHeatmapKey);
        heatmapKeyLabel.setBounds(-170, 650, heatmapKeyImage.getIconWidth(), heatmapKeyImage.getIconHeight());
        layeredPane.add(heatmapKeyLabel, Integer.valueOf(2));

        // Heatmap Panel with Button
        layeredPane.setLayout(null);
        HeatmapPanel heatmapPanel = new HeatmapPanel(plannerPanel);
        statsPanel.setBounds(frame.getWidth() - 260, 560, 240, 150);
        layeredPane.add(statsPanel, Integer.valueOf(3));
        statsPanel.setVisible(false);
        heatmapPanel.setBounds(20, 500, 550, 400);
        layeredPane.add(heatmapPanel, Integer.valueOf(2));

        boolean[] isFromOrTo = { true }; // gives an error if using a normal boolean
        mapViewer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    getPointOnClick.getPointCoordinates(mapViewer, e, plannerPanel, isFromOrTo);
                    isFromOrTo[0] = !isFromOrTo[0]; // to switch in between FROM and TO after a click
                }
            }
        });
        mapViewer.addMouseWheelListener(e -> {
            int wheelRotation = e.getWheelRotation();
            int currentZoom = mapViewer.getZoom();
            int newZoom = currentZoom + wheelRotation; //scroll backwards zooms out and scroll inwards zooms in

            //makes sure zoom level is in bounds
            newZoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, newZoom));

            if (newZoom != currentZoom) {
                mapViewer.setZoom(newZoom);
            }
        });

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame(args));
    }
}