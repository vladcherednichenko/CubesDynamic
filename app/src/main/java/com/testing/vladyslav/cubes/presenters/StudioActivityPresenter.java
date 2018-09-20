package com.testing.vladyslav.cubes.presenters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.Toast;

import com.testing.vladyslav.cubes.CubeRenderer;
import com.testing.vladyslav.cubes.activities.StudioActivity;
import com.testing.vladyslav.cubes.adapters.StudioRecyclerAdapter;
import com.testing.vladyslav.cubes.database.UserModelsDBLoader;
import com.testing.vladyslav.cubes.database.entities.UserModel;
import com.testing.vladyslav.cubes.fragments.EditorFragment;
import com.testing.vladyslav.cubes.objects.userActionsManagement.FigureChangesManager;
import com.testing.vladyslav.cubes.util.ImageLoader;
import com.testing.vladyslav.cubes.util.ImagesHelper;

import java.util.ArrayList;

public class StudioActivityPresenter {

    private UserModelsDBLoader model;
    private StudioActivityView studioActivityView;
    private StudioFragmentView studioFragmentView;
    private EditorFragmentView editorFragmentView;

    private ArrayList<UserModel> usersModelsList;
    private UserModel currentUserModel;
    private final Object changesCommitLock;
    private final Object changesCommitDoneLock;

    private StudioRecyclerAdapter modelListAdapter;
    private ChangeCommitWaitingTask changesCommitWaitTask;

    private volatile boolean imageSaved = false;
    private volatile boolean changeCommit = false;


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
        void setBackwardButtonVisible(Boolean visible);
        void setForwardButtonVisible(Boolean visible);
        void setOnSurfaceViewCreatedListener(EditorFragment.OnSurfaceViewCreatedListener listener);

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

        changesCommitLock = new Object();
        changesCommitDoneLock = new Object();
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

    private void saveUserModel(String name){

        studioActivityView.showProgressBar();

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
        editorFragmentView.setOnSurfaceViewCreatedListener(new EditorFragment.OnSurfaceViewCreatedListener() {
            @Override
            public void onSurfaceViewCreated() {
                editorFragmentView.getRenderer().setChangesRequestListener(new CubeRenderer.ChangesRequestedListener() {
                    @Override
                    public void onActionRequested() {



                        clearChangeCommitThreads();
                        changesCommitWaitTask = new ChangeCommitWaitingTask();
                        changesCommitWaitTask.execute(null, null, null);


                    }
                });
                editorFragmentView.getRenderer().getFigureChangeManager().setChangeCommitListener(new FigureChangesManager.ChangeCommitListener() {
                    @Override
                    public void onChangeCommit() {
                        synchronized (changesCommitLock){

                            figureChangeCommit();
                            changesCommitLock.notifyAll();

                        }

                    }
                });

                updateBackwardForwardButtons();
            }
        });

    }

    private void deleteUserModel(UserModel userModel){
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

    public void saveClicked(){

        if (currentUserModel != null && figureExists(currentUserModel.getName())){

            studioActivityView.showProgressBar();
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

        saveUserModel(name);
        saveImage(name);

    }

    public void openMenuClicked(){

        clearChangeCommitThreads();

    }

    public void viewModeClicked(){
        studioActivityView.showProgressBar();
        //new ChangeCommitWaitingTask().execute(null, null, null);
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

    private void updateBackwardForwardButtons(){

        int state = editorFragmentView.getRenderer().getFigureChangeManager().getState();

        switch(state){

            case FigureChangesManager.CAN_NOT_FORWARD_BACKWARD: {
                editorFragmentView.setForwardButtonVisible(false);
                editorFragmentView.setBackwardButtonVisible(false);
                break;
            }
            case FigureChangesManager.CAN_BACKWARD: {
                editorFragmentView.setForwardButtonVisible(false);
                editorFragmentView.setBackwardButtonVisible(true);
                break;
            }
            case FigureChangesManager.CAN_FORWARD: {
                editorFragmentView.setForwardButtonVisible(true);
                editorFragmentView.setBackwardButtonVisible(false);
                break;
            }
            case FigureChangesManager.CAN_FORWARD_BACKWARD: {
                editorFragmentView.setForwardButtonVisible(true);
                editorFragmentView.setBackwardButtonVisible(true);
                break;
            }


        }

    }


    class ChangeCommitWaitingTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {


            synchronized (changesCommitLock){

                changeCommit = false;
                while(!changeCommit){
                    try {
                        changesCommitLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                changeCommit = false;


            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            updateBackwardForwardButtons();

        }
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
                                lock.notifyAll();
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

    private synchronized void figureChangeCommit(){
        changeCommit = true;
    }

    private void clearChangeCommitThreads(){

        synchronized(changesCommitLock){

            figureChangeCommit();
            changesCommitLock.notifyAll();

        }

    }






}
