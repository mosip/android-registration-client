package io.mosip.registration.app.activites;

import android.os.Bundle;
import androidx.annotation.Nullable;
import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.registration.app.R;

public class ScreenActivity extends DaggerAppCompatActivity {

    private static final String TAG = ScreenActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_activity);
    }
}
