package com.testing.vladyslav.cubes.presenters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.Toast;

import com.testing.vladyslav.cubes.CubeRenderer;
import com.testing.vladyslav.cubes.PixioCube;
import com.testing.vladyslav.cubes.activities.StudioActivity;
import com.testing.vladyslav.cubes.adapters.StudioRecyclerAdapter;
import com.testing.vladyslav.cubes.data.CubeDataHolder;
import com.testing.vladyslav.cubes.database.UserModelsDBLoader;
import com.testing.vladyslav.cubes.database.entities.UserModel;
import com.testing.vladyslav.cubes.dialogs.AskToDeleteDialog;
import com.testing.vladyslav.cubes.dialogs.AskToSaveDialog;
import com.testing.vladyslav.cubes.dialogs.EnterFigureNameDialog;
import com.testing.vladyslav.cubes.dialogs.StudioContextDialog;
import com.testing.vladyslav.cubes.fragments.EditorFragment;
import com.testing.vladyslav.cubes.objects.userActionsManagement.FigureChangesManager;
import com.testing.vladyslav.cubes.util.ImageLoader;
import com.testing.vladyslav.cubes.util.ImagesHelper;
import com.testing.vladyslav.cubes.util.PixioHelper;
import com.testing.vladyslav.cubes.util.TextResourceReader;

import java.util.ArrayList;

public class StudioActivityPresenter {

    private UserModelsDBLoader model;
    private StudioActivityView studioActivityView;
    private StudioFragmentView studioFragmentView;
    private EditorFragmentView editorFragmentView;

    private ArrayList<UserModel> usersModelsList;
    private UserModel currentUserModel;
    private final Object changesCommitLock;

    private StudioRecyclerAdapter modelListAdapter;
    private ChangeCommitWaitingTask changesCommitWaitTask;

    private volatile boolean imageSaved = false;
    private volatile boolean changeCommit = false;

    private String FRAGMENT_STUDIO = "Studio";
    private String FRAGMENT_EDITOR = "Editor";

    private FragmentQueue fragmentQueue;

    private boolean viewMode = false;


    public interface StudioActivityView {

        Context getContext();
        void loadFragment(int fragmentId, boolean isForward);
        void startAnotherActivity(Class<?> cls);
        void showToast(String text);
        void showProgressBar();
        void hideProgressBar();
        void previousActivity();

    }

    public interface StudioFragmentView{

        void attachModelListAdapter(StudioRecyclerAdapter adapter);
        void updateData(ArrayList<UserModel> models, UserModel updatedModel);
        void changeData(ArrayList<UserModel> models);
        void renameModel(UserModel userModel);
        void deleteUserModel(UserModel userModel);
        void openContextMenu(StudioContextDialog.StudioContextDialogListener listener);
        void openEnterNameDialogBox(EnterFigureNameDialog.FigureNameDialogListener callback, String defText);
        void openAskToDeleteDialog(AskToDeleteDialog.AscToDeleteDialogListener callback);

    }

    public interface EditorFragmentView{

        CubeRenderer getRenderer();
        void setModelToOpen(UserModel model);
        void setBackwardButtonVisible(Boolean visible);
        void setForwardButtonVisible(Boolean visible);
        void setOnSurfaceViewCreatedListener(EditorFragment.OnSurfaceViewCreatedListener listener);
        void viewModeEnable(boolean b, ArrayList<PixioCube> cubes);
        void openEnterNameDialogBox(EnterFigureNameDialog.FigureNameDialogListener callback, String defText);
        void openAskToSaveDialogBox(AskToSaveDialog.SaveChangesDialogListener callback);

    }

    public interface ActionAfterSave{
        void onFigureSaved();
    }

    public interface ActionAfterLoadingModels{
        void onModelsLoaded();
    }

    //here communicate with studioActivityView
    public void attachViews(StudioActivityView activityView, EditorFragmentView editorFragmentView, StudioFragmentView studioFragmentView){

        this.studioActivityView = activityView;
        this.studioFragmentView = studioFragmentView;
        this.editorFragmentView = editorFragmentView;

        activityView.loadFragment(StudioActivity.STUDIOFRAGMENTID, true);
        fragmentQueue = new FragmentQueue();
        fragmentQueue.addFragmentToQueue(FRAGMENT_STUDIO);

        //check facetlist data loaded
        if(CubeDataHolder.getInstance().facetListHigh == null) {
            CubeDataHolder.getInstance().facetListLow = TextResourceReader.getFacetsFromFileObject(studioActivityView.getContext(), "cube_simple.obj");
            CubeDataHolder.getInstance().facetListMedium = TextResourceReader.getFacetsFromFileObject(studioActivityView.getContext(), "cube_medium.obj");
            CubeDataHolder.getInstance().facetListHigh = TextResourceReader.getFacetsFromFileObject(studioActivityView.getContext(), "cube_detailed.obj");
        }


    }

    //here do all the preparation work
    public StudioActivityPresenter(UserModelsDBLoader model){

        changesCommitLock = new Object();
        this.model = model;
        model.loadUserModels(new UserModelsDBLoader.LoadUserModelsCallback() {
            @Override
            public void onUserModelsLoad(ArrayList<UserModel> models) {

                usersModelsList = models;
                modelListAdapter = new StudioRecyclerAdapter(usersModelsList);
                modelListAdapter.setListener(new StudioRecyclerAdapter.StudioRecyclerListener() {
                    @Override
                    public void modelSelected(UserModel model) {

                        openUserModel(model, true);
                    }

                    @Override
                    public void contextMenuCalled(UserModel model) {
                        studioContextMenuCalled(model);
                    }

                    @Override
                    public Context getContext() {
                        return studioActivityView.getContext();
                    }


                });


                studioFragmentView.attachModelListAdapter(modelListAdapter);

            }
        });



    }

    public void createNewModelClicked(){

        currentUserModel = null;
        openUserModel(null, true);

    }

    public void openClicked(){

        if(figureIsSaved()){

            openStudio(true);

        }else{

            editorFragmentView.openAskToSaveDialogBox(new AskToSaveDialog.SaveChangesDialogListener() {
                @Override
                public void saveChangesClicked() {

                    saveClicked(new ActionAfterSave() {
                        @Override
                        public void onFigureSaved() {

                            studioActivityView.showToast("saved");
                            openStudio(true);

                        }
                    });

                }

                @Override
                public void doNotSaveChangesClicked() {
                    openStudio(true);
                }
            });

        }

    }

    public void saveClicked(final ActionAfterSave callback){

        updateCurrentUserModel();

        //if model is just created
        if(currentUserModel.getName() == null || currentUserModel.getName().equals("")){
            saveAsClicked(callback);
        }
        //if model already exists
        else if(figureExists(currentUserModel.getName())){
            saveCurrentUserModel(callback);
        }

    }

    public void saveAsClicked(final ActionAfterSave callback){

        updateCurrentUserModel();

        editorFragmentView.openEnterNameDialogBox(new EnterFigureNameDialog.FigureNameDialogListener() {
            @Override
            public boolean enterFigureNamePressed(String name) {

                if(figureExists(name)){
                    Toast.makeText(studioActivityView.getContext(), "Already exists", Toast.LENGTH_LONG).show();
                    return false;
                }else{
                    saveUserModelAs(name, callback);
                    return true;
                }
            }

            @Override
            public void cancelPressed() {

            }
        }, "");

    }

    public void studioContextMenuCalled(final UserModel userModel){

        studioFragmentView.openContextMenu(new StudioContextDialog.StudioContextDialogListener() {
            @Override
            public void onOpen() {

                openUserModel(userModel, true);

            }

            @Override
            public void onRename() {

                renameUserModel(userModel);

            }

            @Override
            public void onDelete() {

                deleteUserModel(userModel);

            }
        });

    }

    public void openMenuClicked(){

        clearChangeCommitThreads();

    }

    public void viewModeClicked(){

        ArrayList<PixioCube> cubes = null;

        viewMode = !viewMode;
        editorFragmentView.getRenderer().setViewMode(viewMode);

        if(viewMode){

            cubes = PixioHelper.figureToCubeList(editorFragmentView.getRenderer().getCubes());

        }

        editorFragmentView.viewModeEnable(viewMode, cubes);


    }

    //if figure already exists
    private void saveCurrentUserModel(ActionAfterSave callback){

        if(currentUserModel == null){ return; }

        studioActivityView.showProgressBar();

        model.updateUserModel(currentUserModel, new UserModelsDBLoader.UpdateModelCallback() {
            @Override
            public void onUserModelUpdated() {

            }
        });

        saveImage(currentUserModel.getName(), callback);
        editorFragmentView.getRenderer().getFigureChangeManager().setIsSaved(true);

    }

    //if figure is just created
    private void saveUserModelAs(String name, ActionAfterSave callback){

        if(currentUserModel == null) return;
        studioActivityView.showProgressBar();

        currentUserModel.setId(null);
        currentUserModel.setName(name);

        ArrayList<UserModel> models = new ArrayList<>();
        models.add(currentUserModel);
        model.insertUserModels(models, new UserModelsDBLoader.InsertUserModelsCallback() {
            @Override
            public void onUserModelsInsert() {

            }
        });

        saveImage(currentUserModel.getName(), callback);
        editorFragmentView.getRenderer().getFigureChangeManager().setIsSaved(true);

    }

    private void openStudio(boolean isForward){
        viewMode = false;
        studioActivityView.loadFragment(StudioActivity.STUDIOFRAGMENTID, isForward);
        if(isForward){
            fragmentQueue.addFragmentToQueue(FRAGMENT_STUDIO);
        }else{
            fragmentQueue.back();
        }

        studioFragmentView.attachModelListAdapter(modelListAdapter);

        loadUserModels(new ActionAfterLoadingModels() {
            @Override
            public void onModelsLoaded() {
                modelListAdapter.setModels(usersModelsList);
                studioFragmentView.attachModelListAdapter(modelListAdapter);
            }
        });
    }

    //interface for studio
    private void openUserModel(UserModel model, boolean isForward){

        editorFragmentView.setModelToOpen(model);
        studioActivityView.loadFragment(StudioActivity.EDITORFRAGMENTID, isForward);
        if(isForward){
            fragmentQueue.addFragmentToQueue(FRAGMENT_EDITOR);
        }else{
            fragmentQueue.back();
        }
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

        viewMode = false;


    }

    //interface for studio
    private void renameUserModel(final UserModel userModel){

        studioFragmentView.openEnterNameDialogBox(new EnterFigureNameDialog.FigureNameDialogListener() {
            @Override
            public boolean enterFigureNamePressed(String name) {

                if(figureExists(name)){

                    studioActivityView.showToast("Already exists");
                    return false;
                }else{

                    studioActivityView.showProgressBar();

                    ImagesHelper.renameImage(userModel.getName(), name, studioActivityView.getContext(), null);

                    userModel.setName(name);

                    model.updateUserModel(userModel, new UserModelsDBLoader.UpdateModelCallback() {
                        @Override
                        public void onUserModelUpdated() {

                            studioActivityView.showToast("Renamed");

                        }
                    });

                    loadUserModels(new ActionAfterLoadingModels() {
                        @Override
                        public void onModelsLoaded() {

                            studioFragmentView.renameModel(userModel);
                            studioActivityView.hideProgressBar();

                        }
                    });


                    return true;
                }

            }

            @Override
            public void cancelPressed() {

            }
        }, userModel.getName());


    }

    //interface for studio
    private void deleteUserModel(final UserModel userModel){

        studioFragmentView.openAskToDeleteDialog(new AskToDeleteDialog.AscToDeleteDialogListener() {
            @Override
            public void onDelete() {
                ImagesHelper.deleteImage(userModel.getName(), studioActivityView.getContext());
                model.deleteUserModel(userModel, new UserModelsDBLoader.DeleteUserModelCallback() {
                    @Override
                    public void onUserModelsDeleted() {
                        model.loadUserModels(new UserModelsDBLoader.LoadUserModelsCallback() {
                            @Override
                            public void onUserModelsLoad(ArrayList<UserModel> models) {

                                usersModelsList = models;
                                studioFragmentView.deleteUserModel(userModel);

                            }
                        });

                    }
                });
            }

            @Override
            public void onCancel() {

            }
        });

    }


    private void loadUserModels(final ActionAfterLoadingModels callback){

        model.loadUserModels(new UserModelsDBLoader.LoadUserModelsCallback() {
            @Override
            public void onUserModelsLoad(ArrayList<UserModel> models) {

                usersModelsList = models;
                if(callback!= null){
                    callback.onModelsLoaded();
                }

            }
        });

    }


    private boolean figureExists(String name){

        for (UserModel model: usersModelsList){
            if(model.getName().equals(name)){
                return true;
            }
        }
        return false;

    }

    private boolean figureIsSaved(){return editorFragmentView.getRenderer().getFigureChangeManager().isSaved();}

    private void saveImage(String name, ActionAfterSave callback){

        ImageLoader.getInstance().setClearImageCache(true);
        new ImageSaveWaitTask(name, callback).execute(null, null, null);

    }

    //synchronises currentUserModel with figureBuilder
    private void updateCurrentUserModel(){

        if(currentUserModel == null){
            currentUserModel = editorFragmentView.getRenderer().getRenderingModel();
        }else {

            UserModel renderingModel = editorFragmentView.getRenderer().getRenderingModel();
            if(renderingModel.getId() == null || !renderingModel.getId().equals(currentUserModel.getId())){
                renderingModel.setId(currentUserModel.getId());
                currentUserModel = renderingModel;
            }else{
                currentUserModel = editorFragmentView.getRenderer().getRenderingModel();
            }
        }

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

    public void activityPaused(){



    }

    public void backButtonPressed(){

        viewMode = false;
        if(fragmentQueue != null){

            if(fragmentQueue.currentFragment.previous == null){
                studioActivityView.previousActivity();
            }else{


                //if current screen is editor
                if(fragmentQueue.currentFragment.tag.equals(FRAGMENT_EDITOR)){

                    clearChangeCommitThreads();

                    if(!figureIsSaved()){
                        editorFragmentView.openAskToSaveDialogBox(new AskToSaveDialog.SaveChangesDialogListener() {
                            @Override
                            public void saveChangesClicked() {
                                saveClicked(new ActionAfterSave() {
                                    @Override
                                    public void onFigureSaved() {
                                        studioActivityView.showToast("saved");
                                        openStudio(false);
                                    }
                                });
                            }

                            @Override
                            public void doNotSaveChangesClicked() {

                                openStudio(false);

                            }
                        });
                    }else{
                        openStudio(false);
                    }


                }
                //if current screen is studio
                else if(fragmentQueue.currentFragment.tag.equals(FRAGMENT_STUDIO)){
                    openUserModel(currentUserModel, false);
                }

            }


        }else{
            studioActivityView.previousActivity();
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
        private ActionAfterSave callback;

        public ImageSaveWaitTask(String name, ActionAfterSave callback){
            this.imageName = name;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            final Object lock = new Object();

            editorFragmentView.getRenderer().setScreenshotMode(new CubeRenderer.ScreenshotHandler() {
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
            if(callback != null){
                callback.onFigureSaved();
            }
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

    private class FragmentQueue{

        private FragmentScreen currentFragment = null;

        void back(){

            if(currentFragment != null) { currentFragment = currentFragment.previous;}

        }
        void addFragmentToQueue(String tag){

            if(currentFragment == null){
                currentFragment = new FragmentScreen(tag, null);
            }else if(currentFragment.tag.equals(tag)){
                return;
            }else if(currentFragment.previous != null && currentFragment.previous.tag.equals(FRAGMENT_EDITOR)){
                back();
            }else{
                currentFragment = new FragmentScreen(tag, currentFragment);
            }

        }

    }

    private class FragmentScreen{

        String tag;
        FragmentScreen previous;

        FragmentScreen(String tag, FragmentScreen previous){
            this.tag = tag;
            this.previous = previous;
        }


    }








}
