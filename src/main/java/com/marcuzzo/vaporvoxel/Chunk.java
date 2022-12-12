package com.marcuzzo.vaporvoxel;

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
    public final int CHUNK_HEIGHT = 320;
    private final List<Cube> chunk = new GlueList<>();
    private boolean didChange = false;
    private final List<Cube> cubes = new GlueList<>();
    private final long seed = 1234567890;

    private final GlueList<Column> columns = new GlueList<>(CHUNK_BOUNDS * CHUNK_BOUNDS);

    public Chunk() {
        setOnMouseClicked(mouseEvent -> {
            didChange = true;
            updateMesh();
        });
    }

    /**
     * Initializes a chunk at a given point. Populates a chunk with cubes.
     * The number of cubes is calculated: CHUNK_BOUNDS x CHUNK_BOUNDS x CHUNK_HEIGHT
     * @param x coordinate of top left chunk corner
     * @param y coordinate of top left chunk corner
     * @param z coordinate of top left chunk corner
     * @return Returns the chunk
     */
    public Chunk initialize(int x, int y, int z) {
        location = new Point3D(x, y, z);
        for (int i = 0; i < CHUNK_BOUNDS; i++) {
            for (int j = 0; j < CHUNK_BOUNDS; j++) {
                for (int k = 0; k < CHUNK_HEIGHT; k++) {
                    Cube value = new Cube(x + i, y + j, z + k);
                    chunk.add(value);

                }
            }
        }

        for (int x1 = x; x1 < x + CHUNK_BOUNDS; x1++) {
            for (int y1 = y; y1 < y + CHUNK_BOUNDS; y1++) {
                float v = OpenSimplex.noise2(seed, x, y);
                columns.add(new Column(x1, y1).setValue((float) Math.abs(Math.floor(v + 100))));

            }
        }

        updateMesh();
        didChange = true;
        return this;
    }
    /**
     * Iterates through a chunks points and creates a chunk mesh based on which points are active.
     */
    public void updateMesh() {
        System.out.println("======Updating Chunk Mesh at (" + getLocation().getX() + " " + getLocation().getY() + " " + getLocation().getZ() + ")======");

        if (didChange) {
            for (Column col : columns) {

                Cube c = getCubeWithLocation(col.getX(), col.getY(), col.getValue());
                if (c != null) {
                    c.setActive(true);
                    cubes.add(c);
                }
            }
        }
        //Removes inner cubes that are culled
        /*
        List<Cube> toRemove = new GlueList<>();
        for (Cube value : cubes) {
            if (value.getZ() <= maxZ && value.getZ() > 5) {
                toRemove.add(value);
            }
        }
        cubes.removeAll(toRemove);

         */

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
            TriangleMesh mesh = (TriangleMesh) scatter.getMeshFromId("scatter").getMesh();
            mesh.setVertexFormat(VertexFormat.POINT_TEXCOORD);
            float[] newTex = {
                    0f, 0f, //upper left corner
                    0f, 0.03125f,
                    0.03125f, 0.03125f, //lower right corner
                    0.03125f, 0f,
            };
            // cubes.get(5).setType(BlockType.TEST);
            System.out.println("Cubes in chunk: " + cubes.size());

            //sets texture coords for faces. Every 12 face elements = 2 triangular
            //faces = 1 square of a cube, see trianglemesh docs
            // System.out.println(mesh.getFaces());
            System.out.println("Face Count: " + mesh.getFaces().size());
            for (int i = 1; i < mesh.getFaces().size(); i += 12) {
                for (Cube cube : cubes) {
                    //every other element in face array is a texture coordinate.
                    if (cube.getType().equals(BlockType.DEFAULT)) {
                        //triangle1
                        mesh.getFaces().set(i, 0);
                        mesh.getFaces().set(i + 2, 1);
                        mesh.getFaces().set(i + 4, 3);

                        //triangle2
                        mesh.getFaces().set(i + 6, 3);
                        mesh.getFaces().set(i + 8, 1);
                        mesh.getFaces().set(i + 10, 2);

                    }
                }

            }

            mesh.getTexCoords().setAll(newTex);
            setMesh(mesh);
            setMaterial(mat);
        }
        didChange = false;


    }


    public Cube getCubeWithLocation(float x, float y, float z) {
        return chunk.stream().filter(c -> c.getX() == x && c.getY() == y && c.getZ() == z).findFirst().orElse(null);
    }
    /**
     * Since the location of each chunk is unique this is used as an identifier for chunk rendering
     * @return The bottom left vertex of this chunks mesh view.
     */
    public Point3D getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "(" + location.getX() + "," + location.getY() + ", " + location.getZ() + ")";
    }
}