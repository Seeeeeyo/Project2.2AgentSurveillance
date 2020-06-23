package Group7.map.mapGenerator;

import java.io.Serializable;

public class Map implements Serializable {

    private final char map[][];

    public Map(char[][] _map) {
        map = _map;
    }

    public String toString() {
        String mapString = " ";
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                mapString += map[i][j];
            }
            mapString += "\n";
        }
        return mapString;
    }
    
}
