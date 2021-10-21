package io.mosip.registration.clientmanager.util;

import android.content.Context;

//import org.springframework.stereotype.Component;

public class EncryptionHelper {

    private static final String CRYPTO = "OfflinePacketCryptoServiceImpl";

    //@Value("${objectstore.crypto.name:OfflinePacketCryptoServiceImpl}")
    private String cryptoName;

    //@Autowired
    private OfflineEncryptionUtil offlineEncryptionUtil;

    //@Autowired
    private OnlineCryptoUtil onlineCryptoUtil;

    public EncryptionHelper(Context context) {
        cryptoName = ConfigService.getProperty("object.store.crypto.name", context);
    }

    public byte[] encrypt(String id, byte[] packet) {
        return offlineEncryptionUtil.encrypt(id, packet);
    }

}
