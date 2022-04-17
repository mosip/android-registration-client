package io.mosip.registration.app.dynamicviews;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.*;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;
import io.mosip.registration.app.R;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.spi.MasterDataService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DynamicDocumentBox extends LinearLayout implements DynamicView  {

    List<String> languages = null;
    FieldSpecDto fieldSpecDto = null;
    MasterDataService masterDataService;
    final int layoutId = R.layout.dynamic_document_box;

    public DynamicDocumentBox(Context context, FieldSpecDto fieldSpecDto, List<String> languages,
                              MasterDataService masterDataService) {
        super(context);
        this.fieldSpecDto = fieldSpecDto;
        this.languages = languages;
        this.masterDataService = masterDataService;
        initializeView(context);
    }


    private void initializeView(Context context) {
        List<String> labels = new ArrayList<>();
        for(String language : languages) {
            labels.add(fieldSpecDto.getLabel().get(language));
        }
        inflate(context, layoutId, this);
        this.setTag(fieldSpecDto.getId());

        ((TextView)findViewById(R.id.document_label)).setText(String.join("/", labels));

        //TODO derive applicant type code
        List<String> items = this.masterDataService.getDocumentTypes(fieldSpecDto.getSubType(),
                "011", languages.get(0));

        @SuppressLint("ResourceType")
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.doctypes_dropdown);
        sItems.setAdapter(adapter);
    }

    @Override
    public String getDataType() {
        return fieldSpecDto.getType();
    }

    @Override
    public Object getValue() {
        Spinner sItems = (Spinner) findViewById(R.id.doctypes_dropdown);
        if(sItems.getSelectedItem() != null)
            return sItems.getSelectedItem().toString();
        return null;
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
