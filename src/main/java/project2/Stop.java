package project2;

// Represents public transport stops
public class Stop {
    private final String id;
    private final String name;
    private final Point location;


    public Stop(String id, String name, Point location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Point getPoint() {
        return this.location;
    }
}
