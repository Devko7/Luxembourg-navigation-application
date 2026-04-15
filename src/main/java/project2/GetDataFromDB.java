package project2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class GetDataFromDB {

    // Fetch stops
    public static List<Stop> getStops() {
        List<Stop> stops = new ArrayList<>();

        String query = "SELECT stop_id, stop_name, stop_lat, stop_lon FROM stops";

        try (
            Connection conn = DataBaseConnection.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                Point location = new Point(rs.getDouble("stop_lat"), rs.getDouble("stop_lon"));
                Stop stop = new Stop(rs.getString("stop_id"), rs.getString("stop_name"), location);
                stops.add(stop);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stops;
    }

    // Fetch RouteIinfos
    public static List<RouteInfo> getRouteInfos() {
        List<RouteInfo> routeInfos = new ArrayList<>();

        String query = "SELECT a.agency_name, r.route_short_name, r.route_long_name, t.trip_headsign " +
                       "FROM trips t " +
                       "JOIN routes r ON t.route_id = r.route_id " +
                       "JOIN agency a ON r.agency_id = a.agency_id ";

        try (
            Connection conn = DataBaseConnection.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(query)
        ) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String operator = rs.getString("agency_name");
                    String shortName = rs.getString("route_short_name");
                    String longName = rs.getString("route_long_name");
                    String headSign = rs.getString("trip_headsign");

                    RouteInfo routeInfo = new RouteInfo(operator, shortName, longName, headSign);
                    routeInfos.add(routeInfo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return routeInfos;
    }

    // Fetch trips with their stop times
    public static List<Trip> getTrips(Map<String, Stop> stopMap) {
        List<Trip> trips = new ArrayList<>();

        String tripQuery = """
            SELECT t.trip_id, r.route_id, t.service_id, 
                   a.agency_name, r.route_short_name, r.route_long_name, t.trip_headsign,
                   st.stop_id, st.arrival_time, st.departure_time, st.stop_sequence
            FROM trips t
            JOIN routes r ON t.route_id = r.route_id
            JOIN agency a ON r.agency_id = a.agency_id
            JOIN stop_times st ON t.trip_id = st.trip_id
            ORDER BY t.trip_id, st.stop_sequence
            """;

        try (Connection conn = DataBaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(tripQuery);
             ResultSet rs = ps.executeQuery()) {

            String currentTripId = null;
            List<StopTime> currentStopTimes = new ArrayList<>();
            RouteInfo currentRouteInfo = null;

            while (rs.next()) {
                String tripId = rs.getString("trip_id");
                if (!tripId.equals(currentTripId)) {
                    if (currentTripId != null) {
                        // Save the previous trip
                        trips.add(new Trip(currentTripId, currentRouteInfo, currentStopTimes));
                        currentStopTimes = new ArrayList<>();
                    }
                    currentTripId = tripId;
                    currentRouteInfo = new RouteInfo(
                        rs.getString("agency_name"),
                        rs.getString("route_short_name"),
                        rs.getString("route_long_name"),
                        rs.getString("trip_headsign")
                    );
                }

                String arrivalStr = rs.getString("arrival_time");
                String departureStr = rs.getString("departure_time");
                LocalTime arrival = parseGtfsTime(arrivalStr);
                LocalTime departure = parseGtfsTime(departureStr);

                Stop stop = stopMap.get(rs.getString("stop_id"));
                currentStopTimes.add(new StopTime(stop, arrival, departure));
            }

            if (currentTripId != null) {
                trips.add(new Trip(currentTripId, currentRouteInfo, currentStopTimes));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trips;
    }

    private static LocalTime parseGtfsTime(String timeStr) {
        String[] parts = timeStr.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;

        // Compute total seconds since midnight (if >86400 then next day)
        int totalSeconds = hours * 3600 + minutes * 60 + seconds;

        // Represent the time of day
        int secsOfDay = totalSeconds % (24 * 3600);
        return LocalTime.ofSecondOfDay(secsOfDay);
    }
}
