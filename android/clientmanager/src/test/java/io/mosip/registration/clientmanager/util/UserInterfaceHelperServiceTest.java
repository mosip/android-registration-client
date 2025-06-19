package io.mosip.registration.clientmanager.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dto.registration.BiometricsDto;
import io.mosip.registration.clientmanager.dto.uispec.ConditionalBioAttrDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.dto.uispec.RequiredDto;
import io.mosip.registration.keymanager.util.CryptoUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserInterfaceHelperServiceTest {

    private UserInterfaceHelperService service;

    @Mock
    private Bitmap mockMissingImage;

    @Mock
    private Bitmap mockResultBitmap;


    @Before
    public void setUp() {
        service = new UserInterfaceHelperService(mock(Context.class));
    }

    @Test
    public void testIsFieldVisible_DefaultTrue() {
        FieldSpecDto dto = new FieldSpecDto();
        assertTrue(service.isFieldVisible(dto, new HashMap<>()));
    }

    @Test
    public void testIsFieldVisible_WithMvelExpression() {
        FieldSpecDto dto = new FieldSpecDto();
        dto.setId("123");
        dto.setRequired(Boolean.valueOf("true"));
        Map<String, Object> data = new HashMap<>();
        data.put("age", 25);
        assertTrue(service.isFieldVisible(dto, data));
    }

    @Test
    public void testIsRequiredField_NullRequired() {
        FieldSpecDto dto = new FieldSpecDto();
        assertFalse(service.isRequiredField(dto, new HashMap<>()));
    }

    @Test
    public void testIsRequiredField_TrueRequired() {
        FieldSpecDto dto = new FieldSpecDto();
        dto.setRequired(true);
        assertTrue(service.isRequiredField(dto, new HashMap<>()));
    }

    @Test
    public void testIsRequiredField_WithRequiredOnMvel() {
        FieldSpecDto dto = new FieldSpecDto();
        RequiredDto req = new RequiredDto();
        req.setEngine("MVEL");
        req.setExpr("identity.age > 18");
        dto.setRequiredOn(Collections.singletonList(req));
        Map<String, Object> data = new HashMap<>();
        data.put("age", 25);
        assertTrue(service.isRequiredField(dto, data));
    }

    @Test
    public void testGetRequiredBioAttributes_NotRequired() {
        FieldSpecDto dto = new FieldSpecDto();
        dto.setRequired(false);
        assertTrue(service.getRequiredBioAttributes(dto, new HashMap<>()).isEmpty());
    }

    @Test
    public void testGetRequiredBioAttributes_WithConditional() {
        FieldSpecDto dto = new FieldSpecDto();
        dto.setRequired(true);
        ConditionalBioAttrDto cond = new ConditionalBioAttrDto();
        cond.setAgeGroup("ADULT");
        cond.setBioAttributes(Arrays.asList("FINGER", "FACE"));
        dto.setConditionalBioAttributes(Collections.singletonList(cond));
        Map<String, Object> data = new HashMap<>();
        data.put(RegistrationConstants.AGE_GROUP, "ADULT");
        assertEquals(Arrays.asList("FINGER", "FACE"), service.getRequiredBioAttributes(dto, data));
    }

    @Test
    public void testGetRequiredBioAttributes_NoConditional() {
        FieldSpecDto dto = new FieldSpecDto();
        dto.setRequired(true);
        dto.setBioAttributes(Arrays.asList("FINGER"));
        assertEquals(Arrays.asList("FINGER"), service.getRequiredBioAttributes(dto, new HashMap<>()));
    }

    @Test
    public void testGetConditionalBioAttributes_Match() {
        FieldSpecDto dto = new FieldSpecDto();
        ConditionalBioAttrDto cond = new ConditionalBioAttrDto();
        cond.setAgeGroup("ADULT");
        dto.setConditionalBioAttributes(Collections.singletonList(cond));
        Map<String, Object> data = new HashMap<>();
        data.put(RegistrationConstants.AGE_GROUP, "ADULT");
        assertEquals(cond, service.getConditionalBioAttributes(dto, data));
    }

    @Test
    public void testGetConditionalBioAttributes_Empty() {
        FieldSpecDto dto = new FieldSpecDto();
        dto.setConditionalBioAttributes(Collections.emptyList());
        assertNull(service.getConditionalBioAttributes(dto, new HashMap<>()));
    }

    @Test
    public void testGetConditionalBioAttributes_AllFallback() {
        FieldSpecDto dto = new FieldSpecDto();
        ConditionalBioAttrDto cond = new ConditionalBioAttrDto();
        cond.setAgeGroup("ALL");
        dto.setConditionalBioAttributes(Collections.singletonList(cond));
        Map<String, Object> data = new HashMap<>();
        data.put(RegistrationConstants.AGE_GROUP, "CHILD");
        assertEquals(cond, service.getConditionalBioAttributes(dto, data));
    }

    @Test
    public void testEvaluateMvel_True() {
        Map<String, Object> data = new HashMap<>();
        data.put("age", 25);
        assertTrue(service.evaluateMvel("identity.age > 18", data));
    }

    @Test
    public void testEvaluateMvel_Exception() {
        try (MockedStatic<Log> logMock = mockStatic(Log.class)) {
            assertFalse(service.evaluateMvel("invalid syntax", new HashMap<>()));
        }
    }

    @Test
    public void testEvaluateValidationExpression_True() {
        Map<String, Boolean> data = new HashMap<>();
        data.put("a", true);
        assertTrue(service.evaluateValidationExpression("a == true", data));
    }

    @Test
    public void testEvaluateValidationExpression_Exception() {
        try (MockedStatic<Log> logMock = mockStatic(Log.class)) {
            assertFalse(service.evaluateValidationExpression("invalid", new HashMap<>()));
        }
    }

    @Test
    public void testGetFaceBitMap_NullInput() {
        assertNull(service.getFaceBitMap(null));
    }

    @Test
    public void testGetFaceBitMap_IOException() {
        BiometricsDto dto = new BiometricsDto();
        dto.setBioValue("invalid");
        try (MockedStatic<CryptoUtil> cryptoMock = mockStatic(CryptoUtil.class);
             MockedStatic<Log> logMock = mockStatic(Log.class)) {
            assertNull(service.getFaceBitMap(dto));
        }
    }

    @Test
    public void testGetFingerBitMap_NullInput() {
        assertNull(service.getFingerBitMap(null));
    }

    @Test
    public void testGetFingerBitMap_IOException() {
        BiometricsDto dto = new BiometricsDto();
        dto.setBioValue("invalid");
        try (MockedStatic<CryptoUtil> cryptoMock = mockStatic(CryptoUtil.class);
             MockedStatic<Log> logMock = mockStatic(Log.class)) {
            assertNull(service.getFingerBitMap(dto));
        }
    }

    @Test
    public void testGetIrisBitMap_NullInput() {
        assertNull(service.getIrisBitMap(null));
    }

    @Test
    public void testGetIrisBitMap_IOException() {
        BiometricsDto dto = new BiometricsDto();
        dto.setBioValue("invalid");
        try (MockedStatic<CryptoUtil> cryptoMock = mockStatic(CryptoUtil.class);
             MockedStatic<Log> logMock = mockStatic(Log.class)) {
            assertNull(service.getIrisBitMap(dto));
        }
    }

    @Test
    public void testGetBytes() throws IOException {
        byte[] data = new byte[]{1,2,3,4,5};
        InputStream is = new ByteArrayInputStream(data);
        byte[] result = service.getBytes(is);
        assertArrayEquals(data, result);
    }

    @Test
    public void evaluateMvel_TrueExpression_Test() {
        Map<String, Object> dataContext = new HashMap<>();
        dataContext.put("age", 25);

        boolean result = UserInterfaceHelperService.evaluateMvel("identity.age > 18", dataContext);

        assertTrue(result);
    }

    @Test
    public void evaluateMvel_FalseExpression_Test() {

        Map<String, Object> dataContext = new HashMap<>();
        dataContext.put("age", 15);

        boolean result = UserInterfaceHelperService.evaluateMvel("identity.age > 18", dataContext);

        assertFalse(result);
    }

    @Test
    public void test_combine_empty_list_of_images() {
        try (MockedStatic<Bitmap> mockedBitmap = mockStatic(Bitmap.class)) {

            when(mockMissingImage.getWidth()).thenReturn(50);
            when(mockMissingImage.getHeight()).thenReturn(50);

            mockedBitmap.when(() -> Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888))
                    .thenReturn(mockMissingImage);

            when(mockResultBitmap.getWidth()).thenReturn(0);
            when(mockResultBitmap.getHeight()).thenReturn(0);

            mockedBitmap.when(() -> Bitmap.createBitmap(0, 0, Bitmap.Config.ARGB_8888))
                    .thenReturn(mockResultBitmap);

            List<Bitmap> emptyImagesList = new ArrayList<>();

            Bitmap result = UserInterfaceHelperService.combineBitmaps(emptyImagesList, mockMissingImage);

            assertNotNull(result);
            assertEquals(0, result.getWidth());
            assertEquals(0, result.getHeight());
        }
    }

    @Test
    public void testIsFieldVisible_DefaultVisible() {
        FieldSpecDto dto = new FieldSpecDto();
        assertTrue(UserInterfaceHelperService.isFieldVisible(dto, new HashMap<>()));
    }

    @Test
    public void testIsFieldVisible_MvelTrue() {
        FieldSpecDto dto = new FieldSpecDto();
        RequiredDto visible = new RequiredDto();
        visible.setEngine("MVEL");
        visible.setExpr("identity.age > 18");
        dto.setVisible(visible);
        Map<String, Object> data = new HashMap<>();
        data.put("age", 25);
        assertTrue(UserInterfaceHelperService.isFieldVisible(dto, data));
    }

    @Test
    public void testIsFieldVisible_MvelFalse() {
        FieldSpecDto dto = new FieldSpecDto();
        RequiredDto visible = new RequiredDto();
        visible.setEngine("MVEL");
        visible.setExpr("identity.age > 18");
        dto.setVisible(visible);
        Map<String, Object> data = new HashMap<>();
        data.put("age", 10);
        assertFalse(UserInterfaceHelperService.isFieldVisible(dto, data));
    }

    @Test
    public void testIsFieldVisible_NullExpr() {
        FieldSpecDto dto = new FieldSpecDto();
        RequiredDto visible = new RequiredDto();
        visible.setEngine("MVEL");
        visible.setExpr(null);
        dto.setVisible(visible);
        assertTrue(UserInterfaceHelperService.isFieldVisible(dto, new HashMap<>()));
    }

    @Test
    public void testIsFieldVisible_NonMvelEngine() {
        FieldSpecDto dto = new FieldSpecDto();
        RequiredDto visible = new RequiredDto();
        visible.setEngine("OTHER");
        visible.setExpr("identity.age > 18");
        dto.setVisible(visible);
        assertTrue(UserInterfaceHelperService.isFieldVisible(dto, new HashMap<>()));
    }

    @Test
    public void testCombineBitmaps_EmptyList() {
        Bitmap missingImage = mock(Bitmap.class);
        when(missingImage.getWidth()).thenReturn(50);
        when(missingImage.getHeight()).thenReturn(50);

        try (MockedStatic<Bitmap> mockedBitmap = mockStatic(Bitmap.class)) {
            Bitmap result = mock(Bitmap.class);
            mockedBitmap.when(() -> Bitmap.createBitmap(0, 0, Bitmap.Config.ARGB_8888)).thenReturn(result);

            Bitmap combined = UserInterfaceHelperService.combineBitmaps(Collections.emptyList(), missingImage);

            assertNotNull(combined);
        }
    }

}