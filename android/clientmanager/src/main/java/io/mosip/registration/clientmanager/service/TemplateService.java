package io.mosip.registration.clientmanager.service;

import static io.mosip.registration.clientmanager.config.SessionManager.USER_NAME;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import net.glxn.qrgen.android.QRCode;

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

import javax.inject.Singleton;

import io.mosip.registration.clientmanager.R;
import io.mosip.registration.clientmanager.constant.Modality;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dto.CenterMachineDto;
import io.mosip.registration.clientmanager.dto.registration.BiometricsDto;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.util.UserInterfaceHelperService;
import io.mosip.registration.packetmanager.dto.SimpleType;

@Singleton
public class TemplateService {

    private static final String TAG = TemplateService.class.getSimpleName();
    private static final String SLASH = "/";
    private static final String TEMPLATE_TYPE_CODE = "reg-android-preview-template-part";
    private static final String ACK_TEMPLATE_TYPE_CODE = "reg-android-ack-template-part";
    private static final String LABEL_KEY = "label";
    private static final String CROSS_MARK = "&#10008;";
    private static final String APPLICANT_IMAGE_SOURCE = "ApplicantImageSource";
    private static final String BASE64_IMAGE_PREFIX = "\"data:image/jpeg;base64,";

    private Context appContext;

    SharedPreferences sharedPreferences;

    MasterDataService masterDataService;

    IdentitySchemaRepository identitySchemaRepository;
    GlobalParamRepository globalParamRepository;

    public TemplateService(Context appContext, MasterDataService masterDataService, IdentitySchemaRepository identitySchemaRepository, GlobalParamRepository globalParamRepository) {
        this.appContext = appContext;
        this.masterDataService = masterDataService;
        this.globalParamRepository = globalParamRepository;
        this.identitySchemaRepository = identitySchemaRepository;
        sharedPreferences = this.appContext.getSharedPreferences(
                this.appContext.getString(R.string.app_name),
                Context.MODE_PRIVATE);
    }

    public String getTemplate(RegistrationDto registrationDto, boolean isPreview, Map<String, String> templateTitleValues) throws Exception {
        StringWriter writer = new StringWriter();
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.init();

        String templateText = isPreview ? this.masterDataService.getPreviewTemplateContent(TEMPLATE_TYPE_CODE,
                registrationDto.getSelectedLanguages().get(0)) :
                this.masterDataService.getPreviewTemplateContent(ACK_TEMPLATE_TYPE_CODE,
                registrationDto.getSelectedLanguages().get(0));

        InputStream is = new ByteArrayInputStream(templateText.getBytes(StandardCharsets.UTF_8));

        VelocityContext velocityContext = new VelocityContext();

        Double version = identitySchemaRepository.getLatestSchemaVersion();
        if (version == null)
            throw new Exception("No Schema found");
        List<FieldSpecDto> schemaFields = identitySchemaRepository.getProcessSpecFields(appContext, registrationDto.getProcess());

        setBasicDetails(isPreview, registrationDto, templateTitleValues, velocityContext);

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
        List<BiometricsDto> capturedException = registrationDto.getBestBiometrics(field.getId(), Modality.EXCEPTION_PHOTO);

        bioData.put("FingerCount", capturedFingers.stream().filter(b -> b.getBioValue() != null).count());
        bioData.put("IrisCount", capturedIris.stream().filter(b -> b.getBioValue() != null).count());
        bioData.put("FaceCount", capturedFace.stream().filter(b -> b.getBioValue() != null).count()); //TODO check this
        bioData.put("subType", field.getSubType());
        bioData.put(LABEL_KEY, getFieldLabel(field, registrationDto));

        Bitmap missingImage = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.wrong);
        Optional<BiometricsDto> result = capturedIris.stream()
                .filter(b -> b.getBioSubType().equalsIgnoreCase("Left")).findFirst();
        if (result.isPresent()) {
            BiometricsDto biometricsDto = result.get();
            bioData.put("LeftEye", (biometricsDto.getBioValue() != null) ? "&#10003;" : CROSS_MARK);
            setBiometricImage(bioData, "CapturedLeftEye", isPreview ? R.drawable.cross_mark : R.drawable.eye,
                    isPreview ? UserInterfaceHelperService.getIrisBitMap(biometricsDto) : BitmapFactory.decodeResource(appContext.getResources(), R.drawable.left_eye_ack), isPreview);
        }

        result = capturedIris.stream()
                .filter(b -> b.getBioSubType().equalsIgnoreCase("Right")).findFirst();
        if (result.isPresent()) {
            BiometricsDto biometricsDto = result.get();
            bioData.put("RightEye", (biometricsDto.getBioValue() != null) ? "&#10003;" : CROSS_MARK);
            setBiometricImage(bioData, "CapturedRightEye", isPreview ? R.drawable.cross_mark : R.drawable.eye,
                    isPreview ? UserInterfaceHelperService.getIrisBitMap(biometricsDto) : BitmapFactory.decodeResource(appContext.getResources(), R.drawable.right_eye_ack), isPreview);
        }

        if (!capturedFingers.isEmpty()) {
            List<String> leftFingers = Modality.FINGERPRINT_SLAB_LEFT.getAttributes();
            List<BiometricsDto> leftHandFingersDtoList = capturedFingers.stream().filter(b -> leftFingers.contains(Modality.getBioAttribute(b.getBioSubType()))).collect(Collectors.toList());
            if(!leftHandFingersDtoList.isEmpty()) {
                setFingerRankings(leftHandFingersDtoList, leftFingers, bioData);
                List<Bitmap> images  = new ArrayList<>();
                BiometricsDto lLittleFinger;
                BiometricsDto lRingFinger;
                BiometricsDto lMiddleFinger;
                BiometricsDto lIndexFinger;
                result = leftHandFingersDtoList.stream().filter(dto -> "Left LittleFinger".equals(dto.getBioSubType())).findFirst();
                if(result.isPresent()) {
                    lLittleFinger = result.get();
                    images.add(UserInterfaceHelperService.getFingerBitMap(lLittleFinger));
                }

                result = leftHandFingersDtoList.stream().filter(dto -> "Left RingFinger".equals(dto.getBioSubType())).findFirst();
                if(result.isPresent()) {
                    lRingFinger = result.get();
                    images.add(UserInterfaceHelperService.getFingerBitMap(lRingFinger));
                }
                result = leftHandFingersDtoList.stream().filter(dto -> "Left MiddleFinger".equals(dto.getBioSubType())).findFirst();
                if(result.isPresent()) {
                    lMiddleFinger = result.get();
                    images.add(UserInterfaceHelperService.getFingerBitMap(lMiddleFinger));
                }
                result = leftHandFingersDtoList.stream().filter(dto -> "Left IndexFinger".equals(dto.getBioSubType())).findFirst();
                if(result.isPresent()) {
                    lIndexFinger = result.get();
                    images.add(UserInterfaceHelperService.getFingerBitMap(lIndexFinger));
                }

                Bitmap leftHandBitmaps = UserInterfaceHelperService.combineBitmaps(images, missingImage);
                setBiometricImage(bioData, "CapturedLeftSlap", isPreview ? 0 : R.drawable.left_palm,
                        isPreview ? leftHandBitmaps : BitmapFactory.decodeResource(appContext.getResources(), R.drawable.left_hand_ack), isPreview);
            }

            List<String> rightFingers = Modality.FINGERPRINT_SLAB_RIGHT.getAttributes();
            List<BiometricsDto> rightHandFingersDtoList = capturedFingers.stream().filter(b -> rightFingers.contains(Modality.getBioAttribute(b.getBioSubType()))).collect(Collectors.toList());
            if(!rightHandFingersDtoList.isEmpty()) {
                setFingerRankings(rightHandFingersDtoList, Modality.FINGERPRINT_SLAB_RIGHT.getAttributes(), bioData);
                List<Bitmap> images  = new ArrayList<>();
                BiometricsDto rIndexFinger;
                BiometricsDto rMiddleFinger;
                BiometricsDto rRingFinger;
                BiometricsDto rLittleFinger;

                result = rightHandFingersDtoList.stream().filter(dto -> "Right IndexFinger".equals(dto.getBioSubType())).findFirst();
                if(result.isPresent()) {
                    rIndexFinger = result.get();
                    images.add(UserInterfaceHelperService.getFingerBitMap(rIndexFinger));
                }

                result = rightHandFingersDtoList.stream().filter(dto -> "Right MiddleFinger".equals(dto.getBioSubType())).findFirst();
                if(result.isPresent()) {
                    rMiddleFinger = result.get();
                    images.add(UserInterfaceHelperService.getFingerBitMap(rMiddleFinger));
                }

                result = rightHandFingersDtoList.stream().filter(dto -> "Right RingFinger".equals(dto.getBioSubType())).findFirst();
                if(result.isPresent()) {
                    rRingFinger = result.get();
                    images.add(UserInterfaceHelperService.getFingerBitMap(rRingFinger));
                }

                result = rightHandFingersDtoList.stream().filter(dto -> "Right LittleFinger".equals(dto.getBioSubType())).findFirst();
                if(result.isPresent()) {
                    rLittleFinger = result.get();
                    images.add(UserInterfaceHelperService.getFingerBitMap(rLittleFinger));
                }

                Bitmap rightHandBitmaps = UserInterfaceHelperService.combineBitmaps(images, missingImage);
                setBiometricImage(bioData, "CapturedRightSlap", isPreview ? 0 : R.drawable.right_palm,
                        isPreview ? rightHandBitmaps : BitmapFactory.decodeResource(appContext.getResources(), R.drawable.right_hand_ack), isPreview);
            }


            List<String> thumbs = Modality.FINGERPRINT_SLAB_THUMBS.getAttributes();
            List<BiometricsDto> thumbsDtoList = capturedFingers.stream().filter(b -> thumbs.contains(Modality.getBioAttribute(b.getBioSubType()))).collect(Collectors.toList());
            if(!thumbsDtoList.isEmpty()) {
                setFingerRankings(thumbsDtoList, Modality.FINGERPRINT_SLAB_THUMBS.getAttributes(), bioData);
                List<Bitmap> images  = new ArrayList<>();
                BiometricsDto lThumb;
                BiometricsDto rThumb;

                result = thumbsDtoList.stream().filter(dto -> "Left Thumb".equals(dto.getBioSubType())).findFirst();
                if(result.isPresent()) {
                    lThumb = result.get();
                    images.add(UserInterfaceHelperService.getFingerBitMap(lThumb));
                }

                result = thumbsDtoList.stream().filter(dto -> "Right Thumb".equals(dto.getBioSubType())).findFirst();
                if(result.isPresent()) {
                    rThumb = result.get();
                    images.add(UserInterfaceHelperService.getFingerBitMap(rThumb));
                }

                Bitmap thumbsBitmap = UserInterfaceHelperService.combineBitmaps(images, missingImage);
                setBiometricImage(bioData, "CapturedThumbs", isPreview ? 0 : R.drawable.thumbs,
                        isPreview ? thumbsBitmap : BitmapFactory.decodeResource(appContext.getResources(), R.drawable.thumbs_ack), isPreview);
            }

        }

        if (!capturedFace.isEmpty()) {
            Bitmap faceBitmap = UserInterfaceHelperService.getFaceBitMap(capturedFace.get(0));
            setBiometricImage(bioData, "FaceImageSource", isPreview ? 0 : R.drawable.face,
                    isPreview ? faceBitmap : BitmapFactory.decodeResource(appContext.getResources(), R.drawable.face_ack), isPreview);

            if ("applicant".equalsIgnoreCase(field.getSubType())) {
                setBiometricImage(velocityContext, APPLICANT_IMAGE_SOURCE, faceBitmap, isPreview);
            }
        }

        if (!capturedException.isEmpty()) {
            if ("applicant".equalsIgnoreCase(field.getSubType())) {
                Bitmap applicantExceptionBitmap = UserInterfaceHelperService.getFaceBitMap(registrationDto.getBestBiometrics(field.getId(), Modality.EXCEPTION_PHOTO).get(0));
                setBiometricImage(velocityContext, "ExceptionImageSource", isPreview ? applicantExceptionBitmap : BitmapFactory.decodeResource(appContext.getResources(),
                        R.drawable.exception_photo), isPreview);
            }
            if ("introducer".equalsIgnoreCase(field.getSubType())) {
                Bitmap introducerExceptionBitmap = UserInterfaceHelperService.getFaceBitMap(registrationDto.getBestBiometrics(field.getId(), Modality.EXCEPTION_PHOTO).get(0));
                setBiometricImage(velocityContext, "IntroducerExceptionImageSource", isPreview ? introducerExceptionBitmap : BitmapFactory.decodeResource(appContext.getResources(),
                        R.drawable.exception_photo), isPreview);
            }
        }

        return bioData;
    }

    private void generateQRCode(VelocityContext velocityContext,RegistrationDto registrationDto){
        Bitmap qrBitmap = QRCode.from(registrationDto.getRId()).bitmap();
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            qrBitmap.compress(Bitmap.CompressFormat.JPEG, 50 , byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encodedBytes = Base64.encodeToString(byteArray, Base64.DEFAULT);
            velocityContext.put("QRCodeSource", BASE64_IMAGE_PREFIX + encodedBytes + "\"");
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
    }

    private void setBiometricImage(Map<String, Object> templateValues, String key, int imagePath, Bitmap bitmap, boolean isPreview) {
        if (bitmap != null) {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                if (isPreview) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                }else if(!isPreview && key.equals(APPLICANT_IMAGE_SOURCE)){
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                } else {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                }
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                String encodedBytes = Base64.encodeToString(byteArray, Base64.DEFAULT);
                templateValues.put(key, BASE64_IMAGE_PREFIX + encodedBytes + "\"");
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage(), ex);
            }
        } else if (imagePath != 0) {
            templateValues.put(key, getImage(imagePath));
        }
    }

    private void setBiometricImage(VelocityContext velocityContext, String key, Bitmap bitmap, boolean isPreview) {
        if (bitmap != null) {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                if (isPreview) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                }else if(!isPreview && key.equals(APPLICANT_IMAGE_SOURCE)){
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                } else {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                }
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                String encodedBytes = Base64.encodeToString(byteArray, Base64.DEFAULT);
                velocityContext.put(key, BASE64_IMAGE_PREFIX + encodedBytes + "\"");
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage(), ex);
            }
        }
    }


    private void setFingerRankings(List<BiometricsDto> capturedFingers, List<String> fingers, Map<String, Object> data) {
        Map<String, Float> sortedValues = capturedFingers.stream()
                .filter(b -> b.getBioValue() != null)
                .sorted(Comparator.comparing(BiometricsDto::getQualityScore))
                .collect(Collectors.toMap(BiometricsDto::getBioSubType, BiometricsDto::getQualityScore));

        int rank = 0;
        double prev = 0;
        Map<String, Integer> rankings = new HashMap<>();
        for (Map.Entry<String, Float> entry : sortedValues.entrySet()) {
            rankings.put(Modality.getBioAttribute(entry.getKey()), prev == 0 ? ++rank : entry.getValue() == prev ? rank : ++rank);
            prev = entry.getValue();
        }

        for (String finger : fingers) {
            Optional<BiometricsDto> result = capturedFingers.stream()
                    .filter(b -> Modality.getBioAttribute(b.getBioSubType()).equalsIgnoreCase(finger)).findFirst();
            if (result.isPresent()) {
                data.put(finger, result.get().getBioValue() == null ? CROSS_MARK :
                        rankings.get(finger));
                        } else {
                data.put(finger, CROSS_MARK);
            }
        }
    }


    private String getImage(int imagePath) {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {
            Bitmap bitmap = BitmapFactory.decodeResource(appContext.getResources(), imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 65, byteStream);
            byte[] byteArray = byteStream.toByteArray();
            String imageEncodedBytes = Base64.encodeToString(byteArray, Base64.DEFAULT);
            return "data:image/jpeg;base64," + imageEncodedBytes;
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
        return "";
    }


    private void setBasicDetails(boolean isPreview, RegistrationDto registrationDto, Map<String, String> templateTitleValues, VelocityContext velocityContext) {
        generateQRCode(velocityContext,registrationDto);
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto = masterDataService.getRegistrationCenterMachineDetails();

        String imp_guidelines = globalParamRepository.getCachedStringGlobalParam(RegistrationConstants.TEMPLATE_IMPORTANT_GUIDELINES
                +"_"+registrationDto.getSelectedLanguages().get(0));

        velocityContext.put("isPreview", isPreview);
        velocityContext.put("ImportantGuidelines", imp_guidelines);
        velocityContext.put("ApplicationIDLabel", appContext.getString(R.string.app_id));
        velocityContext.put("ApplicationID", registrationDto.getRId());
        velocityContext.put("UINLabel", appContext.getString(R.string.uin));
        velocityContext.put("UIN", registrationDto.getDemographics().get("UIN"));

        LocalDateTime currentTime = OffsetDateTime.now().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        velocityContext.put("Date", currentTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")));
        velocityContext.put("DateLabel", appContext.getString(R.string.date));

        velocityContext.put("DemographicInfo", templateTitleValues.get("demographicInfo"));
        velocityContext.put("Photo", appContext.getString(R.string.photo));
        velocityContext.put("DocumentsLabel", templateTitleValues.get("documents"));
        velocityContext.put("BiometricsLabel", templateTitleValues.get("bioMetrics"));
        velocityContext.put("FaceLabel", appContext.getString(R.string.face_label));
        velocityContext.put("ExceptionPhotoLabel", appContext.getString(R.string.exception_photo_label));
        velocityContext.put("RONameLabel", appContext.getString(R.string.ro_label));
        velocityContext.put("ROName", sharedPreferences.getString(USER_NAME, ""));
        velocityContext.put("RegCenterLabel", appContext.getString(R.string.reg_center));
        velocityContext.put("RegCenter", centerMachineDto.getCenterId());

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
            data.put(LABEL_KEY, getFieldLabel(field, registrationDto));
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
            data.put(LABEL_KEY, getFieldLabel(field, registrationDto));
            data.put("value", registrationDto.getDocuments().get(field.getId()).getType());
        }
        return data;
    }

}
