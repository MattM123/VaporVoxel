package com.marcuzzo.vaporvoxel;

import javafx.scene.Group;
import org.fxyz3d.geometry.Point3D;

import java.util.Vector;

public class ChunkManager extends Vector<Chunk> {
    private final Player player;
    private final int RENDER_DISTANCE = 2;

    public ChunkManager(Player player, Group world) {
        this.player = player;
        player.setManager(this);
        add(new Chunk().initialize(0, 0, 0));
        updateRender(world);
    }

    public Chunk getChunkWithPlayer() {
        Chunk c = null;
        for (Chunk chunk : this) {
            if (Math.abs(player.getBoundsInParent().getCenterX() - chunk.getBoundsInParent().getCenterX()) <= chunk.CHUNK_BOUNDS &&
                    Math.abs(player.getBoundsInParent().getCenterY() - chunk.getBoundsInParent().getCenterY()) <= chunk.CHUNK_BOUNDS) {
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
            ChunkRendering render = new ChunkRendering(RENDER_DISTANCE, getChunkWithPlayer().CHUNK_BOUNDS,
                    getChunkWithPlayer(), this);
            if (!world.getChildren().contains(get(0)))
                get(0).updateChunk(world);
            if (world.getChildren().contains(get(0)) && !render.getChunksToRender().contains(get(0))) {
                get(0).removeChunk(world);
            }

            //Add chunks to render to world chunk list
            addAll(render.getChunksToRender());

            //Surrounding chunk rendering and de-rendering
            for (Chunk chunk : render.getChunksToRender()) {
                chunk.updateChunk(world);
            }
            for (Chunk chunk : this) {
                if (!render.getChunksToRender().contains(chunk)) {
                    chunk.removeChunk(world);
                }
            }
        }

    }
}
