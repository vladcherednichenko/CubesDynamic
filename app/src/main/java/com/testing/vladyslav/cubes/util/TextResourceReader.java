package com.testing.vladyslav.cubes.util;

import android.content.Context;
import android.content.res.Resources;

import com.testing.vladyslav.cubes.data.Facet;
import com.testing.vladyslav.cubes.objects.PixioPoint;
import com.testing.vladyslav.cubes.util.Geometry.Vector;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class TextResourceReader {


    public static String readTextFileFromResource(Context context,
                                                  int resourceId) {
        StringBuilder body = new StringBuilder();
        try {
            InputStream inputStream =
                    context.getResources().openRawResource(resourceId);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String nextLine;
            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not open resource: " + resourceId, e);
        } catch (Resources.NotFoundException nfe) {
            throw new RuntimeException("Resource not found: " + resourceId, nfe);
        }
        return body.toString();
    }

    public static ArrayList<Facet> getFacetsFromModel(Context context, int resourceId) {
        ArrayList<Facet> facetList = new ArrayList<>();
        Facet facet = new Facet();

        try {
            InputStream inputStream =
                    context.getResources().openRawResource(resourceId);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String nextLine;
            while ((nextLine = bufferedReader.readLine()) != null) {

                if (nextLine.contains("normal")){

                    facet = new Facet();
                    String coords[] = nextLine.substring(nextLine.indexOf("normal")).split("\\s+");
                    float x = Float.parseFloat(coords[1]);
                    float y = Float.parseFloat(coords[2]);
                    float z = Float.parseFloat(coords[3]);

                    facet.normal = new Vector(x, y, z);

                }else if(nextLine.contains("vertex")){
                    String coords[] = nextLine.substring(nextLine.indexOf("vertex")).split("\\s+");
                    float x = Float.parseFloat(coords[1]);
                    float y = Float.parseFloat(coords[2]);
                    float z = Float.parseFloat(coords[3]);

                    if (facet.A == null){
                        facet.A = new PixioPoint(x, y, z);
                    }else if(facet.B == null){
                        facet.B = new PixioPoint(x, y, z);
                    }else if(facet.C == null){
                        facet.C = new PixioPoint(x, y, z);
                        facetList.add(facet);
                    }

                }

            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not open resource: " + resourceId, e);
        } catch (Resources.NotFoundException nfe) {
            throw new RuntimeException("Resource not found: " + resourceId, nfe);
        }
        return facetList;
    }
    public static ArrayList<Facet> getFacetsFromFileObject(Context context, String file) {

        ArrayList<Facet> facetList = new ArrayList<>();
        ArrayList<String> verticesList = new ArrayList<>();
        ArrayList<String> facesList = new ArrayList<>();

        // Open the OBJ file with p Scanner
        Scanner scanner = null;
        try {
            scanner = new Scanner(context.getAssets().open(file));
            // Loop through all its lines
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(line.startsWith("v ")) {
                    // Add vertex line to list of vertices
                    verticesList.add(line);
                } else if(line.startsWith("f ")) {
                    // Add face line to faces list
                    facesList.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String face: facesList){

            String []pointList = face.split(" ");
            int verticeAPosition = Short.parseShort(pointList[1])-1;
            int verticeBPosition = Short.parseShort(pointList[2])-1;
            int verticeCPosition = Short.parseShort(pointList[3])-1;


            String []verticeAList = verticesList.get(verticeAPosition).split(" ");
            String []verticeBList = verticesList.get(verticeBPosition).split(" ");
            String []verticeCList = verticesList.get(verticeCPosition).split(" ");

            PixioPoint A = new PixioPoint(
                    Float.parseFloat(verticeAList[1]),
                    Float.parseFloat(verticeAList[2]),
                    Float.parseFloat(verticeAList[3])
            );

            PixioPoint B = new PixioPoint(
                    Float.parseFloat(verticeBList[1]),
                    Float.parseFloat(verticeBList[2]),
                    Float.parseFloat(verticeBList[3])
            );

            PixioPoint C = new PixioPoint(
                    Float.parseFloat(verticeCList[1]),
                    Float.parseFloat(verticeCList[2]),
                    Float.parseFloat(verticeCList[3])
            );

            Facet facet = new Facet(calcNormals(A, B, C), A, B, C);
            facetList.add(facet);

        }

        // Close the scanner
        scanner.close();
        return facetList;

    }

    static Geometry.Vector calcNormals(PixioPoint a, PixioPoint b, PixioPoint c)
    {

        Geometry.Vector result = new Vector(0, 0, 0);
        double wrki;
        Vector v1 = new Vector(
                a.x - b.x,
                a.y - b.y,
                a.z - b.z
                );

        Vector v2 = new Vector(
                b.x - c.x,
                b.y - c.y,
                b.z - c.z
        );

        wrki = Math.sqrt(
                        Math.pow(v1.y*v2.z - v1.z * v2.y, 2) +
                        Math.pow(v1.z * v2.x - v1.x * v2.z ,2) +
                        Math.pow(v1.x * v2.y - v1.y * v2.x, 2));

        result.x = (float)((v1.y * v2.z - v1.z * v2.y) / wrki);
        result.y = (float)((v1.z * v2.x - v1.x * v2.z) / wrki);
        result.z = (float)((v1.x * v2.y - v1.y * v2.x) / wrki);

        return  result;
    }



}
