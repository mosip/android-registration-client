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
import io.mosip.registration.clientmanager.exception.ClientCheckedException;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.repository.LanguageRepository;
import io.mosip.registration.clientmanager.spi.RegistrationService;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

public class RegistrationActivity extends DaggerAppCompatActivity {

    private static final String TAG = RegistrationActivity.class.getSimpleName();
    private Map<String, String> configuredLanguages = new LinkedHashMap<>();
    private List<String> selectedLanguages = new ArrayList<>();

    @Inject
    RegistrationService registrationService;

    @Inject
    IdentitySchemaRepository identitySchemaRepository;

    @Inject
    GlobalParamRepository globalParamRepository;

    @Inject
    LanguageRepository languageRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);

        registrationService.clearRegistration();

        //to display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.data_entry_lang_title);

        configuredLanguages.clear();
        selectedLanguages.clear();
        String mandatoryLangString = null;
        List<String> mandatoryLanguages = globalParamRepository.getMandatoryLanguageCodes();
        for(String langCode : mandatoryLanguages) {
            configuredLanguages.put(langCode, getLangFullForm(langCode));
        }
        mandatoryLangString = String.join(",", configuredLanguages.values());

        List<String> optionalLanguages = globalParamRepository.getOptionalLanguageCodes();
        for(String langCode : optionalLanguages) {
            configuredLanguages.put(langCode, getLangFullForm(langCode));
        }

        ((TextView)findViewById(R.id.languageInfoText)).setText(getString(R.string.lang_info_text,
                String.join(",", configuredLanguages.values()),
                String.join(",", mandatoryLangString)));

        ListView listView = findViewById(R.id.languageList);
        listView.clearChoices();
        listView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice,
                configuredLanguages.values().toArray(new String[0])));
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

    private String getLangCode(String nativeName) {
        Optional<Map.Entry<String, String>> resultEntry = configuredLanguages
                .entrySet()
                .stream()
                .filter( e -> e.getValue().equals(nativeName) )
                .findFirst();

        return (resultEntry.isPresent()) ? resultEntry.get().getKey() : null;
    }

    private String getLangFullForm(String langCode) {
        String nativeName = languageRepository.getNativeName(langCode);
        return nativeName == null ? langCode : nativeName;
    }


    private void startRegistration() {
        String errorMessage = null;
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
            return;

        } catch (Exception e) {
            Log.e(TAG, "Failed to start Registration", e);
            errorMessage =  e.getMessage();
        }
        Toast.makeText(this, getString(R.string.start_registration_fail, errorMessage),
                Toast.LENGTH_LONG).show();
        goToHome();
    }

    public void goToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}