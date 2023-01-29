package com.marcuzzo.vaporvoxel;

import com.marcuzzo.vaporvoxel.EventTypes.PlayerEvent;
import com.marcuzzo.vaporvoxel.Events.ChunkTransitionEvent;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;


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
     * Controls camera movement/sensitivity
     */
    private final double moveSpeed = 0.8;
    private final BooleanProperty w = new SimpleBooleanProperty(false);
    private final BooleanProperty a = new SimpleBooleanProperty(false);
    private final BooleanProperty s = new SimpleBooleanProperty(false);
    private final BooleanProperty d = new SimpleBooleanProperty(false);
    private final BooleanBinding anyPressed = w.or(a).or(s).or(d);
    private boolean pause = false;
    private Player camera;
    private final Affine forwardAffine = new Affine();
    private final Affine backwardAffine = new Affine();
    private final Affine leftAffine = new Affine();
    private final Affine rightAffine = new Affine();
    public static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), Thread::new);
    public static ExecutorService interpolExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), Thread::new);
    public static ChunkManager publicManager;
    private final long[] frameTimes = new long[100];
    private int frameTimeIndex = 0 ;
    private boolean arrayFilled = false ;

    @Override
    public void start(Stage stage) {
        //Group root = new Group();
        world = new Group();

        //Getting frame size
        GraphicsEnvironment gc = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle bounds = gc.getMaximumWindowBounds();

        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc.getDefaultScreenDevice().getDefaultConfiguration());

        Rectangle effectiveScreenArea = new Rectangle();
        effectiveScreenArea.x = bounds.x + screenInsets.left;
        effectiveScreenArea.y = bounds.y + screenInsets.top;
        effectiveScreenArea.height = bounds.height - screenInsets.top - screenInsets.bottom;
        effectiveScreenArea.width = bounds.width - screenInsets.left - screenInsets.right;

        //Camera and manager setup
        world.getChildren().add(new AmbientLight(Color.WHITE));
        camera = new Player(true, world);
        ChunkManager manager = new ChunkManager(camera, world);
        publicManager = manager;
        camera.setManager(manager);

        Rotate camRot = new Rotate(-90, Rotate.X_AXIS);
        camera.setFarClip(2000);
        camera.setNearClip(1);
        camera.getTransforms().add(camRot);
        camera.setFieldOfView(60);


        //Defining root groups
        AnchorPane globalRoot = new AnchorPane();
        Scene scene = new Scene(globalRoot, effectiveScreenArea.getWidth(), effectiveScreenArea.getHeight(), true, SceneAntialiasing.BALANCED);

        //3D root
        SubScene sub = new SubScene(world,  effectiveScreenArea.getWidth(), effectiveScreenArea.getHeight(), true, SceneAntialiasing.BALANCED);
        sub.setCamera(camera);
        globalRoot.getChildren().add(sub);

        //Framerate and position display information
        Label framerate = new Label();
        Label chunkCache = new Label();
        Label position = new Label();
        Label chunkPos = new Label();

        framerate.setTranslateY(0);
        chunkCache.setTranslateY(15);
        position.setTranslateY(30);
        chunkPos.setTranslateY(45);

        AnimationTimer frameRateMeter = new AnimationTimer() {
            @Override
            public void handle(long now) {
                chunkCache.setText("Chunk Cache Size: " + manager.size());
                position.setText("Current Position: (" + camera.getBoundsInParent().getCenterX() + ", "
                        + camera.getBoundsInParent().getCenterY() + ", " + camera.getBoundsInParent().getCenterZ());
                chunkPos.setText("Current Chunk: " + camera.playerChunk);

                long oldFrameTime = frameTimes[frameTimeIndex] ;
                frameTimes[frameTimeIndex] = now ;
                frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length ;
                if (frameTimeIndex == 0) {
                    arrayFilled = true ;
                }
                if (arrayFilled) {
                    long elapsedNanos = now - oldFrameTime ;
                    long elapsedNanosPerFrame = elapsedNanos / frameTimes.length ;
                    double frameRate = 1_000_000_000.0 / elapsedNanosPerFrame ;
                    framerate.setText(String.format("Current frame rate: %.3f", frameRate));
                }
            }
        };
        frameRateMeter.start();
        globalRoot.getChildren().addAll(framerate, chunkCache, position, chunkPos);


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
                    rotation = new Rotate(-mouseSensitivity, camera.getTranslateX(), camera.getTranslateY(), camera.getTranslateZ(), Rotate.Y_AXIS);
                }
                //Right
                else if (dx > 0) {
                    rotation = new Rotate(mouseSensitivity, camera.getTranslateX(), camera.getTranslateY(), camera.getTranslateZ(), Rotate.Y_AXIS);
                }
                //Up
                else if (dy < 0) {
                    rotation = new Rotate(mouseSensitivity, camera.getTranslateX(), camera.getTranslateY(), camera.getTranslateZ(), Rotate.X_AXIS);
                }
                //Down
                else if (dy > 0) {
                    rotation = new Rotate(-mouseSensitivity, camera.getTranslateX(), camera.getTranslateY(), camera.getTranslateZ(), Rotate.X_AXIS);
                }

                camera.getTransforms().add(rotation);
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
         doing camera movements at the same time for concurrent key presses.
         */
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long timestamp) {
                if (manager.getChunkWithPlayer() != camera.playerChunk)
                    camera.fireEvent(new ChunkTransitionEvent(PlayerEvent.CHUNK_TRANSITION));


                if (!pause) {
                    if (w.get()) {
                        camera.getTransforms().add(forwardAffine);
                        moveForward();
                    }
                    if (a.get()) {
                        strafeLeft();
                        camera.getTransforms().add(leftAffine);
                    }
                    if (s.get()) {
                        moveBack();
                        camera.getTransforms().add(backwardAffine);
                    }
                    if (d.get()) {
                        strafeRight();
                        camera.getTransforms().add(rightAffine);
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