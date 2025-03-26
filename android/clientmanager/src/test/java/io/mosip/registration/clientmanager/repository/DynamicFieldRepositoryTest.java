package io.mosip.registration.clientmanager.repository;

import io.mosip.registration.clientmanager.dao.DynamicFieldDao;
import io.mosip.registration.clientmanager.dto.registration.GenericValueDto;
import io.mosip.registration.clientmanager.entity.DynamicField;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
public class DynamicFieldRepositoryTest {

    @Mock
    private DynamicFieldDao dynamicFieldDao;

    @Mock
    private JSONObject jsonObject;

    @InjectMocks
    private DynamicFieldRepository dynamicFieldRepository;

    private static final String FIELD_NAME = "residenceStatus";
    private static final String LANG_CODE = "en";
    private static final String CODE = "FR";

    private DynamicField mockDynamicField;

    @Before
    public void setUp() throws JSONException {
        MockitoAnnotations.initMocks(this);

        if (dynamicFieldRepository == null) {
            dynamicFieldRepository = new DynamicFieldRepository(dynamicFieldDao);
        }

        // Mock a valid DynamicField entity
        String jsonValues = "[{\"code\":\"FR\",\"value\":\"Foreign\"}, {\"code\":\"NFR\",\"value\":\"Non-Foreign\"}]";
        mockDynamicField = new DynamicField("1");
        mockDynamicField.setName(FIELD_NAME);
        mockDynamicField.setLangCode(LANG_CODE);
        mockDynamicField.setValueJson(jsonValues);

        // Mock JSONObject for saveDynamicField
        when(jsonObject.getString("id")).thenReturn("1");
        when(jsonObject.getString("dataType")).thenReturn("String");
        when(jsonObject.getString("name")).thenReturn(FIELD_NAME);
        when(jsonObject.getString("langCode")).thenReturn(LANG_CODE);
        when(jsonObject.getBoolean("isActive")).thenReturn(true);
        when(jsonObject.getJSONArray("fieldVal")).thenReturn(new JSONArray(jsonValues));
    }

    @Test
    public void testGetDynamicValues_Success() {
        when(dynamicFieldDao.findDynamicFieldByName(FIELD_NAME, LANG_CODE)).thenReturn(mockDynamicField);

        List<GenericValueDto> result = dynamicFieldRepository.getDynamicValues(FIELD_NAME, LANG_CODE);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Foreign", result.get(0).getName());
        assertEquals("FR", result.get(0).getCode());
    }

    @Test
    public void testGetDynamicValues_NoData() {
        when(dynamicFieldDao.findDynamicFieldByName(FIELD_NAME, LANG_CODE)).thenReturn(null);

        List<GenericValueDto> result = dynamicFieldRepository.getDynamicValues(FIELD_NAME, LANG_CODE);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetDynamicValuesByCode_Success() {
        List<DynamicField> dynamicFields = new ArrayList<>();
        dynamicFields.add(mockDynamicField);

        when(dynamicFieldDao.findAllDynamicValuesByName(FIELD_NAME)).thenReturn(dynamicFields);

        List<GenericValueDto> result = dynamicFieldRepository.getDynamicValuesByCode(FIELD_NAME, CODE);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Foreign", result.get(0).getName());
        assertEquals("FR", result.get(0).getCode());
    }

    @Test
    public void testGetDynamicValuesByCode_NoMatch() {
        List<DynamicField> dynamicFields = new ArrayList<>();
        dynamicFields.add(mockDynamicField);

        when(dynamicFieldDao.findAllDynamicValuesByName(FIELD_NAME)).thenReturn(dynamicFields);

        List<GenericValueDto> result = dynamicFieldRepository.getDynamicValuesByCode(FIELD_NAME, "INVALID");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetDynamicValuesByCode_NoData() {
        when(dynamicFieldDao.findAllDynamicValuesByName(FIELD_NAME)).thenReturn(Collections.emptyList());

        List<GenericValueDto> result = dynamicFieldRepository.getDynamicValuesByCode(FIELD_NAME, CODE);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testSaveDynamicField_Success() throws JSONException {
        dynamicFieldRepository.saveDynamicField(jsonObject);

        verify(dynamicFieldDao, times(1)).insert(any(DynamicField.class));
    }

    @Test(expected = JSONException.class)
    public void testSaveDynamicField_InvalidJson() throws JSONException {
        when(jsonObject.getString("id")).thenThrow(new JSONException("Invalid key"));

        dynamicFieldRepository.saveDynamicField(jsonObject);
    }
}
