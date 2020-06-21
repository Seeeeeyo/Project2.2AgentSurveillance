package Group7.map.mapGenerator;

public class PrimObject implements Comparable {
    
    // Values of prim object
    private Room room;
    private double value;

    // ID of the PrimObject, location in array
    private final int index;

    public PrimObject(Room room, double value, int index) {
        this.room = room;
        this.value = value;
        this.index = index;
    }

    /***
     * Returns the room of the object
     * @return Room
     */
    public Room getRoom() {
        return room;
    }

    /***
     * Returns index in which this object is stored in.
     * @return Index of heap
     */
    public int getIndex() {
        return index;
    }
    
    
    /***
     * Returns the value of the object
     * @return Value of the object
     */
    public double getValue() {
        return value;
    }

    /***
     * Set room of the object
     * @param room Room for the object
     */
    public void setRoom(Room room) {
        this.room = room;
    }

    /***
     * Set new value for the object
     * @param value 
     */
    public void setValue(double value) {
        this.value = value;
    }


    @Override
    public int compareTo(Object t) {
        if (t instanceof PrimObject) {
            PrimObject other = (PrimObject) t;
            if (other.value > this.value) {
                return -1;
            }  else if (other.value < this.value) {
                return 1;
            } else {
                return 0;
            }
        }
        
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PrimObject) {
            PrimObject primObj = (PrimObject) obj;
            return primObj.index == this.index && primObj.value == this.value;
        }
        return false; 
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.value) ^ (Double.doubleToLongBits(this.value) >>> 32));
        hash = 97 * hash + this.index;
        return hash;
    }

    
    @Override
    public String toString() {
        return "(Id: " + this.index + ", value: " + this.value + ")" ;
    }
    
    
}
