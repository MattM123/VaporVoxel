package com.marcuzzo.vaporvoxel;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class HelloApplication extends Application {
    private Group world;
    private Scene scene;
    private double newX;
    private double newY;
    private double dx;
    private double dy;

    /**
     * How much to increment or decrement the camera rotation every time the mouse is moved
     */
    private final double mouseSensitivity = 0.005;
    /**
     * How much to increment or decrement the camera position every time a directional key is pressed
     */
    private final double cameraSensitivity = 0.01;
    private double camX = 0.0;
    private double camZ = 0.0;
    private final BooleanProperty w = new SimpleBooleanProperty(false);
    private final BooleanProperty a = new SimpleBooleanProperty(false);
    private final BooleanProperty s = new SimpleBooleanProperty(false);
    private final BooleanProperty d = new SimpleBooleanProperty(false);
    private final BooleanBinding anyPressed = w.or(a).or(s).or(d);
    private boolean pause = false;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Parent root = fxmlLoader.load();
        world = new Group(root);

        scene = new Scene(world, Double.MAX_VALUE, Double.MAX_VALUE);
        stage.setTitle("VaporVoxel");
        stage.setScene(scene);
        stage.show();

        Box box = new Box(5, 5, 5);
        box.setMaterial(new PhongMaterial(Color.BLUE));
        box.setTranslateZ(40);
        world.getChildren().add(box);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setFarClip(2000);
        camera.setNearClip(1);
        scene.setCamera(camera);
        scene.setCursor(Cursor.NONE);
        Rotate worldRotX = new Rotate(0, Rotate.X_AXIS);
        Rotate worldRotY = new Rotate(0, Rotate.Y_AXIS);

        Translate worldTransX = new Translate();
        world.getTransforms().addAll(worldRotY, worldRotX, worldTransX);
        /*===========================================
        Controls camera movement and rotation
        ===========================================*/

        AtomicBoolean pressed = new AtomicBoolean(false);

        scene.setOnMouseEntered((MouseEvent event) -> {
            pressed.set(true);
            newX = event.getSceneX();
            newY = event.getSceneY();
        });

        scene.setOnMouseMoved((MouseEvent event) -> {
          //  worldRotX.setPivotX(camera.getTranslateX());
          //  worldRotX.setPivotY(camera.getTranslateY());
          //  worldRotX.setPivotZ(camera.getTranslateZ());

         //   worldRotY.setPivotX(camera.getTranslateX());
         //   worldRotY.setPivotY(camera.getTranslateY());
         //   worldRotY.setPivotZ(camera.getTranslateZ());
          //  camera.setTranslateX(world.getTranslateX());
         //   camera.setTranslateY(world.getTranslateY());
          //  camera.setTranslateZ(world.getTranslateZ());

            if (pressed.get()) {
                dx = event.getSceneX() - newX;
                dy = event.getSceneY() - newY;

                //Left
                if (dx < 0) {
                    worldRotY.setAngle(worldRotY.getAngle() - dx);
                }
                //Right
                else if (dx > 0) {
                    worldRotY.setAngle(worldRotY.getAngle() - dx);
                }
                //Up
                else if (dy < 0) {
                    worldRotX.setAngle(worldRotX.getAngle() + dy);
                }
                //Down
                else if (dy > 0) {
                    worldRotX.setAngle(worldRotX.getAngle() + dy);
                }
                newX = event.getSceneX();
                newY = event.getSceneY();
            }
        });

        scene.setOnKeyPressed(event -> {
            System.out.println(camera.getBoundsInParent().getCenterX() + " " + camera.getBoundsInParent().getCenterY()
                    + " " + camera.getBoundsInParent().getCenterZ());
            switch (event.getCode()){
                case A -> a.set(true);
                case D -> d.set(true);
                case W -> w.set(true);
                case S -> s.set(true);
                case ESCAPE -> pause = true;
            }
        });

        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case W -> w.set(false);
                case A -> a.set(false);
                case S -> s.set(false);
                case D -> d.set(false);
                case ESCAPE -> pause = false;
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
                if (w.get()) {
                    camZ += cameraSensitivity;
                    if (camZ > 0)
                        world.setTranslateZ(world.getTranslateZ() - camZ);
                    else
                        world.setTranslateZ(world.getTranslateZ() + camZ);
                }
                if (a.get()) {
                    camX -= cameraSensitivity;
                    if (camX > 0)
                        world.setTranslateX(world.getTranslateX() + camX);
                    else
                        world.setTranslateX(world.getTranslateX() - camX);
                }
                if (s.get()) {
                    camZ -= cameraSensitivity;
                    if (camZ > 0)
                        world.setTranslateZ(world.getTranslateZ() + camZ);
                    else
                        world.setTranslateZ(world.getTranslateZ() - camZ);
                }
                if (d.get()) {
                    camX += cameraSensitivity;
                    if (camX > 0)
                        world.setTranslateX(world.getTranslateX() - camX);
                    else
                        world.setTranslateX(world.getTranslateX() + camX);
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

    public static void main(String[] args) {
        launch();
    }
}