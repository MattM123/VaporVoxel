package com.marcuzzo.vaporvoxel;

import javafx.scene.shape.Box;
import org.fxyz3d.geometry.Point3D;

public class Cube extends Box {
    private boolean isActive;
    private BlockType type;
    public Cube() {
        super();
        isActive = true;
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

/*
    public Point3D getLowerRightVertex() {
       return new Point3D(getBoundsInParent().getMaxX(), getBoundsInParent().getMaxY(),
                getBoundsInParent().getMaxZ());
    }

 */
    public Point3D getUpperLeftVertex() {
        return new Point3D(getBoundsInParent().getMinX(), getBoundsInParent().getMinY(),
                getBoundsInParent().getMinZ());
    }


//   @Override
  //  public String toString() {
  //      return "Upper Left Vertex: (x-" + getUpperLeftVertex().getX() + ",y-" +  getUpperLeftVertex().getY() + ",z-" + getUpperLeftVertex().getZ() + ") " +
 //               "Lower Right Vertex: (x-" + getLowerRightVertex().getX() + ",y-" + getLowerRightVertex().getY() + ",z-" + getLowerRightVertex().getZ() + ") ";
   // }
}
