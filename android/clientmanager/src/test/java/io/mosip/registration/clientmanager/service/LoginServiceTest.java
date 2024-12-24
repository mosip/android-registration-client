package io.mosip.registration.clientmanager.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import io.mosip.registration.clientmanager.exception.InvalidMachineSpecIDException;
import io.mosip.registration.clientmanager.repository.UserDetailRepository;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;

@RunWith(MockitoJUnitRunner.class)
public class LoginServiceTest {
    @Mock UserDetailRepository userDetailRepository;
    @Mock ClientCryptoManagerService clientCryptoManagerService;
    @Mock Context context;
    LoginService loginService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        loginService = new LoginService(context, clientCryptoManagerService, userDetailRepository);
    }

    @Test(expected = InvalidMachineSpecIDException.class)
    public void saveAuthToken_throwException() throws Exception {
        when(this.clientCryptoManagerService.decrypt(any())).thenReturn(null);
        loginService.saveAuthToken(null, "");
    }

    @Test
    public void isValidUserId_WithActiveUser_Test() {
        String userId = "9343";
        when(userDetailRepository.getUserDetailCount()).thenReturn(1);
        when(userDetailRepository.isActiveUser(userId)).thenReturn(true);

        assertTrue(loginService.isValidUserId(userId));
    }

    @Test
    public void isValidUserId_WithInactiveUser_Test() {
        String userId = "9343";
        when(userDetailRepository.getUserDetailCount()).thenReturn(1);
        when(userDetailRepository.isActiveUser(userId)).thenReturn(false);

        assertFalse(loginService.isValidUserId(userId));
    }
}
