package io.mosip.registration.app;

import io.mosip.registration.clientmanager.dto.crypto.*;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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
    private String encryption;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.testerView);
        serviceIntent= new Intent(getApplicationContext(), LocalClientCryptoServiceImpl.class);
        bindService();
    }


    public void click_encrypt(View view) {
        textView.setText("Clicked Encrypt");
        try{
            Thread.sleep(2000);
        } catch(InterruptedException e){
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                textView.setText("Starting encryption process");
//            creating public key request
                PublicKeyRequestDto publicKeyRequestDto = new PublicKeyRequestDto();
                PublicKeyResponseDto publicKeyResponseDto = localClientCryptoService.getPublicKey(publicKeyRequestDto);
//                PublicKeyResponseDto publicKeyResponseDto;
                try{
                    Thread.sleep(2000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                textView.setText("Got public key..creating cryptoRequest");

                CryptoRequestDto cryptoRequestDto = new CryptoRequestDto(
                        "message", publicKeyResponseDto.getPublicKey());

                CryptoResponseDto cryptoResponseDto = localClientCryptoService.encrypt(cryptoRequestDto);

                try{
                    Thread.sleep(2000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }

                textView.setText("Encryption done");
                try{
                    Thread.sleep(2000);
                }catch(InterruptedException e) {
                    e.printStackTrace();
                }
                textView.setText(cryptoResponseDto.getValue());
                encryption = cryptoResponseDto.getValue();
            }
        }).start();
    }

    public void click_decrypt(View view) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PublicKeyRequestDto publicKeyRequestDto=new PublicKeyRequestDto();
                    PublicKeyResponseDto publicKeyResponseDto=localClientCryptoService.getPublicKey(publicKeyRequestDto);
//                PublicKeyResponseDto publicKeyResponseDto;
                    textView.setText("Got public key..creating cryptoRequest for decrypt");
                    try{
                        Thread.sleep(2000);
                    }catch(InterruptedException e){
                        System.out.println(e);
                    }

                    CryptoRequestDto cryptoRequestDto = new CryptoRequestDto(encryption, publicKeyResponseDto.getPublicKey());
                    textView.setText("Decrypting......");
                    try{
                        Thread.sleep(2000);
                    }catch(InterruptedException e){
                        System.out.println(e);
                    }

                    CryptoResponseDto cryptoResponseDto=localClientCryptoService.decrypt(cryptoRequestDto);
                    System.out.println(cryptoResponseDto.getValue());
                    textView.setText("Decrypted message is : "+cryptoResponseDto.getValue() );

                    try{
                        Thread.sleep(2000);
                    }catch(InterruptedException e){
                        System.out.println(e);
                    }

                } catch(Exception ex) {
                    ex.printStackTrace();
                }


                textView.setText("Clicked Decrypt");
            }
        }).start();

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