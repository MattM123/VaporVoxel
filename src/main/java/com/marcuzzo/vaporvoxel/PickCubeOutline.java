package com.marcuzzo.vaporvoxel;

import javafx.scene.paint.Color;
import org.fxyz3d.geometry.Point3D;
import org.fxyz3d.shapes.composites.PolyLine3D;

import java.util.List;

public class PickCubeOutline extends PolyLine3D {
    public PickCubeOutline(List<Point3D> points, float width, Color color, LineType lineType) {
        super(points, width, color, lineType);

    }
}
