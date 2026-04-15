package project2.GUI;

import org.jxmapviewer.JXMapViewer;

import project2.Point;

public class PointTypeConverter {
    public static PixelPoint convertPointToPixelPoint(JXMapViewer mapViewer, Point point) {
        if(point==null) {
            System.out.println("Point does not exist");
            return null; // Handle null point case
        }
        double latitude = point.getLat();
        double longitude = point.getLon();

        int[] pixelCoords = CoordinatesConverter.convertGeoToPixel(mapViewer, latitude, longitude);
        int pixelX = pixelCoords[0];
        int pixelY = pixelCoords[1];
        return new PixelPoint(pixelX, pixelY); //returns pixel coordinates
    }
    public static Point convertPixelPointToPoint(JXMapViewer mapViewer, PixelPoint pixelPoint) {
        if(pixelPoint==null) {
            System.out.println("PixelPoint does not exist");
            return null; // Handle null pixel point case
        }
        int x = pixelPoint.getLonPixel();
        int y = pixelPoint.getLatPixel();

        double[] geoCoords = CoordinatesConverter.convertPixelToGeo(mapViewer, x, y);
        double latitude = geoCoords[0];
        double longitude = geoCoords[1];
        return new Point(latitude, longitude); //returns geographic coordinates
    }
}
