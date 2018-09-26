package com.testing.vladyslav.cubes;

import android.support.annotation.NonNull;


public class PixioCube implements Comparable<PixioCube>{
    public PixioCube(String filename, int amount, int color){
        this.filename = filename;
        this.amount = amount;
        this.color = color;
    }

    public PixioCube(int color, int amount){
        this.color = color;
        this.filename = null;
        this.amount = amount;
    }

    public String filename;
    public int amount;
    public int color;


    @Override
    public int compareTo(@NonNull PixioCube o) {
        int compareOrder=((PixioCube)o).amount;
        return compareOrder - this.amount;
    }
}