package io.mosip.registration.app;

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
    }

    public void click_decrypt(View view) {
        textView.setText("Clicked Decrypt");
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