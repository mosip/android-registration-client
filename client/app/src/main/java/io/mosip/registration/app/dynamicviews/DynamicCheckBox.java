package io.mosip.registration.app.dynamicviews;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import io.mosip.registration.app.R;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.util.UserInterfaceHelperService;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import static io.mosip.registration.app.util.ClientConstants.FIELD_LABEL_TEMPLATE;
import static io.mosip.registration.app.util.ClientConstants.REQUIRED_FIELD_LABEL_TEMPLATE;

public class DynamicCheckBox extends LinearLayout implements DynamicView {

    private static final String TAG = DynamicCheckBox.class.getSimpleName();

    String selected = null;
    RegistrationDto registrationDto = null;
    FieldSpecDto fieldSpecDto = null;
    final int layoutId= R.layout.dynamic_check_box;

    public DynamicCheckBox(Context context, FieldSpecDto fieldSpecDto, RegistrationDto registrationDto) {
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

        CheckBox checkBox = findViewById(R.id.checkBox);
        checkBox.setText(Html.fromHtml(isRequired() ?
                String.format(REQUIRED_FIELD_LABEL_TEMPLATE, String.join("/", labels)) :
                String.format(FIELD_LABEL_TEMPLATE, String.join("/", labels)), 1));


        checkBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selected = checkBox.isSelected() ? "Y" : "N";
                registrationDto.addDemographicField(fieldSpecDto.getId(), selected);
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
