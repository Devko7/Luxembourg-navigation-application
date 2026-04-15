package project2;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import project2.GUI.MainFrame;

public class ConsoleInterface {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in).useLocale(Locale.US);

        System.out.println("Load NEW GTFS data? [y / n]: ");
        String loadData = scanner.nextLine();

        if (loadData.equals("y") || loadData.equals("Y")) {
            new RoutingEngine().run();
        }

        System.out.println("Do you want to start the GUI (this will open a new window)? [y / n]: ");
        String openGUI = scanner.nextLine();

        if (openGUI.equals("y") || openGUI.equals("Y")) {
            MainFrame.main(args);
        }
        
        System.out.println("Start point latitude coordinates (in the form 12.345): ");
        double startPointLat = scanner.nextDouble();
        System.out.println("Start point longitude coordiantes (in the form 12.345): ");
        double startPointLon = scanner.nextDouble();
        System.out.println("End point latitude coordinates (in the form 12.345): ");
        double endPointLat = scanner.nextDouble();
        System.out.println("End point longitude coordinates (in the form 12.345): ");
        double endPointLon = scanner.nextDouble();
        System.out.println("Depart at time: ");
        scanner.nextLine();
        String startTimeString = scanner.nextLine();
        LocalTime startTime = LocalTime.parse(startTimeString);

        Point startPoint = new Point(startPointLat, startPointLon);
        Point endPoint = new Point(endPointLat, endPointLon);
        List<RouteStep> route = PathFinder.findRoute(startPoint, endPoint, startTime);
        for (RouteStep rs : route) {
            System.out.println(rs.toString());
        }

        System.out.println("Run again? [y / n]: ");
        String runAgain = scanner.nextLine();

        if (runAgain.equals("y") || runAgain.equals("Y")) {
            RoutingEngine.main(null);
        }

        scanner.close();
    }
}
