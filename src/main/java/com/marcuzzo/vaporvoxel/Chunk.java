package com.marcuzzo.vaporvoxel;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
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
    private TriangleMesh mesh;

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
        //Retrieving active vertices that should be rendered
        List<Cube> cubes = new ArrayList<>();
        for (Cube value : chunk) {
            if (value.isActive()) {
                cubes.add(value);
            }
        }

        //Mesh creation
        if (cubes.size() > 0) {
            List<Point3D> pCubes = new ArrayList<>(cubes);

            //Defining block texture atlas
            PhongMaterial mat = new PhongMaterial();
            mat.setDiffuseMap(new Image("file:src/main/resources/textures.png"));

            //Scatter mesh to be converted to triangle mesh
            ScatterMesh scatter = new ScatterMesh(pCubes, 1);
            scatter.setMarker(MarkerFactory.Marker.CUBE);
            scatter.setId("scatter");
            setCache(true);
            setDrawMode(DrawMode.FILL);
            setCullFace(CullFace.BACK);

            //Triangle mesh creation
            mesh = (TriangleMesh) scatter.getMeshFromId("scatter").getMesh();
            mesh.setVertexFormat(VertexFormat.POINT_TEXCOORD);
            float[] newTex = {
                    0f, 0f, //upper left corner
                    0f, 0.03125f,
                    0.03125f, 0.03125f, //lower right corner
                    0.03125f, 0f,
                    0.5f, 0.5f //Empty
            };

            for (int i = 1; i < mesh.getFaces().size(); i += 12) {
                for (int j = 0; j < cubes.size(); j++) {
                    if (j == 5) {
                        cubes.get(j).setType(BlockType.TEST);
                    }

                    if (cubes.get(j).getType().equals(BlockType.DEFAULT)) {
                        mesh.getFaces().set(i, 0);
                        mesh.getFaces().set(i + 2, 1);
                        mesh.getFaces().set(i + 4, 3);

                        mesh.getFaces().set(i + 6, 3);
                        mesh.getFaces().set(i + 8, 1);
                        mesh.getFaces().set(i + 10, 2);
                    }
                    else {
                        mesh.getFaces().set(i, 4);
                        mesh.getFaces().set(i + 2, 4);
                        mesh.getFaces().set(i + 4, 4);

                        mesh.getFaces().set(i + 6, 4);
                        mesh.getFaces().set(i + 8, 4);
                        mesh.getFaces().set(i + 10, 4);
                    }
                }
            }

            mesh.getTexCoords().setAll(newTex);
            setMesh(mesh);
            setMaterial(mat);

        }
        didChange = false;
    }

    /**
     * Since the location of each chunk is unique this is used as an identifier for chunk rendering
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
                if ((int) value.getZ() == 10) {
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