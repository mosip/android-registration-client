package io.mosip.registration.app.ui.dynamic.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;


import io.mosip.registration.app.R;
import io.mosip.registration.app.ui.dynamic.DynamicView;

public class DynamicDropDownBox extends LinearLayout implements DynamicView {
    Spinner drpDown;

    String languageCode="";
    String labelText="";
    String validationRule="";
    final int layoutId = R.layout.dynamic_dropdown_box;
    public DynamicDropDownBox(Context context, String langCode, String label, String validation) {
        super(context);

        languageCode=langCode;
        labelText=label;
        validationRule=validation;
        init(context);
    }

    public DynamicDropDownBox(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DynamicDropDownBox(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public DynamicDropDownBox(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    private void init(Context context) {
        inflate(context, layoutId, this);
        ((TextView)findViewById(R.id.dropdown_label)).setText(labelText);
        initComponents();
    }

    private void initComponents() {
         drpDown= findViewById(R.id.dropdown_input);
    }

    public String getValue(){
        String dob="";
        //dob =dateBox.getText().toString()+"/"+monthBox.getText().toString()+"/"+yearBox.getText().toString();
        return dob;
    }

    @Override
    public void setValue(String value) {
        String[] dob = value.split("/");
        if(dob.length==3){
//            dateBox.setText(dob[0]);
//            monthBox.setText(dob[1]);
//            yearBox.setText(dob[2]);
        }
    }

}
