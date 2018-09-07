package com.testing.vladyslav.cubes.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.testing.vladyslav.cubes.database.entities.UserModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface IDataUserFiguresAccessObject {

    @Query("SELECT * FROM MODELS")
    List<UserModel> loadAllUserModels();

    @Insert
    void insertUserModels(ArrayList<UserModel> modelsList);


    @Query("DELETE FROM MODELS WHERE MODELS.id = :modelId")
    void deleteUserModel(int modelId);

    @Update(onConflict = REPLACE)
    void updateUserModel(UserModel model);



//    @Query("SELECT * FROM PixioSet")
//    public List<PixioSet> loadAllSets();
//
//    @Query("SELECT * FROM PixioFigure")
//    public List<PixioFigure> loadAllFigures();
//
//    @Query("SELECT * FROM PixioFigure WHERE PixioFigure.categoryId = :id")
//    public List<PixioFigure> loadFiguresFromCategories(String id);
//
//    @Query("SELECT * FROM PixioFigure WHERE PixioFigure.setId = :id")
//    public List<PixioFigure> loadFiguresFromSets(String id);
//
//    @Query("SELECT * FROM PixioFigure WHERE PixioFigure.id IN (:favorites)")
//    public List<PixioFigure> loadFavorites(Set<String> favorites);
//
//    @Query("SELECT * FROM PixioVersion")
//    public List<PixioVersion> loadVersion();
//
//    @Delete
//    void deleteversion(PixioVersion version);
//
//    @Insert
//    void insertVersion(PixioVersion version);
//
//    @Query("DELETE FROM PixioCategory")
//    public void clearCategories();
//
//    @Query("DELETE FROM PixioSet")
//    public void clearSets();
//
//    @Query("DELETE FROM PixioFigure")
//    public void clearFigures();
//
//    @Query("DELETE FROM PixioVersion")
//    public void clearVersion();
//
//    @Insert
//    void insertCategories(ArrayList<PixioCategory> categories);
//
//    @Insert
//    void insertFigures(ArrayList<PixioFigure> figures);
//
//    @Insert
//    void insertSets(ArrayList<PixioSet> sets);
//
//
//
//    @Insert
//    public void insertOnlySingleRecord(PixioCategory pixioCategory);

}
