package com.testing.vladyslav.cubes.util;

import java.util.HashMap;

public class PixioHelper {

    public static final HashMap<Integer, String> colorCodeToHex = new HashMap<Integer, String>(){
        {
            put(248, "#E4002B"); //red
            put(247, "#FF8200"); //orange
            put(246, "#FEDB00"); //yellow
            put(245, "#62b013"); //light green
            put(244, "#00843D"); //green
            put(243, "#00b8b8"); //turquoise
            put(242, "#41B6E6"); //light blue
            put(241, "#003087"); //blue
            put(249, "#753BBD"); //violet
            put(250, "#F57EB6"); //pink
            put(251, "#FCD299"); //tan
            put(252, "#C88242"); //light brown
            put(253, "#693F23"); //brown
            put(255, "#A2AAAD"); //grey
            put(240, "#333333"); //black
            put(254, "#ffffff"); //white
            put(238, "#333333"); //black
        }};

    public static final HashMap<String, Integer> hexToColorCode = new HashMap<String, Integer>(){
        {
            put("#E4002B", 248); //red
            put("#FF8200", 247); //orange
            put("#FEDB00", 246); //yellow
            put("#62b013", 245); //light green
            put("#00843D", 244); //green
            put("#00b8b8", 243); //turquoise
            put("#41B6E6", 242); //light blue
            put("#003087", 241); //blue
            put("#753BBD", 249); //violet
            put("#F57EB6", 250); //pink
            put("#FCD299", 251); //tan
            put("#C88242", 252); //light brown
            put("#693F23", 253); //brown
            put("#A2AAAD", 255); //grey
            put("#333333", 240); //black
            put("#ffffff", 254); //white
        }};

}
