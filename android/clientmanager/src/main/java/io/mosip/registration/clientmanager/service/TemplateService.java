package io.mosip.registration.clientmanager.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

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
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import io.mosip.registration.clientmanager.R;
import io.mosip.registration.clientmanager.constant.Modality;
import io.mosip.registration.clientmanager.dto.registration.BiometricsDto;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.util.UserInterfaceHelperService;
import io.mosip.registration.packetmanager.dto.SimpleType;

@Singleton
public class TemplateService {

    private static final String TAG = TemplateService.class.getSimpleName();
    private static final String SLASH = "/";
    private static final String TEMPLATE_TYPE_CODE = "reg-android-preview-template-part";

    private Context appContext;

    MasterDataService masterDataService;

    IdentitySchemaRepository identitySchemaRepository;

    public TemplateService(Context appContext, MasterDataService masterDataService, IdentitySchemaRepository identitySchemaRepository){
        this.appContext = appContext;
        this.masterDataService = masterDataService;
        this.identitySchemaRepository = identitySchemaRepository;
    }

    public String getTemplate(RegistrationDto registrationDto, boolean isPreview) throws Exception {
        StringWriter writer = new StringWriter();
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.init();

        String templateText = this.masterDataService.getPreviewTemplateContent(TEMPLATE_TYPE_CODE,
                registrationDto.getSelectedLanguages().get(0));

        InputStream is = new ByteArrayInputStream(templateText.getBytes(StandardCharsets.UTF_8));

        VelocityContext velocityContext = new VelocityContext();

        Double version = identitySchemaRepository.getLatestSchemaVersion();
        if (version == null)
            throw new Exception("No Schema found");
        List<FieldSpecDto> schemaFields = identitySchemaRepository.getAllFieldSpec(appContext, version);

        setBasicDetails(isPreview, registrationDto, velocityContext);

        Map<String, Map<String, Object>> demographicsData = new HashMap<>();
        Map<String, Map<String, Object>> documentsData = new HashMap<>();
        Map<String, Map<String, Object>> biometricsData = new HashMap<>();

        for (FieldSpecDto field : schemaFields) {
            switch (field.getType()) {
                case "documentType":
                    Map<String, Object> docData = getDocumentData(field, registrationDto, velocityContext);
                    if (docData != null) {
                        documentsData.put(field.getId(), docData);
                    }
                    break;

                case "biometricsType":
                    Map<String, Object> bioData = getBiometricData(field, registrationDto, isPreview, velocityContext);
                    if (bioData != null) {
                        biometricsData.put(field.getId(), bioData);
                    }
                    break;

                default:
                    Map<String, Object> demoData = getDemographicData(field, registrationDto);
                    if (demoData != null) {
                        demographicsData.put(field.getId(), demoData);
                    }
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
        velocityContext.put("Fingers", appContext.getString(R.string.fingers));
        velocityContext.put("Iris", appContext.getString(R.string.double_iris));
        velocityContext.put("Face", appContext.getString(R.string.face_label));

        Map<String, Object> bioData = new HashMap<>();

        List<BiometricsDto> capturedFingers = registrationDto.getBestBiometrics(field.getId(), Modality.FINGERPRINT_SLAB_RIGHT);
        capturedFingers.addAll(registrationDto.getBestBiometrics(field.getId(), Modality.FINGERPRINT_SLAB_LEFT));
        capturedFingers.addAll(registrationDto.getBestBiometrics(field.getId(), Modality.FINGERPRINT_SLAB_THUMBS));
        List<BiometricsDto> capturedIris = registrationDto.getBestBiometrics(field.getId(), Modality.IRIS_DOUBLE);
        List<BiometricsDto> capturedFace = registrationDto.getBestBiometrics(field.getId(), Modality.FACE);

        bioData.put("FingerCount", capturedFingers.stream().filter(b -> b.getBioValue() != null).count());
        bioData.put("IrisCount", capturedIris.stream().filter(b -> b.getBioValue() != null).count());
        bioData.put("FaceCount", 1); //TODO check this
        bioData.put("subType", field.getSubType());
        bioData.put("label", getFieldLabel(field, registrationDto));

        Bitmap missingImage = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.wrong);
        Optional<BiometricsDto> result = capturedIris.stream()
                .filter(b -> b.getBioSubType().equalsIgnoreCase("Left")).findFirst();
        if (result.isPresent()) {
            BiometricsDto biometricsDto = result.get();
            bioData.put("LeftEye", (biometricsDto.getBioValue() != null) ? "&#10003;" : "&#10008;");
            setBiometricImage(bioData, "CapturedLeftEye", isPreview ? R.drawable.cross_mark : R.drawable.eye,
                    isPreview ? UserInterfaceHelperService.getIrisBitMap(biometricsDto) : null);
        }

        result = capturedIris.stream()
                .filter(b -> b.getBioSubType().equalsIgnoreCase("Right")).findFirst();
        if (result.isPresent()) {
            BiometricsDto biometricsDto = result.get();
            bioData.put("RightEye", (biometricsDto.getBioValue() != null) ? "&#10003;" : "&#10008;");
            setBiometricImage(bioData, "CapturedRightEye", isPreview ? R.drawable.cross_mark : R.drawable.eye,
                    isPreview ? UserInterfaceHelperService.getIrisBitMap(biometricsDto) : null);
        }

        if (!capturedFingers.isEmpty()) {
            setFingerRankings(capturedFingers, Modality.FINGERPRINT_SLAB_LEFT.getAttributes(), bioData);
            BiometricsDto lLittleFinger = capturedFingers.stream().filter(dto -> "Left LittleFinger".equals(dto.getBioSubType())).findFirst().get();
            BiometricsDto lRingFinger = capturedFingers.stream().filter(dto -> "Left RingFinger".equals(dto.getBioSubType())).findFirst().get();
            BiometricsDto lMiddleFinger = capturedFingers.stream().filter(dto -> "Left MiddleFinger".equals(dto.getBioSubType())).findFirst().get();
            BiometricsDto lIndexFinger = capturedFingers.stream().filter(dto -> "Left IndexFinger".equals(dto.getBioSubType())).findFirst().get();

            Bitmap leftHandBitmaps = UserInterfaceHelperService.combineBitmaps(Arrays.asList(
                    UserInterfaceHelperService.getFingerBitMap(lLittleFinger),
                    UserInterfaceHelperService.getFingerBitMap(lRingFinger),
                    UserInterfaceHelperService.getFingerBitMap(lMiddleFinger),
                    UserInterfaceHelperService.getFingerBitMap(lIndexFinger)), missingImage);
            setBiometricImage(bioData, "CapturedLeftSlap", isPreview ? 0 : R.drawable.left_palm,
                    isPreview ? leftHandBitmaps : null);

            setFingerRankings(capturedFingers, Modality.FINGERPRINT_SLAB_RIGHT.getAttributes(), bioData);

            BiometricsDto rIndexFinger = capturedFingers.stream().filter(dto -> "Right IndexFinger".equals(dto.getBioSubType())).findFirst().get();
            BiometricsDto rMiddleFinger = capturedFingers.stream().filter(dto -> "Right MiddleFinger".equals(dto.getBioSubType())).findFirst().get();
            BiometricsDto rRingFinger = capturedFingers.stream().filter(dto -> "Right RingFinger".equals(dto.getBioSubType())).findFirst().get();
            BiometricsDto rLittleFinger = capturedFingers.stream().filter(dto -> "Right LittleFinger".equals(dto.getBioSubType())).findFirst().get();

            Bitmap rightHandBitmaps = UserInterfaceHelperService.combineBitmaps(Arrays.asList(
                    UserInterfaceHelperService.getFingerBitMap(rIndexFinger),
                    UserInterfaceHelperService.getFingerBitMap(rMiddleFinger),
                    UserInterfaceHelperService.getFingerBitMap(rRingFinger),
                    UserInterfaceHelperService.getFingerBitMap(rLittleFinger)), missingImage);
            setBiometricImage(bioData, "CapturedRightSlap", isPreview ? 0 : R.drawable.right_palm,
                    isPreview ? rightHandBitmaps : null);

            setFingerRankings(capturedFingers, Modality.FINGERPRINT_SLAB_THUMBS.getAttributes(), bioData);

            BiometricsDto lThumb = capturedFingers.stream().filter(dto -> "Left Thumb".equals(dto.getBioSubType())).findFirst().get();
            BiometricsDto rThumb = capturedFingers.stream().filter(dto -> "Right Thumb".equals(dto.getBioSubType())).findFirst().get();

            Bitmap thumbsBitmap = UserInterfaceHelperService.combineBitmaps(Arrays.asList(
                    UserInterfaceHelperService.getFingerBitMap(lThumb),
                    UserInterfaceHelperService.getFingerBitMap(rThumb)), missingImage);
            setBiometricImage(bioData, "CapturedThumbs", isPreview ? 0 : R.drawable.thumbs,
                    isPreview ? thumbsBitmap : null);
        }

        if (!capturedFace.isEmpty()) {
            Bitmap faceBitmap = UserInterfaceHelperService.getFaceBitMap(capturedFace.get(0));
            setBiometricImage(bioData, "FaceImageSource", isPreview ? 0 : R.drawable.face,
                    isPreview ? faceBitmap : null);

            if ("applicant".equalsIgnoreCase(field.getSubType())) {
                setBiometricImage(velocityContext, "ApplicantImageSource", faceBitmap);
            }
        }
        return bioData;
    }


    private void setBiometricImage(Map<String, Object> templateValues, String key, int imagePath, Bitmap bitmap) {
        if (bitmap != null) {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                String encodedBytes = Base64.encodeToString(byteArray, Base64.DEFAULT);
                templateValues.put(key, "\"data:image/png;base64," + encodedBytes + "\"");
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage(), ex);
            }
        } else if (imagePath != 0) {
            templateValues.put(key, getImage(imagePath));
        }
    }

    private void setBiometricImage(VelocityContext velocityContext, String key, Bitmap bitmap) {
        if (bitmap != null) {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                String encodedBytes = Base64.encodeToString(byteArray, Base64.DEFAULT);
                velocityContext.put(key, "data:image/png;base64," + encodedBytes);
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage(), ex);
            }
        }
    }


    private void setFingerRankings(List<BiometricsDto> capturedFingers, List<String> fingers, Map<String, Object> data) {
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
            Bitmap bitmap = BitmapFactory.decodeResource(appContext.getResources(), imagePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
            byte[] byteArray = byteStream.toByteArray();
            String imageEncodedBytes = Base64.encodeToString(byteArray, Base64.DEFAULT);
            return "data:image/png;base64," + imageEncodedBytes;
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
        return "";
    }


    private void setBasicDetails(boolean isPreview, RegistrationDto registrationDto, VelocityContext velocityContext) {
        velocityContext.put("isPreview", isPreview);
        velocityContext.put("ApplicationIDLabel", appContext.getString(R.string.app_id));
        velocityContext.put("ApplicationID", registrationDto.getRId());
        velocityContext.put("UINLabel", appContext.getString(R.string.uin));
        velocityContext.put("UIN", registrationDto.getDemographics().get("UIN"));

        LocalDateTime currentTime = OffsetDateTime.now().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        velocityContext.put("Date", currentTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")));
        velocityContext.put("DateLabel", appContext.getString(R.string.date));

        velocityContext.put("DemographicInfo", appContext.getString(R.string.demographic_info));
        velocityContext.put("Photo", appContext.getString(R.string.photo));
        velocityContext.put("DocumentsLabel", appContext.getString(R.string.documents));
        velocityContext.put("BiometricsLabel", appContext.getString(R.string.biometrics));
        velocityContext.put("FaceLabel", appContext.getString(R.string.face_label));
        velocityContext.put("ExceptionPhotoLabel", appContext.getString(R.string.exception_photo_label));
        velocityContext.put("RONameLabel", appContext.getString(R.string.ro_label));
        velocityContext.put("ROName", "110011");
        velocityContext.put("RegCenterLabel", appContext.getString(R.string.reg_center));
        velocityContext.put("RegCenter", "10011");
        velocityContext.put("ImportantGuidelines", appContext.getString(R.string.imp_guidelines));

        velocityContext.put("LeftEyeLabel", appContext.getString(R.string.left_iris));
        velocityContext.put("RightEyeLabel", appContext.getString(R.string.right_iris));
        velocityContext.put("LeftPalmLabel", appContext.getString(R.string.left_slap));
        velocityContext.put("RightPalmLabel", appContext.getString(R.string.right_slap));
        velocityContext.put("ThumbsLabel", appContext.getString(R.string.thumbs_label));
    }

    private Map<String, Object> getDemographicData(FieldSpecDto field, RegistrationDto registrationDto) {
        Map<String, Object> data = null;
        if ("UIN".equalsIgnoreCase(field.getId()) || "IDSchemaVersion".equalsIgnoreCase(field.getId()))
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
        if (registrationDto.getDocuments().get(field.getId()) != null) {
            data = new HashMap<>();
            data.put("label", getFieldLabel(field, registrationDto));
            data.put("value", registrationDto.getDocuments().get(field.getId()).getType());
        }
        return data;
    }

}
