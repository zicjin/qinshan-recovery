package com.sunwayworld.emc.module.mdm.equipt.equiptacqfile.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;

/**
 * 仪器采集文件(T_EMC_EQUIPT_ACQ_FILE)
 *
 */
@Table("T_EMC_EQUIPT_ACQ_FILE")
public class EmcEquiptAcqFileBean extends AbstractPersistable<Long> {

    @Transient
    private static final long serialVersionUID = -6082060927083176873L;

    @Id
    private Long id; // 主键

    private Long equiptId; // 仪器ID

    private String sourceFileName; // 源文件名称(客户提供）

    private String targetFileName; // 目录文件名称(程序处理）

    private String content; // 文件文本内容

    private String result; // 解析文件的字符串结果

    private String status; // 处理状态0未处理，1已处理

    private String runId; // 批号

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime; // 制单时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEquiptId() {
        return equiptId;
    }

    public void setEquiptId(Long equiptId) {
        this.equiptId = equiptId;
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    public String getTargetFileName() {
        return targetFileName;
    }

    public void setTargetFileName(String targetFileName) {
        this.targetFileName = targetFileName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
