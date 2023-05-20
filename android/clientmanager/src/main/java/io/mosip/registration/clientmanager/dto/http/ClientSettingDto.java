package io.mosip.registration.clientmanager.dto.http;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ClientSettingDto {

    @SerializedName("lastSyncTime")
    @Expose
    private String lastSyncTime;
    @SerializedName("dataToSync")
    @Expose
    private List<MasterData> dataToSync = null;

    public String getLastSyncTime() {
        return lastSyncTime;
    }

    public void setLastSyncTime(String lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }

    public List<MasterData> getDataToSync() {
        return dataToSync;
    }

    public void setDataToSync(List<MasterData> dataToSync) {
        this.dataToSync = dataToSync;
    }
}

