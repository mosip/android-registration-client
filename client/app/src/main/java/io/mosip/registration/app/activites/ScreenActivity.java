package io.mosip.registration.app.activites;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.registration.app.R;
import io.mosip.registration.app.dynamicviews.DynamicComponentFactory;
import io.mosip.registration.app.dynamicviews.DynamicView;
import io.mosip.registration.app.util.BiometricService;
import io.mosip.registration.app.util.ClientConstants;
import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.constant.Modality;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dto.registration.BiometricsDto;
import io.mosip.registration.clientmanager.dto.sbi.*;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.dto.uispec.ProcessSpecDto;
import io.mosip.registration.clientmanager.dto.uispec.ScreenSpecDto;
import io.mosip.registration.clientmanager.exception.ClientCheckedException;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.repository.LanguageRepository;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.RegistrationService;

import io.mosip.registration.clientmanager.util.UserInterfaceHelperService;

import javax.inject.Inject;

import java.io.*;
import java.util.*;


public class ScreenActivity extends DaggerAppCompatActivity implements BiometricService {

    private static final String TAG = ScreenActivity.class.getSimpleName();
    private static final String COLON_SEPARATED_MODULE_NAME = "%s: %s";

    private ViewGroup fieldPanel = null;
    private int BIO_SCAN_REQUEST_CODE = 1;
    private int SCAN_REQUEST_CODE = 99;
    private Map<Integer, String> requestCodeMap = new HashMap<>();
    private Map<String, DynamicView> currentDynamicViews = new HashMap<>();
    private Modality currentModality;
    private String callbackId;

    @Inject
    RegistrationService registrationService;

    @Inject
    IdentitySchemaRepository identitySchemaRepository;

    @Inject
    MasterDataService masterDataService;

    @Inject
    LanguageRepository languageRepository;

    @Inject
    ObjectMapper objectMapper;

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
        DynamicComponentFactory factory = new DynamicComponentFactory(this, masterDataService);

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
            switch (requestCode) {
                case 1:
                    parseDiscoverResponse(data.getExtras());
                    break;
                case 2:
                    parseDeviceInfoResponse(data.getExtras());
                    break;
                case 3:
                    parseRCaptureResponse(data.getExtras());
                    break;
                default:
                    if (requestCodeMap.containsKey(requestCode)) {
                        Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
                        try (InputStream iStream = getContentResolver().openInputStream(uri)) {
                            Spinner sItems = ((Spinner) ((View) currentDynamicViews.get(fieldId)).findViewById(R.id.doctypes_dropdown));
                            EditText editText = ((EditText) ((View) currentDynamicViews.get(fieldId)).findViewById(R.id.doc_refid));
                            this.registrationService.getRegistrationDto().addDocument(fieldId, sItems.getSelectedItem().toString(),
                                    editText == null ? null : editText.getText().toString(), getBytes(iStream));
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
                    break;
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

    @Override
    public void startBiometricCapture(Modality modality) {
        auditManagerService.audit(AuditEvent.BIOMETRIC_CAPTURE, Components.REGISTRATION.getId(), String.format(COLON_SEPARATED_MODULE_NAME, Components.REGISTRATION.getName(), modality.name()));
        currentModality = modality;
        discoverSBI();
    }

    private void queryPackage(Intent intent) throws ClientCheckedException {
        List activities = this.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_ALL);
        //if(activities.size() == 0)
        //    throw new ClientCheckedException("Supported apps not found!");
    }

    private void discoverSBI() {
        try {
            Toast.makeText(this, "Started to discover SBI", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.setAction(RegistrationConstants.DISCOVERY_INTENT_ACTION);
            queryPackage(intent);
            DiscoverRequest discoverRequest = new DiscoverRequest();
            discoverRequest.setType(currentModality.getSingleType().name());
            intent.putExtra(RegistrationConstants.SBI_INTENT_REQUEST_KEY, objectMapper.writeValueAsBytes(discoverRequest));
            this.startActivityForResult(intent, 1);
        } catch (Exception ex) {
            auditManagerService.audit(AuditEvent.DISCOVER_SBI_FAILED, Components.REGISTRATION, ex.getMessage());
            Log.e(TAG, ex.getMessage(), ex);
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void info(String callbackId) {
        if (callbackId == null) {
            Toast.makeText(this, "No SBI found!", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            Intent intent = new Intent();
            intent.setAction(callbackId + RegistrationConstants.D_INFO_INTENT_ACTION);
            queryPackage(intent);
            Toast.makeText(getApplicationContext(), "Initiating Device info request : " + callbackId,
                    Toast.LENGTH_LONG).show();
            startActivityForResult(intent, 2);
        } catch (ClientCheckedException ex) {
            auditManagerService.audit(AuditEvent.DEVICE_INFO_FAILED, Components.REGISTRATION, ex.getMessage());
            Log.e(TAG, ex.getMessage(), ex);
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void rcapture(String callbackId, String deviceId) {
        if (deviceId == null || callbackId == null) {
            Toast.makeText(this, "No SBI found!", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            Intent intent = new Intent();
            //callbackId = callbackId.replace("\\.info","");
            intent.setAction(callbackId + RegistrationConstants.R_CAPTURE_INTENT_ACTION);
            queryPackage(intent);
            Toast.makeText(this, "Initiating capture request : " + callbackId, Toast.LENGTH_LONG).show();
            CaptureRequest captureRequest = new CaptureRequest();
            captureRequest.setEnv("Developer");
            captureRequest.setPurpose("Registration");
            captureRequest.setTimeout(10000);
            captureRequest.setSpecVersion("0.9.5");
            List<CaptureBioDetail> list = new ArrayList<>();
            list.add(getBioObject(deviceId));
            captureRequest.setBio(list);
            intent.putExtra("input", objectMapper.writeValueAsBytes(captureRequest));
            startActivityForResult(intent, 3);
        } catch (Exception ex) {
            auditManagerService.audit(AuditEvent.R_CAPTURE_FAILED, Components.REGISTRATION, ex.getMessage());
            Log.e(TAG, ex.getMessage(), ex);
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private CaptureBioDetail getBioObject(String deviceId) {
        CaptureBioDetail detail = new CaptureBioDetail();
        detail.setType(currentModality.getSingleType().name());
        detail.setBioSubType(Modality.getSpecBioSubType(currentModality.getAttributes()).toArray(new String[0]));
        detail.setCount(currentModality.getAttributes().size());
        detail.setException(new String[]{});
        detail.setDeviceId(deviceId);
        detail.setRequestedScore(40);
        detail.setDeviceSubId(String.valueOf(currentModality.getDeviceSubId()));
        detail.setPreviousHash("");
        return detail;
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

    private void parseDiscoverResponse(Bundle bundle) {
        try {
            byte[] bytes = bundle.getByteArray(RegistrationConstants.SBI_INTENT_RESPONSE_KEY);
            List<DeviceDto> list = objectMapper.readValue(bytes, new TypeReference<List<DeviceDto>>() {
            });
            if (list.isEmpty()) {
                Toast.makeText(this, "No SBI discovered!", Toast.LENGTH_LONG).show();
                return;
            }

            DeviceDto deviceDto = list.get(0);
            if (deviceDto.getError() != null && !"0".equals(deviceDto.getError().getErrorCode())) {
                Log.e(TAG, deviceDto.getError().getErrorCode() + " --> " + deviceDto.getError().getErrorInfo());
                Toast.makeText(this, deviceDto.getError().getErrorInfo(), Toast.LENGTH_LONG).show();
                return;
            }
            callbackId = deviceDto.getCallbackId();
            String deviceStatus = deviceDto.getDeviceStatus();
            Log.i(TAG, callbackId + " --> " + deviceStatus);
        } catch (Exception e) {
            auditManagerService.audit(AuditEvent.DISCOVER_SBI_PARSE_FAILED, Components.REGISTRATION, e.getMessage());
            Log.e(TAG, "Failed to parse discover response", e);
        }
        info(callbackId);
    }

    private void parseDeviceInfoResponse(Bundle bundle) {
        String callbackId = null;
        String serialNo = null;
        try {
            byte[] bytes = bundle.getByteArray(RegistrationConstants.SBI_INTENT_RESPONSE_KEY);
            List<InfoResponse> list = objectMapper.readValue(bytes, new TypeReference<List<InfoResponse>>() {
            });

            if (list.isEmpty()) {
                Toast.makeText(this, "No SBI discovered!", Toast.LENGTH_LONG).show();
                return;
            }

            InfoResponse response = list.get(0);
            if (response.getError() != null && !"0".equals(response.getError().getErrorCode())) {
                Log.e(TAG, response.getError().getErrorCode() + " --> " + response.getError().getErrorInfo());
                Toast.makeText(this, response.getError().getErrorInfo(), Toast.LENGTH_LONG).show();
                return;
            }

            byte[] payloadBuffer = this.getPayloadBuffer(response.getDeviceInfo());
            DeviceDto deviceDto = objectMapper.readValue(payloadBuffer, DeviceDto.class);
            callbackId = deviceDto.getCallbackId().replace(".info", "");
            byte[] digitalIdBuffer = this.getPayloadBuffer(deviceDto.getDigitalId());
            DigitalId digitalId = objectMapper.readValue(digitalIdBuffer, DigitalId.class);
            serialNo = digitalId.getSerialNo();
            Log.i(TAG, callbackId + " --> " + serialNo);
        } catch (Exception e) {
            auditManagerService.audit(AuditEvent.DEVICE_INFO_PARSE_FAILED, Components.REGISTRATION, e.getMessage());
            Log.e(TAG, "Failed to parse device info response", e);
        }
        rcapture(callbackId, serialNo);
    }

    private void parseRCaptureResponse(Bundle bundle) {
        try {
            Uri uri = bundle.getParcelable(RegistrationConstants.SBI_INTENT_RESPONSE_KEY);
            InputStream respData = getContentResolver().openInputStream(uri);
            CaptureResponse captureResponse = objectMapper.readValue(respData, new TypeReference<CaptureResponse>() {
            });
            List<CaptureRespDetail> list = captureResponse.getBiometrics();
            for (CaptureRespDetail bio : list) {
                if (bio.getError() != null && !"0".equals(bio.getError().getErrorCode())) {
                    Log.e(TAG, bio.getError().getErrorCode() + " --> " + bio.getError().getErrorInfo());
                    continue;
                }
                String signature = this.getSignature(bio.getData());
                byte[] payloadBuffer = this.getPayloadBuffer(bio.getData());
                CaptureDto captureDto = objectMapper.readValue(payloadBuffer, new TypeReference<CaptureDto>() {
                });
                if (captureDto.getQualityScore() > 0 && captureDto.getBioValue() != null || !captureDto.getBioValue().equals("")) {
                    BiometricsDto biometricsDto = new BiometricsDto(captureDto.getBioType(), captureDto.getBioSubType(), captureDto.getBioValue(),
                            bio.getSpecVersion(), false, new String(payloadBuffer), signature, false, 1, 0,
                            captureDto.getQualityScore());
                    this.registrationService.getRegistrationDto().addBiometric("individualBiometrics",
                            Modality.getBioAttribute(captureDto.getBioSubType()), biometricsDto);
                }
            }
            displayCapturedImage("individualBiometrics");
        } catch (Exception e) {
            auditManagerService.audit(AuditEvent.R_CAPTURE_PARSE_FAILED, Components.REGISTRATION, e.getMessage());
            Log.e(TAG, "Failed to parse rcapture response", e);
            Toast.makeText(this, "Failed parsing Capture response : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void displayCapturedImage(String fieldId) throws Exception {
        List<BiometricsDto> list = this.registrationService.getRegistrationDto().getBiometrics(fieldId, currentModality);
        if (list.isEmpty()) {
            Toast.makeText(this, "No biometric data saved!", Toast.LENGTH_LONG).show();
            return;
        }

        Bitmap missingImage = BitmapFactory.decodeResource(getResources(), R.drawable.wrong);
        ImageView imageView = ((View) currentDynamicViews.get(fieldId)).findViewWithTag(currentModality.name());

        switch (currentModality) {
            case FACE:
                imageView.setImageBitmap(UserInterfaceHelperService.getFaceBitMap(list.get(0)));
                break;
            case FINGERPRINT_SLAB_LEFT:
                imageView.setImageBitmap(UserInterfaceHelperService.combineBitmaps(Arrays.asList(
                        UserInterfaceHelperService.getFingerBitMap(list, "Left LittleFinger"),
                        UserInterfaceHelperService.getFingerBitMap(list, "Left RingFinger"),
                        UserInterfaceHelperService.getFingerBitMap(list, "Left MiddleFinger"),
                        UserInterfaceHelperService.getFingerBitMap(list, "Left IndexFinger")), missingImage));
                break;
            case FINGERPRINT_SLAB_RIGHT:
                imageView.setImageBitmap(UserInterfaceHelperService.combineBitmaps(Arrays.asList(
                        UserInterfaceHelperService.getFingerBitMap(list, "Right IndexFinger"),
                        UserInterfaceHelperService.getFingerBitMap(list, "Right MiddleFinger"),
                        UserInterfaceHelperService.getFingerBitMap(list, "Right RingFinger"),
                        UserInterfaceHelperService.getFingerBitMap(list, "Right LittleFinger")), missingImage));
                break;
            case FINGERPRINT_SLAB_THUMBS:
                imageView.setImageBitmap(UserInterfaceHelperService.combineBitmaps(Arrays.asList(
                        UserInterfaceHelperService.getFingerBitMap(list, "Left Thumb"),
                        UserInterfaceHelperService.getFingerBitMap(list, "Right Thumb")), missingImage));
                break;
            case IRIS_DOUBLE:
                imageView.setImageBitmap(UserInterfaceHelperService.combineBitmaps(Arrays.asList(
                        UserInterfaceHelperService.getIrisBitMap(list, "Left"),
                        UserInterfaceHelperService.getIrisBitMap(list, "Right")), missingImage));
                break;
        }

        Toast.makeText(getApplicationContext(), "Registration Capture completed", Toast.LENGTH_LONG).show();
    }
}
