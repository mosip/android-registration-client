package io.mosip.registration.app.ui.dynamic.views;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;

import io.mosip.registration.app.R;
import io.mosip.registration.app.ui.dynamic.DynamicView;

public class DynamicTextBox extends LinearLayout implements DynamicView {
    EditText editText;
    String languageCode="";
    String labelText="";
    String validationRule="";
    final int layoutId=R.layout.dynamic_text_box;

    public DynamicTextBox(Context context,String langCode,String label,String validation) {
        super(context);


        languageCode=langCode;
        labelText=label;
        validationRule=validation;
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
        inflate(context,layoutId , this);
        initComponents();
   }

    private void initComponents() {
        editText = findViewById(R.id.text_input_edit);
        //editText.setHint(labelText);
        //((TextInputLayout)findViewById(R.id.text_input_edit_layout)).setHint(labelText);
    }

    public String getValue(){
        return editText.getText().toString();
    }

    @Override
    public void setValue(String value) {
        editText.setText(value);
    }

    public EditText getEditText() {
        return editText;
    }
    public void setTextChangeListener(TextWatcher textWatcher){
        editText.addTextChangedListener(textWatcher);
    }
}
