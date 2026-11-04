/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.gantt.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.gantt.bean.AbstractDhtmlxGanttData;
import com.sunwayworld.framework.support.gantt.bean.AbstractDhtmlxGanttLink;
import java.io.Serializable;
import java.util.List;

public class CoreDhtmlxGanttDTO
implements Serializable {
    @Transient
    private static final long serialVersionUID = 5818260847537353225L;
    private List<? extends AbstractDhtmlxGanttData> data;
    private List<? extends AbstractDhtmlxGanttLink> links;

    public CoreDhtmlxGanttDTO(List<? extends AbstractDhtmlxGanttData> data, List<? extends AbstractDhtmlxGanttLink> links) {
        this.data = data;
        this.links = links;
    }

    public CoreDhtmlxGanttDTO(List<? extends AbstractDhtmlxGanttData> data) {
        this.data = data;
    }

    public List<? extends AbstractDhtmlxGanttData> getData() {
        return this.data;
    }

    public void setData(List<? extends AbstractDhtmlxGanttData> data) {
        this.data = data;
    }

    public List<? extends AbstractDhtmlxGanttLink> getLinks() {
        return this.links;
    }

    public void setLinks(List<? extends AbstractDhtmlxGanttLink> links) {
        this.links = links;
    }
}
