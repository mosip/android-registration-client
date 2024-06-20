package io.mosip.registration_client.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.ClientManagerConstant;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.constant.PacketClientStatus;
import io.mosip.registration.clientmanager.constant.PacketTaskStatus;
import io.mosip.registration.clientmanager.dao.GlobalParamDao;
import io.mosip.registration.clientmanager.entity.GlobalParam;
import io.mosip.registration.clientmanager.entity.Registration;
import io.mosip.registration.clientmanager.entity.SyncJobDef;
import io.mosip.registration.clientmanager.repository.SyncJobDefRepository;
import io.mosip.registration.clientmanager.spi.AsyncPacketTaskCallBack;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.PacketService;
import io.mosip.registration_client.MainActivity;
import io.mosip.registration_client.R;

public class BatchJob {

    PacketService packetService;
    AuditManagerService auditManagerService;
    GlobalParamDao globalParamDao;
    SyncJobDefRepository syncJobDefRepository;
    Activity activity;

    @Inject
    public BatchJob(PacketService packetService, AuditManagerService auditManagerService,
                    GlobalParamDao globalParamDao, SyncJobDefRepository syncJobDefRepository) {
        this.packetService = packetService;
        this.auditManagerService = auditManagerService;
        this.globalParamDao = globalParamDao;
        this.syncJobDefRepository = syncJobDefRepository;
    }

    public void setCallbackActivity(MainActivity mainActivity){
        this.activity=mainActivity;
    }

    public void syncRegistrationPackets(Context context) {
        if(NetworkUtils.isNetworkConnected(context)){
            Log.d(getClass().getSimpleName(), "Sync Packets in Batch Job");
            Integer batchSize = getBatchSize();
            List<Registration> registrationList = packetService.getRegistrationsByStatus(PacketClientStatus.APPROVED.name(), batchSize);
            final Integer[] remainingPack = {registrationList.size(), 0};

            Integer packetSize = registrationList.size();
            CustomToast newToast = new CustomToast(activity);

            if(registrationList.isEmpty()){
                uploadRegistrationPackets(context);
                return;
            }
            for (Registration value : registrationList) {
                try {
                    Log.d(getClass().getSimpleName(), "Syncing " + value.getPacketId());
                    auditManagerService.audit(AuditEvent.SYNC_PACKET, Components.REG_PACKET_LIST);

                    Integer remaining = packetSize - remainingPack[0];
                    newToast.setText(String.format("Sync Packet Status : %s/%s Processed", remaining.toString(), packetSize.toString()));
                    newToast.showToast();

                    packetService.syncRegistration(value.getPacketId(), new AsyncPacketTaskCallBack() {
                        @Override
                        public void inProgress(String RID) {
                            //Do nothing
                            newToast.showToast();
                        }

                        @Override
                        public void onComplete(String RID, PacketTaskStatus status) {
                            if(status.equals(PacketTaskStatus.SYNC_COMPLETED)){
                                remainingPack[1] += 1;
                            }
                            remainingPack[0] -= 1;

                            Integer remaining = packetSize - remainingPack[0];
                            newToast.setText(String.format("Sync Packet Status : %s/%s Processed", remaining.toString(), packetSize.toString()));

                            if(remainingPack[0] == 0){
                                Integer failed = packetSize- remainingPack[1];
                                newToast.setIcon(R.drawable.done);
                                newToast.setText(String.format("Sync Packet Status: %s/%s Success, %s/%s failed", remainingPack[1].toString(), packetSize.toString(), failed.toString() ,packetSize.toString()));
                                newToast.showToast();

                                Log.d(getClass().getSimpleName(), "Last Packet"+RID);
                                uploadRegistrationPackets(context);
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), e.getMessage());
                }
            }
        }
    }

    public void uploadRegistrationPackets(Context context) {
        if(NetworkUtils.isNetworkConnected(context)){
            Log.d(getClass().getSimpleName(), "Upload Packets in Batch Job");
            Integer batchSize = getBatchSize();
            List<Registration>  registrationList = packetService.getRegistrationsByStatus(PacketClientStatus.SYNCED.name(), batchSize);

            Integer packetSize = registrationList.size();
            final Integer[] remainingPack = {packetSize, 0};
            CustomToast newToast = new CustomToast(activity);

            for (Registration value : registrationList) {
                try {
                    Log.d(getClass().getSimpleName(), "Uploading " + value.getPacketId());
                    auditManagerService.audit(AuditEvent.UPLOAD_PACKET, Components.REG_PACKET_LIST);

                    Integer remaining = packetSize - remainingPack[0];
                    newToast.setText(String.format("Upload Packet Status : %s/%s Processed", remaining.toString(), packetSize.toString()));
                    newToast.showToast();

                    packetService.uploadRegistration(value.getPacketId(), new AsyncPacketTaskCallBack() {
                        @Override
                        public void inProgress(String RID) {
                            //Do nothing
                            newToast.showToast();
                        }

                        @Override
                        public void onComplete(String RID, PacketTaskStatus status) {
                            if(status.equals(PacketTaskStatus.UPLOAD_COMPLETED)){
                                remainingPack[1] += 1;
                            }
                            remainingPack[0] -= 1;

                            Integer remaining = packetSize - remainingPack[0];
                            newToast.setText(String.format("Upload Packet Status : %s/%s Processed", remaining.toString(), packetSize.toString()));

                            if(remainingPack[0] == 0){
                                Integer failed = packetSize- remainingPack[1];
                                newToast.setIcon(R.drawable.done);
                                newToast.setText(String.format("Upload Packet Status : %s/%s Success, %s/%s failed", remainingPack[1].toString(), packetSize.toString(), failed.toString() ,packetSize.toString()));
                                newToast.showToast();
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), e.getMessage());
                }
            }
        }
    }

    public Integer getBatchSize(){
        // Default batch size is 4
        List<GlobalParam> globalParams = globalParamDao.getGlobalParams();
        for (GlobalParam value : globalParams) {
            if (Objects.equals(value.getId(), "mosip.registration.packet_upload_batch_size")) {
                return Integer.parseInt(value.getValue());
            }
        }
        return ClientManagerConstant.DEFAULT_BATCH_SIZE;
    }

    public long getIntervalMillis(String api){
        // Default everyday at Noon - 12pm
        String cronExp = ClientManagerConstant.DEFAULT_UPLOAD_CRON;
        List<SyncJobDef> syncJobs = syncJobDefRepository.getAllSyncJobDefList();
        for (SyncJobDef value : syncJobs) {
            if (Objects.equals(value.getApiName(), api)) {
                Log.d(getClass().getSimpleName(), api + " Cron Expression : " + String.valueOf(value.getSyncFreq()));
                cronExp = String.valueOf(value.getSyncFreq());
                break;
            }
        }
        long nextExecution = CronParserUtil.getNextExecutionTimeInMillis(cronExp);
        Log.d(getClass().getSimpleName(), " Next Execution : " + String.valueOf(nextExecution));
        return nextExecution;
    }
}
