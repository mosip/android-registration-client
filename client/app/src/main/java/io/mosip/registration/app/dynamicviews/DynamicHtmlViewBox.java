package io.mosip.registration.app.dynamicviews;

import android.content.Context;
import android.text.Html;
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

public class DynamicHtmlViewBox extends LinearLayout implements DynamicView {

    RegistrationDto registrationDto = null;
    FieldSpecDto fieldSpecDto = null;
    MasterDataService masterDataService;
    final int layoutId = R.layout.dynamic_html_box;

    public DynamicHtmlViewBox(Context context, FieldSpecDto fieldSpecDto, RegistrationDto registrationDto,
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

        /*List<String> labels = new ArrayList<>();
        for(String language : registrationDto.getSelectedLanguages()) {
            labels.add(fieldSpecDto.getLabel().get(language));
        }

        ((TextView)findViewById(R.id.html_label)).setText(Html.fromHtml(isRequired() ?
                String.format(REQUIRED_FIELD_LABEL_TEMPLATE, String.join("/", labels)) :
                String.format(FIELD_LABEL_TEMPLATE, String.join("/", labels)), 1));*/

        ((TextView)findViewById(R.id.html_content)).setText(Html.fromHtml(masterDataService.getTemplateContent(fieldSpecDto.getTemplateName(),
                registrationDto.getSelectedLanguages().get(0)), 1));
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
        return true;
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
            registrationDto.removeDocumentField(fieldSpecDto.getId());
            this.setVisibility(GONE);
        }
    }
}
