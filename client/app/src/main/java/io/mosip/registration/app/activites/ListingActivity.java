package io.mosip.registration.app.activites;

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
import io.mosip.registration.app.viewmodel.RegistrationPacketViewModel;
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

        //to display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Registrations");
        getSupportActionBar().setSubtitle("Note : Packets are auto approved");

        ViewModelFactory viewModelFactory = new ViewModelFactory(new RegistrationPacketViewModel(packetService));
        RegistrationPacketViewModel model = new ViewModelProvider(this, viewModelFactory).get(RegistrationPacketViewModel.class);
        model.getList().observe(this, list -> {
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
}

