package com.testing.vladyslav.cubes.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.testing.vladyslav.cubes.R;

public class EnterFigureNameDialog extends Dialog implements
        View.OnClickListener{


        public Activity c;
        public Dialog d;
        private Button yes, no;
        private EditText textField;

        private FigureNameDialogListener listener;

        public interface FigureNameDialogListener {

            boolean enterFigureNamePressed(String name);
            void cancelPressed();

        }

        public void setListener(FigureNameDialogListener listener){this.listener = listener;}

        public EnterFigureNameDialog(Activity a) {
            super(a);
            // TODO Auto-generated constructor stub
            this.c = a;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_enter_figure_name);
            yes = (Button) findViewById(R.id.btn_yes);
            no = (Button) findViewById(R.id.btn_no);
            textField = findViewById(R.id.edit_pixiocode);
            yes.setOnClickListener(this);
            no.setOnClickListener(this);

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_yes: {


                    if (listener != null) {
                        if(listener.enterFigureNamePressed(textField.getText().toString())){
                            dismiss();
                        }

                    }else{
                        dismiss();
                    }

                    break;
                }
                case R.id.btn_no:

                    if (listener != null) {
                        listener.cancelPressed();
                    }

                    dismiss();
                    break;
                default:
                    break;
            }

        }


}
