package io.mosip.registration.clientmanager.util;

//import io.mosip.commons.khazana.constant.KhazanaConstant;
//import io.mosip.commons.khazana.constant.KhazanaErrorCodes;
//import io.mosip.commons.khazana.exception.ObjectStoreAdapterException;
//import io.mosip.kernel.core.util.CryptoUtil;
//import io.mosip.kernel.cryptomanager.dto.CryptomanagerRequestDto;
//import io.mosip.kernel.cryptomanager.service.CryptomanagerService;
//import io.mosip.kernel.cryptomanager.service.impl.CryptomanagerServiceImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.ApplicationContext;
//import org.springframework.stereotype.Component;

import android.content.Context;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import dagger.Component;
import io.mosip.registration.clientmanager.dto.crypto.CryptoRequestDto;
import io.mosip.registration.clientmanager.service.crypto.LocalClientCryptoServiceImpl;
import io.mosip.registration.clientmanager.spi.crypto.ClientCryptoManagerService;

//@Component
public class OfflineEncryptionUtil {
    public static final String APPLICATION_ID = "REGISTRATION";

    // encoder and decoder from java itself
    private static Base64.Encoder base64encoder;
    private static Base64.Decoder base64decoder;

    //@Autowired
    private Context applicationContext;

    //@Value("${mosip.utc-datetime-pattern:yyyy-MM-dd'T'HH:mm:ss.SSS'Z'}")
    private String DATETIME_PATTERN;

    /**
     * The cryptomanager service.
     */
    private ClientCryptoManagerService cryptomanagerService = null;

    /**
     * The sign applicationid.
     */
    //@Value("${mosip.sign.applicationid:KERNEL}")
    private String signApplicationid;

    /**
     * The sign refid.
     */
    //@Value("${mosip.sign.refid:SIGN}")
    private String signRefid;

    //@Value("${mosip.kernel.registrationcenterid.length:5}")
    private int centerIdLength;

    //@Value("${mosip.kernel.machineid.length:5}")
    private int machineIdLength;

    //@Value("${crypto.PrependThumbprint.enable:true}")
    private boolean isPrependThumbprintEnabled;

    public OfflineEncryptionUtil(Context context) {
        applicationContext = context;
        DATETIME_PATTERN = ConfigService.getProperty("mosip.utc-datetime-pattern", context);
        signApplicationid = ConfigService.getProperty("mosip.sign.applicationid", context);
        signRefid = ConfigService.getProperty("mosip.sign.refid", context);
        centerIdLength = Integer.parseInt(ConfigService.getProperty("mosip.kernel.registrationcenterid.length", context));
        machineIdLength = Integer.parseInt(ConfigService.getProperty("mosip.kernel.machineid.length", context));
        isPrependThumbprintEnabled = Boolean.parseBoolean(ConfigService.getProperty("crypto.PrependThumbprint.enable", context));
        base64encoder = Base64.getEncoder();
        base64decoder = Base64.getDecoder();
    }

    public byte[] encrypt(String id, byte[] packet) {
        String centerId = id.substring(0, centerIdLength);
        String machineId = id.substring(centerIdLength, centerIdLength + machineIdLength);
        String refId = centerId + "_" + machineId;
        String packetString = base64encoder.encodeToString(packet);
        CryptoRequestDto cryptomanagerRequestDto = new CryptoRequestDto();
//        cryptomanagerRequestDto.setApplicationId(APPLICATION_ID);
          cryptomanagerRequestDto.setValue(packetString);
//        cryptomanagerRequestDto.setPrependThumbprint(isPrependThumbprintEnabled);
//        cryptomanagerRequestDto.setReferenceId(refId);
//
//        SecureRandom sRandom = new SecureRandom();
        //TODO remove hardcoding
        byte[] nonce = new byte[12];
        byte[] aad = new byte[32];
//        sRandom.nextBytes(nonce);
//        sRandom.nextBytes(aad);
//        cryptomanagerRequestDto.setAad(CryptoUtil.encodeBase64String(aad));
//        cryptomanagerRequestDto.setSalt(CryptoUtil.encodeBase64String(nonce));
        // setLocal Date Time
        if (id.length() > 14) {
            String packetCreatedDateTime = id.substring(id.length() - 14);
            String formattedDate = packetCreatedDateTime.substring(0, 8) + "T"
                    + packetCreatedDateTime.substring(packetCreatedDateTime.length() - 6);

            //cryptomanagerRequestDto.setTimeStamp(LocalDateTime.parse(formattedDate, DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")));
        } else {
            throw new RuntimeException("Packet Encryption Failed-Invalid Packet format");
            //throw new ObjectStoreAdapterException(KhazanaErrorCodes.ENCRYPTION_FAILURE.getErrorCode(), KhazanaErrorCodes.ENCRYPTION_FAILURE.getErrorMessage());
        }

        //byte[] encryptedData = base64decoder.decode((getCryptomanagerService().encrypt(cryptomanagerRequestDto).getValue());

        //return EncryptionUtil.mergeEncryptedData(encryptedData, nonce, aad);
        return null;
    }

    private ClientCryptoManagerService getCryptomanagerService() {
        if (cryptomanagerService == null)
            cryptomanagerService = new LocalClientCryptoServiceImpl(applicationContext);
        return cryptomanagerService;
    }
}
