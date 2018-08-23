package com.testing.vladyslav.cubes.objects;

import com.testing.vladyslav.cubes.util.Geometry;

public class Point {

    public float x;
    public float y;
    public float z;

    public Point(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void translateX(float dx){this.x+=dx; }
    public void translateY(float dy){this.y+=dy; }
    public void translateZ(float dz){this.z+=dz; }

    public Point translate(Geometry.Vector vector) {
        return new Point(
                x + vector.x,
                y + vector.y,
                z + vector.z);
    }

    public Point clone(){

        return new Point(x, y, z);

    }

}
