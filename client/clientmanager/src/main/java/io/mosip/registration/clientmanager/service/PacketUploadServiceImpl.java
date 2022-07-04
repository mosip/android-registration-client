package io.mosip.registration.clientmanager.service;

import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.constant.PacketTaskStatus;
import io.mosip.registration.clientmanager.spi.AsyncPacketTaskCallBack;
import io.mosip.registration.clientmanager.spi.JobTransactionService;
import io.mosip.registration.clientmanager.spi.PacketService;
import io.mosip.registration.clientmanager.spi.PacketUploadProgressCallBack;
import io.mosip.registration.clientmanager.spi.PacketUploadService;

/**
 * @author Anshul vanawat
 * @since 1.0.0
 */

@Singleton
public class PacketUploadServiceImpl implements PacketUploadService {

    private static final String TAG = JobTransactionService.class.getSimpleName();

    private Operation ongoingOperation;
    private Queue<Operation> queue = new LinkedList<>();

    @Inject
    PacketService packetService;

    @Inject
    public PacketUploadServiceImpl(PacketService packetService) {
        this.packetService = packetService;
    }

    @Override
    public void syncAndUploadPacket(String packetId, PacketUploadProgressCallBack uploadProgressCallBack) {
        Runnable onFinished = this::operationFinished;
        if (ongoingOperation != null) {
            queue.add(new Operation(packetId, uploadProgressCallBack, onFinished));
        } else {
            ongoingOperation = new Operation(packetId, uploadProgressCallBack,onFinished);
            ongoingOperation.execute();
        }
    }

    private void operationFinished() {
        ongoingOperation = null;
        Operation nextOperation = queue.poll();
        if (nextOperation != null)
            nextOperation.execute();
    }

    private class Operation {
        private final String packetId;
        private final PacketUploadProgressCallBack uploadProgressCallBack;
        private Runnable onFinished;

        public Operation(String packetId, PacketUploadProgressCallBack uploadProgressCallBack, Runnable onFinished) {
            this.packetId = packetId;
            this.uploadProgressCallBack = uploadProgressCallBack;
            this.onFinished = onFinished;
        }

        void execute() {
            try {
                packetService.syncRegistration(packetId, new AsyncPacketTaskCallBack() {
                    @Override
                    public void inProgress(String RID) {
                        uploadProgressCallBack.progress(RID, PacketTaskStatus.SYNC_STARTED);
                    }

                    @Override
                    public void onComplete(String RID, PacketTaskStatus status) {
                        uploadProgressCallBack.progress(RID, status);
                        if (status != PacketTaskStatus.SYNC_FAILED) {
                            try {
                                packetService.uploadRegistration(RID, new AsyncPacketTaskCallBack() {
                                    @Override
                                    public void inProgress(String RID) {
                                        uploadProgressCallBack.progress(RID, PacketTaskStatus.UPLOAD_STARTED);
                                    }

                                    @Override
                                    public void onComplete(String RID, PacketTaskStatus status) {
                                        uploadProgressCallBack.progress(RID, status);
                                        onFinished.run();
                                    }
                                });
                            } catch (Exception e) {
                                Log.e(TAG, "Packet upload failed", e);
                                uploadProgressCallBack.progress(RID, PacketTaskStatus.UPLOAD_FAILED);
                                onFinished.run();
                            }
                        } else {
                            uploadProgressCallBack.progress(RID, status);
                            onFinished.run();
                        }
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Packet sync failed", e);
                uploadProgressCallBack.progress(packetId, PacketTaskStatus.SYNC_FAILED);
                onFinished.run();
            }
        }
    }
}
