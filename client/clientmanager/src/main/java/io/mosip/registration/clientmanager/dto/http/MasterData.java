package io.mosip.registration.clientmanager.dto.http;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MasterData {

    @SerializedName("entityName")
    @Expose
    private String entityName;
    @SerializedName("entityType")
    @Expose
    private String entityType;
    @SerializedName("data")
    @Expose
    private String data;

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
