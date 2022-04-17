package io.mosip.registration.app.dynamicviews;


import android.content.Context;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import java.util.List;

public class DynamicComponentFactory {

    private Context context;
    private MasterDataService masterDataService;

    public DynamicComponentFactory(Context context, MasterDataService masterDataService) {
        this.context = context;
        this.masterDataService = masterDataService;
    }

    public DynamicView getTextComponent(FieldSpecDto fieldSpecDto, List<String> selectedLanguages) {
        return new DynamicTextBox(context, fieldSpecDto, selectedLanguages);
    }

    public DynamicView getAgeDateComponent(FieldSpecDto fieldSpecDto, List<String> selectedLanguages) {
        return new DynamicAgeDateBox(context, fieldSpecDto, selectedLanguages, masterDataService);
    }

    public DynamicView getSwitchComponent(FieldSpecDto fieldSpecDto, List<String> selectedLanguages) {
        return new DynamicSwitchBox(context, fieldSpecDto, selectedLanguages, masterDataService);
    }

    public DynamicView getDropdownComponent(FieldSpecDto fieldSpecDto, List<String> selectedLanguages) {
        return new DynamicDropDownBox(context, fieldSpecDto, selectedLanguages, masterDataService);
    }

    public DynamicView getDocumentComponent(FieldSpecDto fieldSpecDto, List<String> selectedLanguages) {
        return new DynamicDocumentBox(context, fieldSpecDto, selectedLanguages, masterDataService);
    }

    public DynamicView getBiometricsComponent(FieldSpecDto fieldSpecDto, List<String> selectedLanguages) {
        return new DynamicBiometricsBox(context, fieldSpecDto, selectedLanguages);
    }
}
