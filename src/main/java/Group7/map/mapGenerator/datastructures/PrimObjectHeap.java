package Group7.map.mapGenerator.datastructures;

import Group7.map.mapGenerator.PrimObject;


public class PrimObjectHeap extends MinHeap<PrimObject> {

    // When item is not in the heap
    private final int EMPTY = -1;
    
    // Locations in the heap
    private int[] locations;

    public PrimObjectHeap(int capacity) {
        super(capacity);
        initializeLocations(capacity);

    }

    public PrimObjectHeap() {
        super();
        initializeLocations(DEFAULT_CAPACITY);
    }
    
    private void initializeLocations(int capacity) {
        this.locations = new int[capacity + 1];
        for (int i = 0; i < locations.length; i++ ) {
            this.locations[i] = EMPTY;
        }
    }

    @Override
    protected void swap(int first, int second) {
        super.swap(first, second);
        PrimObject firstObj = (PrimObject)this.array[first];
        PrimObject secondObj = (PrimObject)this.array[second];
        
        int temp = this.locations[secondObj.getIndex()];
        this.locations[secondObj.getIndex()] = this.locations[firstObj.getIndex()];
        this.locations[firstObj.getIndex()] = temp;
    }

    @Override
    public void insert(PrimObject value) {
        this.heapSize++;
        int index = this.heapSize;
        this.locations[value.getIndex()] = heapSize;
        
        while(index > 1 && this.array[this.parent(index)].compareTo(value) > 0) {
            // Keep track of the locations here
            int temp = this.locations[parent(index)];
            this.locations[parent(index)] = this.locations[index];
            this.locations[index] = temp;
            
            // Go up
            this.array[index] = this.array[this.parent(index)];
            index = this.parent(index);
        }
        
        this.array[index] = value;
        this.locations[value.getIndex()] = index;
    }


    public void update(PrimObject object) {
        int heapLocation = this.locations[object.getIndex()];
        this.decreaseKey(heapLocation, object);
    }


    public boolean contains(PrimObject object) {
        return locations[object.getIndex()] != EMPTY;
    }


    public Comparable[] getArray() {
        return array.clone();
    }
    

    public int[] getHeapLocations() {
        return locations.clone();
    }

    @Override
    public PrimObject deleteMin() {
        PrimObject minimum = (PrimObject)this.array[1];
        
        // Locations is set first because the index
        int first  = ((PrimObject) this.array[1]).getIndex();
        int second = ((PrimObject) this.array[this.heapSize]).getIndex();
        this.locations[first] = EMPTY; 
        this.locations[second] = 1;
        
        // Add bottom to top
        this.array[1] = this.array[this.heapSize];
        this.array[this.heapSize] = null;
        
        // Remove from track
        this.locations[minimum.getIndex()] = EMPTY;
        this.heapSize--;
        
        // Fix minheap
        this.heapify(1);
                
        return minimum;
    }

}
