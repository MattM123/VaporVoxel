package com.marcuzzo.vaporvoxel;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

public class Cube extends Box {
    private boolean isActive;
    private final BlockType type;

    public Cube() {
        super();
        type = BlockType.DEFAULT;
        setMaterial(new PhongMaterial(Color.AQUA));
        isActive = true;

    }

    public boolean isActive(){
        return isActive;
    }

    public void setActive(boolean b) {
        isActive = b;
    }

    public BlockType getType() {
        return type;
    }

    public double[] getLowerRightVertex() {
        return new double[] {this.getBoundsInParent().getMaxX(), this.getBoundsInParent().getMaxY(),
                this.getBoundsInParent().getMaxZ()};
    }

    public double[] getUpperLeftVertex() {
        return new double[] {this.getBoundsInParent().getMinX(), this.getBoundsInParent().getMinY(),
                this.getBoundsInParent().getMinZ()};
    }

    @Override
    public String toString() {
        return "Cube Location: (" + getUpperLeftVertex()[0] + "," +  getUpperLeftVertex()[1] + "," + getUpperLeftVertex()[2] + ") ("
                + getLowerRightVertex()[0] + "," + getLowerRightVertex()[1] + "," + getLowerRightVertex()[2] + ")";
    }



}
