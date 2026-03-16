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
import java.util.Arrays;
import java.util.List;

import io.mosip.registration.clientmanager.dao.UserDetailDao;
import io.mosip.registration.clientmanager.dao.UserPasswordDao;
import io.mosip.registration.clientmanager.dao.UserTokenDao;
import io.mosip.registration.clientmanager.entity.UserDetail;
import io.mosip.registration.clientmanager.entity.UserPassword;
import io.mosip.registration.clientmanager.entity.UserToken;
import io.mosip.registration.packetmanager.util.HMACUtils2;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
public class UserDetailRepositoryTest {

    @Mock
    private UserDetailDao userDetailDao;

    @Mock
    private UserTokenDao userTokenDao;

    @Mock
    private UserPasswordDao userPasswordDao;

    @Mock
    private GlobalParamRepository globalParamRepository;

    @InjectMocks
    private UserDetailRepository userDetailRepository;

    private AutoCloseable closeable;

    @Before
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    /**
     * Test saveUserDetail() should save user details from JSONArray and verify correct insertion.
     */
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

    /**
     * Test isActiveUser() should return true when user exists.
     */
    @Test
    public void testIsActiveUser_UserExists() {
        when(userDetailDao.getUserDetail("9343")).thenReturn(new UserDetail("9343"));

        boolean result = userDetailRepository.isActiveUser("9343");

        assertTrue(result);
    }

    /**
     * Test isActiveUser() should return false when user does not exist.
     */
    @Test
    public void testIsActiveUser_UserDoesNotExist() {
        when(userDetailDao.getUserDetail("9343")).thenReturn(null);

        boolean result = userDetailRepository.isActiveUser("9343");

        assertFalse(result);
    }

    /**
     * Test getUserDetailByUserId() should return user detail for given userId.
     */
    @Test
    public void testGetUserDetailByUserId() {
        UserDetail user = new UserDetail("9343");
        when(userDetailDao.getUserDetail("9343")).thenReturn(user);

        UserDetail result = userDetailRepository.getUserDetailByUserId("9343");

        assertNotNull(result);
        assertEquals("9343", result.getId());
    }

    /**
     * Test getUserDetailCount() should return the correct count of user details.
     */
    @Test
    public void testGetUserDetailCount() {
        when(userDetailDao.getUserDetailCount()).thenReturn(5);

        int result = userDetailRepository.getUserDetailCount();

        assertEquals(5, result);
    }

    /**
     * Test isPasswordPresent() should return true when password exists for user.
     */
    @Test
    public void testIsPasswordPresent_PasswordExists() {
        UserPassword password = new UserPassword("9343");
        password.setSalt("someSalt");
        when(userPasswordDao.getUserPassword("9343")).thenReturn(password);
        when(userDetailDao.getUserDetail("9343")).thenReturn(new UserDetail("9343"));

        boolean result = userDetailRepository.isPasswordPresent("9343");

        assertTrue(result);
    }

    /**
     * Test isPasswordPresent() should return false when password does not exist for user.
     */
    @Test
    public void testIsPasswordPresent_NoPassword() {
        when(userPasswordDao.getUserPassword("9343")).thenReturn(null);
        when(userDetailDao.getUserDetail("9343")).thenReturn(new UserDetail("9343"));

        boolean result = userDetailRepository.isPasswordPresent("9343");

        assertFalse(result);
    }

    /**
     * Test isValidPassword() should return true for correct password.
     */
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

    /**
     * Test isValidPassword() should return false for incorrect password.
     */
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

    /**
     * Test setPasswordHash() should set and save new password hash for user.
     */
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

    /**
     * Test saveUserAuthToken() should save or update user authentication token.
     */
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

    /**
     * Test getUserAuthToken() should return token when user has a token.
     */
    @Test
    public void testGetUserAuthToken_UserHasToken() {
        UserToken token = new UserToken("9343", "authToken", "refreshToken", 1000L, 2000L);
        when(userTokenDao.findByUsername("9343")).thenReturn(token);

        String result = userDetailRepository.getUserAuthToken("9343");

        assertEquals("authToken", result);
    }

    /**
     * Test getUserAuthToken() should return empty string when user has no token.
     */
    @Test
    public void testGetUserAuthToken_UserHasNoToken() {
        when(userTokenDao.findByUsername("9343")).thenReturn(null);

        String result = userDetailRepository.getUserAuthToken("9343");

        assertEquals("", result);
    }

    /**
     * Test updateUserDetail() should update user detail as expected.
     */
    @Test
    public void testUpdateUserDetail() {
        doNothing().when(userDetailDao).updateUserDetail(anyBoolean(), anyString(), anyLong());
        userDetailRepository.updateUserDetail("9343");
        verify(userDetailDao).updateUserDetail(eq(true), eq("9343"), anyLong());
    }

    /**
     * Test isValidPassword() should return false when exception occurs during password validation.
     */
    @Test
    public void testIsValidPassword_Exception() throws NoSuchAlgorithmException {
        UserPassword password = new UserPassword("9343");
        password.setSalt("encodedSalt");
        password.setPwd("correctHash");

        when(userPasswordDao.getUserPassword("9343")).thenReturn(password);

        try (MockedStatic<HMACUtils2> mockedStatic = mockStatic(HMACUtils2.class)) {
            mockedStatic.when(() -> HMACUtils2.digestAsPlainTextWithSalt(any(byte[].class), any(byte[].class)))
                    .thenThrow(new NoSuchAlgorithmException("Algorithm not found"));

            boolean result = userDetailRepository.isValidPassword("9343", "1234");

            assertFalse(result);
        }
    }

    /**
     * Test setPasswordHash() should handle exception during password hashing and still insert password.
     */
    @Test
    public void testSetPasswordHash_Exception() throws NoSuchAlgorithmException {
        UserPassword password = new UserPassword("9343");
        password.setSalt("encodedSalt");

        when(userPasswordDao.getUserPassword("9343")).thenReturn(password);

        try (MockedStatic<HMACUtils2> mockedStatic = mockStatic(HMACUtils2.class)) {
            mockedStatic.when(() -> HMACUtils2.digestAsPlainTextWithSalt(any(byte[].class), any(byte[].class)))
                    .thenThrow(new NoSuchAlgorithmException("Algorithm not found"));

            userDetailRepository.setPasswordHash("9343", "admin123");

            verify(userPasswordDao).insertUserPassword(any(UserPassword.class));
        }
    }

    /**
     * Test setPasswordHash() should insert new password when UserPassword is null.
     */
    @Test
    public void testSetPasswordHash_UserPasswordNull() throws NoSuchAlgorithmException {
        when(userPasswordDao.getUserPassword("9343")).thenReturn(null);

        try (MockedStatic<HMACUtils2> mockedStatic = mockStatic(HMACUtils2.class)) {
            mockedStatic.when(() -> HMACUtils2.digestAsPlainTextWithSalt(any(byte[].class), any(byte[].class)))
                    .thenReturn("newHash");

            userDetailRepository.setPasswordHash("9343", "admin123");

            verify(userPasswordDao).insertUserPassword(any(UserPassword.class));
        }
    }

    /**
     * Test setPasswordHash() should generate and set salt if salt is null.
     */
    @Test
    public void testSetPasswordHash_SaltNull() throws NoSuchAlgorithmException {
        UserPassword password = new UserPassword("9343");
        password.setSalt(null);

        when(userPasswordDao.getUserPassword("9343")).thenReturn(password);

        try (MockedStatic<HMACUtils2> mockedStatic = mockStatic(HMACUtils2.class)) {
            mockedStatic.when(() -> HMACUtils2.digestAsPlainTextWithSalt(any(byte[].class), any(byte[].class)))
                    .thenReturn("newHash");

            userDetailRepository.setPasswordHash("9343", "admin123");

            verify(userPasswordDao).insertUserPassword(any(UserPassword.class));
            assertNotNull(password.getSalt());
        }
    }

    /**
     * Test isPasswordPresent() returns false when userDetail is null.
     */
    @Test
    public void testIsPasswordPresent_UserDetailNull() {
        UserPassword password = new UserPassword("9343");
        password.setSalt("salt");
        when(userPasswordDao.getUserPassword("9343")).thenReturn(password);
        when(userDetailDao.getUserDetail("9343")).thenReturn(null);

        boolean result = userDetailRepository.isPasswordPresent("9343");

        assertFalse(result);
    }

    /**
     * Test isPasswordPresent() returns false when salt is null.
     */
    @Test
    public void testIsPasswordPresent_SaltNull() {
        UserPassword password = new UserPassword("9343");
        password.setSalt(null);
        when(userPasswordDao.getUserPassword("9343")).thenReturn(password);
        when(userDetailDao.getUserDetail("9343")).thenReturn(new UserDetail("9343"));

        boolean result = userDetailRepository.isPasswordPresent("9343");

        assertFalse(result);
    }

    /**
     * Test saveUserAuthToken() creates new UserToken when none exists.
     */
    @Test
    public void testSaveUserAuthToken_NewToken() {
        when(userTokenDao.findByUsername("9343")).thenReturn(null);

        userDetailRepository.saveUserAuthToken("9343", "token1", "refresh1", 100L, 200L);

        ArgumentCaptor<UserToken> captor = ArgumentCaptor.forClass(UserToken.class);
        verify(userTokenDao).insert(captor.capture());
        assertEquals("9343", captor.getValue().getUsername());
        assertEquals("token1", captor.getValue().getToken());
        assertEquals("refresh1", captor.getValue().getRefreshToken());
        assertEquals(100L, captor.getValue().getTExpiry());
        assertEquals(200L, captor.getValue().getRExpiry());
    }

    /**
     * Test saveUserDetail() merges existing user attributes when user already exists.
     */
    @Test
    public void testSaveUserDetail_WithExistingUser() throws JSONException {
        UserDetail existing = new UserDetail("9343");
        existing.setName("Existing Name");
        existing.setDefault(true);
        existing.setSupervisor(true);
        existing.setOfficer(false);
        existing.setOnboarded(true);
        existing.setUnsuccessfulLoginCount(0);
        existing.setLastLoginDtimes(12345L);
        existing.setLastLoginMethod("PWD");
        existing.setUserLockTillDtimes(null);

        JSONArray users = new JSONArray();
        JSONObject userJson = new JSONObject();
        userJson.put("userId", "9343");
        userJson.put("isDeleted", false);
        userJson.put("isActive", true);
        userJson.put("regCenterId", "10011");
        users.put(userJson.toString());

        when(userDetailDao.getAllUserDetails()).thenReturn(new ArrayList<>(Arrays.asList(existing)));

        userDetailRepository.saveUserDetail(users);

        ArgumentCaptor<List<UserDetail>> captor = ArgumentCaptor.forClass(List.class);
        verify(userDetailDao).truncateAndInsertAll(captor.capture());
        UserDetail saved = (UserDetail) captor.getValue().get(0);
        assertEquals("9343", saved.getId());
        assertEquals("Existing Name", saved.getName());
        assertTrue(saved.isDefault());
        assertTrue(saved.isSupervisor());
        assertTrue(saved.isOnboarded());
        assertEquals(Long.valueOf(12345L), saved.getLastLoginDtimes());
        assertEquals("PWD", saved.getLastLoginMethod());
    }

    /**
     * Test isUserLocked() returns false when user does not exist.
     */
    @Test
    public void testIsUserLocked_UserNull() {
        when(userDetailDao.getUserDetail("9343")).thenReturn(null);

        boolean result = userDetailRepository.isUserLocked("9343");

        assertFalse(result);
    }

    /**
     * Test isUserLocked() returns false when lockUntil is null.
     */
    @Test
    public void testIsUserLocked_LockUntilNull() {
        UserDetail user = new UserDetail("9343");
        user.setUserLockTillDtimes(null);
        when(userDetailDao.getUserDetail("9343")).thenReturn(user);

        boolean result = userDetailRepository.isUserLocked("9343");

        assertFalse(result);
    }

    /**
     * Test isUserLocked() returns true when lockUntil is in future.
     */
    @Test
    public void testIsUserLocked_Locked() {
        UserDetail user = new UserDetail("9343");
        user.setUserLockTillDtimes(System.currentTimeMillis() + 60000);
        when(userDetailDao.getUserDetail("9343")).thenReturn(user);

        boolean result = userDetailRepository.isUserLocked("9343");

        assertTrue(result);
    }

    /**
     * Test isUserLocked() returns false and resets attempt count when lock has expired.
     */
    @Test
    public void testIsUserLocked_ExpiredLock() {
        UserDetail user = new UserDetail("9343");
        user.setUserLockTillDtimes(System.currentTimeMillis() - 1000);
        when(userDetailDao.getUserDetail("9343")).thenReturn(user);

        boolean result = userDetailRepository.isUserLocked("9343");

        assertFalse(result);
        verify(userDetailDao).updateLoginAttemptCount(eq("9343"), eq(0), isNull());
    }

    /**
     * Test recordFailedLoginAttempt() does nothing when user is null.
     */
    @Test
    public void testRecordFailedLoginAttempt_UserNull() {
        when(userDetailDao.getUserDetail("9343")).thenReturn(null);

        userDetailRepository.recordFailedLoginAttempt("9343");

        verify(userDetailDao, never()).updateLoginAttemptCount(anyString(), anyInt(), any());
    }

    /**
     * Test recordFailedLoginAttempt() increments count and applies lock when over max.
     */
    @Test
    public void testRecordFailedLoginAttempt_OverMax_LocksUser() {
        UserDetail user = new UserDetail("9343");
        user.setUnsuccessfulLoginCount(50);
        user.setUserLockTillDtimes(null);
        when(userDetailDao.getUserDetail("9343")).thenReturn(user);
        when(globalParamRepository.getCachedStringInvalidLoginCount()).thenReturn("50");
        when(globalParamRepository.getCachedStringInvalidLoginTime()).thenReturn("2");

        userDetailRepository.recordFailedLoginAttempt("9343");

        ArgumentCaptor<Long> lockCaptor = ArgumentCaptor.forClass(Long.class);
        verify(userDetailDao).updateLoginAttemptCount(eq("9343"), eq(51), lockCaptor.capture());
        assertNotNull(lockCaptor.getValue());
        assertTrue(lockCaptor.getValue() > System.currentTimeMillis());
    }

    /**
     * Test recordFailedLoginAttempt() increments count below max (uses default config when null).
     */
    @Test
    public void testRecordFailedLoginAttempt_BelowMax() {
        UserDetail user = new UserDetail("9343");
        user.setUnsuccessfulLoginCount(2);
        user.setUserLockTillDtimes(null);
        when(userDetailDao.getUserDetail("9343")).thenReturn(user);
        when(globalParamRepository.getCachedStringInvalidLoginCount()).thenReturn(null);
        when(globalParamRepository.getCachedStringInvalidLoginTime()).thenReturn(null);

        userDetailRepository.recordFailedLoginAttempt("9343");

        verify(userDetailDao).updateLoginAttemptCount(eq("9343"), eq(3), isNull());
    }

    /**
     * Test resetFailedLoginAttempts() does nothing when user is null.
     */
    @Test
    public void testResetFailedLoginAttempts_UserNull() {
        when(userDetailDao.getUserDetail("9343")).thenReturn(null);

        userDetailRepository.resetFailedLoginAttempts("9343");

        verify(userDetailDao, never()).updateLoginAttemptCount(anyString(), anyInt(), any());
    }

    /**
     * Test resetFailedLoginAttempts() does nothing when no failed attempts and not locked.
     */
    @Test
    public void testResetFailedLoginAttempts_NoAttemptsNoLock() {
        UserDetail user = new UserDetail("9343");
        user.setUnsuccessfulLoginCount(0);
        user.setUserLockTillDtimes(null);
        when(userDetailDao.getUserDetail("9343")).thenReturn(user);

        userDetailRepository.resetFailedLoginAttempts("9343");

        verify(userDetailDao, never()).updateLoginAttemptCount(anyString(), anyInt(), any());
    }

    /**
     * Test resetFailedLoginAttempts() clears count and lock when user had attempts.
     */
    @Test
    public void testResetFailedLoginAttempts_ClearsCountAndLock() {
        UserDetail user = new UserDetail("9343");
        user.setUnsuccessfulLoginCount(3);
        user.setUserLockTillDtimes(System.currentTimeMillis() + 60000);
        when(userDetailDao.getUserDetail("9343")).thenReturn(user);

        userDetailRepository.resetFailedLoginAttempts("9343");

        verify(userDetailDao).updateLoginAttemptCount(eq("9343"), eq(0), isNull());
    }

}
