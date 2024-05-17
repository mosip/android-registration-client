package io.mosip.registration_client.api_services;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.constant.PacketClientStatus;
import io.mosip.registration.clientmanager.dao.UserDetailDao;
import io.mosip.registration.clientmanager.entity.Registration;
import io.mosip.registration.clientmanager.entity.UserDetail;
import io.mosip.registration.clientmanager.repository.RegistrationRepository;
import io.mosip.registration_client.model.DashBoardPigeon;

@Singleton
public class DashBoardDetailsApi implements DashBoardPigeon.DashBoardApi {
    private UserDetailDao userDetailDao;
    private RegistrationRepository registrationRepository;

    @Inject
    public DashBoardDetailsApi(UserDetailDao userDetailDao, RegistrationRepository registrationRepository){
        this.userDetailDao = userDetailDao;
        this.registrationRepository = registrationRepository;
    };

    @Override
    public void getDashBoardDetails(@NonNull DashBoardPigeon.Result<List<DashBoardPigeon.DashBoardData>> result) {
        List<DashBoardPigeon.DashBoardData> dashBoardList = new ArrayList<>();
        try{
            List<UserDetail> dashBoardValues = this.userDetailDao.getAllUserDetails();
            dashBoardValues.forEach((v) -> {
                DashBoardPigeon.DashBoardData data = new DashBoardPigeon.DashBoardData.Builder()
                        .setUserId(v.getId())
                        .setUserName(v.getName())
                        .setUserStatus(v.getIsActive())
                        .setUserIsOnboarded(v.isOnboarded())
                        .build();
                dashBoardList.add(data);
            });
            result.success(dashBoardList);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Getting DashBoardData failed!" + Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void getPacketUploadedDetails(@NonNull DashBoardPigeon.Result<Long> result) {
        int uploadedValues = 0;
        try{
            uploadedValues = this.registrationRepository.getAllRegistrationByStatus(PacketClientStatus.UPLOADED.name());
//            List<String> response = new ArrayList<>();
//            uploadedValues.forEach((v) -> {
//                response.add(v.getCrDtime().toString());
//                    // current date
////                     long currentTime = System.currentTimeMillis();
////                     long thirtyDaysAgo = currentTime - (30 * 24 * 60 * 60 * 1000L);
////                     if(v.getCrDtime() >= thirtyDaysAgo) {
////                        response.add(v.getCrDtime().toString());
////                     }
//            });
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Getting UploadedData failed!" + Arrays.toString(e.getStackTrace()));
        }
        result.success((long)uploadedValues);
    }

    @Override
    public void getPacketUploadedPendingDetails(@NonNull DashBoardPigeon.Result<Long> result) {
        int pendingValues = 0;
        try{
            pendingValues = this.registrationRepository.getAllRegistrationByPendingStatus(PacketClientStatus.SYNCED.name(),PacketClientStatus.APPROVED.name());
//            List<String> response = new ArrayList<>();
//            pendingValues.forEach((v) -> {
//                response.add(v.getCrDtime().toString());
////                long currentTime = System.currentTimeMillis();
////                long thirtyDaysAgo = currentTime - (30 * 24 * 60 * 60 * 1000L);
////                if(v.getCrDtime() >= thirtyDaysAgo) {
////                    response.add(v.getCrDtime().toString());
////                }
//            });
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Getting UploadPendingData failed!" + Arrays.toString(e.getStackTrace()));
        }
        result.success((long)pendingValues);
    }

    @Override
    public void getCreatedPacketDetails(@NonNull DashBoardPigeon.Result<Long> result) {
        int createdPacketsCount = 0;
        try{
            createdPacketsCount = this.registrationRepository.getAllCreatedPacketStatus();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Getting CreatedData failed!" + Arrays.toString(e.getStackTrace()));
        }
        result.success((long)createdPacketsCount);
    }

    public void getSyncedPacketDetails(@NonNull DashBoardPigeon.Result<Long> result) {
        int syncedPacketsCount = 0;
        try{
            syncedPacketsCount = this.registrationRepository.findRegistrationCountBySyncedStatus(PacketClientStatus.SYNCED.name());
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Getting SyncedData failed!" + Arrays.toString(e.getStackTrace()));
        }
        result.success((long)syncedPacketsCount);
    }
}