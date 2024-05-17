
package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

import io.mosip.registration.clientmanager.entity.PreRegistrationList;
import io.mosip.registration.keymanager.entity.CACertificateStore;


/**
 * Pre registration repository to get/save/update and verify pre-reg
 *
 * @author YASWANTH S
 * @author Dinesh Ashokan
 * @since 1.0.0
 *
 */

@Dao
public interface PreRegistrationDataSyncRepositoryDao {
    @Query("select * from pre_registration_list where prereg_id = :preRegId")
    PreRegistrationList findByPreRegId(String preRegId);

    @Query("SELECT * FROM pre_registration_list WHERE appointment_date < :startDate AND is_deleted = :isDeleted")
    List<PreRegistrationList> findByAppointmentDateBeforeAndIsDeleted(String startDate, Boolean isDeleted);

    @Query("SELECT * FROM pre_registration_list ORDER BY last_upd_dtimes DESC")
    PreRegistrationList findTopByOrderByLastUpdatedPreRegTimeStampDesc();
//    @Update
//    long update(PreRegistrationList preReg);

    @Insert()
    long save(PreRegistrationList preRegistration);

    @Query("SELECT * FROM pre_registration_list WHERE id = :id LIMIT 1")
    PreRegistrationList getById(long id);

}