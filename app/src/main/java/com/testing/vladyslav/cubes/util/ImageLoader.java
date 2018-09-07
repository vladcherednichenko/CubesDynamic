package com.testing.vladyslav.cubes.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.signature.ObjectKey;
import com.testing.vladyslav.cubes.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ImageLoader{

    private static ImageLoader instance;

    private ImageLoader(){}

    private boolean clearImageCache = true;

    public static ImageLoader getInstance() {


        if(instance == null){
            instance = new ImageLoader();
        }
        return instance;
    }

    public void setClearImageCache(boolean b){
        clearImageCache = b;
    }

    public void loadImageIntoImageView(Context context, String imageName, ImageView imageView){

        File picture = new File(context.getFilesDir() + File.separator + imageName);

        if (picture.exists()) {

            if(clearImageCache){
                GlideApp.with(context).load(picture).signature(new ObjectKey(String.valueOf(System.currentTimeMillis()))).into(imageView);
            }else{
                GlideApp.with(context).load(picture).into(imageView);
            }

        }else{
            GlideApp.with(context).load(R.drawable.cubes_logo).into(imageView);
        }

    }


}
