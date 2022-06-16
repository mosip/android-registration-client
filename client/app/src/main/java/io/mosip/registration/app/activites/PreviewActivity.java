package io.mosip.registration.app.activites;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.dto.http.ResponseWrapper;
import io.mosip.registration.clientmanager.dto.http.ServiceError;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.SyncRestUtil;
import io.mosip.registration.clientmanager.util.UserInterfaceHelperService;
import io.mosip.registration.packetmanager.dto.SimpleType;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.registration.app.R;
import io.mosip.registration.clientmanager.dto.registration.BiometricsDto;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.inject.Inject;

public class PreviewActivity extends DaggerAppCompatActivity {

    private static final String TAG = PreviewActivity.class.getSimpleName();
    private static final String SLASH = "/";

    private WebView webView;

    @Inject
    RegistrationService registrationService;

    @Inject
    IdentitySchemaRepository identitySchemaRepository;

    @Inject
    MasterDataService masterDataService;

    @Inject
    SyncRestUtil syncRestFactory;

    @Inject
    SyncRestService syncRestService;

    @Inject
    AuditManagerService auditManagerService;

    private String webViewContent;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        startActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        webViewContent = null;
        super.onCreate(savedInstanceState);
        startActivity();
    }

    private void startActivity() {
        webViewContent = null;
        setContentView(R.layout.activity_preview);
        webView = findViewById(R.id.registration_preview);

        //to display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.preview_title);

        try {
            RegistrationDto registrationDto = this.registrationService.getRegistrationDto();
            webViewContent = getTemplate(registrationDto, "Android", "reg-preview-template-part", true);
        } catch (Exception e) {
            Log.e(TAG, "Failed to set the preview content", e);
        }

        final EditText usernameEditText = findViewById(R.id.packet_auth_username);
        final EditText passwordEditText = findViewById(R.id.packet_auth_pwd);
        final ProgressBar loadingProgressBar = findViewById(R.id.auth_loading);
        final Button button = findViewById(R.id.createpacket);
        button.setOnClickListener( v -> {
            auditManagerService.audit(AuditEvent.CREATE_PACKET_AUTH, Components.REGISTRATION);
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            if(validateLogin(username, password)) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                doPacketAuth(username, password, loadingProgressBar);
            }
        });

        webView.loadDataWithBaseURL(null, webViewContent, "text/HTML", "UTF-8", null);
        auditManagerService.audit(AuditEvent.LOADED_REGISTRATION_PREVIEW, Components.REGISTRATION);
    }

    public String getTemplate(RegistrationDto registrationDto, String templateName, String templateTypeCode, boolean isPreview) throws Exception {
        StringWriter writer = new StringWriter();
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.init();

        String templateText = this.masterDataService.getPreviewTemplateContent(templateName, templateTypeCode, "eng");
        InputStream is = new ByteArrayInputStream(templateText.getBytes(StandardCharsets.UTF_8));

        VelocityContext velocityContext = new VelocityContext();

        Double version = identitySchemaRepository.getLatestSchemaVersion();
        if(version == null)
            throw new Exception("No Schema found");
        List<FieldSpecDto> schemaFields = identitySchemaRepository.getAllFieldSpec(getApplicationContext(), version);

        setBasicDetails(isPreview, registrationDto, velocityContext);

        Map<String, Map<String, Object>> demographicsData = new HashMap<>();
        Map<String, Map<String, Object>> documentsData = new HashMap<>();
        Map<String, Map<String, Object>> biometricsData = new HashMap<>();

        for (FieldSpecDto field : schemaFields) {
            switch (field.getType()) {
                case "documentType":
                    Map<String, Object> docData = getDocumentData(field, registrationDto, velocityContext);
                    if(docData != null) { documentsData.put(field.getId(), docData); }
                    break;

                case "biometricsType":
                    Map<String, Object> bioData = getBiometricData(field, registrationDto, isPreview, velocityContext);
                    if(bioData != null) { biometricsData.put(field.getId(), bioData); }
                    break;

                default:
                    Map<String, Object> demoData = getDemographicData(field, registrationDto);
                    if(demoData != null) { demographicsData.put(field.getId(), demoData); }
                    break;
            }
        }
        velocityContext.put("demographics", demographicsData);
        velocityContext.put("documents", documentsData);
        velocityContext.put("biometrics", biometricsData);

        velocityEngine.evaluate(velocityContext, writer, "templateManager-mergeTemplate", new InputStreamReader(is));
        return writer.toString();
    }

    private Map<String, Object> getBiometricData(FieldSpecDto field, RegistrationDto registrationDto, boolean isPreview, VelocityContext velocityContext) throws Exception {
        velocityContext.put("Fingers", getString(R.string.fingers));
        velocityContext.put("Iris", getString(R.string.double_iris));
        velocityContext.put("Face", getString(R.string.face_label));

        List<BiometricsDto> capturedList = new ArrayList<>();
        for (String attribute : field.getBioAttributes()) {
            String key = String.format("%s_%s", field.getId(), attribute);
            if (registrationDto.getBiometrics().containsKey(key))
                capturedList.add(registrationDto.getBiometrics().get(key));
        }

        if(capturedList.isEmpty()) { return null; }

        Map<String, Object> bioData = new HashMap<>();

        List<BiometricsDto> capturedFingers = capturedList.stream()
                .filter(d -> d.getModality().toLowerCase().contains("finger")).collect(Collectors.toList());
        List<BiometricsDto> capturedIris = capturedList.stream()
                .filter(d -> d.getModality().toLowerCase().contains("iris")).collect(Collectors.toList());
        List<BiometricsDto> capturedFace = capturedList.stream()
                .filter(d -> d.getModality().toLowerCase().contains("face")).collect(Collectors.toList());

        bioData.put("FingerCount", capturedFingers.stream().filter( b -> b.getBioValue() != null).count());
        bioData.put("IrisCount", capturedIris.stream().filter( b -> b.getBioValue() != null).count());
        bioData.put("FaceCount", 1); //TODO check this
        bioData.put("subType", field.getSubType());
        bioData.put("label", getFieldLabel(field, registrationDto));

        /*Bitmap missingImage = BitmapFactory.decodeResource(getResources(), R.drawable.wrong);
        Optional<BiometricsDto> result = capturedIris.stream()
                .filter(b -> b.getBioSubType().equalsIgnoreCase("Left")).findFirst();
        if (result.isPresent()) {
            BiometricsDto biometricsDto = result.get();
            bioData.put("LeftEye", (biometricsDto.getBioValue() != null) ? "&#10003;" : "&#10008;");
            setBiometricImage(bioData, "CapturedLeftEye", isPreview ? R.drawable.cross_mark : R.drawable.eye,
                    isPreview ?  UserInterfaceHelperService.getIrisBitMap(biometricsDto) : null);
        }

        result = capturedIris.stream()
                .filter(b -> b.getBioSubType().equalsIgnoreCase("Right")).findFirst();
        if (result.isPresent()) {
            BiometricsDto biometricsDto = result.get();
            bioData.put("RightEye", (biometricsDto.getBioValue() != null) ? "&#10003;" : "&#10008;");
            setBiometricImage(bioData, "CapturedRightEye", isPreview ? R.drawable.cross_mark : R.drawable.eye,
                    isPreview ? UserInterfaceHelperService.getIrisBitMap(biometricsDto) : null);
        }

        if(!capturedFingers.isEmpty()) {
            setFingerRankings(capturedFingers, Modality.FINGERPRINT_SLAB_LEFT.getAttributes(), bioData);
            Bitmap leftHandBitmaps = UserInterfaceHelperService.combineBitmaps(Arrays.asList(
                    UserInterfaceHelperService.getFingerBitMap(capturedFingers, "Left LittleFinger"),
                    UserInterfaceHelperService.getFingerBitMap(capturedFingers, "Left RingFinger"),
                    UserInterfaceHelperService.getFingerBitMap(capturedFingers, "Left MiddleFinger"),
                    UserInterfaceHelperService.getFingerBitMap(capturedFingers, "Left IndexFinger")), missingImage);
            setBiometricImage(bioData, "CapturedLeftSlap", isPreview ? 0 : R.drawable.left_palm,
                    isPreview ? leftHandBitmaps : null);

            setFingerRankings(capturedFingers, Modality.FINGERPRINT_SLAB_RIGHT.getAttributes(), bioData);
            Bitmap rightHandBitmaps = UserInterfaceHelperService.combineBitmaps(Arrays.asList(
                    UserInterfaceHelperService.getFingerBitMap(capturedFingers, "Right IndexFinger"),
                    UserInterfaceHelperService.getFingerBitMap(capturedFingers, "Right MiddleFinger"),
                    UserInterfaceHelperService.getFingerBitMap(capturedFingers, "Right RingFinger"),
                    UserInterfaceHelperService.getFingerBitMap(capturedFingers, "Right LittleFinger")), missingImage);
            setBiometricImage(bioData, "CapturedRightSlap", isPreview ? 0 : R.drawable.right_palm,
                    isPreview ? rightHandBitmaps : null);

            setFingerRankings(capturedFingers, Modality.FINGERPRINT_SLAB_THUMBS.getAttributes(), bioData);
            Bitmap thumbsBitmap = UserInterfaceHelperService.combineBitmaps(Arrays.asList(
                    UserInterfaceHelperService.getFingerBitMap(capturedFingers, "Left Thumb"),
                    UserInterfaceHelperService.getFingerBitMap(capturedFingers, "Right Thumb")), missingImage);
            setBiometricImage(bioData, "CapturedThumbs", isPreview ? 0 : R.drawable.thumbs,
                    isPreview ? thumbsBitmap : null);
        }*/

        if(!capturedFace.isEmpty()) {
            Bitmap faceBitmap = UserInterfaceHelperService.getFaceBitMap(capturedFace.get(0));
            setBiometricImage(bioData, "FaceImageSource", isPreview ? 0 : R.drawable.face,
                    isPreview ? faceBitmap : null);

            if("applicant".equalsIgnoreCase(field.getSubType())) {
                setBiometricImage(velocityContext, "ApplicantImageSource", faceBitmap);
            }
        }
        return bioData;
    }

    private void setBiometricImage(Map<String, Object> templateValues, String key, int imagePath, Bitmap bitmap) {
        if (bitmap != null) {
            try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                String encodedBytes = Base64.encodeToString(byteArray, Base64.DEFAULT);
                templateValues.put(key, "data:image/png;base64," + encodedBytes);
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage(), ex);
            }
        } else if (imagePath != 0) {
            templateValues.put(key, getImage(imagePath));
        }
    }

    private void setBiometricImage(VelocityContext velocityContext, String key, Bitmap bitmap) {
        if (bitmap != null) {
            try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                String encodedBytes = Base64.encodeToString(byteArray, Base64.DEFAULT);
                velocityContext.put(key, "data:image/png;base64," + encodedBytes);
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage(), ex);
            }
        }
    }

    private void setFingerRankings(List<BiometricsDto> capturedFingers,	List<String> fingers, Map<String, Object> data) {
        Map<String, Float> sortedValues = capturedFingers.stream()
                .filter(b -> fingers.contains(b.getBioSubType().replaceAll("\\s+", "")) && b.getBioValue() != null)
                .sorted(Comparator.comparing(BiometricsDto::getQualityScore))
                .collect(Collectors.toMap(BiometricsDto::getBioSubType, BiometricsDto::getQualityScore));

        int rank = 0;
        double prev = 0;
        Map<String, Integer> rankings = new HashMap<>();
        for (Map.Entry<String, Float> entry : sortedValues.entrySet()) {
            rankings.put(entry.getKey(), prev == 0 ? ++rank : entry.getValue() == prev ? rank : ++rank);
            prev = entry.getValue();
        }

        for (String finger : fingers) {
            Optional<BiometricsDto> result = capturedFingers.stream()
                    .filter(b -> b.getBioSubType().equalsIgnoreCase(finger)).findFirst();
            if (result.isPresent()) {
                data.put(finger, result.get().getBioValue() == null ? "&#10008;" :
                        rankings.get(finger));
            }
        }
    }

    private String getImage(int imagePath) {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteStream);
            byte[] byteArray = byteStream.toByteArray();
            String imageEncodedBytes = Base64.encodeToString(byteArray,Base64.DEFAULT);
            return "data:image/jpeg;base64," + imageEncodedBytes;
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
        return "";
    }

    private void setBasicDetails(boolean isPreview, RegistrationDto registrationDto, VelocityContext velocityContext) {
        velocityContext.put("isPreview", isPreview);
        velocityContext.put("ApplicationIDLabel", getString(R.string.app_id));
        velocityContext.put("ApplicationID", registrationDto.getRId());
        velocityContext.put("UINLabel", getString(R.string.uin));
        velocityContext.put("UIN", registrationDto.getDemographics().get("UIN"));

        LocalDateTime currentTime = OffsetDateTime.now().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        velocityContext.put("Date", currentTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")));
        velocityContext.put("DateLabel", getString(R.string.date));

        velocityContext.put("DemographicInfo", getString(R.string.demographic_info));
        velocityContext.put("Photo", getString(R.string.photo));
        velocityContext.put("DocumentsLabel", getString(R.string.documents));
        velocityContext.put("BiometricsLabel", getString(R.string.biometrics));
        velocityContext.put("FaceLabel", getString(R.string.face_label));
        velocityContext.put("ExceptionPhotoLabel", getString(R.string.exception_photo_label));
        velocityContext.put("RONameLabel", getString(R.string.ro_label));
        velocityContext.put("ROName", "110011");
        velocityContext.put("RegCenterLabel", getString(R.string.reg_center));
        velocityContext.put("RegCenter", "10011");
        velocityContext.put("ImportantGuidelines", getString(R.string.imp_guidelines));

        velocityContext.put("LeftEyeLabel", getString(R.string.left_iris));
        velocityContext.put("RightEyeLabel", getString(R.string.right_iris));
        velocityContext.put("LeftPalmLabel", getString(R.string.left_slap));
        velocityContext.put("RightPalmLabel", getString(R.string.right_slap));
        velocityContext.put("ThumbsLabel", getString(R.string.thumbs_label));
    }

    private Map<String, Object> getDemographicData(FieldSpecDto field, RegistrationDto registrationDto) {
        Map<String, Object> data = null;
        if("UIN".equalsIgnoreCase(field.getId()) || "IDSchemaVersion".equalsIgnoreCase(field.getId()))
            return null;

        String value = getValue(registrationDto.getDemographics().get(field.getId()));
        if (value != null && !value.isEmpty()) {
            data = new HashMap<>();
            data.put("label", getFieldLabel(field, registrationDto));
            data.put("value", getFieldValue(field, registrationDto));
        }
        return data;
    }

    private String getValue(Object fieldValue) {
        String value = "";

        if (fieldValue instanceof String || fieldValue instanceof Integer || fieldValue instanceof BigInteger
                || fieldValue instanceof Double) {
            value = String.valueOf(fieldValue);
        } else {
            if (null != fieldValue) {
                List<SimpleType> valueList = (List<SimpleType>) fieldValue;
                value = valueList.get(0).getValue();
            }
        }
        return value;
    }

    private String getValue(Object fieldValue, String lang) {
        String value = "";

        if (fieldValue instanceof List<?>) {
            Optional<SimpleType> demoValueInRequiredLang = ((List<SimpleType>) fieldValue).stream()
                    .filter(valueDTO -> valueDTO.getLanguage().equals(lang)).findFirst();

            if (demoValueInRequiredLang.isPresent() && demoValueInRequiredLang.get().getValue() != null) {
                value = demoValueInRequiredLang.get().getValue();
            }
        } else if (fieldValue instanceof String || fieldValue instanceof Integer || fieldValue instanceof BigInteger
                || fieldValue instanceof Double) {
            value = String.valueOf(fieldValue);
        }

        return value == null ? "" : value;
    }

    private Object getFieldLabel(FieldSpecDto field, RegistrationDto registrationDto) {
        List<String> labels = new ArrayList<>();
        List<String> selectedLanguages = registrationDto.getSelectedLanguages();
        for (String selectedLanguage : selectedLanguages) {
            labels.add(field.getLabel().get(selectedLanguage));
        }
        return String.join(SLASH, labels);
    }

    private String getFieldValue(FieldSpecDto field, RegistrationDto registrationDto) {
        Object fieldValue = registrationDto.getDemographics().get(((FieldSpecDto) field).getId());
        List<String> values = new ArrayList<>();
        List<String> selectedLanguages = registrationDto.getSelectedLanguages();
        for (String selectedLanguage : selectedLanguages) {
            values.add(getValue(fieldValue, selectedLanguage));
            if (!field.getType().equalsIgnoreCase("simpleType")) {
                return String.join(SLASH, values);
            }
        }
        return String.join(SLASH, values);
    }

    private Map<String, Object> getDocumentData(FieldSpecDto field, RegistrationDto registrationDto, VelocityContext velocityContext) {
        Map<String, Object> data = null;
        if(registrationDto.getDocuments().get(field.getId()) != null) {
            data = new HashMap<>();
            data.put("label", getFieldLabel(field, registrationDto));
            data.put("value", registrationDto.getDocuments().get(field.getId()).getType());
        }
        return data;
    }

    private boolean validateLogin(String username, String password){
        if(username == null || username.trim().length() == 0){
            Toast.makeText(this, R.string.username_required, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password == null || password.trim().length() == 0){
            Toast.makeText(this, R.string.password_required, Toast.LENGTH_SHORT).show();
            return false;
        }
        //TODO check if the username is logged-in user and mapped to correct registration center
        return true;
    }

    private void doPacketAuth(final String username,final String password, final ProgressBar loadingProgressBar){
        //TODO check if the machine is online, if offline check password hash locally
        Call<ResponseWrapper<String>> call = syncRestService.login(syncRestFactory.getAuthRequest(username, password));
        call.enqueue(new Callback<ResponseWrapper<String>>() {
            @Override
            public void onResponse(Call call, Response response) {
                loadingProgressBar.setVisibility(View.INVISIBLE);
                ResponseWrapper<String> wrapper = (ResponseWrapper<String>) response.body();
                if(response.isSuccessful()) {
                    ServiceError error = SyncRestUtil.getServiceError(wrapper);
                    if(error == null) {
                        submitForm(username);
                        return;
                    }
                    Log.e(TAG, response.raw().toString());
                    Toast.makeText(PreviewActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(PreviewActivity.this, R.string.login_failed, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                loadingProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(PreviewActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void submitForm(String makerName) {
        try {
            registrationService.submitRegistrationDto(makerName);
            Intent intent = new Intent(PreviewActivity.this, AcknowledgementActivity.class);
            intent.putExtra("content", webViewContent);
            startActivity(intent);
            Toast.makeText(this, R.string.registration_success, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "Failed on registration submission", e);
            Toast.makeText(this, R.string.registration_fail, Toast.LENGTH_LONG).show();
        }
    }
}
