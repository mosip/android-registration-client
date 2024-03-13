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
    public void getPacketUploadedDetails(@NonNull DashBoardPigeon.Result<List<String>> result) {
        try{
            List<Registration> uploadedValues = this.registrationRepository.getAllRegistrationByStatus(PacketClientStatus.UPLOADED.name());
            List<String> response = new ArrayList<>();
            uploadedValues.forEach((v) -> {
                    // current date
                     long currentTime = System.currentTimeMillis();
                     long thirtyDaysAgo = currentTime - (30 * 24 * 60 * 60 * 1000L);
                     if(v.getCrDtime() >= thirtyDaysAgo) {
                        response.add(v.getCrDtime().toString());
                     }
            });
            result.success(response);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Getting UploadedData failed!" + Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void getPacketUploadedPendingDetails(@NonNull DashBoardPigeon.Result<List<String>> result) {
        try{
            List<Registration> pendingValues = this.registrationRepository.getAllRegistrationByStatus(PacketClientStatus.SYNCED.name());
            List<String> response = new ArrayList<>();
            pendingValues.forEach((v) -> {
                long currentTime = System.currentTimeMillis();
                long thirtyDaysAgo = currentTime - (30 * 24 * 60 * 60 * 1000L);
                if(v.getCrDtime() >= thirtyDaysAgo) {
                    response.add(v.getCrDtime().toString());
                }
            });
            result.success(response);
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Getting UploadPendingData failed!" + Arrays.toString(e.getStackTrace()));
        }
    }
}