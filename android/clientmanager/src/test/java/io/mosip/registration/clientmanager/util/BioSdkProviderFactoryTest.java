package io.mosip.registration.clientmanager.util;

import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.mosip.kernel.biometrics.constant.BiometricFunction;
import io.mosip.kernel.biometrics.constant.BiometricType;
import io.mosip.kernel.biometrics.model.SDKInfo;
import io.mosip.kernel.biometrics.spi.IBioApiV2;
import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.constant.Modality;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.spi.AuditManagerService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BioSdkProviderFactoryTest {

    @Mock
    private Context context;

    @Mock
    private GlobalParamRepository globalParamRepository;

    @Mock
    private AuditManagerService auditManagerService;

    @Mock
    private IBioApiV2 provider;

    @Mock
    private SDKInfo sdkInfo;

    @Test
    public void testInitialize_withNullContext_doesNothing() {
        BioSdkProviderFactory factory =
                new BioSdkProviderFactory(null, globalParamRepository, auditManagerService);

        factory.initialize();

        verify(globalParamRepository, never()).getBiometricProviderConfig();
        verifyNoInteractions(auditManagerService);
    }

    @Test
    public void testInitialize_noSdkFiles_auditsAndReturns() {
        BioSdkProviderFactory factory =
                new BioSdkProviderFactory(context, globalParamRepository, auditManagerService);

        try (MockedStatic<BioSdkLoader> mocked = org.mockito.Mockito.mockStatic(BioSdkLoader.class)) {
            mocked.when(() -> BioSdkLoader.findAllSdkFiles(context))
                    .thenReturn(Collections.emptyList());

            factory.initialize();

            verify(auditManagerService).audit(
                    AuditEvent.BIO_SDK_FILES_NOT_FOUND,
                    Components.REGISTRATION,
                    "No biometric SDK files found in assets");
            verify(globalParamRepository, never()).getBiometricProviderConfig();
        }
    }

    @Test
    public void testInitialize_nullSdkFiles_auditsAndReturns() {
        BioSdkProviderFactory factory =
                new BioSdkProviderFactory(context, globalParamRepository, auditManagerService);

        try (MockedStatic<BioSdkLoader> mocked = org.mockito.Mockito.mockStatic(BioSdkLoader.class)) {
            mocked.when(() -> BioSdkLoader.findAllSdkFiles(context))
                    .thenReturn(null);

            factory.initialize();

            verify(auditManagerService).audit(
                    AuditEvent.BIO_SDK_FILES_NOT_FOUND,
                    Components.REGISTRATION,
                    "No biometric SDK files found in assets");
            verify(globalParamRepository, never()).getBiometricProviderConfig();
        }
    }

    @Test
    public void testInitialize_noConfig_returnsWithoutRegistry() {
        BioSdkProviderFactory factory =
                new BioSdkProviderFactory(context, globalParamRepository, auditManagerService);

        List<File> sdkFiles = new ArrayList<>();
        sdkFiles.add(new File("dummy"));

        try (MockedStatic<BioSdkLoader> mocked = org.mockito.Mockito.mockStatic(BioSdkLoader.class)) {
            mocked.when(() -> BioSdkLoader.findAllSdkFiles(context))
                    .thenReturn(sdkFiles);

            when(globalParamRepository.getBiometricProviderConfig())
                    .thenReturn(null);

            factory.initialize();

            Map<?, ?> registry = ReflectionTestUtils.getField(factory, "registry") instanceof Map
                    ? (Map<?, ?>) ReflectionTestUtils.getField(factory, "registry")
                    : null;
            assertEquals(0, registry.size());
        }
    }

    @Test
    public void testInitialize_invalidModalityKey_isSkipped() {
        BioSdkProviderFactory factory =
                new BioSdkProviderFactory(context, globalParamRepository, auditManagerService);

        List<File> sdkFiles = new ArrayList<>();
        sdkFiles.add(new File("dummy"));

        Map<String, Map<String, Map<String, String>>> config = new HashMap<>();
        config.put("unknown", new HashMap<String, Map<String, String>>());

        try (MockedStatic<BioSdkLoader> mocked = org.mockito.Mockito.mockStatic(BioSdkLoader.class)) {
            mocked.when(() -> BioSdkLoader.findAllSdkFiles(context))
                    .thenReturn(sdkFiles);

            when(globalParamRepository.getBiometricProviderConfig())
                    .thenReturn(config);

            factory.initialize();

            Map<?, ?> registry = ReflectionTestUtils.getField(factory, "registry") instanceof Map
                    ? (Map<?, ?>) ReflectionTestUtils.getField(factory, "registry")
                    : null;
            assertEquals(0, registry.size());
        }
    }

    @Test
    public void testInitialize_missingOrEmptyClassName_isSkipped() {
        BioSdkProviderFactory factory =
                new BioSdkProviderFactory(context, globalParamRepository, auditManagerService);

        List<File> sdkFiles = new ArrayList<>();
        sdkFiles.add(new File("dummy"));

        // vendor with null and empty classname values
        Map<String, String> paramsNull = new HashMap<>();
        paramsNull.put("version", "1.0");

        Map<String, String> paramsEmpty = new HashMap<>();
        paramsEmpty.put("classname", "  "); // trimmed empty
        paramsEmpty.put("version", "1.0");

        Map<String, Map<String, String>> vendors = new HashMap<>();
        vendors.put("vendorNull", paramsNull);
        vendors.put("vendorEmpty", paramsEmpty);

        Map<String, Map<String, Map<String, String>>> config = new HashMap<>();
        config.put("face", vendors);

        try (MockedStatic<BioSdkLoader> mocked = org.mockito.Mockito.mockStatic(BioSdkLoader.class)) {
            mocked.when(() -> BioSdkLoader.findAllSdkFiles(context))
                    .thenReturn(sdkFiles);

            when(globalParamRepository.getBiometricProviderConfig())
                    .thenReturn(config);

            factory.initialize();

            Map<?, ?> registry = ReflectionTestUtils.getField(factory, "registry") instanceof Map
                    ? (Map<?, ?>) ReflectionTestUtils.getField(factory, "registry")
                    : null;
            assertEquals(0, registry.size());
        }
    }

    @Test
    public void testInitialize_successfulRegistration_andLookup() {
        BioSdkProviderFactory factory =
                new BioSdkProviderFactory(context, globalParamRepository, auditManagerService);

        List<File> sdkFiles = new ArrayList<>();
        sdkFiles.add(new File("dummy"));

        Map<String, String> params = new HashMap<>();
        params.put("classname", "com.example.Provider");
        params.put("version", "1.0");

        Map<String, Map<String, String>> vendors = new HashMap<>();
        vendors.put("vendor1", params);

        Map<String, Map<String, Map<String, String>>> config = new HashMap<>();
        config.put("face", vendors);

        Map<BiometricFunction, List<BiometricType>> supported = new HashMap<>();
        supported.put(BiometricFunction.MATCH,
                Collections.singletonList(BiometricType.FACE));

        when(sdkInfo.getApiVersion()).thenReturn("1.0");
        when(sdkInfo.getSupportedMethods()).thenReturn(supported);
        when(provider.init(params)).thenReturn(sdkInfo);

        try (MockedStatic<BioSdkLoader> mocked = org.mockito.Mockito.mockStatic(BioSdkLoader.class)) {
            mocked.when(() -> BioSdkLoader.findAllSdkFiles(context))
                    .thenReturn(sdkFiles);
            mocked.when(() -> BioSdkLoader.loadProvider(context, "com.example.Provider", sdkFiles))
                    .thenReturn(provider);

            when(globalParamRepository.getBiometricProviderConfig())
                    .thenReturn(config);

            factory.initialize();

            IBioApiV2 result = factory.getProviderForMatch(Modality.FACE);
            assertSame(provider, result);

            verify(auditManagerService).audit(
                    eq(AuditEvent.BIO_SDK_PROVIDER_REGISTERED),
                    eq(Components.REGISTRATION),
                    eq("vendor1"));
        }
    }

    @Test
    public void testInitialize_initProviderThrows_exceptionHandledAndNotRegistered() {
        BioSdkProviderFactory factory =
                new BioSdkProviderFactory(context, globalParamRepository, auditManagerService);

        List<File> sdkFiles = new ArrayList<>();
        sdkFiles.add(new File("dummy"));

        Map<String, String> params = new HashMap<>();
        params.put("classname", "com.example.Provider");
        params.put("version", "1.0");

        Map<String, Map<String, String>> vendors = new HashMap<>();
        vendors.put("vendor1", params);

        Map<String, Map<String, Map<String, String>>> config = new HashMap<>();
        config.put("face", vendors);

        // init throws, which should be caught in initProvider()
        when(provider.init(params)).thenThrow(new RuntimeException("init failed"));

        try (MockedStatic<BioSdkLoader> mocked = org.mockito.Mockito.mockStatic(BioSdkLoader.class)) {
            mocked.when(() -> BioSdkLoader.findAllSdkFiles(context))
                    .thenReturn(sdkFiles);
            mocked.when(() -> BioSdkLoader.loadProvider(context, "com.example.Provider", sdkFiles))
                    .thenReturn(provider);

            when(globalParamRepository.getBiometricProviderConfig())
                    .thenReturn(config);

            factory.initialize();

            Map<?, ?> registry = ReflectionTestUtils.getField(factory, "registry") instanceof Map
                    ? (Map<?, ?>) ReflectionTestUtils.getField(factory, "registry")
                    : null;
            assertEquals(0, registry.size());
        }
    }

    @Test
    public void testInitialize_sdkVersionMismatch_notRegistered() {
        BioSdkProviderFactory factory =
                new BioSdkProviderFactory(context, globalParamRepository, auditManagerService);

        List<File> sdkFiles = new ArrayList<>();
        sdkFiles.add(new File("dummy"));

        Map<String, String> params = new HashMap<>();
        params.put("classname", "com.example.Provider");
        params.put("version", "1.0");

        Map<String, Map<String, String>> vendors = new HashMap<>();
        vendors.put("vendor1", params);

        Map<String, Map<String, Map<String, String>>> config = new HashMap<>();
        config.put("face", vendors);

        when(sdkInfo.getApiVersion()).thenReturn("2.0");
        when(provider.init(params)).thenReturn(sdkInfo);

        try (MockedStatic<BioSdkLoader> mocked = org.mockito.Mockito.mockStatic(BioSdkLoader.class)) {
            mocked.when(() -> BioSdkLoader.findAllSdkFiles(context))
                    .thenReturn(sdkFiles);
            mocked.when(() -> BioSdkLoader.loadProvider(context, "com.example.Provider", sdkFiles))
                    .thenReturn(provider);

            when(globalParamRepository.getBiometricProviderConfig())
                    .thenReturn(config);

            factory.initialize();

            IBioApiV2 result = factory.getProviderForMatch(Modality.FACE);
            assertNull(result);
        }
    }

    @Test
    public void testGetProviderForFunction_registryEmpty_returnsNull() {
        BioSdkProviderFactory factory =
                new BioSdkProviderFactory(context, globalParamRepository, auditManagerService);

        try (MockedStatic<BioSdkLoader> mocked = org.mockito.Mockito.mockStatic(BioSdkLoader.class)) {
            mocked.when(() -> BioSdkLoader.findAllSdkFiles(context))
                    .thenReturn(Collections.emptyList());

            IBioApiV2 result = factory.getProviderForFunction(Modality.IRIS_DOUBLE, BiometricFunction.MATCH);

            assertNull(result);
        }
    }

    @Test
    public void testGetProviderForFunction_nullModality_returnsNull() {
        BioSdkProviderFactory factory =
                new BioSdkProviderFactory(context, globalParamRepository, auditManagerService);

        try (MockedStatic<BioSdkLoader> mocked = org.mockito.Mockito.mockStatic(BioSdkLoader.class)) {
            mocked.when(() -> BioSdkLoader.findAllSdkFiles(context))
                    .thenReturn(Collections.emptyList());

            IBioApiV2 result = factory.getProviderForFunction(null, BiometricFunction.MATCH);

            assertNull(result);
        }
    }

    @Test
    public void testModalityKeyMapping_andValidationHelpers() {
        BioSdkProviderFactory factory =
                new BioSdkProviderFactory(context, globalParamRepository, auditManagerService);

        String fingerKey = ReflectionTestUtils.invokeMethod(factory, "getModalityKey",
                Modality.FINGERPRINT_SLAB_LEFT);
        String irisKey = ReflectionTestUtils.invokeMethod(factory, "getModalityKey",
                Modality.IRIS_DOUBLE);
        String faceKey = ReflectionTestUtils.invokeMethod(factory, "getModalityKey",
                Modality.FACE);
        String unknownKey = ReflectionTestUtils.invokeMethod(factory, "getModalityKey",
                (Modality) null);

        assertEquals("finger", fingerKey);
        assertEquals("iris", irisKey);
        assertEquals("face", faceKey);
        assertNull(unknownKey);

        assertEquals(Modality.FINGERPRINT_SLAB_LEFT,
                ReflectionTestUtils.invokeMethod(factory, "validateModalityKey", "finger"));
        assertEquals(Modality.IRIS_DOUBLE,
                ReflectionTestUtils.invokeMethod(factory, "validateModalityKey", "iris"));
        assertEquals(Modality.FACE,
                ReflectionTestUtils.invokeMethod(factory, "validateModalityKey", "face"));
        assertNull(ReflectionTestUtils.invokeMethod(factory, "validateModalityKey", "other"));
    }
}

