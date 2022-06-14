package io.mosip.registration.app.activites;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.registration.app.R;
import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;


/**
 * @author anusha
 */
public class AboutActivity extends DaggerAppCompatActivity {

    private static final String TAG = AboutActivity.class.getSimpleName();

    @Inject
    ClientCryptoManagerService clientCryptoManagerService;

    @Inject
    AuditManagerService auditManagerService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getSupportActionBar().setTitle(R.string.about_client);

        final TextView editText = findViewById(R.id.about);
        editText.setTextIsSelectable(true);
        Map<String, String> details = clientCryptoManagerService.getMachineDetails();
        JSONObject jsonObject = new JSONObject(details);
        try {
            jsonObject.put("version", "Alpha");
            editText.setText(jsonObject.toString(4));
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            editText.setText(R.string.initialization_error);
        }

        auditManagerService.audit(AuditEvent.LOADED_ABOUT, Components.LOGIN);
    }
}
