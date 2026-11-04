/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.nfssi.order.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table(value="T_CORE_NFSSI_VERIFY_REASON")
public class CoreNfssiVerifyReasonBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    private Long testItemId;
    private String stdMinLimitUnit;
    private String stdMinLimit;
    private String maxLimit;
    private String stdMaxLimit;
    private String stdMaxLimitUnit;
    private String judgeStandard;
    private String minLimitUnit;

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

    public String getStdMinLimitUnit() {
        return this.stdMinLimitUnit;
    }

    public void setStdMinLimitUnit(String stdMinLimitUnit) {
        this.stdMinLimitUnit = stdMinLimitUnit;
    }

    public String getStdMinLimit() {
        return this.stdMinLimit;
    }

    public void setStdMinLimit(String stdMinLimit) {
        this.stdMinLimit = stdMinLimit;
    }

    public String getMaxLimit() {
        return this.maxLimit;
    }

    public void setMaxLimit(String maxLimit) {
        this.maxLimit = maxLimit;
    }

    public String getStdMaxLimit() {
        return this.stdMaxLimit;
    }

    public void setStdMaxLimit(String stdMaxLimit) {
        this.stdMaxLimit = stdMaxLimit;
    }

    public String getStdMaxLimitUnit() {
        return this.stdMaxLimitUnit;
    }

    public void setStdMaxLimitUnit(String stdMaxLimitUnit) {
        this.stdMaxLimitUnit = stdMaxLimitUnit;
    }

    public String getJudgeStandard() {
        return this.judgeStandard;
    }

    public void setJudgeStandard(String judgeStandard) {
        this.judgeStandard = judgeStandard;
    }

    public String getMinLimitUnit() {
        return this.minLimitUnit;
    }

    public void setMinLimitUnit(String minLimitUnit) {
        this.minLimitUnit = minLimitUnit;
    }
}
