package project2.GUI;

import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

public class PointDrawer {
    private final MapObject pointDrawer;

    public PointDrawer(JXMapViewer mapViewer, GeoPosition pos, boolean start) {
        // if true, start, else, end point
        String path = start
            ? "src/main/resources/map point start.png"
            : "src/main/resources/map point end.png";
        ImageIcon orig = new ImageIcon(path);
        Image scaled = orig.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
        Icon icon = new ImageIcon(scaled);

        pointDrawer = new MapObject(mapViewer, pos, icon, icon.getIconWidth(), icon.getIconHeight());
    }

    public JPanel getMarkerPanel(){
        return pointDrawer.getPanel();
    }

    public void remove(){
        pointDrawer.remove();
    }
}
