package project2;

// Calculates actual distance between two coordinate points
public class Haversine {
    private static final double EARTH_RADIUS_KM = 6371.0;
    
    public static double distance(Point p1, Point p2) {
        double lat1Rad = Math.toRadians(p1.getLat());
        double lat2Rad = Math.toRadians(p2.getLat());
        double deltaLatRad = Math.toRadians(p2.getLat() - p1.getLat());
        double deltaLonRad = Math.toRadians(p2.getLon() - p1.getLon());
        
        double a = Math.sin(deltaLatRad / 2) * Math.sin(deltaLatRad / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLonRad / 2) * Math.sin(deltaLonRad / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_KM * c;
    }
}
