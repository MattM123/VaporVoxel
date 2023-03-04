package com.marcuzzo.vaporvoxel;

import javafx.scene.Group;

import java.awt.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.stream.Stream;

public class Region extends ChunkManager implements Serializable {
    public Rectangle regionBounds;

    /**
     * An object representing a 32x32 chunk area, 1024 chunks in total managed by a chunk manager.
     * @param x coordinate of the corner of this region
     * @param y coordinate of the corner of this region
     */
    public Region(int x, int y) {
        super(MainApplication.testCamera);
        Group world = RegionManager.worldGroup;
        regionBounds = new Rectangle(x, y, 512, 512);

        if (RegionManager.getRegionCoordsWithPlayer().getX() == x && RegionManager.getRegionCoordsWithPlayer().getY() == y) {
            add(getChunkWithPlayer());
            updateRender(world);
        }
    }

    @Serial
    private void writeObject(ObjectOutputStream o) {
        try {
            o.writeObject(this);
            /*
            for (Chunk c : this) {
                Future<?> f = MainApplication.executor.submit(() -> {
                    try {
                        System.out.println("write to region");
                        o.writeObject(c);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                try {
                    f.get();
                } catch (Exception ignored) {
                }
            }

             */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Serial
    private void readObject(ObjectInputStream o) {
        try {
            Object o1 = o.readObject();
            Stream.of(o1).forEach(c -> this.add((Chunk) c));
            /*
            for (int i = 0; i < this.size; i++) {
                Future<?> f = MainApplication.executor.submit(() -> {
                    try {
                        System.out.println("read from region");
                        o.readObject();
                    } catch (IOException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
                try {
                    f.get();
                } catch (Exception ignored) {
                }
            }

             */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Region r) {
            return this.regionBounds.x == r.regionBounds.x && this.regionBounds.y == r.regionBounds.y;
        }
        return false;
    }
    @Override
    public String toString() {
        return "Region: (" + regionBounds.getX() + ", " + regionBounds.getY() + ")";
    }

}
