package io.mosip.registration.app.activites;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.registration.app.R;
import io.mosip.registration.app.viewmodel.CustomPagerAdapter;
import io.mosip.registration.app.viewmodel.ModalityPagerAdapter;
import io.mosip.registration.clientmanager.spi.RegistrationService;

import javax.inject.Inject;

public class ModalityActivity  extends DaggerAppCompatActivity {

    private static final String TAG = ModalityActivity.class.getSimpleName();
    String fieldId = "NA";
    String fieldLabel = "NA";
    @Inject
    RegistrationService registrationService;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        fieldId = intent.getStringExtra("fieldId");
        fieldLabel = intent.getStringExtra("fieldLabel");
        startActivity();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        fieldId = intent.getStringExtra("fieldId");
        fieldLabel = intent.getStringExtra("fieldLabel");
        startActivity();
    }

    private void startActivity() {
        setContentView(R.layout.modality_activity);

        //to hide home back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(fieldLabel);

        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new ModalityPagerAdapter(this, this.registrationService, this.fieldId));
    }
}
