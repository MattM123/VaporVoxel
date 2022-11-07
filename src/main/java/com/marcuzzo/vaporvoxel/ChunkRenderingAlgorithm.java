package com.marcuzzo.vaporvoxel;

import org.fxyz3d.geometry.Point3D;

import java.util.ArrayList;


public class ChunkRenderingAlgorithm {
    private final int renderDistance;
    private final int bounds;
    private final Chunk playerChunk;
    private final ChunkManager manager;

    /**
     *
     * Determines what chunks should be rendered around the player.
     * @param renderDistance Controls the range in which chunks should render.
     * @param bounds The length of the chunk.
     * @param playerChunk The chunk a player inhabits.
     * @param manager The chunk manager that is responsible for rendering,
     *                de-rendering and updating chunks
     */
    public ChunkRenderingAlgorithm(int renderDistance, int bounds, Chunk playerChunk, ChunkManager manager) {
        this.renderDistance = renderDistance;
        this.bounds = bounds;
        this.playerChunk = playerChunk;
        this.manager = manager;
    }

    /**
     * Gets the chunks that should be rendered along the X And Y axis. E.x a render distance
     * of 2 would return 8 chunks, 2 on each side of the player
     *
     * @return A list of chunks that should be rendered in x, y, -x, and -y directions
     */
    public ArrayList<Chunk> getCardinalChunks() {
        ArrayList<Chunk> chunks = new ArrayList<>();
        //Positive X
        for (int i = 1; i < renderDistance + 1; i++) {
            if (manager.containsChunkWithLocation(new Point3D(playerChunk.getLocation().getX() + (i * bounds), playerChunk.getLocation().getY(), 0))) {
                chunks.add(manager.getChunkWithLocation(new Point3D(playerChunk.getLocation().getX() + (i * bounds), playerChunk.getLocation().getY(), 0)));
            }
            else {
                chunks.add(new Chunk().initialize((int) playerChunk.getLocation().getX() + (i * bounds), (int) playerChunk.getLocation().getY(), 0));
            }
        }

        //Negative X
        for (int i = 1; i < renderDistance + 1; i++) {
            if (manager.containsChunkWithLocation(new Point3D(playerChunk.getLocation().getX() - (i * bounds), playerChunk.getLocation().getY(), 0))) {
                chunks.add(manager.getChunkWithLocation(new Point3D(playerChunk.getLocation().getX() - (i * bounds), playerChunk.getLocation().getY(), 0)));
            }
            else {
                chunks.add(new Chunk().initialize((int) playerChunk.getLocation().getX() - (i * bounds), (int) playerChunk.getLocation().getY(), 0));
            }
        }

        //Positive Y
        for (int i = 1; i < renderDistance + 1; i++) {
            if (manager.containsChunkWithLocation(new Point3D(playerChunk.getLocation().getX(), playerChunk.getLocation().getY() + (i * bounds), 0))) {
                chunks.add(manager.getChunkWithLocation(new Point3D(playerChunk.getLocation().getX(), playerChunk.getLocation().getY() + (i * bounds), 0)));
            }
            else {
                chunks.add(new Chunk().initialize((int) playerChunk.getLocation().getX(), (int) playerChunk.getLocation().getY() + (i * bounds), 0));
            }
        }
        //Negative Y
        for (int i = 1; i < renderDistance + 1; i++) {
            if (manager.containsChunkWithLocation(new Point3D(playerChunk.getLocation().getX(), playerChunk.getLocation().getY() - (i * bounds), 0))) {
                chunks.add(manager.getChunkWithLocation(new Point3D(playerChunk.getLocation().getX(), playerChunk.getLocation().getY() - (i * bounds), 0)));
            }
            else {
                chunks.add(new Chunk().initialize((int) playerChunk.getLocation().getX(), (int) playerChunk.getLocation().getY() - (i * bounds), 0));
            }
        }
        return chunks;
    }
    /**
     * When determining the chunks to render around the player, first a starting chunk is found. This is
     * the upper left most chunk within render distance based around the chunk the player is currently
     * standing in. From there it iterates in a left-to-right, up-to-down
     * fashion and adds the chunks that should be rendered to the output list.
     * @return The list of chunks that should be rendered
     */
    public ArrayList<Chunk> getChunksToRender() {
        ArrayList<Chunk> chunks = new ArrayList<>();
        chunks.add(playerChunk);
        chunks.addAll(getCardinalChunks());
        return chunks;
    }
}
