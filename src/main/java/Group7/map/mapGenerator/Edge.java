package Group7.map.mapGenerator;

public class Edge {
    
    private final Room source;
    private final Room target;


    public Edge(Room source, Room target) {
        this.source = source;
        this.target = target;
    }
    

    public double distance() {
        return source.distance(target);
    }


    public Room getFirst() {
        return source;
    }


    public Room getSecond() {
        return target;
    }

    @Override
    public String toString() {
        return "[" + source + ", " + target + ", distance: " + source.distance(target) + "]";
    }

}
