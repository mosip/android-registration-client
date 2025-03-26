package io.mosip.registration.clientmanager.repository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.robolectric.annotation.Config;
import org.robolectric.RobolectricTestRunner;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import io.mosip.registration.clientmanager.dao.UserDetailDao;
import io.mosip.registration.clientmanager.dao.UserPasswordDao;
import io.mosip.registration.clientmanager.dao.UserTokenDao;
import io.mosip.registration.clientmanager.entity.UserDetail;
import io.mosip.registration.clientmanager.entity.UserPassword;
import io.mosip.registration.clientmanager.entity.UserToken;
import io.mosip.registration.packetmanager.util.HMACUtils2;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
public class UserDetailRepositoryTest {

    @Mock
    private UserDetailDao userDetailDao;

    @Mock
    private UserTokenDao userTokenDao;

    @Mock
    private UserPasswordDao userPasswordDao;

    @InjectMocks
    private UserDetailRepository userDetailRepository;

    private AutoCloseable closeable;

    @Before
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveUserDetail() throws JSONException {
        JSONArray users = new JSONArray();
        JSONObject user1 = new JSONObject();
        user1.put("userId", "9343");
        user1.put("isDeleted", false);
        user1.put("isActive", true);
        user1.put("regCenterId", "10011");
        users.put(user1);

        when(userDetailDao.getAllUserDetails()).thenReturn(new ArrayList<>());

        userDetailRepository.saveUserDetail(users);

        ArgumentCaptor<List<UserDetail>> captor = ArgumentCaptor.forClass(List.class);
        verify(userDetailDao).truncateAndInsertAll(captor.capture());

        List<UserDetail> capturedList = captor.getValue();
        assertEquals(1, capturedList.size());
        assertEquals("9343", capturedList.get(0).getId());
    }

    @Test
    public void testIsActiveUser_UserExists() {
        when(userDetailDao.getUserDetail("9343")).thenReturn(new UserDetail("9343"));

        boolean result = userDetailRepository.isActiveUser("9343");

        assertTrue(result);
    }

    @Test
    public void testIsActiveUser_UserDoesNotExist() {
        when(userDetailDao.getUserDetail("9343")).thenReturn(null);

        boolean result = userDetailRepository.isActiveUser("9343");

        assertFalse(result);
    }

    @Test
    public void testGetUserDetailByUserId() {
        UserDetail user = new UserDetail("9343");
        when(userDetailDao.getUserDetail("9343")).thenReturn(user);

        UserDetail result = userDetailRepository.getUserDetailByUserId("9343");

        assertNotNull(result);
        assertEquals("9343", result.getId());
    }

    @Test
    public void testGetUserDetailCount() {
        when(userDetailDao.getUserDetailCount()).thenReturn(5);

        int result = userDetailRepository.getUserDetailCount();

        assertEquals(5, result);
    }

    @Test
    public void testIsPasswordPresent_PasswordExists() {
        UserPassword password = new UserPassword("9343");
        password.setSalt("someSalt");
        when(userPasswordDao.getUserPassword("9343")).thenReturn(password);
        when(userDetailDao.getUserDetail("9343")).thenReturn(new UserDetail("9343"));

        boolean result = userDetailRepository.isPasswordPresent("9343");

        assertTrue(result);
    }

    @Test
    public void testIsPasswordPresent_NoPassword() {
        when(userPasswordDao.getUserPassword("9343")).thenReturn(null);
        when(userDetailDao.getUserDetail("9343")).thenReturn(new UserDetail("9343"));

        boolean result = userDetailRepository.isPasswordPresent("9343");

        assertFalse(result);
    }

    @Test
    public void testIsValidPassword_CorrectPassword() throws NoSuchAlgorithmException {
        UserPassword password = new UserPassword("9343");
        password.setSalt("encodedSalt");
        password.setPwd("hashedPassword");

        when(userPasswordDao.getUserPassword("9343")).thenReturn(password);

        try (MockedStatic<HMACUtils2> mockedStatic = mockStatic(HMACUtils2.class)) {
            mockedStatic.when(() -> HMACUtils2.digestAsPlainTextWithSalt(any(byte[].class), any(byte[].class)))
                    .thenReturn("hashedPassword");

            boolean result = userDetailRepository.isValidPassword("9343", "admin123");

            assertTrue(result);
        }
    }

    @Test
    public void testIsValidPassword_IncorrectPassword() throws NoSuchAlgorithmException {
        UserPassword password = new UserPassword("9343");
        password.setSalt("encodedSalt");
        password.setPwd("correctHash");

        when(userPasswordDao.getUserPassword("9343")).thenReturn(password);

        try (MockedStatic<HMACUtils2> mockedStatic = mockStatic(HMACUtils2.class)) {
            mockedStatic.when(() -> HMACUtils2.digestAsPlainTextWithSalt(any(byte[].class), any(byte[].class)))
                    .thenReturn("wrongHash");

            boolean result = userDetailRepository.isValidPassword("9343", "1234");

            assertFalse(result);
        }
    }

    @Test
    public void testSetPasswordHash() throws NoSuchAlgorithmException {
        UserPassword password = new UserPassword("9343");
        password.setSalt("encodedSalt");

        when(userPasswordDao.getUserPassword("9343")).thenReturn(password);

        try (MockedStatic<HMACUtils2> mockedStatic = mockStatic(HMACUtils2.class)) {
            mockedStatic.when(() -> HMACUtils2.digestAsPlainTextWithSalt(any(byte[].class), any(byte[].class)))
                    .thenReturn("newHash");

            userDetailRepository.setPasswordHash("9343", "admin123");

            ArgumentCaptor<UserPassword> captor = ArgumentCaptor.forClass(UserPassword.class);
            verify(userPasswordDao).insertUserPassword(captor.capture());

            assertEquals("newHash", captor.getValue().getPwd());
        }
    }

    @Test
    public void testSaveUserAuthToken() {
        UserToken token = new UserToken("9343", "", "", 0, 0);
        when(userTokenDao.findByUsername("9343")).thenReturn(token);

        userDetailRepository.saveUserAuthToken("9343", "newToken", "refreshToken", 1000L, 2000L);

        ArgumentCaptor<UserToken> captor = ArgumentCaptor.forClass(UserToken.class);
        verify(userTokenDao).insert(captor.capture());

        assertEquals("newToken", captor.getValue().getToken());
        assertEquals("refreshToken", captor.getValue().getRefreshToken());
        assertEquals(1000L, captor.getValue().getTExpiry());
        assertEquals(2000L, captor.getValue().getRExpiry());
    }

    @Test
    public void testGetUserAuthToken_UserHasToken() {
        UserToken token = new UserToken("9343", "authToken", "refreshToken", 1000L, 2000L);
        when(userTokenDao.findByUsername("9343")).thenReturn(token);

        String result = userDetailRepository.getUserAuthToken("9343");

        assertEquals("authToken", result);
    }

    @Test
    public void testGetUserAuthToken_UserHasNoToken() {
        when(userTokenDao.findByUsername("9343")).thenReturn(null);

        String result = userDetailRepository.getUserAuthToken("9343");

        assertEquals("", result);
    }
}
