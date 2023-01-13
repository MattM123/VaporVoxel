package com.marcuzzo.vaporvoxel;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.image.Image;
import org.fxyz3d.geometry.Point3D;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkManager extends GlueList<Chunk> {
    private final Player player;
    public final int RENDER_DISTANCE = 5;
    public static ChunkRenderer render;
    private final TextureAtlas textures;

    public ChunkManager(Player player, Group world) {

        Map<String, Image> textureMap = new HashMap<>();
        textureMap.put("grass_top", new Image("/grass_top.png"));
        textureMap.put("grass_side", new Image("/grass_side.png"));
        textures = new TextureAtlas((textureMap));

        this.player = player;
        player.setManager(this);
        add(new Chunk(textures).initialize(0, 0, 0));
        get(0).updateMesh();
        updateRender(world);
    }

    public Chunk getChunkWithPlayer() {
        Chunk c = null;
        if (render != null) {
            for (Chunk chunk : this) {
                if (Math.abs(player.getBoundsInParent().getCenterX() - chunk.getBoundsInParent().getCenterX()) <= chunk.CHUNK_BOUNDS &&
                        Math.abs(player.getBoundsInParent().getCenterY() - chunk.getBoundsInParent().getCenterY()) <= chunk.CHUNK_BOUNDS) {
                    c = chunk;
                    break;
                }
            }
        }
        if (size() == 1) {
            c = get(0);
        }
        return c;
    }

    public boolean containsChunkWithLocation(final Point3D loc) {
        return stream().map(Chunk::getLocation).anyMatch(loc::equals);
    }

    /**
     * Gets a chunk from the manager that is located in a specific position. This location is the same
     * location that was used when the chunk was initialized. If no chunk is found with the location
     * null is returned
     *
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

    /**
     * Updates the chunks surrounding the player to be added to the world if in render distance.
     * Removes chunks from world that are no longer in render distance.
     * @param world The root group of the scene
     */
    public void updateRender(Group world) {
        if (getChunkWithPlayer() != null) {

            //Spawn chunk rendering and de-rendering
            render = new ChunkRenderer(RENDER_DISTANCE, getChunkWithPlayer().CHUNK_BOUNDS,
                    getChunkWithPlayer(), this, textures);

            List<Chunk> cList = render.getChunksToRender();

            if (!world.getChildren().contains(get(0)))
                world.getChildren().add(get(0));
            if (world.getChildren().contains(get(0)) && !cList.contains(get(0))) {
                world.getChildren().remove(get(0));
            }

            //Add chunks marked for rendering to world chunk list if they don't already exist
            for (Chunk chunk : cList) {
                Platform.runLater(() -> {
                    if (!contains(chunk)) {
                        add(chunk);
                        chunk.updateMesh();
                    }

                    if (!world.getChildren().contains(chunk))
                          world.getChildren().add(chunk);
              });
            }
        }
    }
}