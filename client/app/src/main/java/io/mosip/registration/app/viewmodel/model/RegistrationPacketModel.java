package io.mosip.registration.app.viewmodel.model;

public class RegistrationPacketModel {
    private String packetId;
    private String packetStatus;
    private String packetCreatedDate;

    public RegistrationPacketModel(String packetId, String packetStatus, String packetCreatedDate) {
        this.packetId = packetId;
        this.packetStatus = packetStatus;
        this.packetCreatedDate = packetCreatedDate;
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

    @Override
    public String toString() {
        return packetId + "\n" + packetStatus;
    }
}
