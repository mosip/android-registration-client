package io.mosip.registration.clientmanager.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;

import io.mosip.registration.clientmanager.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SessionManagerTest {

    @Mock
    Context mockContext;
    @Mock
    SharedPreferences mockPrefs;
    @Mock
    SharedPreferences.Editor mockEditor;

    @Before
    public void setUp() throws Exception {
        // Reset singleton so each test gets a fresh SessionManager backed by the current mock
        Field managerField = SessionManager.class.getDeclaredField("manager");
        managerField.setAccessible(true);
        managerField.set(null, null);

        lenient().when(mockContext.getString(anyInt())).thenReturn("app_name");
        lenient().when(mockContext.getSharedPreferences(eq("app_name"), eq(Context.MODE_PRIVATE))).thenReturn(mockPrefs);
        lenient().when(mockPrefs.edit()).thenReturn(mockEditor);
        lenient().when(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor);
        lenient().when(mockEditor.putBoolean(anyString(), anyBoolean())).thenReturn(mockEditor);
        lenient().when(mockEditor.remove(anyString())).thenReturn(mockEditor);
    }

    @Test
    public void getSessionManager_calledTwice_returnsSameInstance() {
        SessionManager m1 = SessionManager.getSessionManager(mockContext);
        SessionManager m2 = SessionManager.getSessionManager(mockContext);
        assertSame(m1, m2);
    }

    @Test(expected = Exception.class)
    public void saveAuthToken_withExpiredToken_throwsException() throws Exception {
        try (MockedConstruction<JWT> jwtMock = Mockito.mockConstruction(JWT.class, (mock, context) -> {
            lenient().when(mock.isExpired(15)).thenReturn(true);
        })) {
            SessionManager manager = SessionManager.getSessionManager(mockContext);
            manager.saveAuthToken("expiredToken");
        }
    }

    @Test(expected = Exception.class)
    public void saveAuthToken_withNoRoles_throwsException() throws Exception {
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", Collections.emptyList());
        try (MockedConstruction<JWT> jwtMock = Mockito.mockConstruction(JWT.class, (mock, context) -> {
            lenient().when(mock.isExpired(15)).thenReturn(false);
            lenient().when(mock.getClaim(eq("realm_access"))).thenReturn(mockClaim(realmAccess));
        })) {
            SessionManager manager = SessionManager.getSessionManager(mockContext);
            manager.saveAuthToken("noRolesToken");
        }
    }

    @Test(expected = Exception.class)
    public void saveAuthToken_withMissingRequiredRoles_throwsException() throws Exception {
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", Arrays.asList("REGISTRATION_OPERATOR"));
        try (MockedConstruction<JWT> jwtMock = Mockito.mockConstruction(JWT.class, (mock, context) -> {
            lenient().when(mock.isExpired(15)).thenReturn(false);
            lenient().when(mock.getClaim(eq("realm_access"))).thenReturn(mockClaim(realmAccess));
        })) {
            SessionManager manager = SessionManager.getSessionManager(mockContext);
            manager.saveAuthToken("badRolesToken");
        }
    }

    private Claim mockClaim(Object value) {
        Claim claim = mock(Claim.class);
        if (value instanceof String) {
            when(claim.asString()).thenReturn((String) value);
        } else if (value instanceof Map) {
            when(claim.asObject(Map.class)).thenReturn((Map) value);
        }
        return claim;
    }

    @Test
    public void fetchAuthToken_withNoTokenStored_returnsNull() {
        lenient().when(mockPrefs.getString(SessionManager.USER_TOKEN, null)).thenReturn(null);

        SessionManager sessionManager = SessionManager.getSessionManager(mockContext);
        String token = sessionManager.fetchAuthToken();

        assertNull(token);
    }

    @Test
    public void fetchAuthToken_withStoredToken_returnsToken() {
        String token = "valid.jwt.token";
        lenient().when(mockPrefs.getString(SessionManager.USER_TOKEN, null)).thenReturn(token);

        SessionManager sessionManager = SessionManager.getSessionManager(mockContext);
        String result = sessionManager.fetchAuthToken();

        assertEquals(token, result);
    }

    @Test
    public void fetchAuthToken_withMockContext_usesCorrectPreferencesName() {
        SessionManager sessionManager = SessionManager.getSessionManager(mockContext);
        sessionManager.fetchAuthToken();

        verify(mockContext, atLeastOnce()).getSharedPreferences(eq("app_name"), eq(Context.MODE_PRIVATE));
    }

    @Test
    public void clearAuthToken_withActiveSession_returnsNull() {
        lenient().when(mockPrefs.getString(SessionManager.USER_TOKEN, null)).thenReturn(null);

        SessionManager sessionManager = SessionManager.getSessionManager(mockContext);
        String result = sessionManager.clearAuthToken();

        assertNull(result);
    }

    @Test
    public void saveAuthToken_withExpiredJwtString_throwsException() {
        String expiredToken = "expired.jwt.token";
        JWT mockJwt = mock(JWT.class);
        Date expiryDate = new Date(System.currentTimeMillis() - 1000);

        lenient().when(mockJwt.isExpired(15)).thenReturn(true);
        lenient().when(mockJwt.getExpiresAt()).thenReturn(expiryDate);

        SessionManager sessionManager = SessionManager.getSessionManager(mockContext);

        assertThrows(Exception.class, () -> {
            sessionManager.saveAuthToken(expiredToken);
        });
    }

    @Test
    public void setOperatorCaptureTransactionId_storesTransactionIdInPreferences() {
        lenient().when(mockEditor.putString(eq(SessionManager.OPERATOR_CAPTURE_TRANSACTION_ID), eq("TXN-123"))).thenReturn(mockEditor);

        SessionManager sessionManager = SessionManager.getSessionManager(mockContext);
        sessionManager.setOperatorCaptureTransactionId("TXN-123");

        verify(mockEditor).putString(eq(SessionManager.OPERATOR_CAPTURE_TRANSACTION_ID), eq("TXN-123"));
        verify(mockEditor).apply();
    }

    @Test
    public void getOperatorCaptureTransactionId_withStoredId_returnsTransactionId() {
        String expectedId = "TXN-ABC-123";
        lenient().when(mockPrefs.getString(SessionManager.OPERATOR_CAPTURE_TRANSACTION_ID, null)).thenReturn(expectedId);

        SessionManager sessionManager = SessionManager.getSessionManager(mockContext);
        String result = sessionManager.getOperatorCaptureTransactionId();

        assertEquals(expectedId, result);
    }

    @Test
    public void getOperatorCaptureTransactionId_withNoStoredId_returnsNull() {
        lenient().when(mockPrefs.getString(SessionManager.OPERATOR_CAPTURE_TRANSACTION_ID, null)).thenReturn(null);

        SessionManager sessionManager = SessionManager.getSessionManager(mockContext);
        String result = sessionManager.getOperatorCaptureTransactionId();

        assertNull(result);
    }

    @Test
    public void clearAuthToken_removesOperatorCaptureTransactionId() {
        lenient().when(mockPrefs.getString(SessionManager.USER_TOKEN, null)).thenReturn(null);

        SessionManager sessionManager = SessionManager.getSessionManager(mockContext);
        sessionManager.clearAuthToken();

        verify(mockEditor).remove(SessionManager.OPERATOR_CAPTURE_TRANSACTION_ID);
    }

}
