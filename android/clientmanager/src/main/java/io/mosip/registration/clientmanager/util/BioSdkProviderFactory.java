package io.mosip.registration.clientmanager.util;

import android.content.Context;
import android.util.Log;

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
 * Factory to manage and initialize biometric SDK providers based on config properties.
 * Loads providers (finger/iris/face) from config under {@link GlobalParamRepository#BIOMETRIC_SDK_PROVIDERS_PREFIX}
 * and caches them per modality. Calls SDK init with config params and validates API version against
 * {@link SDKInfo}; stores {@link SDKInfo} for method-support checks (e.g. CAPTURE, MATCH, QUALITY_CHECK).
 * <ul>
 *   <li>If prefix does not match or config is missing: no exception at init; registry stays empty.</li>
 *   <li>Unconfigured modalities are optional; failure happens when {@link #getBioProvider(Modality)} returns null.</li>
 * </ul>
 */
@Singleton
public class BioSdkProviderFactory {

    private static final String TAG = BioSdkProviderFactory.class.getSimpleName();
    /** Param key for SDK/API version in config and init params. */
    private static final String VERSION_KEY = "version";

    private final Context context;
    private final GlobalParamRepository globalParamRepository;

    /** Modality key -> list of loaded providers (one per vendor in config order). */
    private final Map<String, List<IBioApiV2>> providerRegistry = new HashMap<>();
    /** Modality key -> list of SDKInfo (same index as providerRegistry for that key). */
    private final Map<String, List<SDKInfo>> sdkInfoRegistry = new HashMap<>();
    private volatile boolean initialized = false;

    @Inject
    public BioSdkProviderFactory(Context context, GlobalParamRepository globalParamRepository) {
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
                if (!config.containsKey(modalityKey)) {
                    continue;
                }
                Modality modality = modalityKeyToModality(modalityKey);
                if (modality == null) {
                    continue;
                }
                List<Map<String, String>> vendorParamsList = globalParamRepository != null
                        ? globalParamRepository.getModalityVendorsParamsList(modalityKey) : null;
                if (vendorParamsList == null || vendorParamsList.isEmpty()) continue;

                List<Map<String, String>> paramsWithClassname = new ArrayList<>();
                for (Map<String, String> p : vendorParamsList) {
                    String cn = p != null ? p.get("classname") : null;
                    if (cn != null && !cn.trim().isEmpty()) paramsWithClassname.add(p);
                }
                if (paramsWithClassname.isEmpty()) continue;

                List<IBioApiV2> loaded = BioSDKLoader.loadAllProvidersForModality(context, modality, globalParamRepository);
                List<IBioApiV2> providers = new ArrayList<>();
                List<SDKInfo> infos = new ArrayList<>();
                for (int i = 0; i < loaded.size() && i < paramsWithClassname.size(); i++) {
                    IBioApiV2 provider = loaded.get(i);
                    Map<String, String> vendorParams = paramsWithClassname.get(i);
                    if (provider == null || vendorParams == null) continue;
                    try {
                        SDKInfo sdkInfo = initProviderAndGetSdkInfo(provider, vendorParams, modalityKey);
                        if (sdkInfo != null) {
                            String configuredVersion = vendorParams.get(VERSION_KEY);
                            if (configuredVersion != null && !configuredVersion.isEmpty()
                                    && !configuredVersion.equals(sdkInfo.getApiVersion())) {
                                Log.e(TAG, "SDK version mismatch for modality: " + modalityKey
                                        + " configured=" + configuredVersion + " sdkInfo=" + sdkInfo.getApiVersion());
                                continue;
                            }
                            infos.add(sdkInfo);
                        } else {
                            infos.add(null);
                        }
                        providers.add(provider);
                        Log.i(TAG, "Biometric provider loaded for modality: " + modalityKey);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to init BioSDK for modality: " + modalityKey + " index: " + i, e);
                    }
                }
                if (!providers.isEmpty()) {
                    providerRegistry.put(modalityKey, providers);
                    sdkInfoRegistry.put(modalityKey, infos);
                }
            }
            initialized = true;
        }
    }

    /**
     * Returns the first biometric provider for the given modality, or null if none loaded.
     * Initializes from config on first call if not already done.
     */
    public IBioApiV2 getBioProvider(Modality modality) {
        if (!initialized) {
            initialize(context);
        }
        String key = getModalityKey(modality);
        List<IBioApiV2> list = key != null ? providerRegistry.get(key) : null;
        return (list != null && !list.isEmpty()) ? list.get(0) : null;
    }

    /**
     * Returns SDKInfo for the first loaded provider of this modality, or null if not loaded or init did not return info.
     * Use with {@link #isFunctionSupported(Modality, BiometricFunction)} to decide whether to call CAPTURE, MATCH, or QUALITY_CHECK.
     */
    public SDKInfo getSDKInfo(Modality modality) {
        if (!initialized) {
            initialize(context);
        }
        String key = getModalityKey(modality);
        List<SDKInfo> list = key != null ? sdkInfoRegistry.get(key) : null;
        return (list != null && !list.isEmpty()) ? list.get(0) : null;
    }

    /**
     * Returns true if the first loaded SDK for this modality supports the given function (e.g. QUALITY_CHECK, MATCH, CAPTURE).
     * If SDKInfo is not available, returns false so callers do not invoke unsupported methods.
     */
    public boolean isFunctionSupported(Modality modality, BiometricFunction function) {
        SDKInfo info = getSDKInfo(modality);
        if (info == null || info.getSupportedMethods() == null) return false;
        List<BiometricType> supported = info.getSupportedMethods().get(function);
        if (supported == null || supported.isEmpty()) return false;
        BiometricType type = modalityToBiometricType(modality);
        return type != null && supported.contains(type);
    }

    /**
     * Returns the first provider for this modality that supports the given function (MATCH, QUALITY_CHECK, or CAPTURE).
     * Checks only SDKs loaded for the same modality (e.g. for iris: tries iris SDK1, then iris SDK2 if SDK1 does not support the function).
     * Returns null if no SDK for this modality supports the function.
     */
    public IBioApiV2 getProviderForFunction(Modality modality, BiometricFunction function) {
        if (!initialized) {
            initialize(context);
        }
        BiometricType type = modalityToBiometricType(modality);
        if (type == null) return null;

        String key = getModalityKey(modality);
        List<IBioApiV2> providers = key != null ? providerRegistry.get(key) : null;
        List<SDKInfo> infos = key != null ? sdkInfoRegistry.get(key) : null;
        if (providers == null || infos == null) return null;

        for (int i = 0; i < providers.size(); i++) {
            SDKInfo info = i < infos.size() ? infos.get(i) : null;
            if (info == null || info.getSupportedMethods() == null) continue;
            List<BiometricType> supportedTypes = info.getSupportedMethods().get(function);
            if (supportedTypes != null && supportedTypes.contains(type)) {
                IBioApiV2 provider = providers.get(i);
                if (i > 0) {
                    Log.d(TAG, "Using alternative SDK " + (i + 1) + " for " + key + " " + function + " (same modality)");
                }
                return provider;
            }
        }
        return null;
    }

    /**
     * Returns a provider that can perform MATCH for the given modality. Same as
     * {@link #getProviderForFunction(Modality, BiometricFunction.MATCH)}.
     */
    public IBioApiV2 getProviderForMatch(Modality modality) {
        return getProviderForFunction(modality, BiometricFunction.MATCH);
    }

    /**
     * Calls provider.init(params) via reflection and returns SDKInfo if the method exists and returns non-null.
     */
    private SDKInfo initProviderAndGetSdkInfo(IBioApiV2 provider, Map<String, String> params, String modalityKey) {
        if (params == null) params = new HashMap<>();
        try {
            java.lang.reflect.Method init = provider.getClass().getMethod("init", Map.class);
            Object result = init.invoke(provider, params);
            if (result != null && result instanceof SDKInfo) {
                return (SDKInfo) result;
            }
        } catch (NoSuchMethodException e) {
            Log.d(TAG, "init(Map) not found on provider for " + modalityKey + ", SDKInfo will be null");
        } catch (Exception e) {
            Log.e(TAG, "SDK init failed for modality: " + modalityKey, e);
        }
        return null;
    }

    private static BiometricType modalityToBiometricType(Modality modality) {
        if (modality == null) return null;
        switch (modality) {
            case FINGERPRINT_SLAB_LEFT:
            case FINGERPRINT_SLAB_RIGHT:
            case FINGERPRINT_SLAB_THUMBS:
                return BiometricType.FINGER;
            case IRIS_DOUBLE:
                return BiometricType.IRIS;
            case FACE:
            case EXCEPTION_PHOTO:
                return BiometricType.FACE;
            default:
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
