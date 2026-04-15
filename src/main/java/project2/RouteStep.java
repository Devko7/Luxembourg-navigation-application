package project2;

import java.util.LinkedHashMap;
import java.util.Map;

public class RouteStep {
    private final String mode;
    private final Point to;
    private final Integer duration;
    private final String startTime;
    private final String stop;
    private final RouteInfo route;

    public RouteStep(String mode, Point to, int duration, String startTime, String stop, RouteInfo route) {
        this.mode = mode;
        this.to = to;
        this.duration = duration;
        this.startTime = startTime;
        this.stop = stop;
        this.route = route;
    }

    public String getMode() {
        return this.mode;
    }

    public Point getPoint() {
        return this.to;
    }

    public Integer getDuration() {
        return this.duration;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public String getStop() {
        return this.stop;
    }

    public RouteInfo getRoute() {
        return this.route;
    }

    public Map<String,Object> toJson() {
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("mode", this.mode);
        map.put("to", this.to.toJson());
        map.put("duration", this.duration);
        map.put("startTime", this.startTime);
        if ("ride".equals(this.mode)) {
            map.put("stop", this.stop);
            map.put("route", this.route.toJson());
        }
        return map;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RouteStep[mode=").append(mode).append(", to=").append(to).append(", duration=").append(duration).append(" min").append(", startTime=").append(startTime);

        if (stop != null) {
            sb.append(", stop=").append(stop);
        }

        if (route != null) {
            sb.append(", route=").append(route);
        }

        sb.append("]");
        return sb.toString();
    }
}
