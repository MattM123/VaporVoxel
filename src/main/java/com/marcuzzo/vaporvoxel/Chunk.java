package com.marcuzzo.vaporvoxel;
import javafx.scene.layout.Region;
import javafx.scene.Group;

public class Chunk extends Region {
    private final Cube[][][] chunk = new Cube[16][64][16];
    public Chunk() {
        int vFlag = 0;
        for (int x = 0; x < chunk.length; x++) {
            for (int y = 0; y < chunk.length; y++) {
                for (int z = 0; z < chunk.length; z++) {
                    Cube c = new Cube();
                    vFlag++;
                    if (vFlag > 16) {
                        vFlag = 1;
                    }
                    c.setTranslateZ(c.getTranslateZ() + vFlag);
                    c.resizeRelocate(x, y, 1, 1);
                    chunk[x][y][z] = c;
                    System.out.println(chunk[x][y][z]);
                }
            }
        }
    }

    public void addToWorld(Group world) {
        for (Cube[][] cubes : chunk) {
            for (int y = 0; y < chunk.length; y++) {
                for (int z = 0; z < chunk.length; z++) {
                    if (cubes[y][z].isActive())
                        world.getChildren().add(cubes[y][z]);
                }
            }
        }
    }
}
