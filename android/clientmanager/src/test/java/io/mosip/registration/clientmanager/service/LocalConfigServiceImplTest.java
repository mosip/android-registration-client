package io.mosip.registration.clientmanager.service;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dao.LocalConfigDAO;

@RunWith(MockitoJUnitRunner.class)
public class LocalConfigServiceImplTest {

    @Mock
    private LocalConfigDAO localConfigDAO;

    @InjectMocks
    private LocalConfigServiceImpl localConfigService;

    @Test
    public void testGetLocalConfigurations_returnsDaoResult() {
        Map<String, String> expected = Map.of(
                "mosip.registration.idle_time", "300",
                "mosip.registration.theme", "dark");
        when(localConfigDAO.getLocalConfigurations()).thenReturn(expected);

        Map<String, String> result = localConfigService.getLocalConfigurations();

        assertEquals(expected, result);
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

        assertEquals(permitted, result);
        verify(localConfigDAO).getPermittedConfigurations(RegistrationConstants.PERMITTED_CONFIG_TYPE);
    }

    @Test
    public void testGetValue_delegatesToDao() {
        when(localConfigDAO.getValue("mosip.registration.idle_time", RegistrationConstants.PERMITTED_CONFIG_TYPE))
                .thenReturn("300");

        String result = localConfigService.getValue("mosip.registration.idle_time", RegistrationConstants.PERMITTED_CONFIG_TYPE);

        assertEquals("300", result);
        verify(localConfigDAO).getValue("mosip.registration.idle_time", RegistrationConstants.PERMITTED_CONFIG_TYPE);
    }

    @Test
    public void testGetPermittedJobs_delegatesToDao() {
        List<String> jobs = List.of("SyncJob", "PacketStatusSyncJob");
        when(localConfigDAO.getPermittedConfigurations(RegistrationConstants.PERMITTED_JOB_TYPE)).thenReturn(jobs);

        List<String> result = localConfigService.getPermittedJobs();

        assertEquals(jobs, result);
        verify(localConfigDAO).getPermittedConfigurations(RegistrationConstants.PERMITTED_JOB_TYPE);
    }

    @Test
    public void testModifyJob_success_validJobAndCron() {
        List<String> permitted = List.of("SyncJob");
        when(localConfigDAO.getPermittedConfigurations(RegistrationConstants.PERMITTED_JOB_TYPE)).thenReturn(permitted);

        localConfigService.modifyJob("SyncJob", "0 0 * * * ?");

        verify(localConfigDAO).modifyJob("SyncJob", "0 0 * * * ?");
    }

    @Test
    public void testModifyJob_throwsWhenJobNameNull() {
        assertThrows(IllegalArgumentException.class, () ->
                localConfigService.modifyJob(null, "0 0 * * * ?"));
        verify(localConfigDAO, org.mockito.Mockito.never()).modifyJob(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    public void testModifyJob_throwsWhenJobNameEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
                localConfigService.modifyJob("  ", "0 0 * * * ?"));
        verify(localConfigDAO, org.mockito.Mockito.never()).modifyJob(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    public void testModifyJob_throwsWhenCronNull() {
        assertThrows(IllegalArgumentException.class, () ->
                localConfigService.modifyJob("SyncJob", null));
        verify(localConfigDAO, org.mockito.Mockito.never()).modifyJob(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    public void testModifyJob_throwsWhenCronEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
                localConfigService.modifyJob("SyncJob", ""));
        verify(localConfigDAO, org.mockito.Mockito.never()).modifyJob(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    public void testModifyJob_throwsWhenCronInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
                localConfigService.modifyJob("SyncJob", "not-a-cron"));
        verify(localConfigDAO, org.mockito.Mockito.never()).modifyJob(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    public void testModifyJob_throwsWhenJobNotPermitted() {
        List<String> permitted = List.of("SyncJob");
        when(localConfigDAO.getPermittedConfigurations(RegistrationConstants.PERMITTED_JOB_TYPE)).thenReturn(permitted);

        assertThrows(IllegalArgumentException.class, () ->
                localConfigService.modifyJob("OtherJob", "0 0 * * * ?"));
        verify(localConfigDAO, org.mockito.Mockito.never()).modifyJob(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
    }
}