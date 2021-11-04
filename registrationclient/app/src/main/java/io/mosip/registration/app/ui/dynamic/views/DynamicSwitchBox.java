package io.mosip.registration.app.ui.dynamic.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;


import java.util.ArrayList;
import java.util.List;

import io.mosip.registration.app.R;
import io.mosip.registration.app.ui.dynamic.DynamicView;

public class DynamicSwitchBox extends LinearLayout implements DynamicView {



    String languageCode="";
    String labelText="";
    String validationRule="";
    final int layoutId=R.layout.dynamic_switch_box;
    public DynamicSwitchBox(Context context, String langCode, String label, String validation) {
        super(context);

        languageCode=langCode;
        labelText=label;
        validationRule=validation;
        init(context);
    }

    public DynamicSwitchBox(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DynamicSwitchBox(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public DynamicSwitchBox(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    private void init(Context context) {
        inflate(context, layoutId, this);
        ((TextView)findViewById(R.id.switch_label)).setText(labelText);
        initComponents(context);
    }
    String selectedOption="";
    List<Button> allOptions = new ArrayList<>();

    private void initComponents(Context context) {
        ViewGroup viewGroup = findViewById(R.id.option_holder_panel);

        for(int i=1;i<=2;i++){
            Button option=new Button(context);
            option.setText("Option "+i);

            option.setBackground(getResources().getDrawable(R.drawable.button_option_default));
            LayoutParams param=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,50);

            param.setMarginEnd(16);
//            param.setMarginStart(16);
            option.setLayoutParams(param);
            viewGroup.addView(option);
            allOptions.add(option);

            option.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedOption=option.getText().toString();
                    for(Button btn:allOptions){
                        if(btn.getText().toString().equalsIgnoreCase(option.getText().toString())==false){
                            btn.setBackground(getResources().getDrawable(R.drawable.button_option_default));
                        }
                        else{
                            btn.setBackground(getResources().getDrawable(R.drawable.button_option_selected));
                        }
                    }
                }
            });

        }

    }


    public String getValue(){

        return selectedOption;
    }

    @Override
    public void setValue(String value) {
       for(Button btn :allOptions){
           if(btn.getText().toString().equalsIgnoreCase(value)){
               selectedOption=value;
               btn.setBackground(getResources().getDrawable(R.drawable.button_option_selected));

           }
           else{
               btn.setBackground(getResources().getDrawable(R.drawable.button_option_default));
           }
       }
    }

}
