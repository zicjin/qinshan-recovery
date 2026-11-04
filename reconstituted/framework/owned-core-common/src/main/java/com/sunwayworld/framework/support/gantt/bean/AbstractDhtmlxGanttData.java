/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.gantt.bean;

import com.alibaba.fastjson.annotation.JSONField;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public abstract class AbstractDhtmlxGanttData {
    private String id;
    private String text;
    @JSONField(format="dd-MM-yyyy HH:mm")
    @DateTimeFormat(pattern="dd-MM-yyyy HH:mm")
    private LocalDateTime start_date;
    private Integer duration;
    private String parent = "0";
    private Boolean open = Boolean.TRUE;
    private Double progress = 0.0;
    private String type;
    private String render;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getStart_date() {
        return this.start_date;
    }

    public void setStart_date(LocalDateTime start_date) {
        this.start_date = start_date;
    }

    public Integer getDuration() {
        return this.duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getParent() {
        return this.parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public Boolean getOpen() {
        return this.open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public Double getProgress() {
        return this.progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRender() {
        return this.render;
    }

    public void setRender(String render) {
        this.render = render;
    }
}
