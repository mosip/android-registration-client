package io.mosip.registration.clientmanager.service;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dao.LocalConfigDAO;

@RunWith(MockitoJUnitRunner.class)
public class LocalConfigServiceImplTest {

    @Mock
    private LocalConfigDAO localConfigDAO;

    @InjectMocks
    private LocalConfigServiceImpl localConfigService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetLocalConfigurations_returnsDaoResult() {
        Map<String, String> expected = Map.of(
                "mosip.registration.idle_time", "300",
                "mosip.registration.theme", "dark");
        when(localConfigDAO.getLocalConfigurations()).thenReturn(expected);

        Map<String, String> result = localConfigService.getLocalConfigurations();

        assertSame(expected, result);
        verify(localConfigDAO).getLocalConfigurations();
    }

    @Test
    public void testModifyConfigurations_delegatesToDao() {
        Map<String, String> preferences = Map.of(
                "mosip.registration.idle_time", "600");

        localConfigService.modifyConfigurations(preferences);

        verify(localConfigDAO).modifyConfigurations(preferences);
    }

    @Test
    public void testGetPermittedConfiguration_usesRegistrationConstant() {
        List<String> permitted = List.of("mosip.registration.idle_time", "mosip.registration.theme");
        when(localConfigDAO.getPermittedConfigurations(RegistrationConstants.PERMITTED_CONFIG_TYPE))
                .thenReturn(permitted);

        List<String> result = localConfigService.getPermittedConfiguration();

        assertSame(permitted, result);
        verify(localConfigDAO).getPermittedConfigurations(RegistrationConstants.PERMITTED_CONFIG_TYPE);
    }
}