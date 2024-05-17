package io.mosip.registration.clientmanager.dao;

import io.mosip.registration.clientmanager.entity.PreRegistrationList;
import java.util.List;


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

    
    public long save(PreRegistrationList preRegistration);

   
    public List<PreRegistrationList> fetchRecordsToBeDeleted(String startDate);

   
  //  public long update(PreRegistrationList preReg);

    
    //public void deleteAll(List<PreRegistrationList> preRegistrationList);

    
   // List<PreRegistrationList> getAllPreRegPackets();

   
    public String getLastPreRegPacketDownloadedTime();

    public PreRegistrationList getById(long id);
}
