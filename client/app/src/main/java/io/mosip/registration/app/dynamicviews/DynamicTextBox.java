package io.mosip.registration.app.dynamicviews;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import android.widget.TextView;
import androidx.annotation.Nullable;

import io.mosip.registration.app.R;

import java.util.HashMap;
import java.util.Map;

public class DynamicTextBox extends LinearLayout implements DynamicView {

    String languageCode="";
    String labelText="";
    String validationRule="";
    String fieldType = "";
    final int layoutId=R.layout.dynamic_text_box;

    public DynamicTextBox(Context context,String langCode,String label,String validation, String type) {
        super(context);
        languageCode=langCode;
        labelText=label;
        validationRule=validation;
        fieldType = type;
        init(context);
    }

    public DynamicTextBox(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DynamicTextBox(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public DynamicTextBox(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    private void init(Context context) {
        inflate(context, layoutId , this);
        ((TextView)findViewById(R.id.text_label)).setText(labelText);
        initComponents();
   }

    private void initComponents() {
        ((TextView)findViewById(R.id.text_input_edit)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public String getDataType() {
        return fieldType;
    }

    public Object getValue() {
        String value = ((TextView)findViewById(R.id.text_input_edit)).getText().toString();
        if (getDataType().equalsIgnoreCase("simpleType")) {
            Map<String, String> map = new HashMap<>();
            map.put("eng", value);
            return map;
        }
        return value;
    }

    @Override
    public void setValue() {
    }

    @Override
    public boolean validValue() {
        return true;
    }

}
