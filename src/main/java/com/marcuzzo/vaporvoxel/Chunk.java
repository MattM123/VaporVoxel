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
    private final List<Cube> chunk = new ArrayList<>();
    private Group world;
    private boolean didChange = false;

    public Chunk() {
        setOnMouseClicked(mouseEvent -> {
            didChange = true;
            updateChunk(world);
        });
    }
    public Chunk initialize(int x, int y, int z) {
        location = new Point3D(x, y, z);

        for (int i = 0; i < CHUNK_BOUNDS; i++) {
            for (int j = 0; j < CHUNK_BOUNDS; j++) {
                for (int k = 0; k < CHUNK_HEIGHT; k++) {
                    Cube c = new Cube(x + i, y + j, z + k);
                    chunk.add(c);
                }
            }
        }
        updateMesh();
        didChange = true;
        return this;
    }
    /**
     * Iterates through a three-dimensional array of cubes and adds their upper left vertices to a
     * mesh used for the chunks structure. Adds a "blank" chunks to the world.
     */
    public void updateMesh() {
        List<Point3D> cubes = new ArrayList<>();
        for (Cube value : chunk) {
            if (value.isActive()) {
                cubes.add(value);
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
        didChange = false;
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
        this.world = world;
        if (didChange) {
            for (Cube value : chunk) {
                if ((int) value.getZ() == CHUNK_HEIGHT / 2) {
                    value.setActive(true);
                }
            }
            updateMesh();
        }
    }

    @Override
    public String toString() {
        return "(" + location.getX() + "," + location.getY() + ", " + location.getZ() + ")";
    }
}