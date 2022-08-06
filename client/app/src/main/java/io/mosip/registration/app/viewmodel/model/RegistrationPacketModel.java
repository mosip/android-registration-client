package io.mosip.registration.app.viewmodel.model;

import android.view.View;

public class RegistrationPacketModel {
    private String packetId;
    private String packetStatus;
    private String packetCreatedDate;
    private int progress;
    private Boolean progressBarVisible;

    public RegistrationPacketModel(String packetId, String packetStatus, String packetCreatedDate) {
        this.packetId = packetId;
        this.packetStatus = packetStatus;
        this.packetCreatedDate = packetCreatedDate;
        this.progress = 0;
        this.progressBarVisible = false;
    }

    public String getPacketId() {
        return packetId;
    }

    public void setPacketId(String packetId) {
        this.packetId = packetId;
    }

    public String getPacketStatus() {
        return packetStatus;
    }

    public void setPacketStatus(String packetStatus) {
        this.packetStatus = packetStatus;
    }

    public String getPacketCreatedDate() {
        return packetCreatedDate;
    }

    public void setPacketCreatedDate(String packetCreatedDate) {
        this.packetCreatedDate = packetCreatedDate;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean ProgressBarVisible() {
        return progressBarVisible;
    }

    public void ProgressBarVisible(boolean progressBarVisible) {
        this.progressBarVisible = progressBarVisible;
    }

    @Override
    public String toString() {
        return packetId + "\n" + packetStatus;
    }
}
