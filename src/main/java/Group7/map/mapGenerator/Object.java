package Group7.map.mapGenerator;

public class Object implements Comparable {

    private Room room;
    private final int pointer;
    private double key;

    public Object(Room _room, double _key, int _pointer) {

        room = _room;
        key = _key;
        pointer = _pointer;

    }

    public Room getRoom() {
        return room;
    }

    public int getIndex() {
        return pointer;
    }

    public double getValue() {
        return key;
    }

    public void setValue(double _key) {
        key = _key;
    }

    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (int) (Double.doubleToLongBits(key) ^ (Double.doubleToLongBits(key) >>> 32));
        hash = 97 * hash + pointer;
        return hash;
    }

    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public int compareTo(java.lang.Object o) {
        return 0;
    }
}
