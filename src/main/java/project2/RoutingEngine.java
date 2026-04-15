package project2;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import com.leastfixedpoint.json.JSONReader;
import com.leastfixedpoint.json.JSONSyntaxError;
import com.leastfixedpoint.json.JSONWriter;

public class RoutingEngine {
    private JSONReader requestReader = new JSONReader(new InputStreamReader(System.in));
    private JSONWriter<OutputStreamWriter> responseWriter = new JSONWriter<>(new OutputStreamWriter(System.out));


    public static void main(String[] args) throws IOException {
        RoutingEngine engine = new RoutingEngine();
        engine.run();
    }

    public void run() throws IOException
    {
        System.err.println("Starting");
        while (true)
        {
            Object json;
            try {
                json = requestReader.read();
            } catch (JSONSyntaxError e) {
                sendError("Bad JSON input");
                break;
            } catch (EOFException e) {
                System.err.println("End of input detected");
                break;
            }
            if (json instanceof Map<?,?>)
            {
                Map<?,?> request = (Map<?,?>) json;
                if (request.containsKey("ping"))
                {
                    sendOk(Map.of("pong", request.get("ping")));
                    continue;
                }
                else if (request.containsKey("load"))
                {
                    try {
                        String dataPath = (String) request.get("load");

                        // Load the data in the database
                        DataBaseConnection dbManager = DataBaseConnection.getInstance();
                        dbManager.loadGTFS(dataPath);

                        sendOk("loaded");
                    } catch (FileNotFoundException e) {
                        sendError("File not found");
                        break;
                    } catch (Exception e) {
                        sendError(e.getMessage());
                        break;
                    }
                    continue;
                }
                else if (request.containsKey("routeFrom") && request.containsKey("to") && request.containsKey("startingAt")) {
                    try {
                        // Parse from point
                        @SuppressWarnings("unchecked")
                        Map<String,?> src = (Map<String,?>) request.get("routeFrom");
                        Point from = new Point(((Number)src.get("lat")).doubleValue(), ((Number)src.get("lon")).doubleValue());

                        // Parse to point
                        @SuppressWarnings("unchecked")
                        Map<String,?> tgt = (Map<String,?>) request.get("to");
                        Point to = new Point(((Number)tgt.get("lat")).doubleValue(), ((Number)tgt.get("lon")).doubleValue());

                        // Parse start time
                        String t = (String) request.get("startingAt");
                        LocalTime time = LocalTime.parse(t);
                        
                        // Find route
                        List<RouteStep> steps = PathFinder.findRoute(from, to, time);
                        // convert to JSON
                        List<Map<String,Object>> jsonSteps = steps.stream().map(RouteStep::toJson).toList();
                        
                        sendOk(jsonSteps);

                    } catch (Exception e) {
                        sendError("Routing failed: " + e.getMessage());
                    }
                    continue;
                }
            }
            sendError("Bad request");
        }
    }
    private void sendOk(Object value) throws IOException
    {
        responseWriter.write(Map.of("ok", value));
        responseWriter.getWriter().flush();
    }
    private void sendError(String message) throws IOException
    {
        responseWriter.write(Map.of("error", message));
        responseWriter.getWriter().flush();
    }
}
