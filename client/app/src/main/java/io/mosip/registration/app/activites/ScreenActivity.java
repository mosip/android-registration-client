package io.mosip.registration.app.activites;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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
import com.gemalto.jp2.JP2Decoder;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.biometrics.util.face.FaceBDIR;
import io.mosip.biometrics.util.finger.FingerBDIR;
import io.mosip.biometrics.util.iris.IrisBDIR;
import io.mosip.registration.app.R;
import io.mosip.registration.app.dynamicviews.DynamicComponentFactory;
import io.mosip.registration.app.dynamicviews.DynamicView;
import io.mosip.registration.app.util.BiometricService;
import io.mosip.registration.app.util.ClientConstants;
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
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.RegistrationService;

import io.mosip.registration.keymanager.util.CryptoUtil;

import javax.inject.Inject;

import java.io.*;
import java.util.*;


public class ScreenActivity extends DaggerAppCompatActivity  implements BiometricService {

    private static final String TAG = ScreenActivity.class.getSimpleName();
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

                loadScreenFields(screen.get());
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

        } catch (Throwable t) {
            Log.e(TAG, "Failed to launch registration screen", t);
            goToHome();
        }
    }

    private void loadScreenFields(ScreenSpecDto screenSpecDto) throws Exception {
        fieldPanel.removeAllViews();
        DynamicComponentFactory factory = new DynamicComponentFactory(this, masterDataService);

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
                    case "checkbox" :
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
                }
            }
        }
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
                            Log.e(TAG, "Failed to set document to registration dto", e);
                        } finally {
                            getContentResolver().delete(uri, null, null);
                        }
                    } else
                        Toast.makeText(this, R.string.doc_scan_fail, Toast.LENGTH_LONG).show();
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
            int preference = ScanConstants.OPEN_CAMERA;
            Intent intent = new Intent(this, ScanActivity.class);
            intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
            startActivityForResult(intent, requestCode);
        });

        TextView textView = view.findViewById(R.id.doc_preview);
        textView.setOnClickListener(v -> {
            Intent intent = new Intent(this, PreviewDocumentActivity.class);
            List<String> labels = new ArrayList<>();
            for(String language : selectedLanguages) {
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
            Log.e(TAG, ex.getMessage(), ex);
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private CaptureBioDetail getBioObject(String deviceId){
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
            List<DeviceDto> list = objectMapper.readValue(bytes, new TypeReference<List<DeviceDto>>() {});
            if(list.isEmpty()) {
                Toast.makeText(this, "No SBI discovered!", Toast.LENGTH_LONG).show();
                return;
            }

            DeviceDto deviceDto = list.get(0);
            if(deviceDto.getError() != null && !"0".equals(deviceDto.getError().getErrorCode())) {
                Log.e(TAG, deviceDto.getError().getErrorCode() + " --> " + deviceDto.getError().getErrorInfo());
                Toast.makeText(this, deviceDto.getError().getErrorInfo(), Toast.LENGTH_LONG).show();
                return;
            }
            callbackId = deviceDto.getCallbackId();
            String deviceStatus = deviceDto.getDeviceStatus();
            Log.i(TAG, callbackId + " --> " + deviceStatus);
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse discover response", e);
        }
        info(callbackId);
    }

    private void parseDeviceInfoResponse(Bundle bundle) {
        String callbackId = null;
        String serialNo = null;
        try {
            byte[] bytes = bundle.getByteArray(RegistrationConstants.SBI_INTENT_RESPONSE_KEY);
            List<InfoResponse> list = objectMapper.readValue(bytes, new TypeReference<List<InfoResponse>>() {});
            if(list.isEmpty()) {
                Toast.makeText(this, "No SBI discovered!", Toast.LENGTH_LONG).show();
                return;
            }

            InfoResponse response = list.get(0);
            if(response.getError() != null && !"0".equals(response.getError().getErrorCode())) {
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
            Log.e(TAG, "Failed to parse device info response", e);
        }
        rcapture(callbackId, serialNo);
    }

    private void parseRCaptureResponse(Bundle bundle) {
        try {
            Uri uri = bundle.getParcelable(RegistrationConstants.SBI_INTENT_RESPONSE_KEY);
            InputStream respData = getContentResolver().openInputStream(uri);
            CaptureResponse captureResponse = objectMapper.readValue(respData, new TypeReference<CaptureResponse>() {});
            List<CaptureRespDetail> list = captureResponse.getBiometrics();
            for(CaptureRespDetail bio : list) {
                if(bio.getError() != null && !"0".equals(bio.getError().getErrorCode())) {
                    Log.e(TAG, bio.getError().getErrorCode() + " --> " + bio.getError().getErrorInfo());
                    continue;
                }
                String signature = this.getSignature(bio.getData());
                byte[] payloadBuffer = this.getPayloadBuffer(bio.getData());
                CaptureDto captureDto = objectMapper.readValue(payloadBuffer, new TypeReference<CaptureDto>() {});
                if(captureDto.getQualityScore() > 0 && captureDto.getBioValue() != null || !captureDto.getBioValue().equals("")) {
                    BiometricsDto biometricsDto = new BiometricsDto(captureDto.getBioType(), captureDto.getBioSubType(), captureDto.getBioValue(),
                            bio.getSpecVersion(), false, new String(payloadBuffer), signature, false, 1, 0,
                            captureDto.getQualityScore());
                    this.registrationService.getRegistrationDto().addBiometric("individualBiometrics",
                            Modality.getBioAttribute(captureDto.getBioSubType()), biometricsDto);
                }
            }

            displayCapturedImage("individualBiometrics");

        } catch (Exception e) {
            Log.e(TAG, "Failed to parse rcapture response", e);
            Toast.makeText(this, "Failed parsing Capture response : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void displayCapturedImage(String fieldId) throws Exception {
        List<BiometricsDto> list = this.registrationService.getRegistrationDto().getBiometrics(fieldId, currentModality);
        if(list.isEmpty()) {
            Toast.makeText(this, "No biometric data saved!", Toast.LENGTH_LONG).show();
            return;
        }

        ImageView imageView = ((View) currentDynamicViews.get(fieldId)).findViewWithTag(currentModality.name());

        switch (currentModality) {
            case FACE:
                try(ByteArrayInputStream bais = new ByteArrayInputStream(CryptoUtil.base64decoder.decode(list.get(0).getBioValue()));
                    DataInputStream inputStream = new DataInputStream(bais);) {
                    FaceBDIR faceBDIR = new FaceBDIR(inputStream);
                    byte[] bytes = faceBDIR.getRepresentation().getRepresentationData().getImageData().getImage();
                    Bitmap bitmap = new JP2Decoder(bytes).decode();
                    imageView.setImageBitmap(bitmap);
                }
                 break;
            case FINGERPRINT_SLAB_LEFT:
                imageView.setImageBitmap(combineBitmaps(Arrays.asList(getFingerBitMap(list, "Left LittleFinger"),
                        getFingerBitMap(list, "Left RingFinger"),
                        getFingerBitMap(list, "Left MiddleFinger"),
                        getFingerBitMap(list, "Left IndexFinger"))));
                break;
            case FINGERPRINT_SLAB_RIGHT:
                imageView.setImageBitmap(combineBitmaps(Arrays.asList(getFingerBitMap(list, "Right IndexFinger"),
                        getFingerBitMap(list, "Right MiddleFinger"),
                        getFingerBitMap(list, "Right RingFinger"),
                        getFingerBitMap(list, "Right LittleFinger"))));
                break;
            case FINGERPRINT_SLAB_THUMBS:
                imageView.setImageBitmap(combineBitmaps(Arrays.asList(getFingerBitMap(list, "Left Thumb"),
                        getFingerBitMap(list, "Right Thumb"))));
                break;
            case IRIS_DOUBLE:
                imageView.setImageBitmap(combineBitmaps(Arrays.asList(getIrisBitMap(list, "Left"),
                        getIrisBitMap(list, "Right"))));
                break;
        }

        Toast.makeText(getApplicationContext(), "Registration Capture completed", Toast.LENGTH_LONG).show();
    }

    private Bitmap getFingerBitMap(List<BiometricsDto> list, String attribute) throws IOException {
        Optional<BiometricsDto> result = list.stream().filter( dto -> attribute.equals(dto.getBioSubType())).findFirst();
        if(!result.isPresent())
            return null;

        try(ByteArrayInputStream bais = new ByteArrayInputStream(CryptoUtil.base64decoder.decode(result.get().getBioValue()));
            DataInputStream inputStream = new DataInputStream(bais);) {
            FingerBDIR fingerBDIR = new FingerBDIR(inputStream);
            byte[] bytes = fingerBDIR.getRepresentation().getRepresentationBody().getImageData().getImage();
            return new JP2Decoder(bytes).decode();
        }
    }

    private Bitmap getIrisBitMap(List<BiometricsDto> list, String attribute) {
        Optional<BiometricsDto> result = list.stream().filter( dto -> attribute.equals(dto.getBioSubType())).findFirst();
        if(!result.isPresent())
            return null;

        try(ByteArrayInputStream bais = new ByteArrayInputStream(CryptoUtil.base64decoder.decode(result.get().getBioValue()));
            DataInputStream inputStream = new DataInputStream(bais);) {
            IrisBDIR irisBDIR = new IrisBDIR(inputStream);
            byte[] bytes = irisBDIR.getRepresentation()
                    .getRepresentationData().getImageData().getImage();
            return new JP2Decoder(bytes).decode();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
        return null;
    }

    private Bitmap combineBitmaps(List<Bitmap> images) {
        // Get the size of the images combined side by side.
        int width = 0;
        int height = 0;
        for(Bitmap image : images) {
            if(image == null)
                image = BitmapFactory.decodeResource(getResources(), R.drawable.wrong);
            width = width + image.getWidth();
            height = image.getHeight() > height ? image.getHeight() : height;
        }

        // Create a Bitmap large enough to hold both input images and a canvas to draw to this
        // combined bitmap.
        Bitmap combined = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(combined);

        // Render both input images into the combined bitmap and return it.
        float left = 0f;
        float top = 0f;
        for(Bitmap image : images) {
            if(image == null)
                image = BitmapFactory.decodeResource(getResources(), R.drawable.wrong);
            canvas.drawBitmap(image, left, top, null);
            left = left + image.getWidth();
        }
        return combined;
    }
}
