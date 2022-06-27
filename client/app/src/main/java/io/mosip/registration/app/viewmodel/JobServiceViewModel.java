package io.mosip.registration.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import io.mosip.registration.app.util.JobServiceHelper;
import io.mosip.registration.app.viewmodel.model.JobServiceModel;
import io.mosip.registration.clientmanager.entity.SyncJobDef;

public class JobServiceViewModel extends ViewModel implements IListingViewModel {

    private static final String TAG = JobServiceViewModel.class.getSimpleName();

    JobServiceHelper jobServiceHelper;

    public JobServiceViewModel(JobServiceHelper jobServiceHelper) {
        this.jobServiceHelper = jobServiceHelper;
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

        List<SyncJobDef> syncJobDefList = jobServiceHelper.getAllSyncJobDefList();

        for (SyncJobDef jobDef : syncJobDefList) {
            int jobId = jobServiceHelper.getId(jobDef.getId());

            boolean isActiveAndImplemented = jobDef.getIsActive() && jobServiceHelper.isJobImplementedOnRegClient(jobDef.getApiName());
            boolean isScheduled = jobServiceHelper.isJobScheduled(jobId);

            String lastSyncTime = jobServiceHelper.getLastSyncTime(jobId);
            String nextSyncTime = jobServiceHelper.getNextSyncTime(jobId);


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
