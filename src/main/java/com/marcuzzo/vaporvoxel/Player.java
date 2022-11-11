package com.marcuzzo.vaporvoxel;

import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;

public class Player extends PerspectiveCamera {
    private Chunk playerChunk;
    private ChunkManager manager;
    private final Group world;
    public Player(boolean b, Group world) {
        super(b);
        this.world = world;
    }

    /**
     * Detects player movement from one chunk to another. Used to tell the chunk manager
     * when to render and de-render chunks surrounding a player.
     */
    public void checkChunk() {
        if (playerChunk != manager.getChunkWithPlayer()) {
            playerChunk = manager.getChunkWithPlayer();

            //De-renders surrounding chunks
            for (int i = 0; i < ChunkManager.render.getChunksToRender().size(); i++) {
                world.getChildren().remove(ChunkManager.render.getChunksToRender().get(i));
            }
            manager.updateRender(world);
        }
    }
    public void setManager(ChunkManager manager) {
        this.manager = manager;
        this.playerChunk =  manager.getChunkWithPlayer();
    }
}
