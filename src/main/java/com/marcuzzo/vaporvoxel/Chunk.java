package com.marcuzzo.vaporvoxel;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import org.fxyz3d.geometry.Point3D;
import org.fxyz3d.shapes.primitives.ScatterMesh;
import org.fxyz3d.shapes.primitives.helper.MarkerFactory;

import java.util.ArrayList;
import java.util.List;

public class Chunk extends MeshView {
    private Point3D location;
    public final int CHUNK_BOUNDS = 16;
    public final int CHUNK_HEIGHT = 256;
    private final List<Layer<Cube>> chunk = new ArrayList<>();

    public Chunk() {
    }
    public Chunk initialize(int x, int y, int z) {
        location = new Point3D(x, y, z);

        for (int i = 0; i < CHUNK_BOUNDS; i++) {
            for (int j = 0; j < CHUNK_BOUNDS; j++) {
                for (int k = 0; k < CHUNK_HEIGHT; k++) {
                    Layer<Cube> l = new Layer<>();
                    l.setZ(k);
                    Cube c = new Cube(x + i, y + j, z + k);
                 //   c.setTranslateZ(c.getTranslateZ() + l.getZ());
                 //   c.resizeRelocate(location.getX() + i, location.getY() + j, c.getBoundsInLocal().getWidth(), c.getBoundsInLocal().getHeight());
                    l.add(c);
                    chunk.add(l);
                }
            }
        }
        updateMesh();
        return this;
    }
    /**
     * Iterates through a three-dimensional array of cubes and adds their upper left vertices to a
     * mesh used for the chunks structure. Adds a "blank" chunks to the world.
     */
    public void updateMesh() {
        List<Point3D> cubes = new ArrayList<>();
        for (List<Cube> value : chunk) {
            for (Cube c : value) {
                if (c.isActive()) {
                    cubes.add(c);
                }
            }
        }
        if (cubes.size() > 0) {
            ScatterMesh mesh = new ScatterMesh(cubes, 1);
            mesh.setMarker(MarkerFactory.Marker.CUBE);
            mesh.setId("scatter");
            setCache(true);
            setCullFace(CullFace.FRONT);
            setMesh(mesh.getMeshFromId(mesh.getId()).getMesh());
            setMaterial(new PhongMaterial(Color.BLUE));
        }
    }

    /**
     * Removes a chunks mesh view from the world so its mesh can be unloaded
     * or updated and re-added
     * @param world Game world where objects are spawned
     */
    public void removeChunk(Group world) { world.getChildren().remove(this);
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
        for (Layer<Cube> cubes : chunk) {
            for (int y = 0; y < cubes.size(); y++) {
                Cube c = cubes.get(y);
                if (cubes.getZ() == CHUNK_HEIGHT / 2) {
                    c.setActive(true);
                }
            }
        }
        removeChunk(world);
        updateMesh();
    }

    @Override
    public String toString() {
        return "(" + location.getX() + "," + location.getY() + ", " + location.getZ() + ")";
    }
}