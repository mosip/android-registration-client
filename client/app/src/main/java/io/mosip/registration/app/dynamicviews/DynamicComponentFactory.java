package io.mosip.registration.app.dynamicviews;


import android.content.Context;

import android.util.Log;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

public class DynamicComponentFactory {

    private Context context;
    private MasterDataService masterDataService;

    public DynamicComponentFactory(Context context, MasterDataService masterDataService) {
        this.context = context;
        this.masterDataService = masterDataService;
    }

    private String getValidationRule(String languageCode, JSONArray validatorRules){
        //Todo Iterate and find validation rule using languagecode
        return "";
    }

    public DynamicComponent getTextComponent(JSONObject labels, JSONArray validatorRules, String type) {

        DynamicComponent dd = new DynamicComponent(context);
        try {
            Iterator<String> keys = labels.keys();//Keys are Language codes

            while (keys.hasNext()) {
                String langCode = keys.next();
                DynamicTextBox control = new DynamicTextBox(context,langCode,labels.getString(langCode),
                        getValidationRule(langCode,validatorRules), type);
                dd.addView(control);
            }




            //This should be set by Iterating through all added objects
            //setTextWatcher(object1, object2);
            //setTextWatcher(object2,object1);
        }catch (Exception ex){
            Log.e("", "Failed to build text box", ex);
        }
        return dd;
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
            Log.e("", "Failed to build AgeDateComponent", ex);
        }
        return dd;
    }


    public DynamicComponent getSwitchComponent(String fieldId, JSONObject labels, JSONArray validatorRules) {

        DynamicComponent dd = new DynamicComponent(context);
        try {
            Iterator<String> keys = labels.keys();//Keys are Language codes

            while (keys.hasNext()) {
                String langCode = keys.next();
                DynamicSwitchBox control = new DynamicSwitchBox(context, langCode, labels.getString(langCode),
                        getValidationRule(langCode,validatorRules), fieldId, masterDataService);
                dd.addView(control);
            }




            //This should be set by Iterating through all added objects
            //setTextWatcher(object1, object2);
            //setTextWatcher(object2,object1);
        }catch (Exception ex){
            Log.e("", "Failed to build SwitchComponent", ex);
        }
        return dd;
    }

    public DynamicComponent getDropdownComponent(JSONObject labels, JSONArray validatorRules, String subType, String fieldId) {

        DynamicComponent dd = new DynamicComponent(context);
        try {
            Iterator<String> keys = labels.keys();//Keys are Language codes

            while (keys.hasNext()) {
                String langCode = keys.next();
                DynamicDropDownBox control = new DynamicDropDownBox(context,langCode,labels.getString(langCode),
                        getValidationRule(langCode,validatorRules), masterDataService, subType, fieldId);
                dd.addView(control);
            }




            //This should be set by Iterating through all added objects
            //setTextWatcher(object1, object2);
            //setTextWatcher(object2,object1);
        }catch (Exception ex){
            Log.e("", "Failed to build DropdownComponent", ex);
        }
        return dd;
    }

    public DynamicComponent getDocumentComponent(JSONObject labels, JSONArray validatorRules, String subType) {

        DynamicComponent dd = new DynamicComponent(context);
        try {
            Iterator<String> keys = labels.keys();//Keys are Language codes

            while (keys.hasNext()) {
                String langCode = keys.next();
                DynamicDocumentBox control = new DynamicDocumentBox(context,langCode,labels.getString(langCode),
                        getValidationRule(langCode,validatorRules), this.masterDataService, subType);
                dd.addView(control);
            }

        }catch (Exception ex){
            Log.e("", "Failed to build DocumentComponent", ex);
        }
        return dd;
    }

    public DynamicComponent getBiometricsComponent(JSONObject labels, JSONArray validatorRules) {

        DynamicComponent dd = new DynamicComponent(context);
        try {
            Iterator<String> keys = labels.keys();//Keys are Language codes

            while (keys.hasNext()) {
                String langCode = keys.next();
                DynamicBiometricsBox control = new DynamicBiometricsBox(context,langCode,labels.getString(langCode),getValidationRule(langCode,validatorRules));
                dd.addView(control);
            }

        }catch (Exception ex){
            Log.e("", "Failed to build BiometricsComponent", ex);
        }
        return dd;
    }
}
