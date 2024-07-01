package io.mosip.registration.clientmanager.dto;

/**
 * The DTO Class PreRegistration.
 */
public class PreRegistrationDto {

    private String preRegId;
    private byte[] decryptedPacket;
    private String packetPath;
    private String symmetricKey;
    public String getPreRegId() {
        return preRegId;
    }
    public void setPreRegId(String preRegId) {
        this.preRegId = preRegId;
    }
    public byte[] getEncryptedPacket() {
        return decryptedPacket;
    }
    public void setEncryptedPacket(byte[] encryptedPacket) {
        this.decryptedPacket = encryptedPacket;
    }
    public String getPacketPath() {
        return packetPath;
    }
    public void setPacketPath(String packetPath) {
        this.packetPath = packetPath;
    }
    public String getSymmetricKey() {
        return symmetricKey;
    }
    public void setSymmetricKey(String symmetricKey) {
        this.symmetricKey = symmetricKey;
    }

}
