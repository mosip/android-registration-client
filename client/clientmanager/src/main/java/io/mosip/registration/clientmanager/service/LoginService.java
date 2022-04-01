package io.mosip.registration.clientmanager.service;

import android.content.Context;

import org.json.JSONObject;

/*import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;*/
import io.mosip.registration.clientmanager.config.ClientDatabase;
import io.mosip.registration.clientmanager.dao.UserTokenDao;
import io.mosip.registration.clientmanager.entity.UserToken;
import io.mosip.registration.clientmanager.factory.SyncRestFactory;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.keymanager.dto.CryptoRequestDto;
import io.mosip.registration.keymanager.dto.CryptoResponseDto;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.keymanager.util.CryptoUtil;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LoginService {

    private static final String TAG = LoginService.class.getSimpleName();

    private Context context;

    @Inject
    ClientCryptoManagerService clientCryptoManagerService;

    @Inject
    SyncRestFactory syncRestFactory;

    @Inject
    SyncRestService syncRestService;

    public LoginService(Context context, ClientCryptoManagerService clientCryptoManagerService) {
        this.context = context;
        this.clientCryptoManagerService = clientCryptoManagerService;
    }

    public void login(String authResponse) throws Exception {
        CryptoRequestDto cryptoRequestDto = new CryptoRequestDto();
        cryptoRequestDto.setValue(authResponse);
        CryptoResponseDto cryptoResponseDto = clientCryptoManagerService.decrypt(cryptoRequestDto);
        if(cryptoResponseDto != null) {
            byte[] decodedBytes = CryptoUtil.base64decoder.decode(cryptoResponseDto.getValue());
            JSONObject authResponseJson = new JSONObject(new String(decodedBytes));
            //Jws<Claims> claims = Jwts.parserBuilder().build().parseClaimsJws((String) authResponseJson.get("token"));
            UserTokenDao userTokenDao = ClientDatabase.getDatabase(this.context).userTokenDao();
            UserToken userToken = new UserToken("test","test", "test", 0L, 0L);
            userTokenDao.insert(userToken);
        }
        throw new Exception("Login failed, Unable to extract auth token");
    }
}
