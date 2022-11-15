module com.marcuzzo.vaporvoxel {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires org.fxyz3d.core;
    requires annotations;
    requires javafx.graphics;

    opens com.marcuzzo.vaporvoxel to javafx.fxml;
    exports com.marcuzzo.vaporvoxel;
}