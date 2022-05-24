package io.mosip.registration.app.dynamicviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.*;

import io.mosip.registration.app.R;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.util.UserInterfaceHelperService;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static io.mosip.registration.app.util.ClientConstants.FIELD_LABEL_TEMPLATE;
import static io.mosip.registration.app.util.ClientConstants.REQUIRED_FIELD_LABEL_TEMPLATE;

public class DynamicDropDownBox extends LinearLayout implements DynamicView {

    private static final String TAG = DynamicDropDownBox.class.getSimpleName();

    String selected = null;
    RegistrationDto registrationDto = null;
    FieldSpecDto fieldSpecDto = null;
    MasterDataService masterDataService;
    final int layoutId = R.layout.dynamic_dropdown_box;

    public DynamicDropDownBox(Context context, FieldSpecDto fieldSpecDto, RegistrationDto registrationDto,
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
        ((TextView)findViewById(R.id.dropdown_label)).setText(Html.fromHtml(isRequired() ?
                String.format(REQUIRED_FIELD_LABEL_TEMPLATE, String.join("/", labels)) :
                String.format(FIELD_LABEL_TEMPLATE, String.join("/", labels)), 1));

        List<String> items = (fieldSpecDto.getFieldType().equalsIgnoreCase("dynamic")) ?
                this.masterDataService.getFieldValues(fieldSpecDto.getSubType(),
                        registrationDto.getSelectedLanguages().get(0)):
                this.masterDataService.findLocationByHierarchyLevel(fieldSpecDto.getSubType(),
                        registrationDto.getSelectedLanguages().get(0));

        @SuppressLint("ResourceType")
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,
                items == null ? new ArrayList<>() : items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.dropdown_input);
        sItems.setAdapter(adapter);

        sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected = items.get(position);
                registrationDto.addDemographicField(fieldSpecDto.getId(), selected);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected = null;
                registrationDto.removeDemographicField(fieldSpecDto.getId());
            }
        });
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
        return selected != null;
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
}
