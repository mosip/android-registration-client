package io.mosip.registration.clientmanager.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import io.mosip.registration.clientmanager.entity.Registration;

import java.util.List;

@Dao
public interface RegistrationDao {

    @Query("SELECT * FROM registration where packet_id=:packetId")
    Registration findOneByPacketId(String packetId);

    @Query("SELECT * FROM registration order by cr_dtimes desc")
    List<Registration> findAll();

    @Query("SELECT * FROM registration where client_status!='UPLOADED' AND client_status!='CREATED' order by cr_dtimes desc")
    List<Registration> findAllNotUploaded();

    @Query("SELECT * FROM registration where client_status = :status order by cr_dtimes desc limit :batchSize")
    List<Registration> findRegistrationByStatus(String status, Integer batchSize);

    @Query("SELECT * FROM registration where server_status in (:statuses) order by cr_dtimes desc")
    List<Registration> findAllByServerStatus(List<String> statuses);

    @Query("SELECT * FROM registration where client_status in (:statuses) order by cr_dtimes desc")
    List<Registration> findAllByClientStatus(List<String> statuses);

    @Query("SELECT * FROM registration where client_status in (:clientStatuses) and server_status in (:serverStatuses) order by cr_dtimes desc")
    List<Registration> findAllByClientStatusAndServerStatus(List<String> clientStatuses, List<String> serverStatuses);

    @Query("Update registration set server_status = :status where packet_id = :packetId")
    void updateServerStatus(String packetId, String status);

    @Query("Update registration set client_status = :status where packet_id = :packetId")
    void updateClientStatus(String packetId, String status);

    @Query("Update registration set client_status = :clientStatus,  server_status = :serverStatus where packet_id = :packetId")
    void updateStatus(String packetId, String clientStatus, String serverStatus);

    @Query("Update registration set client_status = :supervisorStatus,  client_status_comment = :supervisorComment where packet_id = :packetId")
    void updateSupervisorReview(String packetId, String supervisorStatus, String supervisorComment);

    @Insert
    void insert(Registration registration);

    @Query("delete from registration where packet_id = :packetId")
    void delete(String packetId);

    @Query("SELECT COUNT (*) FROM registration where client_status = :status")
    int findAllRegistrationByStatus(String status);

    @Query("SELECT COUNT(*) FROM registration where client_status in (:syncedStatus, :approvedStatus)")
    int findRegistrationCountBySyncedStatusAndApprovedStatus(String syncedStatus, String approvedStatus);


    @Query("SELECT COUNT(*) FROM registration where client_status in (:syncedStatus)")
    int findRegistrationCountBySyncedStatus(String syncedStatus);

    @Query("SELECT COUNT(*) FROM registration where cr_dtimes < CURRENT_DATE")
    int getAllCreatedPacketStatus();

}
