package io.mosip.registration.app.activites;

import android.Manifest;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.registration.app.R;

public class UploadActivity  extends DaggerAppCompatActivity {

    private static final String TAG = UploadActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_controller);

        //Adding file permissions
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(UploadActivity.this,
                permissions, 110);

        //to display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
