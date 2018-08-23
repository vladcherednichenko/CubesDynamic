package com.testing.vladyslav.cubes;

import com.testing.vladyslav.cubes.objects.Point;
import com.testing.vladyslav.cubes.util.Geometry;

public class Facet {

    public Geometry.Vector normal;
    public Point A;
    public Point B;
    public Point C;

    public Facet(){};

    public Facet(Geometry.Vector normal, Point a, Point b, Point c) {
        this.normal = normal;
        A = a;
        B = b;
        C = c;
    }
}
