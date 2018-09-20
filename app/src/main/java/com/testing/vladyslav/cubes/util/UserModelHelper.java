package com.testing.vladyslav.cubes.util;

import com.testing.vladyslav.cubes.objects.Cube;
import com.testing.vladyslav.cubes.objects.PixioPoint;

import java.util.ArrayList;

public class UserModelHelper {

    private static String splitter = "," ;
    private static String devider = ":";

    public static String getStringModelForm(ArrayList<Cube> cubeList){

        StringBuilder result = new StringBuilder();

        for (Cube cube: cubeList){

            result.append(String.valueOf(cube.center.x)).append(devider).
                    append(String.valueOf(cube.center.y)).append(devider).
                    append(String.valueOf(cube.center.z)).append(devider).
                    append(cube.color.hexColor) ;

            result.append(cubeList.indexOf(cube) == cubeList.size()-1? "" : splitter);

        }

        return result.toString();

    }

    public static ArrayList<Cube> getCubesModelForm(String cubes){

        ArrayList<Cube> result = new ArrayList<>();

        String[] stringCubesArray = cubes.split(splitter);

        for (String string: stringCubesArray){

            String [] valuesArray = string.split(devider);

            Cube cube = new Cube(
                    new PixioPoint(
                            Float.valueOf(valuesArray[0]),
                            Float.valueOf(valuesArray[1]),
                            Float.valueOf(valuesArray[2])),
                    new PixioColor(valuesArray[3]),
                    false);

            result.add(cube);

        }

        return result;

    }

}
