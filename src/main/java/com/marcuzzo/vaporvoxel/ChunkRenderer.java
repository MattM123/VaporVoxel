package com.marcuzzo.vaporvoxel;

import org.fxyz3d.geometry.Point3D;

import java.util.ArrayList;
import java.util.List;


public class ChunkRenderer {
    private static int renderDistance;
    private static int bounds;
    private static Chunk playerChunk;
    private static Player player;
    public static final List<Region> regions = new GlueList<>();

    /**
     *
     * Determines what chunks should be rendered around the player.
     * @param renderDistance Controls the range in which chunks should renderer.
     * @param bounds The length of the chunk.
     * @param playerChunk The chunk a player inhabits.
     */
    public ChunkRenderer(int renderDistance, int bounds, Chunk playerChunk, Player player) {
        ChunkRenderer.renderDistance = renderDistance;
        ChunkRenderer.bounds = bounds;
        ChunkRenderer.playerChunk = playerChunk;
        ChunkRenderer.player = player;
    }

    /**
     * Gets the chunks diagonally oriented from the chunk the player is in.
     * This includes each 4 quadrants surrounding the player. This does not include the chunks
     * aligned straight out from the player.
     *
     * @return A list of chunks that should be rendered diagonally from the chunk the
     * player is in.
     */
    private static List<Chunk> getQuadrantChunks() {
        List<Chunk> chunks = new ArrayList<>();
        //Top left quadrant
        Point3D TLstart = new Point3D(playerChunk.getLocation().getX() - bounds, playerChunk.getLocation().getY() + bounds, 0);
        for (int x = (int) TLstart.getX(); x > TLstart.getX() - (renderDistance * bounds); x -= bounds) {
            for (int y = (int) TLstart.getY(); y < TLstart.getY() + (renderDistance * bounds); y += bounds) {
                Chunk c = player.playerRegion.getChunkWithLocation(new Point3D(x, y, 0));
                if (c != null) {
                    chunks.add(c);
                    if (!regions.contains(c.getRegion()))
                        regions.add(c.getRegion());
                } else {
                    Chunk d = player.playerRegion.binaryInsertChunkWithLocation(0, player.playerRegion.size() - 1, new Point3D(x, y, 0));
                    chunks.add(d);
                    if (!regions.contains(d.getRegion()))
                        regions.add(d.getRegion());
                }
            }
        }


            //Top right quadrant
            Point3D TRStart = new Point3D(playerChunk.getLocation().getX() + bounds, playerChunk.getLocation().getY() + bounds, 0);
            for (int x = (int) TRStart.getX(); x < TRStart.getX() + (renderDistance * bounds); x += bounds) {
                for (int y = (int) TRStart.getY(); y < TRStart.getY() + (renderDistance * bounds); y += bounds) {
                    Chunk c = player.playerRegion.getChunkWithLocation(new Point3D(x, y, 0));
                    if (c != null) {
                        chunks.add(c);
                        if (!regions.contains(c.getRegion()))
                            regions.add(c.getRegion());
                    } else {
                        Chunk d = player.playerRegion.binaryInsertChunkWithLocation(0, player.playerRegion.size() - 1, new Point3D(x, y, 0));
                        chunks.add(d);
                        if (!regions.contains(d.getRegion()))
                            regions.add(d.getRegion());
                    }
                }
            }

            //Bottom right quadrant
            Point3D BRStart = new Point3D(playerChunk.getLocation().getX() - bounds, playerChunk.getLocation().getY() - bounds, 0);
            for (int x = (int) BRStart.getX(); x > BRStart.getX() - (renderDistance * bounds); x -= bounds) {
                for (int y = (int) BRStart.getY(); y > BRStart.getY() - (renderDistance * bounds); y -= bounds) {
                    Chunk c = player.playerRegion.getChunkWithLocation(new Point3D(x, y, 0));
                    if (c != null) {
                        chunks.add(c);
                        if (!regions.contains(c.getRegion()))
                            regions.add(c.getRegion());
                    } else {
                        Chunk d = player.playerRegion.binaryInsertChunkWithLocation(0, player.playerRegion.size() - 1, new Point3D(x, y, 0));
                        chunks.add(d);
                        if (!regions.contains(d.getRegion()))
                            regions.add(d.getRegion());
                    }
                }
            }

            //Bottom left quadrant
            Point3D BLStart = new Point3D(playerChunk.getLocation().getX() + bounds, playerChunk.getLocation().getY() - bounds, 0);
            for (int x = (int) BLStart.getX(); x < BLStart.getX() + (renderDistance * bounds); x += bounds) {
                for (int y = (int) BLStart.getY(); y > BLStart.getY() - (renderDistance * bounds); y -= bounds) {
                    Chunk c = player.playerRegion.getChunkWithLocation(new Point3D(x, y, 0));
                    if (c != null) {
                        chunks.add(c);
                        if (!regions.contains(c.getRegion()))
                            regions.add(c.getRegion());
                    } else {
                        Chunk d = player.playerRegion.binaryInsertChunkWithLocation(0, player.playerRegion.size() - 1, new Point3D(x, y, 0));
                        chunks.add(d);
                        if (!regions.contains(d.getRegion()))
                            regions.add(d.getRegion());
                    }
                }
            }

        return chunks;
    }
    /**
     * Gets the chunks that should be rendered along the X And Y axis. E.x a renderer distance
     * of 2 would return 8 chunks, 2 on every side of the player in each cardinal direction
     *
     * @return A list of chunks that should be rendered in x, y, -x, and -y directions
     */
    private static ArrayList<Chunk> getCardinalChunks() {
        ArrayList<Chunk> chunks = new ArrayList<>();
        //Positive X
        for (int i = 1; i <= renderDistance; i++) {
            Point3D p = new Point3D(playerChunk.getLocation().getX() + (i * bounds), playerChunk.getLocation().getY(), 0);
            Chunk c =  player.playerRegion.getChunkWithLocation(p);
            if (c != null) {
                chunks.add(c);
                if (!regions.contains(c.getRegion()))
                    regions.add(c.getRegion());
            }
            else {
                Chunk d = player.playerRegion.binaryInsertChunkWithLocation(0, player.playerRegion.size() - 1, p);
                chunks.add(d);
                if (!regions.contains(d.getRegion()))
                    regions.add(d.getRegion());
            }
        }

        //Negative X
        for (int i = 1; i <= renderDistance; i++) {
            Point3D p = new Point3D(playerChunk.getLocation().getX() - (i * bounds), playerChunk.getLocation().getY(), 0);
            Chunk c =  player.playerRegion.getChunkWithLocation(p);
            if (c != null) {
                chunks.add(c);
                if (!regions.contains(c.getRegion()))
                    regions.add(c.getRegion());
            }
            else {
                Chunk d = player.playerRegion.binaryInsertChunkWithLocation(0, player.playerRegion.size() - 1, p);
                chunks.add(d);
                if (!regions.contains(d.getRegion()))
                    regions.add(d.getRegion());
            }
        }

        //Positive Y
        for (int i = 1; i <= renderDistance; i++) {
            Point3D p = new Point3D(playerChunk.getLocation().getX(), playerChunk.getLocation().getY() + (i * bounds), 0);
            Chunk c =  player.playerRegion.getChunkWithLocation(p);
            if (c != null) {
                chunks.add(c);
                if (!regions.contains(c.getRegion()))
                    regions.add(c.getRegion());
            }
            else {
                Chunk d = player.playerRegion.binaryInsertChunkWithLocation(0, player.playerRegion.size() - 1, p);
                chunks.add(d);
                if (!regions.contains(d.getRegion()))
                    regions.add(d.getRegion());
            }
        }
        //Negative Y
        for (int i = 1; i <= renderDistance; i++) {
            Point3D p = new Point3D(playerChunk.getLocation().getX(), playerChunk.getLocation().getY() - (i * bounds), 0);
            Chunk c =  player.playerRegion.getChunkWithLocation(p);
            if (c != null) {
                chunks.add(c);
                if (!regions.contains(c.getRegion()))
                    regions.add(c.getRegion());
            }
            else {
                Chunk d = player.playerRegion.binaryInsertChunkWithLocation(0, player.playerRegion.size() - 1, p);
                chunks.add(d);
                if (!regions.contains(d.getRegion()))
                    regions.add(d.getRegion());
            }
        }
        return chunks;
    }
    /**
     * Returns a list of chunks that should be rendered around a player based on a render distance value
     * @return The list of chunks that should be rendered
     */
    public List<Chunk> getChunksToRender() {
        regions.clear();
        List<Chunk> chunks = new ArrayList<>();
        chunks.addAll(getQuadrantChunks());
        chunks.addAll(getCardinalChunks());
        chunks.add(playerChunk);


        return chunks;
    }

    public List<Region> getRegions() {
        return regions;
    }
}
