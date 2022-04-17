package io.mosip.registration.app.dynamicviews;

import android.content.Context;
import android.text.InputFilter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.mosip.registration.app.R;
import io.mosip.registration.app.util.InputTextRegexFilter;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.spi.MasterDataService;

import java.util.ArrayList;
import java.util.List;

public class DynamicAgeDateBox extends LinearLayout implements DynamicView {
    EditText dateBox;
    EditText monthBox;
    EditText yearBox;
    EditText ageBox;

    List<String> languages = null;
    FieldSpecDto fieldSpecDto = null;
    MasterDataService masterDataService;
    final int layoutId = R.layout.dynamic_agedate_box;

    public DynamicAgeDateBox(Context context, FieldSpecDto fieldSpecDto, List<String> languages,
                              MasterDataService masterDataService) {
        super(context);
        this.fieldSpecDto = fieldSpecDto;
        this.languages = languages;
        this.masterDataService = masterDataService;
        initializeView(context);
    }

    private void initializeView(Context context) {
        inflate(context, layoutId, this);
        this.setTag(fieldSpecDto.getId());

        List<String> labels = new ArrayList<>();
        for(String language : languages) {
            labels.add(fieldSpecDto.getLabel().get(language));
        }
        ((TextView)findViewById(R.id.dob_label)).setText(String.join("/", labels));

        //TODO re-arrange / build it based on date format from UI spec
        dateBox = findViewById(R.id.dob_date);
        dateBox.setFilters(new InputFilter[] { new InputTextRegexFilter(dateBox, "^(0[1-9]|1[0-9]|2[0-9]|3[01])$")});

        monthBox= findViewById(R.id.dob_month);
        monthBox.setFilters(new InputFilter[] { new InputTextRegexFilter(monthBox, "^(0[1-9]|1[012])$")});

        yearBox= findViewById(R.id.dob_year);
        ageBox= findViewById(R.id.dob_age);
        ageBox.setFilters(new InputFilter[] { new InputTextRegexFilter(monthBox, "^(0[1-9]|1[012])$")});
    }

    @Override
    public String getDataType() {
        return fieldSpecDto.getType();
    }

    public String getValue() {
        //TODO construct based on date format
        return dateBox.getText().toString()+"/"+monthBox.getText().toString()+"/"+yearBox.getText().toString();
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
