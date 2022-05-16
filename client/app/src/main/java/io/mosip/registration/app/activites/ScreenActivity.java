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
import androidx.annotation.OptIn;
import androidx.core.app.ActivityCompat;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.registration.app.R;
import io.mosip.registration.app.dynamicviews.DynamicComponentFactory;
import io.mosip.registration.app.dynamicviews.DynamicView;
import io.mosip.registration.clientmanager.dto.registration.BiometricsDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.dto.uispec.ProcessSpecDto;
import io.mosip.registration.clientmanager.dto.uispec.ScreenSpecDto;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.RegistrationService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static android.content.pm.PackageManager.MATCH_DEFAULT_ONLY;

public class ScreenActivity extends DaggerAppCompatActivity {

    private static final String TAG = ScreenActivity.class.getSimpleName();
    private ViewGroup fieldPanel = null;
    private int BIO_SCAN_REQUEST_CODE = 1;
    private int SCAN_REQUEST_CODE = 99;
    private Map<Integer, String> requestCodeMap = new HashMap<>();
    private Map<String, DynamicView> currentDynamicViews = new HashMap<>();

    @Inject
    RegistrationService registrationService;

    @Inject
    IdentitySchemaRepository identitySchemaRepository;

    @Inject
    MasterDataService masterDataService;

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

                loadScreenFields(screen.get(), languages);
            } else {
                //No more screens start loading preview screen
                Intent intent = new Intent(this, PreviewActivity.class);
                startActivity(intent);
            }

            final Button nextButton = findViewById(R.id.next);
            nextButton.setOnClickListener(v -> {

                Optional<DynamicView> view = currentDynamicViews.values()
                        .stream()
                        .filter(d -> (d.isRequired() && !d.isValidValue()))
                        .findFirst();

                if (view.isPresent()) {
                    ((View) view.get()).requestFocus();
                    nextButton.setError("Invalid value found");
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

        } catch (Throwable t) {
            Log.e(TAG, "Failed to launch registration screen", t);
            goToHome();
        }
    }

    private void loadScreenFields(ScreenSpecDto screenSpecDto, List<String> languages) throws Exception {
        fieldPanel.removeAllViews();
        DynamicComponentFactory factory = new DynamicComponentFactory(getApplicationContext(), masterDataService);

        for (FieldSpecDto fieldSpecDto : screenSpecDto.getFields()) {
            if (fieldSpecDto.getInputRequired()) {
                DynamicView dynamicView = null;
                switch (fieldSpecDto.getControlType().toLowerCase()) {
                    case "textbox":
                        dynamicView = factory.getTextComponent(fieldSpecDto, this.registrationService.getRegistrationDto());
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
                    case "fileupload":
                        dynamicView = factory.getDocumentComponent(fieldSpecDto, this.registrationService.getRegistrationDto());
                        SCAN_REQUEST_CODE = SCAN_REQUEST_CODE + 1;
                        requestCodeMap.put(SCAN_REQUEST_CODE, fieldSpecDto.getId());
                        setScanButtonListener(SCAN_REQUEST_CODE, (View) dynamicView, fieldSpecDto);
                        break;
                    case "biometrics":
                        dynamicView = factory.getBiometricsComponent(fieldSpecDto, this.registrationService.getRegistrationDto());
                        setRCaptureButtonListener((View) dynamicView);
                        break;
                }

                if (dynamicView != null) {
                    fieldPanel.addView((View) dynamicView);
                    currentDynamicViews.put(fieldSpecDto.getId(), dynamicView);
                    this.registrationService.getRegistrationDto().addObserver((Observer) dynamicView);
                }
            }
        }
    }

    public void goToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    @OptIn(markerClass = com.google.android.material.badge.ExperimentalBadgeUtils.class)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String fieldId = requestCodeMap.get(requestCode);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:
                    parseDiscoverResponse(data.getStringExtra("Response"));
                    break;
                case 2:
                    parseDeviceInfoResponse(data.getStringExtra("Response"));
                    break;
                case 3:
                    parseRCaptureResponse(data.getStringExtra("Response"));
                    break;
                default:
                    if (requestCodeMap.containsKey(requestCode)) {
                        Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
                        try (InputStream iStream = getContentResolver().openInputStream(uri)) {
                            Spinner sItems = ((Spinner) ((View) currentDynamicViews.get(fieldId)).findViewById(R.id.doctypes_dropdown));
                            this.registrationService.getRegistrationDto().addDocument(fieldId, sItems.getSelectedItem().toString(), getBytes(iStream));
                            View view = ((View) currentDynamicViews.get(fieldId)).findViewById(R.id.doc_saved);
                            view.setVisibility(View.VISIBLE);
                            BadgeDrawable badgeDrawable = BadgeDrawable.create(this);
                            badgeDrawable.setNumber(this.registrationService.getRegistrationDto().getScannedPages(fieldId).size());
                            badgeDrawable.setVisible(true);
                            BadgeUtils.attachBadgeDrawable(badgeDrawable, view);
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to set document to registration dto", e);
                        }
                    } else
                        Toast.makeText(this, "Scan failed", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    private void setRCaptureButtonListener(View view) {
        Button faceButton = view.findViewById(R.id.rcapture_face);
        faceButton.setOnClickListener(v -> {
            discoverSBI("face");
        });
    }

    private void setScanButtonListener(int requestCode, View view, FieldSpecDto fieldSpecDto) {
        Button button = view.findViewById(R.id.scan_doc);
        button.setOnClickListener(v -> {
            int preference = ScanConstants.OPEN_CAMERA;
            Intent intent = new Intent(this, ScanActivity.class);
            intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
            startActivityForResult(intent, requestCode);
        });

        ImageButton previewButton = view.findViewById(R.id.doc_saved);
        previewButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, PreviewDocumentActivity.class);
            intent.putExtra("fieldId", fieldSpecDto.getId());
            //TODO get label based on logged in language
            intent.putExtra("fieldLabel", fieldSpecDto.getLabel().get("eng"));
            startActivity(intent);
        });
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream()) {
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            return byteBuffer.toByteArray();
        }
    }

    private void discoverSBI(String currentModality) {
        Toast.makeText(this, "Started to discover " + currentModality + " SBI", Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.setAction("sbi.reg.device");
        List activities = this.getPackageManager().queryIntentActivities(intent, MATCH_DEFAULT_ONLY);
        if (activities.size() > 0) {
            intent.putExtra("input", "{\"type\":\"" + currentModality + "\"}");
            this.startActivityForResult(intent, 1);
        } else {
            Toast.makeText(getApplicationContext(), "Supported apps not found!", Toast.LENGTH_LONG).show();
        }
    }

    private void info(String callbackId) {
        if (callbackId == null) {
            Toast.makeText(this, "No SBI found!", Toast.LENGTH_LONG).show();
            return;
        }
        Intent localIntent = new Intent();
        localIntent.setAction(callbackId + ".info");
        List activities = getPackageManager().queryIntentActivities(localIntent, MATCH_DEFAULT_ONLY);
        if (activities.size() > 0) {
            Toast.makeText(getApplicationContext(), "Initiating Device info request : " + callbackId, Toast.LENGTH_LONG).show();
            startActivityForResult(localIntent, 2);
        } else {
            Toast.makeText(getApplicationContext(), "Supported apps not found!", Toast.LENGTH_LONG).show();
        }
    }

    private void rcapture(String callbackId, String deviceId) {
        if (deviceId == null || callbackId == null) {
            Toast.makeText(this, "No SBI found!", Toast.LENGTH_LONG).show();
            return;
        }

        Intent localIntent = new Intent();
        //TODO in SBI - callback is wrong in deviceInfo response
        callbackId = callbackId.replace(".info", "");

        localIntent.setAction(callbackId + ".rcapture");
        List activities = getPackageManager().queryIntentActivities(localIntent, MATCH_DEFAULT_ONLY);
        if (activities.size() > 0) {
            Toast.makeText(getApplicationContext(), "Initiating capture request : " + callbackId, Toast.LENGTH_LONG).show();
            localIntent.putExtra("input", "{\"env\":\"Developer\",\"purpose\":\"Registration\",\"specVersion\":\"0.9.5\",\"timeout\":10000,\"captureTime\":\"2021-07-21T15:00:03Z\",\"transactionId\":\"1626879603493\",\"bio\":[{\"type\":\"Face\",\"count\":1,\"exception\":[],\"requestedScore\":40,\"deviceId\":\"" + deviceId + "\",\"deviceSubId\":0,\"previousHash\":\"\",\"bioSubType\":[]}],\"customOpts\":null}");
            startActivityForResult(localIntent, 3);
        } else {
            Toast.makeText(getApplicationContext(), "Supported apps not found!", Toast.LENGTH_LONG).show();
        }
    }

    private byte[] getPayloadBuffer(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            return Base64.getUrlDecoder().decode(parts[1]);
        } catch (Exception e) {
            Log.e(TAG, "Failed to decode payload");
        }
        return null;
    }

    private String getSignature(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            return parts[2];
        } catch (Exception e) {
            Log.e(TAG, "Failed to decode payload");
        }
        return null;
    }

    private void parseDiscoverResponse(String response) {
        String callbackId = null;
        try {
            JSONObject jsonObject = new JSONArray(response).getJSONObject(0);
            if (jsonObject.getJSONObject("error").getInt("errorCode") == 0) {
                callbackId = jsonObject.getString("callbackId");
                String deviceStatus = jsonObject.getString("deviceStatus");
                if (!callbackId.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Discovered : " + callbackId + " With status : " +
                            deviceStatus, Toast.LENGTH_LONG).show();
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse discover response");
        }
        info(callbackId);
    }

    private void parseDeviceInfoResponse(String response) {
        String callbackId = null;
        String serialNo = null;
        try {
            JSONObject jsonObject = new JSONArray(response).getJSONObject(0);
            if (jsonObject.getJSONObject("error").getInt("errorCode") == 0) {
                byte[] payloadBuffer = this.getPayloadBuffer(jsonObject.getString("deviceInfo"));
                JSONObject payload = new JSONObject(new String(payloadBuffer));
                callbackId = payload.getString("callbackId");
                byte[] digitalIdBuffer = this.getPayloadBuffer(payload.getString("digitalId"));
                JSONObject digitalId = new JSONObject(new String(digitalIdBuffer));
                serialNo = digitalId.getString("serialNo");
            }
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse device info response");
        }
        rcapture(callbackId, serialNo);
    }

    private void parseRCaptureResponse(String response) {
        try {
            JSONArray jsonObject = new JSONObject(response).getJSONArray("biometrics");
            String specVersion = jsonObject.getJSONObject(0).getString("specVersion");
            String data = jsonObject.getJSONObject(0).getString("data");
            if (data == null || data.isEmpty()) {
                JSONObject error = jsonObject.getJSONObject(0).getJSONObject("error");
                Toast.makeText(getApplicationContext(), "Biometric Capture failed : " + error.toString(2),
                        Toast.LENGTH_LONG).show();
            } else {
                String signature = this.getSignature(data);
                byte[] payloadBuffer = this.getPayloadBuffer(data);
                String decodedPayload = new String(payloadBuffer);
                JSONObject dataDTO = new JSONObject(decodedPayload);
                String qualityScore = dataDTO.getString("qualityScore");
                String bioValue = dataDTO.getString("bioValue");

                //TODO - better way to handle all modalities
                BiometricsDto biometricsDto = new BiometricsDto("face", "face", specVersion, false,
                        decodedPayload, signature, bioValue, qualityScore);
                ((TextView) ((View) currentDynamicViews.get("individualBiometrics")).findViewById(R.id.sbi_result))
                        .setText(String.format("\nSpecVersion : %s \nQualityScore : %s", specVersion, qualityScore));
                this.registrationService.getRegistrationDto().addBiometric("individualBiometrics",
                        "face", biometricsDto);
                Toast.makeText(getApplicationContext(), "Successfully captured face", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse rcapture response", e);
            Toast.makeText(this, "Failed parsing Capture response : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
