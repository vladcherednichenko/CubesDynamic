package com.testing.vladyslav.cubes;

public class Settings {


    public static float cubeSize = 1f;

    //graphics settings
    public enum quality{LOW, MEDIUM, ULTRA};
    public static int LOW = 0;
    public static int MEDIUM = 1;
    public static int ULTRA = 2;
    public static int graphicsQuality = 1;
    public static boolean dynamicShadows = false;
    public static boolean antialiasing = false;
    public static float lightDistance = 100.0f;

    //graphics editor
    public static boolean debugTextView = false;
    public static String editorBackGroundColor = "#eeeeef";
    //grid
    public static String gridColor = "#828282";
    public static boolean unlimitedGrid = false;
    public static int unlimitedGridSize = 1000;
    public static float gridHeight = -1.5f;
    public static int minimumGridSize = 10;
    public static int maximumGridSize = 24;
    public static int maximumModelSize = 24;
    //figure
    public static float minimumFigureScale = 0.1f;
    public static float maximumFigureScale = 6.0f;



    public static void setDevelopersOptionsEnabled(Boolean b){

        unlimitedGrid = b;

    }


}
