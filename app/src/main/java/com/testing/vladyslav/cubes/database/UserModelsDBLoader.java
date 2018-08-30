package com.testing.vladyslav.cubes.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.util.Log;

import com.huma.room_for_asset.RoomAsset;
import com.testing.vladyslav.cubes.database.entities.UserModel;

import java.util.ArrayList;
import java.util.List;

public class UserModelsDBLoader {


    private static UserModelsDBLoader instance;
    private UserFiguresDatabase mainDatabase;

    private UserModelsDBLoader(){}

    public static UserModelsDBLoader getInstance(Context context) {
        if (instance == null) {

            instance = new UserModelsDBLoader();

            final Migration MIGRATION = new Migration(0, 1) {
                @Override
                public void migrate(SupportSQLiteDatabase database) {

                }
            };

            instance.mainDatabase = RoomAsset
                    .databaseBuilder(context, UserFiguresDatabase.class, "user_models.sqlite").addMigrations(MIGRATION)
                    .build();


            Log.d("UserModelsDBLoader", "UserModels Database created");
        }

        return instance;
    }


    public ArrayList<UserModel> loadUserModels() {

        return (ArrayList<UserModel>) mainDatabase.daoAccess().loadAllUserModels();

    }

    public void insertUserModels(ArrayList<UserModel> models) {

        mainDatabase.daoAccess().insertUserModels(models);

    }


}
