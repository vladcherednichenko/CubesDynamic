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

        final Migration MIGRATION = new Migration(1, 2) {
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

    public void deleteUserModel(UserModel model, DeleteUserModelCallback callback) {

        ArrayList<UserModel> models = new ArrayList<>();
        models.add(model);
        new UserModelsDeleteTask(models, callback).execute(null, null, null);

    }

    public void updateUserModel(UserModel userModel, UpdateModelCallback callback){

        new UserModelsUpdateTask(userModel, callback).execute(null, null, null);

    }



    //callbacks
    public interface LoadUserModelsCallback{

        void onUserModelsLoad(ArrayList<UserModel> models);

    }

    public interface InsertUserModelsCallback{

        void onUserModelsInsert();

    }

    public interface DeleteUserModelCallback{

        void onUserModelsDeleted();

    }

    public interface UpdateModelCallback{

        void onUserModelUpdated();

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

    private static class UserModelsDeleteTask extends AsyncTask<Void, Void, Void>{

        private ArrayList<UserModel> modelList;
        private final DeleteUserModelCallback callback;

        public UserModelsDeleteTask(ArrayList<UserModel> modelList ,DeleteUserModelCallback callback){
            this.callback = callback;
            this.modelList = modelList;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            for (UserModel model: modelList){
                mainDatabase.daoAccess().deleteUserModel(model.getId());

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (callback!= null){
                callback.onUserModelsDeleted();
            }
        }
    }

    private static class UserModelsUpdateTask extends AsyncTask<Void, Void, Void>{

        private UserModel userModel;
        private final UpdateModelCallback callback;

        public UserModelsUpdateTask(UserModel userModel ,UpdateModelCallback callback){
            this.callback = callback;
            this.userModel = userModel;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            mainDatabase.daoAccess().updateUserModel(userModel);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (callback!= null){
                callback.onUserModelUpdated();
            }
        }
    }




}
