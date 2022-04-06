package io.mosip.registration.app.dynamicviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;


import io.mosip.registration.app.R;
import io.mosip.registration.app.dynamicviews.DynamicView;
import io.mosip.registration.clientmanager.spi.MasterDataService;

import java.util.ArrayList;
import java.util.List;

public class DynamicDropDownBox extends LinearLayout implements DynamicView {

    String languageCode="";
    String labelText="";
    String validationRule="";
    String subType = null;
    String fieldId = null;
    final int layoutId = R.layout.dynamic_dropdown_box;
    private MasterDataService masterDataService;

    public DynamicDropDownBox(Context context, String langCode, String label, String validation,
                              MasterDataService masterDataService, String subType, String fieldId) {
        super(context);

        languageCode=langCode;
        labelText=label;
        validationRule=validation;
        this.masterDataService = masterDataService;
        this.subType = subType;
        this.fieldId = fieldId;
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
        initComponents(context);
    }

    private void initComponents(Context context) {
        List<String> items = (subType != null) ?
                this.masterDataService.findLocationByHierarchyLevel(Integer.parseInt(subType), languageCode) :
                this.masterDataService.getFieldValues(fieldId, languageCode);

        @SuppressLint("ResourceType")
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,
                items == null ? new ArrayList<>() : items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.dropdown_input);
        sItems.setAdapter(adapter);
    }

    @Override
    public String getDataType() {
        return "simpleType";
    }

    public String getValue(){
        Spinner sItems = (Spinner) findViewById(R.id.dropdown_input);
        return sItems.getSelectedItem().toString();
    }

    @Override
    public void setValue() {
    }

    @Override
    public boolean validValue() {
        return true;
    }
}
