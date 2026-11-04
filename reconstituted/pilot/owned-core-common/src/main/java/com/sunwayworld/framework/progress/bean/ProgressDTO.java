/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.progress.bean;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ProgressDTO
implements Serializable {
    private static final long serialVersionUID = 6221127530935831448L;
    private String id;
    private String taskName;
    private LocalDateTime startTime;
    private Long total;
    private Long success;
    private Long failure;
    private String failureMessage;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Long getTotal() {
        return this.total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getSuccess() {
        return this.success;
    }

    public void setSuccess(Long success) {
        this.success = success;
    }

    public Long getFailure() {
        return this.failure;
    }

    public void setFailure(Long failure) {
        this.failure = failure;
    }

    public String getFailureMessage() {
        return this.failureMessage;
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }
}
