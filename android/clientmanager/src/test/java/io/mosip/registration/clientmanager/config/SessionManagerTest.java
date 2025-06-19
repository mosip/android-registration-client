package io.mosip.registration.clientmanager.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;

import io.mosip.registration.clientmanager.R;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

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
    public void setUp() {
        lenient().when(mockContext.getString(anyInt())).thenReturn("app_name");
        lenient().when(mockContext.getSharedPreferences(eq("app_name"), eq(Context.MODE_PRIVATE))).thenReturn(mockPrefs);
        lenient().when(mockPrefs.edit()).thenReturn(mockEditor);
        lenient().when(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor);
        lenient().when(mockEditor.putBoolean(anyString(), anyBoolean())).thenReturn(mockEditor);
        lenient().when(mockEditor.remove(anyString())).thenReturn(mockEditor);
    }

    @Test
    public void testGetSessionManager_Singleton() {
        SessionManager m1 = SessionManager.getSessionManager(mockContext);
        SessionManager m2 = SessionManager.getSessionManager(mockContext);
        assertSame(m1, m2);
    }

    @Test(expected = Exception.class)
    public void testSaveAuthToken_ExpiredToken() throws Exception {
        try (MockedConstruction<JWT> jwtMock = Mockito.mockConstruction(JWT.class, (mock, context) -> {
            lenient().when(mock.isExpired(15)).thenReturn(true);
        })) {
            SessionManager manager = SessionManager.getSessionManager(mockContext);
            manager.saveAuthToken("expiredToken");
        }
    }

    @Test(expected = Exception.class)
    public void testSaveAuthToken_NoRoles() throws Exception {
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
    public void testSaveAuthToken_MissingRequiredRoles() throws Exception {
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

    @Ignore
    @Test
    public void test_fetch_auth_token_returns_null_when_no_token_exists() {
        Mockito.when(mockContext.getString(R.string.app_name)).thenReturn("app_name");
        lenient().when(mockContext.getSharedPreferences("app_name", Context.MODE_PRIVATE)).thenReturn(mockPrefs);
        lenient().when(mockPrefs.getString(SessionManager.USER_TOKEN, null)).thenReturn(null);

        SessionManager sessionManager = SessionManager.getSessionManager(mockContext);
        String token = sessionManager.fetchAuthToken();

        Assertions.assertNull(token);
    }

    @Ignore
    @Test
    public void test_fetch_auth_token_retrieves_saved_token() throws Exception {
        lenient().when(mockContext.getString(R.string.app_name)).thenReturn("app_name");
        lenient().when(mockContext.getSharedPreferences("app_name", Context.MODE_PRIVATE)).thenReturn(mockPrefs);
        lenient().when(mockPrefs.edit()).thenReturn(mockEditor);
        lenient().when(mockEditor.putString(Mockito.anyString(), Mockito.anyString())).thenReturn(mockEditor);
        lenient().when(mockEditor.putBoolean(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(mockEditor);

        SessionManager sessionManager = SessionManager.getSessionManager(mockContext);
        String token = "valid.jwt.token";

        lenient().when(mockPrefs.getString(SessionManager.USER_TOKEN, null)).thenReturn(token);

        sessionManager.fetchAuthToken();
    }

    @Ignore
    @Test
    public void test_fetch_auth_token_uses_correct_shared_preferences_name() {
        lenient().when(mockContext.getString(R.string.app_name)).thenReturn("app_name");
        lenient().when(mockContext.getSharedPreferences("app_name", Context.MODE_PRIVATE)).thenReturn(mockPrefs);

        SessionManager sessionManager = SessionManager.getSessionManager(mockContext);
        sessionManager.fetchAuthToken();
    }

    @Ignore
    @Test
    public void test_clear_auth_token_removes_all_user_session_data() {
        lenient().when(mockContext.getString(R.string.app_name)).thenReturn("app_name");
        lenient().when(mockContext.getSharedPreferences("app_name", Context.MODE_PRIVATE)).thenReturn(mockPrefs);
        lenient().when(mockPrefs.edit()).thenReturn(mockEditor);
        lenient().when(mockEditor.remove(anyString())).thenReturn(mockEditor);
        lenient().when(mockPrefs.getString(SessionManager.USER_TOKEN, null)).thenReturn(null);

        SessionManager sessionManager = SessionManager.getSessionManager(mockContext);

        String result = sessionManager.clearAuthToken();

        assertNull(result);
    }

    @Test
    public void test_expired_token_throws_exception() {
        String expiredToken = "expired.jwt.token";
        JWT mockJwt = mock(JWT.class);
        Date expiryDate = new Date(System.currentTimeMillis() - 1000);

        lenient().when(mockJwt.isExpired(15)).thenReturn(true);
        lenient().when(mockJwt.getExpiresAt()).thenReturn(expiryDate);

        SessionManager sessionManager = SessionManager.getSessionManager(mockContext);

        assertThrows(Exception.class, () -> {
            sessionManager.saveAuthToken(expiredToken);
        });

        verify(mockContext, never()).getSharedPreferences(anyString(), anyInt());
    }

}
