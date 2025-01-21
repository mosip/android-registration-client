package io.mosip.registration.clientmanager.service;

import android.content.Context;

import io.mosip.registration.clientmanager.config.ClientDatabase;
import io.mosip.registration.clientmanager.dao.GlobalParamDao;
import io.mosip.registration.clientmanager.dao.PreRegistrationDataSyncDao;
import io.mosip.registration.clientmanager.dto.CenterMachineDto;
import io.mosip.registration.clientmanager.dto.PreRegistrationIdsDto;
import io.mosip.registration.clientmanager.dto.http.ResponseWrapper;
import io.mosip.registration.clientmanager.entity.PreRegistrationList;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.service.external.PreRegZipHandlingService;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.clientmanager.exception.ClientCheckedException;
import retrofit2.Call;
import retrofit2.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

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

    private PreRegistrationDataSyncServiceImpl service;

    @Mock
    PreRegistrationList preRegistration;

    @Mock
    GlobalParamRepository globalParamRepository;
    ClientDatabase clientDatabase;

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
                globalParamRepository
        );
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
}
