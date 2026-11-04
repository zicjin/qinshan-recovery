/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.log;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import java.io.Serializable;
import java.time.LocalDateTime;

public class ScriptLogInfo
implements Serializable {
    private static final long serialVersionUID = 9001740360173732930L;
    private Long id;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime executionTime;
    private String type;
    private String threadName;
    private String log;
    private String status;
    private String user;

    public LocalDateTime getExecutionTime() {
        return this.executionTime;
    }

    public void setExecutionTime(LocalDateTime executionTime) {
        this.executionTime = executionTime;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getThreadName() {
        return this.threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getLog() {
        return this.log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public static ScriptLogInfo of(String type, String log) {
        ScriptLogInfo scriptLogInfo = new ScriptLogInfo();
        scriptLogInfo.setId(ApplicationContextHelper.getNextIdentity());
        scriptLogInfo.setExecutionTime(LocalDateTime.now());
        scriptLogInfo.setThreadName(Thread.currentThread().getName());
        scriptLogInfo.setLog(log);
        scriptLogInfo.setType(type);
        scriptLogInfo.setStatus("1");
        return scriptLogInfo;
    }

    public static ScriptLogInfo forceOffline(String type, String log) {
        ScriptLogInfo scriptLogInfo = ScriptLogInfo.of(type, log);
        scriptLogInfo.setStatus("0");
        return scriptLogInfo;
    }
}
