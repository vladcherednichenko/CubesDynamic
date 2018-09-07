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
import com.testing.vladyslav.cubes.data.CubeDataHolder;
import com.testing.vladyslav.cubes.database.UserModelsDBLoader;
import com.testing.vladyslav.cubes.database.entities.UserModel;
import com.testing.vladyslav.cubes.fragments.EditorFragment;
import com.testing.vladyslav.cubes.fragments.StudioFragment;
import com.testing.vladyslav.cubes.presenters.StudioActivityPresenter;


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

        fragmentManager = getSupportFragmentManager();
        studioFragment = new StudioFragment();
        editorFragment = new EditorFragment();
        fragmentFrame = findViewById(R.id.fragment_frame);
        progressBar = findViewById(R.id.progress_bar_editor);
        progressBar.setVisibility(View.INVISIBLE);

        graphicsQuality = getIntent().getIntExtra("quality", 1);
        CubeDataHolder.getInstance().setGraphicsQuality(graphicsQuality);

        UserModelsDBLoader modelsDBModel = new UserModelsDBLoader(getApplicationContext());
        presenter = new StudioActivityPresenter(modelsDBModel);
        presenter.attachViews(this, editorFragment, studioFragment);

        studioFragment.setPresenter(presenter);
        editorFragment.setPresenter(presenter);


    }

    public void loadFragment(int fragmentId){

        switch (fragmentId){
            case EDITORFRAGMENTID:{
                loadFragment(editorFragment);
                break;
            }
            case STUDIOFRAGMENTID:{
                loadFragment(studioFragment);
                break;
            }
            default:{
                loadFragment(studioFragment);
                break;
            }
        }

    }

    private void loadFragment(Fragment fragment){

        if(fragmentManager!=null){

            FragmentTransaction transaction = fragmentManager.beginTransaction();
//            transaction.setCustomAnimations(R.anim.fade_in, R.anim.menu_slide_out);
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
}