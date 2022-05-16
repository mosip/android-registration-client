package io.mosip.registration.app.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import io.mosip.registration.app.util.JobServiceHelper;
import io.mosip.registration.app.viewmodel.model.JobServiceModel;
import io.mosip.registration.clientmanager.entity.SyncJobDef;
import io.mosip.registration.clientmanager.spi.PacketService;

public class JobServiceViewModel extends ViewModel implements IListingViewModel {

    private static final String TAG = JobServiceViewModel.class.getSimpleName();
    private static final int numLengthLimit = 5;

    JobServiceHelper jobServiceHelper;
    PacketService packetService;

    public JobServiceViewModel(JobServiceHelper jobServiceHelper, PacketService packetService) {
        this.jobServiceHelper = jobServiceHelper;
        this.packetService = packetService;
    }

    private MutableLiveData<List<JobServiceModel>> jobServiceList;

    @Override
    public LiveData<List<JobServiceModel>> getList() {
        if (jobServiceList == null) {
            jobServiceList = new MutableLiveData<>();
            loadServices();
        }
        return jobServiceList;
    }

    private void loadServices() {
        List<JobServiceModel> jobServices = new ArrayList<>();

        List<SyncJobDef> syncJobDefList = packetService.getAllSyncJobDefList();
        for (SyncJobDef jobDef : syncJobDefList) {

            boolean isImplemented = jobServiceHelper.isJobImplemented(jobDef.getApiName());
            boolean isEnabled = jobServiceHelper.isJobEnabled(getId(jobDef.getId()));

            jobServices.add(new JobServiceModel(
                    getId(jobDef.getId()),
                    jobDef.getName(),
                    jobDef.getApiName(),
                    jobDef.getParentSyncJobId(),
                    jobDef.getSyncFreq(),
                    jobDef.getLockDuration(),
                    jobDef.getLangCode(),
                    jobDef.getIsDeleted(),
                    jobDef.getIsActive(),
                    isImplemented,
                    isEnabled
            ));
        }

        jobServiceList.setValue(jobServices);
    }

    private int getId(String jobId) {
        try {
            String lastCharsWithNumLengthLimit = jobId.substring(jobId.length() - numLengthLimit);
            return Integer.parseInt(lastCharsWithNumLengthLimit);
        } catch (Exception ex) {
            Log.e(TAG, "Conversion of jobId : " + jobId + "to int failed for length " + numLengthLimit + ex.getMessage());
            throw ex;
        }
    }
}
