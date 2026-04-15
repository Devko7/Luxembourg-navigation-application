package project2;

import java.time.LocalTime;

// Represents an edge with mode "walk"
public class WalkEdge extends Edge {
    private final int durationMinutes;

    public WalkEdge(Stop fromStop, Stop toStop, LocalTime departure, LocalTime arrival, int durationMinutes) {
        super(fromStop, toStop, departure, arrival);
        this.durationMinutes = durationMinutes;
    }

    @Override
    public int getDurationMinutes() {
        return durationMinutes;
    }
}
