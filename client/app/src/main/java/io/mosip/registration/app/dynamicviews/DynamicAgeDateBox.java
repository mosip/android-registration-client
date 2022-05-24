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
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldValidatorDto;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.util.UserInterfaceHelperService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static io.mosip.registration.app.util.ClientConstants.FIELD_LABEL_TEMPLATE;
import static io.mosip.registration.app.util.ClientConstants.REQUIRED_FIELD_LABEL_TEMPLATE;

public class DynamicAgeDateBox extends LinearLayout implements DynamicView {

    private static final String TAG = DynamicAgeDateBox.class.getSimpleName();

    EditText dateBox;
    EditText monthBox;
    EditText yearBox;
    EditText ageBox;

    RegistrationDto registrationDto = null;
    FieldSpecDto fieldSpecDto = null;
    MasterDataService masterDataService;
    final int layoutId = R.layout.dynamic_agedate_box;

    public DynamicAgeDateBox(Context context, FieldSpecDto fieldSpecDto, RegistrationDto registrationDto,
                              MasterDataService masterDataService) {
        super(context);
        this.fieldSpecDto = fieldSpecDto;
        this.registrationDto = registrationDto;
        this.masterDataService = masterDataService;
        initializeView(context);
    }

    private void initializeView(Context context) {
        inflate(context, layoutId, this);
        this.setTag(fieldSpecDto.getId());

        List<String> labels = new ArrayList<>();
        for(String language : registrationDto.getSelectedLanguages()) {
            labels.add(fieldSpecDto.getLabel().get(language));
        }
        ((TextView)findViewById(R.id.dob_label)).setText(Html.fromHtml(isRequired() ?
                String.format(REQUIRED_FIELD_LABEL_TEMPLATE, String.join("/", labels)) :
                String.format(FIELD_LABEL_TEMPLATE, String.join("/", labels)), 1));

        //TODO re-arrange / build it based on date format from UI spec
        dateBox = findViewById(R.id.dob_date);
        dateBox.setFilters(new InputFilter[] { new InputFilter.LengthFilter(2),
                new InputTextRegexFilter((TextInputLayout) findViewById(R.id.text_input_layout_dd),
                        "^(0[1-9]|1[0-9]|2[0-9]|3[01])$")});
        dateBox.addTextChangedListener(getDateTextWatcher());

        monthBox= findViewById(R.id.dob_month);
        monthBox.setFilters(new InputFilter[] { new InputFilter.LengthFilter(2), new InputTextRegexFilter(
                (TextInputLayout) findViewById(R.id.text_input_layout_mm), "^(0[1-9]|1[012])$")});
        monthBox.addTextChangedListener(getDateTextWatcher());

        yearBox= findViewById(R.id.dob_year);
        yearBox.setFilters(new InputFilter[] { new InputFilter.LengthFilter(4), new InputTextRegexFilter(
                (TextInputLayout) findViewById(R.id.text_input_layout_yy), "^(19[0-9]{2}|2[0-9]{3})$")});
        yearBox.addTextChangedListener(getDateTextWatcher());

        ageBox= findViewById(R.id.dob_age);
        ageBox.setFilters(new InputFilter[] { new InputFilter.LengthFilter(3), new InputTextRegexFilter(
                (TextInputLayout) findViewById(R.id.text_input_layout_age), "[0-9]{1,3}")});
        ageBox.addTextChangedListener(getAgeTextWatcher());
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
        return !((TextInputLayout) findViewById(R.id.text_input_layout_dd)).isErrorEnabled() &&
                !((TextInputLayout) findViewById(R.id.text_input_layout_mm)).isErrorEnabled() &&
                !((TextInputLayout) findViewById(R.id.text_input_layout_yy)).isErrorEnabled() &&
                !((TextInputLayout) findViewById(R.id.text_input_layout_age)).isErrorEnabled();
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

    private TextWatcher getDateTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    registrationDto.setDateField(fieldSpecDto.getId(), fieldSpecDto.getSubType(), dateBox.getText().toString(),
                            monthBox.getText().toString(), yearBox.getText().toString());
                } catch (Exception e) {
                    Log.i(TAG, "Failed to set date", e);
                }
            }
        };
    }

    private TextWatcher getAgeTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String ageString = ageBox.getText().toString();
                if(!ageString.isEmpty()) {
                    Calendar defaultDate = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("UTC")));
                    defaultDate.set(Calendar.DATE, 1);
                    defaultDate.set(Calendar.MONTH, 0);
                    defaultDate.add(Calendar.YEAR, -Integer.parseInt(ageString));

                    dateBox.setText(0+String.valueOf(defaultDate.get(Calendar.DATE)));
                    monthBox.setText(0+String.valueOf(defaultDate.get(Calendar.MONTH)+ 1));
                    yearBox.setText(String.valueOf(defaultDate.get(Calendar.YEAR)));
                }
                else {
                    dateBox.setText("");
                    monthBox.setText("");
                    yearBox.setText("");
                }
            }
        };
    }
}
