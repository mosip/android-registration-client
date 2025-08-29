package io.mosip.registration.clientmanager.repository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.mosip.registration.clientmanager.dao.UserRoleDao;
import io.mosip.registration.clientmanager.entity.UserRole;

public class UserRoleRepository {

    private final UserRoleDao userRoleDao;

    @Inject
    public UserRoleRepository(UserRoleDao userRoleDao) {
        this.userRoleDao = userRoleDao;
    }

    public void saveRoles(String userId, List<String> roleCodes) {
        userRoleDao.deleteByUsrId(userId);
        for (String code : roleCodes) {
            userRoleDao.insert(new UserRole(userId, code, null));
        }
    }

    public List<String> getRolesByUserId(String userId) {
        List<UserRole> rows = userRoleDao.findByUsrId(userId);
        List<String> codes = new ArrayList<>();
        for (UserRole r : rows) {
            codes.add(r.getRoleCode());
        }
        return codes;
    }
}