package io.mosip.registration.clientmanager.util;

import android.content.Context;
import io.mosip.kernel.biometrics.constant.BiometricFunction;
import io.mosip.kernel.biometrics.constant.BiometricType;
import io.mosip.kernel.biometrics.entities.RegistryIDType;
import io.mosip.kernel.biometrics.model.SDKInfo;
import io.mosip.kernel.biometrics.spi.IBioApiV2;
import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.constant.Modality;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BioSdkProviderFactoryTest {

    @Mock private Context mockContext;
    @Mock private GlobalParamRepository globalParamRepository;
    @Mock private AuditManagerService auditManagerService;
    @Mock private IBioApiV2 mockProvider;
    @Mock private SDKInfo mockSdkInfo;

    private BioSdkProviderFactory factory;
    private MockedStatic<BioSdkLoader> bioSdkLoaderMock;

    private static final List<File> ONE_SDK_FILE = Collections.singletonList(new File("test.dex"));

    @Before
    public void setUp() {
        bioSdkLoaderMock = Mockito.mockStatic(BioSdkLoader.class);
        factory = new BioSdkProviderFactory(mockContext, globalParamRepository, auditManagerService);
    }

    @After
    public void tearDown() {
        if (bioSdkLoaderMock != null) bioSdkLoaderMock.close();
    }

    // ───────────── initialize() — SDK file checks ─────────────

    @Test
    public void testInitialize_emptySDKFiles_auditsNotFound() {
        bioSdkLoaderMock.when(() -> BioSdkLoader.findAllSdkFiles(mockContext))
                .thenReturn(Collections.emptyList());

        factory.initialize();

        verify(auditManagerService).audit(
                eq(AuditEvent.BIO_SDK_FILES_NOT_FOUND), eq(Components.REGISTRATION), anyString());
    }

    @Test
    public void testInitialize_nullSdkFiles_auditsNotFound() {
        bioSdkLoaderMock.when(() -> BioSdkLoader.findAllSdkFiles(mockContext))
                .thenReturn(null);

        factory.initialize();

        verify(auditManagerService).audit(
                eq(AuditEvent.BIO_SDK_FILES_NOT_FOUND), eq(Components.REGISTRATION), anyString());
    }

    // ───────────── initialize() — config checks ─────────────

    @Test
    public void testInitialize_nullConfig_noRegistration() {
        bioSdkLoaderMock.when(() -> BioSdkLoader.findAllSdkFiles(mockContext)).thenReturn(ONE_SDK_FILE);
        when(globalParamRepository.getBiometricProviderConfig()).thenReturn(null);

        factory.initialize();

        verifyNoRegistrationAudit();
    }

    @Test
    public void testInitialize_emptyConfig_noRegistration() {
        bioSdkLoaderMock.when(() -> BioSdkLoader.findAllSdkFiles(mockContext)).thenReturn(ONE_SDK_FILE);
        when(globalParamRepository.getBiometricProviderConfig()).thenReturn(Collections.emptyMap());

        factory.initialize();

        verifyNoRegistrationAudit();
    }

    @Test
    public void testInitialize_unknownModalityKey_skipsEntry() {
        bioSdkLoaderMock.when(() -> BioSdkLoader.findAllSdkFiles(mockContext)).thenReturn(ONE_SDK_FILE);
        when(globalParamRepository.getBiometricProviderConfig())
                .thenReturn(configWith("unknown_modality", "classname", "com.example.Sdk"));

        factory.initialize();

        verifyNoRegistrationAudit();
    }

    @Test
    public void testInitialize_nullModalityKey_skipsEntry() {
        bioSdkLoaderMock.when(() -> BioSdkLoader.findAllSdkFiles(mockContext)).thenReturn(ONE_SDK_FILE);
        Map<String, Map<String, Map<String, String>>> config = new HashMap<>();
        config.put(null, vendorsWith("classname", "com.example.Sdk")); // null key
        when(globalParamRepository.getBiometricProviderConfig()).thenReturn(config);

        factory.initialize();

        verifyNoRegistrationAudit();
    }

    @Test
    public void testInitialize_nullVendorsMap_skipsModality() {
        bioSdkLoaderMock.when(() -> BioSdkLoader.findAllSdkFiles(mockContext)).thenReturn(ONE_SDK_FILE);
        Map<String, Map<String, Map<String, String>>> config = new HashMap<>();
        config.put("face", null);
        when(globalParamRepository.getBiometricProviderConfig()).thenReturn(config);

        factory.initialize();

        verifyNoRegistrationAudit();
    }

    @Test
    public void testInitialize_nullParams_skipsVendor() {
        bioSdkLoaderMock.when(() -> BioSdkLoader.findAllSdkFiles(mockContext)).thenReturn(ONE_SDK_FILE);
        Map<String, Map<String, Map<String, String>>> config = new HashMap<>();
        Map<String, Map<String, String>> vendors = new HashMap<>();
        vendors.put("vendor1", null); // null params
        config.put("face", vendors);
        when(globalParamRepository.getBiometricProviderConfig()).thenReturn(config);

        factory.initialize();

        verifyNoRegistrationAudit();
    }

    @Test
    public void testInitialize_missingClassname_skipsVendor() {
        bioSdkLoaderMock.when(() -> BioSdkLoader.findAllSdkFiles(mockContext)).thenReturn(ONE_SDK_FILE);
        when(globalParamRepository.getBiometricProviderConfig())
                .thenReturn(configWith("face", "other_param", "value")); // no "classname" key

        factory.initialize();

        verifyNoRegistrationAudit();
    }

    @Test
    public void testInitialize_emptyClassname_skipsVendor() {
        bioSdkLoaderMock.when(() -> BioSdkLoader.findAllSdkFiles(mockContext)).thenReturn(ONE_SDK_FILE);
        when(globalParamRepository.getBiometricProviderConfig())
                .thenReturn(configWith("face", "classname", "")); // empty classname

        factory.initialize();

        verifyNoRegistrationAudit();
    }

    @Test
    public void testInitialize_blankClassname_skipsVendor() {
        bioSdkLoaderMock.when(() -> BioSdkLoader.findAllSdkFiles(mockContext)).thenReturn(ONE_SDK_FILE);
        when(globalParamRepository.getBiometricProviderConfig())
                .thenReturn(configWith("face", "classname", "   ")); // blank classname

        factory.initialize();

        verifyNoRegistrationAudit();
    }

    @Test
    public void testInitialize_providerLoadFails_skipsVendor() {
        bioSdkLoaderMock.when(() -> BioSdkLoader.findAllSdkFiles(mockContext)).thenReturn(ONE_SDK_FILE);
        bioSdkLoaderMock.when(() -> BioSdkLoader.loadProvider(eq(mockContext), anyString(), anyList()))
                .thenReturn(null);
        when(globalParamRepository.getBiometricProviderConfig())
                .thenReturn(configWith("face", "classname", "com.example.Sdk"));

        factory.initialize();

        verifyNoRegistrationAudit();
    }

    @Test
    public void testInitialize_initProviderThrowsException_skipsVendor() {
        bioSdkLoaderMock.when(() -> BioSdkLoader.findAllSdkFiles(mockContext)).thenReturn(ONE_SDK_FILE);
        bioSdkLoaderMock.when(() -> BioSdkLoader.loadProvider(eq(mockContext), anyString(), anyList()))
                .thenReturn(mockProvider);
        when(mockProvider.init(any())).thenThrow(new RuntimeException("SDK init crashed"));
        when(globalParamRepository.getBiometricProviderConfig())
                .thenReturn(configWith("face", "classname", "com.example.Sdk"));

        factory.initialize();

        verifyNoRegistrationAudit();
    }

    @Test
    public void testInitialize_initReturnsNull_skipsVendor() {
        bioSdkLoaderMock.when(() -> BioSdkLoader.findAllSdkFiles(mockContext)).thenReturn(ONE_SDK_FILE);
        bioSdkLoaderMock.when(() -> BioSdkLoader.loadProvider(eq(mockContext), anyString(), anyList()))
                .thenReturn(mockProvider);
        when(mockProvider.init(any())).thenReturn(null); // sdkInfo is null
        when(globalParamRepository.getBiometricProviderConfig())
                .thenReturn(configWith("face", "classname", "com.example.Sdk"));

        factory.initialize();

        verifyNoRegistrationAudit();
    }

    // ───────────── initialize() — version check ─────────────

    @Test
    public void testInitialize_versionMismatch_skipsVendor() {
        bioSdkLoaderMock.when(() -> BioSdkLoader.findAllSdkFiles(mockContext)).thenReturn(ONE_SDK_FILE);
        bioSdkLoaderMock.when(() -> BioSdkLoader.loadProvider(eq(mockContext), anyString(), anyList()))
                .thenReturn(mockProvider);
        when(mockProvider.init(any())).thenReturn(mockSdkInfo);
        when(mockSdkInfo.getApiVersion()).thenReturn("1.0"); // SDK reports 1.0

        Map<String, String> params = new HashMap<>();
        params.put("classname", "com.example.Sdk");
        params.put("version", "2.0"); // configured expects 2.0 — mismatch
        when(globalParamRepository.getBiometricProviderConfig())
                .thenReturn(configWithParams("face", "vendor1", params));

        factory.initialize();

        verifyNoRegistrationAudit();
    }

    // ───────────── initialize() — successful registration ─────────────

    @Test
    public void testInitialize_successfulRegistration_noVersionInConfig_auditsWithVendorKey() {
        bioSdkLoaderMock.when(() -> BioSdkLoader.findAllSdkFiles(mockContext)).thenReturn(ONE_SDK_FILE);
        bioSdkLoaderMock.when(() -> BioSdkLoader.loadProvider(eq(mockContext), anyString(), anyList()))
                .thenReturn(mockProvider);
        when(mockProvider.init(any())).thenReturn(mockSdkInfo);
        when(mockSdkInfo.getProductOwner()).thenReturn(null); // null → use vendor key

        when(globalParamRepository.getBiometricProviderConfig())
                .thenReturn(configWith("face", "classname", "com.example.Sdk"));

        factory.initialize();

        verify(auditManagerService).audit(
                eq(AuditEvent.BIO_SDK_PROVIDER_REGISTERED), eq(Components.REGISTRATION), eq("vendor1"));
    }

    @Test
    public void testInitialize_successfulRegistration_matchingVersion_registersProvider() {
        bioSdkLoaderMock.when(() -> BioSdkLoader.findAllSdkFiles(mockContext)).thenReturn(ONE_SDK_FILE);
        bioSdkLoaderMock.when(() -> BioSdkLoader.loadProvider(eq(mockContext), anyString(), anyList()))
                .thenReturn(mockProvider);
        when(mockProvider.init(any())).thenReturn(mockSdkInfo);
        when(mockSdkInfo.getApiVersion()).thenReturn("2.0");
        when(mockSdkInfo.getProductOwner()).thenReturn(null);

        Map<String, String> params = new HashMap<>();
        params.put("classname", "com.example.Sdk");
        params.put("version", "2.0"); // matching version
        when(globalParamRepository.getBiometricProviderConfig())
                .thenReturn(configWithParams("face", "vendor1", params));

        factory.initialize();

        verify(auditManagerService).audit(
                eq(AuditEvent.BIO_SDK_PROVIDER_REGISTERED), eq(Components.REGISTRATION), anyString());
    }

    @Test
    public void testInitialize_successfulRegistration_withProductOwner_auditsWithOrganization() {
        RegistryIDType productOwner = new RegistryIDType();
        productOwner.setOrganization("TestVendorOrg");

        bioSdkLoaderMock.when(() -> BioSdkLoader.findAllSdkFiles(mockContext)).thenReturn(ONE_SDK_FILE);
        bioSdkLoaderMock.when(() -> BioSdkLoader.loadProvider(eq(mockContext), anyString(), anyList()))
                .thenReturn(mockProvider);
        when(mockProvider.init(any())).thenReturn(mockSdkInfo);
        when(mockSdkInfo.getProductOwner()).thenReturn(productOwner);

        when(globalParamRepository.getBiometricProviderConfig())
                .thenReturn(configWith("face", "classname", "com.example.Sdk"));

        factory.initialize();

        verify(auditManagerService).audit(
                eq(AuditEvent.BIO_SDK_PROVIDER_REGISTERED), eq(Components.REGISTRATION), eq("TestVendorOrg"));
    }

    @Test
    public void testInitialize_multipleModalities_allSucceed_registersEach() {
        bioSdkLoaderMock.when(() -> BioSdkLoader.findAllSdkFiles(mockContext)).thenReturn(ONE_SDK_FILE);
        bioSdkLoaderMock.when(() -> BioSdkLoader.loadProvider(eq(mockContext), anyString(), anyList()))
                .thenReturn(mockProvider);
        when(mockProvider.init(any())).thenReturn(mockSdkInfo);
        when(mockSdkInfo.getProductOwner()).thenReturn(null);

        Map<String, Map<String, Map<String, String>>> config = new HashMap<>();
        config.put("face", vendorsWith("classname", "com.example.FaceSdk"));
        config.put("finger", vendorsWith("classname", "com.example.FingerSdk"));
        config.put("iris", vendorsWith("classname", "com.example.IrisSdk"));
        when(globalParamRepository.getBiometricProviderConfig()).thenReturn(config);

        factory.initialize();

        verify(auditManagerService, times(3)).audit(
                eq(AuditEvent.BIO_SDK_PROVIDER_REGISTERED), eq(Components.REGISTRATION), anyString());
    }

    // ───────────── getProviderForFunction() — empty registry ─────────────

    @Test
    public void testGetProviderForFunction_nullModality_returnsNull() {
        bioSdkLoaderMock.when(() -> BioSdkLoader.findAllSdkFiles(mockContext)).thenReturn(Collections.emptyList());

        IBioApiV2 result = factory.getProviderForFunction(null, BiometricFunction.MATCH);

        assertNull(result);
    }

    @Test
    public void testGetProviderForMatch_face_emptyRegistry_returnsNull() {
        bioSdkLoaderMock.when(() -> BioSdkLoader.findAllSdkFiles(mockContext)).thenReturn(Collections.emptyList());

        assertNull(factory.getProviderForMatch(Modality.FACE));
    }

    @Test
    public void testGetProviderForMatch_fingerprintSlabLeft_emptyRegistry_returnsNull() {
        bioSdkLoaderMock.when(() -> BioSdkLoader.findAllSdkFiles(mockContext)).thenReturn(Collections.emptyList());

        assertNull(factory.getProviderForMatch(Modality.FINGERPRINT_SLAB_LEFT));
    }

    @Test
    public void testGetProviderForMatch_fingerprintSlabRight_emptyRegistry_returnsNull() {
        bioSdkLoaderMock.when(() -> BioSdkLoader.findAllSdkFiles(mockContext)).thenReturn(Collections.emptyList());

        assertNull(factory.getProviderForMatch(Modality.FINGERPRINT_SLAB_RIGHT));
    }

    @Test
    public void testGetProviderForMatch_fingerprintSlabThumbs_emptyRegistry_returnsNull() {
        bioSdkLoaderMock.when(() -> BioSdkLoader.findAllSdkFiles(mockContext)).thenReturn(Collections.emptyList());

        assertNull(factory.getProviderForMatch(Modality.FINGERPRINT_SLAB_THUMBS));
    }

    @Test
    public void testGetProviderForMatch_irisDouble_emptyRegistry_returnsNull() {
        bioSdkLoaderMock.when(() -> BioSdkLoader.findAllSdkFiles(mockContext)).thenReturn(Collections.emptyList());

        assertNull(factory.getProviderForMatch(Modality.IRIS_DOUBLE));
    }

    @Test
    public void testGetProviderForMatch_exceptionPhoto_emptyRegistry_returnsNull() {
        bioSdkLoaderMock.when(() -> BioSdkLoader.findAllSdkFiles(mockContext)).thenReturn(Collections.emptyList());

        assertNull(factory.getProviderForMatch(Modality.EXCEPTION_PHOTO));
    }

    // ───────────── getProviderForFunction() — populated registry ─────────────

    @Test
    public void testGetProviderForFunction_registeredFaceProvider_matchingFunction_returnsProvider() {
        setUpSuccessfulRegistration("face", BiometricType.FACE, BiometricFunction.MATCH);

        factory.initialize();
        IBioApiV2 result = factory.getProviderForFunction(Modality.FACE, BiometricFunction.MATCH);

        assertSame(mockProvider, result);
    }

    @Test
    public void testGetProviderForFunction_registeredFingerProvider_matchingFunction_returnsProvider() {
        setUpSuccessfulRegistration("finger", BiometricType.FINGER, BiometricFunction.MATCH);

        factory.initialize();
        IBioApiV2 result = factory.getProviderForFunction(Modality.FINGERPRINT_SLAB_LEFT, BiometricFunction.MATCH);

        assertSame(mockProvider, result);
    }

    @Test
    public void testGetProviderForFunction_registeredIrisProvider_matchingFunction_returnsProvider() {
        setUpSuccessfulRegistration("iris", BiometricType.IRIS, BiometricFunction.MATCH);

        factory.initialize();
        IBioApiV2 result = factory.getProviderForFunction(Modality.IRIS_DOUBLE, BiometricFunction.MATCH);

        assertSame(mockProvider, result);
    }

    @Test
    public void testGetProviderForFunction_nullSupportedMethods_returnsNull() {
        bioSdkLoaderMock.when(() -> BioSdkLoader.findAllSdkFiles(mockContext)).thenReturn(ONE_SDK_FILE);
        bioSdkLoaderMock.when(() -> BioSdkLoader.loadProvider(eq(mockContext), anyString(), anyList()))
                .thenReturn(mockProvider);
        when(mockProvider.init(any())).thenReturn(mockSdkInfo);
        when(mockSdkInfo.getProductOwner()).thenReturn(null);
        when(mockSdkInfo.getSupportedMethods()).thenReturn(null); // null methods

        when(globalParamRepository.getBiometricProviderConfig())
                .thenReturn(configWith("face", "classname", "com.example.Sdk"));
        factory.initialize();

        IBioApiV2 result = factory.getProviderForFunction(Modality.FACE, BiometricFunction.MATCH);

        assertNull(result);
    }

    @Test
    public void testGetProviderForFunction_registeredProvider_biometricTypeNotInSupportedList_returnsNull() {
        // Provider is registered for FINGER but we ask for FACE
        setUpSuccessfulRegistration("face", BiometricType.FINGER, BiometricFunction.MATCH);

        factory.initialize();
        IBioApiV2 result = factory.getProviderForFunction(Modality.FACE, BiometricFunction.MATCH);

        assertNull(result); // FACE not in supported types (only FINGER)
    }

    @Test
    public void testGetProviderForFunction_functionNotInSupportedMethods_returnsNull() {
        // Provider supports QUALITY but we ask for MATCH
        Map<BiometricFunction, List<BiometricType>> methods = new HashMap<>();
        methods.put(BiometricFunction.QUALITY_CHECK, Collections.singletonList(BiometricType.FACE));

        bioSdkLoaderMock.when(() -> BioSdkLoader.findAllSdkFiles(mockContext)).thenReturn(ONE_SDK_FILE);
        bioSdkLoaderMock.when(() -> BioSdkLoader.loadProvider(eq(mockContext), anyString(), anyList()))
                .thenReturn(mockProvider);
        when(mockProvider.init(any())).thenReturn(mockSdkInfo);
        when(mockSdkInfo.getProductOwner()).thenReturn(null);
        when(mockSdkInfo.getSupportedMethods()).thenReturn(methods);

        when(globalParamRepository.getBiometricProviderConfig())
                .thenReturn(configWith("face", "classname", "com.example.Sdk"));
        factory.initialize();

        IBioApiV2 result = factory.getProviderForFunction(Modality.FACE, BiometricFunction.MATCH);

        assertNull(result);
    }

    @Test
    public void testGetProviderForFunction_registryPrePopulated_doesNotReinitialize() {
        // After initialize() populates the registry, subsequent getProviderForFunction calls
        // must NOT call initialize() again (registry.isEmpty() is false)
        setUpSuccessfulRegistration("face", BiometricType.FACE, BiometricFunction.MATCH);
        factory.initialize();

        // Call getProviderForFunction — registry is non-empty, no re-init
        factory.getProviderForFunction(Modality.FACE, BiometricFunction.MATCH);

        // findAllSdkFiles should have been called exactly once (during initialize, not again)
        bioSdkLoaderMock.verify(() -> BioSdkLoader.findAllSdkFiles(mockContext), times(1));
    }

    @Test
    public void testGetProviderForMatch_delegatesToGetProviderForFunction() {
        setUpSuccessfulRegistration("face", BiometricType.FACE, BiometricFunction.MATCH);
        factory.initialize();

        IBioApiV2 viaMatch = factory.getProviderForMatch(Modality.FACE);
        IBioApiV2 viaFunction = factory.getProviderForFunction(Modality.FACE, BiometricFunction.MATCH);

        assertSame(viaFunction, viaMatch);
    }

    // ───────────── helpers ─────────────

    private void setUpSuccessfulRegistration(String modalityKey, BiometricType supportedType,
                                              BiometricFunction supportedFunction) {
        Map<BiometricFunction, List<BiometricType>> methods = new HashMap<>();
        methods.put(supportedFunction, Collections.singletonList(supportedType));

        bioSdkLoaderMock.when(() -> BioSdkLoader.findAllSdkFiles(mockContext)).thenReturn(ONE_SDK_FILE);
        bioSdkLoaderMock.when(() -> BioSdkLoader.loadProvider(eq(mockContext), anyString(), anyList()))
                .thenReturn(mockProvider);
        when(mockProvider.init(any())).thenReturn(mockSdkInfo);
        when(mockSdkInfo.getProductOwner()).thenReturn(null);
        when(mockSdkInfo.getSupportedMethods()).thenReturn(methods);

        when(globalParamRepository.getBiometricProviderConfig())
                .thenReturn(configWith(modalityKey, "classname", "com.example.Sdk"));
    }

    private Map<String, Map<String, Map<String, String>>> configWith(
            String modalityKey, String paramKey, String paramValue) {
        return configWithParams(modalityKey, "vendor1",
                Collections.singletonMap(paramKey, paramValue));
    }

    private Map<String, Map<String, Map<String, String>>> configWithParams(
            String modalityKey, String vendorKey, Map<String, String> params) {
        Map<String, Map<String, Map<String, String>>> config = new HashMap<>();
        config.put(modalityKey, vendorsWith(vendorKey, params));
        return config;
    }

    private Map<String, Map<String, String>> vendorsWith(String paramKey, String paramValue) {
        return vendorsWith("vendor1", Collections.singletonMap(paramKey, paramValue));
    }

    private Map<String, Map<String, String>> vendorsWith(String vendorKey, Map<String, String> params) {
        Map<String, Map<String, String>> vendors = new HashMap<>();
        vendors.put(vendorKey, params);
        return vendors;
    }

    private void verifyNoRegistrationAudit() {
        verify(auditManagerService, never()).audit(
                eq(AuditEvent.BIO_SDK_PROVIDER_REGISTERED), any(Components.class), anyString());
    }
}
