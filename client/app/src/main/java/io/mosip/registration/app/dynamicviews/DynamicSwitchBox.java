package io.mosip.registration.app.dynamicviews;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.mosip.registration.app.R;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.spi.MasterDataService;

public class DynamicSwitchBox extends LinearLayout implements DynamicView {


    String selectedOption="";
    List<Button> allOptions = new ArrayList<>();
    MasterDataService masterDataService;

    List<String> languages = null;
    FieldSpecDto fieldSpecDto = null;
    final int layoutId=R.layout.dynamic_switch_box;

    public DynamicSwitchBox(Context context, FieldSpecDto fieldSpecDto, List<String> languages,
                            MasterDataService masterDataService) {
        super(context);
        this.fieldSpecDto = fieldSpecDto;
        this.languages = languages;
        this.masterDataService = masterDataService;
        initializeView(context);
    }

    private void initializeView(Context context) {
        inflate(context, layoutId, this);
        this.setTag(fieldSpecDto.getId());

        List<String> labels = new ArrayList<>();
        for(String language : languages) {
            labels.add(fieldSpecDto.getLabel().get(language));
        }
        ((TextView)findViewById(R.id.switch_label)).setText(String.join("/", labels));

        selectedOption = "";
        ViewGroup viewGroup = findViewById(R.id.option_holder_panel);

        List<String> options = masterDataService.getFieldValues(fieldSpecDto.getId(), languages.get(0));

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
                    selectedOption = button.getText().toString();
                    for(Button btn:allOptions){
                        if(btn.getText().toString().equalsIgnoreCase(button.getText().toString())==false){
                            btn.setBackground(getResources().getDrawable(R.drawable.button_option_default));
                        }
                        else{
                            btn.setBackground(getResources().getDrawable(R.drawable.button_option_selected));
                        }
                    }
                }
            });
        }
    }


    @Override
    public String getDataType() {
        return fieldSpecDto.getType();
    }

    public String getValue() {
        return selectedOption;
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
