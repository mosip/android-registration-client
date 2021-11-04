package io.mosip.registration.app.ui.dynamic.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;


import io.mosip.registration.app.R;
import io.mosip.registration.app.ui.dynamic.DynamicView;

public class DynamicAgeDateBox extends LinearLayout implements DynamicView {
    EditText dateBox;
    EditText monthBox;
    EditText yearBox;
    EditText ageBox;

    String languageCode="";
    String labelText="";
    String validationRule="";
final int layoutId =R.layout.dynamic_agedate_box;
    public DynamicAgeDateBox(Context context,String langCode,String label,String validation) {
        super(context);

        languageCode=langCode;
        labelText=label;
        validationRule=validation;
        init(context);
    }

    public DynamicAgeDateBox(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DynamicAgeDateBox(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public DynamicAgeDateBox(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    private void init(Context context) {
        inflate(context, layoutId, this);
        ((TextView)findViewById(R.id.dob_label)).setText(labelText);
        initComponents();
    }

    private void initComponents() {
         dateBox= findViewById(R.id.dob_date);
         monthBox= findViewById(R.id.dob_month);
         yearBox= findViewById(R.id.dob_year);
         ageBox= findViewById(R.id.dob_age);
    }

    public String getValue(){
        String dob="";
        dob =dateBox.getText().toString()+"/"+monthBox.getText().toString()+"/"+yearBox.getText().toString();
        return dob;
    }

    @Override
    public void setValue(String value) {
        String[] dob = value.split("/");
        if(dob.length==3){
            dateBox.setText(dob[0]);
            monthBox.setText(dob[1]);
            yearBox.setText(dob[2]);
        }
    }

}
