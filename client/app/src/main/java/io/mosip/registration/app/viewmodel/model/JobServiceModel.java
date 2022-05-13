package io.mosip.registration.app.viewmodel.model;


public class JobServiceModel {

    private int id;
    private String name;
    private String apiName;
    private String parentSyncJobId;
    private String syncFreq;
    private String lockDuration;
    private String langCode;
    private Boolean isDeleted;
    private Boolean isActive;
    private Boolean isImplemented;
    private Boolean isEnabled;

    public JobServiceModel(int id, String name, String apiName, String parentSyncJobId, String syncFreq, String lockDuration, String langCode, Boolean isDeleted, Boolean isActive, Boolean isImplemented, Boolean isEnabled) {
        this.id = id;
        this.name = name;
        this.apiName = apiName;
        this.parentSyncJobId = parentSyncJobId;
        this.syncFreq = syncFreq;
        this.lockDuration = lockDuration;
        this.langCode = langCode;
        this.isDeleted = isDeleted;
        this.isActive = isActive;
        this.isImplemented = isImplemented;
        this.isEnabled = isEnabled;
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

    public String getParentSyncJobId() {
        return parentSyncJobId;
    }

    public void setParentSyncJobId(String parentSyncJobId) {
        this.parentSyncJobId = parentSyncJobId;
    }

    public String getSyncFreq() {
        return syncFreq;
    }

    public void setSyncFreq(String syncFreq) {
        this.syncFreq = syncFreq;
    }

    public String getLockDuration() {
        return lockDuration;
    }

    public void setLockDuration(String lockDuration) {
        this.lockDuration = lockDuration;
    }

    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
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
}
