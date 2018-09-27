package com.testing.vladyslav.cubes.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.testing.vladyslav.cubes.R;

public class StudioContextDialog extends Dialog implements
        View.OnClickListener{


        public Activity c;
        public Dialog d;
        private TextView txt_open, txt_rename, txt_delete;

        private StudioContextDialogListener listener;

        public interface StudioContextDialogListener {

            void onOpen();
            void onRename();
            void onDelete();

        }

        public void setListener(StudioContextDialogListener listener){this.listener = listener;}

        public StudioContextDialog(Activity a) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_studio_context_menu);

            txt_open = findViewById(R.id.studio_context_open);
            txt_rename = findViewById(R.id.studio_context_rename);
            txt_delete = findViewById(R.id.studio_context_delete);

            txt_open.setOnClickListener(this);
            txt_rename.setOnClickListener(this);
            txt_delete.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.studio_context_open: {


                    if (listener != null) {

                        listener.onOpen();

                    }

                    dismiss();

                    break;
                }
                case R.id.studio_context_rename: {

                    if (listener != null) {

                        listener.onRename();

                    }

                    dismiss();
                    break;

                }
                case R.id.studio_context_delete: {

                    if (listener != null) {

                        listener.onDelete();

                    }

                    dismiss();
                    break;

                }

                default:
                    dismiss();
                    break;
            }

        }


}
