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

import io.mosip.registration.clientmanager.dao.PermittedLocalConfigDao;
import io.mosip.registration.clientmanager.entity.PermittedLocalConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PermittedLocalConfigRepositoryTest {

    private static final String CONFIG_TYPE = "CONFIGURATION";

    @Mock
    private PermittedLocalConfigDao permittedLocalConfigDao;

    @InjectMocks
    private PermittedLocalConfigRepository repository;

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

    private PermittedLocalConfig buildConfig(String code) {
        PermittedLocalConfig config = new PermittedLocalConfig(code);
        config.setType(CONFIG_TYPE);
        config.setIsActive(true);
        return config;
    }

    @Test
    public void savePermittedConfigs_success() {
        List<PermittedLocalConfig> configs = Arrays.asList(
                buildConfig("idle_time"),
                buildConfig("fingerprint_threshold")
        );

        repository.savePermittedConfigs(configs);

        verify(permittedLocalConfigDao).insertAll(configs);
    }

    @Test
    public void savePermittedConfigs_handlesException() {
        List<PermittedLocalConfig> configs = Collections.singletonList(buildConfig("idle_time"));
        doThrow(new RuntimeException("insert fail")).when(permittedLocalConfigDao).insertAll(configs);

        repository.savePermittedConfigs(configs);

        verify(permittedLocalConfigDao).insertAll(configs);
        logMock.verify(() -> Log.e(Mockito.anyString(), Mockito.anyString(), any(Throwable.class)), times(1));
    }

    @Test
    public void getPermittedConfigsByType_success() {
        List<PermittedLocalConfig> expected = Arrays.asList(
                buildConfig("leftslap_threshold"),
                buildConfig("rightslap_threshold")
        );
        when(permittedLocalConfigDao.findByIsActiveTrueAndType(CONFIG_TYPE)).thenReturn(expected);

        List<PermittedLocalConfig> result = repository.getPermittedConfigsByType(CONFIG_TYPE);

        assertEquals(expected, result);
        verify(permittedLocalConfigDao).findByIsActiveTrueAndType(CONFIG_TYPE);
    }

    @Test
    public void getPermittedConfigsByType_handlesException() {
        when(permittedLocalConfigDao.findByIsActiveTrueAndType(CONFIG_TYPE)).thenThrow(new RuntimeException("db error"));

        List<PermittedLocalConfig> result = repository.getPermittedConfigsByType(CONFIG_TYPE);

        assertNull(result);
        logMock.verify(() -> Log.e(Mockito.anyString(), Mockito.anyString(), any(Throwable.class)), times(1));
    }
}