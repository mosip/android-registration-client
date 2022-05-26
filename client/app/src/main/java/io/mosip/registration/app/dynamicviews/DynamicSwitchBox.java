package io.mosip.registration.app.dynamicviews;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import io.mosip.registration.app.R;
import io.mosip.registration.app.util.ClientConstants;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.util.UserInterfaceHelperService;

import static io.mosip.registration.app.util.ClientConstants.FIELD_LABEL_TEMPLATE;
import static io.mosip.registration.app.util.ClientConstants.REQUIRED_FIELD_LABEL_TEMPLATE;

public class DynamicSwitchBox extends LinearLayout implements DynamicView {

    private static final String TAG = DynamicSwitchBox.class.getSimpleName();

    String selected = null;
    List<Button> allOptions = new ArrayList<>();
    MasterDataService masterDataService;

    RegistrationDto registrationDto = null;
    FieldSpecDto fieldSpecDto = null;
    final int layoutId=R.layout.dynamic_switch_box;

    public DynamicSwitchBox(Context context, FieldSpecDto fieldSpecDto, RegistrationDto registrationDto,
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
        ((TextView)findViewById(R.id.switch_label)).setText(Html.fromHtml(isRequired() ?
                String.format(REQUIRED_FIELD_LABEL_TEMPLATE, String.join(ClientConstants.LABEL_SEPARATOR, labels)) :
                String.format(FIELD_LABEL_TEMPLATE, String.join(ClientConstants.LABEL_SEPARATOR, labels)), 1));

        ViewGroup viewGroup = findViewById(R.id.option_holder_panel);
        List<String> options = masterDataService.getFieldValues(fieldSpecDto.getId(), registrationDto.getSelectedLanguages().get(0));

        for(String option : options) {
            Button button = new Button(context);
            button.setText(option);
            button.setBackground(getResources().getDrawable(R.drawable.button_option_default));
            LayoutParams param=new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            param.setMargins(10, 0, 10,0);
            button.setLayoutParams(param);
            viewGroup.addView(button);
            allOptions.add(button);

            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(Button btn:allOptions) {
                        btn.setBackground(getResources().getDrawable(
                                (btn.getText().toString().equalsIgnoreCase(button.getText().toString()) ?
                                        R.drawable.button_option_selected : R.drawable.button_option_default)));
                    }
                    registrationDto.addDemographicField(fieldSpecDto.getId(), button.getText().toString());
                    selected = button.getText().toString();
                }
            });
        }
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
