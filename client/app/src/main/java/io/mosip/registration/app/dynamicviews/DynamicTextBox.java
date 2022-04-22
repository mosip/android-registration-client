package io.mosip.registration.app.dynamicviews;

import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;

import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import io.mosip.registration.app.R;
import io.mosip.registration.app.util.InputTextRegexFilter;
import io.mosip.registration.clientmanager.dto.registration.GenericDto;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldValidatorDto;
import io.mosip.registration.clientmanager.util.UserInterfaceHelperService;

import java.util.*;

import static io.mosip.registration.app.util.ClientConstants.FIELD_LABEL_TEMPLATE;
import static io.mosip.registration.app.util.ClientConstants.REQUIRED_FIELD_LABEL_TEMPLATE;

public class DynamicTextBox extends LinearLayout implements DynamicView {

    private static final String TAG = DynamicTextBox.class.getSimpleName();

    RegistrationDto registrationDto = null;
    FieldSpecDto fieldSpecDto = null;
    final int layoutId=R.layout.dynamic_text_box;

    public DynamicTextBox(Context context, FieldSpecDto fieldSpecDto, RegistrationDto registrationDto) {
        super(context);
        this.fieldSpecDto = fieldSpecDto;
        this.registrationDto = registrationDto;
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
        for(String language : registrationDto.getSelectedLanguages()) {
            labels.add(fieldSpecDto.getLabel().get(language));
        }
        ((TextView)findViewById(R.id.text_label)).setText(Html.fromHtml(isRequired() ?
                String.format(REQUIRED_FIELD_LABEL_TEMPLATE, String.join("/", labels)) :
                String.format(FIELD_LABEL_TEMPLATE, String.join("/", labels)), 1));

        EditText editText = findViewById(R.id.custom_edit_text);
        editText.setTag(registrationDto.getSelectedLanguages().get(0));
        editText.addTextChangedListener(getTextWatcher());

        if(result.isPresent()) {
            editText.setFilters(new InputFilter[] { new InputTextRegexFilter(
                    (TextInputLayout) findViewById(R.id.text_input_layout), result.get().getValidator())});
        }

        this.setVisibility((UserInterfaceHelperService.isFieldVisible(fieldSpecDto, registrationDto.getMVELDataContext())) ?
                VISIBLE : GONE);
    }

    @Override
    public String getDataType() {
        return fieldSpecDto.getType();
    }

    @Override
    public void setValue() {
    }

    @Override
    public boolean isValidValue() {
        return !((TextInputLayout) findViewById(R.id.text_input_layout)).isErrorEnabled();
    }

    @Override
    public boolean isRequired() {
        return UserInterfaceHelperService.isRequiredField(fieldSpecDto, registrationDto.getMVELDataContext());
    }

    @Override
    public void update(Observable o, Object arg) {
        if(UserInterfaceHelperService.isFieldVisible(fieldSpecDto, registrationDto.getMVELDataContext())) {
            this.setVisibility(VISIBLE);
        }
        else {
            registrationDto.removeDemographicField(fieldSpecDto.getId());
            this.setVisibility(GONE);
        }
    }

    private TextWatcher getTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(getDataType().equalsIgnoreCase("simpleType")) {
                    registrationDto.addDemographicField(fieldSpecDto.getId(),
                            ((EditText)findViewById(R.id.custom_edit_text)).getText().toString(), "eng");
                }
                else {
                    registrationDto.addDemographicField(fieldSpecDto.getId(),
                            ((EditText)findViewById(R.id.custom_edit_text)).getText().toString());
                }
            }
        };
    }
}
