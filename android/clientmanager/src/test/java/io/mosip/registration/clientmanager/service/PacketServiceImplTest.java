package io.mosip.registration.clientmanager.service;

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
import org.junit.*;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import retrofit2.*;

import java.io.*;
import java.util.*;

@RunWith(MockitoJUnitRunner .class)
public class PacketServiceImplTest {

    @Mock private Context mockContext;
    @Mock private RegistrationRepository mockRegistrationRepository;
    @Mock private IPacketCryptoService mockPacketCryptoService;
    @Mock private SyncRestService mockSyncRestService;
    @Mock private MasterDataService mockMasterDataService;
    @Mock private GlobalParamRepository mockGlobalParamRepository;

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
                mockSyncRestService, mockMasterDataService, mockGlobalParamRepository
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

            Mockito.verify(mockRegistrationRepository).updateStatus(
                    Mockito.eq("reg123"),
                    Mockito.anyString(),
                    Mockito.eq(PacketClientStatus.UPLOADED.name())
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


}
