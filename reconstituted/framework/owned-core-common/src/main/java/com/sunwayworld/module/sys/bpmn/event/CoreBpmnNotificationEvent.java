/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.event;

import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import java.util.List;
import org.springframework.context.ApplicationEvent;

public class CoreBpmnNotificationEvent
extends ApplicationEvent {
    private static final long serialVersionUID = -595507526485393875L;
    private ProcessStatus processStatus;
    private String name;
    private String desc;
    private String fromTaskName;
    private String toTaskName;
    private String lastAuditorId;
    private String lastComment;
    private List<String> notifiedUserIdList;

    public CoreBpmnNotificationEvent(Object source, ProcessStatus processStatus, String name, String desc, String fromTaskName, String toTaskName, String lastAuditorId, String lastComment) {
        super(source);
        this.processStatus = processStatus;
        this.name = name;
        this.desc = desc;
        this.fromTaskName = fromTaskName;
        this.toTaskName = toTaskName;
        this.lastAuditorId = lastAuditorId;
        this.lastComment = lastComment;
    }

    public ProcessStatus getProcessStatus() {
        return this.processStatus;
    }

    public void setProcessStatus(ProcessStatus processStatus) {
        this.processStatus = processStatus;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getFromTaskName() {
        return this.fromTaskName;
    }

    public void setFromTaskName(String fromTaskName) {
        this.fromTaskName = fromTaskName;
    }

    public String getToTaskName() {
        return this.toTaskName;
    }

    public void setToTaskName(String toTaskName) {
        this.toTaskName = toTaskName;
    }

    public String getLastAuditorId() {
        return this.lastAuditorId;
    }

    public void setLastAuditorId(String lastAuditorId) {
        this.lastAuditorId = lastAuditorId;
    }

    public String getLastComment() {
        return this.lastComment;
    }

    public void setLastComment(String lastComment) {
        this.lastComment = lastComment;
    }

    public List<String> getNotifiedUserIdList() {
        return this.notifiedUserIdList;
    }

    public void setNotifiedUserIdList(List<String> notifiedUserIdList) {
        this.notifiedUserIdList = notifiedUserIdList;
    }
}
