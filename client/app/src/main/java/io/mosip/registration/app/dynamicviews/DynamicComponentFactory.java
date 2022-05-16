package io.mosip.registration.app.dynamicviews;


import android.content.Context;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.spi.MasterDataService;

public class DynamicComponentFactory {

    private Context context;
    private MasterDataService masterDataService;

    public DynamicComponentFactory(Context context, MasterDataService masterDataService) {
        this.context = context;
        this.masterDataService = masterDataService;
    }

    public DynamicView getTextComponent(FieldSpecDto fieldSpecDto, RegistrationDto registrationDto) {
        return new DynamicTextBox(context, fieldSpecDto, registrationDto);
    }

    public DynamicView getAgeDateComponent(FieldSpecDto fieldSpecDto, RegistrationDto registrationDto) {
        return new DynamicAgeDateBox(context, fieldSpecDto, registrationDto, masterDataService);
    }

    public DynamicView getSwitchComponent(FieldSpecDto fieldSpecDto, RegistrationDto registrationDto) {
        return new DynamicSwitchBox(context, fieldSpecDto, registrationDto, masterDataService);
    }

    public DynamicView getDropdownComponent(FieldSpecDto fieldSpecDto, RegistrationDto registrationDto) {
        return new DynamicDropDownBox(context, fieldSpecDto, registrationDto, masterDataService);
    }

    public DynamicView getDocumentComponent(FieldSpecDto fieldSpecDto, RegistrationDto registrationDto) {
        return new DynamicDocumentBox(context, fieldSpecDto, registrationDto, masterDataService);
    }

    public DynamicView getBiometricsComponent(FieldSpecDto fieldSpecDto, RegistrationDto registrationDto) {
        return new DynamicBiometricsBox(context, fieldSpecDto, registrationDto);
    }

    public DynamicView getHtmlComponent(FieldSpecDto fieldSpecDto, RegistrationDto registrationDto) {
        return new DynamicHtmlViewBox(context, fieldSpecDto, registrationDto, masterDataService);
    }

    public DynamicView getCheckboxComponent(FieldSpecDto fieldSpecDto, RegistrationDto registrationDto) {
        return new DynamicCheckBox(context, fieldSpecDto, registrationDto);
    }
}
