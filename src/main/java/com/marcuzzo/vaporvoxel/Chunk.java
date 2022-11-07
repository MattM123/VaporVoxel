package com.marcuzzo.vaporvoxel;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import org.fxyz3d.geometry.Point3D;
import org.fxyz3d.shapes.primitives.ScatterMesh;
import org.fxyz3d.shapes.primitives.helper.MarkerFactory;

import java.util.ArrayList;

public class Chunk extends MeshView {
    private Point3D location;
    private final int CHUNK_BOUNDS = 16;
    private final int CHUNK_HEIGHT = 16;
    private final Cube[][][] chunk = new Cube[CHUNK_BOUNDS][CHUNK_BOUNDS][CHUNK_HEIGHT];

    public Chunk() {

    }
    public Chunk initialize(int x, int y, int z) {
        int vFlag = 0;
        location = new Point3D(x, y, z);
        for (int i = 0; i < chunk.length; i++) {
            for (int j = 0; j < chunk.length; j++) {
                for (int k = 0; k < CHUNK_HEIGHT; k++) {
                    Cube c = new Cube();
                    vFlag++;
                    if (vFlag > CHUNK_HEIGHT)
                        vFlag = 1;
                    c.setTranslateZ(c.getTranslateZ() + vFlag);
                    c.resizeRelocate(location.getX() + i, location.getY() + j, c.getBoundsInLocal().getWidth(), c.getBoundsInLocal().getHeight());
                    chunk[i][j][k] = c;
                }
            }
        }
        return this;
    }
    /**
     * Iterates through a three-dimensional array of cubes and adds their upper left vertices to a
     * mesh used for the chunks structure. Adds a "blank" chunks to the world.
     * @param world Game world where objects are spawned
     */
    public void addToWorld(Group world) {
        ArrayList<Point3D> cubes = new ArrayList<>();
        for (Cube[][] value : chunk) {
            for (int y = 0; y < chunk.length; y++) {
                for (int z = 0; z < CHUNK_HEIGHT; z++) {
                    Cube c = value[y][z];
                    if (c.isActive()) {
                        cubes.add(c.getUpperLeftVertex());
                    }
                }
            }
        }
        if (cubes.size() > 0) {
            ScatterMesh mesh = new ScatterMesh(cubes, 1);
            mesh.setMarker(MarkerFactory.Marker.CUBE);
            mesh.setId("scatter");
            setMesh(mesh.getMeshFromId(mesh.getId()).getMesh());
            setDrawMode(DrawMode.LINE);
            setCullFace(CullFace.NONE);
            setMaterial(new PhongMaterial(Color.BLUE));
            world.getChildren().add(this);
        }
    }

    /**
     * Removes a chunks mesh view from the world so its mesh can be unloaded
     * or updated and re-added
     * @param world Game world where objects are spawned
     */
    public void removeChunk(Group world) {
        world.getChildren().remove(this);
    }

    /**
     * Since the location of each chunk is unique this is used as an identifier for render and chunk management
     * @return The bottom left vertex of this chunks mesh view.
     */
    public Point3D getLocation() {
        return location;
    }

    /**
     * For each cube, this function checks the surrounding area. If there is at least one space not occupied by a cube,
     * not including diagonally adjacent cubes, then that cube is set to active and will render.
     * @param world Game world where objects are spawned
     */
    public void updateChunk(Group world) {
        removeChunk(world);
        for (Cube[][] cubes : chunk) {
            for (int y = 0; y < chunk.length; y++) {
                for (int z = 0; z < CHUNK_HEIGHT; z++) {

                    Cube c = cubes[y][z];
                    if (z == CHUNK_HEIGHT / 2) {
                        c.setActive(true);
                    }

/*
                    c.setActive(false);
                    boolean[] exists = new boolean[6];
                    Arrays.fill(exists, false);
                    Cube c0 = null;
                    Cube c1 = null;
                    Cube c2 = null;
                    Cube c3 = null;
                    Cube c4 = null;
                    Cube c5 = null;
                    try {
                        c0 = chunk[x + 1][y][z];
                        exists[0] = true;
                    } catch (IndexOutOfBoundsException | NullPointerException e) {
                        c.setActive(false);
                    } try {
                        c1 = chunk[x - 1][y][z];
                        exists[1] = true;
                    } catch (IndexOutOfBoundsException | NullPointerException e) {
                        c.setActive(false);
                    } try {
                        c2 = chunk[x][y + 1][z];
                        exists[2] = true;
                    } catch (IndexOutOfBoundsException | NullPointerException e) {
                        c.setActive(false);
                    } try {
                        c3 = chunk[x][y - 1][z];
                        exists[3] = true;
                    } catch (IndexOutOfBoundsException | NullPointerException e) {
                        c.setActive(false);
                    } try {
                        c4 = chunk[x][y][z + 1];
                        exists[4] = true;
                    } catch (IndexOutOfBoundsException | NullPointerException e) {
                        c.setActive(false);
                    } try {
                        c5 = chunk[x][y][z - 1];
                        exists[5] = true;
                    } catch (IndexOutOfBoundsException | NullPointerException e) {
                        c.setActive(false);
                    }
                   // Cube[] cubes = {c0, c1, c2, c3, c4, c5};
                    //System.out.println(Arrays.toString(cubes));
                    for (boolean exist: exists) {
                        if (!exist) {
                            c.setActive(true);
                            break;
                        }
                    }

 */


                }
            }
        }
        addToWorld(world);
    }

    @Override
    public String toString() {
        return "(" + location.getX() + "," + location.getY() + ", " + location.getZ() + ")";
    }
}
