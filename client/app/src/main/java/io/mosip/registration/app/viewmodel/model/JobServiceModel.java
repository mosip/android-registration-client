package io.mosip.registration.app.viewmodel.model;

public class JobServiceModel {

    public int getJobId() {
        return jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public boolean isActive() {
        return isActive;
    }

    public Class<?> getJobServiceClass() {
        return jobServiceClass;
    }

    private int jobId;
    private String jobName;
    private String jobDescription;
    private boolean isActive;
    private Class<?> jobServiceClass;

    public JobServiceModel(int jobId, String jobName, String jobDescription, boolean isActive, Class<?> jobServiceClass){
        this.jobId = jobId;
        this.jobName = jobName;
        this.jobDescription = jobDescription;
        this.isActive = isActive;
        this.jobServiceClass = jobServiceClass;
    }

}
