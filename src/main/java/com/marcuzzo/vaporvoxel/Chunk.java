package com.marcuzzo.vaporvoxel;

import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import org.apache.commons.lang3.ArrayUtils;
import org.fxyz3d.geometry.Point3D;
import org.fxyz3d.shapes.polygon.PolygonMesh;
import org.fxyz3d.shapes.polygon.PolygonMeshView;

import java.util.Arrays;
import java.util.List;

public class Chunk extends PolygonMeshView {
    private Point3D location;
    public final int CHUNK_BOUNDS = 16;
    public final int CHUNK_HEIGHT = 320;
    private boolean didChange = false;
    private final List<Cube> cubes = new GlueList<>();
    private final int[][] heightMap = new int[CHUNK_BOUNDS][CHUNK_BOUNDS];
    private final List<Point3D> heightMapPointList = new GlueList<>();
    private final long seed = 1234567890;

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

        //===================================
        //Generate chunk height map
        //===================================

        //Affects height of terrain. A higher value will result in lower, smoother terrain while a lower value will result in
        // a rougher, raised terrain
        float var1 = 12;

        //Affects coalescence of terrain. A higher value will result in more condensed, sharp peaks and a lower value will result in
        //more smooth, spread out hills.
        double var2 = 0.005;

        int xCount = 0;
        int yCount = 0;
        for (int x1 = x; x1 < x + CHUNK_BOUNDS; x1++) {
            for (int y1 = y; y1 < y + CHUNK_BOUNDS; y1++) {
                float f = (1 * OpenSimplex.noise2(seed, (x1 * var2), (y1 * var2)) / var1) //Noise Octave 1
                            + (float) (0.5 * OpenSimplex.noise2(seed, (x1 * (var2 * 2)), (y1 * (var2 * 2))) / var1); //Noise Octave 2

                //Converts the raw noise value in the range of -1 to 1, to the range of 0 to 320 to match Z coordinate.
                float elevation = (float) Math.floor(((f + 1) / 2) * (CHUNK_HEIGHT - 1));

                heightMap[xCount][yCount] = (int) elevation;
                heightMapPointList.add(new Point3D(x1, y1, elevation));

                yCount++;
                if (yCount > CHUNK_BOUNDS - 1)
                    yCount = 0;
            }
            xCount++;
            if (xCount > CHUNK_BOUNDS - 1)
                xCount = 0;
        }


        //How far down caves should start generating
        int caveStart = 50;

        //checks chunks for cubes to render based on noise value and heightmap
        for (int x1 = x; x1 < x + CHUNK_BOUNDS; x1++) {
            for (int y1 = y; y1 < y + CHUNK_BOUNDS; y1++) {
                for (int z1 = z; z1 <= heightMap[xCount][yCount]; z1++) {

                    Cube c = new Cube(x1, y1, z1);
                    c.f = OpenSimplex.noise3_ImproveXZ(seed, x1 * 0.05, z1 * 0.05, y1 * 0.05);
                    if (c.f > 0.00)
                        cubes.add(c);
                    if (c.f <= 0.00 && z1 >= heightMap[xCount][yCount] - caveStart)
                        cubes.add(c);


                    yCount++;
                    if (yCount > CHUNK_BOUNDS - 1)
                        yCount = 0;
                }
                xCount++;
                if (xCount > CHUNK_BOUNDS - 1)
                    xCount = 0;
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
        if (cubes.size() > 0 && didChange) {
            System.out.println("======Updating Chunk Mesh at (" + getLocation().getX() + " " + getLocation().getY() + " " + getLocation().getZ() + ")======");

            //Defining block texture atlas
            PhongMaterial mat = new PhongMaterial();
            mat.setDiffuseMap(new Image("file:src/main/resources/textures.svg"));

            /*===============================================
                 Texture coordinates for mesh section
             ================================================*/
            float[] texCoords = {
                    0f, 0f, //upper left corner
                    0f, 0.03125f,
                    0.03125f, 0.03125f, //lower right corner
                    0.03125f, 0f,
            };

            float[] points = new float[0];
            int[][] faces = new int[0][0];

            /*===================================
              Rendering Z Axis Faces
            ==================================*/
            for (int i = 0; i < CHUNK_HEIGHT; i++) {
                float finalI = i;
                List<Point3D> p = heightMapPointList.stream().filter(q -> q.getZ() == finalI).toList();

                if (!p.isEmpty()) {
                    for (Point3D point3D : p) {
                        int[] face = new int[0];

                        //First face point 0
                        float[] t = {point3D.getX(), point3D.getY(), point3D.getZ()};
                        if (getPointIndex(points, t) > -1)
                            face = ArrayUtils.add(face, getPointIndex(points, t));
                        else {
                            points = ArrayUtils.add(points, point3D.getX());
                            points = ArrayUtils.add(points, point3D.getY());
                            points = ArrayUtils.add(points, point3D.getZ());
                            face = ArrayUtils.add(face, points.length / 3 - 1);
                        }
                        face = ArrayUtils.add(face, 0);

                        //Second face point 1
                        float[] t1 = {point3D.getX(), point3D.getY() + 1, point3D.getZ()};
                        if (getPointIndex(points, t1) > -1)
                            face = ArrayUtils.add(face, getPointIndex(points, t1));
                        else {
                            points = ArrayUtils.add(points, point3D.getX());
                            points = ArrayUtils.add(points, point3D.getY() + 1);
                            points = ArrayUtils.add(points, point3D.getZ());
                            face = ArrayUtils.add(face, points.length / 3 - 1);
                        }
                        face = ArrayUtils.add(face, 1);


                        //Third face point 2
                        float[] t2 = {point3D.getX() + 1, point3D.getY() + 1, point3D.getZ()};
                        if (getPointIndex(points, t2) > -1)
                            face = ArrayUtils.add(face, getPointIndex(points, t2));
                        else {
                            points = ArrayUtils.add(points, point3D.getX() + 1);
                            points = ArrayUtils.add(points, point3D.getY() + 1);
                            points = ArrayUtils.add(points, point3D.getZ());
                            face = ArrayUtils.add(face, points.length / 3 - 1);
                        }
                        face = ArrayUtils.add(face, 2);


                        //Fourth face point 3
                        float[] t3 = {point3D.getX() + 1, point3D.getY(), point3D.getZ()};
                        if (getPointIndex(points, t3) > -1)
                            face = ArrayUtils.add(face, getPointIndex(points, t3));
                        else {
                            points = ArrayUtils.add(points, point3D.getX() + 1);
                            points = ArrayUtils.add(points, point3D.getY());
                            points = ArrayUtils.add(points, point3D.getZ());
                            face = ArrayUtils.add(face, points.length / 3 - 1);
                        }
                        face = ArrayUtils.add(face, 3);

                        faces = ArrayUtils.add(faces, face);
                    }
                }
            }
            /*===================================
              Rendering X and Y Axis Faces
            ==================================*/
            for (int i = 0; i < CHUNK_BOUNDS; i++) {
                //TODO: Render X and Y axis faces
            }

            PolygonMesh mesh = new PolygonMesh(points, texCoords, faces);
            int[] smooth = new int[faces.length];
            Arrays.setAll(smooth,i -> i + 1);
            mesh.getFaceSmoothingGroups().setAll(smooth);

            System.out.println(mesh.getPoints().size() + " Visible Vertices");
            System.out.println(mesh.getFaces().length + " Visible Faces");
            System.out.println("Total Cubes in chunk: " + cubes.size());

            setCullFace(CullFace.NONE);
            setMaterial(mat);
            setMesh(mesh);

        }
        didChange = false;
    }

    private int getPointIndex(float[] mainArr, float[] subArr) {
        int out = -1;
        for (int i = 0; i < mainArr.length; i += 3) {
            float[] temp = {mainArr[i], mainArr[i + 1], mainArr[i + 2]};
            if (temp[0] == subArr[0] && temp[1] == subArr[1] && temp[2] == subArr[2]) {
                out = i / 3;
                break;
            }
        }
        return out;
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