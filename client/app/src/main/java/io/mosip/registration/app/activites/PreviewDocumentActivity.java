package io.mosip.registration.app.activites;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;
import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.registration.app.R;
import io.mosip.registration.app.viewmodel.CustomPagerAdapter;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.spi.RegistrationService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PreviewDocumentActivity extends DaggerAppCompatActivity {

    private static final String TAG = PreviewDocumentActivity.class.getSimpleName();
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
        setContentView(R.layout.doc_preview_activity);

        //to hide home back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(fieldLabel);

        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new CustomPagerAdapter(this, this.registrationService, this.fieldId));
    }
}
