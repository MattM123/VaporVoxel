package com.marcuzzo.vaporvoxel;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainApplication extends Application {
    private Group world;
    private double newX;
    private double newY;
    private double dx;
    private double dy;

    /**
     * How much to increment or decrement the camera rotation every time the mouse is moved
     */
    private final double mouseSensitivity = 1.5;
    /**
     * Controls camera move speed
     */
    private final double moveSpeed = 0.5;
    private final BooleanProperty w = new SimpleBooleanProperty(false);
    private final BooleanProperty a = new SimpleBooleanProperty(false);
    private final BooleanProperty s = new SimpleBooleanProperty(false);
    private final BooleanProperty d = new SimpleBooleanProperty(false);
    private final BooleanBinding anyPressed = w.or(a).or(s).or(d);
    private boolean pause = false;
    private Rotate rotation = new Rotate(0, Rotate.X_AXIS);
    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private final Affine forwardAffine = new Affine();
    private final Affine backwardAffine = new Affine();
    private final Affine leftAffine = new Affine();
    private final Affine rightAffine = new Affine();
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("view.fxml"));
        Parent root = fxmlLoader.load();
        world = new Group(root);

        Scene scene = new Scene(world, Double.MAX_VALUE, Double.MAX_VALUE);
        stage.setTitle("VaporVoxel");
        stage.setScene(scene);
        stage.show();
        world.getChildren().add(new AmbientLight(Color.WHITE));
        Chunk chunk = new Chunk().initialize(0,0,0);
        Chunk chunk1 = new Chunk().initialize(32,0,32);

        camera.setFarClip(2000);
        camera.setNearClip(1);
        scene.setCamera(camera);
        AtomicBoolean pressed = new AtomicBoolean(false);

        Thread t = new Thread(() -> Platform.runLater(() -> {
            chunk.updateChunk(world);
            chunk1.updateChunk(world);
        }));



        t.start();


        scene.setOnMouseEntered((MouseEvent event) -> {
            pressed.set(true);
            newX = event.getSceneX();
            newY = event.getSceneY();
        });

       // Robot mouseMover = new Robot();
        scene.setOnMouseMoved((MouseEvent event) -> {
        //    chunk.updateChunk(world);
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