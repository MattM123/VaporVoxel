package com.marcuzzo.vaporvoxel;

import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import org.fxyz3d.geometry.Point3D;
import java.util.Vector;

public class ChunkManager extends Vector<Chunk> {
    private final PerspectiveCamera player;
    private int RENDER_DISTANCE = 2;

    public ChunkManager(PerspectiveCamera player) {
        this.player = player;
        add(new Chunk().initialize(0, 0, 0));
    }
    public Chunk getChunkWithPlayer() {
        Chunk c = null;
        for (Chunk chunk : this) {
            if (player.getBoundsInParent().intersects(chunk.getBoundsInParent())) {
                c = chunk;
                break;
            }
        }
        if (size() == 1) {
            c = get(0);
        }
        return c;
    }
    public boolean containsChunkWithLocation(final Point3D loc){
        return stream().map(Chunk::getLocation).anyMatch(loc::equals);
    }

    /**
     * Gets a chunk from the manager that is located in a specific position. This location is the same
     * location that was used when the chunk was initialized. If no chunk is found with the location
     * null is returned
     * @param loc The location of the chunk
     * @return Null if the chunk doesn't exist, else will return the chunk
     */
    public Chunk getChunkWithLocation(Point3D loc) {
        Chunk c = null;
        if (containsChunkWithLocation(loc)) {
            for (Chunk chunk : this) {
                if (chunk.getLocation().equals(loc)) {
                    c = chunk;
                    break;
                }
            }
        }
        return c;
    }
    public void updateRender(Group world) {
        //Spawn chunk rendering and de-rendering
        if (getChunkWithPlayer() != null) {
            int CHUNK_BOUNDS = 16;
            ChunkRenderingAlgorithm render = new ChunkRenderingAlgorithm(RENDER_DISTANCE, CHUNK_BOUNDS, getChunkWithPlayer(), this);
            if (!world.getChildren().contains(get(0)))
                get(0).updateChunk(world);
            if (world.getChildren().contains(get(0)) && !render.getChunksToRender().contains(get(0))) {
                get(0).removeChunk(world);
            }

        //Surrounding chunk rendering and de-rendering
            for (Chunk chunk : render.getChunksToRender()) {
                if (getChunkWithLocation(chunk.getLocation()) != null)
                    chunk.updateChunk(world);
                else {
                    add(chunk);
                    chunk.addToWorld(world);
                }
            }
            for (Chunk chunk : this) {
                if (!render.getChunksToRender().contains(chunk)) {
                    chunk.removeChunk(world);
                }
            }
        }

    }
}
