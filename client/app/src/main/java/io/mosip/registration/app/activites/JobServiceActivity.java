package io.mosip.registration.app.activites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.List;

import javax.inject.Inject;

import io.mosip.registration.app.R;
import io.mosip.registration.app.viewmodel.JobServiceViewModel;
import io.mosip.registration.app.viewmodel.ViewModelFactory;
import io.mosip.registration.app.viewmodel.model.JobServiceModel;
import io.mosip.registration.clientmanager.spi.PacketService;

public class JobServiceActivity extends AppCompatActivity {

    private static final String TAG = JobServiceActivity.class.getSimpleName();

    @Inject
    PacketService packetService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_service);
        ListView listView = (ListView) findViewById(R.id.jobList);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);

        //to display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Job Service");

        ViewModelFactory viewModelFactory = new ViewModelFactory(new JobServiceViewModel());
        JobServiceViewModel model = new ViewModelProvider(this, viewModelFactory).get(JobServiceViewModel.class);
        model.getList().observe(this, list -> {
            // update UI
            ArrayAdapter<JobServiceModel> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text2, list);
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
            if(convertView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                convertView = layoutInflater.inflate(layout, parent, false);

                ViewHolder viewHolder = new ViewHolder();
                viewHolder.jobName = convertView.findViewById(R.id.jobName);
                viewHolder.jobDesc = convertView.findViewById(R.id.jobDesc);
                viewHolder.triggerJobButton = convertView.findViewById(R.id.triggerJob);
                viewHolder.toggleActiveButton = convertView.findViewById(R.id.toggleActive);
                convertView.setTag(viewHolder);
            }

            JobServiceModel jobServiceModel = this.mObjects.get(position);

            mainViewHolder = (ViewHolder) convertView.getTag();
            mainViewHolder.triggerJobButton.setOnClickListener(v -> {
                Toast.makeText(JobServiceActivity.this,"Starting packet status sync", Toast.LENGTH_SHORT).show();
                try {
                    //TODO trigger respective job
                } catch (Exception e) {
                    Log.e(TAG, "packet status sync failed", e);
                    Toast.makeText(JobServiceActivity.this, "packet status sync failed", Toast.LENGTH_SHORT).show();
                }
            });
            mainViewHolder.toggleActiveButton.setOnClickListener(v -> {
                Toast.makeText(JobServiceActivity.this,"Setting up job", Toast.LENGTH_SHORT).show();
                if(v.isActivated()){
                    try {
                        int resultCode = scheduleJob(jobServiceModel.getJobId(), jobServiceModel.getJobServiceClass());

                        if (resultCode == JobScheduler.RESULT_SUCCESS) {
                            Log.d(TAG, "Job scheduled");
                            Toast.makeText(JobServiceActivity.this, "Job scheduled", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "Job scheduling failed");
                            Toast.makeText(JobServiceActivity.this, "Job scheduling failed", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Job scheduling failed", e);
                        Toast.makeText(JobServiceActivity.this, "Job scheduling failed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(JobServiceActivity.this,"Cancelling Job", Toast.LENGTH_SHORT).show();
                    try {
                        cancelJob(jobServiceModel.getJobId());
                        Log.d(TAG, "Job cancelled");
                        Toast.makeText(JobServiceActivity.this, "Job cancelled", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Cancelling Job failed", e);
                        Toast.makeText(JobServiceActivity.this, "Cancelling Job failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            mainViewHolder.jobName.setText(jobServiceModel.getJobName());
            mainViewHolder.jobDesc.setText(jobServiceModel.getJobDescription());
            mainViewHolder.toggleActiveButton.setChecked(jobServiceModel.isActive());
            return convertView;
        }
    }

    public class ViewHolder {
        TextView jobName;
        TextView jobDesc;
        Button triggerJobButton;
        ToggleButton toggleActiveButton;
    }

    private int scheduleJob(int jobId, @NonNull Class<?> cls) {
        ComponentName componentName = new ComponentName(this, cls);
        JobInfo info = new JobInfo.Builder(jobId, componentName)
                .setRequiresCharging(false)
                //.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true)
                .setPeriodic(15 * 60 * 1000)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        return scheduler.schedule(info);
    }

    private void cancelJob(int jobId) {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(jobId);
    }
}