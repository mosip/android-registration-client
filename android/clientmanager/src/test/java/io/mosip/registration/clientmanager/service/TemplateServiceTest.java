package io.mosip.registration.clientmanager.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import io.mosip.registration.clientmanager.R;
import io.mosip.registration.clientmanager.constant.Modality;
import io.mosip.registration.clientmanager.dto.CenterMachineDto;
import io.mosip.registration.clientmanager.dto.registration.BiometricsDto;
import io.mosip.registration.clientmanager.dto.registration.DocumentDto;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.util.UserInterfaceHelperService;
import io.mosip.registration.packetmanager.dto.SimpleType;
import net.glxn.qrgen.android.QRCode;
import org.apache.velocity.VelocityContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
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

    /**
     * Test getTemplate() for successful template generation in preview mode.
     */
    @Test
    public void testGetTemplate_success() throws Exception {
        try (
                MockedStatic<Bitmap> bitmapStaticMock = mockStatic(Bitmap.class);
                MockedStatic<BitmapFactory> bitmapFactoryMock = mockStatic(BitmapFactory.class);
                MockedStatic<Base64> base64Mock = mockStatic(Base64.class)
        ) {
            Bitmap mockBitmap = mock(Bitmap.class);
            bitmapStaticMock.when(() -> Bitmap.createBitmap(anyInt(), anyInt(), any(Bitmap.Config.class)))
                    .thenReturn(mockBitmap);
            bitmapFactoryMock.when(() -> BitmapFactory.decodeResource(any(), anyInt()))
                    .thenReturn(mockBitmap);
            base64Mock.when(() -> Base64.encodeToString(any(byte[].class), anyInt()))
                    .thenReturn("mocked_base64");

            RegistrationDto registrationDto = mock(RegistrationDto.class);
            when(registrationDto.getSelectedLanguages()).thenReturn(Collections.singletonList("en"));
            when(masterDataService.getPreviewTemplateContent(anyString(), anyString())).thenReturn("<html>Test</html>");
            when(identitySchemaRepository.getLatestSchemaVersion()).thenReturn(1.0);
            List<FieldSpecDto> fields = new ArrayList<>();
            FieldSpecDto field1 = new FieldSpecDto();
            field1.setType("simpleType");
            field1.setId("firstName");
            field1.setLabel(Collections.singletonMap("en", "First Name"));
            fields.add(field1);
            when(identitySchemaRepository.getProcessSpecFields(any(), any())).thenReturn(fields);
            Map<String, String> templateTitleValues = new HashMap<>();
            templateTitleValues.put("demographicInfo", "Demographic Info");
            templateTitleValues.put("documents", "Documents");
            templateTitleValues.put("bioMetrics", "Biometrics");
            when(registrationDto.getProcess()).thenReturn("REG");
            when(registrationDto.getDemographics()).thenReturn(Collections.singletonMap("firstName", "John"));
            when(registrationDto.getRId()).thenReturn("RID123");
            CenterMachineDto centerMachineDto = new CenterMachineDto();
            centerMachineDto.setCenterId("CENTER1");
            when(masterDataService.getRegistrationCenterMachineDetails()).thenReturn(centerMachineDto);
            when(globalParamRepository.getCachedStringGlobalParam(anyString())).thenReturn("Important guidelines");
            String result = templateService.getTemplate(registrationDto, true, templateTitleValues);
            assertTrue(result.contains("Test"));
        }
    }

    /**
     * Test getBiometricData() for iris and face biometrics in preview mode.
     */
    @Test
    public void testGetBiometricData_full() throws Exception {
        try (
                MockedStatic<BitmapFactory> bitmapFactoryMock = mockStatic(BitmapFactory.class);
                MockedStatic<Bitmap> bitmapStaticMock = mockStatic(Bitmap.class);
                MockedStatic<Base64> base64Mock = mockStatic(Base64.class)
        ) {
            Bitmap mockBitmap = mock(Bitmap.class);
            bitmapFactoryMock.when(() -> BitmapFactory.decodeResource(any(), anyInt())).thenReturn(mockBitmap);
            bitmapStaticMock.when(() -> Bitmap.createBitmap(anyInt(), anyInt(), any(Bitmap.Config.class))).thenReturn(mockBitmap);
            base64Mock.when(() -> Base64.encodeToString(any(byte[].class), anyInt())).thenReturn("mocked_base64");

            FieldSpecDto field = new FieldSpecDto();
            field.setId("bio1");
            field.setLabel(Collections.singletonMap("en", "BioLabel"));
            field.setSubType("applicant");
            field.setType("biometricsType");
            RegistrationDto registrationDto = mock(RegistrationDto.class);
            when(registrationDto.getSelectedLanguages()).thenReturn(Collections.singletonList("en"));
            List<BiometricsDto> empty = new ArrayList<>();
            List<BiometricsDto> iris = new ArrayList<>();
            BiometricsDto irisDto = new BiometricsDto();
            irisDto.setBioSubType("Left");
            irisDto.setBioValue("irisval");
            irisDto.setQualityScore(90f);
            iris.add(irisDto);
            List<BiometricsDto> face = new ArrayList<>();
            BiometricsDto faceDto = new BiometricsDto();
            faceDto.setBioSubType("Face");
            faceDto.setBioValue("faceval");
            faceDto.setQualityScore(87f);
            face.add(faceDto);
            when(registrationDto.getBestBiometrics(anyString(), eq(Modality.FINGERPRINT_SLAB_RIGHT))).thenReturn(empty);
            when(registrationDto.getBestBiometrics(anyString(), eq(Modality.FINGERPRINT_SLAB_LEFT))).thenReturn(empty);
            when(registrationDto.getBestBiometrics(anyString(), eq(Modality.FINGERPRINT_SLAB_THUMBS))).thenReturn(empty);
            when(registrationDto.getBestBiometrics(anyString(), eq(Modality.IRIS_DOUBLE))).thenReturn(iris);
            when(registrationDto.getBestBiometrics(anyString(), eq(Modality.FACE))).thenReturn(face);
            when(registrationDto.getBestBiometrics(anyString(), eq(Modality.EXCEPTION_PHOTO))).thenReturn(empty);
            VelocityContext velocityContext = new VelocityContext();
            Map<String, Object> data = ReflectionTestUtils.invokeMethod(templateService, "getBiometricData", field, registrationDto, true, velocityContext);
            assertNotNull(data);
            assertTrue(data.containsKey("LeftEye"));
            assertEquals("&#10003;", data.get("LeftEye"));
            assertEquals("BioLabel", data.get("label"));
            assertNull(velocityContext.get("ApplicantImageSource"));
        }
    }

    /**
     * Test setBiometricImage() with null bitmap and non-zero imagePath.
     */
    @Test
    public void testSetBiometricImage_nullBitmap_imagePath() {
        Map<String, Object> templateValues = new HashMap<>();
        ReflectionTestUtils.invokeMethod(templateService, "setBiometricImage", templateValues, "Key", 123, null, false);
        assertTrue(templateValues.containsKey("Key"));
    }

    /**
     * Test setBiometricImage() with a valid bitmap in preview mode.
     */
    @Test
    public void testSetBiometricImage_withBitmap() {
        try (MockedStatic<Base64> base64Mock = mockStatic(Base64.class)) {
            base64Mock.when(() -> Base64.encodeToString(any(byte[].class), anyInt()))
                    .thenReturn("mocked_base64");
            Map<String, Object> templateValues = new HashMap<>();
            Bitmap bitmap = mock(Bitmap.class);
            when(bitmap.compress(any(), anyInt(), any(ByteArrayOutputStream.class))).thenAnswer((Answer<Boolean>) invocation -> {
                ByteArrayOutputStream os = invocation.getArgument(2);
                os.write("abc".getBytes());
                return true;
            });
            ReflectionTestUtils.invokeMethod(templateService, "setBiometricImage", templateValues, "Key", 0, bitmap, true);
            assertTrue(templateValues.get("Key").toString().contains("mocked_base64"));
        }
    }

    /**
     * Test setBiometricImage() with VelocityContext and valid bitmap.
     */
    @Test
    public void testSetBiometricImage_velocityContext() {
        try (MockedStatic<Base64> base64Mock = mockStatic(Base64.class)) {
            base64Mock.when(() -> Base64.encodeToString(any(byte[].class), anyInt()))
                    .thenReturn("mocked_base64");
            VelocityContext ctx = new VelocityContext();
            Bitmap bitmap = mock(Bitmap.class);
            when(bitmap.compress(any(), anyInt(), any(ByteArrayOutputStream.class))).thenAnswer(invocation -> {
                ByteArrayOutputStream os = invocation.getArgument(2);
                os.write("xyz".getBytes());
                return true;
            });
            ReflectionTestUtils.invokeMethod(templateService, "setBiometricImage", ctx, "Key", bitmap, true);
            Object value = ctx.get("Key");
            assertNotNull("VelocityContext should contain the key", value);
            assertTrue(value.toString().contains("mocked_base64"));
        }
    }

    /**
     * Test setFingerRankings() with empty capturedFingers list.
     */
    @Test
    public void testSetFingerRankings_empty() {
        List<BiometricsDto> capturedFingers = new ArrayList<>();
        List<String> fingers = Arrays.asList("leftIndex", "rightThumb");
        Map<String, Object> data = new HashMap<>();
        ReflectionTestUtils.invokeMethod(templateService, "setFingerRankings", capturedFingers, fingers, data);
        assertEquals("&#10008;", data.get("leftIndex"));
        assertEquals("&#10008;", data.get("rightThumb"));
    }

    /**
     * Test setFingerRankings() with non-empty capturedFingers list.
     */
    @Test
    public void testSetFingerRankings_nonEmpty() {
        List<BiometricsDto> capturedFingers = new ArrayList<>();
        BiometricsDto b1 = new BiometricsDto();
        b1.setBioSubType("Left Thumb");
        b1.setBioValue("val1");
        b1.setQualityScore(90f);
        BiometricsDto b2 = new BiometricsDto();
        b2.setBioSubType("Right Thumb");
        b2.setBioValue("val2");
        b2.setQualityScore(70f);
        capturedFingers.add(b1);
        capturedFingers.add(b2);
        List<String> fingers = Arrays.asList("leftThumb", "rightThumb");
        Map<String, Object> data = new HashMap<>();
        ReflectionTestUtils.invokeMethod(templateService, "setFingerRankings", capturedFingers, fingers, data);
        assertTrue(data.get("leftThumb") instanceof Integer);
        assertTrue(data.get("rightThumb") instanceof Integer);
    }

    /**
     * Test getImage() returns empty string when BitmapFactory.decodeResource throws exception.
     */
    @Test
    public void testGetImage_returnsEmptyOnException() {
        Context mockContext = mock(Context.class);
        when(mockContext.getResources()).thenThrow(new RuntimeException("No resources"));
        TemplateService ts = new TemplateService(mockContext, masterDataService, identitySchemaRepository, globalParamRepository);
        String result = ReflectionTestUtils.invokeMethod(ts, "getImage", 123);
        assertEquals("", result);
    }

    /**
     * Test getDemographicData() skips field with id "UIN".
     */
    @Test
    public void testGetDemographicData_skipsUIN() {
        FieldSpecDto field = new FieldSpecDto();
        field.setId("UIN");
        RegistrationDto registrationDto = mock(RegistrationDto.class);
        Map<String, Object> data = ReflectionTestUtils.invokeMethod(templateService, "getDemographicData", field, registrationDto);
        assertNull(data);
    }

    /**
     * Test getDemographicData() returns data for valid field.
     */
    @Test
    public void testGetDemographicData_valid() {
        FieldSpecDto field = new FieldSpecDto();
        field.setId("firstName");
        field.setLabel(Collections.singletonMap("en", "First Name"));
        field.setType("simpleType");
        RegistrationDto registrationDto = mock(RegistrationDto.class);
        when(registrationDto.getDemographics()).thenReturn(Collections.singletonMap("firstName", "John"));
        when(registrationDto.getSelectedLanguages()).thenReturn(Collections.singletonList("en"));
        Map<String, Object> data = ReflectionTestUtils.invokeMethod(templateService, "getDemographicData", field, registrationDto);
        assertNotNull(data);
        assertEquals("First Name", data.get("label"));
    }

    /**
     * Test getValue() with different types of input values.
     */
    @Test
    public void testGetValue_types() {
        assertEquals("str", ReflectionTestUtils.invokeMethod(templateService, "getValue", "str"));
        assertEquals("1", ReflectionTestUtils.invokeMethod(templateService, "getValue", Integer.valueOf(1)));
        assertEquals("2", ReflectionTestUtils.invokeMethod(templateService, "getValue", BigInteger.valueOf(2)));
        assertEquals("3.5", ReflectionTestUtils.invokeMethod(templateService, "getValue", Double.valueOf(3.5)));
    }

    /**
     * Test getValue() with a list of SimpleType.
     */
    @Test
    public void testGetValue_list() {
        SimpleType st = new SimpleType();
        st.setValue("abc");
        List<SimpleType> list = Collections.singletonList(st);
        assertEquals("abc", ReflectionTestUtils.invokeMethod(templateService, "getValue", list));
    }

    /**
     * Test getValue() with null input.
     */
    @Test
    public void testGetValue_null() {
        assertEquals("", ReflectionTestUtils.invokeMethod(templateService, "getValue", (Object)null));
    }

    /**
     * Test getValue() with language filtering and matching language.
     */
    @Test
    public void testGetValue_withLang() {
        SimpleType st = new SimpleType();
        st.setValue("val");
        st.setLanguage("en");
        List<SimpleType> list = Collections.singletonList(st);
        assertEquals("val", ReflectionTestUtils.invokeMethod(templateService, "getValue", list, "en"));
    }

    /**
     * Test getValue() with language filtering and no matching language.
     */
    @Test
    public void testGetValue_withLang_noMatch() {
        SimpleType st = new SimpleType();
        st.setValue("val");
        st.setLanguage("fr");
        List<SimpleType> list = Collections.singletonList(st);
        assertEquals("", ReflectionTestUtils.invokeMethod(templateService, "getValue", list, "en"));
    }

    /**
     * Test getFieldLabel() returns label for selected language.
     */
    @Test
    public void testGetFieldLabel() {
        FieldSpecDto field = new FieldSpecDto();
        field.setLabel(Collections.singletonMap("en", "Label"));
        RegistrationDto registrationDto = mock(RegistrationDto.class);
        when(registrationDto.getSelectedLanguages()).thenReturn(Collections.singletonList("en"));
        Object label = ReflectionTestUtils.invokeMethod(templateService, "getFieldLabel", field, registrationDto);
        assertEquals("Label", label);
    }

    /**
     * Test getFieldValue() for simpleType field.
     */
    @Test
    public void testGetFieldValue_simpleType() {
        FieldSpecDto field = new FieldSpecDto();
        field.setId("firstName");
        field.setType("simpleType");
        RegistrationDto registrationDto = mock(RegistrationDto.class);
        when(registrationDto.getDemographics()).thenReturn(Collections.singletonMap("firstName", "John"));
        when(registrationDto.getSelectedLanguages()).thenReturn(Collections.singletonList("en"));
        String value = ReflectionTestUtils.invokeMethod(templateService, "getFieldValue", field, registrationDto);
        assertEquals("John", value);
    }

    /**
     * Test getFieldValue() for complexType field.
     */
    @Test
    public void testGetFieldValue_complexType() {
        FieldSpecDto field = new FieldSpecDto();
        field.setId("firstName");
        field.setType("complexType");
        RegistrationDto registrationDto = mock(RegistrationDto.class);
        when(registrationDto.getDemographics()).thenReturn(Collections.singletonMap("firstName", "John"));
        when(registrationDto.getSelectedLanguages()).thenReturn(Collections.singletonList("en"));
        String value = ReflectionTestUtils.invokeMethod(templateService, "getFieldValue", field, registrationDto);
        assertEquals("John", value);
    }

    /**
     * Test getDocumentData() returns data for valid document.
     */
    @Test
    public void testGetDocumentData_valid() {
        FieldSpecDto field = new FieldSpecDto();
        field.setId("doc1");
        field.setLabel(Collections.singletonMap("en", "DocLabel"));
        RegistrationDto registrationDto = mock(RegistrationDto.class);
        Map<String, DocumentDto> docs = new HashMap<>();
        DocumentDto doc = new DocumentDto();
        doc.setType("passport");
        docs.put("doc1", doc);
        when(registrationDto.getDocuments()).thenReturn(docs);
        when(registrationDto.getSelectedLanguages()).thenReturn(Collections.singletonList("en"));
        VelocityContext velocityContext = new VelocityContext();
        Map<String, Object> data = ReflectionTestUtils.invokeMethod(templateService, "getDocumentData", field, registrationDto, velocityContext);
        assertNotNull(data);
        assertEquals("DocLabel", data.get("label"));
        assertEquals("passport", data.get("value"));
    }

    /**
     * Test getTemplate() throws exception when schema version is not found.
     */
    @Test(expected = Exception.class)
    public void testGetTemplate_throwsExceptionWhenNoSchema() throws Exception {
        RegistrationDto registrationDto = mock(RegistrationDto.class);
        when(registrationDto.getSelectedLanguages()).thenReturn(Collections.singletonList("en"));
        when(masterDataService.getPreviewTemplateContent(anyString(), anyString())).thenReturn("<html>Test</html>");
        when(identitySchemaRepository.getLatestSchemaVersion()).thenReturn(null);
        Map<String, String> templateTitleValues = new HashMap<>();
        templateService.getTemplate(registrationDto, true, templateTitleValues);
    }

    /**
     * Test getBiometricData() returns label when no iris, face, or exception biometrics are present.
     */
    @Test
    public void testGetBiometricData_noIrisNoFaceNoException() throws Exception {
        try (
                MockedStatic<BitmapFactory> bitmapFactoryMock = mockStatic(BitmapFactory.class);
                MockedStatic<Bitmap> bitmapStaticMock = mockStatic(Bitmap.class);
                MockedStatic<Base64> base64Mock = mockStatic(Base64.class)
        ) {
            Bitmap mockBitmap = mock(Bitmap.class);
            bitmapFactoryMock.when(() -> BitmapFactory.decodeResource(any(), anyInt())).thenReturn(mockBitmap);
            bitmapStaticMock.when(() -> Bitmap.createBitmap(anyInt(), anyInt(), any(Bitmap.Config.class))).thenReturn(mockBitmap);
            base64Mock.when(() -> Base64.encodeToString(any(byte[].class), anyInt())).thenReturn("mocked_base64");

            FieldSpecDto field = new FieldSpecDto();
            field.setId("bio2");
            field.setLabel(Collections.singletonMap("en", "BioLabel2"));
            field.setSubType("introducer");
            field.setType("biometricsType");
            RegistrationDto registrationDto = mock(RegistrationDto.class);
            when(registrationDto.getSelectedLanguages()).thenReturn(Collections.singletonList("en"));
            when(registrationDto.getBestBiometrics(anyString(), any(Modality.class))).thenReturn(Collections.emptyList());
            VelocityContext velocityContext = new VelocityContext();
            Map<String, Object> data = ReflectionTestUtils.invokeMethod(templateService, "getBiometricData", field, registrationDto, true, velocityContext);
            assertNotNull(data);
            assertEquals("BioLabel2", data.get("label"));
        }
    }

    /**
     * Test setBiometricImage() with null bitmap and imagePath zero.
     */
    @Test
    public void testSetBiometricImage_bitmapNull_imagePathZero() {
        Map<String, Object> templateValues = new HashMap<>();
        ReflectionTestUtils.invokeMethod(templateService, "setBiometricImage", templateValues, "Key", 0, null, false);
        assertFalse(templateValues.containsKey("Key"));
    }

    /**
     * Test setBiometricImage() with VelocityContext and null bitmap.
     */
    @Test
    public void testSetBiometricImage_velocityContext_bitmapNull() {
        VelocityContext ctx = new VelocityContext();
        ReflectionTestUtils.invokeMethod(templateService, "setBiometricImage", ctx, "Key", (Bitmap) null, true);
        assertNull(ctx.get("Key"));
    }

    /**
     * Test setFingerRankings() with BiometricsDto having null bioValue.
     */
    @Test
    public void testSetFingerRankings_nullBioValue() {
        List<BiometricsDto> capturedFingers = new ArrayList<>();
        BiometricsDto b = new BiometricsDto();
        b.setBioSubType("Left Thumb");
        b.setBioValue(null);
        b.setQualityScore(90f);
        capturedFingers.add(b);
        List<String> fingers = Collections.singletonList("leftThumb");
        Map<String, Object> data = new HashMap<>();
        ReflectionTestUtils.invokeMethod(templateService, "setFingerRankings", capturedFingers, fingers, data);
        assertEquals("&#10008;", data.get("leftThumb"));
    }

    /**
     * Test getImage() returns empty string when BitmapFactory.decodeResource returns null.
     */
    @Test
    public void testGetImage_decodeResourceNull() {
        Context mockContext = mock(Context.class);
        when(mockContext.getResources()).thenReturn(null);
        TemplateService ts = new TemplateService(mockContext, masterDataService, identitySchemaRepository, globalParamRepository);
        String result = ReflectionTestUtils.invokeMethod(ts, "getImage", 123);
        assertEquals("", result);
    }

    /**
     * Test getDemographicData() skips field with id "IDSchemaVersion".
     */
    @Test
    public void testGetDemographicData_skipsIDSchemaVersion() {
        FieldSpecDto field = new FieldSpecDto();
        field.setId("IDSchemaVersion");
        RegistrationDto registrationDto = mock(RegistrationDto.class);
        Map<String, Object> data = ReflectionTestUtils.invokeMethod(templateService, "getDemographicData", field, registrationDto);
        assertNull(data);
    }

    /**
     * Test getDemographicData() returns null when value is null or empty.
     */
    @Test
    public void testGetDemographicData_valueNullOrEmpty() {
        FieldSpecDto field = new FieldSpecDto();
        field.setId("lastName");
        field.setLabel(Collections.singletonMap("en", "Last Name"));
        field.setType("simpleType");
        RegistrationDto registrationDto = mock(RegistrationDto.class);
        Map<String, Object> demoMap = new HashMap<>();
        demoMap.put("lastName", "");
        when(registrationDto.getDemographics()).thenReturn(demoMap);
        Map<String, Object> data = ReflectionTestUtils.invokeMethod(templateService, "getDemographicData", field, registrationDto);
        assertNull(data);
    }

    /**
     * Test getValue() throws NullPointerException when list contains null element.
     */
    @Test
    public void testGetValue_listWithNull() {
        List<SimpleType> list = new ArrayList<>();
        list.add(null);
        assertThrows(NullPointerException.class, () -> {
            ReflectionTestUtils.invokeMethod(templateService, "getValue", list);
        });
    }

    /**
     * Test getValue() with language filtering and no SimpleType matches.
     */
    @Test
    public void testGetValue_withLang_noSimpleTypeMatch() {
        SimpleType st = new SimpleType();
        st.setValue("val");
        st.setLanguage("fr");
        List<SimpleType> list = Collections.singletonList(st);
        assertEquals("", ReflectionTestUtils.invokeMethod(templateService, "getValue", list, "en"));
    }

    /**
     * Test getFieldLabel() returns "null" when label is missing for selected language.
     */
    @Test
    public void testGetFieldLabel_missingLanguage() {
        FieldSpecDto field = new FieldSpecDto();
        field.setLabel(Collections.singletonMap("fr", "Etiquette"));
        RegistrationDto registrationDto = mock(RegistrationDto.class);
        when(registrationDto.getSelectedLanguages()).thenReturn(Collections.singletonList("en"));
        Object label = ReflectionTestUtils.invokeMethod(templateService, "getFieldLabel", field, registrationDto);
        assertTrue(label.toString().contains("null"));
    }

    /**
     * Test getFieldValue() returns empty string when value is null.
     */
    @Test
    public void testGetFieldValue_nullValue() {
        FieldSpecDto field = new FieldSpecDto();
        field.setId("address");
        field.setType("simpleType");
        RegistrationDto registrationDto = mock(RegistrationDto.class);
        when(registrationDto.getDemographics()).thenReturn(Collections.singletonMap("address", null));
        when(registrationDto.getSelectedLanguages()).thenReturn(Collections.singletonList("en"));
        String value = ReflectionTestUtils.invokeMethod(templateService, "getFieldValue", field, registrationDto);
        assertEquals("", value);
    }

    /**
     * Test getDocumentData() returns null when document is not present.
     */
    @Test
    public void testGetDocumentData_noDocument() {
        FieldSpecDto field = new FieldSpecDto();
        field.setId("doc2");
        field.setLabel(Collections.singletonMap("en", "DocLabel2"));
        RegistrationDto registrationDto = mock(RegistrationDto.class);
        Map<String, DocumentDto> docs = new HashMap<>();
        when(registrationDto.getDocuments()).thenReturn(docs);
        when(registrationDto.getSelectedLanguages()).thenReturn(Collections.singletonList("en"));
        VelocityContext velocityContext = new VelocityContext();
        Map<String, Object> data = ReflectionTestUtils.invokeMethod(templateService, "getDocumentData", field, registrationDto, velocityContext);
        assertNull(data);
    }

    /**
     * Test generateQRCode() handles exception when Bitmap.compress throws.
     */
    @Test
    public void testGenerateQRCode_bitmapCompressThrows() {
        try (
                MockedStatic<QRCode> qrCodeStaticMock = mockStatic(QRCode.class);
                MockedStatic<Base64> base64Mock = mockStatic(Base64.class)
        ) {
            QRCode qrCodeMockInstance = mock(QRCode.class);
            Bitmap mockBitmap = mock(Bitmap.class);

            qrCodeStaticMock.when(() -> QRCode.from(anyString())).thenReturn(qrCodeMockInstance);
            when(qrCodeMockInstance.bitmap()).thenReturn(mockBitmap);
            when(mockBitmap.compress(any(), anyInt(), any(ByteArrayOutputStream.class))).thenThrow(new RuntimeException("compress fail"));

            VelocityContext ctx = new VelocityContext();
            RegistrationDto registrationDto = mock(RegistrationDto.class);
            when(registrationDto.getRId()).thenReturn("RID123");
            ReflectionTestUtils.invokeMethod(templateService, "generateQRCode", ctx, registrationDto);
            assertNull(ctx.get("QRCodeSource"));
        }
    }

    /**
     * Test setBiometricImage() handles exception when Bitmap.compress throws (Map version).
     */
    @Test
    public void testSetBiometricImage_bitmapCompressThrows() {
        try (MockedStatic<Base64> base64Mock = mockStatic(Base64.class)) {
            Map<String, Object> templateValues = new HashMap<>();
            Bitmap bitmap = mock(Bitmap.class);
            when(bitmap.compress(any(), anyInt(), any(ByteArrayOutputStream.class))).thenThrow(new RuntimeException("fail"));
            ReflectionTestUtils.invokeMethod(templateService, "setBiometricImage", templateValues, "Key", 0, bitmap, true);
            assertFalse(templateValues.containsKey("Key"));
        }
    }

    /**
     * Test setBiometricImage() handles exception when Bitmap.compress throws (VelocityContext version).
     */
    @Test
    public void testSetBiometricImage_velocityContextBitmapCompressThrows() {
        try (MockedStatic<Base64> base64Mock = mockStatic(Base64.class)) {
            VelocityContext ctx = new VelocityContext();
            Bitmap bitmap = mock(Bitmap.class);
            when(bitmap.compress(any(), anyInt(), any(ByteArrayOutputStream.class))).thenThrow(new RuntimeException("fail"));
            ReflectionTestUtils.invokeMethod(templateService, "setBiometricImage", ctx, "Key", bitmap, true);
            assertNull(ctx.get("Key"));
        }
    }

    /**
     * Test setBasicDetails() when UIN is null in demographics.
     */
    @Test
    public void testSetBasicDetails_nullUIN() {
        try (
                MockedStatic<QRCode> qrCodeStaticMock = mockStatic(QRCode.class);
                MockedStatic<Bitmap> bitmapStaticMock = mockStatic(Bitmap.class);
                MockedStatic<Base64> base64Mock = mockStatic(Base64.class)
        ) {
            QRCode qrCodeMock = mock(QRCode.class);
            qrCodeStaticMock.when(() -> QRCode.from(anyString())).thenReturn(qrCodeMock);

            Bitmap mockBitmap = mock(Bitmap.class);
            when(qrCodeMock.bitmap()).thenReturn(mockBitmap);
            bitmapStaticMock.when(() -> Bitmap.createBitmap(anyInt(), anyInt(), any(Bitmap.Config.class))).thenReturn(mockBitmap);
            when(mockBitmap.compress(any(), anyInt(), any(ByteArrayOutputStream.class))).thenReturn(true);
            base64Mock.when(() -> Base64.encodeToString(any(byte[].class), anyInt())).thenReturn("mocked_base64");

            Map<String, String> templateTitleValues = new HashMap<>();
            templateTitleValues.put("demographicInfo", "Demographic Info");
            templateTitleValues.put("documents", "Documents");
            templateTitleValues.put("bioMetrics", "Biometrics");
            RegistrationDto registrationDto = mock(RegistrationDto.class);
            when(registrationDto.getSelectedLanguages()).thenReturn(Collections.singletonList("en"));
            when(registrationDto.getRId()).thenReturn("RID123");
            when(registrationDto.getDemographics()).thenReturn(Collections.singletonMap("other", "notUIN"));
            when(masterDataService.getRegistrationCenterMachineDetails()).thenReturn(new CenterMachineDto());
            when(globalParamRepository.getCachedStringGlobalParam(anyString())).thenReturn("guidelines");
            VelocityContext velocityContext = new VelocityContext();
            ReflectionTestUtils.invokeMethod(templateService, "setBasicDetails", true, registrationDto, templateTitleValues, velocityContext);
            assertNull(velocityContext.get("UIN"));
        }
    }

    /**
     * Test getImage() returns empty string when Bitmap.compress throws exception.
     */
    @Test
    public void testGetImage_bitmapCompressThrows() {
        Context mockContext = mock(Context.class);
        Bitmap mockBitmap = mock(Bitmap.class);
        when(mockContext.getResources()).thenReturn(mock( android.content.res.Resources.class ));
        try (MockedStatic<BitmapFactory> bitmapFactoryMock = mockStatic(BitmapFactory.class)) {
            bitmapFactoryMock.when(() -> BitmapFactory.decodeResource(any(), anyInt())).thenReturn(mockBitmap);
            when(mockBitmap.compress(any(), anyInt(), any(ByteArrayOutputStream.class))).thenThrow(new RuntimeException("fail"));
            TemplateService ts = new TemplateService(mockContext, masterDataService, identitySchemaRepository, globalParamRepository);
            String result = ReflectionTestUtils.invokeMethod(ts, "getImage", 123);
            assertEquals("", result);
        }
    }

    /**
     * Test getValue() throws IndexOutOfBoundsException when list is empty.
     */
    @Test
    public void testGetValue_listEmpty() {
        List<SimpleType> list = new ArrayList<>();
        assertThrows(IndexOutOfBoundsException.class, () -> {
            ReflectionTestUtils.invokeMethod(templateService, "getValue", list);
        });
    }

    /**
     * Test getValue() throws ClassCastException for unhandled type.
     */
    @Test(expected = ClassCastException.class)
    public void testGetValue_unhandledType() {
        Object unknown = new Object();
        ReflectionTestUtils.invokeMethod(templateService, "getValue", unknown);
    }

    /**
     * Test getValue() with language filtering when fieldValue is a String.
     */
    @Test
    public void testGetValue_withLang_string() {
        assertEquals("foo", ReflectionTestUtils.invokeMethod(templateService, "getValue", "foo", "en"));
    }

    /**
     * Test getFieldValue() when field type is null.
     */
    @Test(expected = NullPointerException.class)
    public void testGetFieldValue_typeNull() {
        FieldSpecDto field = new FieldSpecDto();
        field.setId("id");
        RegistrationDto registrationDto = mock(RegistrationDto.class);
        when(registrationDto.getDemographics()).thenReturn(Collections.singletonMap("id", "val"));
        when(registrationDto.getSelectedLanguages()).thenReturn(Collections.singletonList("en"));
        String value = ReflectionTestUtils.invokeMethod(templateService, "getFieldValue", field, registrationDto);
        assertEquals("val", value);
    }

    /**
     * Test getFieldValue() returns empty string when selectedLanguages is empty.
     */
    @Test
    public void testGetFieldValue_selectedLanguagesEmpty() {
        FieldSpecDto field = new FieldSpecDto();
        field.setId("id");
        field.setType("simpleType");
        RegistrationDto registrationDto = mock(RegistrationDto.class);
        when(registrationDto.getDemographics()).thenReturn(Collections.singletonMap("id", "val"));
        when(registrationDto.getSelectedLanguages()).thenReturn(new ArrayList<>());
        String value = ReflectionTestUtils.invokeMethod(templateService, "getFieldValue", field, registrationDto);
        assertEquals("", value);
    }

    /**
     * Test getFieldLabel() throws NullPointerException when selectedLanguages is null.
     */
    @Test(expected = NullPointerException.class)
    public void testGetFieldLabel_selectedLanguagesNull() {
        FieldSpecDto field = new FieldSpecDto();
        field.setLabel(Collections.singletonMap("en", "Label"));
        RegistrationDto registrationDto = mock(RegistrationDto.class);
        when(registrationDto.getSelectedLanguages()).thenReturn(null);
        ReflectionTestUtils.invokeMethod(templateService, "getFieldLabel", field, registrationDto);
    }

    /**
     * Test getFieldLabel() throws NullPointerException when label is null.
     */
    @Test(expected = NullPointerException.class)
    public void testGetFieldLabel_labelNull() {
        FieldSpecDto field = new FieldSpecDto();
        field.setLabel(null);
        RegistrationDto registrationDto = mock(RegistrationDto.class);
        when(registrationDto.getSelectedLanguages()).thenReturn(Collections.singletonList("en"));
        ReflectionTestUtils.invokeMethod(templateService, "getFieldLabel", field, registrationDto);
    }

    /**
     * Test getDocumentData() throws NullPointerException when documents is null.
     */
    @Test(expected = NullPointerException.class)
    public void testGetDocumentData_documentsNull() {
        FieldSpecDto field = new FieldSpecDto();
        field.setId("doc3");
        field.setLabel(Collections.singletonMap("en", "DocLabel3"));
        RegistrationDto registrationDto = mock(RegistrationDto.class);
        when(registrationDto.getDocuments()).thenReturn(null);
        when(registrationDto.getSelectedLanguages()).thenReturn(Collections.singletonList("en"));
        VelocityContext velocityContext = new VelocityContext();
        Map<String, Object> data = ReflectionTestUtils.invokeMethod(templateService, "getDocumentData", field, registrationDto, velocityContext);
        assertNull(data);
    }

    /**
     * Test getTemplate() for acknowledgement mode (non-preview).
     */
    @Test
    public void testGetTemplate_acknowledgementMode() throws Exception {
        try (
                MockedStatic<Bitmap> bitmapStaticMock = mockStatic(Bitmap.class);
                MockedStatic<BitmapFactory> bitmapFactoryMock = mockStatic(BitmapFactory.class);
                MockedStatic<Base64> base64Mock = mockStatic(Base64.class)
        ) {
            Bitmap mockBitmap = mock(Bitmap.class);
            bitmapStaticMock.when(() -> Bitmap.createBitmap(anyInt(), anyInt(), any(Bitmap.Config.class)))
                    .thenReturn(mockBitmap);
            bitmapFactoryMock.when(() -> BitmapFactory.decodeResource(any(), anyInt()))
                    .thenReturn(mockBitmap);
            base64Mock.when(() -> Base64.encodeToString(any(byte[].class), anyInt()))
                    .thenReturn("mocked_base64");

            RegistrationDto registrationDto = mock(RegistrationDto.class);
            when(registrationDto.getSelectedLanguages()).thenReturn(Collections.singletonList("en"));
            when(masterDataService.getPreviewTemplateContent(anyString(), anyString())).thenReturn("<html>ACK</html>");
            when(identitySchemaRepository.getLatestSchemaVersion()).thenReturn(1.0);
            List<FieldSpecDto> fields = new ArrayList<>();
            FieldSpecDto field1 = new FieldSpecDto();
            field1.setType("simpleType");
            field1.setId("firstName");
            field1.setLabel(Collections.singletonMap("en", "First Name"));
            fields.add(field1);
            when(identitySchemaRepository.getProcessSpecFields(any(), any())).thenReturn(fields);
            Map<String, String> templateTitleValues = new HashMap<>();
            templateTitleValues.put("demographicInfo", "Demographic Info");
            templateTitleValues.put("documents", "Documents");
            templateTitleValues.put("bioMetrics", "Biometrics");
            when(registrationDto.getProcess()).thenReturn("REG");
            when(registrationDto.getDemographics()).thenReturn(Collections.singletonMap("firstName", "John"));
            when(registrationDto.getRId()).thenReturn("RID123");
            CenterMachineDto centerMachineDto = new CenterMachineDto();
            centerMachineDto.setCenterId("CENTER1");
            when(masterDataService.getRegistrationCenterMachineDetails()).thenReturn(centerMachineDto);
            when(globalParamRepository.getCachedStringGlobalParam(anyString())).thenReturn("Important guidelines");
            String result = templateService.getTemplate(registrationDto, false, templateTitleValues);
            assertTrue(result.contains("ACK"));
        }
    }

    /**
     * Test getBiometricData() for all branches (fingers, iris, face, exception photo) in non-preview mode.
     */
    @Test
    public void testGetBiometricData_allBranches_nonPreview() throws Exception {
        try (
                MockedStatic<BitmapFactory> bitmapFactoryMock = mockStatic(BitmapFactory.class);
                MockedStatic<Bitmap> bitmapStaticMock = mockStatic(Bitmap.class);
                MockedStatic<Base64> base64Mock = mockStatic(Base64.class);
                MockedStatic<UserInterfaceHelperService> uiHelperMock = mockStatic(UserInterfaceHelperService.class)
        ) {
            Bitmap mockBitmap = mock(Bitmap.class);
            bitmapFactoryMock.when(() -> BitmapFactory.decodeResource(any(), anyInt())).thenReturn(mockBitmap);
            bitmapStaticMock.when(() -> Bitmap.createBitmap(anyInt(), anyInt(), any(Bitmap.Config.class))).thenReturn(mockBitmap);
            base64Mock.when(() -> Base64.encodeToString(any(byte[].class), anyInt())).thenReturn("mocked_base64");
            uiHelperMock.when(() -> UserInterfaceHelperService.getIrisBitMap(any())).thenReturn(mockBitmap);
            uiHelperMock.when(() -> UserInterfaceHelperService.getFingerBitMap(any())).thenReturn(mockBitmap);
            uiHelperMock.when(() -> UserInterfaceHelperService.combineBitmaps(anyList(), any())).thenReturn(mockBitmap);
            uiHelperMock.when(() -> UserInterfaceHelperService.getFaceBitMap(any())).thenReturn(mockBitmap);

            FieldSpecDto field = new FieldSpecDto();
            field.setId("bio3");
            field.setLabel(Collections.singletonMap("en", "BioLabel3"));
            field.setSubType("applicant");
            field.setType("biometricsType");
            RegistrationDto registrationDto = mock(RegistrationDto.class);
            when(registrationDto.getSelectedLanguages()).thenReturn(Collections.singletonList("en"));

            List<BiometricsDto> rightSlab = new ArrayList<>();
            BiometricsDto right1 = new BiometricsDto(); right1.setBioSubType("Right IndexFinger"); right1.setBioValue("rf1"); right1.setQualityScore(88f);
            BiometricsDto right2 = new BiometricsDto(); right2.setBioSubType("Right MiddleFinger"); right2.setBioValue("rf2"); right2.setQualityScore(89f);
            rightSlab.add(right1); rightSlab.add(right2);

            List<BiometricsDto> leftSlab = new ArrayList<>();
            BiometricsDto left1 = new BiometricsDto(); left1.setBioSubType("Left LittleFinger"); left1.setBioValue("lf1"); left1.setQualityScore(80f);
            BiometricsDto left2 = new BiometricsDto(); left2.setBioSubType("Left IndexFinger"); left2.setBioValue("lf2"); left2.setQualityScore(81f);
            leftSlab.add(left1); leftSlab.add(left2);

            List<BiometricsDto> thumbs = new ArrayList<>();
            BiometricsDto thumb1 = new BiometricsDto(); thumb1.setBioSubType("Left Thumb"); thumb1.setBioValue("lthumb"); thumb1.setQualityScore(92f);
            BiometricsDto thumb2 = new BiometricsDto(); thumb2.setBioSubType("Right Thumb"); thumb2.setBioValue("rthumb"); thumb2.setQualityScore(93f);
            thumbs.add(thumb1); thumbs.add(thumb2);

            List<BiometricsDto> iris = new ArrayList<>();
            BiometricsDto irisLeft = new BiometricsDto(); irisLeft.setBioSubType("Left"); irisLeft.setBioValue("liris"); irisLeft.setQualityScore(90f);
            BiometricsDto irisRight = new BiometricsDto(); irisRight.setBioSubType("Right"); irisRight.setBioValue("riris"); irisRight.setQualityScore(91f);
            iris.add(irisLeft); iris.add(irisRight);

            List<BiometricsDto> face = new ArrayList<>();
            BiometricsDto faceDto = new BiometricsDto(); faceDto.setBioSubType("Face"); faceDto.setBioValue("faceval"); faceDto.setQualityScore(87f);
            face.add(faceDto);

            List<BiometricsDto> exception = new ArrayList<>();
            BiometricsDto exDto = new BiometricsDto(); exDto.setBioSubType("Exception"); exDto.setBioValue("exceptionval"); exDto.setQualityScore(60f);
            exception.add(exDto);

            when(registrationDto.getBestBiometrics(anyString(), eq(Modality.FINGERPRINT_SLAB_RIGHT))).thenReturn(rightSlab);
            when(registrationDto.getBestBiometrics(anyString(), eq(Modality.FINGERPRINT_SLAB_LEFT))).thenReturn(leftSlab);
            when(registrationDto.getBestBiometrics(anyString(), eq(Modality.FINGERPRINT_SLAB_THUMBS))).thenReturn(thumbs);
            when(registrationDto.getBestBiometrics(anyString(), eq(Modality.IRIS_DOUBLE))).thenReturn(iris);
            when(registrationDto.getBestBiometrics(anyString(), eq(Modality.FACE))).thenReturn(face);
            when(registrationDto.getBestBiometrics(anyString(), eq(Modality.EXCEPTION_PHOTO))).thenReturn(exception);

            VelocityContext velocityContext = new VelocityContext();
            Map<String, Object> data = ReflectionTestUtils.invokeMethod(templateService, "getBiometricData", field, registrationDto, false, velocityContext);
            assertNotNull(data);
            assertEquals("BioLabel3", data.get("label"));
            assertTrue(data.containsKey("LeftEye"));
            assertTrue(data.containsKey("RightEye"));
            assertTrue(data.containsKey("CapturedLeftSlap"));
            assertTrue(data.containsKey("CapturedRightSlap"));
            assertTrue(data.containsKey("CapturedThumbs"));
            assertTrue(data.containsKey("FaceImageSource"));
        }
    }

    /**
     * Test setBiometricImage() for "ApplicantImageSource" key in non-preview mode.
     */
    @Test
    public void testSetBiometricImage_applicantImageSource_nonPreview() {
        try (MockedStatic<Base64> base64Mock = mockStatic(Base64.class)) {
            base64Mock.when(() -> Base64.encodeToString(any(byte[].class), anyInt())).thenReturn("mocked_base64");
            Map<String, Object> templateValues = new HashMap<>();
            Bitmap bitmap = mock(Bitmap.class);
            when(bitmap.compress(any(), anyInt(), any(ByteArrayOutputStream.class))).thenAnswer((Answer<Boolean>) invocation -> {
                ByteArrayOutputStream os = invocation.getArgument(2);
                os.write("abc".getBytes());
                return true;
            });
            ReflectionTestUtils.invokeMethod(templateService, "setBiometricImage", templateValues, "ApplicantImageSource", 0, bitmap, false);
            assertTrue(templateValues.get("ApplicantImageSource").toString().contains("mocked_base64"));
        }
    }

    /**
     * Test setBiometricImage() with null bitmap and imagePath that triggers exception.
     */
    @Test
    public void testSetBiometricImage_bitmapNull_imagePath_throws() {
        Map<String, Object> templateValues = new HashMap<>();
        try (MockedStatic<BitmapFactory> bitmapFactoryMock = mockStatic(BitmapFactory.class)) {
            bitmapFactoryMock.when(() -> BitmapFactory.decodeResource(any(), anyInt())).thenThrow(new RuntimeException("fail"));
            ReflectionTestUtils.invokeMethod(templateService, "setBiometricImage", templateValues, "Key", 111, null, false);
            assertEquals("", templateValues.get("Key"));
        }
    }

    /**
     * Test getDocumentData() when document is present but label is missing for selected language.
     */
    @Test
    public void testGetDocumentData_labelMissingLanguage() {
        FieldSpecDto field = new FieldSpecDto();
        field.setId("doc1");
        field.setLabel(Collections.singletonMap("fr", "Etiquette"));
        RegistrationDto registrationDto = mock(RegistrationDto.class);
        Map<String, DocumentDto> docs = new HashMap<>();
        DocumentDto doc = new DocumentDto();
        doc.setType("passport");
        docs.put("doc1", doc);
        when(registrationDto.getDocuments()).thenReturn(docs);
        when(registrationDto.getSelectedLanguages()).thenReturn(Collections.singletonList("en"));
        VelocityContext velocityContext = new VelocityContext();
        Map<String, Object> data = ReflectionTestUtils.invokeMethod(templateService, "getDocumentData", field, registrationDto, velocityContext);
        assertNotNull(data);
        assertTrue(data.get("label").toString().contains("null"));
    }

    /**
     * Test setBasicDetails() when UIN is present in demographics.
     */
    @Test
    public void testSetBasicDetails_UINNotNull() {
        try (
                MockedStatic<QRCode> qrCodeStaticMock = mockStatic(QRCode.class);
                MockedStatic<Bitmap> bitmapStaticMock = mockStatic(Bitmap.class);
                MockedStatic<Base64> base64Mock = mockStatic(Base64.class)
        ) {
            QRCode qrCodeMock = mock(QRCode.class);
            qrCodeStaticMock.when(() -> QRCode.from(anyString())).thenReturn(qrCodeMock);

            Bitmap mockBitmap = mock(Bitmap.class);
            when(qrCodeMock.bitmap()).thenReturn(mockBitmap);
            bitmapStaticMock.when(() -> Bitmap.createBitmap(anyInt(), anyInt(), any(Bitmap.Config.class))).thenReturn(mockBitmap);
            when(mockBitmap.compress(any(), anyInt(), any(ByteArrayOutputStream.class))).thenReturn(true);
            base64Mock.when(() -> Base64.encodeToString(any(byte[].class), anyInt())).thenReturn("mocked_base64");

            Map<String, String> templateTitleValues = new HashMap<>();
            templateTitleValues.put("demographicInfo", "Demographic Info");
            templateTitleValues.put("documents", "Documents");
            templateTitleValues.put("bioMetrics", "Biometrics");
            RegistrationDto registrationDto = mock(RegistrationDto.class);
            when(registrationDto.getSelectedLanguages()).thenReturn(Collections.singletonList("en"));
            when(registrationDto.getRId()).thenReturn("RID123");
            Map<String, Object> demoMap = new HashMap<>();
            demoMap.put("UIN", "theUIN");
            when(registrationDto.getDemographics()).thenReturn(demoMap);
            CenterMachineDto center = new CenterMachineDto();
            center.setCenterId("centerX");
            when(masterDataService.getRegistrationCenterMachineDetails()).thenReturn(center);
            when(globalParamRepository.getCachedStringGlobalParam(anyString())).thenReturn("guidelines");
            VelocityContext velocityContext = new VelocityContext();
            ReflectionTestUtils.invokeMethod(templateService, "setBasicDetails", true, registrationDto, templateTitleValues, velocityContext);
            assertEquals("theUIN", velocityContext.get("UIN"));
            assertEquals("centerX", velocityContext.get("RegCenter"));
        }
    }
}
