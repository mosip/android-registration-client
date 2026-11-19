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
import io.mosip.registration.clientmanager.spi.RegistrationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.ReflectionUtils;

import io.mosip.registration.clientmanager.repository.AuditRepository;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
    public void constructor_withValidDependencies_initializesInstanceVariables() {
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
    public void constructor_withNullContext_setsNullContextField() {
        AuditManagerServiceImpl serviceWithNullContext = new AuditManagerServiceImpl(null, mockAuditRepository, mockGlobalParamRepository);

        Field contextField = ReflectionUtils.findField(AuditManagerServiceImpl.class, "context");
        ReflectionUtils.makeAccessible(contextField);
        assertNull(ReflectionUtils.getField(contextField, serviceWithNullContext));
    }

    @Test
    public void audit_withNullAuditEventTwoArgs_throwsNullPointerException() {
        AuditEvent nullAuditEvent = null;
        Components component = Components.REGISTRATION;

        assertThrows(NullPointerException.class, () -> {
            auditManagerService.audit(nullAuditEvent, component);
        });
    }

    @Test
    public void audit_withRegistrationEventAndValidRid_setsRidAsReferenceId() {
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
    public void audit_withNullEventFourArgs_throwsNullPointerException() {
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
    public void deleteAuditLogs_withValidTillDate_deletesAndReturnsTrue() {
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
    public void deleteAuditLogs_withNullTillDate_usesCurrentTimeAndReturnsTrue() {
        Context mockContext = Mockito.mock(Context.class);
        GlobalParamRepository mockGlobalParamRepo = Mockito.mock(GlobalParamRepository.class);
        AuditRepository mockAuditRepo = Mockito.mock(AuditRepository.class);

        AuditManagerServiceImpl auditManagerService = new AuditManagerServiceImpl(mockContext, mockAuditRepo, mockGlobalParamRepo);

        Mockito.when(mockGlobalParamRepo.getGlobalParamValue(RegistrationConstants.AUDIT_EXPORTED_TILL))
                .thenReturn(null);

        long beforeCall = System.currentTimeMillis();
        boolean result = auditManagerService.deleteAuditLogs();
        long afterCall = System.currentTimeMillis();

        assertTrue(result);
        ArgumentCaptor<Long> dateCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(mockAuditRepo).deleteAllAuditsTillDate(dateCaptor.capture());
        
        long capturedDate = dateCaptor.getValue();
        assertTrue("Captured date should be between before and after call timestamps",
                capturedDate >= beforeCall && capturedDate <= afterCall);
        Mockito.verify(mockGlobalParamRepo).saveGlobalParam(RegistrationConstants.AUDIT_EXPORTED_TILL, null);
    }

    @Test
    public void deleteAuditLogs_whenRepoThrowsException_returnsFalse() {
        Context mockContext = Mockito.mock(Context.class);
        GlobalParamRepository mockGlobalParamRepo = Mockito.mock(GlobalParamRepository.class);
        AuditRepository mockAuditRepo = Mockito.mock(AuditRepository.class);

        AuditManagerServiceImpl auditManagerService = new AuditManagerServiceImpl(mockContext, mockAuditRepo, mockGlobalParamRepo);

        String validTillDate = "1625097600000";
        Mockito.when(mockGlobalParamRepo.getGlobalParamValue(RegistrationConstants.AUDIT_EXPORTED_TILL))
                .thenReturn(validTillDate);

        // Mock deleteAllAuditsTillDate to throw RuntimeException to make deleteAuditLogs return false
        Mockito.doThrow(new RuntimeException("Test exception"))
                .when(mockAuditRepo).deleteAllAuditsTillDate(Mockito.anyLong());

        boolean result = auditManagerService.deleteAuditLogs();

        assertFalse(result);
        Mockito.verify(mockAuditRepo).deleteAllAuditsTillDate(Long.parseLong(validTillDate));
        Mockito.verify(mockGlobalParamRepo, never()).saveGlobalParam(anyString(), any());
    }

    @Test
    public void audit_threeArgsWithNullErrorMessage_delegatesToOverload() {
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences);
        when(mockContext.getString(R.string.app_name)).thenReturn("TestApp");
        when(mockSharedPreferences.getString(SessionManager.USER_NAME, null)).thenReturn("testUser");

        AuditManagerServiceImpl auditService = new AuditManagerServiceImpl(mockContext, mockAuditRepository, mockGlobalParamRepository);

        auditService.audit(AuditEvent.LOGIN_WITH_PASSWORD, "TEST_MODULE", "Test Module");
    }

    @Test
    public void audit_withComponentsAndErrorMessage_insertsAuditRecord() {
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences);
        when(mockContext.getString(R.string.app_name)).thenReturn("TestApp");
        when(mockSharedPreferences.getString(SessionManager.USER_NAME, null)).thenReturn("testUser");
        when(mockSharedPreferences.getString(SessionManager.RID, null)).thenReturn(null);

        AuditManagerServiceImpl auditService = new AuditManagerServiceImpl(mockContext, mockAuditRepository, mockGlobalParamRepository);

        auditService.audit(AuditEvent.LOGIN_WITH_PASSWORD, Components.LOGIN, "Test error message");
    }

    @Test
    public void audit_withNullEventAndComponent_throwsNullPointerException() {
        AuditManagerServiceImpl auditService = new AuditManagerServiceImpl(mockContext, mockAuditRepository, mockGlobalParamRepository);

        assertThrows(NullPointerException.class, () -> {
            auditService.audit(null, Components.LOGIN, "Test error message");
        });
    }

    @Test
    public void audit_fiveArgsWithValidEvent_insertsAuditRecord() {
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences);
        when(mockContext.getString(R.string.app_name)).thenReturn("TestApp");
        when(mockSharedPreferences.getString(SessionManager.PREFERRED_USERNAME, null)).thenReturn("testUser");

        auditManagerService.audit(AuditEvent.LOGIN_WITH_PASSWORD, "MOD001", "LoginModule", "REF123", "USER_ID");

        verify(mockAuditRepository, times(1)).insertAudit(any(Audit.class));
    }

    @Test
    public void audit_withNullEventFiveArgs_doesNotInsertAuditRecord() {
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences);
        when(mockContext.getString(R.string.app_name)).thenReturn("TestApp");

        // addAudit catches the NPE from null auditEventEnum internally — no exception propagates
        auditManagerService.audit(null, "MOD001", "LoginModule", "REF123", "USER_ID");
        verify(mockAuditRepository, never()).insertAudit(any(Audit.class));
    }

    @Test
    public void getAuditLogs_withValidFromDateTime_returnsAuditList() {
        List<Audit> expectedAudits = Arrays.asList(
                new Audit(1234567890L, "EVT-001", "Login", "USER", 1234567890L, "host1", "192.168.1.1", "app1", "TestApp", "user1", "user1", "ref1", "USER_ID", "user1", "AuthModule", "AUTH-001", "User login successful"),
                new Audit(1234567891L, "EVT-002", "Logout", "USER", 1234567891L, "host1", "192.168.1.1", "app1", "TestApp", "user1", "user1", "ref1", "USER_ID", "user1", "AuthModule", "AUTH-002", "User logout successful")
        );

        when(mockAuditRepository.getAuditsFromDate(1234567890L)).thenReturn(expectedAudits);

        AuditManagerServiceImpl auditManagerService = new AuditManagerServiceImpl(mockContext, mockAuditRepository, mockGlobalParamRepository);

        List<Audit> actualAudits = auditManagerService.getAuditLogs(1234567890L);

        assertEquals(expectedAudits, actualAudits);
        verify(mockAuditRepository).getAuditsFromDate(1234567890L);
    }

    @Test
    public void getAuditLogs_withNegativeFromDateTime_returnsEmptyList() {
        List<Audit> expectedAudits = new ArrayList<>();

        when(mockAuditRepository.getAuditsFromDate(-1000L)).thenReturn(expectedAudits);

        AuditManagerServiceImpl auditManagerService = new AuditManagerServiceImpl(mockContext, mockAuditRepository, mockGlobalParamRepository);

        List<Audit> actualAudits = auditManagerService.getAuditLogs(-1000L);

        assertEquals(expectedAudits, actualAudits);
        verify(mockAuditRepository).getAuditsFromDate(-1000L);
    }

    @Test
    public void audit_withComponents_delegatesWithComponentId() {
        Context ctx = mock(Context.class);
        SharedPreferences prefs = mock(SharedPreferences.class);
        AuditRepository repo = mock(AuditRepository.class);
        GlobalParamRepository globalRepo = mock(GlobalParamRepository.class);
        AuditManagerServiceImpl service = new AuditManagerServiceImpl(ctx, repo, globalRepo);

        when(ctx.getString(R.string.app_name)).thenReturn("TestApp");
        when(ctx.getSharedPreferences("TestApp", Context.MODE_PRIVATE)).thenReturn(prefs);
        when(prefs.getString(SessionManager.USER_NAME, null)).thenReturn("officer1");
        when(prefs.getString(SessionManager.RID, null)).thenReturn(null);
        when(prefs.getString(SessionManager.PREFERRED_USERNAME, null)).thenReturn("officer1");

        service.audit(AuditEvent.LOGIN_WITH_PASSWORD, Components.LOGIN);

        verify(repo, times(1)).insertAudit(any(Audit.class));
    }

    @Test
    public void auditWithArguments_withSessionUser_insertsAudit() {
        Context ctx = mock(Context.class);
        SharedPreferences prefs = mock(SharedPreferences.class);
        AuditRepository repo = mock(AuditRepository.class);
        GlobalParamRepository globalRepo = mock(GlobalParamRepository.class);
        AuditManagerServiceImpl service = new AuditManagerServiceImpl(ctx, repo, globalRepo);

        when(ctx.getString(R.string.app_name)).thenReturn("TestApp");
        when(ctx.getSharedPreferences("TestApp", Context.MODE_PRIVATE)).thenReturn(prefs);
        when(prefs.getString(SessionManager.USER_NAME, null)).thenReturn("operator1");
        when(prefs.getString(SessionManager.RID, null)).thenReturn(null);
        when(prefs.getString(SessionManager.PREFERRED_USERNAME, null)).thenReturn("operator1");

        service.auditWithArguments(AuditEvent.LOGIN_WITH_PASSWORD, "MOD-001", "LoginModule");

        verify(repo, times(1)).insertAudit(any(Audit.class));
    }

    @Test
    public void auditWithArguments_withRegistrationEventAndRid_setsRidAsRefId() {
        Context ctx = mock(Context.class);
        SharedPreferences prefs = mock(SharedPreferences.class);
        AuditRepository repo = mock(AuditRepository.class);
        GlobalParamRepository globalRepo = mock(GlobalParamRepository.class);
        AuditManagerServiceImpl service = new AuditManagerServiceImpl(ctx, repo, globalRepo);

        String rid = "2024010112345678901234567890";
        when(ctx.getString(R.string.app_name)).thenReturn("TestApp");
        when(ctx.getSharedPreferences("TestApp", Context.MODE_PRIVATE)).thenReturn(prefs);
        when(prefs.getString(SessionManager.USER_NAME, null)).thenReturn("operator1");
        when(prefs.getString(SessionManager.RID, null)).thenReturn(rid);
        when(prefs.getString(SessionManager.PREFERRED_USERNAME, null)).thenReturn("operator1");

        ArgumentCaptor<Audit> captor = ArgumentCaptor.forClass(Audit.class);
        service.auditWithArguments(AuditEvent.REGISTRATION_START, "REG-MOD", "RegModule");

        verify(repo).insertAudit(captor.capture());
        assertEquals(rid, captor.getValue().getRefId());
    }

    @Test
    public void auditWithArguments_withNoSession_usesAppNameAsRefId() {
        Context ctx = mock(Context.class);
        SharedPreferences prefs = mock(SharedPreferences.class);
        AuditRepository repo = mock(AuditRepository.class);
        GlobalParamRepository globalRepo = mock(GlobalParamRepository.class);
        AuditManagerServiceImpl service = new AuditManagerServiceImpl(ctx, repo, globalRepo);

        when(ctx.getString(R.string.app_name)).thenReturn("RegApp");
        when(ctx.getSharedPreferences("RegApp", Context.MODE_PRIVATE)).thenReturn(prefs);
        when(prefs.getString(SessionManager.USER_NAME, null)).thenReturn(null);
        when(prefs.getString(SessionManager.RID, null)).thenReturn(null);
        when(prefs.getString(SessionManager.PREFERRED_USERNAME, null)).thenReturn(null);

        ArgumentCaptor<Audit> captor = ArgumentCaptor.forClass(Audit.class);
        service.auditWithArguments(AuditEvent.LOGIN_WITH_PASSWORD, "MOD-001", "LoginModule");

        verify(repo).insertAudit(captor.capture());
        assertEquals("RegApp", captor.getValue().getRefId());
    }

    @Test
    public void auditWithArguments_withFormatStringEvent_formatsDescription() {
        Context ctx = mock(Context.class);
        SharedPreferences prefs = mock(SharedPreferences.class);
        AuditRepository repo = mock(AuditRepository.class);
        GlobalParamRepository globalRepo = mock(GlobalParamRepository.class);
        AuditManagerServiceImpl service = new AuditManagerServiceImpl(ctx, repo, globalRepo);

        when(ctx.getString(R.string.app_name)).thenReturn("TestApp");
        when(ctx.getSharedPreferences("TestApp", Context.MODE_PRIVATE)).thenReturn(prefs);
        when(prefs.getString(SessionManager.USER_NAME, null)).thenReturn("operator1");
        when(prefs.getString(SessionManager.RID, null)).thenReturn(null);
        when(prefs.getString(SessionManager.PREFERRED_USERNAME, null)).thenReturn("operator1");

        ArgumentCaptor<Audit> captor = ArgumentCaptor.forClass(Audit.class);
        // NEXT_BUTTON_CLICKED has description "Next button clicked to %s"
        service.auditWithArguments(AuditEvent.NEXT_BUTTON_CLICKED, "REG-MOD", "RegModule", "FingerPage");

        verify(repo).insertAudit(captor.capture());
        assertTrue(captor.getValue().getDescription().contains("FingerPage"));
    }

    @Test
    public void audit_withNullSessionAndNonRegistrationEvent_usesAppNameAsRefId() {
        Context ctx = mock(Context.class);
        SharedPreferences prefs = mock(SharedPreferences.class);
        AuditRepository repo = mock(AuditRepository.class);
        GlobalParamRepository globalRepo = mock(GlobalParamRepository.class);
        AuditManagerServiceImpl service = new AuditManagerServiceImpl(ctx, repo, globalRepo);

        when(ctx.getString(R.string.app_name)).thenReturn("RegApp");
        when(ctx.getSharedPreferences("RegApp", Context.MODE_PRIVATE)).thenReturn(prefs);
        when(prefs.getString(SessionManager.USER_NAME, null)).thenReturn(null);
        when(prefs.getString(SessionManager.RID, null)).thenReturn(null);
        when(prefs.getString(SessionManager.PREFERRED_USERNAME, null)).thenReturn(null);

        ArgumentCaptor<Audit> captor = ArgumentCaptor.forClass(Audit.class);
        service.audit(AuditEvent.LOGIN_WITH_PASSWORD, "MOD-001", "LoginModule", null);

        verify(repo).insertAudit(captor.capture());
        assertEquals("RegApp", captor.getValue().getRefId());
        assertEquals(AuditReferenceIdTypes.APPLICATION_ID.name(), captor.getValue().getRefIdType());
    }

}