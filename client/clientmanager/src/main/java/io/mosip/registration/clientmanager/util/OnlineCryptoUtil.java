package io.mosip.registration.clientmanager.util;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;

import dagger.Component;
import io.mosip.registration.clientmanager.dto.crypto.CryptoRequestDto;

public class OnlineCryptoUtil {

    public static final String APPLICATION_ID = "REGISTRATION";
    private static final String DECRYPT_SERVICE_ID = "mosip.cryptomanager.decrypt";
    private static final String IO_EXCEPTION = "Exception while reading packet inputStream";
    private static final String DATE_TIME_EXCEPTION = "Error while parsing packet timestamp";

    private static Base64.Encoder base64encoder;
    private static Base64.Decoder base64decoder;

    //@Value("${mosip.utc-datetime-pattern:yyyy-MM-dd'T'HH:mm:ss.SSS'Z'}")
    private String DATETIME_PATTERN;

    //@Value("${mosip.kernel.cryptomanager.request_version:v1}")
    private String APPLICATION_VERSION;

    //@Value("${mosip.kernel.registrationcenterid.length:5}")
    private int centerIdLength;

    //@Value("${CRYPTOMANAGER_DECRYPT:null}")
    private String cryptomanagerDecryptUrl;

    //@Value("${mosip.kernel.machineid.length:5}")
    private int machineIdLength;

    //@Value("${CRYPTOMANAGER_ENCRYPT:null}")
    private String cryptomanagerEncryptUrl;

    //@Value("${crypto.PrependThumbprint.enable:true}")
    private boolean isPrependThumbprintEnabled;

    //@Autowired
    private ObjectMapper mapper;

    //@Autowired
    private Context applicationContext;

    //private RestTemplate restTemplate = null;

    public OnlineCryptoUtil(Context context){
        applicationContext = context;
        base64encoder = Base64.getEncoder();
        base64decoder = Base64.getDecoder();
    }


    public byte[] encrypt(String id, byte[] packet) {
        byte[] encryptedPacket = null;

        try {
            String centerId = id.substring(0, centerIdLength);
            String machineId = id.substring(centerIdLength, centerIdLength + machineIdLength);
            String refId = centerId + "_" + machineId;
            String packetString = base64encoder.encodeToString(packet);
            CryptoRequestDto cryptomanagerRequestDto = new CryptoRequestDto();
//            RequestWrapper<CryptoRequestDto> request = new RequestWrapper<>();
//            cryptomanagerRequestDto.setApplicationId(APPLICATION_ID);
              cryptomanagerRequestDto.setValue(packetString);
//            cryptomanagerRequestDto.setReferenceId(refId);
//            cryptomanagerRequestDto.setPrependThumbprint(isPrependThumbprintEnabled);
//
//            SecureRandom sRandom = new SecureRandom();
            //TODO remove hardcoding
            byte[] nonce = new byte[12];
            byte[] aad = new byte[32];
            //sRandom.nextBytes(nonce);
            //sRandom.nextBytes(aad);
            //cryptomanagerRequestDto.setAad(CryptoUtil.encodeBase64String(aad));
            //cryptomanagerRequestDto.setSalt(CryptoUtil.encodeBase64String(nonce));
            // setLocal Date Time
            if (id.length() > 14) {
                String packetCreatedDateTime = id.substring(id.length() - 14);
                String formattedDate = packetCreatedDateTime.substring(0, 8) + "T"
                        + packetCreatedDateTime.substring(packetCreatedDateTime.length() - 6);

                //cryptomanagerRequestDto.setTimeStamp(
                //        LocalDateTime.parse(formattedDate, DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")));
            } else {
                throw new RuntimeException("Packet Encryption Failed-Invalid Packet format");
                //throw new ObjectStoreAdapterException("", "Packet Encryption Failed-Invalid Packet format");
            }
//            request.setId(DECRYPT_SERVICE_ID);
//            request.setMetadata(null);
//            request.setRequest(cryptomanagerRequestDto);
//            DateTimeFormatter format = DateTimeFormatter.ofPattern(DATETIME_PATTERN);
//            LocalDateTime localdatetime = LocalDateTime
//                    .parse(DateUtils.getUTCCurrentDateTimeString(DATETIME_PATTERN), format);
//            request.setRequesttime(localdatetime);
//            request.setVersion(APPLICATION_VERSION);
//            HttpEntity<RequestWrapper<CryptomanagerRequestDto>> httpEntity = new HttpEntity<>(request);
//
//            ResponseEntity<String> response = getRestTemplate().exchange(cryptomanagerEncryptUrl, HttpMethod.POST, httpEntity, String.class);
//            CryptomanagerResponseDto responseObject = mapper.readValue(response.getBody(), CryptomanagerResponseDto.class);
//            if (responseObject != null &&
//                    responseObject.getErrors() != null && !responseObject.getErrors().isEmpty()) {
//                ServiceError error = responseObject.getErrors().get(0);
//                throw new ObjectStoreAdapterException("", error.getMessage());
//            }
//            encryptedPacket = responseObject.getResponse().getData().getBytes();
//            byte[] encryptedData = CryptoUtil.decodeBase64(responseObject.getResponse().getData());
//            encryptedPacket = mergeEncryptedData(encryptedData, nonce, aad);
        } catch (Exception e) {
//            throw new ObjectStoreAdapterException("", IO_EXCEPTION, e);
        }
        return encryptedPacket;
    }

    private byte[] mergeEncryptedData(byte[] encryptedData, byte[] nonce, byte[] aad) {
        byte[] finalEncData = new byte[encryptedData.length + 32 + 12];
        System.arraycopy(nonce, 0, finalEncData, 0, nonce.length);
        System.arraycopy(aad, 0, finalEncData, nonce.length, aad.length);
        System.arraycopy(encryptedData, 0, finalEncData, nonce.length + aad.length,	encryptedData.length);
        return finalEncData;
    }

//    private RestTemplate getRestTemplate() {
////        if (restTemplate == null)
////            restTemplate = applicationContext.getBean(RestTemplate.class);
////        return restTemplate;
//        return null;
//    }


    public byte[] decrypt(String id, byte[] packet) {
        byte[] decryptedPacket = null;
//
//        try {
//            String centerId = id.substring(0, centerIdLength);
//            String machineId = id.substring(centerIdLength, centerIdLength + machineIdLength);
//            String refId = centerId + "_" + machineId;
//            CryptomanagerRequestDto cryptomanagerRequestDto = new CryptomanagerRequestDto();
//            RequestWrapper<CryptomanagerRequestDto> request = new RequestWrapper<>();
//            cryptomanagerRequestDto.setApplicationId(APPLICATION_ID);
//            cryptomanagerRequestDto.setReferenceId(refId);
//            byte[] nonce = Arrays.copyOfRange(packet, 0, KhazanaConstant.GCM_NONCE_LENGTH);
//            byte[] aad = Arrays.copyOfRange(packet, KhazanaConstant.GCM_NONCE_LENGTH,
//                    KhazanaConstant.GCM_NONCE_LENGTH + KhazanaConstant.GCM_AAD_LENGTH);
//            byte[] encryptedData = Arrays.copyOfRange(packet, KhazanaConstant.GCM_NONCE_LENGTH + KhazanaConstant.GCM_AAD_LENGTH,
//                    packet.length);
//            cryptomanagerRequestDto.setAad(CryptoUtil.encodeBase64String(aad));
//            cryptomanagerRequestDto.setSalt(CryptoUtil.encodeBase64String(nonce));
//            cryptomanagerRequestDto.setData(CryptoUtil.encodeBase64String(encryptedData));
//            cryptomanagerRequestDto.setPrependThumbprint(isPrependThumbprintEnabled);
//            // setLocal Date Time
//            if (id.length() > 14) {
//                String packetCreatedDateTime = id.substring(id.length() - 14);
//                String formattedDate = packetCreatedDateTime.substring(0, 8) + "T"
//                        + packetCreatedDateTime.substring(packetCreatedDateTime.length() - 6);
//
//                cryptomanagerRequestDto.setTimeStamp(
//                        LocalDateTime.parse(formattedDate, DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")));
//            } else {
//                throw new ObjectStoreAdapterException("","Packet DecryptionFailed-Invalid Packet format");
//            }
//            request.setId(DECRYPT_SERVICE_ID);
//            request.setMetadata(null);
//            request.setRequest(cryptomanagerRequestDto);
//            DateTimeFormatter format = DateTimeFormatter.ofPattern(DATETIME_PATTERN);
//            LocalDateTime localdatetime = LocalDateTime
//                    .parse(DateUtils.getUTCCurrentDateTimeString(DATETIME_PATTERN), format);
//            request.setRequesttime(localdatetime);
//            request.setVersion(APPLICATION_VERSION);
//            HttpEntity<RequestWrapper<CryptomanagerRequestDto>> httpEntity = new HttpEntity<>(request);
//
//            ResponseEntity<String> response = restTemplate.exchange(cryptomanagerDecryptUrl, HttpMethod.POST, httpEntity, String.class);
//
//            CryptomanagerResponseDto responseObject = mapper.readValue(response.getBody(), CryptomanagerResponseDto.class);
//
//            if (responseObject != null &&
//                    responseObject.getErrors() != null && !responseObject.getErrors().isEmpty()) {
//                ServiceError error = responseObject.getErrors().get(0);
//                throw new ObjectStoreAdapterException("",error.getMessage());
//            }
//            decryptedPacket = CryptoUtil.decodeBase64(responseObject.getResponse().getData());
//
//        } catch (Exception e) {
//            throw new ObjectStoreAdapterException("", "",e);
//        }
        return decryptedPacket;
    }
}
