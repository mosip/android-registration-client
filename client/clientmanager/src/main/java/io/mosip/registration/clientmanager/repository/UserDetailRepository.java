package io.mosip.registration.clientmanager.repository;

import io.mosip.registration.clientmanager.dao.UserDetailDao;
import io.mosip.registration.clientmanager.dao.UserTokenDao;
import io.mosip.registration.clientmanager.entity.UserDetail;
import io.mosip.registration.keymanager.entity.KeyStore;
import org.json.JSONArray;
import org.json.JSONException;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDetailRepository {

    private UserDetailDao userDetailDao;
    private UserTokenDao userTokenDao;

    @Inject
    public UserDetailRepository(UserDetailDao userDetailDao, UserTokenDao userTokenDao) {
        this.userDetailDao = userDetailDao;
        this.userTokenDao = userTokenDao;
    }

    public void saveUserDetail(JSONArray users) throws JSONException {
        List<UserDetail> existingUsers = userDetailDao.getAllUserDetails();
        List<UserDetail> userDetailList = new ArrayList<>();
        for(int i =0 ;i < users.length(); i++) {
            String userId = users.getJSONObject(i).getString("userId");
            Optional<UserDetail> result = existingUsers.stream()
                    .filter(u-> u.getId().equalsIgnoreCase(userId))
                    .findFirst();

            UserDetail userDetail = new UserDetail(userId);
            userDetail.setIsDeleted(users.getJSONObject(i).getBoolean("isDeleted"));
            userDetail.setIsActive(users.getJSONObject(i).getBoolean("isActive"));
            userDetail.setRegCenterId(users.getJSONObject(i).getString("regCenterId"));
            userDetail.setName(userId);

            if(result.isPresent()) {
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

    public int getUserDetailCount() {
        return userDetailDao.getUserDetailCount();
    }
}
