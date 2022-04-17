package io.mosip.registration.app.dynamicviews;

import android.content.Context;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import io.mosip.registration.app.R;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;

import java.util.ArrayList;
import java.util.List;

public class DynamicBiometricsBox extends LinearLayout implements DynamicView {

    List<String> languages = null;
    FieldSpecDto fieldSpecDto = null;
    final int layoutId = R.layout.dynamic_biometrics_box;

    public DynamicBiometricsBox(Context context, FieldSpecDto fieldSpecDto, List<String> languages) {
        super(context);
        this.fieldSpecDto = fieldSpecDto;
        this.languages = languages;
        initializeView(context);
    }

    private void initializeView(Context context) {
        inflate(context, layoutId, this);
        this.setTag(fieldSpecDto.getId());

        List<String> labels = new ArrayList<>();
        for(String language : languages) {
            labels.add(fieldSpecDto.getLabel().get(language));
        }
        ((TextView)findViewById(R.id.biometric_label)).setText(String.join("/", labels));
    }

    @Override
    public String getDataType() {
        return fieldSpecDto.getType();
    }

    @Override
    public Object getValue() {
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
