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
package Group7.map.mapGenerator.datastructures;

public class MinHeap <T extends Comparable>{
    
    protected final int DEFAULT_CAPACITY = 27;
    
    protected int heapSize;
    protected Comparable array[];

    public MinHeap() {
        this.heapSize = 0;
        this.array = (T[]) new Comparable[DEFAULT_CAPACITY + 1];
    }
    
    public MinHeap(int capacity) {
        this.heapSize = 0;
        this.array = (T[]) new Comparable[capacity + 1];
    }
    

    public void buildHeap(T array[]) {
        this.heapSize = array.length;

        Comparable[] newArray = new Comparable[array.length + 1];
        for (int index = 0; index < array.length; index++) {
            newArray[index + 1] = array[index];
        }
        
        this.array = (T[]) newArray;
        for (int index = this.heapSize / 2; 0 < index; index--) {
            this.heapify(index);
        }
    }
    

    protected int parent(int index) {
        return index / 2;
    }
    

    protected int left(int index) {
        return 2 * index;
    }
    

    protected int right(int index) {
        return 2 * index + 1;
    }
    

    protected void swap(int first, int second) {
        Comparable temp = this.array[first];
        this.array[first] = this.array[second];
        this.array[second] = temp;
    }
    

    protected void heapify(int index) {
        int leftIndex = this.left(index);
        int rightIndex = this.right(index);
        
        if (rightIndex <= this.heapSize) {
            int smallestIndex;
            if (this.array[leftIndex].compareTo(this.array[rightIndex]) > 0) {
                smallestIndex = rightIndex;
            } else {
                smallestIndex = leftIndex;
            }
            
            if (this.array[index].compareTo(this.array[smallestIndex]) > 0) {
                this.swap(index, smallestIndex);
                this.heapify(smallestIndex);
            }
            
        } else if (leftIndex == this.heapSize && this.array[index].compareTo(this.array[leftIndex]) > 0) {
            this.swap(index, leftIndex);
        }
    }
    

    public void insert(T value) {
        this.heapSize++;
        int index = this.heapSize;
        
        while(index > 1 && this.array[this.parent(index)].compareTo(value) > 0) {
            this.array[index] = this.array[this.parent(index)];
            index = this.parent(index);
        }
        
        this.array[index] = value;
    }
    

    protected void decreaseKey(int index, T newValue) {
        if (this.array[index] == null) return;
        if (this.array[index].compareTo(newValue) <= 0) {
            this.array[index] = newValue;
            this.heapify(1);
        }
    }
    

    protected void increaseKey(int index, T newValue) {
        if (newValue.compareTo(this.array[index]) > 0) {
            this.array[index] = newValue;
            while (index > 1 && 0 < this.array[index].compareTo(this.array[this.parent(index)])) {
                this.swap(index, this.parent(index));
                index = this.parent(index);
            }
        }
    }
    

    public boolean isEmpty() {
        return this.heapSize == 0;
    }
    

    public int getHeapSize() {
        return this.heapSize;
    }
    

    public T deleteMin() {
        Comparable minimum = this.array[1];
        this.array[1] = this.array[this.heapSize];
        this.heapSize--;
        this.heapify(1);
        return (T) minimum;
    }
}
