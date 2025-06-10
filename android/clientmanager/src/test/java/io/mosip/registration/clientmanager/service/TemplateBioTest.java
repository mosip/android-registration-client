package io.mosip.registration.clientmanager.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import net.glxn.qrgen.android.QRCode;

import org.apache.velocity.VelocityContext;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import io.mosip.registration.clientmanager.R;
import io.mosip.registration.clientmanager.constant.Modality;
import io.mosip.registration.clientmanager.dto.CenterMachineDto;
import io.mosip.registration.clientmanager.dto.registration.BiometricsDto;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.packetmanager.dto.SimpleType;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TemplateBioTest {

    @Mock Context mockContext;
    @Mock SharedPreferences mockPrefs;
    @Mock SharedPreferences.Editor mockEditor;
    @Mock MasterDataService masterDataService;
    @Mock IdentitySchemaRepository identitySchemaRepository;
    @Mock GlobalParamRepository globalParamRepository;
    @Mock Resources mockResources;
    @Mock SharedPreferences sharedPreferences;

    TemplateService templateService;

    @Before
    public void setUp() {
        when(mockContext.getString(anyInt())).thenReturn("app_name");
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockPrefs);
        when(mockContext.getResources()).thenReturn(mockResources);
        lenient().when(mockPrefs.getString(anyString(), anyString())).thenReturn("user");
        templateService = new TemplateService(mockContext, masterDataService, identitySchemaRepository, globalParamRepository);
    }

    @Test
    public void test_setBiometricImage_bitmap_and_null() throws Exception {
        Map<String, Object> templateValues = new HashMap<>();
        String key = "testKey";
        int imagePath = 123;
        Bitmap mockBitmap = mock(Bitmap.class);

        Method m = TemplateService.class.getDeclaredMethod("setBiometricImage", Map.class, String.class, int.class, Bitmap.class, boolean.class);
        m.setAccessible(true);
        m.invoke(templateService, templateValues, key, imagePath, mockBitmap, true);
        m.invoke(templateService, templateValues, key, imagePath, null, false);
        assertTrue(templateValues.containsKey(key));
    }

    @Test
    public void test_setBiometricImage_velocityContext() throws Exception {
        VelocityContext velocityContext = new VelocityContext();
        String key = "testKey";
        Bitmap mockBitmap = mock(Bitmap.class);

        Method m = TemplateService.class.getDeclaredMethod("setBiometricImage", VelocityContext.class, String.class, Bitmap.class, boolean.class);
        m.setAccessible(true);
        m.invoke(templateService, velocityContext, key, mockBitmap, true);
        m.invoke(templateService, velocityContext, key, null, false);
    }

    @Test
    public void test_setFingerRankings_various() throws Exception {
        List<BiometricsDto> capturedFingers = new ArrayList<>();
        BiometricsDto b1 = new BiometricsDto();
        b1.setBioSubType("Left IndexFinger");
        b1.setBioValue("v1");
        b1.setQualityScore(80.5f);
        BiometricsDto b2 = new BiometricsDto();
        b2.setBioSubType("Right Thumb");
        b2.setBioValue("v2");
        b2.setQualityScore(95.2f);
        BiometricsDto b3 = new BiometricsDto();
        b3.setBioSubType("Left Thumb");
        b3.setBioValue(null);
        b3.setQualityScore(70.1f);
        capturedFingers.add(b1);
        capturedFingers.add(b2);
        capturedFingers.add(b3);

        List<String> fingers = Arrays.asList("leftIndex", "rightThumb", "leftThumb");
        Map<String, Object> data = new HashMap<>();

        Method m = TemplateService.class.getDeclaredMethod("setFingerRankings", List.class, List.class, Map.class);
        m.setAccessible(true);
        m.invoke(templateService, capturedFingers, fingers, data);

        assertEquals(1, data.get("leftIndex"));
        assertEquals(2, data.get("rightThumb"));
        assertEquals("&#10008;", data.get("leftThumb"));
    }

    @Test
    public void test_setFingerRankings_empty() throws Exception {
        List<BiometricsDto> capturedFingers = new ArrayList<>();
        List<String> fingers = Arrays.asList("leftIndex", "rightThumb", "leftThumb");
        Map<String, Object> data = new HashMap<>();

        Method m = TemplateService.class.getDeclaredMethod("setFingerRankings", List.class, List.class, Map.class);
        m.setAccessible(true);
        m.invoke(templateService, capturedFingers, fingers, data);

        assertEquals("&#10008;", data.get("leftIndex"));
        assertEquals("&#10008;", data.get("rightThumb"));
        assertEquals("&#10008;", data.get("leftThumb"));
    }

    @Test
    public void test_getImage_success_and_exception() throws Exception {
        Method m = TemplateService.class.getDeclaredMethod("getImage", int.class);
        m.setAccessible(true);
        BitmapFactoryMockHolder.decodeResourceShouldThrow = true;
        String result = (String) m.invoke(templateService, 123);
        assertEquals("", result);
        BitmapFactoryMockHolder.decodeResourceShouldThrow = false;
    }

    public static class BitmapFactoryMockHolder {
        public static Bitmap mockBitmap;
        public static boolean decodeResourceShouldThrow = false;
    }

    @Test
    public void test_setBiometricImage_nullBitmapAndZeroImagePath() throws Exception {
        Map<String, Object> templateValues = new HashMap<>();
        String key = "testKey";
        int imagePath = 0;
        Method m = TemplateService.class.getDeclaredMethod("setBiometricImage", Map.class, String.class, int.class, Bitmap.class, boolean.class);
        m.setAccessible(true);
        m.invoke(templateService, templateValues, key, imagePath, null, false);
        assertFalse(templateValues.containsKey(key));
    }

    @Test
    public void test_setBiometricImage_velocityContext_nullBitmap() throws Exception {
        VelocityContext velocityContext = new VelocityContext();
        String key = "testKey";
        Method m = TemplateService.class.getDeclaredMethod("setBiometricImage", VelocityContext.class, String.class, Bitmap.class, boolean.class);
        m.setAccessible(true);
        m.invoke(templateService, velocityContext, key, null, false);
        assertNull(velocityContext.get(key));
    }

    @Test
    public void test_getImage_nullBitmap() throws Exception {
        BitmapFactoryMockHolder.mockBitmap = null;
        Method m = TemplateService.class.getDeclaredMethod("getImage", int.class);
        m.setAccessible(true);
        String result = (String) m.invoke(templateService, 123);
        assertEquals("", result);
    }

    @Test
    public void test_setFingerRankings_nullBioSubType() throws Exception {
        List<BiometricsDto> capturedFingers = new ArrayList<>();
        BiometricsDto b1 = new BiometricsDto();
        b1.setBioSubType(null);
        b1.setBioValue("v1");
        b1.setQualityScore(80.5f);
        capturedFingers.add(b1);

        List<String> fingers = Arrays.asList("leftIndex");
        Map<String, Object> data = new HashMap<>();

        Method m = TemplateService.class.getDeclaredMethod("setFingerRankings", List.class, List.class, Map.class);
        m.setAccessible(true);
        m.invoke(templateService, capturedFingers, fingers, data);

        assertEquals("&#10008;", data.get("leftIndex"));
    }

    @Test
    public void test_setFingerRankings_various_pass() {
        List<BiometricsDto> capturedFingers = new ArrayList<>();
        List<String> fingers = Arrays.asList("leftIndex", "rightThumb", "leftThumb");
        Map<String, Object> data = new HashMap<>();

        ReflectionTestUtils.invokeMethod(templateService, "setFingerRankings", capturedFingers, fingers, data);

        assertEquals("&#10008;", data.get("leftIndex"));
        assertEquals("&#10008;", data.get("rightThumb"));
        assertEquals("&#10008;", data.get("leftThumb"));
    }

}