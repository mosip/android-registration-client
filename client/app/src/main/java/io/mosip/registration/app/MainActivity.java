package io.mosip.registration.app;

import static io.mosip.registration.clientmanager.constant.KeyManagerConstant.KEY_ENDEC;
import static io.mosip.registration.clientmanager.constant.KeyManagerConstant.KEY_SIGN;

import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.registration.clientmanager.dto.crypto.*;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;

import javax.inject.Inject;

import io.mosip.registration.clientmanager.service.crypto.LocalClientCryptoServiceImpl;
import io.mosip.registration.clientmanager.util.RestService;

public class MainActivity extends DaggerAppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    public LocalClientCryptoServiceImpl localClientCryptoService;


    EditText messageInput;
    TextView endecTextView;
    TextView signTextView;

    private String endecMessage;
    private String signMessage;
    private String encryption;
    private String signed_string;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messageInput = (EditText) findViewById(R.id.msg_input);
        endecTextView = (TextView) findViewById(R.id.EnDecTextView);
        signTextView = (TextView) findViewById(R.id.SignTextView);
    }
    public void click_encrypt(View view) {
        test_encrypt(view);
    };
    public void click_decrypt(View view) { test_decrypt(view); };

    public void click_sign(View view) {
        test_sign(view);
    };
    public void click_verify(View view) { test_verify(view); };


    public void test_encrypt(View view) {
        try {
            endecMessage = messageInput.getText().toString();
            Log.i(TAG, "test_encrypt: Encrypting...." + endecMessage);
            //            creating public key request
            PublicKeyRequestDto publicKeyRequestDto = new PublicKeyRequestDto();
            publicKeyRequestDto.setAlias(KEY_ENDEC);

            PublicKeyResponseDto publicKeyResponseDto = localClientCryptoService.getPublicKey(publicKeyRequestDto);
            Log.i(TAG,"Got public key..creating cryptoRequest");

            CryptoRequestDto cryptoRequestDto = new CryptoRequestDto(
                    endecMessage, publicKeyResponseDto.getPublicKey());

            CryptoResponseDto cryptoResponseDto = localClientCryptoService.encrypt(cryptoRequestDto);

            Log.i(TAG, "test_encrypt: Encryption completed");

            endecTextView.setText("Encrypted message is : " + cryptoResponseDto.getValue() );
            encryption = cryptoResponseDto.getValue();
        } catch (Exception e) {
            Log.e(TAG, "test_encrypt: Encryption Failed ", e);
        }
    }

    public void test_decrypt(View view) {
        try {
            Log.i(TAG, "test_decrypt: Decrypting....");
            PublicKeyRequestDto publicKeyRequestDto = new PublicKeyRequestDto();
            publicKeyRequestDto.setAlias(KEY_ENDEC);
            PublicKeyResponseDto publicKeyResponseDto = localClientCryptoService.getPublicKey(publicKeyRequestDto);

            CryptoRequestDto cryptoRequestDto = new CryptoRequestDto(encryption, publicKeyResponseDto.getPublicKey());
            Log.i(TAG,"Got public key..creating cryptoRequest");


            CryptoResponseDto cryptoResponseDto=localClientCryptoService.decrypt(cryptoRequestDto);
            Log.i(TAG, "test_decrypt: Decryption Completed");

            endecTextView.setText("Decrypted message is : " + cryptoResponseDto.getValue() );


        } catch(Exception e) {
            Log.e(TAG, "test_decrypt: Decryption Failed ", e);
        }

    }

    public void test_sign(View view){
        try {
            signMessage = messageInput.getText().toString();
            Log.i(TAG, "test_sign: Signing...." + signMessage);

            Log.i(TAG, "test_sign: Creating Sign Request ");
            SignRequestDto signRequestDto = new SignRequestDto(signMessage);

            SignResponseDto signResponseDto = localClientCryptoService.sign(signRequestDto);
            signed_string=signResponseDto.getData();

            Log.i(TAG, "test_sign: Signing completed");
            signTextView.setText("Signature is: "+ signed_string);

        }
        catch (Exception e) {
            Log.e(TAG, "test_sign: Signing Failed", e);
        }
    }

    public void test_verify(View view){
        try{
            Log.i(TAG, "test_verify: SignVerifying....");
            PublicKeyRequestDto publicKeyRequestDto = new PublicKeyRequestDto();
            publicKeyRequestDto.setAlias(KEY_SIGN);
            PublicKeyResponseDto publicKeyResponseDto = localClientCryptoService.getPublicKey(publicKeyRequestDto);
            Log.i(TAG, "test_verify: Got public key..verifying signed data");

            SignVerifyRequestDto signVerifyRequestDto=new SignVerifyRequestDto(signMessage, signed_string, publicKeyResponseDto.getPublicKey());
            SignVerifyResponseDto signVerifyResponseDto=localClientCryptoService.verifySign(signVerifyRequestDto);

            Log.i(TAG, "test_verify: Verification Completed");

            if(signVerifyResponseDto.isVerified()){
                signTextView.setText("Data is correctly signed and matching");
            }
            else{
                signTextView.setText("Incorrect Signature");
            }

        }catch(Exception e){
            Log.e(TAG, "test_verify: SignVerification Failed ", e);
        }
    }
}