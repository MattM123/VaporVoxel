package com.marcuzzo.vaporvoxel;

public class Column {
    private float x;
    private float y;
    private float value = 0;
    /**
     * A Column represents a vertical column of a chunk, 256 blocks in height. Used for elevation sampling.
     * @param x coordinate of the column
     * @param y coordinate of the column
     */
    public Column(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getValue() {
        return value;
    }

    public Column setValue(float v) {
        this.value = v;
        return this;
    }

    /*
            Compares 2 columns. Will return true if and only if the columns XY location is the same as the other column
            */
    @Override
    public boolean equals(Object o) {
        Column c = (Column) o;
        return this.x == c.x && this.y == c.y;
    }
}
