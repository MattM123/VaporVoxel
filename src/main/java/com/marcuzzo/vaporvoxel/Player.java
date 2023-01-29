package com.marcuzzo.vaporvoxel;

import com.marcuzzo.vaporvoxel.EventTypes.PlayerEvent;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;

public class Player extends PerspectiveCamera {
    public Chunk playerChunk;
    private ChunkManager manager;

    public Player(boolean b, Group world) {
        super(b);
        addEventHandler(PlayerEvent.CHUNK_TRANSITION, transitionEvent -> {
            playerChunk = manager.getChunkWithPlayer();

            //Calculates new chunks to render or re-render
            Platform.runLater(() -> manager.updateRender(world));
        });
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
