package io.mosip.registration.app.dynamicviews;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import io.mosip.registration.app.R;
import io.mosip.registration.app.dynamicviews.DynamicView;

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
        initComponents();
    }

    private void initComponents() {

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
        return null;
    }
}
