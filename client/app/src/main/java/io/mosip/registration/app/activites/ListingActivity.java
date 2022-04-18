package io.mosip.registration.app.activites;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.registration.app.R;
import io.mosip.registration.app.jobservice.PacketStatusSyncJobService;
import io.mosip.registration.app.viewmodel.ListingViewModel;
import io.mosip.registration.app.viewmodel.ViewModelFactory;
import io.mosip.registration.clientmanager.entity.Registration;
import io.mosip.registration.clientmanager.spi.PacketService;

import javax.inject.Inject;
import java.util.List;

public class ListingActivity  extends DaggerAppCompatActivity {

    private static final String TAG = ListingActivity.class.getSimpleName();

    @Inject
    PacketService packetService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing_activity);
        ListView listView = (ListView) findViewById(R.id.list);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);

        final Button syncPacketStatusButton = findViewById(R.id.listing_pkt_status_sync);
        final Button jobScheduleButton = findViewById(R.id.listing_job_schedule);
        final Button jobCancelButton = findViewById(R.id.listing_job_cancel);

        syncPacketStatusButton.setOnClickListener(v -> {
            Toast.makeText(ListingActivity.this,"Starting packet status sync", Toast.LENGTH_SHORT).show();
            try {
                packetService.syncAllPacketStatus();
            } catch (Exception e) {
                Log.e(TAG, "packet status sync failed", e);
                Toast.makeText(ListingActivity.this, "packet status sync failed", Toast.LENGTH_SHORT).show();
            }
        });

        jobScheduleButton.setOnClickListener(v -> {
            Toast.makeText(ListingActivity.this,"Setting up job", Toast.LENGTH_SHORT).show();
            try {
                int resultCode = scheduleJob();

                if (resultCode == JobScheduler.RESULT_SUCCESS) {
                    Log.d(TAG, "Job scheduled");
                    Toast.makeText(ListingActivity.this, "Job scheduled", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Job scheduling failed");
                    Toast.makeText(ListingActivity.this, "Job scheduling failed", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Job scheduling failed", e);
                Toast.makeText(ListingActivity.this, "Job scheduling failed", Toast.LENGTH_SHORT).show();
            }
        });

        jobCancelButton.setOnClickListener(v -> {
            Toast.makeText(ListingActivity.this,"Cancelling Job", Toast.LENGTH_SHORT).show();
            try {
                cancelJob();
                Log.d(TAG, "Job cancelled");
                Toast.makeText(ListingActivity.this, "Job cancelled", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, "Cancelling Job failed", e);
                Toast.makeText(ListingActivity.this, "Cancelling Job failed", Toast.LENGTH_SHORT).show();
            }
        });

        //to display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Registrations");
        getSupportActionBar().setSubtitle("Note : Packets are auto approved");

        ViewModelFactory viewModelFactory = new ViewModelFactory(new ListingViewModel(packetService));
        ListingViewModel model = new ViewModelProvider(this, viewModelFactory).get(ListingViewModel.class);
        model.getRegistrationList().observe(this, list -> {
            // update UI
            ArrayAdapter<Registration> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text2, list);
            // Assign adapter to ListView
            listView.setAdapter(new CustomListViewAdapter(this, R.layout.custom_list_view, list));
            progressBar.setVisibility(View.GONE);
        });
    }

    private class CustomListViewAdapter extends ArrayAdapter<Registration> {

        private List<Registration> mObjects;
        private int layout;

        public CustomListViewAdapter(@NonNull Context context, int resource, @NonNull List<Registration> objects) {
            super(context, resource, objects);
            mObjects = objects;
            layout = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder mainViewholder = null;
            if(convertView == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                convertView = layoutInflater.inflate(layout, parent, false);

                ViewHolder viewHolder = new ViewHolder();
                viewHolder.title = convertView.findViewById(R.id.list_item_text);
                viewHolder.syncButton = convertView.findViewById(R.id.list_item_btn1);
                viewHolder.uploadButton = convertView.findViewById(R.id.list_item_btn2);
                convertView.setTag(viewHolder);
            }

            Registration registration = this.mObjects.get(position);

            mainViewholder = (ViewHolder) convertView.getTag();
            mainViewholder.syncButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Starting packet sync", Toast.LENGTH_SHORT).show();
                    try {
                        packetService.syncRegistration(registration.getPacketId());
                        Toast.makeText(getContext(), "Packet sync successful", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Packet sync failed", e);
                        Toast.makeText(getContext(), "Packet sync failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            mainViewholder.uploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "Starting packet upload", Toast.LENGTH_SHORT).show();
                    try {
                        packetService.uploadRegistration(registration.getPacketId());
                        Toast.makeText(getContext(), "Packet upload successful", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Packet upload failed", e);
                        Toast.makeText(getContext(), "Packet upload failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            mainViewholder.title.setText(getItem(position).toString());
            return convertView;
        }
    }

    public class ViewHolder {
        TextView title;
        Button syncButton;
        Button uploadButton;
    }

    private int scheduleJob() {
        ComponentName componentName = new ComponentName(this, PacketStatusSyncJobService.class);
        JobInfo info = new JobInfo.Builder(123, componentName)
                //.setRequiresCharging(false)
                //.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true)
                .setPeriodic(15 * 60 * 1000)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        return scheduler.schedule(info);
    }

    private void cancelJob() {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
    }
}
