/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.order.bean;

import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table(value="T_CORE_NFSSI_TEST_REASON")
public class CoreNfssiTestReasonBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    private Long testItemId;
    private String remark;
    private String inspectStandard;
    private String resultUnit;
    @Clob
    private String shortInspectStandard;
    private String stdCheckLimitUnit;
    private String stdCheckLimit;
    private String reportTaskA;
    private String reportTaskB;
    private String necessary;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getTestItemId() {
        return this.testItemId;
    }

    public void setTestItemId(Long testItemId) {
        this.testItemId = testItemId;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getInspectStandard() {
        return this.inspectStandard;
    }

    public void setInspectStandard(String inspectStandard) {
        this.inspectStandard = inspectStandard;
    }

    public String getResultUnit() {
        return this.resultUnit;
    }

    public void setResultUnit(String resultUnit) {
        this.resultUnit = resultUnit;
    }

    public String getShortInspectStandard() {
        return this.shortInspectStandard;
    }

    public void setShortInspectStandard(String shortInspectStandard) {
        this.shortInspectStandard = shortInspectStandard;
    }

    public String getStdCheckLimitUnit() {
        return this.stdCheckLimitUnit;
    }

    public void setStdCheckLimitUnit(String stdCheckLimitUnit) {
        this.stdCheckLimitUnit = stdCheckLimitUnit;
    }

    public String getStdCheckLimit() {
        return this.stdCheckLimit;
    }

    public void setStdCheckLimit(String stdCheckLimit) {
        this.stdCheckLimit = stdCheckLimit;
    }

    public String getReportTaskA() {
        return this.reportTaskA;
    }

    public void setReportTaskA(String reportTaskA) {
        this.reportTaskA = reportTaskA;
    }

    public String getReportTaskB() {
        return this.reportTaskB;
    }

    public void setReportTaskB(String reportTaskB) {
        this.reportTaskB = reportTaskB;
    }

    public String getNecessary() {
        return this.necessary;
    }

    public void setNecessary(String necessary) {
        this.necessary = necessary;
    }
}
