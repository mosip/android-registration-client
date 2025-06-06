package io.mosip.registration.clientmanager.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import io.mosip.registration.clientmanager.R;
import io.mosip.registration.clientmanager.constant.Modality;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dto.CenterMachineDto;
import io.mosip.registration.clientmanager.dto.registration.BiometricsDto;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.spi.MasterDataService;

import io.mosip.registration.packetmanager.dto.Document;
import io.mosip.registration.packetmanager.dto.SimpleType;
import net.glxn.qrgen.android.QRCode;
import org.apache.velocity.VelocityContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TemplateServiceTest {

    @Mock
    private Context context;
    @Mock
    private MasterDataService masterDataService;
    @Mock
    private IdentitySchemaRepository identitySchemaRepository;
    @Mock
    private GlobalParamRepository globalParamRepository;
    @Mock
    private SharedPreferences sharedPreferences;
    @Mock
    private SharedPreferences.Editor sharedPreferencesEditor;

    private TemplateService templateService;

    private static final String APPLICANT_IMAGE_SOURCE = "ApplicantImageSource";

    private static final String BASE64_IMAGE_PREFIX = "\"data:image/jpeg;base64,";

    private static final String CROSS_MARK = "&#10008;";

    public static final String USER_NAME = "user_name";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences);
        when(sharedPreferences.getString(anyString(), anyString())).thenReturn("testRO");
        when(context.getString(R.string.app_name)).thenReturn("TestApp");
        when(context.getString(R.string.app_id)).thenReturn("APPID");
        when(context.getString(R.string.uin)).thenReturn("UIN");
        when(context.getString(R.string.date)).thenReturn("Date");
        when(context.getString(R.string.photo)).thenReturn("Photo");
        when(context.getString(R.string.face_label)).thenReturn("Face");
        when(context.getString(R.string.exception_photo_label)).thenReturn("ExceptionPhoto");
        when(context.getString(R.string.ro_label)).thenReturn("RO");
        when(context.getString(R.string.reg_center)).thenReturn("RegCenter");
        when(context.getString(R.string.left_iris)).thenReturn("LeftEye");
        when(context.getString(R.string.right_iris)).thenReturn("RightEye");
        when(context.getString(R.string.left_slap)).thenReturn("LeftPalm");
        when(context.getString(R.string.right_slap)).thenReturn("RightPalm");
        when(context.getString(R.string.thumbs_label)).thenReturn("Thumbs");
        when(context.getString(R.string.fingers)).thenReturn("Fingers");
        when(context.getString(R.string.double_iris)).thenReturn("Iris");
        when(context.getString(R.string.face_label)).thenReturn("Face");

        templateService = new TemplateService(context, masterDataService, identitySchemaRepository, globalParamRepository);
    }

    @Test
    public void testGetTemplate_NoSchema_ThrowsException() {
        RegistrationDto registrationDto = mock(RegistrationDto.class);
        Map<String, String> templateTitleValues = new HashMap<>();
        List<String> langs = Collections.singletonList("en");
        when(registrationDto.getSelectedLanguages()).thenReturn(langs);
        when(masterDataService.getPreviewTemplateContent(anyString(), anyString())).thenReturn("<html>Test Template</html>");
        when(identitySchemaRepository.getLatestSchemaVersion()).thenReturn(null);

        Exception ex = assertThrows(Exception.class, () -> {
            templateService.getTemplate(registrationDto, true, templateTitleValues);
        });
        assertEquals("No Schema found", ex.getMessage());
    }

    @Test
    public void testGetDemographicData_ReturnsNullForUIN() {
        FieldSpecDto field = new FieldSpecDto();
        field.setId("UIN");
        RegistrationDto registrationDto = mock(RegistrationDto.class);
        assertNull(invokeGetDemographicData(field, registrationDto));
    }

    @Test
    public void testGetDemographicData_ReturnsNullForIDSchemaVersion() {
        FieldSpecDto field = new FieldSpecDto();
        field.setId("IDSchemaVersion");
        RegistrationDto registrationDto = mock(RegistrationDto.class);
        assertNull(invokeGetDemographicData(field, registrationDto));
    }

    @Test
    public void testGetDemographicData_ReturnsData() {
        FieldSpecDto field = new FieldSpecDto();
        field.setId("name");
        field.setLabel(Collections.singletonMap("en", "Name"));
        field.setType("simpleType");
        RegistrationDto registrationDto = mock(RegistrationDto.class);
        when(registrationDto.getDemographics()).thenReturn(Collections.singletonMap("name", "John"));
        when(registrationDto.getSelectedLanguages()).thenReturn(Collections.singletonList("en"));

        Map<String, Object> data = invokeGetDemographicData(field, registrationDto);
        assertNotNull(data);
        assertEquals("Name", data.get("label"));
        assertEquals("John", data.get("value"));
    }

    @Test
    public void testGetDemographicData_ReturnsNullForEmptyValue() {
        FieldSpecDto field = new FieldSpecDto();
        field.setId("name");
        field.setLabel(Collections.singletonMap("en", "Name"));
        field.setType("simpleType");
        RegistrationDto registrationDto = mock(RegistrationDto.class);
        when(registrationDto.getDemographics()).thenReturn(Collections.singletonMap("name", ""));
        when(registrationDto.getSelectedLanguages()).thenReturn(Collections.singletonList("en"));

        Map<String, Object> data = invokeGetDemographicData(field, registrationDto);
        assertNull(data);
    }

    @Test
    public void testGetFieldValue_SimpleType() {
        FieldSpecDto field = new FieldSpecDto();
        field.setId("name");
        field.setType("simpleType");
        RegistrationDto registrationDto = mock(RegistrationDto.class);
        when(registrationDto.getDemographics()).thenReturn(Collections.singletonMap("name", "John"));
        when(registrationDto.getSelectedLanguages()).thenReturn(Collections.singletonList("en"));

        String value = invokeGetFieldValue(field, registrationDto);
        assertEquals("John", value);
    }

    @Test
    public void testGetFieldValue_NonSimpleType() {
        FieldSpecDto field = new FieldSpecDto();
        field.setId("name");
        field.setType("complexType");
        RegistrationDto registrationDto = mock(RegistrationDto.class);
        when(registrationDto.getDemographics()).thenReturn(Collections.singletonMap("name", "John"));
        when(registrationDto.getSelectedLanguages()).thenReturn(Collections.singletonList("en"));

        String value = invokeGetFieldValue(field, registrationDto);
        assertEquals("John", value);
    }

    @Test
    public void testGetValue_String() {
        String result = invokeGetValue("abc");
        assertEquals("abc", result);
    }

    @Test
    public void testGetValue_Integer() {
        String result = invokeGetValue(123);
        assertEquals("123", result);
    }

    @Test
    public void testGetValue_BigInteger() {
        String result = invokeGetValue(BigInteger.valueOf(123));
        assertEquals("123", result);
    }

    @Test
    public void testGetValue_Double() {
        String result = invokeGetValue(12.34);
        assertEquals("12.34", result);
    }

    @Test
    public void testGetValue_List() {
        List<io.mosip.registration.packetmanager.dto.SimpleType> list = new ArrayList<>();
        io.mosip.registration.packetmanager.dto.SimpleType st = new io.mosip.registration.packetmanager.dto.SimpleType();
        st.setValue("val");
        list.add(st);
        String result = invokeGetValue(list);
        assertEquals("val", result);
    }

    @Test
    public void testGetValue_WithLang_List() {
        List<io.mosip.registration.packetmanager.dto.SimpleType> list = new ArrayList<>();
        io.mosip.registration.packetmanager.dto.SimpleType st = new io.mosip.registration.packetmanager.dto.SimpleType();
        st.setValue("val");
        st.setLanguage("en");
        list.add(st);
        String result = invokeGetValueWithLang(list, "en");
        assertEquals("val", result);
    }

    @Test
    public void testGetValue_WithLang_String() {
        String result = invokeGetValueWithLang("abc", "en");
        assertEquals("abc", result);
    }

    @Test
    public void testGetValue_NullInput() {
        String result = invokeGetValue(null);
        assertEquals("", result);
    }

    @Test
    public void testGetValueWithLang_NullInput() {
        String result = invokeGetValueWithLang(null, "en");
        assertEquals("", result);
    }

    @Test
    public void testGetValueWithLang_ListNoLangMatch() {
        List<SimpleType> list = new ArrayList<>();
        SimpleType st = new SimpleType();
        st.setValue("val");
        st.setLanguage("fr");
        list.add(st);
        String result = invokeGetValueWithLang(list, "en");
        assertEquals("", result);
    }

    @Test
    public void testGetFieldLabel() {
        FieldSpecDto field = new FieldSpecDto();
        field.setLabel(Collections.singletonMap("en", "Label"));
        RegistrationDto registrationDto = mock(RegistrationDto.class);
        when(registrationDto.getSelectedLanguages()).thenReturn(Collections.singletonList("en"));
        Object label = invokeGetFieldLabel(field, registrationDto);
        assertEquals("Label", label);
    }

    @Test
    public void testGetFieldLabel_MultipleLanguages() {
        FieldSpecDto field = new FieldSpecDto();
        Map<String, String> labels = new HashMap<>();
        labels.put("en", "LabelEN");
        labels.put("fr", "LabelFR");
        field.setLabel(labels);
        RegistrationDto registrationDto = mock(RegistrationDto.class);
        when(registrationDto.getSelectedLanguages()).thenReturn(Arrays.asList("en", "fr"));
        Object label = invokeGetFieldLabel(field, registrationDto);
        assertEquals("LabelEN/LabelFR", label);
    }

    @Test
    public void testGetFieldValue_SimpleType_MultipleLanguages() {
        FieldSpecDto field = new FieldSpecDto();
        field.setId("name");
        field.setType("simpleType");
        RegistrationDto registrationDto = mock(RegistrationDto.class);
        when(registrationDto.getDemographics()).thenReturn(Collections.singletonMap("name", "John"));
        when(registrationDto.getSelectedLanguages()).thenReturn(Arrays.asList("en", "fr"));

        String value = invokeGetFieldValue(field, registrationDto);
        assertEquals("John/John", value);
    }

    @Test
    public void testGetFieldValue_NonSimpleType_MultipleLanguages() {
        FieldSpecDto field = new FieldSpecDto();
        field.setId("name");
        field.setType("complexType");
        RegistrationDto registrationDto = mock(RegistrationDto.class);
        when(registrationDto.getDemographics()).thenReturn(Collections.singletonMap("name", "John"));
        when(registrationDto.getSelectedLanguages()).thenReturn(Arrays.asList("en", "fr"));

        String value = invokeGetFieldValue(field, registrationDto);
        assertEquals("John", value);
    }

    @Test
    public void testGetDocumentData_Null() {
        FieldSpecDto field = new FieldSpecDto();
        field.setId("doc1");
        RegistrationDto registrationDto = mock(RegistrationDto.class);
        when(registrationDto.getDocuments()).thenReturn(Collections.emptyMap());
        VelocityContext velocityContext = new VelocityContext();
        Map<String, Object> data = invokeGetDocumentData(field, registrationDto, velocityContext);
        assertNull(data);
    }

    @Test
    public void testGetDocumentData_DocumentTypeNull() {
        FieldSpecDto field = new FieldSpecDto();
        field.setId("doc2");
        RegistrationDto registrationDto = mock(RegistrationDto.class);
        when(registrationDto.getDocuments()).thenReturn(Collections.emptyMap());
        VelocityContext velocityContext = new VelocityContext();
        Map<String, Object> data = invokeGetDocumentData(field, registrationDto, velocityContext);
        assertNull(data);
    }

    @Test
    public void testGetValue_ListWithNullValue() {
        List<SimpleType> list = new ArrayList<>();
        SimpleType st = new SimpleType();
        st.setValue(null);
        list.add(st);
        String result = invokeGetValue(list);
        assertNull(result);
    }

    @Test
    public void testGetValueWithLang_ListWithNullValue() {
        List<SimpleType> list = new ArrayList<>();
        SimpleType st = new SimpleType();
        st.setValue(null);
        st.setLanguage("en");
        list.add(st);
        String result = invokeGetValueWithLang(list, "en");
        assertEquals("", result);
    }

    @Test
    public void testGetFieldLabel_EmptyLanguages() {
        FieldSpecDto field = new FieldSpecDto();
        field.setLabel(Collections.emptyMap());
        RegistrationDto registrationDto = mock(RegistrationDto.class);
        when(registrationDto.getSelectedLanguages()).thenReturn(Collections.emptyList());
        Object label = invokeGetFieldLabel(field, registrationDto);
        assertEquals("", label);
    }

    private Map<String, Object> invokeGetDemographicData(FieldSpecDto field, RegistrationDto registrationDto) {
        try {
            java.lang.reflect.Method m = TemplateService.class.getDeclaredMethod("getDemographicData", FieldSpecDto.class, RegistrationDto.class);
            m.setAccessible(true);
            return (Map<String, Object>) m.invoke(templateService, field, registrationDto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String invokeGetFieldValue(FieldSpecDto field, RegistrationDto registrationDto) {
        try {
            java.lang.reflect.Method m = TemplateService.class.getDeclaredMethod("getFieldValue", FieldSpecDto.class, RegistrationDto.class);
            m.setAccessible(true);
            return (String) m.invoke(templateService, field, registrationDto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String invokeGetValue(Object fieldValue) {
        try {
            java.lang.reflect.Method m = TemplateService.class.getDeclaredMethod("getValue", Object.class);
            m.setAccessible(true);
            return (String) m.invoke(templateService, fieldValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String invokeGetValueWithLang(Object fieldValue, String lang) {
        try {
            java.lang.reflect.Method m = TemplateService.class.getDeclaredMethod("getValue", Object.class, String.class);
            m.setAccessible(true);
            return (String) m.invoke(templateService, fieldValue, lang);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object invokeGetFieldLabel(FieldSpecDto field, RegistrationDto registrationDto) {
        try {
            java.lang.reflect.Method m = TemplateService.class.getDeclaredMethod("getFieldLabel", FieldSpecDto.class, RegistrationDto.class);
            m.setAccessible(true);
            return m.invoke(templateService, field, registrationDto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Object> invokeGetDocumentData(FieldSpecDto field, RegistrationDto registrationDto, VelocityContext velocityContext) {
        try {
            java.lang.reflect.Method m = TemplateService.class.getDeclaredMethod("getDocumentData", FieldSpecDto.class, RegistrationDto.class, VelocityContext.class);
            m.setAccessible(true);
            return (Map<String, Object>) m.invoke(templateService, field, registrationDto, velocityContext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test_bitmap_compression_and_encoding() {
        Map<String, Object> templateValues = new HashMap<>();
        String key = "testKey";
        Bitmap mockBitmap = Mockito.mock(Bitmap.class);

        doAnswer(invocation -> {
            ByteArrayOutputStream os = invocation.getArgument(2);
            os.write("test image data".getBytes());
            return true;
        }).when(mockBitmap).compress(eq(Bitmap.CompressFormat.JPEG), eq(50), any(ByteArrayOutputStream.class));

        ReflectionTestUtils.invokeMethod(templateService, "setBiometricImage", templateValues, key, 0, mockBitmap, true);

        verify(mockBitmap).compress(eq(Bitmap.CompressFormat.JPEG), eq(50), any(ByteArrayOutputStream.class));
    }

    @Test
    public void test_get_image_method_when_bitmap_is_null() {
        Map<String, Object> templateValues = new HashMap<>();
        String key = "testKey";
        int imagePath = 123;

        ReflectionTestUtils.invokeMethod(templateService, "setBiometricImage", templateValues, key, imagePath, null, false);

        assertTrue(templateValues.containsKey(key));
    }

    @Test
    public void test_handles_null_bitmap_gracefully() {
        VelocityContext velocityContext = mock(VelocityContext.class);
        String key = "testKey";
        boolean isPreview = true;
        Bitmap bitmap = null;

        ReflectionTestUtils.invokeMethod(templateService, "setBiometricImage", velocityContext, key, bitmap, isPreview);

        verify(velocityContext, never()).put(anyString(), anyString());
    }

    @Test
    public void test_correctly_ranks_captured_fingers() {
        List<BiometricsDto> capturedFingers = new ArrayList<>();

        BiometricsDto finger1 = new BiometricsDto();
        finger1.setBioSubType(RegistrationConstants.LEFT_INDEX_FINGER);
        finger1.setBioValue("value1");
        finger1.setQualityScore(80.5f);

        BiometricsDto finger2 = new BiometricsDto();
        finger2.setBioSubType(RegistrationConstants.RIGHT_THUMB);
        finger2.setBioValue("value2");
        finger2.setQualityScore(95.2f);

        BiometricsDto finger3 = new BiometricsDto();
        finger3.setBioSubType(RegistrationConstants.LEFT_THUMB);
        finger3.setBioValue("value3");
        finger3.setQualityScore(70.1f);

        capturedFingers.add(finger1);
        capturedFingers.add(finger2);
        capturedFingers.add(finger3);

        List<String> fingers = Arrays.asList("leftIndex", "rightThumb", "leftThumb");
        Map<String, Object> data = new HashMap<>();

        ReflectionTestUtils.invokeMethod(templateService, "setFingerRankings", capturedFingers, fingers, data);

        assertEquals(1, data.get("leftIndex"));
        assertEquals(2, data.get("rightThumb"));
        assertEquals(3, data.get("leftThumb"));
    }

    @Test
    public void test_empty_captured_fingers_list() {
        List<BiometricsDto> capturedFingers = new ArrayList<>();
        List<String> fingers = Arrays.asList("leftIndex", "rightThumb", "leftThumb");
        Map<String, Object> data = new HashMap<>();

        ReflectionTestUtils.invokeMethod(templateService, "setFingerRankings", capturedFingers, fingers, data);

        assertEquals(CROSS_MARK, data.get("leftIndex"));
        assertEquals(CROSS_MARK, data.get("rightThumb"));
        assertEquals(CROSS_MARK, data.get("leftThumb"));
    }

}
