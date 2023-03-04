package com.marcuzzo.vaporvoxel;

import com.marcuzzo.vaporvoxel.EventTypes.PlayerEvent;
import com.marcuzzo.vaporvoxel.Events.ChunkTransitionEvent;
import com.marcuzzo.vaporvoxel.Events.RegionTransitionEvent;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;


public class MainApplication extends Application {
    public static Group world;
    private Rotate rotation = new Rotate(0, Rotate.X_AXIS);

    /**
     * Controls mouse movement and speed/sensitivity
     */
    private double newX;
    private double newY;
    private double dx;
    private double dy;
    private final double mouseSensitivity = 1.2;
    /**
     * Controls testCamera movement/sensitivity
     */
    private final double moveSpeed = 0.8;
    private final BooleanProperty w = new SimpleBooleanProperty(false);
    private final BooleanProperty a = new SimpleBooleanProperty(false);
    private final BooleanProperty s = new SimpleBooleanProperty(false);
    private final BooleanProperty d = new SimpleBooleanProperty(false);
    private final BooleanBinding anyPressed = w.or(a).or(s).or(d);
    private boolean pause = false;
    public static Player testCamera;
    private final Affine forwardAffine = new Affine();
    private final Affine backwardAffine = new Affine();
    private final Affine leftAffine = new Affine();
    private final Affine rightAffine = new Affine();
    public static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), Thread::new);
    public static ExecutorService interpolExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), Thread::new);
    private final long[] frameTimes = new long[100];
    private int frameTimeIndex = 0;
    private boolean arrayFilled = false;
    private final double wid = 0.0;
    public static String root = System.getenv("APPDATA") + "/.voxelGame/";
    public static RegionManager currentWorld;

    private void reloadWorlds(VBox worldList, VBox worldListBox, StackPane base2D, ScrollPane scrollableWorldListPane) {
        try {
            //Display worlds to load
            DirectoryStream<Path> worldStream = Files.newDirectoryStream(Paths.get(root + "worlds/"));
            Iterator<Path> worldIterator = worldStream.iterator();
            int dirLen = Objects.requireNonNull(new File(root + "worlds/").listFiles()).length;
            AtomicReference<Double> cumHeight = new AtomicReference<>(0.0);

            if (dirLen > 0) {
                worldIterator.forEachRemaining(c -> {
                    String nameText = c.getFileName().toString();
                    ButtonBar bar = new ButtonBar();
                    Label name = new Label(nameText);
                    name.getStyleClass().add("world-labels");
                    Button load = new Button("Load World");
                    load.getStyleClass().add("world-buttons");
                    Button delete = new Button("Delete World");
                    delete.getStyleClass().add("world-buttons");
                    bar.getButtons().addAll(name, load, delete);
                    bar.setMinHeight(40);
                    bar.setMinWidth(name.getWidth() + load.getWidth() + delete.getWidth());
                    cumHeight.updateAndGet(v -> v + bar.getMinHeight());
                    worldList.getChildren().add(bar);

                    worldListBox.setMinHeight(cumHeight.get());
                    worldList.setMinHeight(cumHeight.get());

                    delete.setOnAction(a -> {
                        try {
                            FileUtils.deleteDirectory(Paths.get(root + "worlds/" + c.getFileName().toString()).toFile());
                            worldList.getChildren().remove(bar);
                            cumHeight.updateAndGet(v -> v - bar.getMinHeight());
                            worldListBox.setMinHeight(cumHeight.get());
                            worldList.setMinHeight(cumHeight.get());
                        } catch (IOException e) {
                            Logger.getLogger("Logger").warning(e.getMessage());
                            e.printStackTrace();
                        }
                    });

                    load.setOnAction(a -> {
                        currentWorld = new RegionManager(testCamera, Paths.get(root + "worlds/" + c.getFileName()), world);
                        testCamera.fireEvent(new RegionTransitionEvent(PlayerEvent.REGION_TRANSITION));
                        base2D.getChildren().remove(scrollableWorldListPane);
                    });
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) {

        //Creating game directory if it does not exist
        try {
            Files.createDirectories(Paths.get(root + "worlds/"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Getting frame size
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle bounds = ge.getMaximumWindowBounds();

        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(ge.getDefaultScreenDevice().getDefaultConfiguration());

        Rectangle effectiveScreenArea = new Rectangle();
        effectiveScreenArea.x = bounds.x + screenInsets.left;
        effectiveScreenArea.y = bounds.y + screenInsets.top;
        effectiveScreenArea.height = bounds.height - screenInsets.bottom;
        effectiveScreenArea.width = bounds.width - screenInsets.left - screenInsets.right;

        //GL Canvas
        Canvas canvas2D = new Canvas(effectiveScreenArea.getWidth(), ge.getMaximumWindowBounds().getHeight());
        GraphicsContext gc2D = canvas2D.getGraphicsContext2D();
        world = new Group();        //Camera and manager setup
        world.getChildren().add(new AmbientLight(Color.WHITE));
        testCamera = new Player(true, world);

        //Defining root groups
        AnchorPane globalRoot = new AnchorPane();
        globalRoot.getStylesheets().add("/style.css");
        Scene scene = new Scene(globalRoot, effectiveScreenArea.getWidth(), effectiveScreenArea.getHeight(), true, SceneAntialiasing.BALANCED);

        //3D root
        SubScene sub = new SubScene(world, effectiveScreenArea.getWidth(), ge.getMaximumWindowBounds().getHeight(), true, SceneAntialiasing.BALANCED);

        //=======================================
        //2D scene assets
        //=======================================

        //Framerate and position display information
        AnimationTimer frameRateMeter = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (testCamera != null) {
                    gc2D.clearRect(10, 0, 1000, 100);
                    String currentRegion = "Current Region:";
                    if (currentWorld != null) {
                        currentRegion = "Current Region: (" + testCamera.playerRegion.regionBounds.getX() + ", "
                                + testCamera.playerRegion.regionBounds.getY() + ")";
                    }

                    String position = String.format("Current Position: ( %.3f, %.3f, %.3f)", testCamera.getBoundsInParent().getCenterX(),
                            testCamera.getBoundsInParent().getCenterY(), testCamera.getBoundsInParent().getCenterZ());
                    String chunkPos = "Current Chunk: " + testCamera.playerChunk;
                    String framerate = "Current frame rate:";

                    long oldFrameTime = frameTimes[frameTimeIndex];
                    frameTimes[frameTimeIndex] = now;
                    frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length;
                    if (frameTimeIndex == 0) {
                        arrayFilled = true;
                    }
                    if (arrayFilled) {
                        long elapsedNanos = now - oldFrameTime;
                        long elapsedNanosPerFrame = elapsedNanos / frameTimes.length;
                        double frameRate = 1_000_000_000.0 / elapsedNanosPerFrame;
                        framerate = String.format("Current frame rate: %.3f", frameRate);
                    }

                    gc2D.fillText(framerate, 10, 15);
                    gc2D.fillText(position, 10, 30);
                    gc2D.fillText(chunkPos, 10, 45);
                    gc2D.fillText(currentRegion, 10, 60);
                }
            }
        };
        frameRateMeter.start();

        //Pane and vBoxes the worlds will be displayed in
        VBox worldList = new VBox();
        VBox worldListBox = new VBox();
        ScrollPane scrollableWorldListPane = new ScrollPane();
        StackPane base2D = new StackPane(canvas2D);
        base2D.getChildren().add(scrollableWorldListPane);

        scrollableWorldListPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollableWorldListPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        worldListBox.getChildren().add(worldList);

        base2D.setAlignment(Pos.CENTER);
        worldListBox.setAlignment(Pos.CENTER);
        worldList.setAlignment(Pos.CENTER);

        base2D.setPrefSize(effectiveScreenArea.getWidth(), effectiveScreenArea.getHeight());
        worldList.setMaxWidth(effectiveScreenArea.getWidth() / 4);
        scrollableWorldListPane.setMaxSize(effectiveScreenArea.getWidth(), effectiveScreenArea.getHeight() / 4);
        worldListBox.setMaxWidth(worldList.getWidth());

        HBox worldMenu = new HBox();
        reloadWorlds(worldList, worldListBox, base2D, scrollableWorldListPane);

        Rotate camRot = new Rotate(-90, Rotate.X_AXIS);
        //Sets cameras vertical location at the height of the block at 0,0
        Transform camTrans = new Translate(0, -Chunk.getGlobalHeightMapValue(0, 0) - 1, 0);
        testCamera.setFarClip(2000);
        testCamera.setNearClip(1);
        testCamera.getTransforms().addAll(camRot, camTrans);
        testCamera.setFieldOfView(60);
        sub.setCamera(testCamera);
        globalRoot.getChildren().add(sub);

        stage.widthProperty().addListener((observable, oldValue, newValue) -> {
            base2D.setPrefSize(stage.getWidth(), stage.getHeight());
            worldListBox.setMinSize(stage.getWidth(), scrollableWorldListPane.getPrefHeight());
            worldList.setMinSize(wid, worldListBox.getHeight() - worldListBox.getHeight() / 3);
            worldList.setMaxWidth(wid);
            scrollableWorldListPane.setPrefSize(wid, stage.getHeight() / 4);
            worldMenu.setMinWidth(effectiveScreenArea.width / 4.0);

            scene.widthProperty().subtract(scene.widthProperty().get());
          //  System.out.println(scene.widthProperty().get());
            scene.widthProperty().add(stage.widthProperty().get());

         //   System.out.println("scene: " + scene.getWidth());
         //   System.out.println("stage: " + stage.getWidth());
        });
        stage.heightProperty().addListener((observable, oldValue, newValue) -> {
            base2D.setPrefSize(stage.getWidth(), stage.getHeight());
            worldListBox.setMinSize(stage.getWidth(), scrollableWorldListPane.getPrefHeight());
            worldList.setMinSize(wid, worldListBox.getHeight() - worldListBox.getHeight() / 3);
            worldList.setMaxWidth(wid);
            scrollableWorldListPane.setPrefSize(wid, stage.getHeight() / 4);
            worldMenu.setMinWidth(effectiveScreenArea.width / 4.0);

            scene.heightProperty().subtract(scene.heightProperty().get());
         //   System.out.println(scene.heightProperty().get());
            scene.heightProperty().add(stage.heightProperty().get());

          //  System.out.println("scene: " + scene.getHeight());
         //   System.out.println("Stage: " + stage.getHeight());

        });

        TextField worldName = new TextField();
        worldName.setPromptText("Enter a world name");
        TextField seed = new TextField();
        seed.setPromptText("Enter a seed value");
        Button create = new Button("Create World");
        worldMenu.getChildren().addAll(worldName, seed, create);
        create.setOnAction(a -> {
            try {
                Files.createDirectories(Path.of(root + "worlds/" + worldName.getText() + "/regions"));
                worldList.getChildren().remove(1, worldList.getChildren().size());
                reloadWorlds(worldList, worldListBox, base2D, scrollableWorldListPane);
            } catch (FileAlreadyExistsException e) {
                create.setTooltip(new Tooltip("World name must be unique!"));
                create.getTooltip().setShowDuration(Duration.millis(500));
                create.getTooltip().show(worldList, worldList.getBoundsInParent().getCenterX(), worldList.getBoundsInParent().getCenterY());


            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        worldList.getChildren().add(0, worldMenu);

        scrollableWorldListPane.setContent(worldListBox);
        globalRoot.getChildren().add(base2D);

        //Stage setup
        stage.setTitle("TestFrame");
        stage.setScene(scene);
        stage.show();

        AtomicBoolean pressed = new AtomicBoolean(false);
        scene.setOnMouseEntered((MouseEvent event) -> {
            pressed.set(true);
            newX = event.getSceneX();
            newY = event.getSceneY();
        });

        scene.setOnMouseMoved((MouseEvent event) -> {
            if (pressed.get() && !pause) {
                dx = event.getSceneX() - newX;
                dy = event.getSceneY() - newY;
                //Left
                if (dx < 0) {
                    rotation = new Rotate(-mouseSensitivity, testCamera.getTranslateX(), testCamera.getTranslateY(), testCamera.getTranslateZ(), Rotate.Y_AXIS);
                }
                //Right
                else if (dx > 0) {
                    rotation = new Rotate(mouseSensitivity, testCamera.getTranslateX(), testCamera.getTranslateY(), testCamera.getTranslateZ(), Rotate.Y_AXIS);
                }
                //Up
                else if (dy < 0) {
                    rotation = new Rotate(mouseSensitivity, testCamera.getTranslateX(), testCamera.getTranslateY(), testCamera.getTranslateZ(), Rotate.X_AXIS);
                }
                //Down
                else if (dy > 0) {
                    rotation = new Rotate(-mouseSensitivity, testCamera.getTranslateX(), testCamera.getTranslateY(), testCamera.getTranslateZ(), Rotate.X_AXIS);
                }

                testCamera.getTransforms().add(rotation);
                newX = event.getSceneX();
                newY = event.getSceneY();
            }
        });

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()){
                case A -> a.set(true);
                case D -> d.set(true);
                case W -> w.set(true);
                case S -> s.set(true);
                case ESCAPE -> pause = !pause;
                default -> {
                }
            }
        });

        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case W -> w.set(false);
                case A -> a.set(false);
                case S -> s.set(false);
                case D -> d.set(false);
                default -> {
                }
            }
        });

        /*
         Since the KeyPressed event only outputs the latest key event and not concurrent key events
         the event triggers are only used to set booleans to true on key pressed events
         and false on key released events. This timer and boolean binding are responsible for
         doing testCamera movements at the same time for concurrent key presses.
         */

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long timestamp) {
                if (currentWorld != null) {
                    if (testCamera.playerRegion.getChunkWithPlayer() != testCamera.playerChunk)
                        testCamera.fireEvent(new ChunkTransitionEvent(PlayerEvent.CHUNK_TRANSITION));

                    if (RegionManager.getRegionWithPlayer() != testCamera.playerRegion)
                       testCamera.fireEvent(new RegionTransitionEvent(PlayerEvent.REGION_TRANSITION));


                    if (!pause) {
                        if (w.get()) {
                            testCamera.getTransforms().add(forwardAffine);
                            moveForward();
                        }
                        if (a.get()) {
                            strafeLeft();
                            testCamera.getTransforms().add(leftAffine);
                        }
                        if (s.get()) {
                            moveBack();
                            testCamera.getTransforms().add(backwardAffine);
                        }
                        if (d.get()) {
                            strafeRight();
                            testCamera.getTransforms().add(rightAffine);
                        }
                    }
                }
            }
        };

        anyPressed.addListener((obs, wasPressed, isNowPressed) -> {
            if (isNowPressed) {
                timer.start();
            } else {
                timer.stop();
            }
        });
    }
    /**===============================================
     *Movement
    ==============================================*/
    private void moveForward() {
        forwardAffine.setTx(getPosition().getX() + moveSpeed * getN().getX());
        forwardAffine.setTy(getPosition().getY() + moveSpeed * getN().getY());
        forwardAffine.setTz(getPosition().getZ() + moveSpeed * getN().getZ());
    }

    private void strafeLeft() {
        leftAffine.setTx(getPosition().getX() + moveSpeed * -getU().getX());
        leftAffine.setTy(getPosition().getY() + moveSpeed * -getU().getY());
        leftAffine.setTz(getPosition().getZ() + moveSpeed * -getU().getZ());
    }

    private void strafeRight() {
        rightAffine.setTx(getPosition().getX() + moveSpeed * getU().getX());
        rightAffine.setTy(getPosition().getY() + moveSpeed * getU().getY());
        rightAffine.setTz(getPosition().getZ() + moveSpeed * getU().getZ());
    }

    private void moveBack() {
        backwardAffine.setTx(getPosition().getX() + moveSpeed * -getN().getX());
        backwardAffine.setTy(getPosition().getY() + moveSpeed * -getN().getY());
        backwardAffine.setTz(getPosition().getZ() + moveSpeed * -getN().getZ());
    }
/*
    private void moveUp() {
        affine.setTx(getPosition().getX() + moveSpeed * -getV().getX());
        affine.setTy(getPosition().getY() + moveSpeed * -getV().getY());
        affine.setTz(getPosition().getZ() + moveSpeed * -getV().getZ());
    }

    private void moveDown() {
        affine.setTx(getPosition().getX() + moveSpeed * getV().getX());
        affine.setTy(getPosition().getY() + moveSpeed * getV().getY());
        affine.setTz(getPosition().getZ() + moveSpeed * getV().getZ());
    }
    */

    /**===============================================
    *Callbacks
     *==============================================*/
    //Forward / look direction
    //private final Callback<Transform, Point3D> F = (a) -> new Point3D(a.getMzx(), a.getMzy(), a.getMzz());
    private final Callback<Transform, Point3D> N = (a) -> new Point3D(a.getMxz(), a.getMyz(), a.getMzz());
    // up direction
    //private final Callback<Transform, Point3D> UP = (a) -> new Point3D(a.getMyx(), a.getMyy(), a.getMyz());
    //private final Callback<Transform, Point3D> V = (a) -> new Point3D(a.getMxy(), a.getMyy(), a.getMzy());
    // right direction
    //private final Callback<Transform, Point3D> R = (a) -> new Point3D(a.getMxx(), a.getMxy(), a.getMxz());
    private final Callback<Transform, Point3D> U = (a) -> new Point3D(a.getMxx(), a.getMyx(), a.getMzx());
    //position
    private final Callback<Transform, Point3D> P = (a) -> new Point3D(a.getTx(), a.getTy(), a.getTz());
    /*
    private Point3D getF() {
        return F.call(world.getLocalToSceneTransform());
    }

    public Point3D getLookDirection() {
        return getF();
    }
    */
    private Point3D getN() {
        return N.call(world.getLocalToSceneTransform());
    }
/*
    public Point3D getLookNormal() {
        return getN();
    }

    private Point3D getR() {
        return R.call(world.getLocalToSceneTransform());
    }
*/
    private Point3D getU() {
        return U.call(world.getLocalToSceneTransform());
    }
/*
    private Point3D getUp() {
        return UP.call(world.getLocalToSceneTransform());
    }

    private Point3D getV() {
        return V.call(world.getLocalToSceneTransform());
    }
*/
    public final Point3D getPosition() {
        return P.call(world.getLocalToSceneTransform());
    }

    public static void main(String[] args) {
        launch();
    }
}