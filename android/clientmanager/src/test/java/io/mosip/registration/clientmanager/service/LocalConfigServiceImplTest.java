package io.mosip.registration.clientmanager.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
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
        String name = "test.name";
        String configType = "CONFIG";
        String expectedValue = "testValue";
        when(localConfigDAO.getValue(name, configType)).thenReturn(expectedValue);

        String result = localConfigService.getValue(name, configType);

        assertEquals(expectedValue, result);
        verify(localConfigDAO).getValue(name, configType);
    }

    @Test
    public void testGetPermittedJobs_delegatesToDao() {
        List<String> permittedJobs = Arrays.asList("job1", "job2", "job3");
        when(localConfigDAO.getPermittedConfigurations(RegistrationConstants.PERMITTED_JOB_TYPE))
                .thenReturn(permittedJobs);

        List<String> result = localConfigService.getPermittedJobs();

        assertEquals(permittedJobs, result);
        verify(localConfigDAO).getPermittedConfigurations(RegistrationConstants.PERMITTED_JOB_TYPE);
    }

    @Test
    public void testModifyJob_nullName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            localConfigService.modifyJob(null, "0 0 12 * * ?");
        });

        verify(localConfigDAO, never()).modifyJob(anyString(), anyString());
    }

    @Test
    public void testModifyJob_emptyName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            localConfigService.modifyJob("", "0 0 12 * * ?");
        });

        verify(localConfigDAO, never()).modifyJob(anyString(), anyString());
    }

    @Test
    public void testModifyJob_blankName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            localConfigService.modifyJob("   ", "0 0 12 * * ?");
        });

        verify(localConfigDAO, never()).modifyJob(anyString(), anyString());
    }

    @Test
    public void testModifyJob_nullValue_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            localConfigService.modifyJob("job1", null);
        });

        verify(localConfigDAO, never()).modifyJob(anyString(), anyString());
    }

    @Test
    public void testModifyJob_emptyValue_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            localConfigService.modifyJob("job1", "");
        });

        verify(localConfigDAO, never()).modifyJob(anyString(), anyString());
    }

    @Test
    public void testModifyJob_blankValue_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            localConfigService.modifyJob("job1", "   ");
        });

        verify(localConfigDAO, never()).modifyJob(anyString(), anyString());
    }

    @Test
    public void testModifyJob_invalidCronExpression_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            localConfigService.modifyJob("job1", "invalid cron");
        });

        verify(localConfigDAO, never()).modifyJob(anyString(), anyString());
    }

    @Test
    public void testModifyJob_jobNotPermitted_throwsIllegalArgumentException() {
        List<String> permittedJobs = Arrays.asList("job1");
        when(localConfigDAO.getPermittedConfigurations(RegistrationConstants.PERMITTED_JOB_TYPE))
                .thenReturn(permittedJobs);

        assertThrows(IllegalArgumentException.class, () -> {
            localConfigService.modifyJob("unauthorizedJob", "0 0 12 * * ?");
        });

        verify(localConfigDAO, never()).modifyJob(anyString(), anyString());
    }

    @Test
    public void testModifyJob_validInput_delegatesToDao() {
        String jobName = "job1";
        String cronExpression = "0 0 12 * * ?";
        List<String> permittedJobs = Arrays.asList(jobName);
        when(localConfigDAO.getPermittedConfigurations(RegistrationConstants.PERMITTED_JOB_TYPE))
                .thenReturn(permittedJobs);

        localConfigService.modifyJob(jobName, cronExpression);

        verify(localConfigDAO).modifyJob(jobName, cronExpression);
    }

    @Test
    public void testModifyJob_validInputWithWhitespace_delegatesToDao() {
        String jobName = "job1";
        String cronExpression = " 0 0 12 * * ? ";
        List<String> permittedJobs = Arrays.asList(jobName);
        when(localConfigDAO.getPermittedConfigurations(RegistrationConstants.PERMITTED_JOB_TYPE))
                .thenReturn(permittedJobs);

        localConfigService.modifyJob(jobName, cronExpression);

        verify(localConfigDAO).modifyJob(jobName, cronExpression);
    }
}