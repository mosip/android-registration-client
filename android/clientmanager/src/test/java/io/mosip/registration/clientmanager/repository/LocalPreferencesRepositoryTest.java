package io.mosip.registration.clientmanager.repository;

import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dao.LocalPreferencesDao;
import io.mosip.registration.clientmanager.entity.LocalPreferences;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LocalPreferencesRepositoryTest {

    private static final String IDLE_TIME_KEY = "mosip.registration.idle_time";
    private static final String LEFTSLAP_THRESHOLD_KEY = "mosip.registration.leftslap_fingerprint_threshold";

    @Mock
    private LocalPreferencesDao localPreferencesDao;

    @InjectMocks
    private LocalPreferencesRepository repository;

    private MockedStatic<Log> logMock;

    @Before
    public void setUp() {
        logMock = Mockito.mockStatic(Log.class);
        logMock.when(() -> Log.e(Mockito.anyString(), Mockito.anyString(), Mockito.any(Throwable.class))).thenReturn(0);
        logMock.when(() -> Log.e(Mockito.anyString(), Mockito.anyString())).thenReturn(0);
    }

    @After
    public void tearDown() {
        if (logMock != null) {
            logMock.close();
        }
    }

    private LocalPreferences buildPreference(String id, String name, String value) {
        LocalPreferences pref = new LocalPreferences(id);
        pref.setName(name);
        pref.setVal(value);
        pref.setConfigType(RegistrationConstants.PERMITTED_CONFIG_TYPE);
        pref.setIsDeleted(false);
        return pref;
    }

    @Test
    public void getLocalConfigurations_happyPath() {
        List<LocalPreferences> prefs = Arrays.asList(
                buildPreference("1", IDLE_TIME_KEY, "300"),
                buildPreference("2", LEFTSLAP_THRESHOLD_KEY, "40")
        );
        when(localPreferencesDao.findByIsDeletedFalseAndConfigType(RegistrationConstants.PERMITTED_CONFIG_TYPE)).thenReturn(prefs);

        Map<String, String> result = repository.getLocalConfigurations();

        assertEquals(2, result.size());
        assertEquals("300", result.get(IDLE_TIME_KEY));
        assertEquals("40", result.get(LEFTSLAP_THRESHOLD_KEY));
        verify(localPreferencesDao).findByIsDeletedFalseAndConfigType(RegistrationConstants.PERMITTED_CONFIG_TYPE);
    }

    @Test
    public void getLocalConfigurations_handlesNullList() {
        when(localPreferencesDao.findByIsDeletedFalseAndConfigType(RegistrationConstants.PERMITTED_CONFIG_TYPE)).thenReturn(null);

        Map<String, String> result = repository.getLocalConfigurations();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void getLocalConfigurations_handlesException() {
        when(localPreferencesDao.findByIsDeletedFalseAndConfigType(RegistrationConstants.PERMITTED_CONFIG_TYPE))
                .thenThrow(new RuntimeException("db error"));

        Map<String, String> result = repository.getLocalConfigurations();

        assertNotNull(result);
        assertEquals(Collections.emptyMap(), result);
    }

    @Test
    public void findByIsDeletedFalseAndName_happyPath() {
        LocalPreferences pref = buildPreference("1", IDLE_TIME_KEY, "300");
        when(localPreferencesDao.findByIsDeletedFalseAndName(IDLE_TIME_KEY)).thenReturn(pref);

        LocalPreferences result = repository.findByIsDeletedFalseAndName(IDLE_TIME_KEY);

        assertEquals(pref, result);
        verify(localPreferencesDao).findByIsDeletedFalseAndName(IDLE_TIME_KEY);
    }

    @Test
    public void findByIsDeletedFalseAndName_handlesException() {
        when(localPreferencesDao.findByIsDeletedFalseAndName(IDLE_TIME_KEY))
                .thenThrow(new RuntimeException("db error"));

        LocalPreferences result = repository.findByIsDeletedFalseAndName(IDLE_TIME_KEY);

        assertNull(result);
    }

    @Test
    public void save_happyPath() {
        LocalPreferences pref = buildPreference("1", LEFTSLAP_THRESHOLD_KEY, "40");

        repository.save(pref);

        verify(localPreferencesDao).insert(pref);
    }

    @Test
    public void save_handlesException() {
        LocalPreferences pref = buildPreference("1", LEFTSLAP_THRESHOLD_KEY, "40");
        doThrow(new RuntimeException("insert fail")).when(localPreferencesDao).insert(pref);

        repository.save(pref);

        verify(localPreferencesDao).insert(pref);
    }

    @Test
    public void update_happyPath() {
        LocalPreferences pref = buildPreference("1", IDLE_TIME_KEY, "300");

        repository.update(pref);

        verify(localPreferencesDao).update(pref);
    }

    @Test
    public void update_handlesException() {
        LocalPreferences pref = buildPreference("1", IDLE_TIME_KEY, "300");
        doThrow(new RuntimeException("update fail")).when(localPreferencesDao).update(pref);

        repository.update(pref);

        verify(localPreferencesDao).update(pref);
    }

    @Test
    public void delete_happyPath() {
        LocalPreferences pref = buildPreference("1", LEFTSLAP_THRESHOLD_KEY, "40");

        repository.delete(pref);

        verify(localPreferencesDao).deleteByName(LEFTSLAP_THRESHOLD_KEY);
    }

    @Test
    public void delete_handlesException() {
        LocalPreferences pref = buildPreference("1", LEFTSLAP_THRESHOLD_KEY, "40");
        doThrow(new RuntimeException("delete fail")).when(localPreferencesDao).deleteByName(eq(LEFTSLAP_THRESHOLD_KEY));

        repository.delete(pref);

        verify(localPreferencesDao).deleteByName(LEFTSLAP_THRESHOLD_KEY);
    }
}