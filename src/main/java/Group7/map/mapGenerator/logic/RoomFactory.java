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

import Group7.map.mapGenerator.Room;

import java.util.Random;


public class RoomFactory {

    private final int DEFAULT_MINIMUM_WIDTH = 30;
    private final int DEFAULT_MINIMUM_HEIGHT = 10;
    private final int DEFAULT_MINIMUM_X = 0;
    private final int DEFAULT_MINIMUM_Y = 0;

    private final int DEFAULT_MAXIMUM_WIDTH = 30;
    private final int DEFAULT_MAXIMUM_HEIGHT = 10;
    private final int DEFAULT_MAXIMUM_X = 0;
    private final int DEFAULT_MAXIMUM_Y = 0;

    private final boolean DEFAULT_FIXED_SIZE = false;

    private int minWidth;
    private int minHeight;
    private int minX;
    private int minY;

    private int maxWidth;
    private int maxHeight;
    private int maxX;
    private int maxY;

    private boolean isFixedSize;

    private Random random;

    public RoomFactory(Random _random) {
        random = _random;
        maxX = DEFAULT_MAXIMUM_X;
        maxY = DEFAULT_MAXIMUM_Y;
        maxWidth = DEFAULT_MAXIMUM_WIDTH;
        maxHeight = DEFAULT_MAXIMUM_HEIGHT;
        minX = DEFAULT_MINIMUM_X;
        minY = DEFAULT_MINIMUM_Y;
        minWidth = DEFAULT_MINIMUM_WIDTH;
        minHeight = DEFAULT_MINIMUM_HEIGHT;
        isFixedSize = DEFAULT_FIXED_SIZE;
    }

    public RoomFactory() {
        this(new Random());
    }

    public void setMaximHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public void setMaximWidth(int _maxWidth) {
        maxWidth = _maxWidth;
    }

    public void setMaximX(int _maxX) {
        maxX = _maxX;
    }

    public void setFixedSize(boolean _isFixedSize) {
        isFixedSize = _isFixedSize;
    }


    public void setMaximY(int _maxY) {
        maxY = _maxY;
    }

    public Room produceRoom() {
        int xCoordinate = minX + random.nextInt(maxX);
        int yCoordinate = minY + random.nextInt(maxY);
        int width = minWidth;
        int height = minHeight;
        if (isFixedSize == false) {

            width += random.nextInt(maxWidth);
            height += random.nextInt(maxHeight);

        }

        return new Room(xCoordinate, yCoordinate, width, height);
    }

}
