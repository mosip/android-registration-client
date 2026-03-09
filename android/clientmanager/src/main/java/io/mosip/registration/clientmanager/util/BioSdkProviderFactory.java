package io.mosip.registration.clientmanager.util;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.kernel.biometrics.constant.BiometricFunction;
import io.mosip.kernel.biometrics.constant.BiometricType;
import io.mosip.kernel.biometrics.model.SDKInfo;
import io.mosip.kernel.biometrics.spi.IBioApiV2;
import io.mosip.registration.clientmanager.constant.Modality;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;

/**
 * Factory for biometric SDK providers. Call {@link #initialize()} once at app startup.
 */
@Singleton
public class BioSdkProviderFactory {

    private static final String TAG = BioSdkProviderFactory.class.getSimpleName();
    private static final String VERSION_KEY = "version";
    private static final String CLASSNAME_KEY = "classname";

    private final Context context;
    private final GlobalParamRepository globalParamRepository;

    private final Map<String, List<IBioApiV2>> providerRegistry = new HashMap<>();
    private final Map<String, List<SDKInfo>> sdkInfoRegistry = new HashMap<>();

    @Inject
    public BioSdkProviderFactory(Context context, GlobalParamRepository globalParamRepository) {
        this.context = context;
        this.globalParamRepository = globalParamRepository;
    }

    /**
     * Initializes biometric providers from config. Call once at app startup.
     * Single pass via repository callback — no intermediate list; load + init + register per vendor inline.
     */
    public void initialize() {
        if (context == null) {
            Log.w(TAG, "Context is null, cannot initialize biometric providers");
            return;
        }

        List<File> sdkFiles = BioSdkLoader.findAllSdkFiles(context);
        if (sdkFiles == null || sdkFiles.isEmpty()) {
            Log.w(TAG, "No SDK files found in assets");
            return;
        }

        globalParamRepository.forEachVendorParams((modalityKey, params) -> {
            if (parseModalityKey(modalityKey) == null) return;

            String className = params.get(CLASSNAME_KEY);
            if (className == null || className.trim().isEmpty()) return;

            IBioApiV2 provider = BioSdkLoader.loadProvider(context, className, sdkFiles);
            if (provider == null) return;

            SDKInfo sdkInfo = initProvider(provider, params, modalityKey);
            if (sdkInfo == null) return;

            String configuredVersion = params.get(VERSION_KEY);
            if (configuredVersion != null && !configuredVersion.isEmpty()
                    && !configuredVersion.equals(sdkInfo.getApiVersion())) {
                Log.e(TAG, "SDK version mismatch for modality: " + modalityKey);
                return;
            }

            providerRegistry.computeIfAbsent(modalityKey, k -> new ArrayList<>()).add(provider);
            sdkInfoRegistry.computeIfAbsent(modalityKey, k -> new ArrayList<>()).add(sdkInfo);
            Log.i(TAG, "Provider registered — modality: " + modalityKey + ", class: " + className);
        });
    }

    public IBioApiV2 getProviderForFunction(Modality modality, BiometricFunction function) {
        if (providerRegistry.isEmpty()) {
            initialize();
        }

        BiometricType type = Modality.modalityToBiometricType(modality);
        if (type == null) return null;

        String key = getModalityKey(modality);

        List<IBioApiV2> providers = key != null ? providerRegistry.get(key) : null;
        List<SDKInfo> sdkInfoList = key != null ? sdkInfoRegistry.get(key) : null;

        if (providers == null || sdkInfoList == null) return null;

        for (int i = 0; i < providers.size(); i++) {
            SDKInfo sdkInfo = i < sdkInfoList.size() ? sdkInfoList.get(i) : null;
            if (sdkInfo == null || sdkInfo.getSupportedMethods() == null) continue;
            List<BiometricType> supportedTypes = sdkInfo.getSupportedMethods().get(function);
            if (supportedTypes != null && supportedTypes.contains(type)) {
                return providers.get(i);
            }
        }
        return null;
    }

    public IBioApiV2 getProviderForMatch(Modality modality) {
        return getProviderForFunction(modality, BiometricFunction.MATCH);
    }

    private SDKInfo initProvider(IBioApiV2 provider, Map<String, String> params, String modalityKey) {
        try {
            return provider.init(params);
        } catch (Exception e) {
            Log.e(TAG, "SDK init failed for modality: " + modalityKey, e);
            return null;
        }
    }

    private static String getModalityKey(Modality modality) {
        if (modality == null) return null;
        switch (modality) {
            case FINGERPRINT_SLAB_LEFT:
            case FINGERPRINT_SLAB_RIGHT:
            case FINGERPRINT_SLAB_THUMBS:
                return "finger";
            case IRIS_DOUBLE:
                return "iris";
            case FACE:
            case EXCEPTION_PHOTO:
                return "face";
            default:
                return null;
        }
    }

    private static Modality parseModalityKey(String modalityKey) {
        if (modalityKey == null) return null;
        switch (modalityKey) {
            case "finger":
                return Modality.FINGERPRINT_SLAB_LEFT;
            case "iris":
                return Modality.IRIS_DOUBLE;
            case "face":
                return Modality.FACE;
            default:
                return null;
        }
    }
}
