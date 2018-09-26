package com.testing.vladyslav.cubes;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ColorsLayoutCreator {


    public static void createColorBlocksLayout(Context context, ArrayList<PixioCube> cubes, LinearLayoutCompat resultLayout, TextView txtColorsAndBlocks){

        LinearLayoutCompat blockRow1 = new LinearLayoutCompat(context);
        LinearLayoutCompat blockRow2 = new LinearLayoutCompat(context);

        resultLayout.removeAllViews();

        blockRow1.setOrientation(0);
        blockRow2.setOrientation(0);
        blockRow1.setGravity(Gravity.CENTER);
        blockRow2.setGravity(Gravity.CENTER);
        ViewGroup.LayoutParams blockRowParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        blockRow1.setLayoutParams(blockRowParams);
        blockRow1.setLayoutParams(blockRowParams);


        int blockAmount = 0;
        int order = 0;
        Resources r = context.getResources();


        for (PixioCube pc : cubes){

            LinearLayoutCompat colorBlock = new LinearLayoutCompat(context);
            ViewGroup.LayoutParams colorBlockParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            colorBlock.setOrientation(1);
            colorBlock.setGravity(Gravity.CENTER);
            colorBlock.setLayoutParams(colorBlockParams);

            ImageView imgBlock = new ImageView(context);
            imgBlock.setImageDrawable(context.getResources().getDrawable(context.getResources().getIdentifier(pc.filename, "mipmap", context.getPackageName())));

            int blockHeight = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, r.getDisplayMetrics()));
            int blockWidth = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, r.getDisplayMetrics()));
            ViewGroup.LayoutParams imageBlockParams = new ViewGroup.LayoutParams(blockWidth, blockHeight);
            imgBlock.setLayoutParams(imageBlockParams);

            TextView txtBlockAmount = new TextView(context);
            txtBlockAmount.setGravity(Gravity.CENTER);
            txtBlockAmount.setText(String.valueOf(pc.amount));
            txtBlockAmount.setTextSize(10);
            txtBlockAmount.setTextColor(context.getResources().getColor(R.color.colorBlack));
            ViewGroup.LayoutParams txtBlockAmountParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            txtBlockAmount.setLayoutParams(txtBlockAmountParams);

            colorBlock.addView(imgBlock);
            colorBlock.addView(txtBlockAmount);

            //ViewGroup.LayoutParams params2 = new ViewGroup.LayoutParams(300, 300);

            //colorBlock.setLayoutParams(params2);

            if (order <8){
                blockRow1.addView(colorBlock);
            }else{
                blockRow2.addView(colorBlock);
            }

            blockAmount += pc.amount;
            order ++;

        }


//        String text;
//        if (cubes.size() == 0 && thisSet.getSurprise()!=null){
//
//            text = thisSet.getSurprise();
//            text = text.replace("\\n", "\n");
//        }else{
//            text = String.valueOf(blockAmount) + " blocks in " +String.valueOf(cubes.size())+ " colors";
//        }
//        if (txtColorsAndBlocks!= null){
//            txtColorsAndBlocks.setText(text);
//        }

        resultLayout.addView(blockRow1);
        if (cubes.size()> 8){
            resultLayout.addView(blockRow2);

        }


    }

    private static int convertToDevicePixels(Context context,  int pixels){

        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixels, r.getDisplayMetrics()));

    }

}
