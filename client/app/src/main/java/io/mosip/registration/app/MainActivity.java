package io.mosip.registration.app;

import io.mosip.registration.clientmanager.dto.crypto.*;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import io.mosip.registration.clientmanager.service.crypto.LocalClientCryptoServiceImpl;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private LocalClientCryptoServiceImpl localClientCryptoService;
    private boolean isServiceBound;


    private Intent serviceIntent;
    private ServiceConnection serviceConnection;
    private boolean mStopLoop;

    TextView textView;

    private String endecMessage = "encode dummy text";
    private String signMessage = "sign dummy text";
    private String encryption;
    private String signed_string;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.testerView);
        serviceIntent= new Intent(getApplicationContext(), LocalClientCryptoServiceImpl.class);
        bindService();
    }
    public void click_encrypt(View view) {
        test_sign(view);
    };
    public void click_decrypt(View view) {
        test_verify(view);
    };

    public void test_encrypt(View view) {
        try {
            Log.i(TAG, "test_encrypt: Encrypting...." + endecMessage);
            //            creating public key request
            PublicKeyRequestDto publicKeyRequestDto = new PublicKeyRequestDto();
            publicKeyRequestDto.setServerProfile("endec");

            PublicKeyResponseDto publicKeyResponseDto = localClientCryptoService.getPublicKey(publicKeyRequestDto);
            Log.i(TAG,"Got public key..creating cryptoRequest");

            CryptoRequestDto cryptoRequestDto = new CryptoRequestDto(
                    endecMessage, publicKeyResponseDto.getPublicKey());

            CryptoResponseDto cryptoResponseDto = localClientCryptoService.encrypt(cryptoRequestDto);

            Log.i(TAG, "test_encrypt: Encryption completed");

            textView.setText("Encrypted message is : " + cryptoResponseDto.getValue() );
            encryption = cryptoResponseDto.getValue();
        } catch (Exception e) {
            Log.e(TAG, "test_encrypt: Encryption Failed ", e);
        }
    }

    public void test_decrypt(View view) {
        try {
            Log.i(TAG, "test_decrypt: Decrypting....");
            PublicKeyRequestDto publicKeyRequestDto = new PublicKeyRequestDto();
            publicKeyRequestDto.setServerProfile("endec");
            PublicKeyResponseDto publicKeyResponseDto = localClientCryptoService.getPublicKey(publicKeyRequestDto);

            CryptoRequestDto cryptoRequestDto = new CryptoRequestDto(encryption, publicKeyResponseDto.getPublicKey());
            Log.i(TAG,"Got public key..creating cryptoRequest");


            CryptoResponseDto cryptoResponseDto=localClientCryptoService.decrypt(cryptoRequestDto);
            Log.i(TAG, "test_decrypt: Decryption Completed");

            textView.setText("Decrypted message is : " + cryptoResponseDto.getValue() );


        } catch(Exception e) {
            Log.e(TAG, "test_decrypt: Decryption Failed ", e);
        }

    }

    public void test_sign(View view){
        try {
            Log.i(TAG, "test_sign: Signing...." + signMessage);

            Log.i(TAG, "test_sign: Creating Sign Request ");
            SignRequestDto signRequestDto = new SignRequestDto(signMessage);

            SignResponseDto signResponseDto = localClientCryptoService.sign(signRequestDto);
            signed_string=signResponseDto.getData();

            Log.i(TAG, "test_sign: Signing completed");
            textView.setText("Signature is: "+ signed_string);

        }
        catch (Exception e) {
            Log.e(TAG, "test_sign: Signing Failed", e);
        }
    }

    public void test_verify(View view){
        try{
            Log.i(TAG, "test_verify: SignVerifying....");
            PublicKeyRequestDto publicKeyRequestDto = new PublicKeyRequestDto();
            publicKeyRequestDto.setServerProfile("sign");
            PublicKeyResponseDto publicKeyResponseDto = localClientCryptoService.getPublicKey(publicKeyRequestDto);
            Log.i(TAG, "test_verify: Got public key..verifying signed data");

            SignVerifyRequestDto signVerifyRequestDto=new SignVerifyRequestDto(signMessage, signed_string, publicKeyResponseDto.getPublicKey());
            SignVerifyResponseDto signVerifyResponseDto=localClientCryptoService.verifySign(signVerifyRequestDto);

            Log.i(TAG, "test_verify: Verification Completed");

            if(signVerifyResponseDto.isVerified()){
                textView.setText("Data is correctly signed and matching");
            }
            else{
                textView.setText("Incorrect Signature");
            }

        }catch(Exception e){
            Log.e(TAG, "test_verify: SignVerification Failed ", e);
        }

    }


    private void bindService(){
        if(serviceConnection==null){
            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName className,
                                               IBinder service) {
                    // Binding to LocalClientCryptoServiceImpl, cast the IBinder and
                    // getting LocalClientCryptoServiceImpl instance
                    LocalClientCryptoServiceImpl.ClientCryptoServiceBinder binder =
                            (LocalClientCryptoServiceImpl.ClientCryptoServiceBinder) service;
                    //Get instance of your service
                    localClientCryptoService = binder.getServiceInstance();
                    localClientCryptoService.initLocalClientCryptoService();
                    isServiceBound = true;
                }

                @Override
                public void onServiceDisconnected(ComponentName className) {
                    isServiceBound = false;
                }
            };
        }

        bindService(serviceIntent,serviceConnection, Context.BIND_AUTO_CREATE);

    }

    private void unbindService(){
        if(isServiceBound){
            unbindService(serviceConnection);
            isServiceBound=false;
        }
    }

}