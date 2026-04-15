package project2.GUI;

import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.JXMapViewer;
import java.awt.event.MouseEvent;

public class getPointOnClick {

    public static void getPointCoordinates(JXMapViewer mapViewer, MouseEvent e,JourneyPlannerPanel panel, boolean[] isFromOrTo) {
        GeoPosition geoPosition = mapViewer.convertPointToGeoPosition(e.getPoint());
                double latitude = geoPosition.getLatitude();
                double longitude = geoPosition.getLongitude();

                if (isFromOrTo[0]) {
                    panel.getFromField().setText(latitude + " " + longitude); //write on FROM:
                } else {
                    panel.getToField().setText(latitude + " " + longitude); //write on TO:
                }
        System.out.println(latitude + " " + longitude); // Print the coordinates to the console
    }
    
    
}
