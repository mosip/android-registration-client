package io.mosip.registration.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;

import io.mosip.registration.clientmanager.service.crypto.LocalClientCryptoServiceImpl;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private LocalClientCryptoServiceImpl localClientCryptoService;
    private boolean isServiceBound;


    private Intent serviceIntent;
    private ServiceConnection serviceConnection;
    private boolean mStopLoop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        serviceIntent= new Intent(getApplicationContext(), LocalClientCryptoServiceImpl.class);
        bindService();
    }


    public void click_start(View view) {
        switch (view.getId()) {


        }
        openStart();
    }

    public void openStart() {
        OpenDialog od1 = new OpenDialog();
        od1.show(getSupportFragmentManager(),"od1");
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
                    isServiceBound = true;
                }

                @Override
                public void onServiceDisconnected(ComponentName arg0) {
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