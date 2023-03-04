package com.marcuzzo.vaporvoxel;

import javafx.geometry.Point3D;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;

class Point3DByComposition implements Serializable {
    Point3D myPoint;

    public Point3DByComposition(double x, double y, double z) {
        myPoint = new Point3D(x, y, z) ;
    }

   // public Point3D getPoint() {
     //   return myPoint;
   // }

    @Serial
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeDouble(myPoint.getX());
        out.writeDouble(myPoint.getY());
        out.writeDouble(myPoint.getZ());
    }

    @Serial
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        myPoint = new Point3D(in.readDouble(), in.readDouble(), in.readDouble()) ;
    }
}