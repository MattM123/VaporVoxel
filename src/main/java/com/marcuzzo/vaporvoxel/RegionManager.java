package com.marcuzzo.vaporvoxel;

import com.marcuzzo.vaporvoxel.EventTypes.PlayerEvent;
import com.marcuzzo.vaporvoxel.Events.ChunkTransitionEvent;
import com.marcuzzo.vaporvoxel.Events.RegionTransitionEvent;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.image.Image;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class RegionManager extends GlueList<Region> {
    public static Player player = null;
    public static List<Region> visibleRegions = new GlueList<>();
    public static Path worldDir;
    public static Group worldGroup;
    public static TextureAtlas textures = null;
    //private static List<Region> rList = new GlueList<>();
    public static ChunkRenderer renderer;
    public static final int RENDER_DISTANCE = 6;

    /**
     * The highest level object representing a world
     *
     * @param player The player within the world
     * @param path   The path of this worlds directory
     */
    public RegionManager(Player player, Path path, Group world) {
        try {
            Files.createDirectories(Paths.get(worldDir + "\\regions\\"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        RegionManager.worldGroup = world;
        RegionManager.worldDir = path;
        RegionManager.player = player;

        Map<String, Image> textureMap = new HashMap<>();
        try {
            DirectoryStream<Path> textureStream = Files.newDirectoryStream(Paths.get("src/main/resources/textures"));
            Iterator<Path> textureIterator = textureStream.iterator();
            int dirLen = Objects.requireNonNull(new File("src/main/resources/textures").listFiles()).length;

            if (dirLen > 0) {
                textureIterator.forEachRemaining(c -> {
                    String name = c.getFileName().toString();
                    //   Texture texture;
                    //   try {
                    //       texture = TextureIO.newTexture(TextureIO.newTextureData(GLProfile.getDefault(), new File(name), false, "png"));
                    //   } catch (IOException e) {
                    //       throw new RuntimeException(e);
                    //   }

                    //  int width = texture.getImageWidth();
                    //  int height = texture.getImageHeight();


                    textureMap.put(name.substring(0, name.length() - 4), new Image("/" + name));
                });
                textures = new TextureAtlas(textureMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public static Point2D getRegionCoordsWithPlayer() {
        Point2D loc = new Point2D(player.getBoundsInParent().getCenterX(), player.getBoundsInParent().getCenterY());
        return new Point2D(loc.getX() - Math.floorMod((int) loc.getX(), 512), loc.getY() - Math.floorMod((int) loc.getY(), 512));
    }

    /**
     * Generates the first region after a world is created. After which regions will be automatically
     * generated based on player movement around the world.
     */
    /*
    public void loadSpawnRegions() {
        Region origin = enterRegion(new Region(0, 0));
        origin.add(new Chunk(RegionManager.textures).initialize(
                (int) (player.getBoundsInParent().getCenterX() - (player.getBoundsInParent().getCenterX() % 16)),
                (int) (player.getBoundsInParent().getCenterY() - (player.getBoundsInParent().getCenterY() % 16)), 0));

        spawn.add(origin);
        spawn.add(enterRegion(new Region(0, -512)));
        spawn.add(enterRegion(new Region(-512, -512)));
        spawn.add(enterRegion(new Region(-512, 0)));

      //  player.fireEvent(new RegionTransitionEvent(PlayerEvent.REGION_TRANSITION));
    }



    public static List<Region> getSpawnRegions() {
        return spawn;
    }

     */

    /**
     * Gets the region that the player currently inhabits.
     * @return The region that the player is in
     */
    public static Region getRegionWithPlayer() {
        Point2D playerLoc = new Point2D(player.getBoundsInParent().getCenterX(), player.getBoundsInParent().getCenterY());

        Point2D q = new Point2D(playerLoc.getX() - Math.floorMod((int) playerLoc.getX(), 512),
                playerLoc.getY() - Math.floorMod((int) playerLoc.getY(), 512));

        for (Region r : visibleRegions) {
            if ((int) r.regionBounds.getX() == (int) q.getX()
                    && (int) r.regionBounds.getY() == (int) q.getY()) {
                return r;
            }
        }

        //Returns new region it one does not exist
        Region e = new Region((int) q.getX(), (int) q.getY());
        visibleRegions.add(e);
        return e;
    }

    /*
    /**
     * Gets the coordinates of the region the player inhabits.
     * @return The region coordinates
     */
    /*
    public static Region getRegionFromLocation(int x, int y) {
        Point2D p = new Point2D(x - Math.floorMod(x, 512), y - Math.floorMod(y, 512));
        return new Region((int) p.getX(), (int) p.getY());
    }

     */


    /**
     * Removes a region from the visible regions once a player leaves a region and
     * their render distance no longer overlaps it.
     *
     * @param r The region to leave
     */
    public static void leaveRegion(Region r) {
        try {
            File[] regionFiles = new File(worldDir + "\\regions\\").listFiles((dir, name) ->
                    name.equals((int) r.regionBounds.getX() + "." + (int) r.regionBounds.getY() + ".dat"));

            //Writes region to file and removes from visibility
            //If region file already exists
            assert regionFiles != null;
            if (!Arrays.stream(regionFiles).toList().isEmpty()) {
                FileOutputStream f = new FileOutputStream(Arrays.stream(regionFiles).toList().get(0));
                ObjectOutputStream o = new ObjectOutputStream(f);
                Platform.runLater(() -> {
                    try {
                        o.writeObject(r);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                visibleRegions.remove(r);
                Logger.getLogger("Logger").info("[Exited Region] " + r);
            }
            //If region file does not already exist
            else {
                FileOutputStream f = new FileOutputStream(worldDir + "\\regions\\"
                        + (int) r.regionBounds.getX() + "." + (int) r.regionBounds.getY() + ".dat");
                ObjectOutputStream o = new ObjectOutputStream(f);
                Platform.runLater(() -> {
                    try {
                        o.writeObject(r);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                visibleRegions.remove(r);
                Logger.getLogger("Logger").info("[Exited Region] " + r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates or loads an already generated chunk from filesystem when the players
     * render distance intersects with the regions bounds.
     */
    public static Region enterRegion(Region r) {
        try {
            File[] regionFiles = new File(worldDir + "\\regions\\").listFiles((dir, name) ->
                    name.equals((int) r.regionBounds.getX() + "." + (int) r.regionBounds.getY() + ".dat"));

            Region match = visibleRegions.stream().filter(p -> p.regionBounds.getX() == r.regionBounds.getX()
                    && p.regionBounds.getY() == r.regionBounds.getY()).findFirst().orElse(
                    new Region((int) r.regionBounds.getX(), (int) r.regionBounds.getY()));

            //Gets region from files if it's written to file but not visible
            assert regionFiles != null;
            if (!visibleRegions.contains(match) && !Arrays.stream(regionFiles).toList().isEmpty()) {
                FileInputStream f = new FileInputStream(worldDir + "\\regions\\"
                        + (int) r.regionBounds.getX() + "." + (int) r.regionBounds.getY() + ".dat");
                ObjectInputStream o = new ObjectInputStream(f);
                Region q = (Region) o.readObject();
                if (q != null) {
                    visibleRegions.add(q);
                    player.fireEvent(new RegionTransitionEvent(PlayerEvent.REGION_TRANSITION));
                    Logger.getLogger("Logger").info("[Entered Region] " + r);
                    return q;
                }
            }

            //if region is not visible and not written to files creates new region
            else if (!visibleRegions.contains(match) && Arrays.stream(regionFiles).toList().isEmpty()) {
                visibleRegions.add(r);
                player.fireEvent(new RegionTransitionEvent(PlayerEvent.REGION_TRANSITION));
                Logger.getLogger("Logger").info("[Entered Region] " + r);
                return r;
            }

        } catch (Exception e) {
            visibleRegions.add(r);
            player.fireEvent(new RegionTransitionEvent(PlayerEvent.REGION_TRANSITION));
            Logger.getLogger("Logger").info("[Entered Region] " + r);
            return r;
        }
        Logger.getLogger("Logger").info("[Entered Region] " + r);
        return new Region((int) r.regionBounds.getX(), (int) r.regionBounds.getY());
    }

/*
    /**
     * Gets a region from file system that contains a given location.
     * If the region has not been generated and is not visible it generates it and
     * makes it visible by calling enterRegion(x, y)
     *
     * @param x coordinate of location
     * @param y coordinate of location
     */
    /*
    public static Region getIntersectingRegion(int x, int y) {
        Point2D p = //getRegionCoordsFromLocation(x, y);
        new Point2D(0,0);
        try {
            File[] regionFiles = new File(worldDir + "\\regions\\").listFiles((dir, name)
                    -> name.equals((int) p.getX() + "." + (int) p.getY() + ".dat"));

            //reads region from files when entering a region
            assert regionFiles != null;
            if (!Arrays.stream(regionFiles).toList().contains(
                    new File(worldDir + "\\regions\\" + (int) p.getX() + "." + (int) p.getY() + ".dat"))) {

                FileInputStream f = new FileInputStream(worldDir + "\\regions\\" + (int) p.getX() + "." + (int) p.getY() + ".dat");
                ObjectInputStream o = new ObjectInputStream(f);

                if (o.readObject() == null)
                    return new Region((int) p.getX(), (int) p.getY());
                else
                    return (Region) o.readObject();
            }

        } catch (Exception ignored) {
            return new Region(x, y);
        }
        return new Region((int) p.getX(), (int) p.getY());
    }

     */

    /**
     * Updates the regions surrounding the player to be read from file if in renderer distance.
     * Also removes and writes regions to file that are no longer in render distance.
     */
    public void updateRender() {
        List<Region> rList = new GlueList<>();

        if (renderer == null) {
            renderer = new ChunkRenderer(RENDER_DISTANCE, Chunk.CHUNK_BOUNDS,
                    getRegionWithPlayer().getChunkWithPlayer(), player);

            rList.add(enterRegion(getRegionWithPlayer()));
        }
        else {
            rList = renderer.getRegions();
        }
        player.fireEvent(new ChunkTransitionEvent(PlayerEvent.CHUNK_TRANSITION));


        //Updates regions to render
        List<Region> visible = RegionManager.visibleRegions;
        if (visible.size() > 0) {
            List<Region> toEnter = new GlueList<>();
            List<Region> toLeave = new GlueList<>();
            List<Region> finalRList = rList;
            Future<?> f1 = MainApplication.executor.submit(() -> {
                //First leave regions no longer in render distance
                for (Region r : visible)
                    try {
                        finalRList.stream().filter(f -> f.equals(r)).findFirst().get();
                    } catch (NoSuchElementException ignored) {
                        toLeave.add(r);
                    }
                //Then enter new regions within render distances
                for (Region r : finalRList) {
                    try {
                        visible.stream().filter(f -> f.equals(r)).findFirst().get();
                    } catch (NoSuchElementException ignored) {
                        toEnter.add(r);
                    }
                }
            });
            try {
                f1.get();
            } catch (Exception e) {
                e.printStackTrace();
            }


            for (Region r : toLeave) {
                RegionManager.leaveRegion(r);
            }
            for (Region r : toEnter) {
                RegionManager.enterRegion(r);
            }
        }
    }
}
