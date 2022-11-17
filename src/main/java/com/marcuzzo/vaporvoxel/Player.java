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
            MainApplication.chunkUpdater.start();

            //De-renders surrounding chunks
            for (int i = 0; i < ChunkManager.render.getChunksToRender().size(); i++) {
                world.getChildren().remove(ChunkManager.render.getChunksToRender().get(i));
            }
            manager.updateRender(world);
            playerChunk = manager.getChunkWithPlayer();
        }
    }
    public void setManager(ChunkManager manager) {
        this.manager = manager;
        this.playerChunk =  manager.getChunkWithPlayer();
    }

    /*
    public Rectangle getViewport() {
        double z = getBoundsInParent().getCenterZ();
        double w = world.getScene().getWidth();
        double h = world.getScene().getHeight();
        double f = getFieldOfView();

        Rectangle r = new Rectangle(-z * (w/h) * Math.tan(f/2), -z * Math.tan(f/2));
        r.setX(getBoundsInLocal().getMinX());
        r.setY(getBoundsInLocal().getMinY());
        r.setFill(Color.GREEN);
        r.setCursor(Cursor.CROSSHAIR);
        System.out.println("W: " + r.getWidth() + " H: " + r.getHeight());
        return r;

        /*
        So, in summary, the bounds of the scene extend from

        (-z (w/h) tan(f/2), -z tan(f/2))
           in the top left, to

        (w + z (w/h) tan(f/2), h + z tan(f/2))
        in the bottom right, where z is the z-coordinate, w the width of the scene,
        h the height of the scene, and f the (vertical) angle of the field of view.
         */
   // }



}
