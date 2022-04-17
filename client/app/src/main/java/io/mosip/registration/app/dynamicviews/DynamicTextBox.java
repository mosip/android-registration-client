package io.mosip.registration.app.dynamicviews;

import android.content.Context;
import android.graphics.Color;
import android.text.InputFilter;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import io.mosip.registration.app.R;
import io.mosip.registration.app.activites.ScreenActivity;
import io.mosip.registration.app.util.InputTextRegexFilter;
import io.mosip.registration.clientmanager.dto.registration.GenericDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldValidatorDto;

import java.util.*;

public class DynamicTextBox extends LinearLayout implements DynamicView {

    List<String> languages = null;
    FieldSpecDto fieldSpecDto = null;
    final int layoutId=R.layout.dynamic_text_box;

    public DynamicTextBox(Context context, FieldSpecDto fieldSpecDto, List<String> languages) {
        super(context);
        this.fieldSpecDto = fieldSpecDto;
        this.languages = languages;
        initializeView(context);
    }

    private void initializeView(Context context) {
        inflate(context, layoutId , this);
        this.setTag(fieldSpecDto.getId());

        //TODO need to filter based on language
        Optional<FieldValidatorDto> result = (fieldSpecDto.getValidators() != null) ? fieldSpecDto.getValidators()
                .stream()
                .filter(v -> v.getType().equalsIgnoreCase("regex"))
                .findFirst() : Optional.empty();

        List<String> labels = new ArrayList<>();
        for(String language : languages) {
            labels.add(fieldSpecDto.getLabel().get(language));
        }
        ((TextView)findViewById(R.id.text_label)).setText(String.join("/", labels));

        EditText editText = findViewById(R.id.custom_edit_text);
        editText.setTag(languages.get(0));

        if(result.isPresent()) {
            editText.setFilters(new InputFilter[] { new InputTextRegexFilter(
                    (TextInputLayout) findViewById(R.id.text_input_layout), result.get().getValidator())});
        }

        setVisibility();
    }


    private void setVisibility() {
        if(fieldSpecDto.getVisible() != null && fieldSpecDto.getVisible().getEngine().equalsIgnoreCase("jexl")) {
            //TODO
            boolean isVisible = true;
            setVisibility(isVisible ? VISIBLE : GONE);
        }
    }

    @Override
    public String getDataType() {
        return fieldSpecDto.getType();
    }

    public Object getValue() {
        String value = ((TextView)findViewWithTag(languages.get(0))).getText().toString();
        //TODO consider all languages
        if (getDataType().equalsIgnoreCase("simpleType")) {
            return new GenericDto(value, "eng");
        }
        return value;
    }

    @Override
    public void setValue() {
    }

    @Override
    public boolean isValidValue() {
        return false;
    }

    @Override
    public void hide() {

    }

    @Override
    public void unHide() {

    }
}
