package com.testing.vladyslav.cubes.data;

import android.util.Log;

import com.testing.vladyslav.cubes.Facet;

import java.util.ArrayList;

public class CubeDataHolder {


    private static int POINT_COMPONENT_COUNT = 3;
    private static int FACET_COMPONENT_COUNT = 3;

    public int sizeInVertex;


    public ArrayList<Facet> facetListLow;
    public ArrayList<Facet> facetListMedium;
    public ArrayList<Facet> facetListHigh;

    private float[] vertices;
    private float[] normals;

    private CubeDataHolder(){}

    private ArrayList<Facet> facetList;

    private static CubeDataHolder instance;

    public static CubeDataHolder getInstance(){

        if (instance == null){
            instance = new CubeDataHolder();


        }

        return instance;

    }

    public void setFacetList(ArrayList<Facet> facetList){


        this.facetList = facetList;
        vertices = new float[facetList.size() * FACET_COMPONENT_COUNT * POINT_COMPONENT_COUNT];
        normals = new float[facetList.size() * FACET_COMPONENT_COUNT * POINT_COMPONENT_COUNT];

        int verticesPointer = 0;
        int normalsPointer = 0;

        for (Facet facet: facetList){

            normals[normalsPointer++] = facet.normal.x;
            normals[normalsPointer++] = facet.normal.y;
            normals[normalsPointer++] = facet.normal.z;

            normals[normalsPointer++] = facet.normal.x;
            normals[normalsPointer++] = facet.normal.y;
            normals[normalsPointer++] = facet.normal.z;

            normals[normalsPointer++] = facet.normal.x;
            normals[normalsPointer++] = facet.normal.y;
            normals[normalsPointer++] = facet.normal.z;

            vertices[verticesPointer++] = facet.A.x;
            vertices[verticesPointer++] = facet.A.y;
            vertices[verticesPointer++] = facet.A.z;

            vertices[verticesPointer++] = facet.B.x;
            vertices[verticesPointer++] = facet.B.y;
            vertices[verticesPointer++] = facet.B.z;

            vertices[verticesPointer++] = facet.C.x;
            vertices[verticesPointer++] = facet.C.y;
            vertices[verticesPointer++] = facet.C.z;


        }

//        for (Float f: vertices){
//            if ((f < 0 && f< -0.5f )|| (f>0 && f>0.5f)){
//                Log.w("CubeDataHolder", String.valueOf(f));
//            }
//        }

        sizeInVertex = vertices.length / 3;




    }

    public ArrayList<Facet> getFacetList() {
        return facetList;
    }

    public float[] getVertices(){
        return vertices;
    }
    public float[] getNormals(){
        return normals;
    }

}