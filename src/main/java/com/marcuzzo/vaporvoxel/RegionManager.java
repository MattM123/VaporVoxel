package com.marcuzzo.vaporvoxel;

import javafx.geometry.Point2D;
import javafx.scene.Group;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class RegionManager extends GlueList<Region> {
    public final Player player;
    private final List<Region> visibleRegions = new GlueList<>();
    public Path worldDir;
    public Group worldGroup;

    /**
     * The highest level object representing a world
     * @param player The player within the world
     * @param path The path of this worlds directory
     */
    public RegionManager(Player player, Path path, Group world) {
        this.worldGroup = world;
        this.worldDir = path;
        this.player = player;
    }

    private void loadSpawnRegions() {
        DirectoryStream<Path> regionStream = null;
        try {
            Files.createDirectories(Paths.get(worldDir + "/regions"));
            regionStream = Files.newDirectoryStream(Paths.get(worldDir + "/regions"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        File[] regionFiles = new File(worldDir + "/regions").listFiles((dir, name) -> name.equals("0.0.0"));
        if (Arrays.stream(regionFiles).toList().contains(new File("0.0.0"))) {
            System.out.println("Contains");
            new Region(0,0);

        } else {

        }


        }

    /**
     * Gets the region that the player currently inhabits.
     * @return The region that the player is in
     */
    public Region getRegionWithPlayer() {
        Region r = null;

        Point2D playerLoc = new Point2D(player.getBoundsInParent().getCenterX(), player.getBoundsInParent().getCenterY());

        Point2D e = new Point2D(playerLoc.getX() - (playerLoc.getX() % 512), playerLoc.getY() - (playerLoc.getY() % 512));
        if (visibleRegions.stream().filter(c -> c.regionBounds.getX() == e.getX() && c.regionBounds.getY() == e.getY()).findFirst().orElse(null) == null) {
            visibleRegions.add(new Region((int) e.getX(), (int) e.getY()));
        } else {
            return visibleRegions.stream().filter(c -> c.regionBounds.getX() == e.getX() && c.regionBounds.getY() == e.getY()).findFirst().get();
        }
        System.out.println(e);

        return r;
    }
}
