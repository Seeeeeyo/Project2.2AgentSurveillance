package Group7.map.mapGenerator;

import java.io.Serializable;

public class Room implements Serializable {

    private int x;
    private int y;

    private int width;
    private int height;

    /**
     * Representation of a room.
     *
     * @param _width  The width of the room
     * @param _height The height of the room
     * @param _x      Upper-left coordinate
     * @param _y      Upper-left coordinate
     */

    public Room(int _x, int _y, int _width, int _height) {
        x = _x;
        y = _y;
        width = _width;
        height = _height;
    }

    public int getX() {
        return x;
    }

    public void setX(int _x) {
        x = _x;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getY() {
        return y;
    }

    public void setY(int _y) {
        y = _y;
    }

    public double distance(Room room) {

        return this.euclidianDistance(room);

    }

    public double euclidianDistance(Room r) {
        double roomX = r.getX() + r.getWidth() / 2.0;
        double roomY = r.getY() + r.getHeight() / 2.0;

        double centerX = x + width / 2.0;
        double centerY = y + height / 2.0;

        return Math.sqrt(Math.pow(roomX - centerX, 2.0) + Math.pow(roomY - centerY, 2.0));
    }

    public int getCenterX() {
        return x + width / 2;
    }

    public int getCenterY() {
        return y + height / 2;
    }

    public void setHeight(int _height) {
        height = _height;
    }

    public void setWidth(int _width) {
        width = _width;
    }

}
