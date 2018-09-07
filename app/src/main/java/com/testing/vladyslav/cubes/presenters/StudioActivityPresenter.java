package com.testing.vladyslav.cubes.presenters;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.testing.vladyslav.cubes.CubeRenderer;
import com.testing.vladyslav.cubes.activities.StudioActivity;
import com.testing.vladyslav.cubes.adapters.StudioRecyclerAdapter;
import com.testing.vladyslav.cubes.data.CubeDataHolder;
import com.testing.vladyslav.cubes.database.UserModelsDBLoader;
import com.testing.vladyslav.cubes.database.entities.UserModel;
import com.testing.vladyslav.cubes.util.ImageLoader;
import com.testing.vladyslav.cubes.util.ImagesHelper;
import com.testing.vladyslav.cubes.util.ObjectSelectHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import static android.opengl.GLES20.glReadPixels;

public class StudioActivityPresenter {

    private UserModelsDBLoader model;
    private StudioActivityView studioActivityView;
    private StudioFragmentView studioFragmentView;
    private EditorFragmentView editorFragmentView;

    private ArrayList<UserModel> usersModelsList;
    private UserModel currentUserModel;

    private StudioRecyclerAdapter modelListAdapter;

    private volatile boolean imageSaved = false;


    public interface StudioActivityView {

        Context getContext();
        void loadFragment(int fragmentId);
        void startAnotherActivity(Class<?> cls);
        void showToast(String text);
        void showProgressBar();
        void hideProgressBar();

    }

    public interface StudioFragmentView{

        void attachModelListAdapter(StudioRecyclerAdapter adapter);
        void setNewRecyclerViewData(ArrayList<UserModel> models);

    }

    public interface EditorFragmentView{

        CubeRenderer getRenderer();
        void setModelToOpen(UserModel model);
        void openDialogBox();

    }

    public void setUserModelsList(ArrayList<UserModel> models){
        this.usersModelsList = models;
    }


    //here communicate with studioActivityView
    public void attachViews(StudioActivityView activityView, EditorFragmentView editorFragmentView, StudioFragmentView studioFragmentView){

        this.studioActivityView = activityView;
        this.studioFragmentView = studioFragmentView;
        this.editorFragmentView = editorFragmentView;

        activityView.loadFragment(StudioActivity.STUDIOFRAGMENTID);

    }

    //here do all the preparation work
    public StudioActivityPresenter(UserModelsDBLoader model){

        this.model = model;
        model.loadUserModels(new UserModelsDBLoader.LoadUserModelsCallback() {
            @Override
            public void onUserModelsLoad(ArrayList<UserModel> models) {

                usersModelsList = models;
                modelListAdapter = new StudioRecyclerAdapter(usersModelsList);
                modelListAdapter.setListener(new StudioRecyclerAdapter.StudioRecyclerListener() {
                    @Override
                    public void modelSelected(UserModel model) {

                        openUserModel(model);
                    }

                    @Override
                    public Context getContext() {
                        return studioActivityView.getContext();
                    }

                    @Override
                    public void onModelDelete(UserModel model) {
                        deleteUserModel(model);
                    }

                });


                studioFragmentView.attachModelListAdapter(modelListAdapter);

            }
        });



    }

    public void saveUserModel(String name){

        UserModel renderingModel = editorFragmentView.getRenderer().getRenderingModel();
        renderingModel.setName(name);

        ArrayList<UserModel> models = new ArrayList<>();
        models.add(renderingModel);

        model.insertUserModels(models, new UserModelsDBLoader.InsertUserModelsCallback() {
            @Override
            public void onUserModelsInsert() {

                Toast.makeText(studioActivityView.getContext(), "Models saved", Toast.LENGTH_LONG).show();

            }
        });

    }

    private void openUserModel(UserModel model){

        editorFragmentView.setModelToOpen(model);
        studioActivityView.loadFragment(StudioActivity.EDITORFRAGMENTID);
        currentUserModel = model;

    }

    public void createNewModelClicked(){

        currentUserModel = null;
        openUserModel(null);

    }

    public void openClicked(){

        studioActivityView.loadFragment(StudioActivity.STUDIOFRAGMENTID);
        model.loadUserModels(new UserModelsDBLoader.LoadUserModelsCallback() {
            @Override
            public void onUserModelsLoad(ArrayList<UserModel> models) {

                usersModelsList = models;
                modelListAdapter.setModels(models);
                studioFragmentView.attachModelListAdapter(modelListAdapter);

            }
        });

    }

    public void deleteUserModel(UserModel userModel){
        model.deleteUserModel(userModel, new UserModelsDBLoader.DeleteUserModelCallback() {
            @Override
            public void onUserModelsDeleted() {
                model.loadUserModels(new UserModelsDBLoader.LoadUserModelsCallback() {
                    @Override
                    public void onUserModelsLoad(ArrayList<UserModel> models) {

                        usersModelsList = models;
                        studioFragmentView.setNewRecyclerViewData (models);

                    }
                });
            }
        });
    }

    public void saveClicked(){

        studioActivityView.showProgressBar();

        if (currentUserModel != null && figureExists(currentUserModel.getName())){

            ImageLoader.getInstance().setClearImageCache(true);
            UserModel renderingModel = editorFragmentView.getRenderer().getRenderingModel();
            renderingModel.setName(currentUserModel.getName());
            renderingModel.setId(currentUserModel.getId());
            model.updateUserModel(renderingModel, new UserModelsDBLoader.UpdateModelCallback() {
                @Override
                public void onUserModelUpdated() {
                    studioActivityView.showToast("Saved");
                }
            });
            saveImage(currentUserModel.getName());

        }else{
            editorFragmentView.openDialogBox();
        }
    }

    public void saveAsClicked(final String name){

        final String imageName = name;

        if(figureExists(name)) studioActivityView.showToast("This name already exists");

        studioActivityView.showProgressBar();

        saveUserModel(name);
        saveImage(name);

    }

    private boolean figureExists(String name){

        for (UserModel model: usersModelsList){
            if(model.getName().equals(name)){
                return true;
            }
        }
        return false;

    }

    private void saveImage(String name){

        final String imageName = name;

        new ImageSaveWaitTask(imageName).execute(null, null, null);


    }

    class ImageSaveWaitTask extends AsyncTask<Void, Void, Void>{

        private String imageName;

        public ImageSaveWaitTask(String name){
            this.imageName = name;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            final Object lock = new Object();

            editorFragmentView.getRenderer().makeScreenshot(new CubeRenderer.ScreenshotHandler() {
                @Override
                public void makeScreenshot(Bitmap bitmap) {

                    ImagesHelper.saveImage(bitmap, imageName, studioActivityView.getContext(), new ImagesHelper.SaveImageCallback() {
                        @Override
                        public void onImageSaved() {

                            synchronized (lock){
                                setImageSaved();
                                lock.notify();
                            }

                        }
                    });

                }
            });


            synchronized (lock){
                while (!imageSaved){

                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                imageSaved = false;
            }



            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            studioActivityView.hideProgressBar();
        }
    }

    private synchronized void setImageSaved(){
        imageSaved = true;
    }







}
