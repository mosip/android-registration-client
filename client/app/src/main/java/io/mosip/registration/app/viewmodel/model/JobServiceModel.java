package io.mosip.registration.app.viewmodel.model;

public class JobServiceModel {

    private int id;
    private String name;
    private String apiName;
    private Boolean isActiveAndImplemented;
    private Boolean isScheduled;
    private String syncFreq;
    private String lastSyncTime;
    private String nextSyncTime;

    public JobServiceModel(int id, String name, String apiName, Boolean isActiveAndImplemented, Boolean isScheduled, String syncFreq, String lastSyncTime, String nextSyncTime) {
        this.id = id;
        this.name = name;
        this.apiName = apiName;
        this.isActiveAndImplemented = isActiveAndImplemented;
        this.isScheduled = isScheduled;
        this.syncFreq = syncFreq;
        this.lastSyncTime = lastSyncTime;
        this.nextSyncTime = nextSyncTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public Boolean getIsActiveAndImplemented() {
        return isActiveAndImplemented;
    }

    public void setIsActiveAndImplemented(Boolean active) {
        isActiveAndImplemented = active;
    }

    public Boolean getScheduled() {
        return isScheduled;
    }

    public void setScheduled(Boolean scheduled) {
        isScheduled = scheduled;
    }

    public String getSyncFreq() {
        return syncFreq;
    }

    public void setSyncFreq(String syncFreq) {
        this.syncFreq = syncFreq;
    }

    public String getLastSyncTime() {
        return lastSyncTime;
    }

    public void setLastSyncTime(String lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }

    public String getNextSyncTime() {
        return nextSyncTime;
    }

    public void setNextSyncTime(String nextSyncTime) {
        this.nextSyncTime = nextSyncTime;
    }
}
