package io.mosip.registration.app.dynamicviews;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import io.mosip.registration.app.R;
import io.mosip.registration.app.util.ClientConstants;
import io.mosip.registration.clientmanager.constant.Modality;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.util.UserInterfaceHelperService;

import java.util.*;

import static io.mosip.registration.app.util.ClientConstants.FIELD_LABEL_TEMPLATE;
import static io.mosip.registration.app.util.ClientConstants.REQUIRED_FIELD_LABEL_TEMPLATE;

public class DynamicBiometricsBox extends LinearLayout implements DynamicView {

    private static final String TAG = DynamicBiometricsBox.class.getSimpleName();

    RegistrationDto registrationDto = null;
    FieldSpecDto fieldSpecDto = null;
    List<String> bioAttributes = Collections.emptyList();
    final int layoutId = R.layout.dynamic_biometrics_box;

    public DynamicBiometricsBox(Context context, FieldSpecDto fieldSpecDto, RegistrationDto registrationDto) {
        super(context);
        this.fieldSpecDto = fieldSpecDto;
        this.registrationDto = registrationDto;
        initializeView(context);
    }

    private void initializeView(Context context) {
        inflate(context, layoutId, this);
        this.setTag(fieldSpecDto.getId());

        List<String> labels = new ArrayList<>();
        for(String language : registrationDto.getSelectedLanguages()) {
            labels.add(fieldSpecDto.getLabel().get(language));
        }
        ((TextView)findViewById(R.id.biometric_label)).setText(Html.fromHtml(isRequired() ?
                String.format(REQUIRED_FIELD_LABEL_TEMPLATE, String.join(ClientConstants.LABEL_SEPARATOR, labels)) :
                String.format(FIELD_LABEL_TEMPLATE, String.join(ClientConstants.LABEL_SEPARATOR, labels)), 1));

        bioAttributes = UserInterfaceHelperService.getRequiredBioAttributes(fieldSpecDto,
                registrationDto.getMVELDataContext());

        this.setVisibility((UserInterfaceHelperService.isFieldVisible(fieldSpecDto, registrationDto.getMVELDataContext())) ?
                VISIBLE : GONE);
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
        return registrationDto.hasBiometric(fieldSpecDto.getId(), "face");
    }

    @Override
    public boolean isRequired() {
        return !bioAttributes.isEmpty();
    }

    @Override
    public void update(Observable o, Object arg) {
        if(UserInterfaceHelperService.isFieldVisible(fieldSpecDto, registrationDto.getMVELDataContext())) {
            this.setVisibility(VISIBLE);
        }
        else {
            registrationDto.removeBiometricField(fieldSpecDto.getId());
            this.setVisibility(GONE);
        }
    }

    private void checkModality(List<String> configuredBioAttributes, Modality modality) {

    }
}
