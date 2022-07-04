package io.mosip.registration.app.activites;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import io.mosip.registration.app.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new PacketUploadSettingsFragment())
                .commit();
    }
}