package io.mosip.registration.clientmanager;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;


import org.junit.Before;


import static org.junit.Assert.*;


import static io.mosip.registration.clientmanager.constant.KeyManagerConstant.KEY_ENDEC;
import static io.mosip.registration.clientmanager.constant.KeyManagerConstant.KEY_SIGN;

import io.mosip.registration.clientmanager.service.crypto.LocalClientCryptoServiceImpl;


import android.util.Log;
import android.R;

import io.mosip.registration.clientmanager.dto.crypto.CryptoRequestDto;
import io.mosip.registration.clientmanager.dto.crypto.CryptoResponseDto;
import io.mosip.registration.clientmanager.dto.crypto.PublicKeyRequestDto;
import io.mosip.registration.clientmanager.dto.crypto.PublicKeyResponseDto;
import io.mosip.registration.clientmanager.dto.crypto.SignRequestDto;
import io.mosip.registration.clientmanager.dto.crypto.SignResponseDto;
import io.mosip.registration.clientmanager.dto.crypto.SignVerifyRequestDto;
import io.mosip.registration.clientmanager.dto.crypto.SignVerifyResponseDto;
import io.mosip.registration.clientmanager.spi.crypto.ClientCryptoManagerService;
import io.mosip.registration.clientmanager.util.ConfigService;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private static LocalClientCryptoServiceImpl localClientCryptoService;

    //    encryption and decryption keys
    private static PublicKeyRequestDto publicKeyRequestDto_encdec;
    private static PublicKeyResponseDto publicKeyResponseDto_encdec;

    //    sign and verify keys
    private static PublicKeyRequestDto publicKeyRequestDto_sign;
    private static PublicKeyResponseDto publicKeyResponseDto_sign;

    //    to do initializations
    @Before
    public void init(){
//        creating localclientcrypto object for testing
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        localClientCryptoService=new LocalClientCryptoServiceImpl(appContext);
        localClientCryptoService.initLocalClientCryptoService(appContext);

//        creating keys for encryption and decryption testing
        publicKeyRequestDto_encdec= new PublicKeyRequestDto();
        publicKeyRequestDto_encdec.setAlias(KEY_ENDEC);
        publicKeyResponseDto_encdec = localClientCryptoService.getPublicKey(publicKeyRequestDto_encdec);

//        creating keys for sign and verify
        publicKeyRequestDto_sign= new PublicKeyRequestDto();
        publicKeyRequestDto_sign.setAlias(KEY_SIGN);
        publicKeyResponseDto_sign = localClientCryptoService.getPublicKey(publicKeyRequestDto_sign);

    }


    @Test
    public void LoadLocalServiceImplTest() {
        assertNotNull(localClientCryptoService);
        assertNotNull(publicKeyResponseDto_encdec);
        assertNotNull(publicKeyResponseDto_sign);
    }

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }



    @Test
    public void getPublicKey_test(){
//        checking public key for valid types such as endec
        PublicKeyRequestDto pub_RequestDto = new PublicKeyRequestDto();
        pub_RequestDto.setAlias(KEY_ENDEC);
        PublicKeyResponseDto pub_ResponseDto = localClientCryptoService.getPublicKey(pub_RequestDto);
        assertNotNull(pub_ResponseDto);

//        checking for sign
        PublicKeyRequestDto pub_RequestDto_sign = new PublicKeyRequestDto();
        pub_RequestDto_sign.setAlias(KEY_SIGN);
        PublicKeyResponseDto pub_ResponseDto_sign = localClientCryptoService.getPublicKey(pub_RequestDto_sign);
        assertNotNull(pub_ResponseDto_sign);

        //        checking for random string
        PublicKeyRequestDto pub_RequestDto_sign2 = new PublicKeyRequestDto();
        pub_RequestDto_sign2.setAlias("random");
        PublicKeyResponseDto pub_ResponseDto_sign2 = localClientCryptoService.getPublicKey(pub_RequestDto_sign2);
        assertNull(pub_ResponseDto_sign2);

    }

    @Test
    public void encryption_decryption_test(){

//        TESTCASE 1


        String input_message="First Message _+& 9901234 ";
//        encryption
        CryptoRequestDto cryptoRequestDto = new CryptoRequestDto(
                input_message, publicKeyResponseDto_encdec.getPublicKey());
        CryptoResponseDto cryptoResponseDto = localClientCryptoService.encrypt(cryptoRequestDto);
//        decryption
        cryptoRequestDto.setValue(cryptoResponseDto.getValue());
        cryptoResponseDto=localClientCryptoService.decrypt(cryptoRequestDto);
        assertEquals(input_message,cryptoResponseDto.getValue());

//        TESTCASE 2 (CHECKING AGAINST WRONG INPUT)
        assertNotEquals("wrong message",cryptoResponseDto.getValue());



//        TESTCASE 3(against null string)
        cryptoRequestDto.setValue("");
        cryptoResponseDto=localClientCryptoService.encrypt(cryptoRequestDto);
        cryptoRequestDto.setValue(cryptoResponseDto.getValue());
        cryptoResponseDto=localClientCryptoService.decrypt(cryptoRequestDto);
        assertEquals("",cryptoResponseDto.getValue());

    }

    @Test
    public void sign_verify_test(){
        String signMessage = "messageInput 123";
        String signed_string;

        SignRequestDto signRequestDto = new SignRequestDto(signMessage);
        SignResponseDto signResponseDto = localClientCryptoService.sign(signRequestDto);
        signed_string=signResponseDto.getData();
//        signing

//        TESTCASE 1 :CHECKING WITH CORRECT MESSAGE
        SignVerifyRequestDto signVerifyRequestDto=new SignVerifyRequestDto(signMessage, signed_string, publicKeyResponseDto_sign.getPublicKey());
        SignVerifyResponseDto signVerifyResponseDto=localClientCryptoService.verifySign(signVerifyRequestDto);
        assertTrue(signVerifyResponseDto.isVerified());

//        TESTCASE 2:WITH WRONG MESSAGE
        SignVerifyRequestDto signVerifyRequestDto_2=new SignVerifyRequestDto("DUMMY", signed_string, publicKeyResponseDto_sign.getPublicKey());
        SignVerifyResponseDto signVerifyResponseDto_2=localClientCryptoService.verifySign(signVerifyRequestDto_2);
        assertFalse(signVerifyResponseDto_2.isVerified());


    }

}