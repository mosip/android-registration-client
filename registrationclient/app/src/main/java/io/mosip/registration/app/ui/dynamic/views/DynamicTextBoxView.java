package io.mosip.registration.app.ui.dynamic.views;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import io.mosip.registration.app.ui.dynamic.DynamicView;
import io.mosip.registration.app.R;

public class DynamicTextBoxView extends LinearLayout implements DynamicView {

    final int layoutId = R.layout.dynamic_text_box_view;
    EditText editText;


    public DynamicTextBoxView(Context context) {
        super(context);
        this.init(context);
    }

    public DynamicTextBoxView(Context context, EditText editText) {
        super(context);
        this.editText = editText;
        this.init(context);

    }

    public DynamicTextBoxView(Context context, @Nullable AttributeSet attrs, EditText editText) {
        super(context, attrs);
        this.editText = editText;
        this.init(context);

    }

    public DynamicTextBoxView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, EditText editText) {
        super(context, attrs, defStyleAttr);
        this.editText = editText;
        this.init(context);

    }

    public DynamicTextBoxView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, EditText editText) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.editText = editText;
        this.init(context);

    }

    private void init(Context context) {
        inflate(context, layoutId, this);
        initComponents();

    }

    private void initComponents() {

        editText = findViewById(R.id.text_input_edit);
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