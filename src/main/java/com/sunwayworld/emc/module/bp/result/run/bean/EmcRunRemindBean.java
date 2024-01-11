package com.sunwayworld.emc.module.bp.result.run.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 批次提醒
 */
@Table("T_EMC_RUN_REMIND")
public class EmcRunRemindBean extends AbstractInsertable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = -6440839336737807970L;

    @Id
    private Long id;// 主键
    private String remind;// 提醒
    private String fieldName;// 校验的字段
    private String value;// 值
    private String checkResult;// 校验结果
    private String abnormalExplain;// 异常说明

    private String checkType;// 检查类型
    private String qcCategoryCode;// 质控类型编码
    private String qcFieldName;// 质控校验的字段名

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getRemind() {
        return remind;
    }

    public void setRemind(String remind) {
        this.remind = remind;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult;
    }

    public String getAbnormalExplain() {
        return abnormalExplain;
    }

    public void setAbnormalExplain(String abnormalExplain) {
        this.abnormalExplain = abnormalExplain;
    }


    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public String getQcCategoryCode() {
        return qcCategoryCode;
    }

    public void setQcCategoryCode(String qcCategoryCode) {
        this.qcCategoryCode = qcCategoryCode;
    }

    public String getQcFieldName() {
        return qcFieldName;
    }

    public void setQcFieldName(String qcFieldName) {
        this.qcFieldName = qcFieldName;
    }
}
