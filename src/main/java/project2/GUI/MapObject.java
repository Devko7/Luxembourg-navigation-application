
package project2.GUI;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import javax.swing.Icon;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import static project2.GUI.MainFrame.layeredPane;

public class MapObject {
    private final JXMapViewer mapViewer;
    private final GeoPosition position;
    private final JPanel marker;

    public MapObject(JXMapViewer mapViewer, GeoPosition position, Icon icon, int width, int height) {
        this.mapViewer = mapViewer;
        this.position = position;

        marker = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                icon.paintIcon(this, g, 0, 0);
            }
        };

        // get rid of white bg
        marker.setOpaque(false);
        marker.setSize(width, height);
        layeredPane.add(marker, JLayeredPane.PALETTE_LAYER);

        // repositions the point for zoom and panning
        mapViewer.addPropertyChangeListener(evt -> {
            if ("zoom".equals(evt.getPropertyName()) ||
                    "centerPosition".equals(evt.getPropertyName())) {
                updatePosition();
            }
        });
        updatePosition();
    }

    private void updatePosition() {
        Point2D world = mapViewer.getTileFactory().geoToPixel(position, mapViewer.getZoom());
        Rectangle viewport = mapViewer.getViewportBounds();
        int x = (int) (world.getX() - viewport.getX()) - marker.getWidth() / 2;
        int y = (int) (world.getY() - viewport.getY()) - marker.getHeight() / 2;
        marker.setLocation(x, y);
        marker.repaint();
    }

    public void remove(){
        layeredPane.remove(marker);
        layeredPane.repaint();
    }

    public JPanel getPanel(){
        return marker;
    }
}

