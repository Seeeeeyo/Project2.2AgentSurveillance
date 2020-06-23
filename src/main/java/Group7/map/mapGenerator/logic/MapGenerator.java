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
import Group7.map.mapGenerator.Map;
import Group7.map.mapGenerator.Object;
import Group7.map.mapGenerator.Room;

public class MapGenerator {

    private final int INFINITY = Integer.MAX_VALUE;

    private RoomFactory roomFactory;
    private MapVisualizer visualizer;
    
    /**
     * Map Generator
     *
     * @param roomFactory Factory that provides the rooms
     */
    public MapGenerator(RoomFactory roomFactory) {
        this.roomFactory = roomFactory;
        this.visualizer = new MapVisualizer();
    }

    public MapGenerator() {
        this(new RoomFactory());
    }


    /**
     *
     * Get minimum spanning tree with Prim's algorithm
     *
     * @param rooms Rooms which to be connected
     * @return Minimum spanning tree represented as edges
     */
    private Connector[] primAlgorithm(Room rooms[]) {
        int rootIndex = 0;

        ObjectHeap heap = new ObjectHeap(rooms.length);
        Object objects[] = new Object[rooms.length];
        Room parent[] = new Room[rooms.length];

        for (int i = 0; i < rooms.length; i++) {
            parent[i] = null;
            objects[i] = new Object(rooms[i], this.INFINITY, i);
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
            Room room = this.roomFactory.produceRoom();
            mapRooms[i] = room;
        }

        Connector edges[] = primAlgorithm(mapRooms);
        
        char[][] map = visualizer.createMap(mapRooms, edges);
        return new Map(map);
    }

}
