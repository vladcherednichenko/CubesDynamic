package com.testing.vladyslav.cubes.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.testing.vladyslav.cubes.R;
import com.testing.vladyslav.cubes.adapters.StudioRecyclerAdapter;
import com.testing.vladyslav.cubes.database.UserModelsDBLoader;
import com.testing.vladyslav.cubes.database.entities.UserModel;
import com.testing.vladyslav.cubes.dialogs.AskToDeleteDialog;
import com.testing.vladyslav.cubes.dialogs.AskToSaveDialog;
import com.testing.vladyslav.cubes.dialogs.EnterFigureNameDialog;
import com.testing.vladyslav.cubes.dialogs.StudioContextDialog;
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
        recyclerView.setItemAnimator(null);
        icAddModel = view.findViewById(R.id.ic_create_model);
        icAddModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.createNewModelClicked();
            }
        });


        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.backButtonPressed();
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

    public void updateData(ArrayList<UserModel> userModelList, UserModel changedModel){

        adapter.setModels(userModelList);
        adapter.notifyItemChanged(getChangedModelIndex(userModelList, changedModel));
        //adapter.noti

    }

    public void changeData(ArrayList<UserModel> userModelList){

        adapter.setModels(userModelList);
        adapter.notifyDataSetChanged();

    }

    public void renameModel(UserModel changedUserModel){
        adapter.notifyItemChanged(getChangedModelIndex(adapter.getModels(), changedUserModel));
    }

    public void deleteUserModel(UserModel deletedUserModel){

        int oldItemCount = adapter.getItemCount();
        int index = getChangedModelIndex(adapter.getModels(), deletedUserModel);
        adapter.getModels().remove(deletedUserModel);

        int newItemCount = adapter.getItemCount();
        if(newItemCount < oldItemCount){
            adapter.notifyItemRemoved(oldItemCount - 1);
        }

        adapter.notifyItemRangeChanged(index, adapter.getItemCount()-index);


        //adapter.notifyItemChanged(index);

    }

    private int getChangedModelIndex(ArrayList<UserModel> models, UserModel changedModel){

        for (int i = 0; i< models.size(); i++){
            if(models.get(i).getName().equals(changedModel.getName())){
                return i/2;
            }
        }

        return 0;

    }

    @Override
    public void openContextMenu(StudioContextDialog.StudioContextDialogListener callback) {

        StudioContextDialog dialog = new StudioContextDialog(getActivity());
        dialog.setListener(callback);
        dialog.show();

    }

    @Override
    public void openEnterNameDialogBox(EnterFigureNameDialog.FigureNameDialogListener callback, String defText){

        EnterFigureNameDialog dialog = new EnterFigureNameDialog(getActivity(), defText);
        dialog.setListener(callback);
        dialog.show();

    }

    @Override
    public void openAskToDeleteDialog(AskToDeleteDialog.AscToDeleteDialogListener callback){

        AskToDeleteDialog dialog = new AskToDeleteDialog(getActivity());
        dialog.setListener(callback);
        dialog.show();

    }
}
