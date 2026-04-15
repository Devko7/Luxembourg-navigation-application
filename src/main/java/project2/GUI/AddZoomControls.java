package project2.GUI;

import org.jxmapviewer.JXMapViewer;

import javax.swing.*;

import static project2.GUI.MainFrame.MAX_ZOOM;
import static project2.GUI.MainFrame.MIN_ZOOM;

import java.awt.*;

public class AddZoomControls
{
    public static void addZoomControls(JXMapViewer mapViewer, JPanel mainPanel)
    {
        JPanel zoomPanel = new JPanel();
        zoomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        zoomPanel.setOpaque(false);

        JButton zoomInButton = new JButton("-");
        zoomInButton.setFont(new Font("Arial", Font.PLAIN, 24));
        zoomInButton.setFocusable(false);
        zoomInButton.addActionListener(e -> {
            int currentZoom = mapViewer.getZoom();
            if (currentZoom < MAX_ZOOM) {
                mapViewer.setZoom(currentZoom + 1);
            }
        });

        JButton zoomOutButton = new JButton("+");
        zoomOutButton.setFont(new Font("Arial", Font.PLAIN, 24));
        zoomOutButton.setFocusable(false);
        zoomOutButton.addActionListener(e -> {
            int currentZoom = mapViewer.getZoom();
            if (currentZoom > MIN_ZOOM) {
                mapViewer.setZoom(currentZoom - 1);
            }
        });

        zoomPanel.add(zoomInButton);
        zoomPanel.add(zoomOutButton);

        mainPanel.add(zoomPanel, BorderLayout.SOUTH);
    }
}
