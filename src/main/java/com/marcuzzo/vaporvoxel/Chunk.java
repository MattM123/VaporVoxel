package com.marcuzzo.vaporvoxel;
import javafx.scene.shape.*;
import org.fxyz3d.geometry.Point3D;
import javafx.scene.layout.Region;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import org.fxyz3d.shapes.primitives.ScatterMesh;
import org.fxyz3d.shapes.primitives.helper.MarkerFactory;
import java.util.ArrayList;
import java.util.Arrays;

public class Chunk extends Region {
    private Point3D location;
    private final int CHUNK_BOUNDS = 32;
    private final Cube[][][] chunk = new Cube[CHUNK_BOUNDS][CHUNK_BOUNDS][CHUNK_BOUNDS];
    private ScatterMesh mesh;
    public Chunk() {
    }

    public Chunk initialize(int x, int y, int z) {
        int vFlag = 0;
        location = new Point3D(x, y, z);
        for (int i = 0; i < chunk.length; i++) {
            for (int j = 0; j < chunk.length; j++) {
                for (int k = 0; k < chunk.length; k++) {
                    Cube c = new Cube();
                    vFlag++;
                    if (vFlag > CHUNK_BOUNDS)
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
     * Iterates through a Chunk of Cubes and adds them to a Group world
     * @param world Game world where objects are spawned
     */
    public void addToWorld(Group world) {
        int counter = 0;
        ArrayList<Point3D> cubes = new ArrayList<>();
        for (int x = 0; x < chunk.length; x++) {
            for (int y = 0; y < chunk.length; y++) {
                for (int z = 0; z < chunk.length; z++) {
                    Cube c = chunk[x][y][z];
                    if (c.isActive()) {
                        cubes.add(c.getUpperLeftVertex());
                        counter++;
                    }
                }
            }
        }
        mesh = new ScatterMesh(cubes, 1);
        mesh.setMarker(MarkerFactory.Marker.CUBE);
        mesh.setId("scatter");
        MeshView meshView = new MeshView(mesh.getMeshFromId(mesh.getId()).getMesh());
        meshView.setCullFace(CullFace.NONE);
        meshView.setMaterial(new PhongMaterial(Color.BLUE));
        world.getChildren().add(meshView);
        System.out.println(counter);
    }

    /**
     * Iterates through a Chunk of Cubes and removes them from a Group world.
     * @param world Game world where objects are spawned
     */
    public void removeChunk(Group world) {
        ArrayList<Cube> cubes = new ArrayList<>();
        for (int x = 0; x < chunk.length ; x++) {
            for (int y = 0; y < chunk.length; y++) {
                for (int z = 0; z <  chunk.length; z++) {
                    cubes.add(chunk[x][y][z]);
                    world.getChildren().remove(mesh);
                }
            }
        }
    }

    /**
     * For each cube, this function checks the surrounding area. If there is at least one space not occupied by a cube,
     * not including diagonally adjacent cubes, then that cube is set to active and will render.
     * @param world Game world where objects are spawned
     */
    public void updateChunk(Group world) {
        removeChunk(world);
        for (int x = 0; x < chunk.length; x++) {
            for (int y = 0; y < chunk.length; y++) {
                for (int z = 0; z < chunk.length; z++) {
                    Cube c = chunk[x][y][z];
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
                        c.setActive(true);
                    } try {
                        c1 = chunk[x - 1][y][z];
                        exists[1] = true;
                    } catch (IndexOutOfBoundsException | NullPointerException e) {
                        c.setActive(true);
                    } try {
                        c2 = chunk[x][y + 1][z];
                        exists[2] = true;
                    } catch (IndexOutOfBoundsException | NullPointerException e) {
                        c.setActive(true);
                    } try {
                        c3 = chunk[x][y - 1][z];
                        exists[3] = true;
                    } catch (IndexOutOfBoundsException | NullPointerException e) {
                        c.setActive(true);
                    } try {
                        c4 = chunk[x][y][z + 1];
                        exists[4] = true;
                    } catch (IndexOutOfBoundsException | NullPointerException e) {
                        c.setActive(true);
                    } try {
                        c5 = chunk[x][y][z - 1];
                        exists[5] = true;
                    } catch (IndexOutOfBoundsException | NullPointerException e) {
                        c.setActive(true);
                    }
                    Cube[] cubes = {c0, c1, c2, c3, c4, c5};
                    //System.out.println(Arrays.toString(cubes));
                    for (boolean exist : exists) {
                        if (!exist) {
                            c.setActive(true);
                            break;
                        }
                    }
                }
            }
        }
        addToWorld(world);
    }
}
