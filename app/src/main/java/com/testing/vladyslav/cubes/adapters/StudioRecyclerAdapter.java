package com.testing.vladyslav.cubes.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.testing.vladyslav.cubes.R;
import com.testing.vladyslav.cubes.data.CubeDataHolder;
import com.testing.vladyslav.cubes.database.entities.UserModel;
import com.testing.vladyslav.cubes.util.ImageLoader;

import java.util.ArrayList;

public class StudioRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    StudioRecyclerListener listener;

    public interface StudioRecyclerListener{

        void modelSelected(UserModel model);
        Context getContext();
        void onModelDelete(UserModel model);

    }

    public void setListener(StudioRecyclerListener listener){
        this.listener = listener;
    }

    private ArrayList<UserModel> models;

    public StudioRecyclerAdapter(ArrayList<UserModel> models){
        this.models = models;
    }

    public void setModels(ArrayList<UserModel> models){
        this.models = models;
    }

    private class StudioItemViewHolder extends RecyclerView.ViewHolder{

        public TextView leftModelName_txt;
        public TextView rightModelName_txt;

        public ImageView leftModel_img;
        public ImageView rightModel_img;

        public StudioItemViewHolder(View itemView) {
            super(itemView);

            leftModelName_txt = itemView.findViewById(R.id.txt_model_name_left);
            rightModelName_txt = itemView.findViewById(R.id.txt_model_name_right);

            leftModel_img = itemView.findViewById(R.id.img_model_left);
            rightModel_img = itemView.findViewById(R.id.img_model_right);


        }

        public void hideRightFigure(){

            rightModel_img.setVisibility(View.INVISIBLE);
            rightModelName_txt.setVisibility(View.INVISIBLE);

        }


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = (LayoutInflater.from(parent.getContext()).inflate(R.layout.studio_list_row, parent, false));

        return new StudioItemViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final UserModel modelLeft = position*2 > models.size()-1? null: models.get(position*2);
        final UserModel modelRight = position*2+1 > models.size()-1? null: models.get(position*2+1);

        final int pos = position;

        StudioItemViewHolder itemHolder = (StudioItemViewHolder)holder;

        if (modelLeft != null){
            itemHolder.leftModelName_txt.setText(modelLeft.getName());
            itemHolder.leftModel_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    CubeDataHolder.getInstance().modelToLoad = pos*2;
                    if (listener != null){
                        listener.modelSelected(modelLeft);
                    }

                }
            });

            itemHolder.leftModel_img.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    CubeDataHolder.getInstance().modelToLoad = pos*2;
                    if (listener != null){
                        listener.onModelDelete(modelLeft);
                    }
                    return true;
                }
            });

            ImageLoader.getInstance().loadImageIntoImageView(listener.getContext(), modelLeft.getName(), itemHolder.leftModel_img);


        }

        if(modelRight!=null){
            itemHolder.rightModelName_txt.setText(modelRight.getName());
            itemHolder.rightModel_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    CubeDataHolder.getInstance().modelToLoad = pos*2+1;
                    if (listener != null){
                        listener.modelSelected(modelRight);
                    }

                }
            });

            itemHolder.rightModel_img.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    CubeDataHolder.getInstance().modelToLoad = pos*2+1;
                    if (listener != null){
                        listener.onModelDelete(modelRight);
                    }
                    return true;
                }
            });

            ImageLoader.getInstance().loadImageIntoImageView(listener.getContext(), modelRight.getName(), itemHolder.rightModel_img);


        }else{
            itemHolder.hideRightFigure();
        }



    }

    @Override
    public int getItemViewType(int position) {

        return 0;
    }

    @Override
    public int getItemCount() {

        int count = models.size() % 2>0? models.size() /2+1 : models.size() /2;

        return count;
    }
}