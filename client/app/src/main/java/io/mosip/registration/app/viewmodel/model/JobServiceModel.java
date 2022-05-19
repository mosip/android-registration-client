package io.mosip.registration.app.viewmodel.model;


import java.util.Date;

public class JobServiceModel {

    private int id;
    private String name;
    private String apiName;
    private Boolean isActive;
    private Boolean isImplemented;
    private Boolean isEnabled;
    private String lastSyncTime;
    private String nextSyncTime;

    public JobServiceModel(int id, String name, String apiName, Boolean isActive, Boolean isImplemented, Boolean isEnabled, String lastSyncTime, String nextSyncTime) {
        this.id = id;
        this.name = name;
        this.apiName = apiName;
        this.isActive = isActive;
        this.isImplemented = isImplemented;
        this.isEnabled = isEnabled;
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

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getImplemented() {
        return isImplemented;
    }

    public void setImplemented(Boolean implemented) {
        isImplemented = implemented;
    }

    public Boolean getEnabled() {
        return isEnabled;
    }

    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
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
