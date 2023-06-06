package io.mosip.registration.clientmanager.repository;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import io.mosip.registration.clientmanager.dao.UserDetailDao;
import io.mosip.registration.clientmanager.dao.UserPasswordDao;
import io.mosip.registration.clientmanager.dao.UserTokenDao;
import io.mosip.registration.clientmanager.entity.UserDetail;
import io.mosip.registration.clientmanager.entity.UserPassword;
import io.mosip.registration.keymanager.util.CryptoUtil;
import io.mosip.registration.packetmanager.util.DateUtils;
import io.mosip.registration.packetmanager.util.HMACUtils2;

public class UserDetailRepository {

    private UserDetailDao userDetailDao;
    private UserTokenDao userTokenDao;
    private UserPasswordDao userPasswordDao;

    @Inject
    public UserDetailRepository(UserDetailDao userDetailDao, UserTokenDao userTokenDao,
                                UserPasswordDao userPasswordDao) {
        this.userDetailDao = userDetailDao;
        this.userTokenDao = userTokenDao;
        this.userPasswordDao = userPasswordDao;
    }

    public void saveUserDetail(JSONArray users) throws JSONException {
        List<UserDetail> existingUsers = userDetailDao.getAllUserDetails();
        List<UserDetail> userDetailList = new ArrayList<>();
        for (int i = 0; i < users.length(); i++) {
            String userId = users.getJSONObject(i).getString("userId");
            Optional<UserDetail> result = existingUsers.stream()
                    .filter(u -> u.getId().equalsIgnoreCase(userId))
                    .findFirst();

            UserDetail userDetail = new UserDetail(userId);
            userDetail.setIsDeleted(users.getJSONObject(i).getBoolean("isDeleted"));
            userDetail.setIsActive(users.getJSONObject(i).getBoolean("isActive"));
            userDetail.setRegCenterId(users.getJSONObject(i).getString("regCenterId"));
            userDetail.setName(userId);

            if (result.isPresent()) {
                // userDetail.setName(result.get().getName());
                userDetail.setSalt(result.get().getSalt());
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
        if(userDetail == null || userDetail.getSalt() == null || userPassword == null) {
            return false;
        }
        return true;
    }
    public boolean isValidPassword(String userId, String password) throws Exception {
        UserPassword userPassword = userPasswordDao.getUserPassword(userId);
        UserDetail userDetail = userDetailDao.getUserDetail(userId);

        return HMACUtils2.digestAsPlainTextWithSalt(
                    password.getBytes(),
                    CryptoUtil.base64decoder.decode(userDetail.getSalt())
                ).equals(userPassword.getPwd());
    }

    public void setPasswordHash(String userId, String password) throws Exception {
        UserPassword userPassword = userPasswordDao.getUserPassword(userId);
        UserDetail userDetail = userDetailDao.getUserDetail(userId);
        List<UserDetail> userDetailList = new ArrayList<>();
        if(userDetail == null) {
            userDetail = new UserDetail(userId);
        }
        userDetail.setName(userId);
        if (userDetail.getSalt() == null) {
            userDetail.setSalt(
                    CryptoUtil.base64encoder.
                            encodeToString(DateUtils.formatToISOString(LocalDateTime.now()).getBytes())
            );
        }


        if (userPassword == null) {
            userPassword = new UserPassword(userId);
        }
        userPassword.setPwd(
                HMACUtils2.digestAsPlainTextWithSalt(
                        password.getBytes(),
                        CryptoUtil.base64decoder.decode(userDetail.getSalt())
                )
        );
//        userPassword.setUpdDtimes(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime().toString()).toString());

        userDetailList.add(userDetail);
        userDetailDao.insertAllUsers(userDetailList);
        userPasswordDao.insertUserPassword(userPassword);
    }

    public void deletePasswordHash(String userId) {
        userPasswordDao.deleteUserPassword(userId);
    }
}
