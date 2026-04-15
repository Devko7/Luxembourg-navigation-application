package project2.GUI;

import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

public class TransferCircle {
    private final MapObject transferCircle;
   

    public TransferCircle(JXMapViewer mapViewer, GeoPosition position) {
        ImageIcon orig = new ImageIcon(getClass().getResource("/pointIcon.png"));
        Image scaled = orig.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        Icon icon = new ImageIcon(scaled);

        transferCircle = new MapObject(mapViewer, position, icon, icon.getIconWidth(), icon.getIconHeight()); 
    }

    public JPanel getMarkerPanel(){
        return transferCircle.getPanel();
    }

    public void remove(){
        transferCircle.remove();
    }
}
