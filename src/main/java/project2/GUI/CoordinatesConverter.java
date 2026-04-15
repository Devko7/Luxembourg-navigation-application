package project2.GUI;

import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.JXMapViewer;
import java.awt.geom.Point2D;

import java.awt.*;

public class CoordinatesConverter
{
    public static int[] convertGeoToPixel(JXMapViewer mapViewer, double latitude, double longitude) {
        GeoPosition geoPosition = new GeoPosition(latitude, longitude);
        Point2D worldPoint = mapViewer.getTileFactory().geoToPixel(geoPosition, mapViewer.getZoom());
        Rectangle viewportBounds = mapViewer.getViewportBounds();

        int x = (int) (worldPoint.getX() - viewportBounds.getX()); //pixel latitude
        int y = (int) (worldPoint.getY() - viewportBounds.getY()); //pixel longitude

        return new int[] {x,y}; //returns pixel coordinates
    }
    public static double[] convertPixelToGeo(JXMapViewer mapViewer, int pixelX, int pixelY) {
        Rectangle viewportBounds = mapViewer.getViewportBounds();
        int zoom = mapViewer.getZoom();
    
        // Adjust pixel coords based on viewport
        int x = pixelX + viewportBounds.x;
        int y = pixelY + viewportBounds.y;
    
        GeoPosition geo = mapViewer.getTileFactory().pixelToGeo(new Point2D.Double(x, y), zoom); //converts pixel to geo position
        return new double[] { geo.getLatitude(), geo.getLongitude() };
    }
}
