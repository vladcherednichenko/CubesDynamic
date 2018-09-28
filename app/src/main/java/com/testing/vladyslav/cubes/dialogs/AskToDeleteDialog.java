package com.testing.vladyslav.cubes.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.testing.vladyslav.cubes.R;

public class AskToDeleteDialog extends Dialog implements
        View.OnClickListener{


        public Activity c;
        public Dialog d;
        private Button yes, no;

        private AscToDeleteDialogListener listener;

        public interface AscToDeleteDialogListener {

            void onDelete();
            void onCancel();

        }

        public void setListener(AscToDeleteDialogListener listener){this.listener = listener;}

        public AskToDeleteDialog(Activity a) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_ask_to_delete);
            yes = (Button) findViewById(R.id.btn_yes);
            no = (Button) findViewById(R.id.btn_no);
            yes.setOnClickListener(this);
            no.setOnClickListener(this);

            //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_yes: {


                    if (listener != null) {
                        listener.onDelete();
                    }

                    dismiss();
                    break;
                }
                case R.id.btn_no:

                    if(listener != null){
                        listener.onCancel();
                    }

                    dismiss();
                    break;
                default:
                    break;
            }
            dismiss();

        }


}
