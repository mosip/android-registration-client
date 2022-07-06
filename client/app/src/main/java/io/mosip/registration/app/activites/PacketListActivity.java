package io.mosip.registration.app.activites;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.view.ActionMode;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.registration.app.R;
import io.mosip.registration.app.databinding.PacketListActivityBinding;
import io.mosip.registration.app.util.PacketListActivityHelper;
import io.mosip.registration.app.viewmodel.RegistrationPacketListAdapter;
import io.mosip.registration.app.viewmodel.RegistrationPacketViewModel;
import io.mosip.registration.app.viewmodel.model.RegistrationPacketModel;
import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.AuditReferenceIdTypes;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.PacketService;
import io.mosip.registration.clientmanager.spi.PacketUploadService;
import io.mosip.registration.clientmanager.util.DateUtil;

public class PacketListActivity extends DaggerAppCompatActivity {

    private static final String TAG = PacketListActivity.class.getSimpleName();

    private static final int PROGRESS_RESET = 0;
    private static final int PROGRESS_SYNC_STARTED = 20;
    private static final int PROGRESS_SYNC_COMPLETED = 40;
    private static final int PROGRESS_UPLOAD_STARTED = 60;
    private static final int PROGRESS_UPLOAD_COMPLETED = 100;

    @Inject
    PacketService packetService;

    @Inject
    PacketUploadService packetUploadService;

    @Inject
    DateUtil dateUtil;

    @Inject
    AuditManagerService auditManagerService;

    RegistrationPacketViewModel registrationPacketViewModel;

    PacketListActivityBinding bi;
    List<RegistrationPacketModel> list;
    RegistrationPacketListAdapter adapter;
    ActionMode actionMode;
    ActionCallback actionCallback;
    SwipeRefreshLayout swipeRefreshLayout;
    Boolean uploadOverWifiOnly;
    ConnectivityManager connectivityManager;
    SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.packet_list_activity);
        connectivityManager = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this /* Activity context */);
        uploadOverWifiOnly = sharedPreferences.getBoolean("wifi_only", false);

        //to display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.registrations_title);
        getSupportActionBar().setSubtitle(R.string.registrations_subtitle);
        init();
        swipeRefreshLayout = findViewById(R.id.packet_list_swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                registrationPacketViewModel.refreshPacketStatus();
                swipeRefreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
            }
        });
        auditManagerService.audit(AuditEvent.LOADED_REG_LISTING, Components.REG_PACKET_LIST);
    }

    private void init() {
        actionCallback = new ActionCallback();
        registrationPacketViewModel = new RegistrationPacketViewModel(packetService, dateUtil);
        list = registrationPacketViewModel.getList();
        adapter = new RegistrationPacketListAdapter(this, list);

        bi.packetList.setLayoutManager(new LinearLayoutManager(this));
        bi.packetList.setHasFixedSize(true);
        bi.packetList.setAdapter(adapter);
        adapter.setItemClick(new RegistrationPacketListAdapter.OnItemClick() {
            @Override
            public void onItemClick(View view, RegistrationPacketModel registrationPacketModel, int position) {
                toggleActionBar(position);
            }

            @Override
            public void onLongPress(View view, RegistrationPacketModel registrationPacketModel, int position) {
                toggleActionBar(position);
            }

            @Override
            public void onItemBtnPress(View view, RegistrationPacketModel registrationPacketModel, int position) {
                syncAndUploadPacket(position);
            }
        });
    }

    /*
       toggling action bar that will change the color and option
     */
    private void toggleActionBar(int position) {

        if (actionMode == null) {
            actionMode = startSupportActionMode(actionCallback);
        }
        toggleSelection(position);
    }

    /*
       toggle selection of items and show the count of selected items on the action bar
     */
    private void toggleSelection(int position) {
        adapter.toggleSelection(position);
        int count = adapter.selectedItemCount();
        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private void syncAndUploadSelectedPacket() {
        if (!checkNetwork()) {
            return;
        }

        List<Integer> selectedItemPositions = adapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            syncAndUploadPacket(i);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void syncAndUploadPacket(int position) {
        if (!checkNetwork()) {
            return;
        }

        NetworkCapabilities nc = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        int upstreamBandwidthKbps = nc.getLinkUpstreamBandwidthKbps();

        int minUpstreamBandwidthKbps = GlobalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.PACKET_UPLOAD_MIN_UPSTREAM_BANDWIDTH_KBPS);

        if (upstreamBandwidthKbps < minUpstreamBandwidthKbps) {
            String msg = getApplicationContext().getString(R.string.packet_upload_min_bw, String.valueOf(minUpstreamBandwidthKbps), String.valueOf(upstreamBandwidthKbps));
            Log.e(TAG, msg);
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            return;
        }

        String packetId = adapter.getItem(position).getPacketId();

        try {
            auditManagerService.audit(AuditEvent.SYNC_AND_UPLOAD_PACKET, Components.REG_PACKET_LIST.getName(), packetId, AuditReferenceIdTypes.PACKET_ID.name());
            packetUploadService.syncAndUploadPacket(packetId, (RID, progress) -> {
                RegistrationPacketModel packetModel = list.get(position);

                packetModel.setPacketStatus(packetService.getPacketStatus(packetId));
                packetModel.ProgressBarVisible(true);

                switch (progress) {
                    case SYNC_STARTED:
                        packetModel.setProgress(PROGRESS_SYNC_STARTED);
                        break;
                    case SYNC_COMPLETED:
                    case SYNC_ALREADY_COMPLETED:
                        packetModel.setProgress(PROGRESS_SYNC_COMPLETED);
                        Toast.makeText(this, "Packet already synced", Toast.LENGTH_LONG).show();
                        break;
                    case UPLOAD_STARTED:
                        packetModel.setProgress(PROGRESS_UPLOAD_STARTED);
                        break;
                    case UPLOAD_COMPLETED:
                    case UPLOAD_ALREADY_COMPLETED:
                        packetModel.setProgress(PROGRESS_UPLOAD_COMPLETED);
                        packetModel.ProgressBarVisible(false);
                        Toast.makeText(this, "Packet already uploaded", Toast.LENGTH_LONG).show();
                        break;
                    case SYNC_FAILED:
                    case UPLOAD_FAILED:
                        packetModel.setProgress(PROGRESS_RESET);
                        packetModel.ProgressBarVisible(false);
                        break;
                }
                adapter.notifyDataSetChanged();
            });
        } catch (Exception ex) {
            Log.e(TAG, "syncAndUploadPacket: ", ex);
        }
    }

    private class ActionCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            PacketListActivityHelper.toggleStatusBarColor(PacketListActivity.this, R.color.blue);
            mode.getMenuInflater().inflate(R.menu.upload_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.upload:
                    syncAndUploadSelectedPacket();
                    mode.finish();
                    Toast.makeText(getApplicationContext(), R.string.packets_upload_scheduled, Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.select_all:
                    adapter.clearSelection();
                    for (int position = 0; position < adapter.getItemCount(); position++) {
                        toggleSelection(position);
                    }
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.clearSelection();
            actionMode = null;
            PacketListActivityHelper.toggleStatusBarColor(PacketListActivity.this, R.color.primaryColor);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.packet_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Intent i = new Intent(PacketListActivity.this, SettingsActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkNetwork() {
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        Network currentNetwork = connectivityManager.getActiveNetwork();
        NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(currentNetwork);

        if (info == null || !info.isConnected()) {
            Toast.makeText(getApplicationContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (uploadOverWifiOnly && !caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            Toast.makeText(getApplicationContext(), R.string.change_connection_settings, Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }
}

