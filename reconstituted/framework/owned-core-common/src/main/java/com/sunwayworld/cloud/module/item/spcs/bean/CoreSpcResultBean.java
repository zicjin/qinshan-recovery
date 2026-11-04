/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.spcs.bean;

import com.sunwayworld.cloud.module.item.spcs.bean.CoreSpcExceptionDescBean;
import com.sunwayworld.cloud.module.item.spcs.bean.CoreSpcExceptionPileBean;
import com.sunwayworld.cloud.module.item.spcs.bean.CoreSpcExceptionPointBean;
import com.sunwayworld.cloud.module.item.spcs.bean.CoreSpcLineBean;
import java.io.Serializable;
import java.util.List;

public class CoreSpcResultBean
implements Serializable {
    private static final long serialVersionUID = 4263015361671197673L;
    private List<Double> data;
    private List<Double> original;
    private List<CoreSpcLineBean> lines;
    private List<CoreSpcExceptionDescBean> outlier;
    private List<CoreSpcExceptionPileBean> exceptionPile;
    private List<CoreSpcExceptionPointBean> exception;
    private Double average;
    private Double standardDeviation;

    public List<Double> getData() {
        return this.data;
    }

    public void setData(List<Double> data) {
        this.data = data;
    }

    public List<Double> getOriginal() {
        return this.original;
    }

    public void setOriginal(List<Double> original) {
        this.original = original;
    }

    public List<CoreSpcLineBean> getLines() {
        return this.lines;
    }

    public void setLines(List<CoreSpcLineBean> lines) {
        this.lines = lines;
    }

    public List<CoreSpcExceptionDescBean> getOutlier() {
        return this.outlier;
    }

    public void setOutlier(List<CoreSpcExceptionDescBean> outlier) {
        this.outlier = outlier;
    }

    public List<CoreSpcExceptionPileBean> getExceptionPile() {
        return this.exceptionPile;
    }

    public void setExceptionPile(List<CoreSpcExceptionPileBean> exceptionPile) {
        this.exceptionPile = exceptionPile;
    }

    public List<CoreSpcExceptionPointBean> getException() {
        return this.exception;
    }

    public void setException(List<CoreSpcExceptionPointBean> exception) {
        this.exception = exception;
    }

    public Double getAverage() {
        return this.average;
    }

    public void setAverage(Double average) {
        this.average = average;
    }

    public Double getStandardDeviation() {
        return this.standardDeviation;
    }

    public void setStandardDeviation(Double standardDeviation) {
        this.standardDeviation = standardDeviation;
    }
}
