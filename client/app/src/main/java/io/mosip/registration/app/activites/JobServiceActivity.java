package io.mosip.registration.app.activites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import android.app.job.JobScheduler;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.registration.app.R;
import io.mosip.registration.app.util.JobServiceHelper;
import io.mosip.registration.app.viewmodel.JobServiceViewModel;
import io.mosip.registration.app.viewmodel.ViewModelFactory;
import io.mosip.registration.app.viewmodel.model.JobServiceModel;
import io.mosip.registration.clientmanager.spi.JobTransactionService;
import io.mosip.registration.clientmanager.spi.PacketService;

public class JobServiceActivity extends DaggerAppCompatActivity {

    private static final String TAG = JobServiceActivity.class.getSimpleName();
    @Inject
    PacketService packetService;
    @Inject
    JobTransactionService jobTransactionService;

    JobServiceHelper jobServiceHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_service);
        ListView listView = findViewById(R.id.jobList);
        ProgressBar progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);

        jobServiceHelper = new JobServiceHelper(this, (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE), packetService, jobTransactionService);

        //to display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.scheduled_jobs);

        ViewModelFactory viewModelFactory = new ViewModelFactory(new JobServiceViewModel(jobServiceHelper));
        JobServiceViewModel model = new ViewModelProvider(this, viewModelFactory).get(JobServiceViewModel.class);
        model.getList().observe(this, list -> {
            // Assign adapter to ListView
            listView.setAdapter(new CustomListViewAdapter(this, R.layout.custom_list_view_job, list));
            progressBar.setVisibility(View.GONE);
        });
    }

    private class CustomListViewAdapter extends ArrayAdapter<JobServiceModel> {

        private List<JobServiceModel> mObjects;
        private int layout;

        public CustomListViewAdapter(@NonNull Context context, int resource, @NonNull List<JobServiceModel> objects) {
            super(context, resource, objects);
            mObjects = objects;
            layout = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            ViewHolder mainViewHolder = null;
            if (convertView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                convertView = layoutInflater.inflate(layout, parent, false);

                ViewHolder viewHolder = new ViewHolder();
                viewHolder.jobName = convertView.findViewById(R.id.jobName);
                viewHolder.jobLastSyncTime = convertView.findViewById(R.id.last_sync_time_text);
                viewHolder.jobNextSyncTime = convertView.findViewById(R.id.next_sync_time_text);
                viewHolder.triggerJobButton = convertView.findViewById(R.id.triggerJob);
                viewHolder.toggleActiveButton = convertView.findViewById(R.id.toggleActive);

                convertView.setTag(viewHolder);
            }
            JobServiceModel jobServiceModel = this.mObjects.get(position);

            mainViewHolder = (ViewHolder) convertView.getTag();

            mainViewHolder.jobName.setText(jobServiceModel.getName());
            mainViewHolder.jobLastSyncTime.setText(getString(R.string.last_sync_time, jobServiceModel.getLastSyncTime()));
            mainViewHolder.jobNextSyncTime.setText(getString(R.string.next_sync_time, jobServiceModel.getNextSyncTime()));
            mainViewHolder.toggleActiveButton.setEnabled(jobServiceModel.getActive() && jobServiceModel.getImplemented());
            mainViewHolder.triggerJobButton.setEnabled(jobServiceModel.getActive() && jobServiceModel.getImplemented());

            mainViewHolder.toggleActiveButton.setOnCheckedChangeListener(null);
            mainViewHolder.toggleActiveButton.setChecked(jobServiceModel.getEnabled());

            mainViewHolder.triggerJobButton.setOnClickListener(v -> {
                Toast.makeText(JobServiceActivity.this, getString(R.string.starting_job, jobServiceModel.getName()), Toast.LENGTH_SHORT).show();
                try {
                    boolean triggered = jobServiceHelper.triggerJobService(jobServiceModel.getId(), jobServiceModel.getApiName());
                    if (!triggered)
                        Toast.makeText(JobServiceActivity.this, getString(R.string.job_triggering_failed, jobServiceModel.getName()), Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Log.e(TAG, getString(R.string.starting_job, jobServiceModel.getName()), e);
                    Toast.makeText(JobServiceActivity.this, getString(R.string.job_triggering_failed, jobServiceModel.getName()), Toast.LENGTH_SHORT).show();
                }
            });

            mainViewHolder.toggleActiveButton.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                Toast.makeText(JobServiceActivity.this, getString(R.string.setting_up_job), Toast.LENGTH_SHORT).show();
                if (isChecked) {
                    try {
                        int resultCode = jobServiceHelper.scheduleJob(jobServiceModel.getId(), jobServiceModel.getApiName(), jobServiceModel.getSyncFreq());
                        if (resultCode == JobScheduler.RESULT_SUCCESS) {
                            Log.d(TAG, getString(R.string.job_scheduled));
                            jobServiceModel.setEnabled(true);
                            jobServiceModel.setLastSyncTime(jobServiceHelper.getLastSyncTime(jobServiceModel.getId()));
                            Toast.makeText(JobServiceActivity.this, getString(R.string.job_scheduled), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, getString(R.string.job_scheduling_failed));
                            Toast.makeText(JobServiceActivity.this, getString(R.string.job_scheduling_failed), Toast.LENGTH_SHORT).show();
                            jobServiceModel.setEnabled(false);
                            compoundButton.setChecked(false);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, getString(R.string.job_scheduling_failed), e);
                        Toast.makeText(JobServiceActivity.this, getString(R.string.job_scheduling_failed), Toast.LENGTH_SHORT).show();
                        compoundButton.setChecked(false);
                    }
                } else {
                    Toast.makeText(JobServiceActivity.this, getString(R.string.cancelling_job), Toast.LENGTH_SHORT).show();
                    try {
                        jobServiceHelper.cancelJob(jobServiceModel.getId());
                        jobServiceModel.setEnabled(false);
                        Log.d(TAG, getString(R.string.job_cancelled));
                        Toast.makeText(JobServiceActivity.this, getString(R.string.job_cancelled), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, getString(R.string.cancelling_job_failed), e);
                        Toast.makeText(JobServiceActivity.this, getString(R.string.cancelling_job_failed), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return convertView;
        }
    }

    public class ViewHolder {
        TextView jobName;
        TextView jobLastSyncTime;
        TextView jobNextSyncTime;
        Button triggerJobButton;
        Switch toggleActiveButton;
    }
}