package com.testing.vladyslav.cubes.util;

public class PixioColor {

    public float RED = 0f;
    public float GREEN = 0f;
    public float BLUE = 0f;
    public String hexColor;

    public PixioColor(String hexColor){

        this.hexColor = hexColor;
        this.RED = (float)Integer.valueOf( hexColor.substring( 1, 3 ), 16 )/255;
        this.GREEN = (float)Integer.valueOf( hexColor.substring( 3, 5 ), 16 )/255;
        this.BLUE = (float)Integer.valueOf( hexColor.substring( 5, 7 ), 16 )/255;

    }

    public PixioColor(int r, int g, int b) {

        this.RED = (float)r / 255f;
        this.GREEN = (float)g /255f;
        this.BLUE = (float)b / 255f;

    }

}
