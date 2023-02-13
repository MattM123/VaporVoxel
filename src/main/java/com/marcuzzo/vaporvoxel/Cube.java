package com.marcuzzo.vaporvoxel;

import org.fxyz3d.geometry.Point3D;

public class Cube extends Point3D {
    private final boolean isActive;
    private BlockType type;
    public Cube(int x, int y, int z) {
        super(x, y, z);
        isActive = false;
    }

    //public boolean isActive(){
    //    return isActive;
   // }
    //public void setActive(boolean b) { isActive = b; }
    public BlockType getType() {
        return type;
    }
    public void setType(BlockType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Cube) {
            return this.getX() == ((Cube) o).getX() && this.getY() == ((Cube) o).getY() && this.getZ() == ((Cube) o).getZ();
        } else
            return false;
    }

}