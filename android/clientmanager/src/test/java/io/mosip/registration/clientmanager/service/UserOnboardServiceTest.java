package io.mosip.registration.clientmanager.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.util.List;

import io.mosip.registration.clientmanager.dto.registration.BiometricsDto;
import io.mosip.registration.clientmanager.exception.ClientCheckedException;
import io.mosip.registration.clientmanager.repository.UserBiometricRepository;
import io.mosip.registration.clientmanager.repository.UserDetailRepository;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.keymanager.spi.CertificateManagerService;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.keymanager.spi.CryptoManagerService;

public class UserOnboardServiceTest {

    @Mock
    private Context context;

    @Mock
    private SharedPreferences sharedPreferences;

    @Mock
    private UserBiometricRepository userBiometricRepository;

    @Mock
    private UserDetailRepository userDetailRepository;

    @Mock
    private SyncRestService syncRestService;

    @Mock
    private AuditManagerService auditManagerService;

    @Mock
    private CertificateManagerService certificateManagerService;

    @Mock
    private CryptoManagerService cryptoManagerService;

    @Mock
    private RegistrationService registrationService;

    @Mock
    private ClientCryptoManagerService clientCryptoManagerService;


    @InjectMocks
    private UserOnboardService userOnboardService;

    @Before
    public void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        Context context = mock(Context.class);
        when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences);

        userOnboardService = new UserOnboardService(context, objectMapper, auditManagerService,
                certificateManagerService, syncRestService, cryptoManagerService, registrationService,
                userBiometricRepository, clientCryptoManagerService, userDetailRepository);
        userOnboardService.sharedPreferences = sharedPreferences;
    }


    @Test
    public void testOnboardOperator_BiometricsNull_ThrowsException() {
        List<BiometricsDto> biometricsList = null;
        Runnable onFinish = mock(Runnable.class);

        ClientCheckedException exception = assertThrows(ClientCheckedException.class, () -> {
            userOnboardService.onboardOperator(biometricsList, onFinish);
        });
        assertEquals("REG-UOS-001", exception.getErrorCode());  // Validate exception
    }

}
