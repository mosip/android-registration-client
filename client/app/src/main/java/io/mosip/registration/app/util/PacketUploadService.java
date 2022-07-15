package io.mosip.registration.app.util;

import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;

import io.mosip.registration.clientmanager.constant.PacketTaskStatus;
import io.mosip.registration.clientmanager.spi.AsyncPacketTaskCallBack;
import io.mosip.registration.clientmanager.spi.PacketService;

/**
 * @author Anshul vanawat
 * @since 1.0.0
 */

public class PacketUploadService {

    private static final String TAG = PacketUploadService.class.getSimpleName();

    private Operation ongoingOperation;
    private final Queue<Operation> queue = new LinkedList<>();

    PacketService packetService;

    public PacketUploadService(PacketService packetService) {
        this.packetService = packetService;
    }

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
        private final Runnable onFinished;

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
