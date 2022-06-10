package io.mosip.registration.app.dynamicviews;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.*;

import io.mosip.registration.app.R;
import io.mosip.registration.app.util.ClientConstants;
import io.mosip.registration.clientmanager.dto.registration.GenericValueDto;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.util.UserInterfaceHelperService;

import static io.mosip.registration.app.util.ClientConstants.*;

public class DynamicSwitchBox extends LinearLayout implements DynamicView {

    private static final String TAG = DynamicSwitchBox.class.getSimpleName();

    GenericValueDto selected = null;
    Map<Button, GenericValueDto> allOptions = new HashMap();
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
        List<GenericValueDto> options = masterDataService.getFieldValues(fieldSpecDto.getId(), registrationDto.getSelectedLanguages().get(0));

        for(GenericValueDto option : options) {
            Button button = new Button(context);
            button.setText(option.getName());
            button.setBackground(getResources().getDrawable(R.drawable.button_option_default));
            LayoutParams param=new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            param.setMargins(10, 0, 10,0);
            button.setLayoutParams(param);
            viewGroup.addView(button);
            allOptions.put(button, option);

            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(Button btn:allOptions.keySet()) {
                        btn.setBackground(getResources().getDrawable(
                                (btn.getText().toString().equalsIgnoreCase(button.getText().toString()) ?
                                        R.drawable.button_option_selected : R.drawable.button_option_default)));
                        btn.setTextColor(getResources().getColor(
                                (btn.getText().toString().equalsIgnoreCase(button.getText().toString()) ?
                                        R.color.switchbox_text_primary_theme1 : R.color.switchbox_text_secondary_theme1)));
                    }

                    selected = allOptions.get(button);

                    if(getDataType().equalsIgnoreCase(SIMPLE_TYPE)) {
                        setLanguageSpecificValues(selected.getCode());
                    }
                    else {
                        registrationDto.addDemographicField(fieldSpecDto.getId(), button.getText().toString());
                    }
                }
            });
        }
    }

    private void setLanguageSpecificValues(String code) {
        List<GenericValueDto> list = this.masterDataService.getFieldValuesByCode(fieldSpecDto.getSubType(), code);
        registrationDto.getSelectedLanguages().forEach(lang -> {
            Optional<GenericValueDto> result = list.stream().filter(f -> f.getLangCode().equals(lang)).findFirst();
            if(result.isPresent())
                registrationDto.addDemographicField(fieldSpecDto.getId(), result.get().getName(), lang);
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
