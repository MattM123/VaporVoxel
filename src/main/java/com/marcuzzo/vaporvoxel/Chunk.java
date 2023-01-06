package com.marcuzzo.vaporvoxel;

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
    private final GlueList<Cube> heightMapPointList = new GlueList<>();
    private final long seed = 1234567890;
    private final TextureAtlas textures;

    //private Point3D pickedCube;

    public Chunk(TextureAtlas t) {
        textures = t;
        setOnMouseClicked(mouseEvent -> {
            didChange = true;
            updateMesh();
        });

        /*=========================================
        Cube selection outline
        =========================================*/
        //Check if adjacent points are in heightmappointlist
        /*
        setOnMouseMoved(mouseEvent -> {
            Point3D p = Point3D.convertFromJavaFXPoint3D(mouseEvent.getPickResult().getIntersectedPoint());
            //If cursor hovers top or bottom of cube
            if (p.getX() > Math.floor(p.getX()) && p.getX() < Math.ceil(p.getX())
            && p.getY() > Math.floor(p.getY()) && p.getY() < Math.ceil(p.getY())
                && p.getZ() == Math.ceil(p.getZ()) || p.getZ() == Math.ceil(p.getZ())) {
                pickedCube = new Point3D(Math.floor(p.getX()), Math.floor(p.getY()), p.getZ());
            }

            //If cursor hovers in the XZ plane
            if (p.getX() > Math.floor(p.getX()) && p.getX() < Math.ceil(p.getX())
                    && p.getZ() > Math.floor(p.getZ()) && p.getZ() < Math.ceil(p.getZ())
                    && p.getY() == Math.ceil(p.getY()) || p.getY() == Math.ceil(p.getY())) {
                Cube a = new Cube((int) Math.floor(p.getX()), (int) p.getY() + 1, (int) Math.floor(p.getZ()));
                Cube b = new Cube((int) Math.floor(p.getX()), (int) p.getY() - 1, (int) Math.floor(p.getZ()));
                if (heightMapPointList.contains(a) && !heightMapPointList.contains(b)) {
                    System.out.println("test");
                    pickedCube = new Point3D(Math.floor(p.getX()), p.getY() - 1, Math.ceil(p.getZ()));
                }
                else if (heightMapPointList.contains(b) && !heightMapPointList.contains(a)) {
                    System.out.println("test1");
                    pickedCube = new Point3D(Math.floor(p.getX()), p.getY() + 1, Math.ceil(p.getZ()));
                }
            }

            //If cursor hovers in the YZ plane
        //    if (p.getY() > Math.floor(p.getY()) && p.getY() < Math.ceil(p.getY())
         //           && p.getZ() > Math.floor(p.getZ()) && p.getZ() < Math.ceil(p.getZ())
          //          && p.getX() == Math.ceil(p.getX()) || p.getX() == Math.ceil(p.getX())) {
          //      pickedCube = new Point3D(p.getX() - 1, Math.floor(p.getY()), Math.floor(p.getZ()) + 1);
          //  }


            //if x/y > x/y + 0.5
            //if x/y < x/y + 0.5
           // if (p.getZ() > Math.floor(p.getZ()) && p.getZ() < Math.ceil(p.getZ()))
          //      pickedCube = new Point3D(Math.floor(p.getX()), Math.floor(p.getY()), Math.floor(p.getZ()) + 1);
          //  else
            System.out.println(p);
            if (pickedCube != null) {
                List<Point3D> points = new GlueList<>();

                points.add(pickedCube);
                points.add(new Point3D(pickedCube.getX() + 1, pickedCube.getY(), pickedCube.getZ()));
                points.add(new Point3D(pickedCube.getX() + 1, pickedCube.getY() + 1, pickedCube.getZ()));
                points.add(new Point3D(pickedCube.getX(), pickedCube.getY() + 1, pickedCube.getZ()));
                points.add(pickedCube);


                points.add(new Point3D(pickedCube.getX(), pickedCube.getY(), pickedCube.getZ() - 1));
                points.add(new Point3D(pickedCube.getX() + 1, pickedCube.getY(), pickedCube.getZ() - 1));
                points.add(new Point3D(pickedCube.getX() + 1, pickedCube.getY() + 1, pickedCube.getZ() - 1));
                points.add(new Point3D(pickedCube.getX(), pickedCube.getY() + 1, pickedCube.getZ() - 1));
                points.add(new Point3D(pickedCube.getX(), pickedCube.getY(), pickedCube.getZ() - 1));


                PickCubeOutline line = new PickCubeOutline(points, 0.02f, Color.BLACK, PolyLine3D.LineType.RIBBON);
                line.meshView.setDrawMode(DrawMode.LINE);

                for (Node n : MainApplication.world.getChildren()) {
                    CompletableFuture.runAsync(() -> Platform.runLater(() -> {
                        if (n instanceof PickCubeOutline) {
                            MainApplication.world.getChildren().remove(n);
                        }
                    }), MainApplication.executor);
                }
           //     MainApplication.world.getChildren().addAll(line);
            }
        });

         */
    }

    /**
     * Initializes a chunk at a given point. Populates a chunk heightmapped points
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

        int xCount = 0;
        int yCount = 0;
        for (int x1 = x; x1 < x + CHUNK_BOUNDS; x1++) {
            for (int y1 = y; y1 < y + CHUNK_BOUNDS; y1++) {

                //Converts the raw noise value in the range of -1 to 1, to the range of 0 to 320 to match Z coordinate.
                float elevation = (float) Math.floor(((getGlobalHeightMapValue(x1, y1) + 1) / 2) * (CHUNK_HEIGHT - 1));

                heightMap[xCount][yCount] = (int) elevation;
                Cube c = new Cube(x1, y1, (int) elevation);
                c.setType(BlockType.DEFAULT);
                heightMapPointList.add(c);

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
     * Generates chunk mesh based on height-mapped and interpolated points on initial chunk creation
     * Also removes and adds points to mesh based on player actions post-chunk creation.
     */
    public void updateMesh() {
        if (cubes.size() > 0 && didChange) {
            System.out.println("======Updating Chunk Mesh at (" + getLocation().getX() + " " + getLocation().getY() + " " + getLocation().getZ() + ")======");

            //Defining block texture atlas
            PhongMaterial mat = new PhongMaterial();

            if (textures != null)
                mat.setDiffuseMap(textures.getImage());
            else {
                System.out.println("Null Textures");
                System.exit(0);
            }

            /*===============================================
                 Texture coordinates for mesh section
             ================================================*/
            TextureRegion grass_top = textures.getRegion("grass_top");
            TextureRegion grass_side = textures.getRegion("grass_side");
            float[] texCoords = {
                    (float) grass_top.getTextureCoordinates(0, 0)[0], (float) grass_top.getTextureCoordinates(0, 0)[1],
                    (float) grass_top.getTextureCoordinates(0, 256)[0], (float) grass_top.getTextureCoordinates(0, 256)[1],
                    (float) grass_top.getTextureCoordinates(256, 256)[0], (float) grass_top.getTextureCoordinates(256, 256)[1],
                    (float) grass_top.getTextureCoordinates(256, 0)[0], (float) grass_top.getTextureCoordinates(256, 0)[1],

                    (float) grass_side.getTextureCoordinates(0, 0)[0], (float) grass_side.getTextureCoordinates(0, 0)[1],
                    (float) grass_side.getTextureCoordinates(0, 256)[0], (float) grass_side.getTextureCoordinates(0, 256)[1],
                    (float) grass_side.getTextureCoordinates(256, 256)[0], (float) grass_side.getTextureCoordinates(256, 256)[1],
                    (float) grass_side.getTextureCoordinates(256, 0)[0], (float) grass_side.getTextureCoordinates(256, 0)[1],

            };

            float[] points = new float[0];
            int[][] faces = new int[0][0];
            heightMapPointList.addAll(getInterpolatedCubes());

            /*===================================
              Rendering Z Axis Faces
            ==================================*/
            for (int i = 0; i < CHUNK_HEIGHT; i++) {
                float finalI = i;
                List<Cube> p = heightMapPointList.stream().filter(q -> q.getZ() == finalI).toList();

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
                        float[] t1 = {point3D.getX(), point3D.getY() - 1, point3D.getZ()};
                        if (getPointIndex(points, t1) > -1)
                            face = ArrayUtils.add(face, getPointIndex(points, t1));
                        else {
                            points = ArrayUtils.add(points, point3D.getX());
                            points = ArrayUtils.add(points, point3D.getY() - 1);
                            points = ArrayUtils.add(points, point3D.getZ());
                            face = ArrayUtils.add(face, points.length / 3 - 1);
                        }
                        face = ArrayUtils.add(face, 1);


                        //Third face point 2
                        float[] t2 = {point3D.getX() - 1, point3D.getY() - 1, point3D.getZ()};
                        if (getPointIndex(points, t2) > -1)
                            face = ArrayUtils.add(face, getPointIndex(points, t2));
                        else {
                            points = ArrayUtils.add(points, point3D.getX() - 1);
                            points = ArrayUtils.add(points, point3D.getY() - 1);
                            points = ArrayUtils.add(points, point3D.getZ());
                            face = ArrayUtils.add(face, points.length / 3 - 1);
                        }
                        face = ArrayUtils.add(face, 2);


                        //Fourth face point 3
                        float[] t3 = {point3D.getX() - 1, point3D.getY(), point3D.getZ()};
                        if (getPointIndex(points, t3) > -1)
                            face = ArrayUtils.add(face, getPointIndex(points, t3));
                        else {
                            points = ArrayUtils.add(points, point3D.getX() - 1);
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
                float finalI = i;

                List<Cube> x = heightMapPointList.stream().filter(q -> q.getX() == (getLocation().getX() + finalI)).toList();
                List<Cube> y = heightMapPointList.stream().filter(q -> q.getY() == (getLocation().getY() + finalI)).toList();


                if (!x.isEmpty()) {
                    for (Cube point3D : x) {
                        int[] face = new int[0];
                        int[] face1 = new int[0];

                        //Determines if faces should be rendered
                        double base = point3D.getZ();
                        double plus1 = Math.floor((getGlobalHeightMapValue((int) point3D.getX() + 1, (int) point3D.getY()) + 1 ) / 2 * (CHUNK_HEIGHT - 1));
                        double minus1 = Math.floor((getGlobalHeightMapValue((int) point3D.getX() - 1, (int) point3D.getY()) + 1 ) / 2 * (CHUNK_HEIGHT - 1));

                        //First face set
                        if (plus1 != base || minus1 != base) {
                            float[] t = {point3D.getX(), point3D.getY(), point3D.getZ()};
                            if (getPointIndex(points, t) > -1)
                                face = ArrayUtils.add(face, getPointIndex(points, t));
                            else {
                                points = ArrayUtils.add(points, point3D.getX());
                                points = ArrayUtils.add(points, point3D.getY());
                                points = ArrayUtils.add(points, point3D.getZ());
                                face = ArrayUtils.add(face, points.length / 3 - 1);
                            }

                            float[] w = {point3D.getX() - 1, point3D.getY(), point3D.getZ()};
                            if (getPointIndex(points, w) > -1)
                                face1 = ArrayUtils.add(face1, getPointIndex(points, w));
                            else {
                                points = ArrayUtils.add(points, point3D.getX() - 1);
                                points = ArrayUtils.add(points, point3D.getY());
                                points = ArrayUtils.add(points, point3D.getZ());
                                face1 = ArrayUtils.add(face1, points.length / 3 - 1);
                            }
                            switch (point3D.getType()) {
                                case DEFAULT -> {
                                    face1 = ArrayUtils.add(face1, 4);
                                    face = ArrayUtils.add(face, 4);
                                }
                            }
                        }

                        //Second face set
                        if (plus1 != base || minus1 != base) {
                            float[] t1 = {point3D.getX(), point3D.getY() - 1, point3D.getZ()};
                            if (getPointIndex(points, t1) > -1)
                                face = ArrayUtils.add(face, getPointIndex(points, t1));
                            else {
                                points = ArrayUtils.add(points, point3D.getX());
                                points = ArrayUtils.add(points, point3D.getY() - 1);
                                points = ArrayUtils.add(points, point3D.getZ());
                                face = ArrayUtils.add(face, points.length / 3 - 1);
                            }

                            float[] w1 = {point3D.getX() - 1, point3D.getY() - 1, point3D.getZ()};
                            if (getPointIndex(points, w1) > -1)
                                face1 = ArrayUtils.add(face1, getPointIndex(points, w1));
                            else {
                                points = ArrayUtils.add(points, point3D.getX() - 1);
                                points = ArrayUtils.add(points, point3D.getY() - 1);
                                points = ArrayUtils.add(points, point3D.getZ());
                                face1 = ArrayUtils.add(face1, points.length / 3 - 1);
                            }
                            switch (point3D.getType()) {
                                case DEFAULT -> {
                                    face1 = ArrayUtils.add(face1, 5);
                                    face = ArrayUtils.add(face, 5);
                                }
                            }
                        }


                        //Third face set
                        if (plus1 != base || minus1 != base) {
                            float[] t2 = {point3D.getX(), point3D.getY() - 1, point3D.getZ() - 1};
                            if (getPointIndex(points, t2) > -1)
                                face = ArrayUtils.add(face, getPointIndex(points, t2));
                            else {
                                points = ArrayUtils.add(points, point3D.getX());
                                points = ArrayUtils.add(points, point3D.getY() - 1);
                                points = ArrayUtils.add(points, point3D.getZ() - 1);
                                face = ArrayUtils.add(face, points.length / 3 - 1);
                            }

                            float[] w2 = {point3D.getX() - 1, point3D.getY() - 1, point3D.getZ() - 1};
                            if (getPointIndex(points, w2) > -1)
                                face1 = ArrayUtils.add(face1, getPointIndex(points, w2));
                            else {
                                points = ArrayUtils.add(points, point3D.getX() - 1);
                                points = ArrayUtils.add(points, point3D.getY() - 1);
                                points = ArrayUtils.add(points, point3D.getZ() - 1);
                                face1 = ArrayUtils.add(face1, points.length / 3 - 1);
                            }
                            switch (point3D.getType()) {
                                case DEFAULT -> {
                                    face1 = ArrayUtils.add(face1, 6);
                                    face = ArrayUtils.add(face, 6);
                                }
                            }
                        }

                        //Fourth face set
                        if (plus1 != base || minus1 != base) {
                            float[] t3 = {point3D.getX(), point3D.getY(), point3D.getZ() - 1};
                            if (getPointIndex(points, t3) > -1)
                                face = ArrayUtils.add(face, getPointIndex(points, t3));
                            else {
                                points = ArrayUtils.add(points, point3D.getX());
                                points = ArrayUtils.add(points, point3D.getY());
                                points = ArrayUtils.add(points, point3D.getZ() - 1);
                                face = ArrayUtils.add(face, points.length / 3 - 1);
                            }

                            float[] w3 = {point3D.getX() - 1, point3D.getY(), point3D.getZ() - 1};
                            if (getPointIndex(points, w3) > -1)
                                face1 = ArrayUtils.add(face1, getPointIndex(points, w3));
                            else {
                                points = ArrayUtils.add(points, point3D.getX() - 1);
                                points = ArrayUtils.add(points, point3D.getY());
                                points = ArrayUtils.add(points, point3D.getZ() - 1);
                                face1 = ArrayUtils.add(face1, points.length / 3 - 1);
                            }
                            switch (point3D.getType()) {
                                case DEFAULT -> {
                                    face1 = ArrayUtils.add(face1, 7);
                                    face = ArrayUtils.add(face, 7);
                                }
                            }
                        }

                        if (face.length == 8)
                            faces = ArrayUtils.add(faces, face);
                        if (face1.length == 8)
                            faces = ArrayUtils.add(faces, face1);
                    }
                }

                if (!y.isEmpty()) {
                    for (Cube point3D : y) {
                        int[] face = new int[0];
                        int[] face1 = new int[0];

                        //Determines faces should be rendered
                        double base = point3D.getZ();
                        double plus1 = Math.floor((getGlobalHeightMapValue((int) point3D.getX(), (int) point3D.getY() + 1) + 1 ) / 2 * (CHUNK_HEIGHT - 1));
                        double minus1 = Math.floor((getGlobalHeightMapValue((int) point3D.getX(), (int) point3D.getY() - 1) + 1 ) / 2 * (CHUNK_HEIGHT - 1));

                        //First face set
                        if (plus1 != base || minus1 != base) {
                            float[] t = {point3D.getX(), point3D.getY(), point3D.getZ()};
                            if (getPointIndex(points, t) > -1)
                                face = ArrayUtils.add(face, getPointIndex(points, t));
                            else {
                                points = ArrayUtils.add(points, point3D.getX());
                                points = ArrayUtils.add(points, point3D.getY());
                                points = ArrayUtils.add(points, point3D.getZ());
                                face = ArrayUtils.add(face, points.length / 3 - 1);
                            }

                            float[] w = {point3D.getX(), point3D.getY() - 1, point3D.getZ()};
                            if (getPointIndex(points, w) > -1)
                                face1 = ArrayUtils.add(face1, getPointIndex(points, w));
                            else {
                                points = ArrayUtils.add(points, point3D.getX());
                                points = ArrayUtils.add(points, point3D.getY() - 1);
                                points = ArrayUtils.add(points, point3D.getZ());
                                face1 = ArrayUtils.add(face1, points.length / 3 - 1);
                            }
                            switch (point3D.getType()) {
                                case DEFAULT -> {
                                    face1 = ArrayUtils.add(face1, 4);
                                    face = ArrayUtils.add(face, 4);
                                }
                            }
                        }


                        //Second face set
                        if (plus1 != base || minus1 != base) {
                            float[] t1 = {point3D.getX() - 1, point3D.getY(), point3D.getZ()};
                            if (getPointIndex(points, t1) > -1)
                                face = ArrayUtils.add(face, getPointIndex(points, t1));
                            else {
                                points = ArrayUtils.add(points, point3D.getX() - 1);
                                points = ArrayUtils.add(points, point3D.getY());
                                points = ArrayUtils.add(points, point3D.getZ());
                                face = ArrayUtils.add(face, points.length / 3 - 1);
                            }

                            float[] w1 = {point3D.getX() - 1, point3D.getY() - 1, point3D.getZ()};
                            if (getPointIndex(points, w1) > -1)
                                face1 = ArrayUtils.add(face1, getPointIndex(points, w1));
                            else {
                                points = ArrayUtils.add(points, point3D.getX() - 1);
                                points = ArrayUtils.add(points, point3D.getY() - 1);
                                points = ArrayUtils.add(points, point3D.getZ());
                                face1 = ArrayUtils.add(face1, points.length / 3 - 1);
                            }
                            switch (point3D.getType()) {
                                case DEFAULT -> {
                                    face1 = ArrayUtils.add(face1, 5);
                                    face = ArrayUtils.add(face, 5);
                                }
                            }
                        }


                        //Third face set
                        if (plus1 != base || minus1 != base) {
                            float[] t2 = {point3D.getX() - 1, point3D.getY(), point3D.getZ() - 1};
                            if (getPointIndex(points, t2) > -1)
                                face = ArrayUtils.add(face, getPointIndex(points, t2));
                            else {
                                points = ArrayUtils.add(points, point3D.getX() - 1);
                                points = ArrayUtils.add(points, point3D.getY());
                                points = ArrayUtils.add(points, point3D.getZ() - 1);
                                face = ArrayUtils.add(face, points.length / 3 - 1);
                            }

                            float[] w2 = {point3D.getX() - 1, point3D.getY() - 1, point3D.getZ() - 1};
                            if (getPointIndex(points, w2) > -1)
                                face1 = ArrayUtils.add(face1, getPointIndex(points, w2));
                            else {
                                points = ArrayUtils.add(points, point3D.getX() - 1);
                                points = ArrayUtils.add(points, point3D.getY() - 1);
                                points = ArrayUtils.add(points, point3D.getZ() - 1);
                                face1 = ArrayUtils.add(face1, points.length / 3 - 1);
                            }
                            switch (point3D.getType()) {
                                case DEFAULT -> {
                                    face1 = ArrayUtils.add(face1, 6);
                                    face = ArrayUtils.add(face, 6);
                                }
                            }
                        }

                        //Fourth face set
                        if (plus1 != base || minus1 != base) {
                            float[] t3 = {point3D.getX(), point3D.getY(), point3D.getZ() - 1};
                            if (getPointIndex(points, t3) > -1)
                                face = ArrayUtils.add(face, getPointIndex(points, t3));
                            else {
                                points = ArrayUtils.add(points, point3D.getX());
                                points = ArrayUtils.add(points, point3D.getY());
                                points = ArrayUtils.add(points, point3D.getZ() - 1);
                                face = ArrayUtils.add(face, points.length / 3 - 1);
                            }

                            float[] w3 = {point3D.getX(), point3D.getY() - 1, point3D.getZ() - 1};
                            if (getPointIndex(points, w3) > -1)
                                face1 = ArrayUtils.add(face1, getPointIndex(points, w3));
                            else {
                                points = ArrayUtils.add(points, point3D.getX());
                                points = ArrayUtils.add(points, point3D.getY() - 1);
                                points = ArrayUtils.add(points, point3D.getZ() - 1);
                                face1 = ArrayUtils.add(face1, points.length / 3 - 1);
                            }
                            switch (point3D.getType()) {
                                case DEFAULT -> {
                                    face1 = ArrayUtils.add(face1, 7);
                                    face = ArrayUtils.add(face, 7);
                                }
                            }
                        }

                        if (face.length == 8)
                            faces = ArrayUtils.add(faces, face);
                        if (face1.length == 8)
                            faces = ArrayUtils.add(faces, face1);
                    }
                }
            }

            PolygonMesh mesh = new PolygonMesh(points, texCoords, faces);
            int[] smooth = new int[faces.length];
            Arrays.setAll(smooth,i -> i + 1);
            mesh.getFaceSmoothingGroups().setAll(smooth);


            System.out.println(mesh.getPoints().size() + " Visible Vertices");
            System.out.println(mesh.getFaces().length + " Visible Faces");
            System.out.println("Visible Cubes: " + heightMapPointList.size());
            System.out.println("Total Cubes in chunk: " + cubes.size());

            setCullFace(CullFace.NONE);
            setMaterial(mat);
            setMesh(mesh);

        }
        didChange = false;
    }

    /**
     * For any given array of 3 coordinates, checks if it is present in the larger
     * points array that stores all points of a mesh as 3 individual float values, x, y, z.
     * @param mainArr The points array of the mesh
     * @param subArr An array of three xyz coordinates to search for in the points array
     * @return If the subArr is found within mainArr, the index of the first element
     * of subArr is returned else -1 is returned
     */
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
     * Given a chunk heightmap, interpolates between cubes to fill in vertical gaps in terrain generation
     *       |-----|
     *       |  d  |
     * |-----|-----|-----|
     * |  c  |base |  a  |
     * |-----|-----|-----|
     *       |  b  |
     *       |-----|
     */
    private List<Cube> getInterpolatedCubes() {
        List<Cube> interpolation = new GlueList<>();
        for (int i = 0; i < heightMapPointList.size; i++) {
            int finalI = i;
            Cube a = heightMapPointList.stream().filter(cube -> cube.getX() == heightMapPointList.get(finalI).getX() + 1 && cube.getY() == heightMapPointList.get(finalI).getY()).findFirst().orElse(null);
            Cube b = heightMapPointList.stream().filter(cube -> cube.getX() == heightMapPointList.get(finalI).getX() && cube.getY() + 1 == heightMapPointList.get(finalI).getY()).findFirst().orElse(null);
            Cube c = heightMapPointList.stream().filter(cube -> cube.getX() == heightMapPointList.get(finalI).getX() - 1 && cube.getY() == heightMapPointList.get(finalI).getY()).findFirst().orElse(null);
            Cube d = heightMapPointList.stream().filter(cube -> cube.getX() == heightMapPointList.get(finalI).getX() && cube.getY() - 1 == heightMapPointList.get(finalI).getY()).findFirst().orElse(null);

            if (a != null) {
                //Get the tallest column and the number of cubes to interpolate
                int taller = (int) a.getZ() - (int) heightMapPointList.get(i).getZ();
                int numOfCubes = Math.abs(taller) - 1;
                boolean aTaller = taller > 0;

                for (int j = 1; j < numOfCubes + 1; j++) {
                    Cube cube;
                    if (aTaller) {
                        cube = new Cube((int) a.getX(), (int) a.getY(), (int) a.getZ() - j);
                    } else {
                        cube = new Cube((int) heightMapPointList.get(i).getX(), (int) heightMapPointList.get(i).getY(), (int) heightMapPointList.get(i).getZ() - j);
                    }
                    cube.setType(BlockType.DEFAULT);
                    if (!interpolation.contains(cube))
                        interpolation.add(cube);
                }
            }
            if (b != null) {
                //Get the tallest column and the number of cubes to interpolate
                int taller = (int) b.getZ() - (int) heightMapPointList.get(i).getZ();
                int numOfCubes = Math.abs(taller) - 1;
                boolean aTaller = taller > 0;

                for (int j = 1; j < numOfCubes + 1; j++) {
                    Cube cube;
                    if (aTaller) {
                        cube = new Cube((int) b.getX(), (int) b.getY(), (int) b.getZ() - j);
                    } else {
                        cube = new Cube((int) heightMapPointList.get(i).getX(), (int) heightMapPointList.get(i).getY(), (int) heightMapPointList.get(i).getZ() - j);
                    }
                    cube.setType(BlockType.DEFAULT);
                    if (!interpolation.contains(cube))
                        interpolation.add(cube);
                }
            }
            if (c != null) {
                //Get the tallest column and the number of cubes to interpolate
                int taller = (int) c.getZ() - (int) heightMapPointList.get(i).getZ();
                int numOfCubes = Math.abs(taller) - 1;
                boolean aTaller = taller > 0;

                for (int j = 1; j < numOfCubes + 1; j++) {
                    Cube cube;
                    if (aTaller) {
                        cube = new Cube((int) c.getX(), (int) c.getY(), (int) c.getZ() - j);
                    } else {
                        cube = new Cube((int) heightMapPointList.get(i).getX(), (int) heightMapPointList.get(i).getY(), (int) heightMapPointList.get(i).getZ() - j);
                    }
                    cube.setType(BlockType.DEFAULT);
                    if (!interpolation.contains(cube))
                        interpolation.add(cube);
                }
            }
            if (d != null) {
                //Get the tallest column and the number of cubes to interpolate
                int taller = (int) d.getZ() - (int) heightMapPointList.get(i).getZ();
                int numOfCubes = Math.abs(taller) - 1;
                boolean aTaller = taller > 0;
                for (int j = 1; j < numOfCubes + 1; j++) {
                    Cube cube;
                    if (aTaller) {
                        cube = new Cube((int) d.getX(), (int) d.getY(), (int) d.getZ() - j);
                    } else {
                        cube = new Cube((int) heightMapPointList.get(i).getX(), (int) heightMapPointList.get(i).getY(), (int) heightMapPointList.get(i).getZ() - j);
                    }
                    cube.setType(BlockType.DEFAULT);
                    if (!interpolation.contains(cube))
                        interpolation.add(cube);
                }
            }
        }
        return interpolation;
    }
    /**
     * Since the location of each chunk is unique this is used as an identifier for chunk rendering
     * @return The bottom left vertex of this chunks mesh view.
     */

    private float getGlobalHeightMapValue(int x, int y) {
        //Affects height of terrain. A higher value will result in lower, smoother terrain while a lower value will result in
        // a rougher, raised terrain
        float var1 = 12;

        //Affects coalescence of terrain. A higher value will result in more condensed, sharp peaks and a lower value will result in
        //more smooth, spread out hills.
        double var2 = 0.005;

        return (1 * OpenSimplex.noise2(seed, (x * var2), (y * var2)) / var1) //Noise Octave 1
                + (float) (0.5 * OpenSimplex.noise2(seed, (x * (var2 * 2)), (y * (var2 * 2))) / var1);
    }
    public Point3D getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "(" + location.getX() + "," + location.getY() + ", " + location.getZ() + ")";
    }
}