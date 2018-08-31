package com.testing.vladyslav.cubes.presenters;

import android.content.Context;
import android.widget.Toast;

import com.testing.vladyslav.cubes.database.UserModelsDBLoader;
import com.testing.vladyslav.cubes.database.entities.UserModel;

import java.util.ArrayList;

public class EditorActivityPresenter {


    private UserModelsDBLoader model;
    private EditorActivityView view;

    public interface EditorActivityView{

        Context getContext();

    }

    public void attachView(EditorActivityView view){

        this.view = view;

    }

    public EditorActivityPresenter(UserModelsDBLoader model){

        this.model = model;

    }

    public void saveUserModel(String name){

        UserModel userModel;
        userModel = new UserModel();

        userModel.setName(name);
        userModel.setCubeNumber(5);
        userModel.setSizeX(3);
        userModel.setSizeY(3);
        userModel.setSizeZ(3);
        userModel.setCubes("A hell lot of cubes");

        ArrayList<UserModel> models = new ArrayList<>();
        models.add(userModel);

        model.insertUserModels(models, new UserModelsDBLoader.InsertUserModelsCallback() {
            @Override
            public void onUserModelsInsert() {

                Toast.makeText(view.getContext(), "Models saved", Toast.LENGTH_LONG).show();

            }
        });

    }

    public void openUserModel(){

        ArrayList<UserModel> models;

        model.loadUserModels(new UserModelsDBLoader.LoadUserModelsCallback() {
            @Override
            public void onUserModelsLoad(ArrayList<UserModel> models) {

            }
        });

    }





}
