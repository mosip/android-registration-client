package io.mosip.registration.app.activites;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.gemalto.jp2.JP2Decoder;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.biometrics.util.face.FaceBDIR;
import io.mosip.biometrics.util.finger.FingerBDIR;
import io.mosip.biometrics.util.iris.IrisBDIR;
import io.mosip.registration.app.R;
import io.mosip.registration.clientmanager.dto.registration.BiometricsDto;
import io.mosip.registration.clientmanager.dto.registration.GenericDto;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.keymanager.util.CryptoUtil;
import io.mosip.registration.packetmanager.constants.Biometric;

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        startActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity();
    }

    private void startActivity() {
        setContentView(R.layout.activity_preview);
        webView = (WebView) findViewById(R.id.registration_preview);

        //to display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.preview_title);

        final Button button = findViewById(R.id.createpacket);
        button.setOnClickListener( v -> {
            button.setEnabled(false);
            Log.i(TAG, "Clicked on Registration form submit...");
            submitForm();
        });

        try {
            RegistrationDto registrationDto = this.registrationService.getRegistrationDto();
            String val = getTemplate(registrationDto, "reg-preview-template-part", true);
            String base64 = Base64.encodeToString(val.getBytes("UTF-8"), Base64.DEFAULT);
            webView.loadData(base64, "text/html; charset=utf-8", "base64");
        } catch (Exception e) {
            Log.e(TAG, "Failed to set the preview content", e);
        }
    }

    public String getTemplate(RegistrationDto registrationDto, String templateTypeCode, boolean isPreview) throws Exception {
        StringWriter writer = new StringWriter();
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.init();

        String templateText = this.masterDataService.getTemplateContent(templateTypeCode, "eng");
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
        velocityContext.put("Face", getString(R.string.face));

        List<BiometricsDto> capturedList = new ArrayList<>();
        for (String attribute : field.getBioAttributes()) {
            String key = String.format("%s_%s", field.getId(), attribute.equalsIgnoreCase("face") ? "" : attribute);
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
        bioData.put("FaceCount", capturedFace.size());
        bioData.put("subType", field.getSubType());
        bioData.put("label", getFieldLabel(field, registrationDto));

//        Optional<BiometricsDto> result = capturedIris.stream()
//                .filter(b -> b.getBioSubType().equalsIgnoreCase("Left")).findFirst();
//        if (result.isPresent()) {
//            BiometricsDto biometricsDto = result.get();
//            bioData.put("LeftEye", (biometricsDto.getBioValue() != null) ? "&#10003;" : "&#10008;");
//            setBiometricImage(bioData, "CapturedLeftEye", isPreview ? R.drawable.cross_mark : R.drawable.eye,
//                    isPreview ? getIrisBitMap(biometricsDto) : null);
//        }
//
//        result = capturedIris.stream()
//                .filter(b -> b.getBioSubType().equalsIgnoreCase("Right")).findFirst();
//        if (result.isPresent()) {
//            BiometricsDto biometricsDto = result.get();
//            bioData.put("RightEye", (biometricsDto.getBioValue() != null) ? "&#10003;" : "&#10008;");
//            setBiometricImage(bioData, "CapturedRightEye", isPreview ? R.drawable.cross_mark : R.drawable.eye,
//                    isPreview ? getIrisBitMap(biometricsDto) : null);
//        }
//
//        if(!capturedFingers.isEmpty()) {
//            setFingerRankings(capturedFingers, Biometric.getDefaultAttributes("FINGERPRINT_SLAB_LEFT"), bioData);
//            Bitmap leftHandBitmaps = combineBitmaps(Arrays.asList(getFingerBitMap(capturedFingers, "Left LittleFinger"),
//                    getFingerBitMap(capturedFingers, "Left RingFinger"),
//                    getFingerBitMap(capturedFingers, "Left MiddleFinger"),
//                    getFingerBitMap(capturedFingers, "Left IndexFinger")));
//            setBiometricImage(bioData, "CapturedLeftSlap", isPreview ? 0 : R.drawable.left_palm,
//                    isPreview ? leftHandBitmaps : null);
//
//            setFingerRankings(capturedFingers, Biometric.getDefaultAttributes("FINGERPRINT_SLAB_RIGHT"), bioData);
//            Bitmap rightHandBitmaps = combineBitmaps(Arrays.asList(getFingerBitMap(capturedFingers, "Right IndexFinger"),
//                    getFingerBitMap(capturedFingers, "Right MiddleFinger"),
//                    getFingerBitMap(capturedFingers, "Right RingFinger"),
//                    getFingerBitMap(capturedFingers, "Right LittleFinger")));
//            setBiometricImage(bioData, "CapturedRightSlap", isPreview ? 0 : R.drawable.right_palm,
//                    isPreview ? rightHandBitmaps : null);
//
//            setFingerRankings(capturedFingers, Biometric.getDefaultAttributes("FINGERPRINT_SLAB_THUMBS"), bioData);
//            Bitmap thumbsBitmap = combineBitmaps(Arrays.asList(getFingerBitMap(capturedFingers, "Left Thumb"),
//                    getFingerBitMap(capturedFingers, "Right Thumb")));
//            setBiometricImage(bioData, "CapturedThumbs", isPreview ? 0 : R.drawable.thumbs,
//                    isPreview ? thumbsBitmap : null);
//        }
//
//        if(!capturedFace.isEmpty()) {
//            Bitmap faceBitmap;
//            try (ByteArrayInputStream bais = new ByteArrayInputStream(CryptoUtil.base64decoder.decode(capturedFace.get(0).getBioValue()));
//                DataInputStream inputStream = new DataInputStream(bais);) {
//                FaceBDIR faceBDIR = new FaceBDIR(inputStream);
//                byte[] bytes = faceBDIR.getRepresentation().getRepresentationData().getImageData().getImage();
//                faceBitmap = new JP2Decoder(bytes).decode();
//            }
//            setBiometricImage(bioData, "FaceImageSource", isPreview ? 0 : R.drawable.face,
//                    isPreview ? faceBitmap : null);
//
//            if("applicant".equalsIgnoreCase(field.getSubType())) {
//                setBiometricImage(velocityContext, "ApplicantImageSource", faceBitmap);
//            }
//        }
        return bioData;
    }

    private Bitmap getFingerBitMap(List<BiometricsDto> list, String attribute) throws IOException {
        Optional<BiometricsDto> result = list.stream().filter(dto -> attribute.equals(dto.getBioSubType())).findFirst();
        if(!result.isPresent())
            return null;

        try(ByteArrayInputStream bais = new ByteArrayInputStream(CryptoUtil.base64decoder.decode(result.get().getBioValue()));
            DataInputStream inputStream = new DataInputStream(bais);) {
            FingerBDIR fingerBDIR = new FingerBDIR(inputStream);
            byte[] bytes = fingerBDIR.getRepresentation().getRepresentationBody().getImageData().getImage();
            return new JP2Decoder(bytes).decode();
        }
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

    private Bitmap getIrisBitMap(BiometricsDto biometricsDto) {
        try(ByteArrayInputStream bais = new ByteArrayInputStream(CryptoUtil.base64decoder.decode(biometricsDto.getBioValue()));
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
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
            byte[] byteArray = byteStream.toByteArray();
            String imageEncodedBytes = Base64.encodeToString(byteArray,Base64.DEFAULT);
            return "data:image/png;base64," + imageEncodedBytes;
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
        velocityContext.put("LeftPalmLabel", getString(R.string.left_hand_palm));
        velocityContext.put("RightPalmLabel", getString(R.string.right_hand_palm));
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
                List<GenericDto> valueList = (List<GenericDto>) fieldValue;
                value = valueList.get(0).getName();
            }
        }
        return value;
    }

    private String getValue(Object fieldValue, String lang) {
        String value = "";

        if (fieldValue instanceof List<?>) {
            Optional<GenericDto> demoValueInRequiredLang = ((List<GenericDto>) fieldValue).stream()
                    .filter(valueDTO -> valueDTO.getLangCode().equals(lang)).findFirst();

            if (demoValueInRequiredLang.isPresent() && demoValueInRequiredLang.get().getName() != null) {
                value = demoValueInRequiredLang.get().getName();
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

    private void submitForm() {
        try {
            registrationService.submitRegistrationDto();
            Toast.makeText(this, R.string.registration_success, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "Failed on registration submission", e);
            Toast.makeText(this, R.string.registration_fail, Toast.LENGTH_LONG).show();
        }
        goToHome();
    }

    public void goToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
