package com.testing.vladyslav.cubes.util;

import com.testing.vladyslav.cubes.PixioCube;
import com.testing.vladyslav.cubes.database.entities.UserModel;
import com.testing.vladyslav.cubes.objects.Cube;

import java.util.ArrayList;
import java.util.HashMap;

public class PixioHelper {

    public static int [] allColors = {248, 247, 246, 245, 244, 243, 242, 241, 249, 250, 251, 252, 253, 255, 240, 254, 238};

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


    public static final HashMap<Integer, String> colorCodeToFileName = new HashMap<Integer, String>(){{
        put(248,"pixio_color_cube_11");
        put(247,"pixio_color_cube_12");
        put(246,"pixio_color_cube_13");
        put(245,"pixio_color_cube_14");
        put(244,"pixio_color_cube_15");
        put(243,"pixio_color_cube_16");
        put(242,"pixio_color_cube_07");
        put(241,"pixio_color_cube_08");
        put(249,"pixio_color_cube_09");
        put(250,"pixio_color_cube_10");
        put(251,"pixio_color_cube_04");
        put(252,"pixio_color_cube_05");
        put(253,"pixio_color_cube_06");
        put(255,"pixio_color_cube_02");
        put(240,"pixio_color_cube_01");
        put(254,"pixio_color_cube_03");
        put(238,"pixio_color_cube_01");

    }};

    public static ArrayList<PixioCube> figureToCubeList(ArrayList<Cube> cubes){

        HashMap<Integer, Integer> colorAmount = new HashMap<Integer, Integer>(){
            {
                put(248, 0); //red
                put(247, 0); //orange
                put(246, 0); //yellow
                put(245, 0); //light green
                put(244, 0); //green
                put(243, 0); //turquoise
                put(242, 0); //light blue
                put(241, 0); //blue
                put(249, 0); //violet
                put(250, 0); //pink
                put(251, 0); //tan
                put(252, 0); //light brown
                put(253, 0); //brown
                put(255, 0); //grey
                put(240, 0); //black
                put(254, 0); //white
                put(238, 0); //black
            }};


        ArrayList<PixioCube> result = new ArrayList<>();

        for(Cube cube: cubes){
            colorAmount.put(hexToColorCode.get(cube.color.hexColor), colorAmount.get(hexToColorCode.get(cube.color.hexColor))+1);
        }

        for (int i = 0; i< allColors.length; i++){
            if(colorAmount.get(allColors[i]) != 0){
                result.add(new PixioCube(colorCodeToFileName.get(allColors[i]), colorAmount.get(allColors[i]), allColors[i]));
            }
        }


        return result;

    }

}
