package io.mosip.registration.app.activites;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.registration.app.R;
import io.mosip.registration.app.dynamicviews.DynamicComponentFactory;
import io.mosip.registration.app.dynamicviews.DynamicView;
import io.mosip.registration.app.util.ClientConstants;
import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.dto.uispec.ProcessSpecDto;
import io.mosip.registration.clientmanager.dto.uispec.ScreenSpecDto;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.repository.LanguageRepository;
import io.mosip.registration.clientmanager.service.Biometrics095Service;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.BiometricsService;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.RegistrationService;

import io.mosip.registration.clientmanager.util.UserInterfaceHelperService;

import javax.inject.Inject;

import java.io.*;
import java.util.*;


public class ScreenActivity extends DaggerAppCompatActivity  {

    private static final String TAG = ScreenActivity.class.getSimpleName();
    private static final String COLON_SEPARATED_MODULE_NAME = "%s: %s";

    private ViewGroup fieldPanel = null;
    private int BIO_SCAN_REQUEST_CODE = 1;
    private int SCAN_REQUEST_CODE = 99;
    private Map<Integer, String> requestCodeMap = new HashMap<>();
    public static Map<String, DynamicView> currentDynamicViews = new HashMap<>();

    @Inject
    RegistrationService registrationService;

    @Inject
    IdentitySchemaRepository identitySchemaRepository;

    @Inject
    MasterDataService masterDataService;

    @Inject
    Biometrics095Service biometricsService;

    @Inject
    LanguageRepository languageRepository;

    @Inject
    AuditManagerService auditManagerService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_activity);
        fieldPanel = findViewById(R.id.fieldPanel);

        try {
            //Adding file permissions
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(ScreenActivity.this,
                    permissions, 110);

            List<String> languages = registrationService.getRegistrationDto().getSelectedLanguages();
            String[] screens = getIntent().getExtras().getStringArray("screens");
            int currentScreenIndex = getIntent().getExtras().getInt("nextScreenIndex");
            currentDynamicViews.clear();
            final Button nextButton = findViewById(R.id.next);

            nextButton.setOnClickListener(v -> {
                auditManagerService.audit(AuditEvent.NEXT_BUTTON_CLICKED, Components.REGISTRATION.getId(), String.format(COLON_SEPARATED_MODULE_NAME, Components.REGISTRATION.getName(), screens[currentScreenIndex]));

                Optional<DynamicView> view = currentDynamicViews.values()
                        .stream()
                        .filter(d -> (d.isRequired() && !d.isValidValue()))
                        .findFirst();

                if (view.isPresent()) {
                    ((View) view.get()).requestFocus();
                    nextButton.setError(getString(R.string.invalid_value));
                } else {
                    nextButton.setError(null);
                    //start activity to render next screen
                    Intent intent = new Intent(this, ScreenActivity.class);
                    intent.putExtra("screens", screens);
                    intent.putExtra("nextScreenIndex", currentScreenIndex + 1);
                    startActivity(intent);
                    finish();
                }
            });

            if (currentScreenIndex < screens.length) {
                ProcessSpecDto processSpecDto = identitySchemaRepository.getNewProcessSpec(getApplicationContext(),
                        registrationService.getRegistrationDto().getSchemaVersion());
                Optional<ScreenSpecDto> screen = processSpecDto.getScreens().stream()
                        .filter(s -> s.getName().equals(screens[currentScreenIndex]))
                        .findFirst();

                //this can't happen at this stage
                if (!screen.isPresent())
                    throw new Exception("Invalid screen name found");

                getSupportActionBar().setTitle(screen.get().getLabel().get(languages.get(0)));
                getSupportActionBar().setSubtitle(processSpecDto.getFlow());

                if (!loadScreenFields(screen.get())) {
                    //start activity to render next screen
                    ((Button) findViewById(R.id.next)).performClick();
                }

            } else {
                //No more screens start loading preview screen
                Intent intent = new Intent(this, PreviewActivity.class);
                startActivity(intent);
            }
            auditManagerService.audit(AuditEvent.LOADED_REGISTRATION_SCREEN, Components.REGISTRATION);
        } catch (Throwable t) {
            Log.e(TAG, "Failed to launch registration screen", t);
            goToHome();
        }
    }

    private boolean loadScreenFields(ScreenSpecDto screenSpecDto) throws Exception {
        fieldPanel.removeAllViews();
        DynamicComponentFactory factory = new DynamicComponentFactory(this, masterDataService, biometricsService);

        Map<String, Object> mvelContext = this.registrationService.getRegistrationDto().getMVELDataContext();
        int visibleFields = 0;
        for (FieldSpecDto fieldSpecDto : screenSpecDto.getFields()) {
            if (fieldSpecDto.getInputRequired()) {
                DynamicView dynamicView = null;
                switch (fieldSpecDto.getControlType().toLowerCase()) {
                    case "textbox":
                        dynamicView = factory.getTextComponent(fieldSpecDto, this.registrationService.getRegistrationDto(), languageRepository);
                        break;
                    case "agedate":
                        dynamicView = factory.getAgeDateComponent(fieldSpecDto, this.registrationService.getRegistrationDto());
                        break;
                    case "dropdown":
                        dynamicView = factory.getDropdownComponent(fieldSpecDto, this.registrationService.getRegistrationDto());
                        break;
                    case "button":
                        dynamicView = factory.getSwitchComponent(fieldSpecDto, this.registrationService.getRegistrationDto());
                        break;
                    case "html":
                        dynamicView = factory.getHtmlComponent(fieldSpecDto, this.registrationService.getRegistrationDto());
                        break;
                    case "checkbox":
                        dynamicView = factory.getCheckboxComponent(fieldSpecDto, this.registrationService.getRegistrationDto());
                        break;
                    case "fileupload":
                        dynamicView = factory.getDocumentComponent(fieldSpecDto, this.registrationService.getRegistrationDto());
                        SCAN_REQUEST_CODE = SCAN_REQUEST_CODE + 1;
                        requestCodeMap.put(SCAN_REQUEST_CODE, fieldSpecDto.getId());
                        setScanButtonListener(SCAN_REQUEST_CODE, (View) dynamicView, fieldSpecDto,
                                this.registrationService.getRegistrationDto().getSelectedLanguages());
                        break;
                    case "biometrics":
                        dynamicView = factory.getBiometricsComponent(fieldSpecDto, this.registrationService.getRegistrationDto());
                        break;
                }

                if (dynamicView != null) {
                    fieldPanel.addView((View) dynamicView);
                    currentDynamicViews.put(fieldSpecDto.getId(), dynamicView);
                    this.registrationService.getRegistrationDto().addObserver((Observer) dynamicView);
                    visibleFields = visibleFields + (UserInterfaceHelperService.
                            isFieldVisible(fieldSpecDto, mvelContext) ? 1 : 0);
                }
            }
        }
        Log.i(TAG, "Fields visible : " + visibleFields);
        return visibleFields > 0;
    }

    public void goToHome() {
        this.registrationService.clearRegistration();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String fieldId = requestCodeMap.get(requestCode);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCodeMap.containsKey(requestCode)) {
                Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
                try (InputStream iStream = getContentResolver().openInputStream(uri)) {
                    Spinner sItems = ((Spinner) ((View) currentDynamicViews.get(fieldId)).findViewById(R.id.doctypes_dropdown));
                    EditText editText = ((EditText) ((View) currentDynamicViews.get(fieldId)).findViewById(R.id.doc_refid));
                    this.registrationService.getRegistrationDto().addDocument(fieldId, sItems.getSelectedItem().toString(),
                            editText == null ? null : editText.getText().toString(), UserInterfaceHelperService.getBytes(iStream));
                    TextView textView = ((View) currentDynamicViews.get(fieldId)).findViewById(R.id.doc_preview);
                    textView.setText(getString(R.string.page_label, this.registrationService.getRegistrationDto().getScannedPages(fieldId).size()));
                } catch (Exception e) {
                    auditManagerService.audit(AuditEvent.DOCUMENT_SCAN_FAILED, Components.REGISTRATION, e.getMessage());
                    Log.e(TAG, "Failed to set document to registration dto", e);
                } finally {
                    getContentResolver().delete(uri, null, null);
                }
            } else {
                auditManagerService.audit(AuditEvent.DOCUMENT_SCAN_FAILED, Components.REGISTRATION, "Invalid requestCode " + requestCode);
                Toast.makeText(this, R.string.doc_scan_fail, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // TODO handle based on screens and saved data in registration DTO
    }

    private void setScanButtonListener(int requestCode, View view, FieldSpecDto fieldSpecDto, List<String> selectedLanguages) {
        Button button = view.findViewById(R.id.scan_doc);
        button.setOnClickListener(v -> {
            auditManagerService.audit(AuditEvent.DOCUMENT_SCAN, Components.REGISTRATION);

            int preference = ScanConstants.OPEN_CAMERA;
            Intent intent = new Intent(this, ScanActivity.class);
            intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
            startActivityForResult(intent, requestCode);
        });

        TextView textView = view.findViewById(R.id.doc_preview);
        textView.setOnClickListener(v -> {
            auditManagerService.audit(AuditEvent.DOCUMENT_PREVIEW, Components.REGISTRATION);

            Intent intent = new Intent(this, PreviewDocumentActivity.class);
            List<String> labels = new ArrayList<>();
            for (String language : selectedLanguages) {
                labels.add(fieldSpecDto.getLabel().get(language));
            }
            intent.putExtra("fieldId", fieldSpecDto.getId());
            intent.putExtra("fieldLabel", String.join(ClientConstants.LABEL_SEPARATOR, labels));
            startActivity(intent);
        });
    }
}
