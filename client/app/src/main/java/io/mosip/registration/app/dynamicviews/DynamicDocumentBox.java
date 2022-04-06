package io.mosip.registration.app.dynamicviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import io.mosip.registration.app.R;

import java.util.ArrayList;
import java.util.List;

public class DynamicDocumentBox extends LinearLayout implements DynamicView  {

    String languageCode="";
    String labelText="";
    String validationRule="";
    final int layoutId = R.layout.dynamic_document_box;

    public DynamicDocumentBox(Context context,String langCode,String label,String validation) {
        super(context);
        languageCode=langCode;
        labelText=label;
        validationRule=validation;
        init(context);
    }

    private void init(Context context) {
        inflate(context, layoutId, this);
        ((TextView)findViewById(R.id.document_label)).setText(labelText);
        initComponents(context);
    }

    private void initComponents(Context context) {
        List<String> items = new ArrayList<>();
        items.add("Doc type 1");
        items.add("Doc type 2");
        items.add("Doc type 3");
        items.add("Doc type 4");

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
        return sItems.getSelectedItem().toString();
    }

    @Override
    public void setValue() {
    }

    @Override
    public boolean validValue() {
        return true;
    }
}
