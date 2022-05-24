package io.mosip.registration.app.activites;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.*;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatCheckedTextView;
import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.registration.app.R;
import io.mosip.registration.clientmanager.dto.uispec.ProcessSpecDto;
import io.mosip.registration.clientmanager.dto.uispec.ScreenSpecDto;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.spi.RegistrationService;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

public class RegistrationActivity extends DaggerAppCompatActivity {

    private static final String TAG = RegistrationActivity.class.getSimpleName();

    //TODO need to take this from configuration
    private List<String> mandatoryLanguages = Arrays.asList("eng");
    private List<String> optionalLanguages = Arrays.asList();
    List<String> selectedLanguages = new ArrayList<>();

    @Inject
    RegistrationService registrationService;

    @Inject
    IdentitySchemaRepository identitySchemaRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);

        registrationService.clearRegistration();

        //to display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Language selection");

        List<String> configuredLanguages = new ArrayList<>();
        for(String langCode : mandatoryLanguages) {
            configuredLanguages.add(getLangFullForm(langCode));
        }
        for(String langCode : optionalLanguages) {
            configuredLanguages.add(getLangFullForm(langCode));
        }

        ((TextView)findViewById(R.id.languageInfoText)).setText(getString(R.string.lang_info_text,
                String.join(",", configuredLanguages), String.join(",", mandatoryLanguages)));

        ListView listView = findViewById(R.id.languageList);
        listView.clearChoices();
        listView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, configuredLanguages));
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AppCompatCheckedTextView checkBox = (AppCompatCheckedTextView) view;
                if (checkBox.isChecked())
                    selectedLanguages.add(getLangCode(checkBox.getText().toString()));
                else
                    selectedLanguages.remove(getLangCode(checkBox.getText().toString()));
            }
        });

        final Button startRegistration = findViewById(R.id.start_registration);
        startRegistration.setOnClickListener( v -> {
            startRegistration.setEnabled(false);
            startRegistration();
        });
    }

    //TODO need to read from language table
    private String getLangCode(String fullForm) {
        switch (fullForm.toLowerCase()) {
            case "English" : return "eng";
        }
        return "eng";
    }

    //TODO need to read from language table
    private String getLangFullForm(String langCode) {
        switch (langCode) {
            case "eng" : return getString(R.string.eng);
            case "fra" : return getString(R.string.fra);
            case "ara" : return getString(R.string.ara);
        }
        return "English";
    }


    private void startRegistration() {
        try {
            registrationService.startRegistration(selectedLanguages);

            Double schemaVersion = registrationService.getRegistrationDto().getSchemaVersion();
            ProcessSpecDto processSpecDto = identitySchemaRepository.getNewProcessSpec(getApplicationContext(), schemaVersion);
            List<String> screens = processSpecDto.getScreens().stream()
                    .sorted(Comparator.comparing(ScreenSpecDto::getOrder))
                    .map(ScreenSpecDto::getName)
                    .collect(Collectors.toList());

            Intent intent = new Intent(this, ScreenActivity.class);
            intent.putExtra("screens", screens.toArray(new String[0]));
            intent.putExtra("nextScreenIndex", 0);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            Log.e(TAG, "Failed to start Registration", e);
            Toast.makeText(getApplicationContext(), "Failed to start Registration : " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
            goToHome();
        }
    }

    public void goToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}