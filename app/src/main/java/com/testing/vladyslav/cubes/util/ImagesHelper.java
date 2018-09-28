package com.testing.vladyslav.cubes.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.content.ContentValues.TAG;

public class ImagesHelper {

    private static String tag = "Image helper";

    public interface SaveImageCallback{

        void onImageSaved();

    }

    public static void saveImage(Bitmap bitmap, String filename, Context context, SaveImageCallback callback){

        boolean del = deleteImage(filename, context);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(getImagePath(context, filename));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                    if(callback!=null){
                        callback.onImageSaved();
                        Log.d(tag, "saved to: " + getImagePath(context, filename));

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void renameImage(String filename, String newFileName, Context context, SaveImageCallback callback){

        File oldImage = new File(getImagePath(context, filename));
        File newImage = new File(getImagePath(context, newFileName));

        if(oldImage.renameTo(newImage) && callback!= null){
            callback.onImageSaved();
        }

    }

    public static boolean deleteImage(String filename, Context context){

        File file = new File(getImagePath(context, filename));
        if(file.exists()){

            if(file.delete()){
                Log.d(tag, "File deleted: " + file.toString());
            }else{
                Log.d(tag, "File not ddeleted: " + file.toString());
            }

        }

        context.deleteFile(filename);

        return false;
    }

    private static String getImagePath(Context context, String fileName){

        String fileDir = context.getFilesDir() + File.separator;

        return fileDir + fileName;

    }




}
