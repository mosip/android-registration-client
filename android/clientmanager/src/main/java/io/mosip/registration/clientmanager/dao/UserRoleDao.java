package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.mosip.registration.clientmanager.entity.UserRole;

@Dao
public interface UserRoleDao {

	@Query("SELECT * FROM user_role WHERE usr_id = :usrId")
	List<UserRole> findByUsrId(String usrId);

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void insert(UserRole userRole);

	@Query("DELETE FROM user_role WHERE usr_id = :usrId")
	void deleteByUsrId(String usrId);

	@Query("DELETE FROM user_role")
	void deleteAll();
}


