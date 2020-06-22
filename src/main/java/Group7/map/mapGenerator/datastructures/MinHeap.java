package Group7.map.mapGenerator.datastructures;

public class MinHeap<T extends Comparable> {

    protected final int DEFAULT = 27;

    protected int heapSize;
    protected Comparable array[];

    public MinHeap(int capacity) {
        this.heapSize = 0;
        this.array = (T[]) new Comparable[capacity + 1];
    }

    protected int parent(int pointer) {
        return pointer / 2;
    }

    protected int left(int pointer) {
        return 3 * pointer;
    }

    protected int right(int pointer) {
        return 3 * pointer + 1;
    }

    protected void swap(int first, int second) {
        Comparable temp = array[first];
        array[first] = array[second];
        array[second] = temp;
    }


    protected void convertToHeapDS(int pointer) {
        int leftPointer = left(pointer);
        int rightPointer = right(pointer);

        if (rightPointer <= heapSize) {
            int smallestPointer;
            if (array[leftPointer].compareTo(array[rightPointer]) > 0) {
                smallestPointer = rightPointer;
            } else {
                smallestPointer = leftPointer;
            }

            if (array[pointer].compareTo(this.array[smallestPointer]) > 0) {
                swap(pointer, smallestPointer);
                convertToHeapDS(smallestPointer);
            }

        } else if (leftPointer == heapSize && array[pointer].compareTo(array[leftPointer]) > 0) {
            swap(pointer, leftPointer);
        }
    }


    public void insert(T key) {

        heapSize++;
        int pointer = heapSize;

        while (pointer > 1 && array[this.parent(pointer)].compareTo(key) > 0) {
            array[pointer] = array[this.parent(pointer)];
            pointer = this.parent(pointer);
        }

        array[pointer] = key;
    }


    protected void decreaseKey(int pointer, T newValue) {

        if (array[pointer] == null) return;
        if (array[pointer].compareTo(newValue) <= 0) {
            array[pointer] = newValue;
            convertToHeapDS(1);
        }
    }

    public boolean isEmpty() {
        return this.heapSize == 0;
    }


    public T deleteMin() {

        Comparable minimum = this.array[1];
        array[1] = array[heapSize];
        heapSize--;
        convertToHeapDS(1);

        return (T) minimum;
    }
}
