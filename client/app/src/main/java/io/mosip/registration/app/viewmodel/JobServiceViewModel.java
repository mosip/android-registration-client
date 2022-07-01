package io.mosip.registration.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import io.mosip.registration.app.viewmodel.model.JobServiceModel;
import io.mosip.registration.clientmanager.entity.SyncJobDef;
import io.mosip.registration.clientmanager.spi.JobManagerService;

public class JobServiceViewModel extends ViewModel implements IListingViewModel {

    private static final String TAG = JobServiceViewModel.class.getSimpleName();

    JobManagerService jobManagerService;

    public JobServiceViewModel(JobManagerService jobManagerService) {
        this.jobManagerService = jobManagerService;
    }

    private MutableLiveData<List<JobServiceModel>> jobServiceList;

    @Override
    public LiveData<List<JobServiceModel>> getList() {

        jobServiceList = new MutableLiveData<>();
        loadServices();
        return jobServiceList;
    }

    private void loadServices() {
        List<JobServiceModel> jobServices = new ArrayList<>();

        List<SyncJobDef> syncJobDefList = jobManagerService.getAllSyncJobDefList();

        for (SyncJobDef jobDef : syncJobDefList) {
            int jobId = jobManagerService.generateJobServiceId(jobDef.getId());

            boolean isActiveAndImplemented = jobDef.getIsActive() && jobManagerService.isJobImplementedOnRegClient(jobDef.getApiName());
            boolean isScheduled = jobManagerService.isJobScheduled(jobId);

            String lastSyncTime = jobManagerService.getLastSyncTime(jobId);
            String nextSyncTime = jobManagerService.getNextSyncTime(jobId);


            jobServices.add(new JobServiceModel(
                    jobId,
                    jobDef.getName(),
                    jobDef.getApiName(),
                    isActiveAndImplemented,
                    isScheduled,
                    jobDef.getSyncFreq(),
                    lastSyncTime,
                    nextSyncTime
            ));
        }

        jobServiceList.setValue(jobServices);
    }
}
