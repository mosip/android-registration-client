package io.mosip.registration.app.dynamicviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import io.mosip.registration.app.R;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.spi.MasterDataService;

import java.util.ArrayList;
import java.util.List;

public class DynamicDropDownBox extends LinearLayout implements DynamicView {

    List<String> languages = null;
    FieldSpecDto fieldSpecDto = null;
    MasterDataService masterDataService;
    final int layoutId = R.layout.dynamic_dropdown_box;

    public DynamicDropDownBox(Context context, FieldSpecDto fieldSpecDto, List<String> languages,
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
        ((TextView)findViewById(R.id.dropdown_label)).setText(String.join("/", labels));

        List<String> items = (fieldSpecDto.getFieldType().equalsIgnoreCase("dynamic")) ?
                this.masterDataService.getFieldValues(fieldSpecDto.getSubType(), languages.get(0)):
                this.masterDataService.findLocationByHierarchyLevel(fieldSpecDto.getSubType(), languages.get(0));

        @SuppressLint("ResourceType")
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,
                items == null ? new ArrayList<>() : items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.dropdown_input);
        sItems.setAdapter(adapter);
    }

    @Override
    public String getDataType() {
        return fieldSpecDto.getType();
    }

    public String getValue(){
        Spinner sItems = (Spinner) findViewById(R.id.dropdown_input);
        return sItems.getSelectedItem().toString();
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
