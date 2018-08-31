package com.testing.vladyslav.cubes.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.huma.room_for_asset.RoomAsset;
import com.testing.vladyslav.cubes.database.entities.UserModel;

import java.util.ArrayList;
import java.util.List;

public class UserModelsDBLoader {


    private static UserFiguresDatabase mainDatabase;

    public UserModelsDBLoader(Context context){

        final Migration MIGRATION = new Migration(0, 1) {
            @Override
            public void migrate(SupportSQLiteDatabase database) {

            }
        };

        mainDatabase = RoomAsset
                .databaseBuilder(context, UserFiguresDatabase.class, "user_models.sqlite").addMigrations(MIGRATION)
                .build();


    }


    public void loadUserModels(LoadUserModelsCallback callback) {

        new UserModelsLoadAsyncTask(callback).execute(null, null, null);

    }

    public void insertUserModels(ArrayList<UserModel> models, InsertUserModelsCallback callback) {

        new UserModelsInsertTask(models, callback).execute(null, null, null);

    }

    //callbacks
    public interface LoadUserModelsCallback{

        void onUserModelsLoad(ArrayList<UserModel> models);

    }

    public interface InsertUserModelsCallback{

        void onUserModelsInsert();

    }

    //async tasks
    private static class UserModelsLoadAsyncTask extends AsyncTask<Void, Void, Void>{

        private ArrayList<UserModel> modelList;
        private final LoadUserModelsCallback callback;

        public UserModelsLoadAsyncTask(LoadUserModelsCallback callback){
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            modelList = (ArrayList<UserModel>) mainDatabase.daoAccess().loadAllUserModels();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (callback!= null){
                callback.onUserModelsLoad(modelList);
            }
        }
    }

    private static class UserModelsInsertTask extends AsyncTask<Void, Void, Void>{

        private ArrayList<UserModel> modelList;
        private final InsertUserModelsCallback callback;

        public UserModelsInsertTask(ArrayList<UserModel> modelList ,InsertUserModelsCallback callback){
            this.callback = callback;
            this.modelList = modelList;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            mainDatabase.daoAccess().insertUserModels(modelList);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (callback!= null){
                callback.onUserModelsInsert();
            }
        }
    }




}
