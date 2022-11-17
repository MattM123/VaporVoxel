package com.marcuzzo.vaporvoxel;

import javafx.scene.shape.Box;
import org.fxyz3d.geometry.Point3D;

public class Cube extends Box {
    private boolean isActive;
    private BlockType type;
    public Cube() {
        super();
        isActive = false;
        setType(BlockType.DEFAULT);
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

    public Point3D getUpperLeftVertex() {
        return new Point3D(getBoundsInParent().getMinX(), getBoundsInParent().getMinY(),
                getBoundsInParent().getMinZ());
    }

   @Override
    public String toString() {
       return getId();
    }
}