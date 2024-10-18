/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
*/

package io.mosip.registration_client.api_services;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.gemalto.jp2.JP2Decoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.biometrics.util.face.FaceBDIR;
import io.mosip.biometrics.util.finger.FingerBDIR;
import io.mosip.biometrics.util.iris.IrisBDIR;
import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.constant.Modality;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dto.registration.BiometricsDto;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.dto.sbi.CaptureRequest;
import io.mosip.registration.clientmanager.dto.sbi.DiscoverRequest;
import io.mosip.registration.clientmanager.exception.BiometricsServiceException;
import io.mosip.registration.clientmanager.exception.ClientCheckedException;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.service.Biometrics095Service;
import io.mosip.registration.clientmanager.service.RegistrationServiceImpl;
import io.mosip.registration.clientmanager.service.UserOnboardService;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.clientmanager.util.UserInterfaceHelperService;
import io.mosip.registration.keymanager.util.CryptoUtil;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.SingleType;
import io.mosip.registration_client.MainActivity;
import io.mosip.registration_client.model.BiometricsPigeon;

@Singleton
public class BiometricsDetailsApi implements BiometricsPigeon.BiometricsApi {
    private Activity activity;
    private final AuditManagerService auditManagerService;
    private final ObjectMapper objectMapper;
    private final Biometrics095Service biometricsService;
    private final RegistrationService registrationService;
    private final GlobalParamRepository globalParamRepository;
    private final UserOnboardService userOnboardService;

    private Modality currentModality;
    private static final String TAG = "BiometricsDetailApi";
    private String callbackId;
    private String fieldId;
    private String purpose;
    private int capturedAttempts;
    private int currentAttempt;
    private int allowedAttempts;
    private int qualityThreshold;
    List<BiometricsDto> biometricsDtoList;

    private List<Bitmap> listBitmaps1 = new ArrayList<>();
    private byte[] byteArrayTester;
    private List<byte[]> listByteArrayTester1 = new ArrayList<>();
    BiometricsPigeon.Result<String> result1;

    public List<String> OPERATOR_EXCEPTIONS=new ArrayList<>();

    @Inject
    public BiometricsDetailsApi(AuditManagerService auditManagerService, ObjectMapper objectMapper,
            Biometrics095Service biometrics095Service, RegistrationService registrationService,
            GlobalParamRepository globalParamRepository, UserOnboardService userOnboardService) {
        this.auditManagerService = auditManagerService;
        this.objectMapper = objectMapper;
        this.biometricsService = biometrics095Service;
        this.registrationService = registrationService;
        this.globalParamRepository = globalParamRepository;
        this.userOnboardService = userOnboardService;
    }

    public void setCallbackActivity(MainActivity mainActivity) {
        this.activity = mainActivity;
    }

    @Override
    public void invokeDiscoverSbi(@NonNull String fieldId, @NonNull String modality,
            @NonNull BiometricsPigeon.Result<String> result) {
        auditManagerService.audit(AuditEvent.BIOMETRIC_CAPTURE, Components.REGISTRATION);
        currentModality = getModality(modality);
        this.fieldId = fieldId;
        discoverSBI();
        result1 = result;
    }

    @Override
    public void getBestBiometrics(@NonNull String fieldId, @NonNull String modality,
            @NonNull BiometricsPigeon.Result<List<String>> result) {

        if (fieldId.equals("operatorBiometrics")) {
            try {
                biometricsDtoList=new ArrayList<>();
                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                String json;
                List<String> jsonList = new ArrayList<>();

                for (BiometricsDto dto : userOnboardService.getOperatorBiometrics()) {
                    if (modality.equals("Face") && dto.getBioSubType()==null) {
                        biometricsDtoList.add(dto);
                    }
                    if(matchOperatorModality(dto.getBioSubType(),modality)){
                        biometricsDtoList.add(dto);
                    }
                }


                for (int i = 0; i < biometricsDtoList.size(); i++) {
                    json = ow.writeValueAsString(biometricsDtoList.get(i));
                    jsonList.add(json);
                }


                result.success(jsonList);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        } else {
            try {
                RegistrationDto registrationDto = registrationService.getRegistrationDto();
                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                String json;
                List<String> jsonList = new ArrayList<>();
                biometricsDtoList = registrationDto.getBestBiometrics(fieldId, getModality(modality));
                for (int i = 0; i < biometricsDtoList.size(); i++) {
                    json = ow.writeValueAsString(biometricsDtoList.get(i));
                    jsonList.add(json);
                }
                result.success(jsonList);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    @Override
    public void getBiometrics(@NonNull String fieldId, @NonNull String modality, @NonNull Long attempt,
            @NonNull BiometricsPigeon.Result<List<String>> result) {
        try {
            RegistrationDto registrationDto = registrationService.getRegistrationDto();
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json;
            List<String> jsonList = new ArrayList<>();
            biometricsDtoList = registrationDto.getBiometrics(fieldId, getModality(modality), attempt.intValue() - 1);
            for (int i = 0; i < biometricsDtoList.size(); i++) {
                json = ow.writeValueAsString(biometricsDtoList.get(i));
                jsonList.add(json);
            }
            result.success(jsonList);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void extractImageValues(@NonNull String fieldId, @NonNull String modality,
            @NonNull BiometricsPigeon.Result<List<byte[]>> result) {
        List<Bitmap> listBitmaps = new ArrayList<>();
        List<byte[]> listByteArrayTester = new ArrayList<>();
        if (fieldId.equals("operatorBiometrics")) {
            try {


                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                String json;
                List<String> jsonList = new ArrayList<>();
                List<BiometricsDto> biometricsDtoListtemp = new ArrayList<>();

                switch (getModality(modality)) {
                    case FACE:
                    case EXCEPTION_PHOTO: {
                        try {
                            BiometricsDto biometricsDtotemp = new BiometricsDto();

                            for (BiometricsDto dto : userOnboardService.getOperatorBiometrics()) {

                                if (modality.equals("Face") && dto.getBioSubType()==null) {
                                    biometricsDtotemp = dto;
                                    break;
                                }
                            }
                            Bitmap var5;
                            ByteArrayInputStream bais = new ByteArrayInputStream(
                                    CryptoUtil.base64decoder.decode(biometricsDtotemp.getBioValue()));
                            DataInputStream inputStream = new DataInputStream(bais);
                            FaceBDIR faceBDIR = new FaceBDIR(inputStream);
                            byte[] bytes = faceBDIR.getRepresentation().getRepresentationData().getImageData()
                                    .getImage();
                            var5 = (new JP2Decoder(bytes)).decode();
                            listBitmaps.add(var5);
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                        break;
                    case FINGERPRINT_SLAB_LEFT:
                    case FINGERPRINT_SLAB_THUMBS:
                    case FINGERPRINT_SLAB_RIGHT: {
                        try {

                            for (BiometricsDto dto : userOnboardService.getOperatorBiometrics()) {

                                if (matchOperatorModality(dto.getBioSubType(),modality)) {
                                    biometricsDtoListtemp.add(dto);
                                }
                            }
                            Bitmap var5;
                            for (int i = 0; i < biometricsDtoListtemp.size(); i++) {
                                ByteArrayInputStream bais = new ByteArrayInputStream(
                                        CryptoUtil.base64decoder.decode(biometricsDtoListtemp.get(i).getBioValue()));
                                DataInputStream inputStream = new DataInputStream(bais);
                                FingerBDIR fingerBDIR = new FingerBDIR(inputStream);
                                byte[] bytes = fingerBDIR.getRepresentation().getRepresentationBody().getImageData()
                                        .getImage();
                                var5 = (new JP2Decoder(bytes)).decode();
                                listBitmaps.add(var5);
                            }
                            } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                        break;
                    case IRIS_DOUBLE: {
                        try {


                            for (BiometricsDto dto : userOnboardService.getOperatorBiometrics()) {
                                if (matchOperatorModality(dto.getBioSubType(),modality)) {
                                    biometricsDtoListtemp.add(dto);

                                }
                            }

                            Bitmap var5;
                             for (int i = 0; i < biometricsDtoListtemp.size(); i++) {
                                ByteArrayInputStream bais = new ByteArrayInputStream(
                                        CryptoUtil.base64decoder.decode(biometricsDtoListtemp.get(0).getBioValue()));
                                DataInputStream inputStream = new DataInputStream(bais);
                                IrisBDIR irisBDIR = new IrisBDIR(inputStream);
                                byte[] bytes = irisBDIR.getRepresentation().getRepresentationData().getImageData()
                                        .getImage();
                                var5 = (new JP2Decoder(bytes)).decode();
                                listBitmaps.add(var5);
                            }
                            } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                        break;
                }

                for (int i = 0; i < listBitmaps.size(); i++) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    listBitmaps.get(i).compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byteArrayTester = stream.toByteArray();
                    listByteArrayTester.add(byteArrayTester);
                }

                listByteArrayTester1 = listByteArrayTester;
                result.success(listByteArrayTester1);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        } else {
            try {
                RegistrationDto registrationDto = registrationService.getRegistrationDto();
                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                String json;
                List<String> jsonList = new ArrayList<>();

                switch (getModality(modality)) {
                    case FACE:
                    case EXCEPTION_PHOTO: {
                        try {
                            Bitmap var5;
                            ByteArrayInputStream bais = new ByteArrayInputStream(
                                    CryptoUtil.base64decoder.decode(registrationDto
                                            .getBestBiometrics(fieldId, getModality(modality)).get(0).getBioValue()));
                            DataInputStream inputStream = new DataInputStream(bais);
                            FaceBDIR faceBDIR = new FaceBDIR(inputStream);
                            byte[] bytes = faceBDIR.getRepresentation().getRepresentationData().getImageData()
                                    .getImage();
                            var5 = (new JP2Decoder(bytes)).decode();
                            listBitmaps.add(var5);
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                        break;
                    case FINGERPRINT_SLAB_LEFT:
                    case FINGERPRINT_SLAB_THUMBS:
                    case FINGERPRINT_SLAB_RIGHT: {
                        try {
                            Bitmap var5;
                            for (int i = 0; i < registrationDto.getBestBiometrics(fieldId, getModality(modality))
                                    .size(); i++) {
                                ByteArrayInputStream bais = new ByteArrayInputStream(CryptoUtil.base64decoder
                                        .decode(registrationDto.getBestBiometrics(fieldId, getModality(modality)).get(i)
                                                .getBioValue()));
                                DataInputStream inputStream = new DataInputStream(bais);
                                FingerBDIR fingerBDIR = new FingerBDIR(inputStream);
                                byte[] bytes = fingerBDIR.getRepresentation().getRepresentationBody().getImageData()
                                        .getImage();
                                var5 = (new JP2Decoder(bytes)).decode();
                                listBitmaps.add(var5);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                        break;
                    case IRIS_DOUBLE: {
                        try {
                            Bitmap var5;

                            for (int i = 0; i < registrationDto.getBestBiometrics(fieldId, getModality(modality))
                                    .size(); i++) {
                                ByteArrayInputStream bais = new ByteArrayInputStream(CryptoUtil.base64decoder
                                        .decode(registrationDto.getBestBiometrics(fieldId, getModality(modality)).get(0)
                                                .getBioValue()));
                                DataInputStream inputStream = new DataInputStream(bais);
                                IrisBDIR irisBDIR = new IrisBDIR(inputStream);
                                byte[] bytes = irisBDIR.getRepresentation().getRepresentationData().getImageData()
                                        .getImage();
                                var5 = (new JP2Decoder(bytes)).decode();
                                listBitmaps.add(var5);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                        break;
                }

                for (int i = 0; i < listBitmaps.size(); i++) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    listBitmaps.get(i).compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byteArrayTester = stream.toByteArray();
                    listByteArrayTester.add(byteArrayTester);
                }

                listByteArrayTester1 = listByteArrayTester;
                result.success(listByteArrayTester1);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    @Override
    public void extractImageValuesByAttempt(@NonNull String fieldId, @NonNull String modality, @NonNull Long attempt,
            @NonNull BiometricsPigeon.Result<List<byte[]>> result) {
        List<Bitmap> listBitmaps = new ArrayList<>();
        List<byte[]> listByteArrayTester = new ArrayList<>();
        try {
            RegistrationDto registrationDto = registrationService.getRegistrationDto();

            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json;
            List<String> jsonList = new ArrayList<>();

            switch (getModality(modality)) {
                case FACE:
                case EXCEPTION_PHOTO: {
                    try {
                        Bitmap var5;
                        ByteArrayInputStream bais = new ByteArrayInputStream(CryptoUtil.base64decoder.decode(
                                registrationDto.getBiometrics(fieldId, getModality(modality), attempt.intValue() - 1)
                                        .get(0).getBioValue()));
                        DataInputStream inputStream = new DataInputStream(bais);
                        FaceBDIR faceBDIR = new FaceBDIR(inputStream);
                        byte[] bytes = faceBDIR.getRepresentation().getRepresentationData().getImageData().getImage();
                        var5 = (new JP2Decoder(bytes)).decode();
                        listBitmaps.add(var5);
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
                    break;
                case FINGERPRINT_SLAB_LEFT:
                case FINGERPRINT_SLAB_THUMBS:
                case FINGERPRINT_SLAB_RIGHT: {
                    try {
                        Bitmap var5;
                        for (int i = 0; i < registrationDto
                                .getBiometrics(fieldId, getModality(modality), attempt.intValue() - 1).size(); i++) {
                            ByteArrayInputStream bais = new ByteArrayInputStream(
                                    CryptoUtil.base64decoder.decode(registrationDto
                                            .getBiometrics(fieldId, getModality(modality), attempt.intValue() - 1)
                                            .get(i).getBioValue()));
                            DataInputStream inputStream = new DataInputStream(bais);
                            FingerBDIR fingerBDIR = new FingerBDIR(inputStream);
                            byte[] bytes = fingerBDIR.getRepresentation().getRepresentationBody().getImageData()
                                    .getImage();
                            var5 = (new JP2Decoder(bytes)).decode();
                            listBitmaps.add(var5);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
                    break;
                case IRIS_DOUBLE: {
                    try {
                        Bitmap var5;
                        for (int i = 0; i < registrationDto
                                .getBiometrics(fieldId, getModality(modality), attempt.intValue() - 1).size(); i++) {
                            ByteArrayInputStream bais = new ByteArrayInputStream(
                                    CryptoUtil.base64decoder.decode(registrationDto
                                            .getBiometrics(fieldId, getModality(modality), attempt.intValue() - 1)
                                            .get(0).getBioValue()));
                            DataInputStream inputStream = new DataInputStream(bais);
                            IrisBDIR irisBDIR = new IrisBDIR(inputStream);
                            byte[] bytes = irisBDIR.getRepresentation().getRepresentationData().getImageData()
                                    .getImage();
                            var5 = (new JP2Decoder(bytes)).decode();
                            listBitmaps.add(var5);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
                    break;
            }
            for (int i = 0; i < listBitmaps.size(); i++) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                listBitmaps.get(i).compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArrayTester = stream.toByteArray();
                listByteArrayTester.add(byteArrayTester);
            }

            listByteArrayTester1 = listByteArrayTester;
            result.success(listByteArrayTester1);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void incrementBioAttempt(@NonNull String fieldId, @NonNull String modality,
            @NonNull BiometricsPigeon.Result<Long> result) {
        try {
            RegistrationDto registrationDto = registrationService.getRegistrationDto();
            int i = registrationDto.incrementBioAttempt(fieldId, getModality(modality));
            result.success(Long.valueOf(i));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void getBioAttempt(@NonNull String fieldId, @NonNull String modality,
            @NonNull BiometricsPigeon.Result<Long> result) {
        try {
            RegistrationDto registrationDto = registrationService.getRegistrationDto();
            int i = registrationDto.getBioAttempt(fieldId, getModality(modality));
            result.success(Long.valueOf(i));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void startOperatorOnboarding(@NonNull BiometricsPigeon.Result<String> result) {
        try {
            OPERATOR_EXCEPTIONS.clear();
            userOnboardService.getOperatorBiometrics().clear();
            userOnboardService.setIdaResponse(false);
            userOnboardService.setIsOnboardSuccess(false);
            result.success("Ok");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void clearBiometricAndDocumentHashmap(@NonNull BiometricsPigeon.Result<String> result) {
        try {
            RegistrationDto registrationDto = registrationService.getRegistrationDto();
            registrationDto.clearBiometricsHashmap();
            registrationDto.clearDocumentsHashmap();
            result.success("Ok");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void saveOperatorBiometrics(@NonNull BiometricsPigeon.Result<String> result) {
        try{
            userOnboardService.onboardOperator(userOnboardService.getOperatorBiometrics(), () -> {
                if(userOnboardService.getIsOnboardSuccess()) {
                    result.success("OK");
                }
            });
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void addBioException(@NonNull String fieldId, @NonNull String modality, @NonNull String attribute,
            @NonNull BiometricsPigeon.Result<String> result) {
        try {
            if(fieldId.equals("operatorBiometrics")){


                OPERATOR_EXCEPTIONS.add(attribute);


//                String key = String.format(BIO_KEY, fieldId, modality.name());
//                ATTEMPTS.put(key, new AtomicInteger(0));
//                for(String attribute : modality.getAttributes()) {
//                    this.biometrics.keySet()
//                            .removeIf(k -> k.startsWith(String.format(BIO_KEY_PATTERN, fieldId, attribute)));
//                }
                result.success("ok");
            }else{
                RegistrationDto registrationDto = registrationService.getRegistrationDto();
                registrationDto.addBioException(fieldId, getModality(modality), attribute);
                result.success("ok");
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void removeBioException(@NonNull String fieldId, @NonNull String modality, @NonNull String attribute,
            @NonNull BiometricsPigeon.Result<String> result) {
        try {
            if(fieldId.equals("operatorBiometrics")){
                OPERATOR_EXCEPTIONS.remove(attribute);
             

                result.success("ok");
            }else{
                RegistrationDto registrationDto = registrationService.getRegistrationDto();
                registrationDto.removeBioException(fieldId, getModality(modality), attribute);
                result.success("ok");
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void getMapValue(@NonNull String key, @NonNull BiometricsPigeon.Result<String> result) {
        String response = globalParamRepository.getCachedStringGlobalParam(key);
        result.success(response == null ? "" : response);
    }

    @Override
    public void getAgeGroup(@NonNull BiometricsPigeon.Result<String> result) {
        String group = "";
        try {
            RegistrationDto registrationDto = registrationService.getRegistrationDto();
            Map<String, Object> age_group = registrationDto.getAgeGroup();
            Object savedGroup = age_group.get("ageGroup");
            if(savedGroup != null) {
                group = (String) savedGroup;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        result.success(group);
    }

    @Override
    public void conditionalBioAttributeValidation(@NonNull String fieldId, @NonNull String expression,
            @NonNull BiometricsPigeon.Result<Boolean> result) {
        try {
            RegistrationDto registrationDto = registrationService.getRegistrationDto();
            List<BiometricsDto> biometricsDtoList = new ArrayList<>();

            for (Modality modality : Modality.values()) {
                List<BiometricsDto> temp = registrationDto.getBestBiometrics(fieldId, modality);
                biometricsDtoList.addAll(temp);
            }
            Map<String, Boolean> dataContext = new HashMap<String, Boolean>();
            Pattern REGEX_PATTERN = Pattern.compile("[a-zA-Z]+");

            Matcher matcher = REGEX_PATTERN.matcher(expression);

            while (matcher.find()) {
                dataContext.put(matcher.group(), false);
                for (BiometricsDto dto : biometricsDtoList) {
                    if (dto.getBioSubType() != null) {
                        if (customMatcher(dto.getBioSubType(), matcher.group())) {
                            dataContext.put(matcher.group(), true);
                            break;
                        }
                    } else {
                        if (dto.getModality().toUpperCase().matches(matcher.group().toUpperCase())) {
                            dataContext.put(matcher.group(), true);
                            break;
                        }
                    }
                }
            }
//            Log.i(TAG, "Printing Map: " + dataContext);
            Boolean response = UserInterfaceHelperService.evaluateValidationExpression(expression, dataContext);
            result.success(response);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public static Boolean customMatcher(String str1, String str2) {
        Boolean result = false;
        if (str1.matches("Left IndexFinger") && str2.matches("leftIndex")) {
            result = true;
        }
        if (str1.matches("Left MiddleFinger") && str2.matches("leftMiddle")) {
            result = true;
        }
        if (str1.matches("Left RingFinger") && str2.matches("leftRing")) {
            result = true;
        }
        if (str1.matches("Left LittleFinger") && str2.matches("leftLittle")) {
            result = true;
        }
        if (str1.matches("Right IndexFinger") && str2.matches("rightIndex")) {
            result = true;
        }
        if (str1.matches("Right MiddleFinger") && str2.matches("rightMiddle")) {
            result = true;
        }
        if (str1.matches("Right RingFinger") && str2.matches("rightRing")) {
            result = true;
        }
        if (str1.matches("Right LittleFinger") && str2.matches("rightLittle")) {
            result = true;
        }
        if (str1.matches("Left Thumb") && str2.matches("leftThumb")) {
            result = true;
        }
        if (str1.matches("Right Thumb") && str2.matches("rightThumb")) {
            result = true;
        }
        if (str1.matches("Left") && str2.matches("leftEye")) {
            result = true;
        }
        if (str1.matches("Right") && str2.matches("rightEye")) {
            result = true;
        }
        if (str1 == null && str2.matches("face")) {
            result = true;
        }
        return result;
    }

    public static Map<String, String> objectToMap(Object object) {
        Map<String, String> map = new HashMap<>();

        // Get all fields of the object using reflection
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true); // Make the private fields accessible

            try {
                Object fieldValue = field.get(object);
                if (fieldValue != null) {
                    // Convert field value to String and add it to the map
                    map.put(field.getName(), fieldValue.toString());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return map;
    }

    private void queryPackage(Intent intent) throws ClientCheckedException {
        List activities = activity.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_ALL);
        // if(activities.size() == 0)
        // throw new ClientCheckedException("Supported apps not found!");
    }

    private Modality getModality(String modality) {
        Modality currentModality = null;
        if (modality.equals("Iris")) {
            currentModality = Modality.IRIS_DOUBLE;
        } else if (modality.equals("LeftHand")) {
            currentModality = Modality.FINGERPRINT_SLAB_LEFT;
        } else if (modality.equals("RightHand")) {
            currentModality = Modality.FINGERPRINT_SLAB_RIGHT;
        } else if (modality.equals("Thumbs")) {
            currentModality = Modality.FINGERPRINT_SLAB_THUMBS;
        } else if (modality.equals("Face")) {
            currentModality = Modality.FACE;
        } else if (modality.equals("Exception")) {
            currentModality = Modality.EXCEPTION_PHOTO;
        }

        return currentModality;
    }


    private String attributeToBioSubType(String attribute){
//        rightThumb, leftThumb,leftIndex, leftLittle, leftRing, leftMiddle,rightEye, leftEye,rightRing, rightIndex, rightMiddle, rightLittle
        if(attribute.equals("rightThumb")){
            return "Right Thumb";
        }else if(attribute.equals("leftThumb")){
            return "Left Thumb";
        }else if(attribute.equals("leftIndex")){
            return "Left IndexFinger";
        }else if(attribute.equals("leftLittle")){
            return "Left LittleFinger";
        }else if(attribute.equals("leftRing")){
            return "Left RingFinger";
        }else if(attribute.equals("leftMiddle")){
            return "Left MiddleFinger";
        }else if(attribute.equals("rightEye")){
            return "Right";
        }else if(attribute.equals("leftEye")){
            return "Left";
        }else if(attribute.equals("rightRing")){
            return "Right RingFinger";
        }else if(attribute.equals("rightIndex")){
            return "Right IndexFinger";
        }else if(attribute.equals("rightMiddle")){
            return "Right MiddleFinger";
        }else if(attribute.equals("rightLittle")){
            return "Right LittleFinger";
        }
        return "";
    }
    private boolean matchOperatorModality(String bioSubType,String modality) {
       boolean result =false;
        if (modality.equals("Iris")) {
            if(bioSubType!=null && (bioSubType.equals("Left")||bioSubType.equals("Right"))){
                result=true;

            }
        } else if (modality.equals("LeftHand")) {
            if(bioSubType!=null && (bioSubType.equals("Left LittleFinger")||bioSubType.equals("Left MiddleFinger")||bioSubType.equals("Left IndexFinger")||bioSubType.equals("Left RingFinger"))){
                result=true;

            }

        } else if (modality.equals("RightHand")) {
            if(bioSubType!=null && (bioSubType.equals("Right LittleFinger")||bioSubType.equals("Right MiddleFinger")||bioSubType.equals("Right IndexFinger")||bioSubType.equals("Right RingFinger"))){
                result=true;

            }
        } else if (modality.equals("Thumbs")) {
            if(bioSubType!=null && (bioSubType.equals("Left Thumb")||bioSubType.equals("Right Thumb"))){
                result=true;

            }
        }

        return result;
    }

    private void discoverSBI() {
        try {
            Log.i("SBI", "Started to discover SBI");
            Intent intent = new Intent();
            intent.setAction(RegistrationConstants.DISCOVERY_INTENT_ACTION);
            queryPackage(intent);
            DiscoverRequest discoverRequest = new DiscoverRequest();
            discoverRequest.setType(currentModality == Modality.EXCEPTION_PHOTO ? SingleType.FACE.value()
                    : currentModality.getSingleType().value());
            intent.putExtra(RegistrationConstants.SBI_INTENT_REQUEST_KEY,
                    objectMapper.writeValueAsBytes(discoverRequest));
            activity.startActivityForResult(intent, 1);
        } catch (Exception ex) {
            auditManagerService.audit(AuditEvent.DISCOVER_SBI_FAILED, Components.REGISTRATION, ex.getMessage());
            Log.e(TAG, ex.getMessage(), ex);
        }
    }

    private void info(String callbackId) {
        if (callbackId == null) {
            Log.e(TAG, "No SBI found");
            return;
        }

        try {
            Intent intent = new Intent();
            intent.setAction(callbackId + RegistrationConstants.D_INFO_INTENT_ACTION);
            queryPackage(intent);
            activity.startActivityForResult(intent, 2);
        } catch (ClientCheckedException ex) {
            auditManagerService.audit(AuditEvent.DEVICE_INFO_FAILED, Components.REGISTRATION, ex.getMessage());
            Log.e(TAG, ex.getMessage(), ex);
        }
    }

    private void rcapture(String callbackId, String deviceId) {
        if (deviceId == null || callbackId == null) {
            Log.e(TAG, "No SBI found!");
            return;
        }

        try {
            Intent intent = new Intent();
            // callbackId = callbackId.replace("\\.info","");
            intent.setAction(callbackId + RegistrationConstants.R_CAPTURE_INTENT_ACTION);
            queryPackage(intent);
            Log.e(TAG, "Initiating capture request : ");
            CaptureRequest captureRequest = biometricsService.getRCaptureRequest(currentModality, deviceId,
                    getExceptionAttributes());
            intent.putExtra("input", objectMapper.writeValueAsBytes(captureRequest));
            activity.startActivityForResult(intent, 3);
        } catch (Exception ex) {
            auditManagerService.audit(AuditEvent.R_CAPTURE_FAILED, Components.REGISTRATION, ex.getMessage());
            Log.e(TAG, ex.getMessage(), ex);
        }
    }

    public void parseDiscoverResponse(Bundle bundle) {
        try {
            byte[] bytes = bundle.getByteArray(RegistrationConstants.SBI_INTENT_RESPONSE_KEY);
            callbackId = biometricsService.handleDiscoveryResponse(currentModality, bytes);
        } catch (BiometricsServiceException e) {
            auditManagerService.audit(AuditEvent.DISCOVER_SBI_PARSE_FAILED, Components.REGISTRATION, e.getMessage());
            Log.e(TAG, "Failed to parse discover response", e);
        }
        info(callbackId);
    }

    public void parseDeviceInfoResponse(Bundle bundle) {
        String callbackId = null;
        String serialNo = null;
        try {
            byte[] bytes = bundle.getByteArray(RegistrationConstants.SBI_INTENT_RESPONSE_KEY);
            String[] result = biometricsService.handleDeviceInfoResponse(currentModality, bytes);
            callbackId = result[0];
            serialNo = result[1];
            Log.i(TAG, callbackId + " --> " + serialNo);
        } catch (Exception e) {
            auditManagerService.audit(AuditEvent.DEVICE_INFO_PARSE_FAILED, Components.REGISTRATION, e.getMessage());
            Log.e(TAG, "Failed to parse device info response", e);
        }
        rcapture(callbackId, serialNo);
    }

//    public void removeDuplicatesFromOperatorBiometricList(String bioSubType){
//        List<BiometricsDto> biometricsDtoListCopy=new ArrayList<>();
//        biometricsDtoListCopy.addAll(userOnboardService.getOperatorBiometrics());
//        biometricsDtoListCopy.forEach(biometricsDto -> {
//            if(biometricsDto.getBioSubType()==null && bioSubType==null){
//                userOnboardService.getOperatorBiometrics().remove(biometricsDto);
//                return;
//            }
//            if(biometricsDto.getBioSubType().equals(bioSubType)){
//                userOnboardService.getOperatorBiometrics().remove(biometricsDto);
//            }
//        });
//
//    }



    public void removeDuplicatesFromOperatorBiometricList(Modality modality){
        List<BiometricsDto> biometricsDtoListCopy=new ArrayList<>();

        userOnboardService.getOperatorBiometrics().forEach(biometricsDto -> {
            if (modality==Modality.IRIS_DOUBLE) {
                if(biometricsDto.getBioSubType()!=null && (biometricsDto.getBioSubType().equals("Left")||biometricsDto.getBioSubType().equals("Right"))){

                }else{
                    biometricsDtoListCopy.add(biometricsDto);
                }
            } else if (modality==Modality.FINGERPRINT_SLAB_LEFT) {
                if(biometricsDto.getBioSubType()!=null &&(biometricsDto.getBioSubType().equals("Left LittleFinger")||biometricsDto.getBioSubType().equals("Left MiddleFinger")||biometricsDto.getBioSubType().equals("Left IndexFinger")||biometricsDto.getBioSubType().equals("Left RingFinger"))){


                }else{
                    biometricsDtoListCopy.add(biometricsDto);
                }
            } else if (modality==Modality.FINGERPRINT_SLAB_RIGHT) {
                if(biometricsDto.getBioSubType()!=null &&(biometricsDto.getBioSubType().equals("Right LittleFinger")||biometricsDto.getBioSubType().equals("Right MiddleFinger")||biometricsDto.getBioSubType().equals("Right IndexFinger")||biometricsDto.getBioSubType().equals("Right RingFinger"))){


                }else{
                    biometricsDtoListCopy.add(biometricsDto);
                }
            }else if (modality==Modality.FINGERPRINT_SLAB_THUMBS) {
                if(biometricsDto.getBioSubType()!=null &&(biometricsDto.getBioSubType().equals("Left Thumb")||biometricsDto.getBioSubType().equals("Right Thumb"))){


                }else{
                    biometricsDtoListCopy.add(biometricsDto);
                }
            }else if (modality==Modality.FACE) {
                if(biometricsDto.getBioSubType()==null){

                }else{
                    biometricsDtoListCopy.add(biometricsDto);
                }
            }
        });
        userOnboardService.getOperatorBiometrics().clear();
        userOnboardService.getOperatorBiometrics().addAll(biometricsDtoListCopy);


    }

    public void parseRCaptureResponse(Bundle bundle) {
        try {

            Uri uri = bundle.getParcelable(RegistrationConstants.SBI_INTENT_RESPONSE_KEY);
            InputStream respData = activity.getContentResolver().openInputStream(uri);
            List<BiometricsDto> biometricsDtoList = biometricsService.handleRCaptureResponse(currentModality, respData,
                    getExceptionAttributes());
            // if attempts is zero, there is no need to maintain the counter
            if (fieldId.equals("operatorBiometrics")) {
                removeDuplicatesFromOperatorBiometricList(currentModality);
                biometricsDtoList.forEach(biometricsDto -> {userOnboardService.getOperatorBiometrics().add(biometricsDto);});
                List<BiometricsDto> biometricsDtoListCopy=new ArrayList<>();
                biometricsDtoListCopy.addAll(biometricsDtoList);
                if(biometricsDtoListCopy!=null && OPERATOR_EXCEPTIONS!=null && !(OPERATOR_EXCEPTIONS.isEmpty())){
                    for (String exception : OPERATOR_EXCEPTIONS) {
                        for (BiometricsDto biometricsDto : biometricsDtoListCopy) {
                            if(biometricsDto.getBioSubType()==null){
                                break;
                            }

                            if (biometricsDto.getBioSubType().equals(attributeToBioSubType(exception))) {
                                userOnboardService.getOperatorBiometrics().remove(biometricsDto);
                                biometricsDtoList.remove(biometricsDto);
                            }
                        }
                    }
                }


                result1.success("Ok");
            } else {
                currentAttempt = this.registrationService.getRegistrationDto().getBioAttempt(fieldId, currentModality);

                if(!biometricsDtoList.isEmpty()){
                    biometricsDtoList.forEach(dto -> {
                        try {
                            this.registrationService.getRegistrationDto().addBiometric(fieldId,
                                    (currentModality == Modality.EXCEPTION_PHOTO) || (currentModality == Modality.FACE)
                                            ? currentModality.getAttributes().get(0)
                                            : Modality.getBioAttribute(dto.getBioSubType()),
                                    currentAttempt, dto);

                            result1.success("Ok");
                        } catch (Exception ex) {
                            Log.e(TAG, ex.getMessage(), ex);
                        }
                    });
                } else {
                    Toast.makeText(activity.getApplicationContext(),
                            "Unable to capture biometric data",Toast.LENGTH_SHORT).show();
                }
            }

        } catch (Exception e) {
            auditManagerService.audit(AuditEvent.R_CAPTURE_PARSE_FAILED, Components.REGISTRATION, e.getMessage());
            Log.e(TAG, "Failed to parse rcapture response", e);
        }

    }

    private List<String> getExceptionAttributes() {
        return currentModality.getAttributes().stream()
                .filter(it -> {
                    try {
                        return this.registrationService.getRegistrationDto().isBioException(fieldId, currentModality,
                                it);
                    } catch (Exception e) {
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }
}
