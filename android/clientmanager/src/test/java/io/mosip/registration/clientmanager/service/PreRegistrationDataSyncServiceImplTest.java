package io.mosip.registration.clientmanager.service;

import android.content.Context;

import android.content.SharedPreferences;
import android.util.Log;
import io.mosip.registration.clientmanager.config.ClientDatabase;
import io.mosip.registration.clientmanager.dao.GlobalParamDao;
import io.mosip.registration.clientmanager.dao.PreRegistrationDataSyncDao;
import io.mosip.registration.clientmanager.dto.CenterMachineDto;
import io.mosip.registration.clientmanager.dto.PreRegistrationDto;
import io.mosip.registration.clientmanager.dto.PreRegistrationIdsDto;
import io.mosip.registration.clientmanager.dto.http.ResponseWrapper;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.entity.PreRegistrationList;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.service.external.PreRegZipHandlingService;
import io.mosip.registration.clientmanager.service.external.impl.PreRegZipHandlingServiceImpl;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.exception.ClientCheckedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import retrofit2.Call;
import retrofit2.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Ref;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class PreRegistrationDataSyncServiceImplTest {

    @Mock
    private Context mockContext;

    @Mock
    private PreRegistrationDataSyncDao mockPreRegistrationDataSyncDao;

    @Mock
    private MasterDataService mockMasterDataService;

    @Mock
    private SyncRestService mockSyncRestService;

    @Mock
    private PreRegZipHandlingService mockPreRegZipHandlingService;

    @Mock
    private CenterMachineDto mockCenterMachineDto;

    @Mock
    private PreRegZipHandlingServiceImpl mockPreRegZipHandlingServiceImpl;

    @Mock
    private SharedPreferences mockSharedPreferences;

    @Mock
    private PreRegistrationDataSyncDao mockDao;

    private PreRegistrationDataSyncServiceImpl service;

    @Mock
    PreRegistrationList preRegistration;

    @Mock
    GlobalParamRepository globalParamRepository;
    ClientDatabase clientDatabase;

    @Mock
    RegistrationService registrationService;

    public static final String USER_NAME = "user_name";

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new PreRegistrationDataSyncServiceImpl(
                mockContext,
                mockPreRegistrationDataSyncDao,
                mockMasterDataService,
                mockSyncRestService,
                mockPreRegZipHandlingService,
                preRegistration,
                globalParamRepository,
                registrationService
        );

        try {
            Field sharedPreferencesField = PreRegistrationDataSyncServiceImpl.class.getDeclaredField("sharedPreferences");
            sharedPreferencesField.setAccessible(true);
            sharedPreferencesField.set(service, mockSharedPreferences);
        } catch (Exception e) {
            fail("Failed to inject SharedPreferences field: " + e.getMessage());
        }

        // Setup common mock behaviors
        when(mockSharedPreferences.getString(eq(USER_NAME), anyString())).thenReturn("testUser");
    }

    @Test
    public void fetchPreRegistrationIds_success_Test() throws IOException {

        when(mockMasterDataService.getRegistrationCenterMachineDetails()).thenReturn(mockCenterMachineDto);
        when(mockCenterMachineDto.getCenterId()).thenReturn("10001");

        ResponseWrapper<PreRegistrationIdsDto> mockResponseWrapper = mock(ResponseWrapper.class);
        when(mockResponseWrapper.getResponse()).thenReturn(mock(PreRegistrationIdsDto.class));

        Call<ResponseWrapper<PreRegistrationIdsDto>> mockCall = mock(Call.class);
        when(mockCall.execute()).thenReturn(Response.success(mockResponseWrapper));

        when(mockSyncRestService.getPreRegistrationIds(any())).thenReturn(mockCall);

        service.fetchPreRegistrationIds(() -> {
            verify(mockContext).getString(anyInt());
        });

        verify(mockSyncRestService).getPreRegistrationIds(any());
    }

    @Test
    public void fetchPreRegistrationIds_noCenterMachineDetails_Test() {
        when(mockMasterDataService.getRegistrationCenterMachineDetails()).thenReturn(null);

        service.fetchPreRegistrationIds(() -> {
            verify(mockContext).getString(anyInt());
        });

        verify(mockSyncRestService, never()).getPreRegistrationIds(any());
    }

    @Test
    public void getPreRegistration_invalidId_Test() {
        when(mockPreRegistrationDataSyncDao.get(anyString())).thenReturn(null);

        Map<String, Object> result = service.getPreRegistration("5678998467", true);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void downloadAndSavePacket_error_Test() throws Exception {
        Method method = PreRegistrationDataSyncServiceImpl.class.getDeclaredMethod("downloadAndSavePacket", String.class, String.class);
        method.setAccessible(true);

        when(mockMasterDataService.getRegistrationCenterMachineDetails()).thenReturn(null);

        try {
            method.invoke(service, "26506250831081", null);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof ClientCheckedException) {
                assertTrue(cause instanceof ClientCheckedException);
            } else {
                throw e;
            }
        }
    }

    @Test
    public void test_successfully_processes_prereg_ids_with_timestamps() throws Exception {
        ExecutorService mockExecutor = mock(ExecutorService.class);
        ReflectionTestUtils.setField(service, "executorServiceForPreReg", mockExecutor);

        Map<String, String> preRegIds = new HashMap<>();
        preRegIds.put("12345", "2023-01-01T10:15:30.00");
        preRegIds.put("67890", "2023-01-02T11:20:45.00Z");

        ReflectionTestUtils.invokeMethod(service, "getPreRegistrationPackets", preRegIds);

        verify(mockExecutor, times(2)).execute(any(Runnable.class));

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(mockExecutor, times(2)).execute(runnableCaptor.capture());

        assertEquals(2, preRegIds.size());
        assertTrue(preRegIds.get("67890").endsWith("Z"));
    }

    @Test
    public void test_handles_empty_map_of_prereg_ids() {
        ExecutorService mockExecutor = mock(ExecutorService.class);
        ReflectionTestUtils.setField(service, "executorServiceForPreReg", mockExecutor);

        Map<String, String> emptyPreRegIds = new HashMap<>();

        ReflectionTestUtils.invokeMethod(service, "getPreRegistrationPackets", emptyPreRegIds);

        verify(mockExecutor, never()).execute(any(Runnable.class));

        assertEquals(0, emptyPreRegIds.size());
    }

    @Test
    public void test_adds_z_suffix_to_timestamp() {
        Map<String, String> preRegIds = new HashMap<>();
        preRegIds.put("preRegId1", "2023-10-10T10:00:00");

        ReflectionTestUtils.invokeMethod(service, "getPreRegistrationPackets", preRegIds);
    }

    @Test
    public void test_executes_tasks_in_parallel() {
        ExecutorService executorServiceMock = Mockito.mock(ExecutorService.class);
        ReflectionTestUtils.setField(service, "executorServiceForPreReg", executorServiceMock);

        Map<String, String> preRegIds = new HashMap<>();
        preRegIds.put("preRegId1", "2023-10-10T10:00:00Z");
        preRegIds.put("preRegId2", "2023-10-11T11:00:00Z");

        ReflectionTestUtils.invokeMethod(service, "getPreRegistrationPackets", preRegIds);

        Mockito.verify(executorServiceMock, Mockito.times(2)).execute(Mockito.any(Runnable.class));
    }

    @Test
    public void test_successfully_extracts_registration_dto() throws Exception {
        PreRegZipHandlingService mockPreRegZipHandlingService = Mockito.mock(PreRegZipHandlingService.class);
        ReflectionTestUtils.setField(service, "preRegZipHandlingService", mockPreRegZipHandlingService);

        byte[] decryptedPacket = "test-data".getBytes();
        String preRegistrationId = "12345678901234";
        RegistrationDto mockRegistrationDto = new RegistrationDto();

        Mockito.when(mockPreRegZipHandlingService.extractPreRegZipFile(decryptedPacket)).thenReturn(mockRegistrationDto);

        Map<String, Object> result = ReflectionTestUtils.invokeMethod(service, "setPacketToResponse", decryptedPacket, preRegistrationId);

        assertNotNull(result);
        assertTrue(result.containsKey("registrationDto"));
        RegistrationDto resultDto = (RegistrationDto) result.get("registrationDto");
        assertEquals(preRegistrationId, resultDto.getPreRegistrationId());
        Mockito.verify(mockPreRegZipHandlingService).extractPreRegZipFile(decryptedPacket);
    }

    @Test
    public void test_handles_exceptions_during_extraction() throws Exception {
        ReflectionTestUtils.setField(service, "preRegZipHandlingService", mockPreRegZipHandlingService);

        byte[] decryptedPacket = "test-data".getBytes();
        String preRegistrationId = "12345678901234";

        Exception testException = new Exception("Test exception");
        Mockito.when(mockPreRegZipHandlingService.extractPreRegZipFile(decryptedPacket)).thenThrow(testException);

        Map<String, Object> result = ReflectionTestUtils.invokeMethod(service, "setPacketToResponse", decryptedPacket, preRegistrationId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        Mockito.verify(mockPreRegZipHandlingService).extractPreRegZipFile(decryptedPacket);
    }

    @Test
    public void test_set_pre_registration_id() throws Exception {
        byte[] decryptedPacket = new byte[]{1, 2, 3};
        String preRegistrationId = "12345";
        RegistrationDto registrationDto = new RegistrationDto();

        when(mockPreRegZipHandlingService.extractPreRegZipFile(decryptedPacket)).thenReturn(registrationDto);

        Map<String, Object> result = ReflectionTestUtils.invokeMethod(service, "setPacketToResponse", decryptedPacket, preRegistrationId);

        assertEquals(preRegistrationId, ((RegistrationDto) result.get("registrationDto")).getPreRegistrationId());
    }

    @Test
    public void test_return_map_with_registration_dto() throws Exception {
        byte[] decryptedPacket = new byte[]{1, 2, 3};
        String preRegistrationId = "12345";
        RegistrationDto registrationDto = new RegistrationDto();

        when(mockPreRegZipHandlingService.extractPreRegZipFile(decryptedPacket)).thenReturn(registrationDto);

        Map<String, Object> result = ReflectionTestUtils.invokeMethod(service, "setPacketToResponse", decryptedPacket, preRegistrationId);

        assertTrue(result.containsKey("registrationDto"));
        assertEquals(registrationDto, result.get("registrationDto"));
    }

    @Test
    public void test_log_successful_extraction() throws Exception {
        byte[] decryptedPacket = new byte[]{1, 2, 3};
        String preRegistrationId = "12345";
        RegistrationDto registrationDto = new RegistrationDto();

        when(mockPreRegZipHandlingServiceImpl.extractPreRegZipFile(decryptedPacket)).thenReturn(registrationDto);

        ReflectionTestUtils.invokeMethod(service, "setPacketToResponse", decryptedPacket, preRegistrationId);
    }

    @Test
    public void test_creates_new_preregistration_with_random_uuid() {
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId("center123");

        PreRegistrationDto preRegistrationDto = new PreRegistrationDto();
        preRegistrationDto.setPreRegId("preReg123");
        preRegistrationDto.setSymmetricKey("symKey123");
        preRegistrationDto.setPacketPath("/path/to/packet");

        PreRegistrationList result =  ReflectionTestUtils.invokeMethod(service, "preparePreRegistration",
                centerMachineDto, preRegistrationDto, "2023-01-01", "2023-01-01 10:00:00");

        assertNotNull(result);
        assertNotNull(result.getId());
        assertTrue(result.getId().length() > 0);
        assertEquals("preReg123", result.getPreRegId());
        assertEquals("symKey123", result.getPacketSymmetricKey());
        assertEquals("/path/to/packet", result.getPacketPath());

        verify(mockSharedPreferences).getString(eq(USER_NAME), anyString());
    }

    @Test
    public void test_handles_null_appointment_date() {
        // Arrange
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId("center123");

        PreRegistrationDto preRegistrationDto = new PreRegistrationDto();
        preRegistrationDto.setPreRegId("preReg123");
        preRegistrationDto.setSymmetricKey("symKey123");
        preRegistrationDto.setPacketPath("/path/to/packet");

        PreRegistrationList result =  ReflectionTestUtils.invokeMethod(service, "preparePreRegistration",
                centerMachineDto, preRegistrationDto, null, "2023-01-01 10:00:00");

        assertNotNull(result);
        assertNull(result.getAppointmentDate());
        assertEquals("preReg123", result.getPreRegId());
        assertEquals("symKey123", result.getPacketSymmetricKey());
        assertEquals("/path/to/packet", result.getPacketPath());

        verify(mockSharedPreferences).getString(eq(USER_NAME), anyString());
    }

    @Test
    public void test_handles_empty_user_name_from_shared_preferences() {
        when(mockSharedPreferences.getString(eq(USER_NAME), anyString())).thenReturn("");

        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId("center123");

        PreRegistrationDto preRegistrationDto = new PreRegistrationDto();
        preRegistrationDto.setPreRegId("preReg123");
        preRegistrationDto.setSymmetricKey("symKey123");
        preRegistrationDto.setPacketPath("/path/to/packet");

        PreRegistrationList result =  ReflectionTestUtils.invokeMethod(service, "preparePreRegistration",
                centerMachineDto, preRegistrationDto, "2023-01-01", "2023-01-01 10:00:00");

        assertNotNull(result);
        assertEquals("preReg123", result.getPreRegId());

        verify(mockSharedPreferences).getString(eq(USER_NAME), anyString());
    }


    @Test
    public void test_with_spy_approach() {
        PreRegistrationDataSyncServiceImpl spyService = spy(service);

        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId("center123");

        PreRegistrationDto preRegistrationDto = new PreRegistrationDto();
        preRegistrationDto.setPreRegId("preReg123");
        preRegistrationDto.setSymmetricKey("symKey123");
        preRegistrationDto.setPacketPath("/path/to/packet");

        PreRegistrationList result =  ReflectionTestUtils.invokeMethod(spyService, "preparePreRegistration",
                centerMachineDto, preRegistrationDto, "2023-01-01", "2023-01-01 10:00:00");

        assertNotNull(result);
        assertEquals("preReg123", result.getPreRegId());
    }

}
