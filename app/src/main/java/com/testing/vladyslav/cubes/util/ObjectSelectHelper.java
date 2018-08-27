package com.testing.vladyslav.cubes.util;

import android.util.Log;

import com.testing.vladyslav.cubes.objects.Point;

import java.util.ArrayList;
import java.util.Iterator;

import static android.opengl.Matrix.multiplyMV;

public class ObjectSelectHelper {

    public static Point getTouchedCubeSide(Point center, Point touch, float[] modelMatrix){

        float cubeSize = 1f;

        Point A = new Point(center.x + cubeSize/2 , center.y + cubeSize/2, center.z + cubeSize/2);
        Point B = new Point(center.x - cubeSize/2 , center.y + cubeSize/2, center.z + cubeSize/2);
        Point C = new Point(center.x + cubeSize/2 , center.y - cubeSize/2, center.z + cubeSize/2);
        Point D = new Point(center.x + cubeSize/2 , center.y + cubeSize/2, center.z - cubeSize/2);

        Point A1 = new Point(center.x - cubeSize/2 , center.y - cubeSize/2, center.z - cubeSize/2);
        Point B1 = new Point(center.x + cubeSize/2 , center.y - cubeSize/2, center.z - cubeSize/2);
        Point C1 = new Point(center.x - cubeSize/2 , center.y + cubeSize/2, center.z - cubeSize/2);
        Point D1 = new Point(center.x - cubeSize/2 , center.y - cubeSize/2, center.z + cubeSize/2);

        float[] Apos = new float[4];
        float[] Bpos = new float[4];
        float[] Cpos = new float[4];
        float[] Dpos = new float[4];

        float[] A1pos = new float[4];
        float[] B1pos = new float[4];
        float[] C1pos = new float[4];
        float[] D1pos = new float[4];

        multiplyMV (Apos,0, modelMatrix, 0, new float[]{A.x, A.y, A.z, 0}, 0);
        multiplyMV (Bpos,0, modelMatrix, 0, new float[]{B.x, B.y, B.z, 0}, 0);
        multiplyMV (Cpos,0, modelMatrix, 0, new float[]{C.x, C.y, C.z, 0}, 0);
        multiplyMV (Dpos,0, modelMatrix, 0, new float[]{D.x, D.y, D.z, 0}, 0);

        multiplyMV (A1pos,0, modelMatrix, 0, new float[]{A1.x, A1.y, A1.z, 0}, 0);
        multiplyMV (B1pos,0, modelMatrix, 0, new float[]{B1.x, B1.y, B1.z, 0}, 0);
        multiplyMV (C1pos,0, modelMatrix, 0, new float[]{C1.x, C1.y, C1.z, 0}, 0);
        multiplyMV (D1pos,0, modelMatrix, 0, new float[]{D1.x, D1.y, D1.z, 0}, 0);

        A = new Point (Apos[0], Apos[1], Apos[2]);
        B = new Point (Bpos[0], Bpos[1], Bpos[2]);
        C = new Point (Cpos[0], Cpos[1], Cpos[2]);
        D = new Point (Dpos[0], Dpos[1], Dpos[2]);

        A1 = new Point (A1pos[0], A1pos[1], A1pos[2]);
        B1 = new Point (B1pos[0], B1pos[1], B1pos[2]);
        C1 = new Point (C1pos[0], C1pos[1], C1pos[2]);
        D1 = new Point (D1pos[0], D1pos[1], D1pos[2]);

        Geometry.Vector frontNormal = new Geometry.Vector(0f, 0f, 1f);
        Geometry.Vector topNormal = new Geometry.Vector(0f, 1f, 0f);
        Geometry.Vector rightNormal = new Geometry.Vector(1f, 0f, 0f);
        Geometry.Vector backNormal = new Geometry.Vector(0f, 0f, -1f);
        Geometry.Vector bottomNormal = new Geometry.Vector(0f, -1f, 0f);
        Geometry.Vector leftNormal = new Geometry.Vector(-1f, 0f, 0f);

        Geometry.Parallelogram frontSide = new Geometry.Parallelogram(A, B, C, frontNormal, "frontSide");
        Geometry.Parallelogram topSide = new Geometry.Parallelogram(A, B, D, topNormal,"topSide");
        Geometry.Parallelogram rightSide = new Geometry.Parallelogram(A, D, C, rightNormal,"Right side");
        Geometry.Parallelogram backSide = new Geometry.Parallelogram(A1, B1, C1, backNormal,"Back side");
        Geometry.Parallelogram bottomSide = new Geometry.Parallelogram(A1, B1, D1, bottomNormal,"Bottom side");
        Geometry.Parallelogram leftSide = new Geometry.Parallelogram(A1, D1, C1, leftNormal,"Left side");

        ArrayList<Geometry.Parallelogram> sides = new ArrayList<>();
        sides.add(frontSide);
        sides.add(topSide);
        sides.add(rightSide);
        sides.add(backSide);
        sides.add(bottomSide);
        sides.add(leftSide);

        Iterator<Geometry.Parallelogram> iterator = sides.iterator();

        while (iterator.hasNext()){

            Geometry.Parallelogram side = iterator.next();
            if(!side.pointInside(touch)){
                iterator.remove();
            }

        }

        if (sides.size() == 0) return null;

        float closestSpot = -1000f;
        Geometry.Parallelogram touchedSide = sides.get(0);

        for (Geometry.Parallelogram side: sides){

            if (side.getClosestZPositionToScreen() > closestSpot){

                closestSpot = side.getClosestZPositionToScreen();
                touchedSide = side;

            }

        }

        Log.d("Touched side : ", touchedSide.name);

        return center.clone().translate(touchedSide.normal);
        //return center.clone();


    }

    public static Geometry.Ray convertNormalized2DPointToRay(float normalizedX, float normalizedY, float[] invertedViewProjectionMatrix){


        final float[] nearPointNdc = {normalizedX, normalizedY, -1, 1};
        final float[] farPointNdc = {normalizedX, normalizedY, 1, 1};
        final float[] nearPointWorld = new float[4];
        final float[] farPointWorld = new float[4];
        multiplyMV(
                nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0);
        multiplyMV(
                farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0);

        divideByW(nearPointWorld);
        divideByW(farPointWorld);

        Point nearPointRay =
                new Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
        Point farPointRay =
                new Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);

        return new Geometry.Ray(nearPointRay,
                Geometry.vectorBetween(nearPointRay, farPointRay));


    }

    private static void divideByW(float[] vector) {


        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
    }


}
