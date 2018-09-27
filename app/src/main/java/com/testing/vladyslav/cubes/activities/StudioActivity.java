package com.testing.vladyslav.cubes.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.testing.vladyslav.cubes.CubeRenderer;
import com.testing.vladyslav.cubes.R;
import com.testing.vladyslav.cubes.Settings;
import com.testing.vladyslav.cubes.data.CubeDataHolder;
import com.testing.vladyslav.cubes.database.UserModelsDBLoader;
import com.testing.vladyslav.cubes.database.entities.UserModel;
import com.testing.vladyslav.cubes.fragments.EditorFragment;
import com.testing.vladyslav.cubes.fragments.StudioFragment;
import com.testing.vladyslav.cubes.presenters.StudioActivityPresenter;
import com.testing.vladyslav.cubes.util.TextResourceReader;


public class StudioActivity extends AppCompatActivity implements  StudioActivityPresenter.StudioActivityView {

    private FragmentManager fragmentManager;
    private StudioFragment studioFragment;
    private EditorFragment editorFragment;

    private ProgressBar progressBar;

    public final static int STUDIOFRAGMENTID = 0;
    public final static int EDITORFRAGMENTID = 1;

    private FrameLayout fragmentFrame;
    private int FragmentFrameId = R.id.fragment_frame;
    private int graphicsQuality = 1;



    private UserModel userModel;
    private StudioActivityPresenter presenter;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studio);

        //check facetlist data loaded
        if(CubeDataHolder.getInstance().facetListHigh == null) {
            CubeDataHolder.getInstance().facetListLow = TextResourceReader.getFacetsFromFileObject(getApplicationContext(), "cube_simple.obj");
            CubeDataHolder.getInstance().facetListMedium = TextResourceReader.getFacetsFromFileObject(getApplicationContext(), "cube_medium.obj");
            CubeDataHolder.getInstance().facetListHigh = TextResourceReader.getFacetsFromFileObject(getApplicationContext(), "cube_detailed.obj");
        }
        CubeDataHolder.getInstance().setGraphicsQuality(Settings.graphicsQuality);

        fragmentManager = getSupportFragmentManager();
        studioFragment = new StudioFragment();
        editorFragment = new EditorFragment();
        fragmentFrame = findViewById(R.id.fragment_frame);
        progressBar = findViewById(R.id.progress_bar_editor);
        progressBar.setVisibility(View.INVISIBLE);

        UserModelsDBLoader modelsDBModel = new UserModelsDBLoader(getApplicationContext());
        presenter = new StudioActivityPresenter(modelsDBModel);
        presenter.attachViews(this, editorFragment, studioFragment);

        studioFragment.setPresenter(presenter);
        editorFragment.setPresenter(presenter);


    }

    public void loadFragment(int fragmentId, boolean isForward){

        switch (fragmentId){
            case EDITORFRAGMENTID:{
                loadFragment(editorFragment, isForward);
                break;
            }
            case STUDIOFRAGMENTID:{
                loadFragment(studioFragment, isForward);
                break;
            }
            default:{
                loadFragment(studioFragment, isForward);
                break;
            }
        }

    }

    private void loadFragment(Fragment fragment, boolean isForward){

        if(fragmentManager!=null){

            FragmentTransaction transaction = fragmentManager.beginTransaction();

            if(isForward){
                transaction.setCustomAnimations(R.anim.slide_left_on_screen, R.anim.slide_left_off_screen);
            }else{
                transaction.setCustomAnimations(R.anim.slide_right_on_screen, R.anim.slide_right_off_screen);
            }

            transaction.replace(R.id.fragment_frame, fragment, "no_tag");
            transaction.commit();

        }

    }

    public void setTxt_isTouchedText(String txt){

//        if(txt_isTouched != null && txt != null)
//            txt_isTouched.setText(txt);

    }

    @Override
    protected void onResume() {

        super.onResume();
        //presenter.activityResumed();
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }


    @Override
    public void startAnotherActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);

    }

    @Override
    public void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    public void showProgressBar(){
        if(progressBar!= null){
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgressBar(){
        if(progressBar!= null){
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {

        if(presenter!= null){
            presenter.backButtonPressed();
        }

    }

    @Override
    public void previousActivity() {

        super.onBackPressed();

    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.activityPaused();
    }
}
