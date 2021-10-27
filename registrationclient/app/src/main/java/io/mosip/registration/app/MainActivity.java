package io.mosip.registration.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import io.mosip.registration.app.ui.main.MainFragment;
import io.mosip.registration.app.R;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow();
        }
    }
}
