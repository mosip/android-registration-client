package io.mosip.registration.app.dynamicviews;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import io.mosip.registration.app.R;
import io.mosip.registration.app.dynamicviews.DynamicView;

public class DynamicBiometricsBox extends LinearLayout implements DynamicView {

    String languageCode="";
    String labelText="";
    String validationRule="";
    long identifier;
    final int layoutId = R.layout.dynamic_biometrics_box;

    public DynamicBiometricsBox(Context context,String langCode,String label,String validation) {
        super(context);

        languageCode=langCode;
        labelText=label;
        validationRule=validation;
        identifier=System.nanoTime();
        init(context);
    }

    private void init(Context context) {
        inflate(context, layoutId, this);
        //((TextView)findViewById(R.id.biometrics_label)).setText(labelText);
        initComponents();
    }

    private void initComponents() {

    }

    public DynamicBiometricsBox(Context context) {
        super(context);
    }

    @Override
    public String getDataType() {
        return "biometricsType";
    }

    @Override
    public Object getValue() {
        return null;
    }
}
