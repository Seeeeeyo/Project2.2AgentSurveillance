package Group7.map.mapGenerator.datastructures;

import Group7.map.mapGenerator.Object;


public class ObjectHeap extends MinHeap<Object> {

    private final int EMPTY = -1;

    private int[] locations;

    public ObjectHeap(int capacity) {
        super(capacity);
        initializeLocations(capacity);

    }

    private void initializeLocations(int capacity) {
        locations = new int[capacity + 1];
        for (int i = 0; i < locations.length; i++ ) {
            locations[i] = EMPTY;
        }
    }

    @Override
    protected void swap(int first, int second) {
        super.swap(first, second);
        Object firstObj = (Object) array[first];
        Object secondObj = (Object) array[second];

        int temp = locations[secondObj.getIndex()];
        locations[secondObj.getIndex()] = locations[firstObj.getIndex()];
        locations[firstObj.getIndex()] = temp;
    }

    @Override
    public void insert(Object value) {
        heapSize++;
        int index = heapSize;
        locations[value.getIndex()] = heapSize;

        while (index > 1 && array[parent(index)].compareTo(value) > 0) {

            //memorizing locations in here
            int temp = locations[parent(index)];
            locations[parent(index)] = locations[index];
            locations[index] = temp;

            // Up
            array[index] = array[parent(index)];
            index = parent(index);
        }

        array[index] = value;
        locations[value.getIndex()] = index;
    }

    public void update(Object object) {
        int heapLocation = locations[object.getIndex()];
        decreaseKey(heapLocation, object);
    }


    public boolean contains(Object object) {
        return locations[object.getIndex()] != EMPTY;
    }


    public Comparable[] getArray() {
        return array.clone();
    }


    public Object deleteMin() {
        Object minimum = (Object) array[1];

        int first = ((Object) array[1]).getIndex();
        int second = ((Object) array[heapSize]).getIndex();
        locations[first] = EMPTY;
        locations[second] = 1;

        array[1] = array[heapSize];
        array[heapSize] = null;

        locations[minimum.getIndex()] = EMPTY;
        heapSize--;

        convertToHeapDS(1);

        return minimum;
    }

}
