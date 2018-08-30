package com.testing.vladyslav.cubes.database.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@Entity(tableName = "UserModel")
public class UserModel {

    @PrimaryKey(autoGenerate = true)
    @Nullable
    @ColumnInfo(name = "id")
    private Integer id;

    @Nullable
    @ColumnInfo(name = "name")
    private String name;

    @Nullable
    @ColumnInfo(name = "cube_number")
    private Integer cubeNumber;

    @Nullable
    @ColumnInfo(name = "cubes")
    private String cubes;

    @Nullable
    @ColumnInfo(name = "sizeX")
    private Integer sizeX;

    @Nullable
    @ColumnInfo(name = "sizeY")
    private Integer sizeY;

    @Nullable
    @ColumnInfo(name = "sizeZ")
    private Integer sizeZ;


    @Nullable
    public Integer getId() {
        return id;
    }

    @Nullable
    public String getName() {
        return name;
    }

    @Nullable
    public Integer getCubeNumber() {
        return cubeNumber;
    }

    @Nullable
    public String getCubes() {
        return cubes;
    }

    @Nullable
    public Integer getSizeX() {
        return sizeX;
    }

    @Nullable
    public Integer getSizeY() {
        return sizeY;
    }

    @Nullable
    public Integer getSizeZ() {
        return sizeZ;
    }

    public void setId(@Nullable Integer id) {
        this.id = id;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    public void setCubeNumber(@Nullable Integer cubeNumber) {
        this.cubeNumber = cubeNumber;
    }

    public void setCubes(@Nullable String cubes) {
        this.cubes = cubes;
    }

    public void setSizeX(@Nullable Integer sizeX) {
        this.sizeX = sizeX;
    }

    public void setSizeY(@Nullable Integer sizeY) {
        this.sizeY = sizeY;
    }

    public void setSizeZ(@Nullable Integer sizeZ) {
        this.sizeZ = sizeZ;
    }
}