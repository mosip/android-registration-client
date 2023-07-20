package io.mosip.registration_client.api_services;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

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
import java.util.List;
import java.util.Map;
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
import io.mosip.registration.clientmanager.service.Biometrics095Service;
import io.mosip.registration.clientmanager.service.RegistrationServiceImpl;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
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



    private Modality currentModality;

    private static final String TAG="BiometricsDetailApi";

    private String callbackId;

    private String fieldId;
    private String purpose;
    private int capturedAttempts;
    private int currentAttempt;
    private int allowedAttempts;
    private int qualityThreshold;
    List<BiometricsDto> biometricsDtoList;

    private List<Bitmap> listBitmaps1=new ArrayList<>();
    private byte[] byteArrayTester;
    private List<byte[]> listByteArrayTester1=new ArrayList<>();
    BiometricsPigeon.Result<String> result1;

    @Inject
        public BiometricsDetailsApi(AuditManagerService auditManagerService, ObjectMapper objectMapper, Biometrics095Service biometrics095Service, RegistrationService registrationService) {
        this.auditManagerService = auditManagerService;
        this.objectMapper = objectMapper;
        this.biometricsService=biometrics095Service;
        this.registrationService=registrationService;

    }

    public void setCallbackActivity(MainActivity mainActivity){
        this.activity=mainActivity;
    }


    @Override
    public void invokeDiscoverSbi(@NonNull String fieldId, @NonNull String modality, @NonNull BiometricsPigeon.Result<String> result) {
        currentModality = getModality(modality);
        this.fieldId=fieldId;
        discoverSBI();
        result1=result;
    }

    @Override
    public void getBestBiometrics(@NonNull String fieldId, @NonNull String modality, @NonNull BiometricsPigeon.Result<List<String>> result) {
        try{

            RegistrationDto registrationDto=registrationService.getRegistrationDto();

            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json;
            List<String> jsonList=new ArrayList<>();
        biometricsDtoList=registrationDto.getBestBiometrics(fieldId,getModality(modality));
            for(int i=0;i<biometricsDtoList.size();i++){
                json=ow.writeValueAsString(biometricsDtoList.get(i));
                jsonList.add(json);

            }

            result.success(jsonList);


        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }
    }

    @Override
    public void extractImageValues(@NonNull String fieldId, @NonNull String modality, @NonNull BiometricsPigeon.Result<List<byte[]>> result) {
        List<Bitmap> listBitmaps=new ArrayList<>();
        List<byte[]> listByteArrayTester=new ArrayList<>();
        try{
            RegistrationDto registrationDto=registrationService.getRegistrationDto();

            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json;
            List<String> jsonList=new ArrayList<>();

            switch (getModality(modality)) {
                case FACE:
                {
                    try{
                        Bitmap var5;
                        ByteArrayInputStream bais = new ByteArrayInputStream(CryptoUtil.base64decoder.decode(registrationDto.getBestBiometrics(fieldId,getModality(modality)).get(0).getBioValue()));
                        DataInputStream inputStream = new DataInputStream(bais);
                        FaceBDIR faceBDIR = new FaceBDIR(inputStream);
                        byte[] bytes = faceBDIR.getRepresentation().getRepresentationData().getImageData().getImage();
                        var5 = (new JP2Decoder(bytes)).decode();
                        listBitmaps.add(var5);
                    }catch(Exception e){
                        Log.e(TAG,e.getMessage());
                    }
                }
                break;
                case FINGERPRINT_SLAB_LEFT:
                case FINGERPRINT_SLAB_THUMBS:
                case FINGERPRINT_SLAB_RIGHT:
                {
                    try{
                        Bitmap var5;
                        for (int i = 0; i < registrationDto.getBestBiometrics(fieldId,getModality(modality)).size(); i++) {
                            ByteArrayInputStream bais = new ByteArrayInputStream(CryptoUtil.base64decoder.decode(registrationDto.getBestBiometrics(fieldId,getModality(modality)).get(i).getBioValue()));
                            DataInputStream inputStream = new DataInputStream(bais);
                            FingerBDIR fingerBDIR = new FingerBDIR(inputStream);
                            byte[] bytes = fingerBDIR.getRepresentation().getRepresentationBody().getImageData().getImage();
                            var5 = (new JP2Decoder(bytes)).decode();
                            listBitmaps.add(var5);
                        }
                    }catch (Exception e){
                        Log.e(TAG,e.getMessage());
                    }
                }
                break;
                case IRIS_DOUBLE:
                {
                    try{
                        Bitmap var5;
                        for (int i = 0; i < registrationDto.getBestBiometrics(fieldId,getModality(modality)).size(); i++){
                            ByteArrayInputStream bais = new ByteArrayInputStream(CryptoUtil.base64decoder.decode(registrationDto.getBestBiometrics(fieldId,getModality(modality)).get(0).getBioValue()));
                            DataInputStream inputStream = new DataInputStream(bais);
                            IrisBDIR irisBDIR = new IrisBDIR(inputStream);
                            byte[] bytes = irisBDIR.getRepresentation().getRepresentationData().getImageData().getImage();
                            var5 = (new JP2Decoder(bytes)).decode();
                            listBitmaps.add(var5);
                        }
                    }catch (Exception e){
                        Log.e(TAG,e.getMessage());
                    }
                }
                break;
                case EXCEPTION_PHOTO:

                    break;
            }
            for (int i = 0; i < listBitmaps.size(); i++) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                listBitmaps.get(i).compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArrayTester= stream.toByteArray();
                listByteArrayTester.add(byteArrayTester);
            }


            listByteArrayTester1=listByteArrayTester;
            result.success(listByteArrayTester1);
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }
    }

    @Override
    public void addBioException(@NonNull String fieldId, @NonNull String modality, @NonNull String attribute, @NonNull BiometricsPigeon.Result<String> result) {
        try{
            RegistrationDto registrationDto=registrationService.getRegistrationDto();
            registrationDto.addBioException(fieldId,getModality(modality),attribute);
            result.success("ok");
        }catch(Exception e){
            Log.e(TAG,e.getMessage());
        }
    }

    @Override
    public void removeBioException(@NonNull String fieldId, @NonNull String modality, @NonNull String attribute, @NonNull BiometricsPigeon.Result<String> result) {
        try{
            RegistrationDto registrationDto=registrationService.getRegistrationDto();
            registrationDto.addBioException(fieldId,getModality(modality),attribute);
            result.success("ok");
        }catch(Exception e){
            Log.e(TAG,e.getMessage());
        }
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
        //if(activities.size() == 0)
        //    throw new ClientCheckedException("Supported apps not found!");
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

    private void discoverSBI() {
        try {
            Log.i("SBI","Started to discover SBI");
            Intent intent = new Intent();
            intent.setAction(RegistrationConstants.DISCOVERY_INTENT_ACTION);
            queryPackage(intent);
            DiscoverRequest discoverRequest = new DiscoverRequest();
            discoverRequest.setType(currentModality == Modality.EXCEPTION_PHOTO ? SingleType.FACE.name() :
                    currentModality.getSingleType().name());
            intent.putExtra(RegistrationConstants.SBI_INTENT_REQUEST_KEY, objectMapper.writeValueAsBytes(discoverRequest));
            activity.startActivityForResult(intent, 1);
        } catch (Exception ex) {
            auditManagerService.audit(AuditEvent.DISCOVER_SBI_FAILED, Components.REGISTRATION, ex.getMessage());
            Log.e(TAG, ex.getMessage(), ex);
        }
    }

    private void info(String callbackId) {
        if (callbackId == null) {
            Log.e(TAG,"No SBI found");
//            Toast.makeText(this, "No SBI found!", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            Intent intent = new Intent();
            intent.setAction(callbackId + RegistrationConstants.D_INFO_INTENT_ACTION);
            queryPackage(intent);
//            Toast.makeText(getApplicationContext(), "Initiating Device info request : " + callbackId,
//                    Toast.LENGTH_LONG).show();
            activity.startActivityForResult(intent, 2);
        } catch (ClientCheckedException ex) {
            auditManagerService.audit(AuditEvent.DEVICE_INFO_FAILED, Components.REGISTRATION, ex.getMessage());
            Log.e(TAG, ex.getMessage(), ex);
//            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void rcapture(String callbackId, String deviceId) {
        if (deviceId == null || callbackId == null) {
//            Toast.makeText(this, "No SBI found!", Toast.LENGTH_LONG).show();
            Log.e(TAG,"No SBI found!");
            return;
        }

        try {
            Intent intent = new Intent();
            //callbackId = callbackId.replace("\\.info","");
            intent.setAction(callbackId + RegistrationConstants.R_CAPTURE_INTENT_ACTION);
            queryPackage(intent);
            Log.e(TAG,"Initiating capture request : ");
//            Toast.makeText(this, "Initiating capture request : " + callbackId, Toast.LENGTH_LONG).show();
            CaptureRequest captureRequest = biometricsService.getRCaptureRequest(currentModality, deviceId,
                    getExceptionAttributes());
            intent.putExtra("input", objectMapper.writeValueAsBytes(captureRequest));
            activity.startActivityForResult(intent, 3);
        } catch (Exception ex) {
            auditManagerService.audit(AuditEvent.R_CAPTURE_FAILED, Components.REGISTRATION, ex.getMessage());
            Log.e(TAG, ex.getMessage(), ex);
//            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
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

    public void parseRCaptureResponse(Bundle bundle) {
        try {
            Uri uri = bundle.getParcelable(RegistrationConstants.SBI_INTENT_RESPONSE_KEY);
            InputStream respData = activity.getContentResolver().openInputStream(uri);
            List<BiometricsDto> biometricsDtoList = biometricsService.handleRCaptureResponse(currentModality, respData,
                    getExceptionAttributes());
            //if attempts is zero, there is no need to maintain the counter
            currentAttempt = allowedAttempts <= 0 ? 0 : this.registrationService.getRegistrationDto().incrementBioAttempt(fieldId, currentModality);
            biometricsDtoList.forEach( dto -> {
                try {
                    this.registrationService.getRegistrationDto().addBiometric(fieldId,
                            currentModality == Modality.EXCEPTION_PHOTO ? currentModality.getAttributes().get(0) :
                                    Modality.getBioAttribute(dto.getBioSubType()), currentAttempt, dto);

                    result1.success("Ok");
                } catch (Exception ex) { Log.e(TAG, ex.getMessage(), ex); }
            });


        } catch (Exception e) {
            auditManagerService.audit(AuditEvent.R_CAPTURE_PARSE_FAILED, Components.REGISTRATION, e.getMessage());
            Log.e(TAG, "Failed to parse rcapture response", e);
//            Toast.makeText(this, "Failed parsing Capture response : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
    private List<String> getExceptionAttributes() {
        return currentModality.getAttributes().stream()
                .filter( it ->
                {
                    try {
                        return this.registrationService.getRegistrationDto().isBioException(fieldId, currentModality, it);
                    } catch (Exception e) {}
                    return false;
                })
                .collect(Collectors.toList());
    }
}
