package io.mosip.registration.clientmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import android.content.Context;
import android.widget.Toast;
import io.mosip.registration.clientmanager.constant.*;
import io.mosip.registration.clientmanager.dto.*;
import io.mosip.registration.clientmanager.dto.http.*;
import io.mosip.registration.clientmanager.entity.Registration;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.RegistrationRepository;
import io.mosip.registration.clientmanager.spi.*;
import io.mosip.registration.clientmanager.util.SyncRestUtil;
import io.mosip.registration.packetmanager.spi.IPacketCryptoService;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.junit.*;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import retrofit2.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RunWith(MockitoJUnitRunner .class)
public class PacketServiceImplTest {

    @Mock private Context mockContext;
    @Mock private RegistrationRepository mockRegistrationRepository;
    @Mock private IPacketCryptoService mockPacketCryptoService;
    @Mock private SyncRestService mockSyncRestService;
    @Mock private MasterDataService mockMasterDataService;
    @Mock private GlobalParamRepository mockGlobalParamRepository;
    @Mock private AuditManagerService mockAuditManagerService;

    @Mock private Call<RegProcResponseWrapper<List<SyncRIDResponse>>> mockSyncCall;
    @Mock private Call<RegProcResponseWrapper<UploadResponse>> mockUploadCall;
    @Mock private Call<PacketStatusResponse> mockPacketStatusCall;

    @InjectMocks
    private PacketServiceImpl packetService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        packetService = Mockito.spy(new PacketServiceImpl(
                mockContext, mockRegistrationRepository, mockPacketCryptoService,
                mockSyncRestService, mockMasterDataService, mockGlobalParamRepository, mockAuditManagerService
        ));
    }

    @Test
    public void test_syncRegistration_createdStatus() throws Exception {
        Registration reg = new Registration("123");
        reg.setClientStatus(PacketClientStatus.CREATED.name());
        when(mockRegistrationRepository.getRegistration("pid")).thenReturn(reg);

        AsyncPacketTaskCallBack callBack = mock(AsyncPacketTaskCallBack.class);
        packetService.syncRegistration("pid", callBack);

        verify(callBack).onComplete("pid", PacketTaskStatus.SYNC_FAILED);
    }

    @Test
    public void test_syncRegistration_alreadySynced() throws Exception {
        Registration reg = new Registration("123");
        reg.setClientStatus("SOMETHING_ELSE");
        when(mockRegistrationRepository.getRegistration("pid")).thenReturn(reg);

        AsyncPacketTaskCallBack callBack = mock(AsyncPacketTaskCallBack.class);
        packetService.syncRegistration("pid", callBack);

        verify(callBack).onComplete("pid", PacketTaskStatus.SYNC_ALREADY_COMPLETED);
    }

    @Test
    public void test_uploadRegistration_alreadyUploaded() {
        Registration reg = new Registration("123");
        reg.setServerStatus(PacketServerStatus.ACCEPTED.name());
        reg.setClientStatus(PacketClientStatus.SYNCED.name());
        when(mockRegistrationRepository.getRegistration("pid")).thenReturn(reg);

        try (MockedStatic<Toast> toast = Mockito.mockStatic(Toast.class)) {
            toast.when(() -> Toast.makeText(any(), anyString(), anyInt())).thenReturn(mock(Toast.class));
            AsyncPacketTaskCallBack callBack = mock(AsyncPacketTaskCallBack.class);

            packetService.uploadRegistration("pid", callBack);

            verify(callBack).onComplete("pid", PacketTaskStatus.UPLOAD_ALREADY_COMPLETED);
        }
    }

    @Test
    public void test_uploadRegistration_successful() {
        Registration reg = new Registration("123");
        reg.setServerStatus(PacketServerStatus.UPLOAD_PENDING.name());
        reg.setPacketId("pid");
        reg.setFilePath("test.zip");
        when(mockRegistrationRepository.getRegistration("pid")).thenReturn(reg);

        File file = mock(File.class);
        lenient().when(file.getName()).thenReturn("test.zip");
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", "test.zip", RequestBody.create(MediaType.parse("application/zip"), file));
        when(mockSyncRestService.uploadPacket(any())).thenReturn(mockUploadCall);

        AsyncPacketTaskCallBack callBack = mock(AsyncPacketTaskCallBack.class);

        RegProcResponseWrapper<UploadResponse> wrapper = new RegProcResponseWrapper<>();
        UploadResponse uploadResponse = new UploadResponse();
        uploadResponse.setStatus("UPLOADED");
        wrapper.setResponse(uploadResponse);

        try (MockedStatic<SyncRestUtil> util = Mockito.mockStatic(SyncRestUtil.class)) {
            util.when(() -> SyncRestUtil.getServiceError(wrapper)).thenReturn(null);

            packetService.uploadRegistration("pid", callBack);

            ArgumentCaptor<Callback<RegProcResponseWrapper<UploadResponse>>> captor = ArgumentCaptor.forClass(Callback.class);
            verify(mockUploadCall).enqueue(captor.capture());

            Response<RegProcResponseWrapper<UploadResponse>> response = Response.success(wrapper);
            captor.getValue().onResponse(mockUploadCall, response);

            verify(mockRegistrationRepository).updateStatus(eq("pid"), eq("UPLOADED"), eq(PacketClientStatus.UPLOADED.name()));
            verify(callBack).onComplete("pid", PacketTaskStatus.UPLOAD_COMPLETED);
        }
    }

    @Test
    public void test_getAllRegistrations() {
        List<Registration> regs = Arrays.asList(new Registration("123"), new Registration("890"));
        when(mockRegistrationRepository.getAllRegistrations()).thenReturn(regs);

        List<Registration> result = packetService.getAllRegistrations(0, 10);
        assertEquals(2, result.size());
    }

    @Test
    public void test_getAllNotUploadedRegistrations() {
        List<Registration> regs = Arrays.asList(new Registration("123"));
        when(mockRegistrationRepository.getAllNotUploadedRegistrations()).thenReturn(regs);

        List<Registration> result = packetService.getAllNotUploadedRegistrations(0, 10);
        assertEquals(1, result.size());
    }

    @Test
    public void test_getRegistrationsByStatus() {
        List<Registration> regs = Arrays.asList(new Registration("123"));
        when(mockRegistrationRepository.getRegistrationsByStatus("UPLOADED", 5)).thenReturn(regs);

        List<Registration> result = packetService.getRegistrationsByStatus("UPLOADED", 5);
        assertEquals(1, result.size());
    }

    @Test
    public void test_getPacketStatus() {
        Registration reg = new Registration("123");
        reg.setServerStatus("UPLOADED");
        reg.setClientStatus("SYNCED");
        when(mockRegistrationRepository.getRegistration("pid")).thenReturn(reg);

        String status = packetService.getPacketStatus("pid");
        assertEquals("UPLOADED", status);
    }

    @Test
    public void test_sync_registration_calls_overloaded_method() throws Exception {
        String packetId = "TEST-PACKET-123";

        Mockito.doNothing().when(packetService).syncRegistration(
                Mockito.eq(packetId),
                Mockito.any(AsyncPacketTaskCallBack.class)
        );

        packetService.syncRegistration(packetId);

        Mockito.verify(packetService).syncRegistration(
                Mockito.eq(packetId),
                Mockito.any(AsyncPacketTaskCallBack.class)
        );
    }

    @Test
    public void test_sync_registration_throws_exception_for_null_packet_id() {
        String packetId = null;

        assertThrows(NullPointerException.class, () -> {
            packetService.syncRegistration(packetId);
        });
    }

    @Test
    public void test_passes_packet_id_to_overloaded_method() throws Exception {
        String packetId = "testPacketId";

        Mockito.doNothing().when(packetService).syncRegistration(
                Mockito.eq(packetId),
                Mockito.any(AsyncPacketTaskCallBack.class)
        );

        packetService.syncRegistration(packetId);

        Mockito.verify(packetService).syncRegistration(
                Mockito.eq(packetId),
                Mockito.any(AsyncPacketTaskCallBack.class)
        );
    }

    @Test
    public void test_async_packet_task_callback_implementation() throws Exception {
        String packetId = "testPacketId";

        AsyncPacketTaskCallBack callBack = new AsyncPacketTaskCallBack() {
            @Override
            public void inProgress(String RID) {
                //Do nothing
            }

            @Override
            public void onComplete(String RID, PacketTaskStatus status) {
                //Do nothing
            }
        };

        Mockito.doNothing().when(packetService).syncRegistration(
                Mockito.eq(packetId),
                Mockito.any(AsyncPacketTaskCallBack.class)
        );

        packetService.syncRegistration(packetId, callBack);

        Mockito.verify(packetService).syncRegistration(
                Mockito.eq(packetId),
                Mockito.eq(callBack)
        );
    }

    @Test
    public void test_sync_registration_with_created_status() throws Exception {
        String packetId = "12345678901234567890123456789012";

        AsyncPacketTaskCallBack mockCallback = Mockito.mock(AsyncPacketTaskCallBack.class);

        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setMachineRefId("10001_10001");

        Registration registration = new Registration(packetId);
        registration.setPacketId(packetId);
        registration.setClientStatus(PacketClientStatus.CREATED.name());
        registration.setRegType("NEW");

        Mockito.when(mockMasterDataService.getRegistrationCenterMachineDetails()).thenReturn(centerMachineDto);
        Mockito.when(mockRegistrationRepository.getRegistration(packetId)).thenReturn(registration);

        ReflectionTestUtils.setField(packetService, "masterDataService", mockMasterDataService);
        ReflectionTestUtils.setField(packetService, "registrationRepository", mockRegistrationRepository);
        ReflectionTestUtils.setField(packetService, "globalParamRepository", mockGlobalParamRepository);

        packetService.syncRegistration(packetId, mockCallback);

        Mockito.verify(mockCallback).onComplete(packetId, PacketTaskStatus.SYNC_FAILED);

        Mockito.verify(mockGlobalParamRepository, Mockito.never()).getCachedStringGlobalParam(Mockito.anyString());
    }

    @Test
    public void test_get_all_registrations_returns_repository_data() {
        List<Registration> expectedRegistrations = new ArrayList<>();
        Registration reg1 = new Registration("packet1");
        Registration reg2 = new Registration("packet2");
        expectedRegistrations.add(reg1);
        expectedRegistrations.add(reg2);

        Mockito.when(mockRegistrationRepository.getAllRegistrations()).thenReturn(expectedRegistrations);

        List<Registration> result = packetService.getAllRegistrations(1, 10);

        assertEquals(expectedRegistrations, result);
        Mockito.verify(mockRegistrationRepository).getAllRegistrations();
    }

    @Test
    public void test_get_all_registrations_handles_null_from_repository() {
        Mockito.when(mockRegistrationRepository.getAllRegistrations()).thenReturn(null);

        List<Registration> result = packetService.getAllRegistrations(1, 10);

        assertNull(result);
        Mockito.verify(mockRegistrationRepository).getAllRegistrations();
    }


    @Test
    public void test_get_all_not_uploaded_registrations_returns_list() {
        ReflectionTestUtils.setField(packetService, "registrationRepository", mockRegistrationRepository);

        List<Registration> expectedRegistrations = new ArrayList<>();
        Registration reg1 = new Registration("packet1");
        Registration reg2 = new Registration("packet2");
        expectedRegistrations.add(reg1);
        expectedRegistrations.add(reg2);

        Mockito.when(mockRegistrationRepository.getAllNotUploadedRegistrations()).thenReturn(expectedRegistrations);

        List<Registration> result = packetService.getAllNotUploadedRegistrations(0, 10);

        Assertions.assertEquals(expectedRegistrations, result);
        Mockito.verify(mockRegistrationRepository).getAllNotUploadedRegistrations();
    }

    @Test
    public void test_get_all_not_uploaded_registrations_handles_null() {
        ReflectionTestUtils.setField(packetService, "registrationRepository", mockRegistrationRepository);

        Mockito.when(mockRegistrationRepository.getAllNotUploadedRegistrations()).thenReturn(null);

        List<Registration> result = packetService.getAllNotUploadedRegistrations(0, 10);

        Assertions.assertNull(result);
        Mockito.verify(mockRegistrationRepository).getAllNotUploadedRegistrations();
    }

    @Test
    public void test_returns_registrations_filtered_by_status() {
        String status = "CREATED";
        Integer batchSize = 10;
        List<Registration> expectedRegistrations = new ArrayList<>();
        Registration registration = new Registration("test-packet-id");
        registration.setClientStatus(status);
        expectedRegistrations.add(registration);

        Mockito.when(mockRegistrationRepository.getRegistrationsByStatus(status, batchSize)).thenReturn(expectedRegistrations);

        ReflectionTestUtils.setField(packetService, "registrationRepository", mockRegistrationRepository);

        List<Registration> result = packetService.getRegistrationsByStatus(status, batchSize);

        Assertions.assertEquals(expectedRegistrations.size(), result.size());
        Assertions.assertEquals(expectedRegistrations, result);
        Mockito.verify(mockRegistrationRepository).getRegistrationsByStatus(status, batchSize);
    }

    @Test
    public void test_handles_null_status_parameter() {
        String status = null;
        Integer batchSize = 10;
        List<Registration> expectedRegistrations = new ArrayList<>();

        Mockito.when(mockRegistrationRepository.getRegistrationsByStatus(status, batchSize)).thenReturn(expectedRegistrations);

        ReflectionTestUtils.setField(packetService, "registrationRepository", mockRegistrationRepository);

        List<Registration> result = packetService.getRegistrationsByStatus(status, batchSize);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(0, result.size());
        Mockito.verify(mockRegistrationRepository).getRegistrationsByStatus(status, batchSize);
    }

    @Test
    public void test_returns_server_status_when_not_null() {
        String packetId = "test-packet-123";
        String expectedServerStatus = "PROCESSED";

        Registration mockRegistration = new Registration(packetId);
        mockRegistration.setServerStatus(expectedServerStatus);
        mockRegistration.setClientStatus("CREATED");

        Mockito.when(mockRegistrationRepository.getRegistration(packetId)).thenReturn(mockRegistration);

        ReflectionTestUtils.setField(packetService, "registrationRepository", mockRegistrationRepository);

        String actualStatus = packetService.getPacketStatus(packetId);

        assertEquals(expectedServerStatus, actualStatus);
        Mockito.verify(mockRegistrationRepository).getRegistration(packetId);
    }

    @Test
    public void test_handles_null_registration() {
        String packetId = "non-existent-packet";

        Mockito.when(mockRegistrationRepository.getRegistration(packetId)).thenReturn(null);

        ReflectionTestUtils.setField(packetService, "registrationRepository", mockRegistrationRepository);

        assertThrows(NullPointerException.class, () -> {
            packetService.getPacketStatus(packetId);
        });

        Mockito.verify(mockRegistrationRepository).getRegistration(packetId);
    }

    @Test
    public void test_sync_all_packet_status_success() {
        try (MockedStatic<Toast> mockedToast = Mockito.mockStatic(Toast.class)) {
            Toast mockToast = Mockito.mock(Toast.class);
            mockedToast.when(() -> Toast.makeText(Mockito.any(Context.class),
                    Mockito.anyString(), Mockito.anyInt())).thenReturn(mockToast);

            ReflectionTestUtils.setField(packetService, "registrationRepository", mockRegistrationRepository);
            ReflectionTestUtils.setField(packetService, "globalParamRepository", mockGlobalParamRepository);
            ReflectionTestUtils.setField(packetService, "syncRestService", mockSyncRestService);
            ReflectionTestUtils.setField(packetService, "context", mockContext);

            List<Registration> registrations = new ArrayList<>();
            Registration reg1 = new Registration("reg123");
            reg1.setPacketId("reg123");
            registrations.add(reg1);

            Mockito.when(mockRegistrationRepository.getAllRegistrations()).thenReturn(registrations);
            Mockito.when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.SERVER_VERSION))
                    .thenReturn("1.1.5");
            Mockito.when(mockContext.getString(Mockito.anyInt(), Mockito.anyInt())).thenReturn("Success message");

            Call<PacketStatusResponse> mockCall = Mockito.mock(Call.class);
            Mockito.when(mockSyncRestService.getV1PacketStatus(Mockito.any())).thenReturn(mockCall);

            packetService.syncAllPacketStatus();

            ArgumentCaptor<Callback<PacketStatusResponse>> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
            Mockito.verify(mockCall).enqueue(callbackCaptor.capture());

            Callback<PacketStatusResponse> callback = callbackCaptor.getValue();
            PacketStatusResponse successResponse = createSuccessResponse();
            Response<PacketStatusResponse> httpResponse = Response.success(successResponse);

            callback.onResponse(mockCall, httpResponse);

            Mockito.verify(mockRegistrationRepository).updateServerStatusWithTimestamp(
                    Mockito.eq("reg123"),
                    Mockito.eq("UPLOADED"),
                    Mockito.anyLong()
            );
        }
    }

    private PacketStatusResponse createSuccessResponse() {
        PacketStatusResponse response = new PacketStatusResponse();

        List<PacketStatusDto> packetStatusList = new ArrayList<>();
        PacketStatusDto packetStatus = new PacketStatusDto("reg123", "reg123", "UPLOADED");
        packetStatus.setPacketId("reg123");
        packetStatus.setStatusCode("UPLOADED");
        packetStatusList.add(packetStatus);

        response.setResponse(packetStatusList);
        response.setResponsetime("2023-01-01T00:00:00.000Z");
        response.setVersion("1.0");

        return response;
    }

    @Test
    public void test_sync_all_packet_status_empty_registrations() {
        ReflectionTestUtils.setField(packetService, "registrationRepository", mockRegistrationRepository);
        ReflectionTestUtils.setField(packetService, "globalParamRepository", mockGlobalParamRepository);
        ReflectionTestUtils.setField(packetService, "syncRestService", mockSyncRestService);

        List<Registration> emptyRegistrations = new ArrayList<>();
        Mockito.when(mockRegistrationRepository.getAllRegistrations()).thenReturn(emptyRegistrations);

        packetService.syncAllPacketStatus();

        Mockito.verify(mockRegistrationRepository).getAllRegistrations();
        Mockito.verifyNoMoreInteractions(mockRegistrationRepository);
        Mockito.verifyNoInteractions(mockGlobalParamRepository);
        Mockito.verifyNoInteractions(mockSyncRestService);
    }

    @Test
    public void test_api_endpoint_determination_based_on_server_version() {
        List<Registration> registrations = Arrays.asList(new Registration("packet1"), new Registration("packet2"));
        when(mockRegistrationRepository.getAllRegistrations()).thenReturn(registrations);
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.SERVER_VERSION)).thenReturn("1.1.5");

        Call<PacketStatusResponse> callMock = mock(Call.class);
        when(mockSyncRestService.getV1PacketStatus(any(PacketStatusRequest.class))).thenReturn(callMock);

        packetService.syncAllPacketStatus();

        verify(mockSyncRestService).getV1PacketStatus(any(PacketStatusRequest.class));
    }

    @Test
    public void testGetAllRegistrations() {
        Registration reg = new Registration("reg123");
        when(mockRegistrationRepository.getAllRegistrations()).thenReturn(Collections.singletonList(reg));
        List<Registration> result = packetService.getAllRegistrations(0, 10);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetAllNotUploadedRegistrations() {
        Registration reg = new Registration("reg123");
        when(mockRegistrationRepository.getAllNotUploadedRegistrations()).thenReturn(Collections.singletonList(reg));
        List<Registration> result = packetService.getAllNotUploadedRegistrations(0, 10);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetRegistrationsByStatus() {
        Registration reg = new Registration("reg123");
        when(mockRegistrationRepository.getRegistrationsByStatus("SYNCED", 5)).thenReturn(Arrays.asList(reg));
        List<Registration> result = packetService.getRegistrationsByStatus("SYNCED", 5);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetPacketStatus_ServerStatusNotNull() {
        Registration reg = new Registration("reg123");
        reg.setServerStatus(PacketServerStatus.UPLOAD_PENDING.name());
        reg.setClientStatus(PacketClientStatus.APPROVED.name());
        when(mockRegistrationRepository.getRegistration("packet123")).thenReturn(reg);
        String status = packetService.getPacketStatus("packet123");
        assertEquals(PacketServerStatus.UPLOAD_PENDING.name(), status);
    }

    @Test
    public void testGetPacketStatus_ServerStatusNull() {
        Registration reg = new Registration("reg123");
        reg.setServerStatus(null);
        reg.setClientStatus(PacketClientStatus.APPROVED.name());
        when(mockRegistrationRepository.getRegistration("packet123")).thenReturn(reg);
        String status = packetService.getPacketStatus("packet123");
        assertEquals(PacketClientStatus.APPROVED.name(), status);
    }

    @Test
    public void testSyncRegistration_createdStatus() throws Exception {
        Registration reg = new Registration("id");
        reg.setClientStatus(PacketClientStatus.CREATED.name());
        when(mockRegistrationRepository.getRegistration("id")).thenReturn(reg);

        AsyncPacketTaskCallBack cb = mock(AsyncPacketTaskCallBack.class);
        packetService.syncRegistration("id", cb);

        verify(cb).onComplete("id", PacketTaskStatus.SYNC_FAILED);
    }

    @Test
    public void testSyncRegistration_alreadySynced() throws Exception {
        Registration reg = new Registration("id");
        reg.setClientStatus(PacketClientStatus.SYNCED.name());
        when(mockRegistrationRepository.getRegistration("id")).thenReturn(reg);

        AsyncPacketTaskCallBack cb = mock(AsyncPacketTaskCallBack.class);
        packetService.syncRegistration("id", cb);

        verify(cb).onComplete("id", PacketTaskStatus.SYNC_ALREADY_COMPLETED);
    }

    @Test
    public void testSyncRegistration_successfulSync() throws Exception {
        Registration reg = new Registration("id");
        reg.setClientStatus(PacketClientStatus.APPROVED.name());
        reg.setRegType("NEW");
        reg.setPacketId("id");
        reg.setFilePath(File.createTempFile("test", ".zip").getAbsolutePath());
        CenterMachineDto center = new CenterMachineDto();
        center.setMachineRefId("machine");
        when(mockRegistrationRepository.getRegistration("id")).thenReturn(reg);
        when(mockMasterDataService.getRegistrationCenterMachineDetails()).thenReturn(center);
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.SERVER_VERSION)).thenReturn("1.1.5");
        when(mockPacketCryptoService.encrypt(anyString(), any(byte[].class))).thenReturn("abc".getBytes(StandardCharsets.UTF_8));
        Call call = mock(Call.class);
        when(mockSyncRestService.v1syncRID(anyString(), anyString(), anyString())).thenReturn(call);

        doAnswer(invocation -> {
            Callback cb = invocation.getArgument(0);
            RegProcResponseWrapper<List<SyncRIDResponse>> wrapper = new RegProcResponseWrapper<>();
            SyncRIDResponse resp = new SyncRIDResponse();
            resp.setStatus("SUCCESS");
            wrapper.setResponse(Collections.singletonList(resp));
            cb.onResponse(call, Response.success(wrapper));
            return null;
        }).when(call).enqueue(any());

        AsyncPacketTaskCallBack cb = mock(AsyncPacketTaskCallBack.class);
        packetService.syncRegistration("id", cb);

        verify(cb).onComplete("id", PacketTaskStatus.SYNC_COMPLETED);
        verify(cb).inProgress("id");
    }

    @Test
    public void testSyncRegistration_syncFailed() throws Exception {
        Registration reg = new Registration("id");
        reg.setClientStatus(PacketClientStatus.APPROVED.name());
        reg.setRegType("NEW");
        reg.setPacketId("id");
        reg.setFilePath(File.createTempFile("test", ".zip").getAbsolutePath());
        CenterMachineDto center = new CenterMachineDto();
        center.setMachineRefId("machine");
        when(mockRegistrationRepository.getRegistration("id")).thenReturn(reg);
        when(mockMasterDataService.getRegistrationCenterMachineDetails()).thenReturn(center);
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.SERVER_VERSION)).thenReturn("1.1.5");
        when(mockPacketCryptoService.encrypt(anyString(), any(byte[].class))).thenReturn("abc".getBytes(StandardCharsets.UTF_8));
        Call call = mock(Call.class);
        when(mockSyncRestService.v1syncRID(anyString(), anyString(), anyString())).thenReturn(call);

        doAnswer(invocation -> {
            Callback cb = invocation.getArgument(0);
            RegProcResponseWrapper<List<SyncRIDResponse>> wrapper = new RegProcResponseWrapper<>();
            SyncRIDResponse resp = new SyncRIDResponse();
            resp.setStatus("FAIL");
            wrapper.setResponse(Collections.singletonList(resp));
            cb.onResponse(call, Response.success(wrapper));
            return null;
        }).when(call).enqueue(any());

        AsyncPacketTaskCallBack cb = mock(AsyncPacketTaskCallBack.class);
        packetService.syncRegistration("id", cb);

        verify(cb).onComplete("id", PacketTaskStatus.SYNC_FAILED);
    }

    @Test
    public void testSyncRegistration_onFailure() throws Exception {
        Registration reg = new Registration("id");
        reg.setClientStatus(PacketClientStatus.APPROVED.name());
        reg.setRegType("NEW");
        reg.setPacketId("id");
        reg.setFilePath(File.createTempFile("test", ".zip").getAbsolutePath());
        CenterMachineDto center = new CenterMachineDto();
        center.setMachineRefId("machine");
        when(mockRegistrationRepository.getRegistration("id")).thenReturn(reg);
        when(mockMasterDataService.getRegistrationCenterMachineDetails()).thenReturn(center);
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.SERVER_VERSION)).thenReturn("1.1.5");
        when(mockPacketCryptoService.encrypt(anyString(), any(byte[].class))).thenReturn("abc".getBytes(StandardCharsets.UTF_8));
        Call call = mock(Call.class);
        when(mockSyncRestService.v1syncRID(anyString(), anyString(), anyString())).thenReturn(call);

        doAnswer(invocation -> {
            Callback cb = invocation.getArgument(0);
            cb.onFailure(call, new IOException("fail"));
            return null;
        }).when(call).enqueue(any());

        AsyncPacketTaskCallBack cb = mock(AsyncPacketTaskCallBack.class);
        packetService.syncRegistration("id", cb);

        verify(cb).onComplete("id", PacketTaskStatus.SYNC_FAILED);
    }

    @Test
    public void testUploadRegistration_alreadyUploaded() {
        Registration reg = new Registration("id");
        reg.setServerStatus(PacketServerStatus.ACCEPTED.name());
        reg.setClientStatus(PacketClientStatus.APPROVED.name());
        when(mockRegistrationRepository.getRegistration("id")).thenReturn(reg);

        AsyncPacketTaskCallBack cb = mock(AsyncPacketTaskCallBack.class);
        try (MockedStatic<Toast> toast = Mockito.mockStatic(Toast.class)) {
            Toast mockToast = mock(Toast.class);
            toast.when(() -> Toast.makeText(any(), anyString(), anyInt())).thenReturn(mockToast);
            packetService.uploadRegistration("id", cb);
        }
        verify(cb).onComplete("id", PacketTaskStatus.UPLOAD_ALREADY_COMPLETED);
    }

    @Test
    public void testUploadRegistration_successfulUpload() throws IOException {
        Registration reg = new Registration("id");
        reg.setServerStatus(PacketServerStatus.UPLOAD_PENDING.name());
        reg.setClientStatus(PacketClientStatus.APPROVED.name());
        reg.setFilePath(File.createTempFile("test", ".zip").getAbsolutePath());
        when(mockRegistrationRepository.getRegistration("id")).thenReturn(reg);

        Call call = mock(Call.class);
        when(mockSyncRestService.uploadPacket(any())).thenReturn(call);

        doAnswer(invocation -> {
            Callback cb = invocation.getArgument(0);
            RegProcResponseWrapper<UploadResponse> wrapper = new RegProcResponseWrapper<>();
            UploadResponse resp = new UploadResponse();
            resp.setStatus("UPLOADED");
            wrapper.setResponse(resp);
            cb.onResponse(call, Response.success(wrapper));
            return null;
        }).when(call).enqueue(any());

        AsyncPacketTaskCallBack cb = mock(AsyncPacketTaskCallBack.class);
        packetService.uploadRegistration("id", cb);

        verify(cb).onComplete("id", PacketTaskStatus.UPLOAD_COMPLETED);
    }

    @Test
    public void testUploadRegistration_uploadFailed() throws IOException {
        Registration reg = new Registration("id");
        reg.setServerStatus(PacketServerStatus.UPLOAD_PENDING.name());
        reg.setClientStatus(PacketClientStatus.APPROVED.name());
        reg.setFilePath(File.createTempFile("test", ".zip").getAbsolutePath());
        when(mockRegistrationRepository.getRegistration("id")).thenReturn(reg);

        Call call = mock(Call.class);
        when(mockSyncRestService.uploadPacket(any())).thenReturn(call);

        doAnswer(invocation -> {
            Callback cb = invocation.getArgument(0);
            cb.onResponse(call, Response.error(400, ResponseBody.create(null, "fail")));
            return null;
        }).when(call).enqueue(any());

        AsyncPacketTaskCallBack cb = mock(AsyncPacketTaskCallBack.class);
        packetService.uploadRegistration("id", cb);

        verify(cb).onComplete("id", PacketTaskStatus.UPLOAD_FAILED);
    }

    @Test
    public void testUploadRegistration_onFailure() throws IOException {
        Registration reg = new Registration("id");
        reg.setServerStatus(PacketServerStatus.UPLOAD_PENDING.name());
        reg.setClientStatus(PacketClientStatus.APPROVED.name());
        reg.setFilePath(File.createTempFile("test", ".zip").getAbsolutePath());
        when(mockRegistrationRepository.getRegistration("id")).thenReturn(reg);

        Call call = mock(Call.class);
        when(mockSyncRestService.uploadPacket(any())).thenReturn(call);

        doAnswer(invocation -> {
            Callback cb = invocation.getArgument(0);
            cb.onFailure(call, new IOException("fail"));
            return null;
        }).when(call).enqueue(any());

        AsyncPacketTaskCallBack cb = mock(AsyncPacketTaskCallBack.class);
        packetService.uploadRegistration("id", cb);

        verify(cb).onComplete("id", PacketTaskStatus.UPLOAD_FAILED);
    }

    @Test
    public void testSyncAllPacketStatus_emptyList() {
        when(mockRegistrationRepository.getAllRegistrations()).thenReturn(Collections.emptyList());
        packetService.syncAllPacketStatus();
        verify(mockRegistrationRepository).getAllRegistrations();
    }

    @Test
    public void testSyncAllPacketStatus_onFailure() {
        Registration reg = new Registration("id");
        when(mockRegistrationRepository.getAllRegistrations()).thenReturn(Collections.singletonList(reg));
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.SERVER_VERSION)).thenReturn("1.1.5");
        Call call = mock(Call.class);
        when(mockSyncRestService.getV1PacketStatus(any())).thenReturn(call);

        doAnswer(invocation -> {
            Callback cb = invocation.getArgument(0);
            cb.onFailure(call, new IOException("fail"));
            return null;
        }).when(call).enqueue(any());

        try (MockedStatic<Toast> toast = Mockito.mockStatic(Toast.class)) {
            Toast mockToast = mock(Toast.class);
            toast.when(() -> Toast.makeText(any(), anyString(), anyInt())).thenReturn(mockToast);
            packetService.syncAllPacketStatus();
        }
    }

    @Test
    public void test_uploadRegistration_serviceError_RPR_PKR_005() {
        Registration reg = new Registration("id");
        reg.setServerStatus(PacketServerStatus.UPLOAD_PENDING.name());
        reg.setClientStatus(PacketClientStatus.APPROVED.name());
        reg.setFilePath("test.zip");
        when(mockRegistrationRepository.getRegistration("id")).thenReturn(reg);

        Call call = mock(Call.class);
        when(mockSyncRestService.uploadPacket(any())).thenReturn(call);

        AsyncPacketTaskCallBack cb = mock(AsyncPacketTaskCallBack.class);

        RegProcResponseWrapper<UploadResponse> wrapper = new RegProcResponseWrapper<>();
        UploadResponse uploadResponse = new UploadResponse();
        uploadResponse.setStatus("FAILED");
        wrapper.setResponse(uploadResponse);

        ServiceError error = new ServiceError();
        error.setErrorCode("RPR-PKR-005");

        try (MockedStatic<SyncRestUtil> util = Mockito.mockStatic(SyncRestUtil.class)) {
            util.when(() -> SyncRestUtil.getServiceError(wrapper)).thenReturn(error);

            packetService.uploadRegistration("id", cb);

            ArgumentCaptor<Callback<RegProcResponseWrapper<UploadResponse>>> captor = ArgumentCaptor.forClass(Callback.class);
            verify(call).enqueue(captor.capture());

            Response<RegProcResponseWrapper<UploadResponse>> response = Response.success(wrapper);
            captor.getValue().onResponse(call, response);

            verify(mockRegistrationRepository).updateStatus(eq("id"), isNull(), eq(PacketClientStatus.UPLOADED.name()));
            verify(cb).onComplete("id", PacketTaskStatus.UPLOAD_ALREADY_COMPLETED);
        }
    }

    @Test
    public void test_uploadRegistration_serviceError_other() {
        Registration reg = new Registration("id");
        reg.setServerStatus(PacketServerStatus.UPLOAD_PENDING.name());
        reg.setClientStatus(PacketClientStatus.APPROVED.name());
        reg.setFilePath("test.zip");
        when(mockRegistrationRepository.getRegistration("id")).thenReturn(reg);

        Call call = mock(Call.class);
        when(mockSyncRestService.uploadPacket(any())).thenReturn(call);

        AsyncPacketTaskCallBack cb = mock(AsyncPacketTaskCallBack.class);

        RegProcResponseWrapper<UploadResponse> wrapper = new RegProcResponseWrapper<>();
        UploadResponse uploadResponse = new UploadResponse();
        uploadResponse.setStatus("FAILED");
        wrapper.setResponse(uploadResponse);

        ServiceError error = new ServiceError();
        error.setErrorCode("OTHER");

        try (MockedStatic<SyncRestUtil> util = Mockito.mockStatic(SyncRestUtil.class)) {
            util.when(() -> SyncRestUtil.getServiceError(wrapper)).thenReturn(error);

            packetService.uploadRegistration("id", cb);

            ArgumentCaptor<Callback<RegProcResponseWrapper<UploadResponse>>> captor = ArgumentCaptor.forClass(Callback.class);
            verify(call).enqueue(captor.capture());

            Response<RegProcResponseWrapper<UploadResponse>> response = Response.success(wrapper);
            captor.getValue().onResponse(call, response);

            verify(cb).onComplete("id", PacketTaskStatus.UPLOAD_FAILED);
        }
    }

    @Test
    public void test_uploadRegistration_nullServerStatus() {
        Registration reg = new Registration("id");
        reg.setServerStatus(null);
        reg.setClientStatus(PacketClientStatus.APPROVED.name());
        reg.setFilePath("test.zip");
        when(mockRegistrationRepository.getRegistration("id")).thenReturn(reg);

        Call call = mock(Call.class);
        when(mockSyncRestService.uploadPacket(any())).thenReturn(call);

        AsyncPacketTaskCallBack cb = mock(AsyncPacketTaskCallBack.class);

        RegProcResponseWrapper<UploadResponse> wrapper = new RegProcResponseWrapper<>();
        UploadResponse uploadResponse = new UploadResponse();
        uploadResponse.setStatus("UPLOADED");
        wrapper.setResponse(uploadResponse);

        try (MockedStatic<SyncRestUtil> util = Mockito.mockStatic(SyncRestUtil.class)) {
            util.when(() -> SyncRestUtil.getServiceError(wrapper)).thenReturn(null);

            packetService.uploadRegistration("id", cb);

            ArgumentCaptor<Callback<RegProcResponseWrapper<UploadResponse>>> captor = ArgumentCaptor.forClass(Callback.class);
            verify(call).enqueue(captor.capture());

            Response<RegProcResponseWrapper<UploadResponse>> response = Response.success(wrapper);
            captor.getValue().onResponse(call, response);

            verify(cb, atLeastOnce()).onComplete("id", PacketTaskStatus.UPLOAD_COMPLETED);
        }
    }

    @Test
    public void test_uploadRegistration_nullRegistration() {
        when(mockRegistrationRepository.getRegistration("id")).thenReturn(null);
        AsyncPacketTaskCallBack cb = mock(AsyncPacketTaskCallBack.class);
        assertThrows(NullPointerException.class, () -> packetService.uploadRegistration("id", cb));
    }

    @Test
    public void test_uploadRegistration_filePathNull() {
        Registration reg = new Registration("id");
        reg.setServerStatus(PacketServerStatus.UPLOAD_PENDING.name());
        reg.setClientStatus(PacketClientStatus.APPROVED.name());
        reg.setFilePath(null);
        when(mockRegistrationRepository.getRegistration("id")).thenReturn(reg);
        AsyncPacketTaskCallBack cb = mock(AsyncPacketTaskCallBack.class);
        assertThrows(NullPointerException.class, () -> packetService.uploadRegistration("id", cb));
    }

    @Test
    public void test_syncRegistration_nullRegistration() {
        when(mockRegistrationRepository.getRegistration("id")).thenReturn(null);
        AsyncPacketTaskCallBack cb = mock(AsyncPacketTaskCallBack.class);
        assertThrows(NullPointerException.class, () -> packetService.syncRegistration("id", cb));
    }

    @Test
    public void test_syncRegistration_nullCallback() {
        Registration reg = new Registration("id");
        reg.setClientStatus(PacketClientStatus.CREATED.name());
        when(mockRegistrationRepository.getRegistration("id")).thenReturn(reg);
        assertThrows(NullPointerException.class, () -> packetService.syncRegistration("id", null));
    }

    @Test
    public void test_getPacketStatus_nullRegistration() {
        when(mockRegistrationRepository.getRegistration("id")).thenReturn(null);
        assertThrows(NullPointerException.class, () -> packetService.getPacketStatus("id"));
    }

    @Test
    public void test_syncAllPacketStatus_serverVersionNot115() {
        List<Registration> regs = Collections.singletonList(new Registration("id"));
        when(mockRegistrationRepository.getAllRegistrations()).thenReturn(regs);
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.SERVER_VERSION)).thenReturn("2.0.0");
        Call call = mock(Call.class);
        when(mockSyncRestService.getPacketStatus(any())).thenReturn(call);

        packetService.syncAllPacketStatus();

        verify(mockSyncRestService).getPacketStatus(any());
    }

    @Test
    public void test_getPacketStatus_serverStatusNotNull() {
        Registration reg = new Registration("id");
        reg.setServerStatus(PacketServerStatus.UPLOAD_PENDING.name());
        reg.setClientStatus(PacketClientStatus.APPROVED.name());
        when(mockRegistrationRepository.getRegistration("packet123")).thenReturn(reg);
        String status = packetService.getPacketStatus("packet123");
        assertEquals(PacketServerStatus.UPLOAD_PENDING.name(), status);
    }

    @Test
    public void test_getPacketStatus_serverStatusNull() {
        Registration reg = new Registration("id");
        reg.setServerStatus(null);
        reg.setClientStatus(PacketClientStatus.APPROVED.name());
        when(mockRegistrationRepository.getRegistration("packet123")).thenReturn(reg);
        String status = packetService.getPacketStatus("packet123");
        assertEquals(PacketClientStatus.APPROVED.name(), status);
    }

    // ===== isRegisteredPacketApprovalTimeBreached =====

    @Test
    public void isRegisteredPacketApprovalTimeBreached_withNullLimit_returnsFalse() {
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.REG_PAK_MAX_TIME_APPRV_LIMIT)).thenReturn(null);
        assertFalse(packetService.isRegisteredPacketApprovalTimeBreached());
    }

    @Test
    public void isRegisteredPacketApprovalTimeBreached_withEmptyLimit_returnsFalse() {
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.REG_PAK_MAX_TIME_APPRV_LIMIT)).thenReturn("  ");
        assertFalse(packetService.isRegisteredPacketApprovalTimeBreached());
    }

    @Test
    public void isRegisteredPacketApprovalTimeBreached_withZeroDays_returnsFalse() {
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.REG_PAK_MAX_TIME_APPRV_LIMIT)).thenReturn("0");
        assertFalse(packetService.isRegisteredPacketApprovalTimeBreached());
    }

    @Test
    public void isRegisteredPacketApprovalTimeBreached_withNegativeDays_returnsFalse() {
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.REG_PAK_MAX_TIME_APPRV_LIMIT)).thenReturn("-5");
        assertFalse(packetService.isRegisteredPacketApprovalTimeBreached());
    }

    @Test
    public void isRegisteredPacketApprovalTimeBreached_withNoOldestRegistration_returnsFalse() {
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.REG_PAK_MAX_TIME_APPRV_LIMIT)).thenReturn("7");
        when(mockRegistrationRepository.getOldestRegistrationByStatus(PacketClientStatus.CREATED.name())).thenReturn(null);
        assertFalse(packetService.isRegisteredPacketApprovalTimeBreached());
    }

    @Test
    public void isRegisteredPacketApprovalTimeBreached_withRecentRegistration_returnsFalse() {
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.REG_PAK_MAX_TIME_APPRV_LIMIT)).thenReturn("7");
        Registration reg = new Registration("id");
        reg.setCrDtime(System.currentTimeMillis());
        when(mockRegistrationRepository.getOldestRegistrationByStatus(PacketClientStatus.CREATED.name())).thenReturn(reg);
        assertFalse(packetService.isRegisteredPacketApprovalTimeBreached());
    }

    @Test
    public void isRegisteredPacketApprovalTimeBreached_withOldRegistration_returnsTrue() {
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.REG_PAK_MAX_TIME_APPRV_LIMIT)).thenReturn("1");
        Registration reg = new Registration("id");
        reg.setCrDtime(System.currentTimeMillis() - (2L * 24 * 60 * 60 * 1000));
        when(mockRegistrationRepository.getOldestRegistrationByStatus(PacketClientStatus.CREATED.name())).thenReturn(reg);
        assertTrue(packetService.isRegisteredPacketApprovalTimeBreached());
    }

    @Test
    public void isRegisteredPacketApprovalTimeBreached_withInvalidConfig_returnsFalse() {
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.REG_PAK_MAX_TIME_APPRV_LIMIT)).thenReturn("not-a-number");
        assertFalse(packetService.isRegisteredPacketApprovalTimeBreached());
    }

    // ===== validatingLastExportDuration =====

    @Test
    public void validatingLastExportDuration_withNullLimit_returnsFalse() {
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.OPT_TO_REG_LAST_EXPORT_REG_PKTS_TIME)).thenReturn(null);
        assertFalse(packetService.validatingLastExportDuration());
    }

    @Test
    public void validatingLastExportDuration_withEmptyLimit_returnsFalse() {
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.OPT_TO_REG_LAST_EXPORT_REG_PKTS_TIME)).thenReturn("  ");
        assertFalse(packetService.validatingLastExportDuration());
    }

    @Test
    public void validatingLastExportDuration_withZeroDays_returnsFalse() {
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.OPT_TO_REG_LAST_EXPORT_REG_PKTS_TIME)).thenReturn("0");
        assertFalse(packetService.validatingLastExportDuration());
    }

    @Test
    public void validatingLastExportDuration_withNegativeDays_returnsFalse() {
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.OPT_TO_REG_LAST_EXPORT_REG_PKTS_TIME)).thenReturn("-3");
        assertFalse(packetService.validatingLastExportDuration());
    }

    @Test
    public void validatingLastExportDuration_withNoOldestApprovedRegistration_returnsFalse() {
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.OPT_TO_REG_LAST_EXPORT_REG_PKTS_TIME)).thenReturn("7");
        when(mockRegistrationRepository.getOldestRegistrationByStatus(PacketClientStatus.APPROVED.name())).thenReturn(null);
        assertFalse(packetService.validatingLastExportDuration());
    }

    @Test
    public void validatingLastExportDuration_withNullCreationTime_returnsFalse() {
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.OPT_TO_REG_LAST_EXPORT_REG_PKTS_TIME)).thenReturn("7");
        Registration reg = new Registration("id");
        reg.setCrDtime(null);
        when(mockRegistrationRepository.getOldestRegistrationByStatus(PacketClientStatus.APPROVED.name())).thenReturn(reg);
        assertFalse(packetService.validatingLastExportDuration());
    }

    @Test
    public void validatingLastExportDuration_withRecentApprovedRegistration_returnsFalse() {
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.OPT_TO_REG_LAST_EXPORT_REG_PKTS_TIME)).thenReturn("7");
        Registration reg = new Registration("id");
        reg.setCrDtime(System.currentTimeMillis());
        when(mockRegistrationRepository.getOldestRegistrationByStatus(PacketClientStatus.APPROVED.name())).thenReturn(reg);
        assertFalse(packetService.validatingLastExportDuration());
    }

    @Test
    public void validatingLastExportDuration_withOldApprovedRegistration_returnsTrue() {
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.OPT_TO_REG_LAST_EXPORT_REG_PKTS_TIME)).thenReturn("1");
        Registration reg = new Registration("id");
        reg.setCrDtime(System.currentTimeMillis() - (2L * 24 * 60 * 60 * 1000));
        when(mockRegistrationRepository.getOldestRegistrationByStatus(PacketClientStatus.APPROVED.name())).thenReturn(reg);
        assertTrue(packetService.validatingLastExportDuration());
    }

    @Test
    public void validatingLastExportDuration_withInvalidConfig_returnsFalse() {
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.OPT_TO_REG_LAST_EXPORT_REG_PKTS_TIME)).thenReturn("abc");
        assertFalse(packetService.validatingLastExportDuration());
    }

    // ===== isMaxPacketCountLimitReached =====

    @Test
    public void isMaxPacketCountLimitReached_withNullConfig_returnsFalse() {
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.REG_PAK_MAX_CNT_OFFLINE_FREQ)).thenReturn(null);
        assertFalse(packetService.isMaxPacketCountLimitReached());
    }

    @Test
    public void isMaxPacketCountLimitReached_withEmptyConfig_returnsFalse() {
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.REG_PAK_MAX_CNT_OFFLINE_FREQ)).thenReturn("  ");
        assertFalse(packetService.isMaxPacketCountLimitReached());
    }

    @Test
    public void isMaxPacketCountLimitReached_withZeroMaxCount_returnsFalse() {
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.REG_PAK_MAX_CNT_OFFLINE_FREQ)).thenReturn("0");
        assertFalse(packetService.isMaxPacketCountLimitReached());
    }

    @Test
    public void isMaxPacketCountLimitReached_withCountBelowLimit_returnsFalse() {
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.REG_PAK_MAX_CNT_OFFLINE_FREQ)).thenReturn("10");
        when(mockRegistrationRepository.getYetToExportCount()).thenReturn(5);
        assertFalse(packetService.isMaxPacketCountLimitReached());
        verify(mockAuditManagerService).audit(AuditEvent.SYNC_PKT_COUNT_VALIDATE, Components.REGISTRATION);
    }

    @Test
    public void isMaxPacketCountLimitReached_withCountAtLimit_returnsTrue() {
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.REG_PAK_MAX_CNT_OFFLINE_FREQ)).thenReturn("5");
        when(mockRegistrationRepository.getYetToExportCount()).thenReturn(5);
        assertTrue(packetService.isMaxPacketCountLimitReached());
        verify(mockAuditManagerService).audit(AuditEvent.SYNC_PKT_COUNT_VALIDATE, Components.REGISTRATION);
    }

    @Test
    public void isMaxPacketCountLimitReached_withCountExceedingLimit_returnsTrue() {
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.REG_PAK_MAX_CNT_OFFLINE_FREQ)).thenReturn("3");
        when(mockRegistrationRepository.getYetToExportCount()).thenReturn(10);
        assertTrue(packetService.isMaxPacketCountLimitReached());
    }

    @Test
    public void isMaxPacketCountLimitReached_withInvalidConfig_returnsFalse() {
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.REG_PAK_MAX_CNT_OFFLINE_FREQ)).thenReturn("not-a-number");
        assertFalse(packetService.isMaxPacketCountLimitReached());
    }

    @Test
    public void isMaxPacketCountLimitReached_whenRepositoryThrows_returnsFalse() {
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.REG_PAK_MAX_CNT_OFFLINE_FREQ)).thenReturn("5");
        when(mockRegistrationRepository.getYetToExportCount()).thenThrow(new RuntimeException("db error"));
        assertFalse(packetService.isMaxPacketCountLimitReached());
    }

    // ===== isMaxNotApprovedPacketCountLimitReached =====

    @Test
    public void isMaxNotApprovedPacketCountLimitReached_withZeroMaxCount_returnsFalse() {
        when(mockGlobalParamRepository.getCachedIntRegMaxCountApproveLimit()).thenReturn(0);
        assertFalse(packetService.isMaxNotApprovedPacketCountLimitReached());
    }

    @Test
    public void isMaxNotApprovedPacketCountLimitReached_withCountBelowLimit_returnsFalse() {
        when(mockGlobalParamRepository.getCachedIntRegMaxCountApproveLimit()).thenReturn(10);
        when(mockRegistrationRepository.getAllRegistrationByStatus(PacketClientStatus.CREATED.name())).thenReturn(5);
        assertFalse(packetService.isMaxNotApprovedPacketCountLimitReached());
    }

    @Test
    public void isMaxNotApprovedPacketCountLimitReached_withCountAtLimit_returnsTrue() {
        when(mockGlobalParamRepository.getCachedIntRegMaxCountApproveLimit()).thenReturn(5);
        when(mockRegistrationRepository.getAllRegistrationByStatus(PacketClientStatus.CREATED.name())).thenReturn(5);
        assertTrue(packetService.isMaxNotApprovedPacketCountLimitReached());
    }

    @Test
    public void isMaxNotApprovedPacketCountLimitReached_withCountExceedingLimit_returnsTrue() {
        when(mockGlobalParamRepository.getCachedIntRegMaxCountApproveLimit()).thenReturn(3);
        when(mockRegistrationRepository.getAllRegistrationByStatus(PacketClientStatus.CREATED.name())).thenReturn(7);
        assertTrue(packetService.isMaxNotApprovedPacketCountLimitReached());
    }

    @Test
    public void isMaxNotApprovedPacketCountLimitReached_whenRepoThrows_returnsFalse() {
        when(mockGlobalParamRepository.getCachedIntRegMaxCountApproveLimit()).thenThrow(new RuntimeException("error"));
        assertFalse(packetService.isMaxNotApprovedPacketCountLimitReached());
    }

    // ===== deleteRegistrationPackets =====

    @Test
    public void deleteRegistrationPackets_withNullConfig_doesNotDelete() {
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.REG_DELETION_CONFIGURED_DAYS)).thenReturn(null);
        packetService.deleteRegistrationPackets();
        verify(mockRegistrationRepository, never()).findByServerStatusAndCrDtimeBefore(any(), anyLong());
    }

    @Test
    public void deleteRegistrationPackets_withInvalidConfig_doesNotDelete() {
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.REG_DELETION_CONFIGURED_DAYS)).thenReturn("invalid");
        packetService.deleteRegistrationPackets();
        verify(mockRegistrationRepository, never()).findByServerStatusAndCrDtimeBefore(any(), anyLong());
    }

    @Test
    public void deleteRegistrationPackets_withNoMatchingRegistrations_doesNotDelete() {
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.REG_DELETION_CONFIGURED_DAYS)).thenReturn("30");
        when(mockRegistrationRepository.findByServerStatusAndCrDtimeBefore(any(), anyLong())).thenReturn(Collections.emptyList());
        packetService.deleteRegistrationPackets();
        verify(mockRegistrationRepository, never()).deleteRegistration(anyString());
    }

    @Test
    public void deleteRegistrationPackets_withExistingFile_deletesFileAndRecord() throws IOException {
        File tempFile = File.createTempFile("test-packet", ".zip");
        tempFile.deleteOnExit();

        Registration reg = new Registration("packet-del-1");
        reg.setFilePath(tempFile.getAbsolutePath());

        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.REG_DELETION_CONFIGURED_DAYS)).thenReturn("30");
        when(mockRegistrationRepository.findByServerStatusAndCrDtimeBefore(any(), anyLong())).thenReturn(Collections.singletonList(reg));

        packetService.deleteRegistrationPackets();

        verify(mockRegistrationRepository).deleteRegistration("packet-del-1");
    }

    @Test
    public void deleteRegistrationPackets_withNonExistentFile_deletesRecord() {
        Registration reg = new Registration("packet-del-2");
        reg.setFilePath("/non/existent/path/packet.zip");

        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.REG_DELETION_CONFIGURED_DAYS)).thenReturn("30");
        when(mockRegistrationRepository.findByServerStatusAndCrDtimeBefore(any(), anyLong())).thenReturn(Collections.singletonList(reg));

        packetService.deleteRegistrationPackets();

        verify(mockRegistrationRepository).deleteRegistration("packet-del-2");
    }

    @Test
    public void deleteRegistrationPackets_withNullFilePath_deletesRecord() {
        Registration reg = new Registration("packet-del-3");
        reg.setFilePath(null);

        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.REG_DELETION_CONFIGURED_DAYS)).thenReturn("30");
        when(mockRegistrationRepository.findByServerStatusAndCrDtimeBefore(any(), anyLong())).thenReturn(Collections.singletonList(reg));

        packetService.deleteRegistrationPackets();

        verify(mockRegistrationRepository).deleteRegistration("packet-del-3");
    }

    @Test
    public void deleteRegistrationPackets_whenRepoThrows_completesWithoutException() {
        Registration reg = new Registration("packet-del-4");
        reg.setFilePath(null);

        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.REG_DELETION_CONFIGURED_DAYS)).thenReturn("30");
        when(mockRegistrationRepository.findByServerStatusAndCrDtimeBefore(any(), anyLong())).thenReturn(Collections.singletonList(reg));
        doThrow(new RuntimeException("db error")).when(mockRegistrationRepository).deleteRegistration(anyString());

        packetService.deleteRegistrationPackets();
    }

    @Test
    public void deleteRegistrationPackets_withMultipleRegistrations_deletesAll() throws IOException {
        File tempFile1 = File.createTempFile("packet1", ".zip");
        File tempFile2 = File.createTempFile("packet2", ".zip");
        tempFile1.deleteOnExit();
        tempFile2.deleteOnExit();

        Registration reg1 = new Registration("packet-del-5");
        reg1.setFilePath(tempFile1.getAbsolutePath());
        Registration reg2 = new Registration("packet-del-6");
        reg2.setFilePath(tempFile2.getAbsolutePath());

        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.REG_DELETION_CONFIGURED_DAYS)).thenReturn("30");
        when(mockRegistrationRepository.findByServerStatusAndCrDtimeBefore(any(), anyLong())).thenReturn(Arrays.asList(reg1, reg2));

        packetService.deleteRegistrationPackets();

        verify(mockRegistrationRepository).deleteRegistration("packet-del-5");
        verify(mockRegistrationRepository).deleteRegistration("packet-del-6");
    }

    // ===== Missing syncRegistration branches =====

    @Test
    public void syncRegistration_withNonV115Version_usesLegacyEndpoint() throws Exception {
        Registration reg = new Registration("id");
        reg.setClientStatus(PacketClientStatus.APPROVED.name());
        reg.setRegType("NEW");
        reg.setPacketId("id");
        reg.setFilePath(File.createTempFile("test", ".zip").getAbsolutePath());
        CenterMachineDto center = new CenterMachineDto();
        center.setMachineRefId("machine");
        when(mockRegistrationRepository.getRegistration("id")).thenReturn(reg);
        when(mockMasterDataService.getRegistrationCenterMachineDetails()).thenReturn(center);
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.SERVER_VERSION)).thenReturn("2.0.0");
        when(mockPacketCryptoService.encrypt(anyString(), any(byte[].class))).thenReturn("abc".getBytes(StandardCharsets.UTF_8));
        Call call = mock(Call.class);
        when(mockSyncRestService.syncRID(anyString(), anyString(), anyString())).thenReturn(call);

        doAnswer(invocation -> {
            Callback cb = invocation.getArgument(0);
            RegProcResponseWrapper<List<SyncRIDResponse>> wrapper = new RegProcResponseWrapper<>();
            SyncRIDResponse resp = new SyncRIDResponse();
            resp.setStatus("SUCCESS");
            wrapper.setResponse(Collections.singletonList(resp));
            cb.onResponse(call, Response.success(wrapper));
            return null;
        }).when(call).enqueue(any());

        AsyncPacketTaskCallBack cb = mock(AsyncPacketTaskCallBack.class);
        packetService.syncRegistration("id", cb);

        verify(mockSyncRestService).syncRID(anyString(), anyString(), anyString());
        verify(cb).onComplete("id", PacketTaskStatus.SYNC_COMPLETED);
    }

    @Test
    public void syncRegistration_withHttpErrorResponse_callsSyncFailed() throws Exception {
        Registration reg = new Registration("id");
        reg.setClientStatus(PacketClientStatus.APPROVED.name());
        reg.setRegType("NEW");
        reg.setPacketId("id");
        reg.setFilePath(File.createTempFile("test", ".zip").getAbsolutePath());
        CenterMachineDto center = new CenterMachineDto();
        center.setMachineRefId("machine");
        when(mockRegistrationRepository.getRegistration("id")).thenReturn(reg);
        when(mockMasterDataService.getRegistrationCenterMachineDetails()).thenReturn(center);
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.SERVER_VERSION)).thenReturn("1.1.5");
        when(mockPacketCryptoService.encrypt(anyString(), any(byte[].class))).thenReturn("abc".getBytes(StandardCharsets.UTF_8));
        Call call = mock(Call.class);
        when(mockSyncRestService.v1syncRID(anyString(), anyString(), anyString())).thenReturn(call);

        doAnswer(invocation -> {
            Callback cb = invocation.getArgument(0);
            cb.onResponse(call, Response.error(500, ResponseBody.create(null, "error")));
            return null;
        }).when(call).enqueue(any());

        AsyncPacketTaskCallBack cb = mock(AsyncPacketTaskCallBack.class);
        packetService.syncRegistration("id", cb);

        verify(cb).onComplete("id", PacketTaskStatus.SYNC_FAILED);
    }

    @Test
    public void syncRegistration_withAllAdditionalInfoFields_completesSyncSuccessfully() throws Exception {
        Registration reg = new Registration("id");
        reg.setClientStatus(PacketClientStatus.APPROVED.name());
        reg.setRegType("NEW");
        reg.setPacketId("id");
        reg.setFilePath(File.createTempFile("test", ".zip").getAbsolutePath());
        String json = "{\"name\":\"John\",\"phone\":\"9999999999\",\"email\":\"john@test.com\",\"langCode\":\"eng\"}";
        reg.setAdditionalInfo(json.getBytes(StandardCharsets.UTF_8));
        CenterMachineDto center = new CenterMachineDto();
        center.setMachineRefId("machine");
        when(mockRegistrationRepository.getRegistration("id")).thenReturn(reg);
        when(mockMasterDataService.getRegistrationCenterMachineDetails()).thenReturn(center);
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.SERVER_VERSION)).thenReturn("1.1.5");
        when(mockPacketCryptoService.encrypt(anyString(), any(byte[].class))).thenReturn("abc".getBytes(StandardCharsets.UTF_8));
        Call call = mock(Call.class);
        when(mockSyncRestService.v1syncRID(anyString(), anyString(), anyString())).thenReturn(call);

        doAnswer(invocation -> {
            Callback cb = invocation.getArgument(0);
            RegProcResponseWrapper<List<SyncRIDResponse>> wrapper = new RegProcResponseWrapper<>();
            SyncRIDResponse resp = new SyncRIDResponse();
            resp.setStatus("SUCCESS");
            wrapper.setResponse(Collections.singletonList(resp));
            cb.onResponse(call, Response.success(wrapper));
            return null;
        }).when(call).enqueue(any());

        AsyncPacketTaskCallBack cb = mock(AsyncPacketTaskCallBack.class);
        packetService.syncRegistration("id", cb);

        verify(cb).onComplete("id", PacketTaskStatus.SYNC_COMPLETED);
    }

    @Test
    public void syncRegistration_withMissingOptionalInfoFields_completesSyncSuccessfully() throws Exception {
        Registration reg = new Registration("id");
        reg.setClientStatus(PacketClientStatus.REJECTED.name());
        reg.setClientStatusComment("rejected comment");
        reg.setRegType("NEW");
        reg.setPacketId("id");
        reg.setFilePath(File.createTempFile("test", ".zip").getAbsolutePath());
        String json = "{\"langCode\":\"eng\"}";
        reg.setAdditionalInfo(json.getBytes(StandardCharsets.UTF_8));
        CenterMachineDto center = new CenterMachineDto();
        center.setMachineRefId("machine");
        when(mockRegistrationRepository.getRegistration("id")).thenReturn(reg);
        when(mockMasterDataService.getRegistrationCenterMachineDetails()).thenReturn(center);
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.SERVER_VERSION)).thenReturn("1.1.5");
        when(mockPacketCryptoService.encrypt(anyString(), any(byte[].class))).thenReturn("abc".getBytes(StandardCharsets.UTF_8));
        Call call = mock(Call.class);
        when(mockSyncRestService.v1syncRID(anyString(), anyString(), anyString())).thenReturn(call);

        doAnswer(invocation -> {
            Callback cb = invocation.getArgument(0);
            RegProcResponseWrapper<List<SyncRIDResponse>> wrapper = new RegProcResponseWrapper<>();
            SyncRIDResponse resp = new SyncRIDResponse();
            resp.setStatus("SUCCESS");
            wrapper.setResponse(Collections.singletonList(resp));
            cb.onResponse(call, Response.success(wrapper));
            return null;
        }).when(call).enqueue(any());

        AsyncPacketTaskCallBack cb = mock(AsyncPacketTaskCallBack.class);
        packetService.syncRegistration("id", cb);

        verify(cb).onComplete("id", PacketTaskStatus.SYNC_COMPLETED);
    }

    // ===== Missing syncAllPacketStatus branches =====

    @Test
    public void syncAllPacketStatus_withHttpErrorResponse_doesNotUpdateStatus() {
        List<Registration> registrations = Collections.singletonList(new Registration("id"));
        when(mockRegistrationRepository.getAllRegistrations()).thenReturn(registrations);
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.SERVER_VERSION)).thenReturn("1.1.5");
        Call call = mock(Call.class);
        when(mockSyncRestService.getV1PacketStatus(any())).thenReturn(call);
        lenient().when(mockContext.getString(anyInt(), anyString())).thenReturn("error message");

        doAnswer(invocation -> {
            Callback cb = invocation.getArgument(0);
            cb.onResponse(call, Response.error(400, ResponseBody.create(null, "error")));
            return null;
        }).when(call).enqueue(any());

        try (MockedStatic<Toast> toast = Mockito.mockStatic(Toast.class)) {
            Toast mockToast = mock(Toast.class);
            toast.when(() -> Toast.makeText(any(), anyString(), anyInt())).thenReturn(mockToast);
            packetService.syncAllPacketStatus();
        }

        verify(mockRegistrationRepository, never()).updateServerStatusWithTimestamp(any(), any(), anyLong());
    }

    @Test
    public void syncAllPacketStatus_withNullPacketStatusList_doesNotUpdateStatus() {
        List<Registration> registrations = Collections.singletonList(new Registration("id"));
        when(mockRegistrationRepository.getAllRegistrations()).thenReturn(registrations);
        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.SERVER_VERSION)).thenReturn("1.1.5");
        Call call = mock(Call.class);
        when(mockSyncRestService.getV1PacketStatus(any())).thenReturn(call);
        lenient().when(mockContext.getString(anyInt(), anyInt())).thenReturn("0 synced");

        doAnswer(invocation -> {
            Callback cb = invocation.getArgument(0);
            PacketStatusResponse response = new PacketStatusResponse();
            response.setResponse(null);
            cb.onResponse(call, Response.success(response));
            return null;
        }).when(call).enqueue(any());

        try (MockedStatic<Toast> toast = Mockito.mockStatic(Toast.class)) {
            Toast mockToast = mock(Toast.class);
            toast.when(() -> Toast.makeText(any(), anyString(), anyInt())).thenReturn(mockToast);
            packetService.syncAllPacketStatus();
        }

        verify(mockRegistrationRepository, never()).updateServerStatusWithTimestamp(any(), any(), anyLong());
    }

    @Test
    public void syncAllPacketStatus_withNullRegistrations_doesNotCallSyncService() {
        when(mockRegistrationRepository.getAllRegistrations()).thenReturn(null);
        packetService.syncAllPacketStatus();
        verify(mockRegistrationRepository).getAllRegistrations();
        verifyNoInteractions(mockSyncRestService);
    }

    // ===== uploadRegistration single-arg delegation =====

    @Test
    public void uploadRegistration_withSingleArg_delegatesToOverloadedMethod() {
        doNothing().when(packetService).uploadRegistration(anyString(), any(AsyncPacketTaskCallBack.class));
        packetService.uploadRegistration("pid");
        verify(packetService).uploadRegistration(eq("pid"), any(AsyncPacketTaskCallBack.class));
    }

}
