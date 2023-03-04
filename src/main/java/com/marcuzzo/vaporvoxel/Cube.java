package com.marcuzzo.vaporvoxel;

import java.io.Serializable;

public class Cube extends Point3DByComposition implements Serializable {
    private BlockType type;
    public double f;

    public Cube(int x, int y, int z) {
        super(x, y, z);
    }
    public Cube(int x, int y, int z, BlockType b) {
        super(x, y, z);
        type = b;
    }

    public BlockType getType() {
        return type;
    }
    public void setType(BlockType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Cube) {
            return this.myPoint.getX() == ((Cube) o).myPoint.getX() && this.myPoint.getY() == ((Cube) o).myPoint.getY()
                    && this.myPoint.getZ() == ((Cube) o).myPoint.getZ();
        } else
            return false;
    }

}