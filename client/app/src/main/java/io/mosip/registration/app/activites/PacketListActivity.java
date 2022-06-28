package io.mosip.registration.app.activites;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.view.ActionMode;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import io.mosip.registration.clientmanager.repository.RegistrationRepository;
import io.mosip.registration.clientmanager.spi.AsyncPacketTaskCallBack;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.PacketService;
import io.mosip.registration.clientmanager.util.DateUtil;

public class PacketListActivity extends DaggerAppCompatActivity {

    private static final String TAG = PacketListActivity.class.getSimpleName();

    @Inject
    PacketService packetService;

    @Inject
    DateUtil dateUtil;

    @Inject
    AuditManagerService auditManagerService;

    @Inject
    RegistrationRepository registrationRepository;

    RegistrationPacketViewModel registrationPacketViewModel2;

    PacketListActivityBinding bi;
    List<RegistrationPacketModel> list;
    RegistrationPacketListAdapter adapter;
    ActionMode actionMode;
    ActionCallback actionCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.packet_list_activity);

        //to display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.registrations_title);
        getSupportActionBar().setSubtitle(R.string.registrations_subtitle);
        init();

        auditManagerService.audit(AuditEvent.LOADED_REG_LISTING, Components.REG_PACKET_LIST);
    }

    private void init() {
        actionCallback = new ActionCallback();
        registrationPacketViewModel2 = new RegistrationPacketViewModel(packetService, dateUtil);
        list = registrationPacketViewModel2.getList().getValue();
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
        List<Integer> selectedItemPositions = adapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            syncAndUploadPacket(i);
        }
    }

    private void syncAndUploadPacket(int position) {
        String packetId = adapter.getItem(position).getPacketId();
        try {
            auditManagerService.audit(AuditEvent.SYNC_PACKET, Components.REG_PACKET_LIST.getName(), packetId, AuditReferenceIdTypes.PACKET_ID.name());
            packetService.syncRegistration(packetId, new AsyncPacketTaskCallBack() {
                @Override
                public void inProgress(String RID) {
                    //TODO upload Progress Bar
                }

                @Override
                public void onComplete(String RID, int status) {
                    list.get(position).setPacketStatus(registrationRepository.getRegistration(RID).getClientStatus());
                    adapter.notifyDataSetChanged();
                    //TODO upload Progress Bar

                    if (status == RegistrationConstants.PACKET_SYNC_STATUS_SUCCESS) {
                        try {
                            packetService.uploadRegistration(RID, new AsyncPacketTaskCallBack() {
                                @Override
                                public void inProgress(String RID) {
                                    //TODO upload Progress Bar
                                }

                                @Override
                                public void onComplete(String RID, int status) {
                                    list.get(position).setPacketStatus(registrationRepository.getRegistration(RID).getServerStatus());
                                    adapter.notifyDataSetChanged();
                                    //TODO upload Progress Bar
                                }
                            });
                        } catch (Exception e) {
                            Log.e(TAG, "Packet upload failed", e);
                        }
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Packet sync failed", e);
            Toast.makeText(getApplicationContext(), R.string.packet_sync_fail, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}

