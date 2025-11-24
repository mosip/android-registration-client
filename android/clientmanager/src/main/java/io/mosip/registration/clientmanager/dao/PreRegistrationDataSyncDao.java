package io.mosip.registration.clientmanager.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import io.mosip.registration.clientmanager.entity.PreRegistrationList;


/**
 * This class is used to fetch the specific pre registration by passing pre registration id as parameter,
 * To save the new pre registration record to {@link PreRegistrationList} table. To fetch the list
 * of pre registration that needs to be deleted by passing start date as parameter from
 * {@link PreRegistrationList} table. To update the pre registration record in the {@link PreRegistrationList} table.
 * To delete all the specifically given list of pre registration records and to fetch all the pre registration
 * present in the {@link PreRegistrationList} table.
 *
 * @author YASWANTH S
 * @since 1.0.0
 */
public interface PreRegistrationDataSyncDao {


    public PreRegistrationList get(String preRegId);


    public void save(PreRegistrationList preRegistration);


    public List<PreRegistrationList> fetchRecordsToBeDeleted(Date startDate);


    public long update(String id,String updatedBy,String updatedTime);


    public void deleteAll(List<PreRegistrationList> preRegistrationList);


    // List<PreRegistrationList> getAllPreRegPackets();


    public String getLastPreRegPacketDownloadedTime();

    public PreRegistrationList getById(String id);

    public Timestamp getLastPreRegPacketDownloadedTimeAsTimestamp();
}