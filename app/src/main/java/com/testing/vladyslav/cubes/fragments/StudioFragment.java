package com.testing.vladyslav.cubes.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.testing.vladyslav.cubes.R;
import com.testing.vladyslav.cubes.adapters.StudioRecyclerAdapter;
import com.testing.vladyslav.cubes.database.UserModelsDBLoader;
import com.testing.vladyslav.cubes.database.entities.UserModel;
import com.testing.vladyslav.cubes.presenters.StudioActivityPresenter;
import com.testing.vladyslav.cubes.util.ImageLoader;

import java.util.ArrayList;

public class StudioFragment extends Fragment implements StudioActivityPresenter.StudioFragmentView{


    private RecyclerView recyclerView;
    private StudioActivityPresenter presenter;

    private ImageView icAddModel;
    private StudioRecyclerAdapter adapter;

    UserModelsDBLoader dbLoader;

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_studio, container, false);

        recyclerView = view.findViewById(R.id.studio_recycler_view);
        icAddModel = view.findViewById(R.id.ic_create_model);
        icAddModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.createNewModelClicked();
            }
        });

        return view;

    }

    public void setPresenter(StudioActivityPresenter presenter){this.presenter = presenter;}

    @Override
    public void attachModelListAdapter(StudioRecyclerAdapter adapter) {

        this.adapter = adapter;
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    public void setNewRecyclerViewData(ArrayList<UserModel> userModelList){

        //Parcelable recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();

        adapter.setModels(userModelList);
        adapter.notifyDataSetChanged();

        //recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

    }



}
