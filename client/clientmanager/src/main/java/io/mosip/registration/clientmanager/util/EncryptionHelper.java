package io.mosip.registration.clientmanager.util;

import android.content.Context;

import javax.inject.Inject;

//import org.springframework.stereotype.Component;

public class EncryptionHelper {

    private static final String CRYPTO = "OfflinePacketCryptoServiceImpl";

    //@Value("${objectstore.crypto.name:OfflinePacketCryptoServiceImpl}")
    private String cryptoName;

    @Inject
    public OfflineEncryptionUtil offlineEncryptionUtil;

    //@Autowired
    private OnlineCryptoUtil onlineCryptoUtil;

    public EncryptionHelper(Context context) {
        cryptoName = ConfigService.getProperty("object.store.crypto.name", context);
        offlineEncryptionUtil = new OfflineEncryptionUtil(context);
    }

    public byte[] encrypt(String id, byte[] packet) {
        return offlineEncryptionUtil.encrypt(id, packet);
    }

}
