module com.marcuzzo.vaporvoxel {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires org.fxyz3d.core;
    requires org.apache.commons.lang3;
    requires com.google.common;

    opens com.marcuzzo.vaporvoxel to javafx.fxml;
    exports com.marcuzzo.vaporvoxel;
}