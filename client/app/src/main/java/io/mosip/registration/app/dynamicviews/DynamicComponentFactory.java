package io.mosip.registration.app.dynamicviews;


import android.content.Context;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.repository.LanguageRepository;
import io.mosip.registration.clientmanager.service.Biometrics095Service;
import io.mosip.registration.clientmanager.spi.BiometricsService;
import io.mosip.registration.clientmanager.spi.MasterDataService;

public class DynamicComponentFactory {

    private Context context;
    private MasterDataService masterDataService;

    private Biometrics095Service biometricsService;

    public DynamicComponentFactory(Context context, MasterDataService masterDataService, Biometrics095Service biometricsService) {
        this.context = context;
        this.masterDataService = masterDataService;
        this.biometricsService = biometricsService;
    }

    public DynamicView getTextComponent(FieldSpecDto fieldSpecDto, RegistrationDto registrationDto,
                                        LanguageRepository languageRepository) {
        return new DynamicTextBox(context, fieldSpecDto, registrationDto, languageRepository);
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
        return new DynamicBiometricsBox(context, fieldSpecDto, registrationDto, biometricsService);
    }

    public DynamicView getHtmlComponent(FieldSpecDto fieldSpecDto, RegistrationDto registrationDto) {
        return new DynamicHtmlViewBox(context, fieldSpecDto, registrationDto, masterDataService);
    }

    public DynamicView getCheckboxComponent(FieldSpecDto fieldSpecDto, RegistrationDto registrationDto) {
        return new DynamicCheckBox(context, fieldSpecDto, registrationDto);
    }
}
