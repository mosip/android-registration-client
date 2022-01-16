package io.mosip.registration.app;

import static io.mosip.registration.keymanager.util.KeyManagerConstant.KEY_ENDEC;
import static io.mosip.registration.keymanager.util.KeyManagerConstant.KEY_SIGN;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.registration.keymanager.dto.CryptoRequestDto;
import io.mosip.registration.keymanager.dto.CryptoResponseDto;
import io.mosip.registration.keymanager.dto.PublicKeyRequestDto;
import io.mosip.registration.keymanager.dto.PublicKeyResponseDto;
import io.mosip.registration.keymanager.dto.SignRequestDto;
import io.mosip.registration.keymanager.dto.SignResponseDto;
import io.mosip.registration.keymanager.dto.SignVerifyRequestDto;
import io.mosip.registration.keymanager.dto.SignVerifyResponseDto;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;


public class MainActivity extends DaggerAppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    public ClientCryptoManagerService clientCryptoManagerService;


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

    public void click_objectStoreDemo(View view) {
        Intent intent = new Intent(this, PosixAdapterDemo.class);
        startActivity(intent);
    }

    public void click_PacketWriterDemo(View view) {
        Intent intent = new Intent(this, PacketWriterDemo.class);
        startActivity(intent);
    }

    public void test_encrypt(View view) {
        try {
            endecMessage = messageInput.getText().toString();
            Log.i(TAG, "test_encrypt: Encrypting...." + endecMessage);
            //            creating public key request
            PublicKeyRequestDto publicKeyRequestDto = new PublicKeyRequestDto();
            publicKeyRequestDto.setAlias(KEY_ENDEC);

            PublicKeyResponseDto publicKeyResponseDto = clientCryptoManagerService.getPublicKey(publicKeyRequestDto);
            Log.i(TAG,"Got public key..creating cryptoRequest");

            CryptoRequestDto cryptoRequestDto = new CryptoRequestDto(
                    endecMessage, publicKeyResponseDto.getPublicKey());

            CryptoResponseDto cryptoResponseDto = clientCryptoManagerService.encrypt(cryptoRequestDto);

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
            PublicKeyResponseDto publicKeyResponseDto = clientCryptoManagerService.getPublicKey(publicKeyRequestDto);

            CryptoRequestDto cryptoRequestDto = new CryptoRequestDto(encryption, publicKeyResponseDto.getPublicKey());
            Log.i(TAG,"Got public key..creating cryptoRequest");


            CryptoResponseDto cryptoResponseDto=clientCryptoManagerService.decrypt(cryptoRequestDto);
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

            SignResponseDto signResponseDto = clientCryptoManagerService.sign(signRequestDto);
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
            PublicKeyResponseDto publicKeyResponseDto = clientCryptoManagerService.getPublicKey(publicKeyRequestDto);
            Log.i(TAG, "test_verify: Got public key..verifying signed data");

            SignVerifyRequestDto signVerifyRequestDto=new SignVerifyRequestDto(signMessage, signed_string, publicKeyResponseDto.getPublicKey());
            SignVerifyResponseDto signVerifyResponseDto=clientCryptoManagerService.verifySign(signVerifyRequestDto);

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