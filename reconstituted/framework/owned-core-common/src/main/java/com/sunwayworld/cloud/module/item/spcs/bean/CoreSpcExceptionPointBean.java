/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.spcs.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sunwayworld.framework.utils.NumberUtils;
import java.io.Serializable;

public class CoreSpcExceptionPointBean
implements Serializable {
    private static final long serialVersionUID = 230811013565482480L;
    @JsonProperty(value="xAxis")
    private Double xAxis;
    @JsonProperty(value="yAxis")
    private Double yAxis;
    private String tooltip;
    private Integer pileNum;

    public Double getxAxis() {
        return this.xAxis;
    }

    public void setxAxis(Double xAxis) {
        this.xAxis = xAxis;
    }

    public Double getyAxis() {
        return this.yAxis;
    }

    public void setyAxis(Double yAxis) {
        this.yAxis = yAxis;
    }

    public String getTooltip() {
        return this.tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public Integer getPileNum() {
        return this.pileNum;
    }

    public void setPileNum(Integer pileNum) {
        this.pileNum = pileNum;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        CoreSpcExceptionPointBean other = (CoreSpcExceptionPointBean)obj;
        if (this.xAxis == null ? other.xAxis != null : !this.xAxis.equals(other.xAxis)) {
            return false;
        }
        return !(this.yAxis == null ? other.yAxis != null : !this.yAxis.equals(other.yAxis));
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.xAxis == null ? 0 : this.xAxis.hashCode());
        result = 31 * result + (this.yAxis == null ? 0 : this.yAxis.hashCode());
        return result;
    }

    public String toString() {
        return "[" + this.getxAxis().intValue() + ", " + NumberUtils.toDouble(this.getyAxis(), 2) + "]";
    }
}
