package io.mosip.registration.app.activites;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.registration.app.R;
import io.mosip.registration.app.dynamicviews.DynamicComponentFactory;
import io.mosip.registration.app.dynamicviews.DynamicView;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.util.UserInterfaceHelperService;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class DemographicsActivity extends DaggerAppCompatActivity {

    private static final String TAG = DemographicsActivity.class.getSimpleName();
    private Map<String, DynamicView> dynamicViews = new HashMap<>();
    private ViewGroup pnlPrimary = null;

    @Inject
    UserInterfaceHelperService userInterfaceHelperService;

    @Inject
    public MasterDataService masterDataService;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_activity);

        //Adding file permissions
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(DemographicsActivity.this,
                permissions, 110);

        //to display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Demographics Data");

        final Button submitButton = findViewById(R.id.submit);
        submitButton.setOnClickListener( v -> {
            submitButton.setEnabled(false);
            Log.i(TAG, "Clicked on Registration form submit...");
            goToNextActivity();
        });

        pnlPrimary = findViewById(R.id.pnlPrimaryLanguagePanel);
        loadUI();
    }

    public void goToNextActivity() {
        Intent intent = new Intent(this, DocumentsActivity.class);
        startActivity(intent);
    }

    public void goToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        goToHome();
        return true;
    }

    private void loadUI() {
        dynamicViews.clear();
        pnlPrimary.removeAllViews();

        DynamicComponentFactory factory = new DynamicComponentFactory(getApplicationContext(), masterDataService);

        String spec = userInterfaceHelperService.loadJSONFromResource();
        try {
            JSONArray compFromJson = new JSONArray(spec);
            for (int i = 0; i < compFromJson.length(); i++) {
                JSONObject item = compFromJson.getJSONObject(i);
                if(item.getBoolean("inputRequired")) {
                    switch (item.getString("controlType")) {
                        case "textbox" :
                            DynamicView textDynamicView = factory.getTextComponent(item.getJSONObject("label"),
                                            item.getJSONArray("validators"), item.getString("type"))
                                    .getPrimaryView();
                            pnlPrimary.addView((View) textDynamicView);
                            dynamicViews.put(item.getString("id"), textDynamicView);
                            break;

                        case "ageDate" :
                            DynamicView ageDateDynamicView = factory.getAgeDateComponent(item.getJSONObject("label"), item.getJSONArray("validators"))
                                    .getPrimaryView();
                            pnlPrimary.addView((View) ageDateDynamicView);
                            dynamicViews.put(item.getString("id"), ageDateDynamicView);
                            break;

                        case "dropdown" :
                            DynamicView dropdownDynamicView = factory.getDropdownComponent(item.getJSONObject("label"), item.getJSONArray("validators"))
                                    .getPrimaryView();
                            pnlPrimary.addView((View) dropdownDynamicView);
                            dynamicViews.put(item.getString("id"), dropdownDynamicView);
                            break;

                        case "button" :
                            DynamicView buttonDynamicView = factory.getSwitchComponent(item.getString("id"), item.getJSONObject("label"),
                                            item.getJSONArray("validators"))
                                    .getPrimaryView();
                            pnlPrimary.addView((View)buttonDynamicView);
                            dynamicViews.put(item.getString("id"), buttonDynamicView);
                            break;
                    }
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "Failed to build dynamic ui", ex);
        }
    }
}
