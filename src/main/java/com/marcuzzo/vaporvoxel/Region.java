package com.marcuzzo.vaporvoxel;

import javafx.scene.Group;
import javafx.scene.image.Image;

import java.awt.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Region extends ChunkManager implements Serializable {
    private final int REGION_SIZE = 1024;
    //private final Point2D location;
    public Rectangle regionBounds;
    //private final TextureAtlas textures;
    private final Group world;

    /**
     * An object representing a 32x32 chunk area, 1024 chunks in total managed by a chunk manager.
     * @param x coordinate of the corner of this region
     * @param y coordinate of the corner of this region
     */
    public Region(int x, int y) {
        super(MainApplication.currentWorld.player, MainApplication.currentWorld.worldGroup);
        this.world = MainApplication.currentWorld.worldGroup;
       // this.location = new Point2D(x, y);
        regionBounds = new Rectangle(x, y, 512, 512);

        Map<String, javafx.scene.image.Image> textureMap = new HashMap<>();
        textureMap.put("grass_top", new javafx.scene.image.Image("/grass_top.png"));
        textureMap.put("grass_side", new javafx.scene.image.Image("/grass_side.png"));
        textureMap.put("dirt", new Image("/dirt.png"));
        TextureAtlas textures = new TextureAtlas(textureMap);

        if (x == 0 && y == 0 && this.size() == 0) {
            System.out.println("t");
            add(new Chunk(super.getTextures()).initialize(0, 0, 0));
            get(0).updateMesh();
            updateRender(world);
        }
    }

    @Override
    public String toString() {
        return "Region: (" + regionBounds.getX() + ", " + regionBounds.getY() + ")";
    }

}
