package com.marcuzzo.vaporvoxel;

import org.fxyz3d.geometry.Point3D;

import java.util.ArrayList;
import java.util.List;


public class ChunkRenderer {
    private final int renderDistance;
    private final int bounds;
    private final Chunk playerChunk;
    private final ChunkManager manager;
    private final TextureAtlas textures;

    /**
     *
     * Determines what chunks should be rendered around the player.
     * @param renderDistance Controls the range in which chunks should render.
     * @param bounds The length of the chunk.
     * @param playerChunk The chunk a player inhabits.
     * @param manager The chunk manager that is responsible for rendering,
     *                de-rendering and updating chunks
     */
    public ChunkRenderer(int renderDistance, int bounds, Chunk playerChunk, ChunkManager manager, TextureAtlas textures) {
        this.textures = textures;
        this.renderDistance = renderDistance;
        this.bounds = bounds;
        this.playerChunk = playerChunk;
        this.manager = manager;
    }

    /**
     * Gets the chunks diagonally oriented from the chunk the player is in.
     * This includes each 4 quadrants surrounding the player. This does not include the chunks
     * aligned straight out from the player.
     *
     * @return A list of chunks that should be rendered diagonally from the chunk the
     * player is in.
     */
    private List<Chunk> getQuadrantChunks() {
        List<Chunk> chunks = new ArrayList<>();
        //Top left quadrant
        Point3D TLstart = new Point3D(playerChunk.getLocation().getX() - bounds, playerChunk.getLocation().getY() + bounds, 0);
        for (int x = (int) TLstart.getX(); x > TLstart.getX() - (renderDistance * bounds); x -= bounds) {
            for (int y = (int) TLstart.getY(); y < TLstart.getY() + (renderDistance * bounds); y += bounds) {
                if (manager.containsChunkWithLocation(new Point3D(x, y, 0))) {
                    chunks.add(manager.getChunkWithLocation(new Point3D(x, y, 0)));
                } else {
                    chunks.add(new Chunk(textures).initialize(x, y, 0));
                }
            }
        }


            //Top right quadrant
            Point3D TRStart = new Point3D(playerChunk.getLocation().getX() + bounds, playerChunk.getLocation().getY() + bounds, 0);
            for (int x = (int) TRStart.getX(); x < TRStart.getX() + (renderDistance * bounds); x += bounds) {
                for (int y = (int) TRStart.getY(); y < TRStart.getY() + (renderDistance * bounds); y += bounds) {
                    if (manager.containsChunkWithLocation(new Point3D(x, y, 0))) {
                        chunks.add(manager.getChunkWithLocation(new Point3D(x, y, 0)));
                    } else {
                        chunks.add(new Chunk(textures).initialize(x, y, 0));
                    }
                }
            }

            //Bottom right quadrant
            Point3D BRStart = new Point3D(playerChunk.getLocation().getX() - bounds, playerChunk.getLocation().getY() - bounds, 0);
            for (int x = (int) BRStart.getX(); x > BRStart.getX() - (renderDistance * bounds); x -= bounds) {
                for (int y = (int) BRStart.getY(); y > BRStart.getY() - (renderDistance * bounds); y -= bounds) {
                    if (manager.containsChunkWithLocation(new Point3D(x, y, 0))) {
                        chunks.add(manager.getChunkWithLocation(new Point3D(x, y, 0)));
                    } else {
                        chunks.add(new Chunk(textures).initialize(x, y, 0));
                    }
                }
            }

            //Bottom left quadrant
            Point3D BLStart = new Point3D(playerChunk.getLocation().getX() + bounds, playerChunk.getLocation().getY() - bounds, 0);
            for (int x = (int) BLStart.getX(); x < BLStart.getX() + (renderDistance * bounds); x += bounds) {
                for (int y = (int) BLStart.getY(); y > BLStart.getY() - (renderDistance * bounds); y -= bounds) {
                    if (manager.containsChunkWithLocation(new Point3D(x, y, 0))) {
                        chunks.add(manager.getChunkWithLocation(new Point3D(x, y, 0)));
                    } else {
                        chunks.add(new Chunk(textures).initialize(x, y, 0));
                    }
                }
            }

        return chunks;
    }
    /**
     * Gets the chunks that should be rendered along the X And Y axis. E.x a render distance
     * of 2 would return 8 chunks, 2 on every side of the player in each cardinal direction
     *
     * @return A list of chunks that should be rendered in x, y, -x, and -y directions
     */
    private ArrayList<Chunk> getCardinalChunks() {
        ArrayList<Chunk> chunks = new ArrayList<>();
        //Positive X
        for (int i = 1; i <= renderDistance; i++) {
            if (manager.containsChunkWithLocation(new Point3D(playerChunk.getLocation().getX() + (i * bounds), playerChunk.getLocation().getY(), 0))) {
                chunks.add(manager.getChunkWithLocation(new Point3D(playerChunk.getLocation().getX() + (i * bounds), playerChunk.getLocation().getY(), 0)));
            }
            else {
                chunks.add(new Chunk(textures).initialize((int) playerChunk.getLocation().getX() + (i * bounds), (int) playerChunk.getLocation().getY(), 0));
            }
        }

        //Negative X
        for (int i = 1; i <= renderDistance; i++) {
            if (manager.containsChunkWithLocation(new Point3D(playerChunk.getLocation().getX() - (i * bounds), playerChunk.getLocation().getY(), 0))) {
                chunks.add(manager.getChunkWithLocation(new Point3D(playerChunk.getLocation().getX() - (i * bounds), playerChunk.getLocation().getY(), 0)));
            }
            else {
                chunks.add(new Chunk(textures).initialize((int) playerChunk.getLocation().getX() - (i * bounds), (int) playerChunk.getLocation().getY(), 0));
            }
        }

        //Positive Y
        for (int i = 1; i <= renderDistance; i++) {
            if (manager.containsChunkWithLocation(new Point3D(playerChunk.getLocation().getX(), playerChunk.getLocation().getY() + (i * bounds), 0))) {
                chunks.add(manager.getChunkWithLocation(new Point3D(playerChunk.getLocation().getX(), playerChunk.getLocation().getY() + (i * bounds), 0)));
            }
            else {
                chunks.add(new Chunk(textures).initialize((int) playerChunk.getLocation().getX(), (int) playerChunk.getLocation().getY() + (i * bounds), 0));
            }
        }
        //Negative Y
        for (int i = 1; i <= renderDistance; i++) {
            if (manager.containsChunkWithLocation(new Point3D(playerChunk.getLocation().getX(), playerChunk.getLocation().getY() - (i * bounds), 0))) {
                chunks.add(manager.getChunkWithLocation(new Point3D(playerChunk.getLocation().getX(), playerChunk.getLocation().getY() - (i * bounds), 0)));
            }
            else {
                chunks.add(new Chunk(textures).initialize((int) playerChunk.getLocation().getX(), (int) playerChunk.getLocation().getY() - (i * bounds), 0));
            }
        }
        return chunks;
    }
    /**
     * Returns a list of chunks that should be rendered around a player based on a render distance value
     * @return The list of chunks that should be rendered
     */
    public List<Chunk> getChunksToRender() {
        List<Chunk> chunks = new ArrayList<>();
        List<Chunk> qChunk = getQuadrantChunks();
        List<Chunk> cChunk = getCardinalChunks();

        chunks.add(playerChunk);
        chunks.addAll(qChunk);
        chunks.addAll(cChunk);

        return chunks;
    }
}
