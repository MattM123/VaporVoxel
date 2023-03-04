package com.marcuzzo.vaporvoxel;

import com.marcuzzo.vaporvoxel.EventTypes.PlayerEvent;
import com.marcuzzo.vaporvoxel.Events.RegionTransitionEvent;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;

import java.io.Serializable;

public class Player extends PerspectiveCamera implements Serializable {
    public Chunk playerChunk;
    public Region playerRegion;


    public Player(boolean b, Group world) {
        super(b);
        addEventHandler(PlayerEvent.REGION_TRANSITION, transitionEvent -> {
            playerRegion = RegionManager.getRegionWithPlayer();

            //Calculates new regions to render or re-render
            MainApplication.currentWorld.updateRender();

        });

        addEventHandler(PlayerEvent.CHUNK_TRANSITION, transitionEvent -> {
            if (playerRegion != RegionManager.getRegionWithPlayer())
                this.fireEvent(new RegionTransitionEvent(PlayerEvent.REGION_TRANSITION));

       //     System.out.println(playerRegion.getChunkWithPlayer() + " Fffchunk");
        //    System.out.println(playerRegion + " Fffreg");
            playerChunk = playerRegion.getChunkWithPlayer();

            //Calculates new chunks to render or re-render
            Platform.runLater(() -> playerRegion.updateRender(world));
        });
    }
}
