package com.testing.vladyslav.cubes;

import com.testing.vladyslav.cubes.objects.PixioPoint;
import com.testing.vladyslav.cubes.util.Geometry;

public class Facet {

    public Geometry.Vector normal;
    public PixioPoint A;
    public PixioPoint B;
    public PixioPoint C;

    public Facet(){};

    public Facet(Geometry.Vector normal, PixioPoint a, PixioPoint b, PixioPoint c) {
        this.normal = normal;
        A = a;
        B = b;
        C = c;
    }
}
