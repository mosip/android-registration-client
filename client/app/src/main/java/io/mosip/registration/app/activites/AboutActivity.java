package io.mosip.registration.app.activites;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.registration.app.R;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import java.util.Map;

public class AboutActivity extends DaggerAppCompatActivity {

    private static final String TAG = AboutActivity.class.getSimpleName();

    @Inject
    ClientCryptoManagerService clientCryptoManagerService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getSupportActionBar().setTitle("About Client");

        final TextView editText = findViewById(R.id.about);
        editText.setTextIsSelectable(true);
        Map<String,String> details = clientCryptoManagerService.getMachineDetails();
        JSONObject jsonObject = new JSONObject(details);
        try {
            jsonObject.put("version", "Alpha");
            editText.setText(jsonObject.toString(4));
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            editText.setText("Failed to initialize device");
        }
    }
}
