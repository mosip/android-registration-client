package io.mosip.registration.app.dynamicviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import io.mosip.registration.app.R;
import io.mosip.registration.clientmanager.spi.MasterDataService;

import java.util.ArrayList;
import java.util.List;

public class DynamicDocumentBox extends LinearLayout implements DynamicView  {

    String languageCode="";
    String labelText="";
    String validationRule="";
    final int layoutId = R.layout.dynamic_document_box;
    MasterDataService masterDataService;
    String subType = "";

    public DynamicDocumentBox(Context context,String langCode,String label,String validation,
                              MasterDataService masterDataService, String subType) {
        super(context);
        languageCode=langCode;
        labelText=label;
        validationRule=validation;
        this.masterDataService = masterDataService;
        this.subType = subType;
        init(context);
    }

    private void init(Context context) {
        inflate(context, layoutId, this);
        ((TextView)findViewById(R.id.document_label)).setText(labelText);
        initComponents(context);
    }

    private void initComponents(Context context) {
        //TODO derive applicant type code
        List<String> items = this.masterDataService.getDocumentTypes(subType, "011", "eng");

        @SuppressLint("ResourceType")
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.doctypes_dropdown);
        sItems.setAdapter(adapter);
    }

    public DynamicDocumentBox(Context context) {
        super(context);
    }

    @Override
    public String getDataType() {
        return "documentType";
    }

    @Override
    public Object getValue() {
        Spinner sItems = (Spinner) findViewById(R.id.doctypes_dropdown);
        if(sItems.getSelectedItem() == null)
            return sItems.getSelectedItem().toString();
        return null;
    }

    @Override
    public void setValue() {
    }

    @Override
    public boolean validValue() {
        return true;
    }
}
