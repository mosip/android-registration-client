package io.mosip.registration.app.dynamicviews;

import android.content.Context;
import android.os.LocaleList;
import android.text.*;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import io.mosip.registration.app.R;
import io.mosip.registration.app.util.ClientConstants;
import io.mosip.registration.app.util.InputTextRegexFilter;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldValidatorDto;
import io.mosip.registration.clientmanager.repository.LanguageRepository;
import io.mosip.registration.clientmanager.util.UserInterfaceHelperService;

import java.util.*;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static io.mosip.registration.app.util.ClientConstants.FIELD_LABEL_TEMPLATE;
import static io.mosip.registration.app.util.ClientConstants.REQUIRED_FIELD_LABEL_TEMPLATE;

public class DynamicTextBox extends LinearLayout implements DynamicView {

    private static final String TAG = DynamicTextBox.class.getSimpleName();
    private static final String SIMPLE_TYPE = "simpleType";
    private static final String LAYOUT_TAG_PREFIX = "layout-";
    private static final String EDITTEXT_TAG_PREFIX = "text-";

    RegistrationDto registrationDto = null;
    FieldSpecDto fieldSpecDto = null;
    LanguageRepository languageRepository;
    final int layoutId=R.layout.dynamic_text_box;

    public DynamicTextBox(Context context, FieldSpecDto fieldSpecDto, RegistrationDto registrationDto,
                          LanguageRepository languageRepository) {
        super(context);
        this.fieldSpecDto = fieldSpecDto;
        this.registrationDto = registrationDto;
        this.languageRepository = languageRepository;
        initializeView(context);
    }

    private void initializeView(Context context) {
        View view = inflate(context, layoutId , this);
        this.setTag(fieldSpecDto.getId());
        LinearLayout linearLayout = view.findViewById(R.id.lang_text_boxes);

        List<String> labels = new ArrayList<>();
        for(String language : registrationDto.getSelectedLanguages()) {
            labels.add(fieldSpecDto.getLabel().get(language));
        }

        boolean inputMethodPromptRequired = registrationDto.getSelectedLanguages().size() > 1 ? true : false;
        if(getDataType().equalsIgnoreCase(SIMPLE_TYPE))
            registrationDto.getSelectedLanguages().forEach(lang -> {linearLayout.addView(addTextInputLayout(lang, inputMethodPromptRequired));});
        else
            linearLayout.addView(addTextInputLayout(null, inputMethodPromptRequired));

        ((TextView)findViewById(R.id.text_label)).setText(Html.fromHtml(isRequired() ?
                String.format(REQUIRED_FIELD_LABEL_TEMPLATE, String.join(ClientConstants.LABEL_SEPARATOR, labels)) :
                String.format(FIELD_LABEL_TEMPLATE, String.join(ClientConstants.LABEL_SEPARATOR, labels)), 1));

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
        if(getDataType().equalsIgnoreCase(SIMPLE_TYPE)) {
            for(String language : registrationDto.getSelectedLanguages()) {
                if(!nonEmptyAndValidValue(language))
                    return false;
            }
            return true;
        }
        return nonEmptyAndValidValue(null);
    }

    private boolean nonEmptyAndValidValue(String language) {
        String fieldTag = fieldSpecDto.getId()+(language == null ? RegistrationConstants.EMPTY_STRING : language);
        EditText editText = findViewWithTag(EDITTEXT_TAG_PREFIX+fieldTag);
        TextInputLayout textInputLayout = findViewWithTag(LAYOUT_TAG_PREFIX+fieldTag);
        String value = editText.getText().toString();
        return value != null && !value.trim().isEmpty() && !textInputLayout.isErrorEnabled();
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

    private TextWatcher getTextWatcher(String languageCode) {
        return new TextWatcher() {

            private String fieldLanguage = languageCode;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(getDataType().equalsIgnoreCase(SIMPLE_TYPE)) {
                    registrationDto.addDemographicField(fieldSpecDto.getId(), s.toString(), fieldLanguage);
                }
                else {
                    registrationDto.addDemographicField(fieldSpecDto.getId(), s.toString());
                }
            }
        };
    }

    private TextInputLayout addTextInputLayout(String languageCode, boolean inputMethodRequired) {
        String fieldTag = fieldSpecDto.getId()+(languageCode == null ? RegistrationConstants.EMPTY_STRING : languageCode);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        TextInputLayout textInputLayout = new TextInputLayout(new ContextThemeWrapper(getContext(), R.style.Theme_registration_client));
        textInputLayout.setLayoutParams(layoutParams);
        textInputLayout.setTag(LAYOUT_TAG_PREFIX+fieldTag);

        EditText textInputEditText = new EditText(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        textInputEditText.setLayoutParams(params);
        textInputEditText.setFocusable(true);
        textInputEditText.setFocusableInTouchMode(true);
        textInputEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        textInputEditText.setTextLocale(getLocale(languageCode));
        textInputEditText.setTag(EDITTEXT_TAG_PREFIX+fieldTag);
        textInputEditText.addTextChangedListener(getTextWatcher(languageCode));

        //if(inputMethodRequired) {
            /*textInputEditText.setOnFocusChangeListener((v, hasFocus) -> {
                if(hasFocus) {
                    InputMethodManager imeManager = (InputMethodManager)
                            getContext().getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                    imeManager.showInputMethodPicker();
                }
            });*/
            textInputEditText.setHint(languageRepository.getNativeName(languageCode));
            textInputEditText.setImeHintLocales(new LocaleList(getLocale(languageCode)));
            InputMethodManager imeManager = (InputMethodManager)
                    getContext().getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
            imeManager.restartInput(textInputEditText);
        /*}
        else {
            InputMethodManager imeManager = (InputMethodManager)
                    getContext().getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
            imeManager.restartInput(this);
        }*/

        Optional<FieldValidatorDto> result = (fieldSpecDto.getValidators() != null) ? fieldSpecDto.getValidators()
                .stream()
                .filter(v -> v.getType().equalsIgnoreCase("regex")
                        && v.getLangCode() != null && v.getLangCode().equalsIgnoreCase(languageCode))
                .findFirst() : Optional.empty();

        if(!result.isPresent()) {
            result = (fieldSpecDto.getValidators() != null) ? fieldSpecDto.getValidators()
                    .stream()
                    .filter(v -> v.getType().equalsIgnoreCase("regex"))
                    .findFirst() : Optional.empty();
        }

        if(result.isPresent()) {
            textInputEditText.setFilters(new InputFilter[] { new InputTextRegexFilter(textInputLayout,
                    result.get().getValidator())});
        }

        textInputLayout.addView(textInputEditText);
        return textInputLayout;
    }

    private Locale getLocale(String languageCode) {
        if(languageCode == null)
            return Locale.ENGLISH;

        switch (languageCode.toLowerCase()) {
            case "fra" : return Locale.FRENCH;
            case "ara" : return new Locale("ar");
            case "tam" : return new Locale("ta", "IN");
            case "kan" : return new Locale("kn", "IN");
            case "hin" : return new Locale("hi", "IN");
        }
        return Locale.ENGLISH;
    }

}
