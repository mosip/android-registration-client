package io.mosip.registration.clientmanager.factory;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.ListenableWorker;
import androidx.work.WorkerFactory;
import androidx.work.WorkerParameters;
import io.mosip.registration.clientmanager.worker.RestWorker;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ClientWorkerFactory extends WorkerFactory {

    private static final String TAG = ClientWorkerFactory.class.getSimpleName();

    private ClientCryptoManagerService clientCryptoManagerService;


    @Inject
    public ClientWorkerFactory(ClientCryptoManagerService clientCryptoManagerService) {
        this.clientCryptoManagerService = clientCryptoManagerService;
    }


    @Nullable
    @Override
    public ListenableWorker createWorker(@NonNull Context appContext, @NonNull String workerClassName,
                                         @NonNull WorkerParameters workerParameters) {
        Log.i(TAG, "create worker invoked for worker : " + workerClassName);
        return new RestWorker(appContext, workerParameters, this.clientCryptoManagerService);
    }
}
