/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.report.bean;

import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import javax.persistence.Column;

@Table(value="T_CORE_NFSSI_REPORT_ITEM")
public class CoreNfssiReportItemBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    private String reportId;
    private String orderId;
    @Column(name="\u68c0\u9a8c\u9879\u76ee", nullable=false)
    private String testItem;
    @Column(name="\u68c0\u9a8c\u7ed3\u679c", nullable=false)
    private String testResult;
    @Clob
    @Column(name="\u7ed3\u679c\u5224\u5b9a", nullable=false)
    private String resultJudgment;
    @Column(name="\u68c0\u9a8c\u4f9d\u636e", nullable=false)
    private String inspectStandard;
    @Column(name="\u5224\u5b9a\u4f9d\u636e", nullable=false)
    private String judgeStandard;
    @Column(name="\u65b9\u6cd5\u68c0\u51fa\u9650", nullable=false)
    private String methodLimit;
    @Column(name="\u68c0\u51fa\u9650\u5355\u4f4d", nullable=false)
    private String detectionLimitOrg;
    @Column(name="\u6700\u5c0f\u5141\u8bb8\u9650", nullable=false)
    private String minLimit;
    @Column(name="\u6700\u5c0f\u5141\u8bb8\u9650\u5355\u4f4d", nullable=false)
    private String minLimitUnit;
    @Column(name="\u6700\u5927\u5141\u8bb8\u9650", nullable=false)
    private String maxLimit;
    @Column(name="\u6700\u5927\u5141\u8bb8\u9650\u5355\u4f4d", nullable=false)
    private String maxLimitUnit;
    @Column(name="\u8bf4\u660e", nullable=false)
    private String explain;
    @Column(name="\u7ed3\u679c\u5355\u4f4d", nullable=false)
    private String resultUnit;
    @Column(name="\u68c0\u9a8c\u4f9d\u636e\u7b80\u79f0", nullable=false)
    private String shortInspectStandard;
    @Clob
    @Column(name="\u5907\u6ce8", nullable=false)
    private String remark;
    @Column(name="\u7528\u6237\u4fee\u6539\u524d\u7684\u9879\u76ee\u540d\u79f0", nullable=false)
    private String oldItemName;
    @Column(name="\u9879\u76ee\u7c7b\u578b", nullable=false)
    private String itemCategory;
    private String necessary;
    private String funcCategory;
    private Long status;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getReportId() {
        return this.reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getOrderId() {
        return this.orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTestItem() {
        return this.testItem;
    }

    public void setTestItem(String testItem) {
        this.testItem = testItem;
    }

    public String getTestResult() {
        return this.testResult;
    }

    public void setTestResult(String testResult) {
        this.testResult = testResult;
    }

    public String getResultJudgment() {
        return this.resultJudgment;
    }

    public void setResultJudgment(String resultJudgment) {
        this.resultJudgment = resultJudgment;
    }

    public String getInspectStandard() {
        return this.inspectStandard;
    }

    public void setInspectStandard(String inspectStandard) {
        this.inspectStandard = inspectStandard;
    }

    public String getJudgeStandard() {
        return this.judgeStandard;
    }

    public void setJudgeStandard(String judgeStandard) {
        this.judgeStandard = judgeStandard;
    }

    public String getMethodLimit() {
        return this.methodLimit;
    }

    public void setMethodLimit(String methodLimit) {
        this.methodLimit = methodLimit;
    }

    public String getDetectionLimitOrg() {
        return this.detectionLimitOrg;
    }

    public void setDetectionLimitOrg(String detectionLimitOrg) {
        this.detectionLimitOrg = detectionLimitOrg;
    }

    public String getMinLimit() {
        return this.minLimit;
    }

    public void setMinLimit(String minLimit) {
        this.minLimit = minLimit;
    }

    public String getMinLimitUnit() {
        return this.minLimitUnit;
    }

    public void setMinLimitUnit(String minLimitUnit) {
        this.minLimitUnit = minLimitUnit;
    }

    public String getMaxLimit() {
        return this.maxLimit;
    }

    public void setMaxLimit(String maxLimit) {
        this.maxLimit = maxLimit;
    }

    public String getMaxLimitUnit() {
        return this.maxLimitUnit;
    }

    public void setMaxLimitUnit(String maxLimitUnit) {
        this.maxLimitUnit = maxLimitUnit;
    }

    public String getExplain() {
        return this.explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public String getResultUnit() {
        return this.resultUnit;
    }

    public void setResultUnit(String resultUnit) {
        this.resultUnit = resultUnit;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOldItemName() {
        return this.oldItemName;
    }

    public void setOldItemName(String oldItemName) {
        this.oldItemName = oldItemName;
    }

    public String getItemCategory() {
        return this.itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getNecessary() {
        return this.necessary;
    }

    public void setNecessary(String necessary) {
        this.necessary = necessary;
    }

    public String getFuncCategory() {
        return this.funcCategory;
    }

    public void setFuncCategory(String funcCategory) {
        this.funcCategory = funcCategory;
    }

    public Long getStatus() {
        return this.status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getShortInspectStandard() {
        return this.shortInspectStandard;
    }

    public void setShortInspectStandard(String shortInspectStandard) {
        this.shortInspectStandard = shortInspectStandard;
    }
}
