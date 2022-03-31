package io.mosip.registration.app.dynamicviews;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;

import android.widget.TextView;
import androidx.annotation.Nullable;

import io.mosip.registration.app.R;

import java.util.HashMap;
import java.util.Map;

public class DynamicTextBox extends LinearLayout implements DynamicView {

    EditText editText;
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
        editText = findViewById(R.id.text_input_edit);
    }

    @Override
    public String getDataType() {
        return fieldType;
    }

    public Object getValue() {
        String value = editText.getText().toString();
        if (getDataType().equalsIgnoreCase("simpleType")) {
            Map<String, String> map = new HashMap<>();
            map.put("eng", value);
            return map;
        }
        return value;
    }


    public EditText getEditText() {
        return editText;
    }
    public void setTextChangeListener(TextWatcher textWatcher){
        editText.addTextChangedListener(textWatcher);
    }
}
