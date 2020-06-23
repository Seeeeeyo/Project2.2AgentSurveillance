package Group7.map.mapGenerator.logic;

import Group7.map.mapGenerator.Connector;
import Group7.map.mapGenerator.Map;
import Group7.map.mapGenerator.Object;
import Group7.map.mapGenerator.Room;

public class MapGenerator {

    private final int INFINITY = Integer.MAX_VALUE;

    private RoomFactory roomFactory;
    private MapVisualizer visualizer;


    public MapGenerator(RoomFactory _roomFactory) {
        roomFactory = _roomFactory;
        visualizer = new MapVisualizer();
    }


   //Minimum Spanning Tree
    private Connector[] primAlgorithm(Room rooms[]) {
        int rootIndex = 0;

        ObjectHeap heap = new ObjectHeap(rooms.length);
        Object objects[] = new Object[rooms.length];
        Room parent[] = new Room[rooms.length];

        for (int i = 0; i < rooms.length; i++) {
            parent[i] = null;
            objects[i] = new Object(rooms[i], INFINITY, i);
        }
        objects[rootIndex].setValue(0);

        for (Object object : objects) {
            heap.insert(object);
        }

        Connector edges[] = new Connector[rooms.length - 1];
        int edgePointer = 0;

        // Prim's algorithm for Minimum Spanning Tree
        while (!heap.isEmpty()) {
            // Delete the minimum
            Object currentObject = heap.deleteMin();
            // Add edge
            if (parent[currentObject.getIndex()] != null) {
                Connector edge = new Connector(currentObject.getRoom(), parent[currentObject.getIndex()]);
                edges[edgePointer] = edge;
                edgePointer++;
            }

            // Go through all the rooms
            for (int i = 0; i < objects.length; i++) {
                // No paths to self
                if (currentObject.getIndex() != i) {
                    double distance = currentObject.getRoom().distance(objects[i].getRoom());
                    if (distance < objects[i].getValue() && heap.contains(objects[i])) {
                        parent[i] = currentObject.getRoom();
                        objects[i].setValue(distance);
                        heap.update(objects[i]);
                    }
                }
            }
        }
        
        return edges;
    }


    public Map generateMap(int rooms) {
        Room mapRooms[] = new Room[rooms];

        for (int i = 0; i < rooms; i++) {
            Room room = roomFactory.produceRoom();
            mapRooms[i] = room;
        }

        Connector edges[] = primAlgorithm(mapRooms);
        
        char[][] map = visualizer.createMap(mapRooms, edges);
        return new Map(map);
    }

}
