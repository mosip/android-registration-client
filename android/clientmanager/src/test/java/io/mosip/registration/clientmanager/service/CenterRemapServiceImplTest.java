package io.mosip.registration.clientmanager.service;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.constant.PacketTaskStatus;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.entity.Registration;
import io.mosip.registration.clientmanager.spi.AsyncPacketTaskCallBack;
import io.mosip.registration.clientmanager.repository.DynamicFieldRepository;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.repository.LocationRepository;
import io.mosip.registration.clientmanager.repository.RegistrationCenterRepository;
import io.mosip.registration.clientmanager.repository.RegistrationRepository;
import io.mosip.registration.clientmanager.repository.SyncJobDefRepository;
import io.mosip.registration.clientmanager.repository.TemplateRepository;
import io.mosip.registration.clientmanager.repository.UserBiometricRepository;
import io.mosip.registration.clientmanager.repository.UserDetailRepository;
import io.mosip.registration.clientmanager.repository.UserRoleRepository;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.PacketService;
import io.mosip.registration.clientmanager.spi.PreRegistrationDataSyncService;
import io.mosip.registration.keymanager.repository.KeyStoreRepository;

@RunWith(MockitoJUnitRunner.class)
public class CenterRemapServiceImplTest {

    @Mock private Context mockContext;
    @Mock private GlobalParamRepository mockGlobalParamRepository;
    @Mock private RegistrationRepository mockRegistrationRepository;
    @Mock private PacketService mockPacketService;
    @Mock private PreRegistrationDataSyncService mockPreRegistrationDataSyncService;
    @Mock private SyncJobDefRepository mockSyncJobDefRepository;
    @Mock private DynamicFieldRepository mockDynamicFieldRepository;
    @Mock private IdentitySchemaRepository mockIdentitySchemaRepository;
    @Mock private LocationRepository mockLocationRepository;
    @Mock private RegistrationCenterRepository mockRegistrationCenterRepository;
    @Mock private TemplateRepository mockTemplateRepository;
    @Mock private UserBiometricRepository mockUserBiometricRepository;
    @Mock private UserDetailRepository mockUserDetailRepository;
    @Mock private UserRoleRepository mockUserRoleRepository;
    @Mock private AuditManagerService mockAuditManagerService;
    @Mock private KeyStoreRepository mockKeyStoreRepository;
    @Mock private ConnectivityManager mockConnectivityManager;
    @Mock private NetworkInfo mockNetworkInfo;

    private CenterRemapServiceImpl service;

    @Before
    public void setUp() {
        service = new CenterRemapServiceImpl(
                mockContext,
                mockGlobalParamRepository,
                mockRegistrationRepository,
                mockPacketService,
                mockPreRegistrationDataSyncService,
                mockSyncJobDefRepository,
                mockDynamicFieldRepository,
                mockIdentitySchemaRepository,
                mockLocationRepository,
                mockRegistrationCenterRepository,
                mockTemplateRepository,
                mockUserBiometricRepository,
                mockUserDetailRepository,
                mockUserRoleRepository,
                mockAuditManagerService,
                mockKeyStoreRepository);

        when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE))
                .thenReturn(mockConnectivityManager);
        when(mockConnectivityManager.getActiveNetworkInfo()).thenReturn(mockNetworkInfo);
        when(mockNetworkInfo.isConnected()).thenReturn(true);
    }

    // -------------------------------------------------------------------------
    // handleRemapStep — Step 1: disable sync jobs
    // -------------------------------------------------------------------------

    @Test
    public void handleRemapStep_step1_disablesAllJobsAndAudits() throws Exception {
        service.handleRemapStep(1);

        verify(mockSyncJobDefRepository).disableAllJobs();
        verify(mockAuditManagerService).audit(AuditEvent.MACHINE_REMAPPED, Components.CENTER_MACHINE_REMAP);
    }

    // -------------------------------------------------------------------------
    // handleRemapStep — Step 2: sync and upload packets
    // -------------------------------------------------------------------------

    @Test
    public void handleRemapStep_step2NetworkAvailableWithPendingPackets_syncsAndUploads() throws Exception {
        Registration reg1 = new Registration("PKT001");
        Registration reg2 = new Registration("PKT002");
        when(mockRegistrationRepository.getAllPendingForProcessing())
                .thenReturn(Arrays.asList(reg1, reg2));
        doAnswer(invocation -> {
            String rid = invocation.getArgument(0);
            AsyncPacketTaskCallBack callback = invocation.getArgument(1);
            callback.onComplete(rid, PacketTaskStatus.UPLOAD_COMPLETED);
            return null;
        }).when(mockPacketService).uploadRegistration(anyString(), any(AsyncPacketTaskCallBack.class));

        service.handleRemapStep(2);

        verify(mockPacketService).syncAllPacketStatus();
        verify(mockPacketService).uploadRegistration(eq("PKT001"), any(AsyncPacketTaskCallBack.class));
        verify(mockPacketService).uploadRegistration(eq("PKT002"), any(AsyncPacketTaskCallBack.class));
        verify(mockAuditManagerService).audit(AuditEvent.MACHINE_REMAPPED, Components.PACKET_STATUS_SYNCHED);
        verify(mockAuditManagerService).audit(AuditEvent.MACHINE_REMAPPED, Components.PACKETS_UPLOADED);
    }

    @Test
    public void handleRemapStep_step2NoPendingPackets_skipsUpload() throws Exception {
        when(mockRegistrationRepository.getAllPendingForProcessing())
                .thenReturn(new ArrayList<>());

        service.handleRemapStep(2);

        verify(mockPacketService).syncAllPacketStatus();
        verify(mockPacketService, never()).uploadRegistration(anyString());
    }

    @Test
    public void handleRemapStep_step2NetworkUnavailable_throwsException() {
        when(mockNetworkInfo.isConnected()).thenReturn(false);

        assertThrows(Exception.class, () -> service.handleRemapStep(2));
        verify(mockPacketService, never()).syncAllPacketStatus();
    }

    @Test
    public void handleRemapStep_step2ConnectivityManagerNull_throwsException() {
        when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(null);

        assertThrows(Exception.class, () -> service.handleRemapStep(2));
    }

    @Test
    public void handleRemapStep_step2UploadFails_propagatesException() throws Exception {
        Registration reg = new Registration("PKT001");
        when(mockRegistrationRepository.getAllPendingForProcessing())
                .thenReturn(Arrays.asList(reg));
        doAnswer(invocation -> {
            String rid = invocation.getArgument(0);
            AsyncPacketTaskCallBack callback = invocation.getArgument(1);
            callback.onComplete(rid, PacketTaskStatus.UPLOAD_FAILED);
            return null;
        }).when(mockPacketService).uploadRegistration(eq("PKT001"), any(AsyncPacketTaskCallBack.class));

        assertThrows(Exception.class, () -> service.handleRemapStep(2));
    }

    // -------------------------------------------------------------------------
    // handleRemapStep — Step 3: delete all local packets
    // -------------------------------------------------------------------------

    @Test
    public void handleRemapStep_step3_deletesAllPacketsAndPreReg() throws Exception {
        service.handleRemapStep(3);

        verify(mockPacketService).deleteAllRegistrationPackets();
        verify(mockPreRegistrationDataSyncService).deleteAllPreRegRecords();
        verify(mockAuditManagerService).audit(AuditEvent.MACHINE_REMAPPED, Components.PACKET_SYNCHED);
    }

    // -------------------------------------------------------------------------
    // handleRemapStep — Step 4: purge center data and reset flag
    // -------------------------------------------------------------------------

    @Test
    public void handleRemapStep_step4_deletesAllUserAndCenterData() throws Exception {
        InOrder inOrder = inOrder(
                mockUserRoleRepository,
                mockUserBiometricRepository,
                mockUserDetailRepository,
                mockRegistrationCenterRepository,
                mockTemplateRepository,
                mockDynamicFieldRepository,
                mockIdentitySchemaRepository,
                mockLocationRepository,
                mockKeyStoreRepository,
                mockGlobalParamRepository);

        service.handleRemapStep(4);

        // User and center-specific data cleared
        inOrder.verify(mockUserRoleRepository).deleteAll();
        inOrder.verify(mockUserBiometricRepository).deleteAll();
        inOrder.verify(mockUserDetailRepository).deleteAll();
        inOrder.verify(mockRegistrationCenterRepository).deleteAll();
        inOrder.verify(mockTemplateRepository).deleteAll();
        inOrder.verify(mockDynamicFieldRepository).deleteAll();
        inOrder.verify(mockIdentitySchemaRepository).deleteAll(mockContext);
        inOrder.verify(mockLocationRepository).deleteAll();
        inOrder.verify(mockKeyStoreRepository).deleteAll();
        // Sync timestamps cleared so initial sync triggers on next login
        inOrder.verify(mockGlobalParamRepository).saveGlobalParam("sync.lastupdated", null);
        inOrder.verify(mockGlobalParamRepository).saveGlobalParam("masterdata.lastupdated", null);
        // Remap flag cleared last
        inOrder.verify(mockGlobalParamRepository).saveGlobalParam(
                RegistrationConstants.MACHINE_CENTER_CHANGED, "false");
    }

    @Test
    public void handleRemapStep_step4_deletesUserCredentials() throws Exception {
        // Credentials (user_detail, user_pwd, user_token) are wiped so the app behaves
        // as a fresh install and forces first-time online login after the remap restart.
        service.handleRemapStep(4);

        verify(mockUserDetailRepository).deleteAll();
        verify(mockGlobalParamRepository).saveGlobalParam("sync.lastupdated", null);
        verify(mockGlobalParamRepository).saveGlobalParam("masterdata.lastupdated", null);
    }

    @Test
    public void handleRemapStep_step4_resetsRemapFlagToFalse() throws Exception {
        service.handleRemapStep(4);

        verify(mockGlobalParamRepository).saveGlobalParam(
                RegistrationConstants.MACHINE_CENTER_CHANGED, "false");
    }

    @Test
    public void handleRemapStep_step4_auditsCleanUp() throws Exception {
        service.handleRemapStep(4);

        verify(mockAuditManagerService).audit(AuditEvent.MACHINE_REMAPPED, Components.CLEAN_UP);
    }

    // -------------------------------------------------------------------------
    // handleRemapStep — invalid step
    // -------------------------------------------------------------------------

    @Test
    public void handleRemapStep_invalidStep_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.handleRemapStep(99));
    }

    @Test
    public void handleRemapStep_stepZero_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.handleRemapStep(0));
    }

    @Test
    public void handleRemapStep_everyStep_auditsAtStart() throws Exception {
        when(mockRegistrationRepository.getAllPendingForProcessing())
                .thenReturn(new ArrayList<>());

        for (int step = 1; step <= 4; step++) {
            service.handleRemapStep(step);
        }

        verify(mockAuditManagerService, times(4))
                .audit(AuditEvent.MACHINE_REMAPPED, Components.CENTER_MACHINE_REMAP);
    }
}