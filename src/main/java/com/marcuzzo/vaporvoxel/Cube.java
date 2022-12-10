package com.marcuzzo.vaporvoxel;

import org.fxyz3d.geometry.Point3D;

public class Cube extends Point3D {
    private boolean isActive;
    private BlockType type;
    private double noise;
    public Cube(int x, int y, int z) {
        super(x, y, z);
        isActive = false;
        setType(BlockType.DEFAULT);
        setGradientValue(SimplexNoise.noise(x, y, z));
    }

    public boolean isActive(){
        return isActive;
    }
    public void setActive(boolean b) { isActive = b; }
    public BlockType getType() {
        return type;
    }
    public void setType(BlockType type) {
        this.type = type;
    }

    public double getGradientValue() {
        return noise;
    }

    public void setGradientValue(double n) {
        this.noise = n;
    }

    @Override
    /*
     Compares 2 cubes. Will return true if and only if the cubes location is the same as the other cube
     */
    public boolean equals(Object o) {
        if (o.getClass() == Object.class) {
            Cube c = (Cube) o;
            return this.x == c.x && this.y == c.y && this.z == c.z;
        }
        else {
            return false;
        }
    }

}