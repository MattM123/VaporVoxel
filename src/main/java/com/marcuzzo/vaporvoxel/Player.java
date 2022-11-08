package com.marcuzzo.vaporvoxel;

import javafx.application.Platform;
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
            Thread t = new Thread(() -> Platform.runLater(() -> {
                playerChunk = manager.getChunkWithPlayer();
                manager.updateRender(world);
            }));
            t.start();
        }
    }

    public void setManager(ChunkManager manager) {
        this.manager = manager;
        this.playerChunk =  manager.getChunkWithPlayer();
    }
}
