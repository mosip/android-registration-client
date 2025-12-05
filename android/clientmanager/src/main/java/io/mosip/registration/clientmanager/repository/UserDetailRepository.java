package io.mosip.registration.clientmanager.repository;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.mosip.registration.clientmanager.dao.UserDetailDao;
import io.mosip.registration.clientmanager.dao.UserPasswordDao;
import io.mosip.registration.clientmanager.dao.UserTokenDao;
import io.mosip.registration.clientmanager.entity.UserDetail;
import io.mosip.registration.clientmanager.entity.UserPassword;
import io.mosip.registration.clientmanager.entity.UserToken;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.keymanager.util.CryptoUtil;
import io.mosip.registration.packetmanager.util.DateUtils;
import io.mosip.registration.packetmanager.util.HMACUtils2;

public class UserDetailRepository {

    private UserDetailDao userDetailDao;
    private UserTokenDao userTokenDao;
    private UserPasswordDao userPasswordDao;
    private GlobalParamRepository globalParamRepository;

    @Inject
    public UserDetailRepository(UserDetailDao userDetailDao, UserTokenDao userTokenDao,
                                UserPasswordDao userPasswordDao, GlobalParamRepository globalParamRepository) {
        this.userDetailDao = userDetailDao;
        this.userTokenDao = userTokenDao;
        this.userPasswordDao = userPasswordDao;
        this.globalParamRepository = globalParamRepository;
    }

    public void saveUserDetail(JSONArray users) throws JSONException {
        List<UserDetail> existingUsers = userDetailDao.getAllUserDetails();
        List<UserDetail> userDetailList = new ArrayList<>();
        for (int i = 0; i < users.length(); i++) {
            JSONObject jsonObject = new JSONObject(users.getString(i));
            String userId = jsonObject.getString("userId");
            Optional<UserDetail> result = existingUsers.stream()
                    .filter(u -> u.getId().equalsIgnoreCase(userId))
                    .findFirst();

            UserDetail userDetail = new UserDetail(userId);
            userDetail.setIsDeleted(jsonObject.optBoolean("isDeleted"));
            userDetail.setIsActive(jsonObject.getBoolean("isActive"));
            userDetail.setRegCenterId(jsonObject.getString("regCenterId"));
            userDetail.setName(userId);

            if (result.isPresent()) {
                userDetail.setName(result.get().getName());
                userDetail.setDefault(result.get().isDefault());
                userDetail.setSupervisor(result.get().isSupervisor());
                userDetail.setOfficer(result.get().isOfficer());
                userDetail.setOnboarded(result.get().isOnboarded());
                userDetail.setUnsuccessfulLoginCount(result.get().getUnsuccessfulLoginCount());
                userDetail.setLastLoginDtimes(result.get().getLastLoginDtimes());
                userDetail.setLastLoginMethod(result.get().getLastLoginMethod());
                userDetail.setUserLockTillDtimes(result.get().getUserLockTillDtimes());
                existingUsers.remove(result.get());
            }
            userDetailList.add(userDetail);
        }
        userDetailDao.truncateAndInsertAll(userDetailList);
    }

    public boolean isActiveUser(String userId) {
        return userDetailDao.getUserDetail(userId) != null;
    }

    public UserDetail getUserDetailByUserId(String userId){
        return userDetailDao.getUserDetail(userId);
    }

    public int getUserDetailCount() {
        return userDetailDao.getUserDetailCount();
    }

    public boolean isPasswordPresent(String userId) {
        UserPassword userPassword = userPasswordDao.getUserPassword(userId);
        UserDetail userDetail = userDetailDao.getUserDetail(userId);
        if(userDetail == null || userPassword == null || userPassword.getSalt() == null) {
            return false;
        }
        return true;
    }

    public boolean isValidPassword(String userId, String password) {
        UserPassword userPassword = userPasswordDao.getUserPassword(userId);
        boolean isValid = false;

        try {
            isValid = HMACUtils2.digestAsPlainTextWithSalt(
                    password.getBytes(),
                    CryptoUtil.base64decoder.decode(userPassword.getSalt())
            ).equals(userPassword.getPwd());
        } catch (NoSuchAlgorithmException e) {
            isValid = false;
            Log.e(getClass().getSimpleName(), e.getMessage());
        }

        return isValid;
    }

    public void setPasswordHash(String userId, String password) {
        UserPassword userPassword = userPasswordDao.getUserPassword(userId);
        if (userPassword == null) {
            userPassword = new UserPassword(userId);
        }
        if (userPassword.getSalt() == null) {
            userPassword.setSalt(
                    CryptoUtil.base64encoder.
                            encodeToString(DateUtils.formatToISOString(LocalDateTime.now()).getBytes())
            );
        }

        try {
            userPassword.setPwd(
                    HMACUtils2.digestAsPlainTextWithSalt(
                            password.getBytes(),
                            CryptoUtil.base64decoder.decode(userPassword.getSalt())
                    )
            );
        } catch (NoSuchAlgorithmException e) {
            Log.e(getClass().getSimpleName(), e.getMessage());
        }

//        userPassword.setUpdDtimes(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime().toString()).toString());

        userPasswordDao.insertUserPassword(userPassword);
    }

    public void saveUserAuthToken(String userId, String token, String refreshToken,
                                     long tExpiry, long rExpiry) {
        UserToken userToken = userTokenDao.findByUsername(userId);
        if(userToken == null) {
            userToken = new UserToken(userId, "", "", 0, 0);
        }
        userToken.setToken(token);
        userToken.setRefreshToken(refreshToken);
        userToken.setTExpiry(tExpiry);
        userToken.setRExpiry(rExpiry);

        userTokenDao.insert(userToken);
    }
    public void updateUserDetail(String userId){
        Long updatedTime = System.currentTimeMillis();
        userDetailDao.updateUserDetail(true,userId,updatedTime);
    }
    public String getUserAuthToken(String userId) {
        UserToken userToken = userTokenDao.findByUsername(userId);

        if(userToken == null) {
            return "";
        }

        return userToken.getToken();
    }

    public boolean isUserLocked(String userId) {
        UserDetail userDetail = userDetailDao.getUserDetail(userId);
        if(userDetail == null) {
            return false;
        }
        Long lockUntil = userDetail.getUserLockTillDtimes();
        if(lockUntil == null) {
            return false;
        }
        long currentTime = System.currentTimeMillis();
        if(lockUntil > currentTime) {
            return true;
        }
        userDetailDao.updateLoginAttemptMeta(userId, 0, null);
        return false;
    }

    public void recordFailedLoginAttempt(String userId) {
        UserDetail userDetail = userDetailDao.getUserDetail(userId);
        boolean isNewUser = false;
        if(userDetail == null) {
            userDetail = new UserDetail(userId);
            userDetail.setIsActive(true);
            isNewUser = true;
        }
        Integer failedAttempts = Optional.ofNullable(userDetail.getUnsuccessfulLoginCount()).orElse(0);
        failedAttempts++;
        Long lockUntil = userDetail.getUserLockTillDtimes();

        int maxFailedAttempts = 50;
        try {
            String count = globalParamRepository.getCachedStringInvalidLoginCount();
            if (count != null) {
                maxFailedAttempts = Integer.parseInt(count);
            }
        } catch (NumberFormatException e) {
            Log.e(getClass().getSimpleName(), "Invalid login count config format", e);
        }

        long lockDurationMillis = TimeUnit.MINUTES.toMillis(2);
        try {
            String time = globalParamRepository.getCachedStringInvalidLoginTime();
            if (time != null) {
                lockDurationMillis = TimeUnit.MINUTES.toMillis(Long.parseLong(time));
            }
        } catch (NumberFormatException e) {
            Log.e(getClass().getSimpleName(), "Invalid login time config format", e);
        }

        if(failedAttempts >= maxFailedAttempts) {
            lockUntil = System.currentTimeMillis() + lockDurationMillis;
        } else if(lockUntil != null && lockUntil <= System.currentTimeMillis()) {
            lockUntil = null;
        }

        if (isNewUser) {
            userDetail.setUnsuccessfulLoginCount(failedAttempts);
            userDetail.setUserLockTillDtimes(lockUntil);
            List<UserDetail> userList = new ArrayList<>();
            userList.add(userDetail);
            userDetailDao.insertAllUsers(userList);
        } else {
            userDetailDao.updateLoginAttemptMeta(userId, failedAttempts, lockUntil);
        }
    }

    public void resetFailedLoginAttempts(String userId) {
        UserDetail userDetail = userDetailDao.getUserDetail(userId);
        if(userDetail == null) {
            return;
        }
        Integer failedAttempts = userDetail.getUnsuccessfulLoginCount();
        Long lockUntil = userDetail.getUserLockTillDtimes();
        if((failedAttempts == null || failedAttempts == 0) && lockUntil == null) {
            return;
        }
        userDetailDao.updateLoginAttemptMeta(userId, 0, null);
    }
}
