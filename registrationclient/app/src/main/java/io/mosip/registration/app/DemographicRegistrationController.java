package io.mosip.registration.app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.mosip.registration.app.ui.dynamic.DynamicComponent;
import io.mosip.registration.app.ui.dynamic.DynamicComponentFactory;

public class DemographicRegistrationController extends AppCompatActivity {


    ViewGroup pnlPrimary = null;
    ViewGroup pnlSecondary = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demographic_registration_controller);

        pnlPrimary = findViewById(R.id.pnlPrimaryLanguagePanel);
        pnlSecondary = findViewById(R.id.pnlSecondaryLanguagePanel);
        loadUI();
    }



    public String loadJSONFromResource() {
        String json = null;
        try {
            InputStream is = getApplicationContext().getResources().openRawResource(R.raw.ui_specification);//getActivity().getAssets().open("yourfilename.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }




    private List<DynamicComponent> loadedFields = new ArrayList<>();

    private void loadUI() {


////        LinearLayout pnlPrimaryLangGrpComp = new LinearLayout(this.getApplicationContext());
////        LinearLayout pnlSecondLangGrpComp = new LinearLayout(this.getApplicationContext());
//
//        LinearLayout.LayoutParams matchParentLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//        pnlPrimaryLangGrpComp.setOrientation(LinearLayout.HORIZONTAL);
//        pnlSecondLangGrpComp.setOrientation(LinearLayout.HORIZONTAL);
//        pnlPrimaryLangGrpComp.setLayoutParams(matchParentLayout);
//        pnlSecondLangGrpComp.setLayoutParams(matchParentLayout);

        pnlPrimary.removeAllViews();
        pnlSecondary.removeAllViews();
        DynamicComponentFactory factory = new DynamicComponentFactory(getApplicationContext());

        String in = loadJSONFromResource();
        JSONArray compFromJson = null;
        try {
            compFromJson = new JSONArray(in);

            for (int i = 0; i < compFromJson.length(); i++) {
                JSONObject item = compFromJson.getJSONObject(i);
                if (item.get("fieldCategory").toString().equalsIgnoreCase("pvt") || item.get("fieldCategory").toString().equalsIgnoreCase("kyc")) {
                    String fieldName = item.getString("id");
//                    String description=item.getString("description");
//                    String primaryLangLabel=item.getJSONObject("label").getString("primary");
//                    String secondaryLangLabel=item.getJSONObject("label").getString("secondary");
                    String controlType = item.getString("controlType");
                    boolean isRequired = item.getBoolean("inputRequired");

//                    JSONArray validators = item.getJSONArray("validators");
//                    for(int valIndex=0;valIndex<validators.length();valIndex++){
//                        String validatorType = validators.getJSONObject(valIndex).getString("type");
//                        String validator = validators.getJSONObject(valIndex).getString("validator");
//                        //arguments
//                    }

                    if (controlType.equalsIgnoreCase("textbox")) {


                        DynamicComponent component = factory.getTextComponent(item.getJSONObject("label"), item.getJSONArray("validators"));

                        pnlPrimary.addView((View) component.getPrimaryView());
                        pnlSecondary.addView((View) component.getSecondaryView());
//                    for(int v=1;i<component.getViewCount();i++) {
//                        pnlSecondary.addView((View) component.getView(v));
//                        break;
//                    }
                    } else if (controlType.equalsIgnoreCase("ageDate")) {

                        DynamicComponent component = factory.getAgeDateComponent(item.getJSONObject("label"), item.getJSONArray("validators"));

                        pnlPrimary.addView((View) component.getPrimaryView());
                        pnlSecondary.addView((View) component.getSecondaryView());
//                    for(int v=1;i<component.getViewCount();i++) {
//                        pnlSecondary.addView((View) component.getView(v));
//                        break;
//                    }
                    } else if (controlType.equalsIgnoreCase("dropdown")) {


                        if (fieldName.contains("gender")) {
                            DynamicComponent component = factory.getSwitchComponent(item.getJSONObject("label"), item.getJSONArray("validators"));

                            pnlPrimary.addView((View) component.getPrimaryView());
                            pnlSecondary.addView((View) component.getSecondaryView());
                        } else if (fieldName.contains("residenceStatus")) {
                            DynamicComponent component = factory.getSwitchComponent(item.getJSONObject("label"), item.getJSONArray("validators"));

                            pnlPrimary.addView((View) component.getPrimaryView());
                            pnlSecondary.addView((View) component.getSecondaryView());
                        } else {
                            DynamicComponent component = factory.getDropdownComponent(item.getJSONObject("label"), item.getJSONArray("validators"));

                            pnlPrimary.addView((View) component.getPrimaryView());
                            pnlSecondary.addView((View) component.getSecondaryView());
                        }

                    }
//                else if(controlType.equalsIgnoreCase("ageDate")) {
//                    DynamicUIComponent primaryLang = DynamicUIComponent.getSpinnerView("pLang_"+fieldName,primaryLangLabel,this.getContext());
//                    DynamicUIComponent secondaryLang = DynamicUIComponent.getSpinnerView("sLang_"+fieldName,secondaryLangLabel,this.getContext());
//                    primaryLang.setTheOtherComponent(secondaryLang);
//                    secondaryLang.setTheOtherComponent(primaryLang);
//
//                    primaryLang.setRequired(isRequired);
//                    secondaryLang.setRequired(isRequired);
//
//
//                    loadedFields.add(primaryLang);//We dont need to add secondary Language Component to the list, as it is set to primaryLang's object the Other Component
//                    pnlPrimary.addView(primaryLang.getComponentView());
//                    pnlSecondary.addView(secondaryLang.getComponentView());
//                }

                }


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}