package Group7.map.mapGenerator.logic;

public class MinHeap<T extends Comparable> {

    protected int heapSize;
    protected Comparable array[];

    public MinHeap(int capacity) {
        heapSize = 0;
        array = (T[]) new Comparable[capacity + 1];
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
        Comparable<? extends Object> temp = array[first];
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
        return heapSize == 0;
    }


    public T deleteMin() {

        Comparable<? extends Object> minimum = array[1];
        array[1] = array[heapSize];
        heapSize--;
        convertToHeapDS(1);

        return (T) minimum;
    }
}
