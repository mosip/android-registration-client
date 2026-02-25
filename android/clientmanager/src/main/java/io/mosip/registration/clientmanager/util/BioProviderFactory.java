package io.mosip.registration.clientmanager.util;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.kernel.biometrics.spi.IBioApiV2;
import io.mosip.registration.clientmanager.constant.Modality;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;

/**
 * Factory to manage and initialize biometric SDK providers based on config properties.
 * Loads providers (finger/iris/face) from config under {@link GlobalParamRepository#BIOMETRIC_SDK_PROVIDERS_PREFIX}
 * and caches them per modality.
 * <ul>
 *   <li>If prefix does not match or config is missing: no exception at init; registry stays empty.</li>
 *   <li>Unconfigured modalities are optional; failure happens when {@link #getBioProvider(Modality)} returns null.</li>
 * </ul>
 */
@Singleton
public class BioProviderFactory {

    private static final String TAG = BioProviderFactory.class.getSimpleName();

    private final Context context;
    private final GlobalParamRepository globalParamRepository;

    private final Map<String, IBioApiV2> providerRegistry = new HashMap<>();
    private volatile boolean initialized = false;

    @Inject
    public BioProviderFactory(Context context, GlobalParamRepository globalParamRepository) {
        this.context = context;
        this.globalParamRepository = globalParamRepository;
    }

    /**
     * Initializes biometric providers from config (mosip.biometric.sdk.providers). Loads SDK once per
     * configured modality (finger, iris, face) and caches the instance.
     */
    public void initialize(Context context) {
        if (context == null) {
            context = this.context;
        }
        if (context == null) {
            Log.w(TAG, "Context is null, cannot initialize biometric providers");
            return;
        }
        synchronized (providerRegistry) {
            if (initialized) {
                return;
            }
            Map<String, Map<String, Map<String, String>>> config =
                    globalParamRepository != null ? globalParamRepository.getBiometricProviderConfig() : null;
            if (config == null || config.isEmpty()) {
                initialized = true;
                return;
            }
            for (String modalityKey : new String[]{"finger", "iris", "face"}) {
                if (!config.containsKey(modalityKey) || providerRegistry.containsKey(modalityKey)) {
                    continue;
                }
                Modality modality = modalityKeyToModality(modalityKey);
                if (modality == null) {
                    continue;
                }
                try {
                    IBioApiV2 provider = BioSDKLoader.loadBioSDK(context, modality, globalParamRepository);
                    if (provider != null) {
                        providerRegistry.put(modalityKey, provider);
                        Log.i(TAG, "Biometric provider loaded for modality: " + modalityKey);
                    } else {
                        Log.w(TAG, "BioSDKLoader returned null for modality: " + modalityKey);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Failed to load BioSDK for modality: " + modalityKey, e);
                }
            }
            initialized = true;
        }
    }

    /**
     * Returns the biometric provider for the given modality, or null if not configured or load failed.
     * Initializes from config on first call if not already done.
     */
    public IBioApiV2 getBioProvider(Modality modality) {
        if (!initialized) {
            initialize(context);
        }
        String key = getModalityKey(modality);
        return key != null ? providerRegistry.get(key) : null;
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

    private static Modality modalityKeyToModality(String modalityKey) {
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
