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
    requires javafx.swing;
    requires xmlgraphics.commons;
    requires javafxsvg;
    requires java.logging;
    requires batik.transcoder;
    requires jogl.all;
    requires org.apache.commons.io;

    opens com.marcuzzo.vaporvoxel to javafx.fxml;
    exports com.marcuzzo.vaporvoxel;
    exports com.marcuzzo.vaporvoxel.Events;
    opens com.marcuzzo.vaporvoxel.Events to javafx.fxml;
    exports com.marcuzzo.vaporvoxel.EventTypes;
    opens com.marcuzzo.vaporvoxel.EventTypes to javafx.fxml;
}