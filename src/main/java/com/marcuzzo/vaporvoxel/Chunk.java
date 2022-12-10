package com.marcuzzo.vaporvoxel;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import org.fxyz3d.geometry.Point3D;
import org.fxyz3d.shapes.primitives.ScatterMesh;
import org.fxyz3d.shapes.primitives.helper.MarkerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Chunk extends MeshView {
    private Point3D location;
    public final int CHUNK_BOUNDS = 16;
    public final int CHUNK_HEIGHT = 256;
    private final List<Cube> chunk = new GlueList<>();
    private Group world;
    private boolean didChange = false;
    private final List<Cube> cubes = new GlueList<>();
    private  float maxZ = 0;

    public Chunk() {
        setOnMouseClicked(mouseEvent -> {
            didChange = true;
            updateChunk(world);
        });
    }

    /**
     * Initializes a chunk at a given point. Populates a chunk with cubes.
     * The number of cubes is calculated: CHUNK_BOUNDS x CHUNK_BOUNDS x CHUNK_HEIGHT
     * @param x coordinate of chunk corner
     * @param y coordinate of chunk corner
     * @param z coordinate of chunk corner
     * @return Returns the chunk
     */
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
        updateChunk(world);
        didChange = true;
        return this;
    }
    /**
     * Iterates through a chunks points and creates a chunk mesh based on which points are active.
     */
    public void updateMesh() {
        Instant i1 = Instant.now();
        System.out.println("======Updating Chunk at (" + getLocation().getX() + " " + getLocation().getY() + " " + getLocation().getZ() + ")======");
        //Removes inner cubes that are culled
        List<Cube> toRemove = new GlueList<>();
        for (Cube value : cubes) {
            if (value.getZ() < maxZ) {
                toRemove.add(value);
            }
        }
        cubes.removeAll(toRemove);

        Instant i2 = Instant.now();
        Duration d = Duration.between(i1, i2);
        System.out.println("Time to update cubes: " + d.getNano());

        //Mesh creation
        if (cubes.size() > 0) {
            Instant i3 = Instant.now();
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

            Instant i4 = Instant.now();
            Duration d1 = Duration.between(i3, i4);
            System.out.println("Time to update triangle mesh: " + d1.getNano());
        }
        didChange = false;


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
                boolean b = value.getGradientValue() < 0.00 && value.getGradientValue() < 0.10;
                value.setActive(b);
                if (b) {
                    if (value.getZ() > maxZ)
                        maxZ = value.getZ();
                    cubes.add(value);
                }
            }
            updateMesh();
        }
        else {
            System.out.println("Chunk unchanged: Skipping mesh update");
        }

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

