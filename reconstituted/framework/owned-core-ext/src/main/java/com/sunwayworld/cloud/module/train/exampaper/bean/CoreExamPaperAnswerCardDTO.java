/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.exampaper.bean;

import java.io.Serializable;

public class CoreExamPaperAnswerCardDTO
implements Serializable {
    private static final long serialVersionUID = -1155059368818355599L;
    private Long examPaperId;
    private Long columnValue1;
    private Long columnValue2;
    private Long columnValue3;
    private Long columnValue4;
    private String answered1;
    private String answered2;
    private String answered3;
    private String answered4;

    public Long getExamPaperId() {
        return this.examPaperId;
    }

    public void setExamPaperId(Long examPaperId) {
        this.examPaperId = examPaperId;
    }

    public Long getColumnValue1() {
        return this.columnValue1;
    }

    public void setColumnValue1(Long columnValue1) {
        this.columnValue1 = columnValue1;
    }

    public Long getColumnValue2() {
        return this.columnValue2;
    }

    public void setColumnValue2(Long columnValue2) {
        this.columnValue2 = columnValue2;
    }

    public Long getColumnValue3() {
        return this.columnValue3;
    }

    public void setColumnValue3(Long columnValue3) {
        this.columnValue3 = columnValue3;
    }

    public Long getColumnValue4() {
        return this.columnValue4;
    }

    public void setColumnValue4(Long columnValue4) {
        this.columnValue4 = columnValue4;
    }

    public String getAnswered1() {
        return this.answered1;
    }

    public void setAnswered1(String answered1) {
        this.answered1 = answered1;
    }

    public String getAnswered2() {
        return this.answered2;
    }

    public void setAnswered2(String answered2) {
        this.answered2 = answered2;
    }

    public String getAnswered3() {
        return this.answered3;
    }

    public void setAnswered3(String answered3) {
        this.answered3 = answered3;
    }

    public String getAnswered4() {
        return this.answered4;
    }

    public void setAnswered4(String answered4) {
        this.answered4 = answered4;
    }
}
