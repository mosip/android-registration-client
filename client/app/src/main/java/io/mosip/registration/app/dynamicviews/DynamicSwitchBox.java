package io.mosip.registration.app.dynamicviews;

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
import io.mosip.registration.app.dynamicviews.DynamicView;
import io.mosip.registration.clientmanager.spi.MasterDataService;

public class DynamicSwitchBox extends LinearLayout implements DynamicView {

    String fieldId;
    String selectedOption="";
    String languageCode="";
    String labelText="";
    String validationRule="";
    final int layoutId=R.layout.dynamic_switch_box;
    List<Button> allOptions = new ArrayList<>();
    MasterDataService masterDataService;

    public DynamicSwitchBox(Context context, String langCode, String label, String validation, String fieldId,
                            MasterDataService masterDataService) {
        super(context);

        languageCode=langCode;
        labelText=label;
        validationRule=validation;
        this.masterDataService = masterDataService;
        this.fieldId = fieldId;
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

    private void initComponents(Context context) {
        selectedOption = "";
        ViewGroup viewGroup = findViewById(R.id.option_holder_panel);

        List<String> options = masterDataService.getFieldValues(fieldId, languageCode);

        for(String option : options){
            Button button = new Button(context);
            button.setText(option);
            button.setBackground(getResources().getDrawable(R.drawable.button_option_default));
            LayoutParams param=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,50);

            param.setMarginEnd(16);
            button.setLayoutParams(param);
            viewGroup.addView(button);
            allOptions.add(button);

            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedOption = button.getText().toString();
                    for(Button btn:allOptions){
                        if(btn.getText().toString().equalsIgnoreCase(button.getText().toString())==false){
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


    @Override
    public String getDataType() {
        return "simpleType";
    }

    public String getValue() {
        return selectedOption;
    }

}
