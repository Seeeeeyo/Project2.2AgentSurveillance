package Group7.map.mapGenerator;

import java.io.Serializable;


public class Map implements Serializable {
    
    private final Room[] rooms;
    private final char map[][];


    public Map(Room[] rooms, char[][] map) {
        this.rooms = rooms;
        this.map = map;
    }


    public char[][] getMap() {
        return map.clone();
    }


    public Room[] getRooms() {
        return rooms.clone();
    }

    @Override
    public String toString() {
        String mapString = "";
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                mapString += map[i][j];
            }
            mapString += "\n";
        }
        return mapString;
    }
    
}
