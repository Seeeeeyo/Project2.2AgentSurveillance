package Group7.map.mapGenerator;

public class Connector {

    private final Room sourceRoom;
    private final Room targetRoom;


    public Connector(Room _source, Room _target) {
        sourceRoom = _source;
        targetRoom = _target;
    }


    public Room getFirst() {
        return sourceRoom;
    }

    public Room getSecond() {
        return targetRoom;
    }

    public double distance() {
        return sourceRoom.distance(targetRoom);
    }

}
