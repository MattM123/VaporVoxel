package com.marcuzzo.vaporvoxel;

import java.util.ArrayList;

public class Layer<Cube> extends ArrayList<Cube> {

    private int z = 0;

    public Layer() {
        super();
    }
    public void setZ(int z) {
        this.z = z;
    }

    public int getZ() {
        return this.z;
    }

}
