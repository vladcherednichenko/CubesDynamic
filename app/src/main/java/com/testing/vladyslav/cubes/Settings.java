package com.testing.vladyslav.cubes;

public class Settings {


    //graphics editor
    public static boolean unlimitedGrid = false;
    public static boolean customChangeViewButtonEnable = false;

    public static int unlimitedGridSize = 1000;
    public static int minimumGridSize = 10;
    public static int maximumGridSize = 24;
    public static int maximumModelSize = 24;


    public static void setDevelopersOptionsEnabled(Boolean b){

        unlimitedGrid = b;

    }


}
