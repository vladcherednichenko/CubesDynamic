package com.testing.vladyslav.cubes.util;

import com.testing.vladyslav.cubes.objects.PixioPoint;

public class Geometry {

    public static boolean intersects(Sphere sphere, Ray ray){

        return distanceBetween(sphere.center, ray) < sphere.radius;

    }


    public static float distanceBetween(PixioPoint point, Ray ray) {
        Vector p1ToPoint = vectorBetween(ray.point, point);
        Vector p2ToPoint = vectorBetween(ray.point.translate(ray.vector), point);

        // The length of the cross product gives the area of an imaginary
        // parallelogram having the two vectors as sides. A parallelogram can be
        // thought of as consisting of two triangles, so this is the same as
        // twice the area of the triangle defined by the two vectors.
        // http://en.wikipedia.org/wiki/Cross_product#Geometric_meaning
        float areaOfTriangleTimesTwo = p1ToPoint.crossProduct(p2ToPoint).length();
        float lengthOfBase = ray.vector.length();
        // The area of p triangle is also equal to (base * height) / 2. In
        // other words, the height is equal to (area * 2) / base. The height
        // of this triangle is the distance from the point to the ray.
        float distanceFromPointToRay = areaOfTriangleTimesTwo / lengthOfBase;
        return distanceFromPointToRay;
    }


    public static class Ray {
        public final PixioPoint point;
        public final Vector vector;
        public Ray(PixioPoint point, Vector vector) {
            this.point = point;
            this.vector = vector;
        }
    }

    public static class Vector {
        public float x, y, z;
        public Vector(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Vector (PixioPoint a, PixioPoint b){

            this.x = b.x - a.x;
            this.y = b.y - a.y;
            this.z = b.z - a.z;

        }

        public float length() {
            return (float)Math.sqrt(
                    x * x
                            + y * y
                            + z * z);
        }

        public Vector crossProduct(Vector other) {
            return new Vector(
                    (y * other.z) - (z * other.y),
                    (z * other.x) - (x * other.z),
                    (x * other.y) - (y * other.x));
        }



    }



    public static class Sphere {
        public final PixioPoint center;
        public final float radius;
        public Sphere(PixioPoint center, float radius) {
            this.center = center;
            this.radius = radius;
        }
    }
    public static Vector vectorBetween(PixioPoint from, PixioPoint to) {
        return new Vector(
                to.x - from.x,
                to.y - from.y,
                to.z - from.z);
    }


    public static class Parallelogram{


        public String name;
        public PixioPoint p, q, r;
        public Vector normal;

        public Parallelogram(PixioPoint p, PixioPoint q, PixioPoint r, Vector normal, String name) {
            this.p = p;
            this.q = q;
            this.r = r;
            this.name = name;
            this.normal = normal;
        }


        public boolean pointInside(PixioPoint a){

            Vector pq = new Vector (this.p, q);
            Vector pr = new Vector (this.p, r);
            Vector pa = new Vector (this.p, a);

            float n = -det(pa, pq) / det(pq, pr);
            float m = det(pa, pr) / det(pq, pr);

            return n > 0 && n < 1 && m > 0 && m < 1;


        }

        private float det(Vector a, Vector b){

            return a.x * b.y - b.x * a.y;

        }

        public float getClosestZPositionToScreen(){

            float f = -1000f;

            if (p.z > f){
                f = p.z;
            }

            if(q.z > f){
                f = q.z;
            }

            if(r.z > f){
                f = r.z;
            }

            return f;

        }
    }


}
