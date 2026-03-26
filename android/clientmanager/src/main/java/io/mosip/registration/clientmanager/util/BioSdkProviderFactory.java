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
import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.constant.Modality;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.spi.AuditManagerService;

/**
 * Factory for biometric SDK providers. Call {@link #initialize()} once at app startup.
 * Uses a registry keyed by modality where each entry holds both the provider instance
 * and its corresponding {@link SDKInfo}, so they can never get out of sync.
 */
@Singleton
public class BioSdkProviderFactory {

    private static final String TAG = BioSdkProviderFactory.class.getSimpleName();
    private static final String VERSION_KEY = "version";
    private static final String CLASSNAME_KEY = "classname";

    private final Context context;
    private final GlobalParamRepository globalParamRepository;
    private final AuditManagerService auditManagerService;

   
    private final Map<String, List<ProviderEntry>> registry = new HashMap<>();

    private static final class ProviderEntry {
        final IBioApiV2 provider;
        final SDKInfo sdkInfo;

        ProviderEntry(IBioApiV2 provider, SDKInfo sdkInfo) {
            this.provider = provider;
            this.sdkInfo = sdkInfo;
        }
    }

    @Inject
    public BioSdkProviderFactory(Context context, GlobalParamRepository globalParamRepository,
                                 AuditManagerService auditManagerService) {
        this.context = context;
        this.globalParamRepository = globalParamRepository;
        this.auditManagerService = auditManagerService;
    }

    /**
     * Initializes biometric providers from config. Call once at app startup.
     * Reads configuration from GlobalParamRepository, validates it, loads providers and registers them.
     */
    public void initialize() {
        if (context == null) {
            Log.w(TAG, "Context is null, cannot initialize biometric providers");
            return;
        }

        List<File> sdkFiles = BioSdkLoader.findAllSdkFiles(context);
        if (sdkFiles == null || sdkFiles.isEmpty()) {
            Log.w(TAG, "No SDK files found in assets");
            auditManagerService.audit(AuditEvent.BIO_SDK_FILES_NOT_FOUND, Components.REGISTRATION,
                    "No biometric SDK files found in assets");
            return;
        }

        Map<String, Map<String, Map<String, String>>> config =
                globalParamRepository.getBiometricProviderConfig();
        if (config == null || config.isEmpty()) {
            Log.w(TAG, "No biometric provider configuration found");
            return;
        }

        for (Map.Entry<String, Map<String, Map<String, String>>> modalityEntry : config.entrySet()) {
            String modalityKey = modalityEntry.getKey();
            if (validateModalityKey(modalityKey) == null) {
                Log.w(TAG, "Unknown modality key in config: " + modalityKey);
                continue;
            }

            Map<String, Map<String, String>> vendors = modalityEntry.getValue();
            if (vendors == null) {
                continue;
            }

            for (Map.Entry<String, Map<String, String>> vendorEntry : vendors.entrySet()) {
                Map<String, String> params = vendorEntry.getValue();
                if (params == null) {
                    continue;
                }

                String className = params.get(CLASSNAME_KEY);
                if (className == null || className.trim().isEmpty()) {
                    Log.w(TAG, "Missing classname for modality: " + modalityKey);
                    continue;
                }

                IBioApiV2 provider = BioSdkLoader.loadProvider(context, className, sdkFiles);
                if (provider == null) {
                    Log.w(TAG, "Failed to load provider for class: " + className);
                    continue;
                }

                SDKInfo sdkInfo = initProvider(provider, params, modalityKey);
                if (sdkInfo == null) {
                    continue;
                }

                String configuredVersion = params.get(VERSION_KEY);
                if (configuredVersion != null && !configuredVersion.isEmpty()
                        && !configuredVersion.equals(sdkInfo.getApiVersion())) {
                    Log.e(TAG, "SDK version mismatch for modality: " + modalityKey);
                    continue;
                }

                registry.computeIfAbsent(modalityKey, k -> new ArrayList<>())
                        .add(new ProviderEntry(provider, sdkInfo));

                String vendorName =
                        sdkInfo.getProductOwner() == null
                        ? vendorEntry.getKey() : sdkInfo.getProductOwner().getOrganization();
                auditManagerService.audit(AuditEvent.BIO_SDK_PROVIDER_REGISTERED, Components.REGISTRATION,
                        vendorName);
            }
        }
    }

    public IBioApiV2 getProviderForFunction(Modality modality, BiometricFunction function) {
        if (registry.isEmpty()) {
            initialize();
        }

        BiometricType type = Modality.modalityToBiometricType(modality);
        if (type == null) return null;

        String key = getModalityKey(modality);

        List<ProviderEntry> entries = key != null ? registry.get(key) : null;
        if (entries == null) return null;

        for (ProviderEntry entry : entries) {
            SDKInfo sdkInfo = entry.sdkInfo;
            if (sdkInfo == null || sdkInfo.getSupportedMethods() == null) continue;
            List<BiometricType> supportedTypes = sdkInfo.getSupportedMethods().get(function);
            if (supportedTypes != null && supportedTypes.contains(type)) {
                return entry.provider;
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

    private String getModalityKey(Modality modality) {
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

    private Modality validateModalityKey(String modalityKey) {
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
