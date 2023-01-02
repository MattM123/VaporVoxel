package com.marcuzzo.vaporvoxel;

import org.fxyz3d.geometry.Point3D;

public class Cube extends Point3D {
    private boolean isActive;
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

}