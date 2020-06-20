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
package Group7.map.mapGenerator;

import java.io.Serializable;

public class Room implements Serializable {

    private int x;
    private int y;

    private int width;
    private int height;

    /**
     * Representation of a room.
     *
     * @param x Upper-left corners X-coordinate of the room
     * @param y Upper-left corners Y-coordinate of the room
     * @param width The width of the room
     * @param height The height of the room
     */
    public Room(int x, int y, int width, int height) {
        if (this.x < 0) {
            throw new IllegalArgumentException("X should not be negative");
        }
        if (this.y < 0) {
            throw new IllegalArgumentException("X should not be negative");
        }
        if (this.width < 0) {
            throw new IllegalArgumentException("Width should not be negative");
        }
        if (this.height < 0) {
            throw new IllegalArgumentException("Height should not be negative");
        }
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }


    public int getHeight() {
        return height;
    }


    public int getWidth() {
        return width;
    }


    public int getX() {
        return x;
    }


    public int getY() {
        return y;
    }


    public boolean collides(Room room) {
        return (this.x <= room.x && 
               room.x <= this.x + this.width && 
               this.y <= room.y && 
               room.y <= this.y + this.height) || 
               (room.x <= this.x &&
                this.x <= room.x + room.width &&
                room.y <= this.y &&
                this.y <= room.y + room.height );
    }


    public double distance(Room room) {
        return this.euqlidianDistance(room);
    }
    

    public double euqlidianDistance(Room room) {
        double roomX = room.getX() + room.getWidth() / 2.0;
        double roomY = room.getY() + room.getHeight() / 2.0;
        double centerX = this.x + this.width / 2.0;
        double centerY = this.y + this.height / 2.0;
        return Math.sqrt(Math.pow(roomX - centerX, 2.0) + Math.pow(roomY - centerY, 2.0));
    }
    
    

    public double manhattanDistance(Room room) {
        double roomX = room.getX() + room.getWidth() / 2.0;
        double roomY = room.getY() + room.getHeight() / 2.0;
        double centerX = this.x + this.width / 2.0;
        double centerY = this.y + this.height / 2.0;
        return Math.abs(roomX - centerX) + Math.abs(roomY - centerY);
    }
    
    

    public int getCenterX() {
        return this.x + this.width / 2;
    }
    

    public int getCenterY() {
        return this.y + this.height / 2;
    }
    

    public void setX(int x) {
        if (x < 0) {
            throw new IllegalArgumentException("X should be not negative");
        }
        this.x = x;
    }


    public void setY(int y) {
        if (y < 0) {
            throw new IllegalArgumentException("Y should be not negative");
        }
        this.y = y;
    }


    public void setHeight(int height) {
        if (height < 0) {
            throw new IllegalArgumentException("Height should not be negative");
        }
        this.height = height;
    }


    public void setWidth(int width) {
        if (width < 0) {
            throw new IllegalArgumentException("Width should not be negative");
        }
        this.width = width;
    }


    @Override
    public String toString() {
        return "(x: " + this.x + ", y: " + this.y + ", width: " + this.width + ", height: " + this.height + ")";
    }

}
