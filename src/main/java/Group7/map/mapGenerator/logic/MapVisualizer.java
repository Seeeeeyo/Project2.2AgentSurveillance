/*
 * The MIT License
 *
 * Copyright 2018 d471061c.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package Group7.map.mapGenerator.logic;
import Group7.map.mapGenerator.Connector;
import Group7.map.mapGenerator.Room;


public class MapVisualizer {

    public final static char WALL = '#';
    public final static char insideBlock = '.';
    public final static char outerBlock = ' ';

    /**
     * Drawing the room into character code
     *
     * @param room Room to be drawn
     * @param map  Character map to be drawn into
     */

    private void drawRoom(Room room, char[][] map) {
        //N and S
        for (int i = 0; i < room.getWidth(); i++) {
            map[room.getY()][room.getX() + i] = WALL;
            map[room.getY() + room.getHeight() - 1][room.getX() + i] = WALL;
        }

        //E and W
        for (int i = 0; i < room.getHeight(); i++) {
            map[room.getY() + i][room.getX()] = WALL;
            map[room.getY() + i][room.getX() + room.getWidth() - 1] = WALL;
        }

        //blank area
        for (int i = 0; i < room.getWidth() - 2; i++) {
            for (int j = 0; j < room.getHeight() - 2; j++) {
                map[room.getY() + j + 1][room.getX() + i + 1] = outerBlock;
            }
        }
    }

    /**
     * Connects 2 rooms
     *
     * @param edge Edge containing two rooms
     * @param map  Character map
     */
    private void drawEdge(Connector edge, char[][] map) {
        int x = edge.getFirst().getCenterX();
        int y = edge.getFirst().getCenterY();
        int destinationX = edge.getSecond().getCenterX();
        int destinationY = edge.getSecond().getCenterY();

        char direction = '?';
        // Move horizontally
        while (x != destinationX) {
            if (x < destinationX) {
                x += 1;
                direction = 'r';
            } else if (x > destinationX) {
                x -= 1;
                direction = 'l';
            }
            if (map[y][x] != insideBlock) {
                map[y][x] = insideBlock;
                map[y + 1][x] = WALL;
                map[y - 1][x] = WALL;
            }

        }
        
        // Draw turning point
        if (direction == 'l') {
            if (y > destinationY) {
                map[y-1][x-1] = WALL;
                map[y+1][x-1] = WALL;
                map[y][x-1]= WALL;
            } else if (y < destinationY) {
                map[y][x-1] = WALL;
                map[y -1][x-1] = WALL;
            }
        } else if (direction == 'r') {
            if (y > destinationY) {
                map[y+1][x+1] = WALL;
                map[y][x+1] = WALL;
            } else if (y < destinationY) {
                map[y+1][x+1] = WALL;
                map[y][x+1] = WALL;
                map[y-1][x+1] = WALL;
            }
        }
        
        // Move vertically
        while (y != destinationY) {
            if (y < destinationY) {
                y++;
            } else if (y > destinationY) {
                y--;
            }
            if (map[y][x] != insideBlock) {
                map[y][x] = insideBlock;
                map[y][x + 2] = WALL;
                map[y][x - 2] = WALL;
            }

        }
    }

    /***
     * Draw a blank area within the room.
     * @param room Room which area will be drawn blank
     * @param map Map in which to draw
     */
    private void clearRoom(Room room, char[][] map) {
        for (int i = 0; i < room.getWidth() - 2; i++) {
            for (int j = 0; j < room.getHeight() - 2; j++) {
                map[room.getY() + j + 1][room.getX() + i + 1] = insideBlock;
            }
        }
    }
    
    /**
     * Offset all rooms by given x and y offsets.
     * @param rooms Array of rooms.
     * @param offsetX how much x will be offset
     * @param offsetY how much y will be offset
     */
    private void offsetRooms(Room rooms[], int offsetX, int offsetY) {
        for (Room room : rooms) {
            room.setX(room.getX() + offsetX);
            room.setY(room.getY() + offsetY);
        }
    }

    public final char[][] createMap(Room mapRooms[], Connector[] edges) {
        if (mapRooms.length == 0) {
            return new char[0][0];

        }

        int minimumX = mapRooms[0].getX();
        int minimumY = mapRooms[0].getY();
        for (Room room : mapRooms) {
            if (minimumX > room.getX()) {
                minimumX = room.getX();
            }
            if (minimumY > room.getY()) {
                minimumY = room.getY();
            }
        }

        this.offsetRooms(mapRooms, -minimumX, -minimumY);

        int maximumX = 0;
        int maximumY = 0;
        for (Room room : mapRooms) {
            if (maximumY < room.getHeight() + room.getY()) {
                maximumY = room.getHeight() + room.getY();
            }
            if (maximumX < room.getWidth() + room.getX()) {
                maximumX = room.getWidth() + room.getX();
            }
        }

        char map[][] = new char[maximumY][maximumX];
        for (int i = 0; i < maximumY; i++) {
            for (int j = 0; j < maximumX; j++) {
                map[i][j] = outerBlock;
            }
        }

        for (Room room : mapRooms) {
            this.drawRoom(room, map);
        }

        for (Connector edge : edges) {
            drawEdge(edge, map);
        }

        for (Room mapRoom : mapRooms) {
            clearRoom(mapRoom, map);
        }
        return map;
    }
}
