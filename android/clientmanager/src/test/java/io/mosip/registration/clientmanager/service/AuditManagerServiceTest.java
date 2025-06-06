package io.mosip.registration.clientmanager.service;

import android.content.Context;
import android.content.SharedPreferences;
import io.mosip.registration.clientmanager.R;
import io.mosip.registration.clientmanager.config.SessionManager;
import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.AuditReferenceIdTypes;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.entity.Audit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;

import org.springframework.util.ReflectionUtils;

import io.mosip.registration.clientmanager.repository.AuditRepository;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuditManagerServiceTest {

    @Mock
    private Context mockContext;

    @Mock
    private AuditRepository mockAuditRepository;

    @Mock
    private GlobalParamRepository mockGlobalParamRepository;

    @InjectMocks
    private AuditManagerServiceImpl auditManagerService;

    @Mock
    private android.content.SharedPreferences mockSharedPreferences;

    @Before
    public void setUp() {
        lenient().when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences);

        lenient().when(mockSharedPreferences.getString(anyString(), anyString())).thenReturn("mock-value");
    }

    @Test
    public void test_constructor_initializes_instance_variables() {
        AuditManagerServiceImpl manuallyCreatedService = new AuditManagerServiceImpl(mockContext, mockAuditRepository, mockGlobalParamRepository);

        Field contextField = ReflectionUtils.findField(AuditManagerServiceImpl.class, "context");
        Field auditRepositoryField = ReflectionUtils.findField(AuditManagerServiceImpl.class, "auditRepository");
        Field globalParamRepositoryField = ReflectionUtils.findField(AuditManagerServiceImpl.class, "globalParamRepository");

        ReflectionUtils.makeAccessible(contextField);
        ReflectionUtils.makeAccessible(auditRepositoryField);
        ReflectionUtils.makeAccessible(globalParamRepositoryField);

        assertEquals(mockContext, ReflectionUtils.getField(contextField, manuallyCreatedService));
        assertEquals(mockAuditRepository, ReflectionUtils.getField(auditRepositoryField, manuallyCreatedService));
        assertEquals(mockGlobalParamRepository, ReflectionUtils.getField(globalParamRepositoryField, manuallyCreatedService));
    }

    @Test
    public void test_constructor_with_null_context() {
        AuditManagerServiceImpl serviceWithNullContext = new AuditManagerServiceImpl(null, mockAuditRepository, mockGlobalParamRepository);

        Field contextField = ReflectionUtils.findField(AuditManagerServiceImpl.class, "context");
        ReflectionUtils.makeAccessible(contextField);
        assertNull(ReflectionUtils.getField(contextField, serviceWithNullContext));
    }

    @Test
    public void test_audit_with_null_audit_event() {
        AuditEvent nullAuditEvent = null;
        Components component = Components.REGISTRATION;

        assertThrows(NullPointerException.class, () -> {
            auditManagerService.audit(nullAuditEvent, component);
        });
    }

    @Test
    public void test_registration_event_with_valid_rid_sets_correct_reference_id_and_type() {
        Context mockContext = mock(Context.class);
        SharedPreferences mockSharedPreferences = mock(SharedPreferences.class);
        AuditRepository mockAuditRepository = mock(AuditRepository.class);
        GlobalParamRepository mockGlobalParamRepository = mock(GlobalParamRepository.class);

        AuditManagerServiceImpl auditManagerService = new AuditManagerServiceImpl(mockContext, mockAuditRepository, mockGlobalParamRepository);

        String appName = "TestApp";
        String testRid = "12345678901234567890";
        String appModuleId = "REG-MOD-001";
        String appModuleName = "Registration Module";

        when(mockContext.getString(R.string.app_name)).thenReturn(appName);
        when(mockContext.getSharedPreferences(appName, Context.MODE_PRIVATE)).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.getString(SessionManager.RID, null)).thenReturn(testRid);

        AuditEvent auditEvent = AuditEvent.REGISTRATION_START;

        ArgumentCaptor<Audit> auditCaptor = ArgumentCaptor.forClass(Audit.class);

        auditManagerService.audit(auditEvent, appModuleId, appModuleName, null);

        verify(mockAuditRepository).insertAudit(auditCaptor.capture());
        Audit capturedAudit = auditCaptor.getValue();

        assertEquals(testRid, capturedAudit.getRefId());
        assertEquals(AuditReferenceIdTypes.REGISTRATION_ID.getReferenceTypeId(), capturedAudit.getRefIdType());
    }

    @Test
    public void test_null_audit_event_enum_throws_exception() {
        Context mockContext = mock(Context.class);
        AuditRepository mockAuditRepository = mock(AuditRepository.class);
        GlobalParamRepository mockGlobalParamRepository = mock(GlobalParamRepository.class);

        AuditManagerServiceImpl auditManagerService = new AuditManagerServiceImpl(mockContext, mockAuditRepository, mockGlobalParamRepository);

        String appModuleId = "REG-MOD-001";
        String appModuleName = "Registration Module";

        assertThrows(NullPointerException.class, () -> {
            auditManagerService.audit(null, appModuleId, appModuleName, null);
        });

        verify(mockAuditRepository, never()).insertAudit(any(Audit.class));
    }

    @Test
    public void test_delete_audit_logs_success() {
        Context mockContext = Mockito.mock(Context.class);
        GlobalParamRepository mockGlobalParamRepo = Mockito.mock(GlobalParamRepository.class);
        AuditRepository mockAuditRepo = Mockito.mock(AuditRepository.class);

        AuditManagerServiceImpl auditManagerService = new AuditManagerServiceImpl(mockContext, mockAuditRepo, mockGlobalParamRepo);

        String validTillDate = "1625097600000";
        long validTillDateLong = Long.parseLong(validTillDate);

        Mockito.when(mockGlobalParamRepo.getGlobalParamValue(RegistrationConstants.AUDIT_EXPORTED_TILL))
                .thenReturn(validTillDate);

        boolean result = auditManagerService.deleteAuditLogs();

        assertTrue(result);
        Mockito.verify(mockAuditRepo).deleteAllAuditsTillDate(validTillDateLong);
    }

    @Test
    public void test_delete_audit_logs_null_till_date() {
        Context mockContext = Mockito.mock(Context.class);
        GlobalParamRepository mockGlobalParamRepo = Mockito.mock(GlobalParamRepository.class);
        AuditRepository mockAuditRepo = Mockito.mock(AuditRepository.class);

        AuditManagerServiceImpl auditManagerService = new AuditManagerServiceImpl(mockContext, mockAuditRepo, mockGlobalParamRepo);

        Mockito.when(mockGlobalParamRepo.getGlobalParamValue(RegistrationConstants.AUDIT_EXPORTED_TILL))
                .thenReturn(null);

        boolean result = auditManagerService.deleteAuditLogs();

        assertFalse(result);
        Mockito.verify(mockAuditRepo, Mockito.never()).deleteAllAuditsTillDate(Mockito.anyLong());
    }

}