package io.mosip.registration.app.ui.dynamic;


import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;



import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

import io.mosip.registration.app.ui.dynamic.views.DynamicAgeDateBox;
import io.mosip.registration.app.ui.dynamic.views.DynamicDropDownBox;
import io.mosip.registration.app.ui.dynamic.views.DynamicSwitchBox;
import io.mosip.registration.app.ui.dynamic.views.DynamicTextBox;

public class DynamicComponentFactory {

    private Context context;

    public DynamicComponentFactory(Context context){

        this.context = context;
    }
    private String getValidationRule(String languageCode, JSONArray validatorRules){
        //Todo Iterate and find validation rule using languagecode
        return "";
    }

    public DynamicComponent getTextComponent(JSONObject labels, JSONArray validatorRules) {

        DynamicComponent dd = new DynamicComponent(context);
        try {
            Iterator<String> keys = labels.keys();//Keys are Language codes

            while (keys.hasNext()) {
                String langCode = keys.next();
                DynamicTextBox control = new DynamicTextBox(context,langCode,labels.getString(langCode),getValidationRule(langCode,validatorRules));
                dd.addView(control);
            }




            //This should be set by Iterating through all added objects
            //setTextWatcher(object1, object2);
            //setTextWatcher(object2,object1);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return dd;
    }



    private void setTextWatcher(DynamicTextBox mainObject, DynamicTextBox secondObject){
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Todo: This is for test only, should be moved to external class (Translator, Validator ...)
                secondObject.setValue(mainObject.toString());

            }
        };
        mainObject.setTextChangeListener(textWatcher);
    }



    public DynamicComponent getAgeDateComponent(JSONObject labels, JSONArray validatorRules) {

        DynamicComponent dd = new DynamicComponent(context);
        try {
            Iterator<String> keys = labels.keys();//Keys are Language codes

            while (keys.hasNext()) {
                String langCode = keys.next();
                DynamicAgeDateBox control = new DynamicAgeDateBox(context,langCode,labels.getString(langCode),getValidationRule(langCode,validatorRules));
                dd.addView(control);
            }




            //This should be set by Iterating through all added objects
            //setTextWatcher(object1, object2);
            //setTextWatcher(object2,object1);
        }catch (Exception ex){

        }
        return dd;
    }


    public DynamicComponent getSwitchComponent(JSONObject labels, JSONArray validatorRules) {

        DynamicComponent dd = new DynamicComponent(context);
        try {
            Iterator<String> keys = labels.keys();//Keys are Language codes

            while (keys.hasNext()) {
                String langCode = keys.next();
                DynamicSwitchBox control = new DynamicSwitchBox(context,langCode,labels.getString(langCode),getValidationRule(langCode,validatorRules));
                dd.addView(control);
            }




            //This should be set by Iterating through all added objects
            //setTextWatcher(object1, object2);
            //setTextWatcher(object2,object1);
        }catch (Exception ex){

        }
        return dd;
    }

    public DynamicComponent getDropdownComponent(JSONObject labels, JSONArray validatorRules) {

        DynamicComponent dd = new DynamicComponent(context);
        try {
            Iterator<String> keys = labels.keys();//Keys are Language codes

            while (keys.hasNext()) {
                String langCode = keys.next();
                DynamicDropDownBox control = new DynamicDropDownBox(context,langCode,labels.getString(langCode),getValidationRule(langCode,validatorRules));
                dd.addView(control);
            }




            //This should be set by Iterating through all added objects
            //setTextWatcher(object1, object2);
            //setTextWatcher(object2,object1);
        }catch (Exception ex){

        }
        return dd;
    }
    public DynamicComponent getComponentByType(String componentType){

        switch (componentType){
            case "TEXT":
                return null;
            default:
                throw new IllegalStateException("Unexpected value: " + componentType);
        }
    }
}
