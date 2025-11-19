package io.mosip.registration.clientmanager.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dto.CenterMachineDto;
import io.mosip.registration.clientmanager.dto.http.*;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.repository.MachineRepository;
import io.mosip.registration.clientmanager.repository.UserDetailRepository;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.util.SyncRestUtil;
import io.mosip.registration.keymanager.dto.CACertificateResponseDto;
import io.mosip.registration.keymanager.dto.CertificateRequestDto;
import io.mosip.registration.keymanager.spi.CertificateManagerService;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import okhttp3.ResponseBody;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SyncDataServiceImplTest {

    @Mock
    private SyncRestService mockSyncRestService;
    @Mock
    private CertificateManagerService mockCertificateManagerService;
    @Mock
    private Context mockContext;
    @Mock
    private Call<ResponseWrapper<CertificateResponse>> mockCallCertificate;
    @Mock
    private Call<ResponseWrapper<ClientSettingDto>> mockCallClientSetting;
    @Mock
    private Call<ResponseWrapper<Map<String, Object>>> mockCall;
    @Mock
    private Call<ResponseBody> mockCallResponseBody;
    @Mock
    private Call<ResponseWrapper<CACertificateResponseDto>> mockCallCACertificateResponse;

    @Mock
    private ClientCryptoManagerService mockClientCryptoManagerService;
    @Mock
    private GlobalParamRepository mockGlobalParamRepository;
    @Mock
    private MachineRepository mockMachineRepository;
    @Mock
    private IdentitySchemaRepository mockIdentitySchemaRepository;

    @Mock private Runnable mockOnFinish;
    @Mock
    private Toast mockToast;

    @InjectMocks
    private MasterDataServiceImpl masterDataService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        try (MockedStatic<Toast> mockedToast = Mockito.mockStatic(Toast.class)) {
            mockedToast.when(() -> Toast.makeText(any(Context.class), anyString(), anyInt()))
                    .thenReturn(mockToast);
        }
    }

    @Test
    public void test_syncCertificate_nullCenterMachineDto() {
        MasterDataServiceImpl spyService = Mockito.spy(masterDataService);
        doReturn(null).when(spyService).getRegistrationCenterMachineDetails();

        Runnable onFinish = mock(Runnable.class);

        spyService.syncCertificate(onFinish, "appId", "refId", "setAppId", "setRefId", false,"jobId");

        verify(onFinish).run();
        assertEquals("policy_key_sync_failed", spyService.onResponseComplete());
        verify(mockSyncRestService, never()).getPolicyKey(anyString(), anyString(), anyString());
    }

    @Test
    public void test_syncCertificate_successfulResponse_noError_certificateUploaded() {
        MasterDataServiceImpl spyService = Mockito.spy(masterDataService);
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        doReturn(centerMachineDto).when(spyService).getRegistrationCenterMachineDetails();

        when(mockSyncRestService.getPolicyKey(anyString(), anyString(), anyString())).thenReturn(mockCallCertificate);

        Runnable onFinish = mock(Runnable.class);

        spyService.syncCertificate(onFinish, "appId", "refId", "setAppId", "setRefId", false,"jobId");

        ArgumentCaptor<Callback<ResponseWrapper<CertificateResponse>>> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        verify(mockCallCertificate).enqueue(callbackCaptor.capture());

        CertificateResponse certResponse = mock(CertificateResponse.class);
        when(certResponse.getCertificate()).thenReturn("certData");
        ResponseWrapper<CertificateResponse> wrapper = new ResponseWrapper<>();
        wrapper.setResponse(certResponse);

        Response<ResponseWrapper<CertificateResponse>> response = Response.success(wrapper);

        callbackCaptor.getValue().onResponse(mockCallCertificate, response);

        verify(mockCertificateManagerService).uploadOtherDomainCertificate(any(CertificateRequestDto.class));
        verify(onFinish).run();
        assertEquals("", spyService.onResponseComplete());
    }

    @Test
    public void test_syncCertificate_successfulResponse_withError() {
        MasterDataServiceImpl spyService = Mockito.spy(masterDataService);
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        doReturn(centerMachineDto).when(spyService).getRegistrationCenterMachineDetails();

        when(mockSyncRestService.getPolicyKey(anyString(), anyString(), anyString())).thenReturn(mockCallCertificate);

        Runnable onFinish = mock(Runnable.class);

        spyService.syncCertificate(onFinish, "appId", "refId", "setAppId", "setRefId", false,"jobId");

        ArgumentCaptor<Callback<ResponseWrapper<CertificateResponse>>> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        verify(mockCallCertificate).enqueue(callbackCaptor.capture());

        ResponseWrapper<CertificateResponse> wrapper = new ResponseWrapper<>();
        Response<ResponseWrapper<CertificateResponse>> response = Response.success(wrapper);

        try (MockedStatic<SyncRestUtil> util = Mockito.mockStatic(SyncRestUtil.class)) {
            ServiceError error = new ServiceError();
            error.setMessage("Some error");
            util.when(() -> SyncRestUtil.getServiceError(wrapper)).thenReturn(error);

            callbackCaptor.getValue().onResponse(mockCallCertificate, response);

            verify(onFinish).run();
            assertEquals("policy_key_sync_failed", spyService.onResponseComplete());
            verify(mockCertificateManagerService, never()).uploadOtherDomainCertificate(any());
        }
    }

    @Test
    public void test_syncCertificate_unsuccessfulResponse() {
        MasterDataServiceImpl spyService = Mockito.spy(masterDataService);
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        doReturn(centerMachineDto).when(spyService).getRegistrationCenterMachineDetails();

        when(mockSyncRestService.getPolicyKey(anyString(), anyString(), anyString())).thenReturn(mockCallCertificate);

        Runnable onFinish = mock(Runnable.class);

        spyService.syncCertificate(onFinish, "appId", "refId", "setAppId", "setRefId", false,"jobId");

        ArgumentCaptor<Callback<ResponseWrapper<CertificateResponse>>> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        verify(mockCallCertificate).enqueue(callbackCaptor.capture());

        Response<ResponseWrapper<CertificateResponse>> response = Response.error(500, mock(ResponseBody.class));
        callbackCaptor.getValue().onResponse(mockCallCertificate, response);

        verify(onFinish).run();
        assertEquals("policy_key_sync_failed", spyService.onResponseComplete());
        verify(mockCertificateManagerService, never()).uploadOtherDomainCertificate(any());
    }

    @Test
    public void test_syncCertificate_onFailure() {
        MasterDataServiceImpl spyService = Mockito.spy(masterDataService);
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        doReturn(centerMachineDto).when(spyService).getRegistrationCenterMachineDetails();

        when(mockSyncRestService.getPolicyKey(anyString(), anyString(), anyString())).thenReturn(mockCallCertificate);

        Runnable onFinish = mock(Runnable.class);

        spyService.syncCertificate(onFinish, "appId", "refId", "setAppId", "setRefId", false,"jobId");

        ArgumentCaptor<Callback<ResponseWrapper<CertificateResponse>>> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        verify(mockCallCertificate).enqueue(callbackCaptor.capture());

        callbackCaptor.getValue().onFailure(mockCallCertificate, new RuntimeException("Network error"));

        verify(onFinish).run();
        assertEquals("policy_key_sync_failed", spyService.onResponseComplete());
        verify(mockCertificateManagerService, never()).uploadOtherDomainCertificate(any());
    }

    @Test
    public void test_syncCertificate_certificateUploadThrowsException() {
        MasterDataServiceImpl spyService = Mockito.spy(masterDataService);
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        doReturn(centerMachineDto).when(spyService).getRegistrationCenterMachineDetails();

        when(mockSyncRestService.getPolicyKey(anyString(), anyString(), anyString())).thenReturn(mockCallCertificate);

        Runnable onFinish = mock(Runnable.class);

        spyService.syncCertificate(onFinish, "appId", "refId", "setAppId", "setRefId", false,"jobId");

        ArgumentCaptor<Callback<ResponseWrapper<CertificateResponse>>> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        verify(mockCallCertificate).enqueue(callbackCaptor.capture());

        CertificateResponse certResponse = mock(CertificateResponse.class);
        when(certResponse.getCertificate()).thenReturn("certData");
        ResponseWrapper<CertificateResponse> wrapper = new ResponseWrapper<>();
        wrapper.setResponse(certResponse);

        Response<ResponseWrapper<CertificateResponse>> response = Response.success(wrapper);

        doThrow(new RuntimeException("Upload failed")).when(mockCertificateManagerService).uploadOtherDomainCertificate(any());

        callbackCaptor.getValue().onResponse(mockCallCertificate, response);

        verify(onFinish).run();
        assertEquals("policy_key_sync_failed", spyService.onResponseComplete());
    }

    @Test
    public void test_sync_certificate_null_center_machine_dto() {
        MasterDataServiceImpl spyMasterDataService = spy(masterDataService);
        doReturn(null).when(spyMasterDataService).getRegistrationCenterMachineDetails();

        Runnable mockRunnable = mock(Runnable.class);

        spyMasterDataService.syncCertificate(mockRunnable, "appId", "refId", "setAppId", "setRefId", false,"jobId");

        verify(mockRunnable).run();
        assertEquals("policy_key_sync_failed", spyMasterDataService.onResponseComplete());
        verify(mockSyncRestService, never()).getPolicyKey(anyString(), anyString(), anyString());
    }

    @Test
    public void test_syncMasterData_keyIndexException() throws Exception {
        lenient().doThrow(new RuntimeException("keyindex error")).when(mockClientCryptoManagerService).getClientKeyIndex();

        verify(mockSyncRestService, never()).fetchMasterData(any());
    }

    @Test
    public void test_syncMasterData_successfulResponse_noError_centerMachinePresent() throws Exception {
        MasterDataServiceImpl spyService = Mockito.spy(masterDataService);
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId("center1");
        doReturn(centerMachineDto).when(spyService).getRegistrationCenterMachineDetails();

        when(mockClientCryptoManagerService.getClientKeyIndex()).thenReturn("key1");
        when(mockGlobalParamRepository.getGlobalParamValue(anyString())).thenReturn(null);
        when(mockGlobalParamRepository.getCachedStringGlobalParam(anyString())).thenReturn("1.2.0");
        when(mockSyncRestService.fetchMasterData(any())).thenReturn(mockCallClientSetting);

        Runnable onFinish = mock(Runnable.class);

        spyService.syncMasterData(onFinish, 0, true, "");

        ArgumentCaptor<Callback<ResponseWrapper<ClientSettingDto>>> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        verify(mockCallClientSetting).enqueue(callbackCaptor.capture());

        ClientSettingDto clientSettingDto = mock(ClientSettingDto.class);
        ResponseWrapper<ClientSettingDto> wrapper = new ResponseWrapper<>();
        wrapper.setResponse(clientSettingDto);

        try (MockedStatic<SyncRestUtil> util = Mockito.mockStatic(SyncRestUtil.class)) {
            util.when(() -> SyncRestUtil.getServiceError(wrapper)).thenReturn(null);

            assertEquals("", spyService.onResponseComplete());
        }
    }

    @Test
    public void test_syncMasterData_successfulResponse_withError() throws Exception {
        MasterDataServiceImpl spyService = Mockito.spy(masterDataService);
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        doReturn(centerMachineDto).when(spyService).getRegistrationCenterMachineDetails();

        when(mockClientCryptoManagerService.getClientKeyIndex()).thenReturn("key1");
        when(mockGlobalParamRepository.getGlobalParamValue(anyString())).thenReturn(null);
        when(mockGlobalParamRepository.getCachedStringGlobalParam(anyString())).thenReturn("1.2.0");
        when(mockSyncRestService.fetchMasterData(any())).thenReturn(mockCallClientSetting);

        Runnable onFinish = mock(Runnable.class);

        spyService.syncMasterData(onFinish, 0, true, "");

        ArgumentCaptor<Callback<ResponseWrapper<ClientSettingDto>>> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        verify(mockCallClientSetting).enqueue(callbackCaptor.capture());

        ResponseWrapper<ClientSettingDto> wrapper = new ResponseWrapper<>();

        try (MockedStatic<SyncRestUtil> util = Mockito.mockStatic(SyncRestUtil.class)) {
            ServiceError error = new ServiceError();
            error.setMessage("Some error");
            util.when(() -> SyncRestUtil.getServiceError(wrapper)).thenReturn(error);
        }
    }

    @Test
    public void test_syncMasterData_unsuccessfulResponse() throws Exception {
        MasterDataServiceImpl spyService = Mockito.spy(masterDataService);
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        doReturn(centerMachineDto).when(spyService).getRegistrationCenterMachineDetails();

        when(mockClientCryptoManagerService.getClientKeyIndex()).thenReturn("key1");
        when(mockGlobalParamRepository.getGlobalParamValue(anyString())).thenReturn(null);
        when(mockGlobalParamRepository.getCachedStringGlobalParam(anyString())).thenReturn("1.2.0");
        when(mockSyncRestService.fetchMasterData(any())).thenReturn(mockCallClientSetting);

        Runnable onFinish = mock(Runnable.class);

        spyService.syncMasterData(onFinish, 0, true, "");

        ArgumentCaptor<Callback<ResponseWrapper<ClientSettingDto>>> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        verify(mockCallClientSetting).enqueue(callbackCaptor.capture());
    }

    @Test
    public void test_syncMasterData_onFailure() throws Exception {
        MasterDataServiceImpl spyService = Mockito.spy(masterDataService);
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        doReturn(centerMachineDto).when(spyService).getRegistrationCenterMachineDetails();

        when(mockClientCryptoManagerService.getClientKeyIndex()).thenReturn("key1");
        when(mockGlobalParamRepository.getGlobalParamValue(anyString())).thenReturn(null);
        when(mockGlobalParamRepository.getCachedStringGlobalParam(anyString())).thenReturn("1.2.0");
        when(mockSyncRestService.fetchMasterData(any())).thenReturn(mockCallClientSetting);

        Runnable onFinish = mock(Runnable.class);

        spyService.syncMasterData(onFinish, 0, true, "");

        ArgumentCaptor<Callback<ResponseWrapper<ClientSettingDto>>> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        verify(mockCallClientSetting).enqueue(callbackCaptor.capture());
    }

    @Test
    public void test_syncMasterData_recursiveRetry() throws Exception {
        MasterDataServiceImpl spyService = Mockito.spy(masterDataService);
        doReturn(null).when(spyService).getRegistrationCenterMachineDetails();

        when(mockClientCryptoManagerService.getClientKeyIndex()).thenReturn("key1");
        when(mockGlobalParamRepository.getGlobalParamValue(anyString())).thenReturn(null);
        when(mockGlobalParamRepository.getCachedStringGlobalParam(anyString())).thenReturn("1.2.0");
        when(mockSyncRestService.fetchMasterData(any())).thenReturn(mockCallClientSetting);

        Runnable onFinish = mock(Runnable.class);

        spyService.syncMasterData(onFinish, 0, true,"");

        ArgumentCaptor<Callback<ResponseWrapper<ClientSettingDto>>> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        verify(mockCallClientSetting).enqueue(callbackCaptor.capture());

        ClientSettingDto clientSettingDto = mock(ClientSettingDto.class);
        ResponseWrapper<ClientSettingDto> wrapper = new ResponseWrapper<>();
        wrapper.setResponse(clientSettingDto);

        Response<ResponseWrapper<ClientSettingDto>> response = Response.success(wrapper);

        try (MockedStatic<SyncRestUtil> util = Mockito.mockStatic(SyncRestUtil.class)) {
            util.when(() -> SyncRestUtil.getServiceError(wrapper)).thenReturn(null);

            for (int i = 0; i < 3; i++) {
                callbackCaptor.getValue().onResponse(mockCallClientSetting, response);
            }

            callbackCaptor.getValue().onResponse(mockCallClientSetting, response);
        }
    }

    @Test
    public void test_syncGlobalParamsData_successful_noError() throws Exception {
        try (MockedStatic<Toast> mockedToast = Mockito.mockStatic(Toast.class)) {
            mockedToast.when(() -> Toast.makeText(any(Context.class), anyString(), anyInt()))
                    .thenReturn(mock(Toast.class));

            MasterDataServiceImpl spyService = Mockito.spy(masterDataService);
            when(mockClientCryptoManagerService.getClientKeyIndex()).thenReturn("key1");
            when(mockSyncRestService.getGlobalConfigs(anyString(), anyString())).thenReturn(mockCall);

            spyService.syncGlobalParamsData(mockOnFinish, true, "");

            ArgumentCaptor<Callback<ResponseWrapper<Map<String, Object>>>> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
            verify(mockCall).enqueue(callbackCaptor.capture());

            Map<String, Object> responseMap = new HashMap<>();
            ResponseWrapper<Map<String, Object>> wrapper = new ResponseWrapper<>();
            wrapper.setResponse(responseMap);
            Response<ResponseWrapper<Map<String, Object>>> response = Response.success(wrapper);

            try (MockedStatic<SyncRestUtil> util = Mockito.mockStatic(SyncRestUtil.class)) {
                util.when(() -> SyncRestUtil.getServiceError(wrapper)).thenReturn(null);

                assertEquals("", spyService.onResponseComplete());
            }
        }
    }

    @Test
    public void test_syncGlobalParamsData_successful_withError() throws Exception {
        try (MockedStatic<Toast> mockedToast = Mockito.mockStatic(Toast.class)) {
            mockedToast.when(() -> Toast.makeText(any(Context.class), anyString(), anyInt()))
                    .thenReturn(mock(Toast.class));

            MasterDataServiceImpl spyService = Mockito.spy(masterDataService);
            when(mockClientCryptoManagerService.getClientKeyIndex()).thenReturn("key1");
            when(mockSyncRestService.getGlobalConfigs(anyString(), anyString())).thenReturn(mockCall);

            spyService.syncGlobalParamsData(mockOnFinish, true, "");

            ArgumentCaptor<Callback<ResponseWrapper<Map<String, Object>>>> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
            verify(mockCall).enqueue(callbackCaptor.capture());

            ResponseWrapper<Map<String, Object>> wrapper = new ResponseWrapper<>();
            Response<ResponseWrapper<Map<String, Object>>> response = Response.success(wrapper);

            ServiceError error = new ServiceError();
            error.setMessage("Some error");

            try (MockedStatic<SyncRestUtil> util = Mockito.mockStatic(SyncRestUtil.class)) {
                util.when(() -> SyncRestUtil.getServiceError(wrapper)).thenReturn(error);

                callbackCaptor.getValue().onResponse(mockCall, response);

                verify(mockOnFinish).run();
                assertEquals("global_params_sync_failed", spyService.onResponseComplete());
            }
        }
    }

    @Test
    public void test_syncGlobalParamsData_unsuccessfulResponse() throws Exception {
        try (MockedStatic<Toast> mockedToast = Mockito.mockStatic(Toast.class)) {
            mockedToast.when(() -> Toast.makeText(any(Context.class), anyString(), anyInt()))
                    .thenReturn(mock(Toast.class));

            MasterDataServiceImpl spyService = Mockito.spy(masterDataService);
            when(mockClientCryptoManagerService.getClientKeyIndex()).thenReturn("key1");
            when(mockSyncRestService.getGlobalConfigs(anyString(), anyString())).thenReturn(mockCall);

            spyService.syncGlobalParamsData(mockOnFinish, true, "");

            ArgumentCaptor<Callback<ResponseWrapper<Map<String, Object>>>> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
            verify(mockCall).enqueue(callbackCaptor.capture());

            Response<ResponseWrapper<Map<String, Object>>> response = Response.error(500, mock(ResponseBody.class));
            callbackCaptor.getValue().onResponse(mockCall, response);

            verify(mockOnFinish).run();
            assertEquals("global_params_sync_failed", spyService.onResponseComplete());
        }
    }

    @Test
    public void test_syncGlobalParamsData_onFailure() throws Exception {
        try (MockedStatic<Toast> mockedToast = Mockito.mockStatic(Toast.class)) {
            mockedToast.when(() -> Toast.makeText(any(Context.class), anyString(), anyInt()))
                    .thenReturn(mock(Toast.class));

            MasterDataServiceImpl spyService = Mockito.spy(masterDataService);
            when(mockClientCryptoManagerService.getClientKeyIndex()).thenReturn("key1");
            when(mockSyncRestService.getGlobalConfigs(anyString(), anyString())).thenReturn(mockCall);

            spyService.syncGlobalParamsData(mockOnFinish, true, "");

            ArgumentCaptor<Callback<ResponseWrapper<Map<String, Object>>>> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
            verify(mockCall).enqueue(callbackCaptor.capture());
        }
    }

    @Test
    public void test_syncLatestIdSchema_successful_exception() throws Exception {
        try (MockedStatic<Toast> mockedToast = Mockito.mockStatic(Toast.class)) {
            mockedToast.when(() -> Toast.makeText(any(Context.class), anyString(), anyInt()))
                    .thenReturn(mock(Toast.class));

            MasterDataServiceImpl spyService = Mockito.spy(masterDataService);
            when(mockSyncRestService.getLatestIdSchema(anyString(), anyString())).thenReturn(mockCallResponseBody);

            spyService.syncLatestIdSchema(mockOnFinish, true);

            ResponseBody responseBody = mock(ResponseBody.class);
            lenient().when(responseBody.string()).thenThrow(new RuntimeException("IO error"));
        }
    }

    @Test
    public void test_syncLatestIdSchema_unsuccessful() {
        try (MockedStatic<Toast> mockedToast = Mockito.mockStatic(Toast.class)) {
            mockedToast.when(() -> Toast.makeText(any(Context.class), anyString(), anyInt()))
                    .thenReturn(mock(Toast.class));

            MasterDataServiceImpl spyService = Mockito.spy(masterDataService);
            when(mockSyncRestService.getLatestIdSchema(anyString(), anyString())).thenReturn(mockCallResponseBody);

            spyService.syncLatestIdSchema(mockOnFinish, true);
        }
    }

    @Test
    public void test_syncLatestIdSchema_onFailure() {
        try (MockedStatic<Toast> mockedToast = Mockito.mockStatic(Toast.class)) {
            mockedToast.when(() -> Toast.makeText(any(Context.class), anyString(), anyInt()))
                    .thenReturn(mock(Toast.class));

            MasterDataServiceImpl spyService = Mockito.spy(masterDataService);
            when(mockSyncRestService.getLatestIdSchema(anyString(), anyString())).thenReturn(mockCallResponseBody);

            spyService.syncLatestIdSchema(mockOnFinish, true);
        }
    }

    @Test
    public void test_handle_null_server_version() throws Exception {
        SharedPreferences mockSharedPreferences = mock(SharedPreferences.class);
        lenient().when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences);

        ObjectMapper mockObjectMapper = mock(ObjectMapper.class);
        UserDetailRepository mockUserDetailRepository = mock(UserDetailRepository.class);

        lenient().when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.SERVER_VERSION)).thenReturn(null);
        lenient().when(mockClientCryptoManagerService.getClientKeyIndex()).thenReturn("keyIndex");

        Call<ResponseWrapper<UserDetailResponse>> mockCall = mock(Call.class);
        lenient().when(mockSyncRestService.fetchCenterUserDetails(anyString(), anyString())).thenReturn(mockCall);

        MasterDataServiceImpl masterDataService = new MasterDataServiceImpl(
                mockContext, mockObjectMapper, mockSyncRestService, mockClientCryptoManagerService,
                null, null, null, null, null, null, null, null,
                mockGlobalParamRepository, null, null, null, mockUserDetailRepository,
                null, null, null, null, null,
                null, null
        );

        Runnable mockOnFinish = mock(Runnable.class);

        assertThrows(NullPointerException.class, () -> {
            masterDataService.syncUserDetails(mockOnFinish, true, "");
        });
    }

}