package io.mosip.registration.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import io.mosip.registration.app.jobservice.PacketStatusSyncJobService;
import io.mosip.registration.app.viewmodel.model.JobServiceModel;

public class JobServiceViewModel extends ViewModel implements IListingViewModel{

    private static final String TAG = JobServiceViewModel.class.getSimpleName();

    public JobServiceViewModel() {
    }

    private MutableLiveData<List<JobServiceModel>> jobServiceList;

    @Override
    public LiveData<List<JobServiceModel>> getList() {
        if (jobServiceList == null) {
            jobServiceList = new MutableLiveData<>();
            loadRegistrations();
        }
        return jobServiceList;
    }

    private void loadRegistrations() {
        List<JobServiceModel> jobs = new ArrayList<>();
        jobs.add(new JobServiceModel(1, "PacketStatusSync","Auto Sync Packet Status", true, PacketStatusSyncJobService.class));
        jobServiceList.setValue(jobs);
    }
}
