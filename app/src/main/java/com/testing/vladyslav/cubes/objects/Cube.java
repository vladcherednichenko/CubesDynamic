package com.testing.vladyslav.cubes.objects;

import com.testing.vladyslav.cubes.data.CubeDataHolder;
import com.testing.vladyslav.cubes.util.Color;

public class Cube {

    private static final String TAG = "Cube";

    private float cubeSize = 1;
    private int POSITION_COMPONENT_COUNT = 3;
    private int COLOR_COORDINATES_COMPONENT_COUNT = 4;
    private int STRIDE = 0;

    public final Point center;

    public float[] cubePositionData;
    public float[] cubeColorData;
    public float[] cubeNormalData;



    public Cube(Point center, Color color){

        this.center = center;

        cubePositionData = CubeDataHolder.getInstance().getVertices().clone();
        cubeNormalData = CubeDataHolder.getInstance().getNormals();
        cubeColorData = new float[cubeNormalData.length + cubeNormalData.length/3];
        for (int i = 0; i< cubeColorData.length; i++){
            switch(i%4){
                case 0:{
                    cubeColorData[i] = color.RED;
                    break;
                }
                case 1:{
                    cubeColorData[i] = color.GREEN;
                    break;
                }
                case 2:{
                    cubeColorData[i] = color.BLUE;
                    break;
                }
                case 3:{
                    cubeColorData[i] = 1f;
                    break;
                }
            }
        }

        translateCube(center);

    }



    public void translateCube(Point vector){
        for (int i = 0; i< cubePositionData.length; i++){

            switch(i % POSITION_COMPONENT_COUNT) {
                case 0: {
                    cubePositionData[i] += vector.x;
                    break;
                }
                case 1: {
                    cubePositionData[i] += vector.y;
                    break;
                }
                case 2: {
                    cubePositionData[i] += vector.z;
                    break;
                }
            }

        }
    }

}
