package io.mosip.registration.clientmanager.service;

import static org.junit.Assert.assertEquals;
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

import java.util.Arrays;

@RunWith(MockitoJUnitRunner.class)
public class LocalConfigServiceImplTest {

    @Mock
    private LocalConfigDAO localConfigDAO;

    @InjectMocks
    private LocalConfigServiceImpl localConfigService;

    @Test
    public void getLocalConfigurations_withDaoSetup_returnsDaoResult() {
        Map<String, String> expected = Map.of(
                "mosip.registration.idle_time", "300",
                "mosip.registration.theme", "dark");
        when(localConfigDAO.getLocalConfigurations()).thenReturn(expected);

        Map<String, String> result = localConfigService.getLocalConfigurations();

        assertEquals(expected, result);
        verify(localConfigDAO).getLocalConfigurations();
    }

    @Test
    public void modifyConfigurations_withValidMap_delegatesToDao() {
        Map<String, String> preferences = Map.of(
                "mosip.registration.idle_time", "600");

        localConfigService.modifyConfigurations(preferences);

        verify(localConfigDAO).modifyConfigurations(preferences);
    }

    @Test
    public void getPermittedConfiguration_withRegistrationConstant_returnsPermittedList() {
        List<String> permitted = List.of("mosip.registration.idle_time", "mosip.registration.theme");
        when(localConfigDAO.getPermittedConfigurations(RegistrationConstants.PERMITTED_CONFIG_TYPE))
                .thenReturn(permitted);

        List<String> result = localConfigService.getPermittedConfiguration();

        assertEquals(permitted, result);
        verify(localConfigDAO).getPermittedConfigurations(RegistrationConstants.PERMITTED_CONFIG_TYPE);
    }

    @Test
    public void getValue_withKeyParam_delegatesToDao() {
        when(localConfigDAO.getValue("mosip.registration.theme", "CONFIGURATION")).thenReturn("dark");

        String result = localConfigService.getValue("mosip.registration.theme", "CONFIGURATION");

        assertEquals("dark", result);
        verify(localConfigDAO).getValue("mosip.registration.theme", "CONFIGURATION");
    }

    @Test
    public void getPermittedJobs_withPermittedJobType_returnsPermittedJobList() {
        List<String> permittedJobs = List.of("MasterDataSyncJob", "PacketStatusSyncJob");
        when(localConfigDAO.getPermittedConfigurations(RegistrationConstants.PERMITTED_JOB_TYPE))
                .thenReturn(permittedJobs);

        List<String> result = localConfigService.getPermittedJobs();

        assertEquals(permittedJobs, result);
        verify(localConfigDAO).getPermittedConfigurations(RegistrationConstants.PERMITTED_JOB_TYPE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void modifyJob_withNullName_throwsIllegalArgumentException() {
        localConfigService.modifyJob(null, "0 0 12 * * ?");
    }

    @Test(expected = IllegalArgumentException.class)
    public void modifyJob_withEmptyName_throwsIllegalArgumentException() {
        localConfigService.modifyJob("   ", "0 0 12 * * ?");
    }

    @Test(expected = IllegalArgumentException.class)
    public void modifyJob_withNullValue_throwsIllegalArgumentException() {
        localConfigService.modifyJob("MasterDataSyncJob", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void modifyJob_withEmptyValue_throwsIllegalArgumentException() {
        localConfigService.modifyJob("MasterDataSyncJob", "   ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void modifyJob_withInvalidCronExpression_throwsIllegalArgumentException() {
        localConfigService.modifyJob("MasterDataSyncJob", "NOT_A_VALID_CRON");
    }

    @Test(expected = IllegalArgumentException.class)
    public void modifyJob_withNonPermittedJob_throwsIllegalArgumentException() {
        when(localConfigDAO.getPermittedConfigurations(RegistrationConstants.PERMITTED_JOB_TYPE))
                .thenReturn(Arrays.asList("PacketStatusSyncJob"));

        localConfigService.modifyJob("MasterDataSyncJob", "0 0 12 * * ?");
    }

    @Test
    public void modifyJob_withValidCronAndPermittedJob_delegatesToDao() {
        String jobName = "MasterDataSyncJob";
        String cronExpr = "0 0 12 * * ?";
        when(localConfigDAO.getPermittedConfigurations(RegistrationConstants.PERMITTED_JOB_TYPE))
                .thenReturn(Arrays.asList(jobName));

        localConfigService.modifyJob(jobName, cronExpr);

        verify(localConfigDAO).modifyJob(jobName, cronExpr);
    }
}