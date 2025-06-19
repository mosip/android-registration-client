package io.mosip.registration.clientmanager.service;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import android.content.Context;

import com.auth0.android.jwt.DecodeException;
import io.mosip.registration.clientmanager.config.SessionManager;
import io.mosip.registration.clientmanager.entity.UserDetail;
import io.mosip.registration.keymanager.dto.CryptoRequestDto;
import io.mosip.registration.keymanager.dto.CryptoResponseDto;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import io.mosip.registration.clientmanager.exception.InvalidMachineSpecIDException;
import io.mosip.registration.clientmanager.repository.UserDetailRepository;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class LoginServiceTest {
    @Mock UserDetailRepository userDetailRepository;
    @Mock ClientCryptoManagerService clientCryptoManagerService;
    @Mock Context context;
    LoginService loginService;
    @Mock SessionManager sessionManager;

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

    @Test
    public void test_save_valid_auth_token_successfully() throws Exception {
        LoginService loginService = new LoginService(context, clientCryptoManagerService, userDetailRepository);

        Field userDetailRepositoryField = LoginService.class.getDeclaredField("userDetailRepository");
        userDetailRepositoryField.setAccessible(true);
        userDetailRepositoryField.set(loginService, userDetailRepository);

        Field sessionManagerField = LoginService.class.getDeclaredField("sessionManager");
        sessionManagerField.setAccessible(true);
        sessionManagerField.set(loginService, sessionManager);

        String userId = "testUser";
        String validToken = "valid.jwt.token";
        List<String> roles = Arrays.asList("REGISTRATION_SUPERVISOR");

        Mockito.when(userDetailRepository.getUserAuthToken(userId)).thenReturn(validToken);
        Mockito.when(sessionManager.saveAuthToken(validToken)).thenReturn(roles);

        String result = loginService.saveUserAuthTokenOffline(userId);

        assertEquals(validToken, result);
        verify(userDetailRepository).getUserAuthToken(userId);
        verify(sessionManager).saveAuthToken(validToken);
    }

    @Test
    public void test_handle_null_token_from_repository() throws Exception {
        LoginService loginService = new LoginService(context, clientCryptoManagerService, userDetailRepository);

        Field userDetailRepositoryField = LoginService.class.getDeclaredField("userDetailRepository");
        userDetailRepositoryField.setAccessible(true);
        userDetailRepositoryField.set(loginService, userDetailRepository);

        Field sessionManagerField = LoginService.class.getDeclaredField("sessionManager");
        sessionManagerField.setAccessible(true);
        sessionManagerField.set(loginService, sessionManager);

        String userId = "testUser";

        Mockito.when(userDetailRepository.getUserAuthToken(userId)).thenReturn(null);

        String result = loginService.saveUserAuthTokenOffline(userId);

        assertNull(result);
        verify(userDetailRepository).getUserAuthToken(userId);
        verify(sessionManager, Mockito.never()).saveAuthToken(Mockito.anyString());
    }

    @Test
    public void test_handles_empty_token_from_repository() throws Exception {
        LoginService loginService = new LoginService(context, clientCryptoManagerService, userDetailRepository);
        String userId = "testUser";

        when(userDetailRepository.getUserAuthToken(userId)).thenReturn("");

        String result = loginService.saveUserAuthTokenOffline(userId);

        verify(sessionManager, never()).saveAuthToken(anyString());
        assertEquals("", result);
    }

    @Test
    public void test_handles_exceptions_from_session_manager() {
        LoginService loginService = new LoginService(context, clientCryptoManagerService, userDetailRepository);
        String userId = "testUser";
        String token = "validToken";

        when(userDetailRepository.getUserAuthToken(userId)).thenReturn(token);

        assertThrows(Exception.class, () -> {
            loginService.saveUserAuthTokenOffline(userId);
        });
    }

    @Test (expected = InvalidMachineSpecIDException.class)
    public void test_successful_auth_token_save() throws Exception {
        LoginService loginService = new LoginService(context, clientCryptoManagerService, userDetailRepository);

        ClientCryptoManagerService mockCryptoService = mock(ClientCryptoManagerService.class);
        UserDetailRepository mockUserDetailRepository = mock(UserDetailRepository.class);
        SessionManager mockSessionManager = mock(SessionManager.class);

        String authResponse = "encryptedAuthResponse";
        String userId = "testUser";

        CryptoResponseDto cryptoResponseDto = new CryptoResponseDto();
        String jsonResponse = "{\"token\":\"sampleToken\",\"refreshToken\":\"sampleRefreshToken\",\"expiryTime\":\"1000\",\"refreshExpiryTime\":\"2000\"}";
        String encodedResponse = Base64.getUrlEncoder().encodeToString(jsonResponse.getBytes());
        cryptoResponseDto.setValue(encodedResponse);

        lenient().when(mockCryptoService.decrypt(any(CryptoRequestDto.class))).thenReturn(cryptoResponseDto);
        List<String> expectedRoles = Arrays.asList("REGISTRATION_OFFICER", "REGISTRATION_SUPERVISOR");
        lenient().when(mockSessionManager.saveAuthToken("sampleToken")).thenReturn(expectedRoles);

        List<String> roles = loginService.saveAuthToken(authResponse, userId);

        verify(mockCryptoService).decrypt(any(CryptoRequestDto.class));
        verify(mockUserDetailRepository).saveUserAuthToken(userId, "sampleToken", "sampleRefreshToken", 1000L, 2000L);
        verify(mockSessionManager).saveAuthToken("sampleToken");
        assertEquals(expectedRoles, roles);
    }

    @Test
    public void test_null_crypto_response_throws_exception() {
        LoginService loginService = new LoginService(context, clientCryptoManagerService, userDetailRepository);

        String authResponse = "encryptedAuthResponse";
        String userId = "testUser";

        when(clientCryptoManagerService.decrypt(any(CryptoRequestDto.class))).thenReturn(null);

        InvalidMachineSpecIDException exception = assertThrows(InvalidMachineSpecIDException.class, () -> {
            loginService.saveAuthToken(authResponse, userId);
        });

        assertEquals("Invalid Machine Spec ID found", exception.getMessage());
        verify(clientCryptoManagerService).decrypt(any(CryptoRequestDto.class));
        verify(userDetailRepository, never()).saveUserAuthToken(anyString(), anyString(), anyString(), anyLong(), anyLong());
    }

    @Test
    public void test_clear_auth_token_successful() {
        LoginService loginService = new LoginService(context, clientCryptoManagerService, userDetailRepository);

        try {
            Field sessionManagerField = LoginService.class.getDeclaredField("sessionManager");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(loginService, sessionManager);
        } catch (Exception e) {
            fail("Failed to set sessionManager field: " + e.getMessage());
        }

        when(sessionManager.clearAuthToken()).thenReturn(null);

        loginService.clearAuthToken(context);

        verify(sessionManager, times(1)).clearAuthToken();
    }

    @Test
    public void test_clear_auth_token_throws_exception() {
        LoginService loginService = new LoginService(context, clientCryptoManagerService, userDetailRepository);

        try {
            Field sessionManagerField = LoginService.class.getDeclaredField("sessionManager");
            sessionManagerField.setAccessible(true);
            sessionManagerField.set(loginService, sessionManager);
        } catch (Exception e) {
            fail("Failed to set sessionManager field: " + e.getMessage());
        }

        Exception expectedException = new RuntimeException("Session error");
        when(sessionManager.clearAuthToken()).thenThrow(expectedException);

        Exception thrownException = assertThrows(RuntimeException.class, () -> {
            loginService.clearAuthToken(context);
        });

        assertSame(expectedException, thrownException);
        verify(sessionManager, times(1)).clearAuthToken();
    }

    @Test
    public void test_session_manager_null() {
        LoginService loginService = new LoginService(context, null, null);

        Exception exception = assertThrows(Exception.class, () -> {
            loginService.clearAuthToken(context);
        });

        assertNotNull(exception);
    }

    @Test
    public void test_session_manager_returns_non_null_value() {
        LoginService loginService = new LoginService(context, null, null);

        lenient().when(sessionManager.clearAuthToken()).thenReturn("non-null-token");

        Exception exception = assertThrows(Exception.class, () -> {
            loginService.clearAuthToken(context);
        });

        assertNotNull(exception);
    }

    @Test
    public void test_validate_password_returns_true_for_valid_credentials() {
        String userId = "testUser";
        String password = "validPassword";
        Mockito.when(userDetailRepository.isValidPassword(userId, password)).thenReturn(true);

        LoginService loginService = new LoginService(context, clientCryptoManagerService, userDetailRepository);
        loginService.userDetailRepository = userDetailRepository;

        boolean result = loginService.validatePassword(userId, password);

        assertTrue(result);
        Mockito.verify(userDetailRepository).isValidPassword(userId, password);
    }

    @Test
    public void test_validate_password_handles_null_userid() {
        String userId = null;
        String password = "somePassword";
        Mockito.when(userDetailRepository.isValidPassword(userId, password)).thenReturn(false);

        LoginService loginService = new LoginService(context, clientCryptoManagerService, userDetailRepository);
        loginService.userDetailRepository = userDetailRepository;

        boolean result = loginService.validatePassword(userId, password);

        assertFalse(result);
        Mockito.verify(userDetailRepository).isValidPassword(userId, password);
    }

    @Test
    public void test_password_hash_stored_successfully() {
        String userId = "testUser";
        String password = "testPassword";
        LoginService loginService = new LoginService(context, clientCryptoManagerService, userDetailRepository);

        loginService.setPasswordHash(userId, password);

        Mockito.verify(userDetailRepository).setPasswordHash(userId, password);
    }

    @Test
    public void test_returns_true_when_password_exists_for_valid_user() {
        String validUserId = "validUser";
        Mockito.when(userDetailRepository.isPasswordPresent(validUserId)).thenReturn(true);

        LoginService loginService = new LoginService(context, clientCryptoManagerService, userDetailRepository);

        boolean result = loginService.isPasswordPresent(validUserId);

        assertTrue(result);
        Mockito.verify(userDetailRepository).isPasswordPresent(validUserId);
    }

    @Test
    public void test_returns_false_when_user_id_is_null() {
        String nullUserId = null;
        Mockito.when(userDetailRepository.isPasswordPresent(nullUserId)).thenReturn(false);

        LoginService loginService = new LoginService(context, clientCryptoManagerService, userDetailRepository);

        boolean result = loginService.isPasswordPresent(nullUserId);

        assertFalse(result);
        Mockito.verify(userDetailRepository).isPasswordPresent(nullUserId);
    }


    @Test
    public void test_get_user_details_returns_user_when_valid_userid() {
        String userId = "testUser";
        UserDetail expectedUserDetail = new UserDetail(userId);
        expectedUserDetail.setName("Test User");
        expectedUserDetail.setIsActive(true);

        Mockito.when(userDetailRepository.getUserDetailByUserId(userId)).thenReturn(expectedUserDetail);

        LoginService loginService = new LoginService(context, clientCryptoManagerService, userDetailRepository);
        loginService.userDetailRepository = userDetailRepository;

        UserDetail result = loginService.getUserDetailsByUserId(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("Test User", result.getName());
        assertEquals(true, result.getIsActive());

        Mockito.verify(userDetailRepository).getUserDetailByUserId(userId);
    }

    @Test
    public void test_get_user_details_with_null_userid() {
        String userId = null;

        Mockito.when(userDetailRepository.getUserDetailByUserId(userId)).thenReturn(null);

        LoginService loginService = new LoginService(context, clientCryptoManagerService, userDetailRepository);
        loginService.userDetailRepository = userDetailRepository;

        UserDetail result = loginService.getUserDetailsByUserId(userId);

        assertNull(result);

        Mockito.verify(userDetailRepository).getUserDetailByUserId(userId);
    }

    @Test
    public void test_returns_true_when_user_detail_count_is_zero() {
        Mockito.when(userDetailRepository.getUserDetailCount()).thenReturn(0);
        LoginService loginService = new LoginService(context, clientCryptoManagerService, userDetailRepository);

        boolean result = loginService.isValidUserId("anyUserId");

        assertTrue(result);
        Mockito.verify(userDetailRepository).getUserDetailCount();
        Mockito.verify(userDetailRepository, Mockito.never()).isActiveUser(Mockito.anyString());
    }

    @Test (expected = DecodeException.class)
     public void test_saveAuthToken_success() throws Exception {
        String authResponse = "dummyAuth";
        String userId = "user1";
        String token = "token123";
        String refreshToken = "refresh456";
        long expiryTime = 1000L;
        long refreshExpiryTime = 2000L;
        List<String> expectedRoles = Arrays.asList("ROLE1", "ROLE2");

        JSONObject json = new JSONObject();
        json.put("token", token);
        json.put("refreshToken", refreshToken);
        json.put("expiryTime", String.valueOf(expiryTime));
        json.put("refreshExpiryTime", String.valueOf(refreshExpiryTime));
        byte[] encoded = json.toString().getBytes();

        CryptoResponseDto cryptoResponseDto = new CryptoResponseDto();
        cryptoResponseDto.setValue(java.util.Base64.getEncoder().encodeToString(encoded));

        when(clientCryptoManagerService.decrypt(any(CryptoRequestDto.class))).thenReturn(cryptoResponseDto);
        lenient().when(sessionManager.saveAuthToken(token)).thenReturn(expectedRoles);

        List<String> roles = loginService.saveAuthToken(authResponse, userId);

        verify(userDetailRepository).saveUserAuthToken(userId, token, refreshToken, expiryTime, refreshExpiryTime);
        verify(sessionManager).saveAuthToken(token);
        assertEquals(expectedRoles, roles);
    }

    @Test
    public void test_saveAuthToken_nullCryptoResponse_throwsException() {
        String authResponse = "dummyAuth";
        String userId = "user1";
        when(clientCryptoManagerService.decrypt(any(CryptoRequestDto.class))).thenReturn(null);

        Exception ex = assertThrows(InvalidMachineSpecIDException.class, () -> {
            loginService.saveAuthToken(authResponse, userId);
        });
        assertEquals("Invalid Machine Spec ID found", ex.getMessage());
    }

    @Test
    public void test_saveAuthToken_jsonParseException() {
        String authResponse = "dummyAuth";
        String userId = "user1";
        CryptoResponseDto cryptoResponseDto = new CryptoResponseDto();
        cryptoResponseDto.setValue("!!notbase64!!");
        when(clientCryptoManagerService.decrypt(any(CryptoRequestDto.class))).thenReturn(cryptoResponseDto);

        assertThrows(Exception.class, () -> {
            loginService.saveAuthToken(authResponse, userId);
        });
    }

}
