package io.mosip.registration.clientmanager.spi;

public interface AsyncPacketTaskCallBack {
    void inProgress(String RID);
    void onComplete(String RID, int status);
}
