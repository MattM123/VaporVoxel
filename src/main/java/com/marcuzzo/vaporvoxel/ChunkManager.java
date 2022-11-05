package com.marcuzzo.vaporvoxel;

import java.util.Vector;

public class ChunkManager extends  Vector<Chunk> {

    public ChunkManager() {
        add(new Chunk().initialize(0,0,0));
    }
}
