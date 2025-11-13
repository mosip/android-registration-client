package io.mosip.registration.clientmanager.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.mosip.registration.clientmanager.dao.UserRoleDao;
import io.mosip.registration.clientmanager.entity.UserRole;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserRoleRepositoryTest {

    @Mock
    private UserRoleDao userRoleDao;

    @InjectMocks
    private UserRoleRepository repository;

    private static final String USER_ID = "arcbase";
    private static final String ROLE_OPERATOR = "REGISTRATION_OPERATOR";
    private static final String ROLE_OFFICER = "REGISTRATION_OFFICER";

    @Test
    public void saveRoles_replacesRolesForUser() {
        List<String> roleCodes = Arrays.asList(ROLE_OPERATOR, ROLE_OFFICER);

        repository.saveRoles(USER_ID, roleCodes);

        verify(userRoleDao).deleteByUsrId(USER_ID);
        verify(userRoleDao, times(roleCodes.size())).insert(any(UserRole.class));

        // Verify inserted roles contain expected userId and codes
        ArgumentCaptor<UserRole> captor = forClass(UserRole.class);
        verify(userRoleDao, times(roleCodes.size())).insert(captor.capture());
        List<UserRole> captured = captor.getAllValues();
        assertEquals(2, captured.size());
        assertEquals(ROLE_OPERATOR, captured.get(0).getRoleCode());
        assertEquals(USER_ID, captured.get(0).getUsrId());
        assertEquals(ROLE_OFFICER, captured.get(1).getRoleCode());
        assertEquals(USER_ID, captured.get(1).getUsrId());
    }

    @Test
    public void getRolesByUserId_returnsRoleCodes() {
        List<UserRole> stored = Arrays.asList(
                new UserRole(USER_ID, ROLE_OPERATOR, null),
                new UserRole(USER_ID, ROLE_OFFICER, null)
        );
        when(userRoleDao.findByUsrId(USER_ID)).thenReturn(stored);

        List<String> roles = repository.getRolesByUserId(USER_ID);

        assertEquals(Arrays.asList(ROLE_OPERATOR, ROLE_OFFICER), roles);
        verify(userRoleDao).findByUsrId(USER_ID);
    }

    @Test
    public void getRolesByUserId_handlesEmptyList() {
        when(userRoleDao.findByUsrId(USER_ID)).thenReturn(Collections.emptyList());

        List<String> roles = repository.getRolesByUserId(USER_ID);

        assertEquals(Collections.emptyList(), roles);
        verify(userRoleDao).findByUsrId(USER_ID);
    }
}