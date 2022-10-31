package com.marcuzzo.vaporvoxel;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.*;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class HelloApplication extends Application {
    private Group group;
    private Scene scene;
    private double oldX;
    private double oldY;

    private double newX;
    private double newY;

    private double dx;
    private double dy;

    private Rotate yaw;
    private Rotate pitch;
    /**
     * How much to increment or decrement the camera position every time the mouse is moved
     */
    private final double mouseSensitivity = 0.03;
    /**
     * How much to increment or decrement the camera position every time a directional key is pressed
     */
    private final double cameraSensitivity = 0.03;
    private double camX;
    private double camZ;
    private final BooleanProperty w = new SimpleBooleanProperty(false);
    private final BooleanProperty a = new SimpleBooleanProperty(false);
    private final BooleanProperty s = new SimpleBooleanProperty(false);
    private final BooleanProperty d = new SimpleBooleanProperty(false);
    private final BooleanBinding anyPressed = w.or(a).or(s).or(d);
    private boolean pause = false;
    private double taskbarHeight = Toolkit.getDefaultToolkit().getScreenSize().height
            - GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getHeight();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Parent root = fxmlLoader.load();
        group = new Group();
        scene = new Scene(group, Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
                Toolkit.getDefaultToolkit().getScreenSize().getHeight() - taskbarHeight);
        stage.setTitle("Test");


        stage.setScene(scene);
        stage.show();

        yaw = new Rotate(0, Rotate.Y_AXIS);
        pitch = new Rotate(0, Rotate.X_AXIS);

        Box box = new Box(5, 5, 5);
        box.setMaterial(new PhongMaterial(Color.BLUE));
        box.setTranslateZ(40);

        group.getChildren().add(box);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.getTransforms().addAll(yaw, pitch);
        scene.setCamera(camera);

        /*===========================================
        Controls camera movement and rotation
        ===========================================*/
        AtomicBoolean pressed = new AtomicBoolean(false);

        scene.setOnMousePressed((MouseEvent event) -> {
            pressed.set(true);
            newX = event.getSceneX();
            newY = event.getSceneY();
        });

        scene.setOnMouseMoved((MouseEvent event) -> {
            if (pressed.get()) {
                oldX = newX;
                oldY = newY;
                newX = event.getSceneX();
                newY = event.getSceneY();
                dx = newX - oldX;
                dy = newY - oldY;

                camera.setTranslateX(camera.getTranslateX() - dx * mouseSensitivity);
                camera.setTranslateY(camera.getTranslateY() - dy * mouseSensitivity);
            }
        });
        camX = 0.0;
        camZ = 0.0;

        scene.setOnKeyPressed(event -> {
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
                        camera.setTranslateZ(camera.getTranslateZ() + camZ);
                    else
                        camera.setTranslateZ(camera.getTranslateZ() - camZ);
                }
                if (a.get()) {
                    camX += cameraSensitivity;
                    if (camX > 0)
                        camera.setTranslateX(camera.getTranslateX() + camX);
                    else
                        camera.setTranslateX(camera.getTranslateX() - camX);
                }
                if (s.get()) {
                    camZ -= cameraSensitivity;
                    if (camZ > 0)
                        camera.setTranslateZ(camera.getTranslateZ() - camZ);
                    else
                        camera.setTranslateZ(camera.getTranslateZ() + camZ);
                }
                if (d.get()) {
                    camX -= cameraSensitivity;
                    if (camX > 0)
                        camera.setTranslateX(camera.getTranslateX() - camX);
                    else
                        camera.setTranslateX(camera.getTranslateX() + camX);
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