package com.sunwayworld.emc.module.bp.result.run.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * QC关联样品
 * @author zhengxy@sunwayworld.com 2021年12月08日
 */
@Table("T_EMC_QC_RELATED_ORDER")
public class EmcQcRelatedOrderBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = -5246321089275844958L;

    @Id
    private Long id;  // 主键
    private Long qcOrderId;  // QC样品ID
    private String qcOrderNo;  // QC样品编号
    private Long qcCategoryId;  // QC ID
    private String qcCategoryCode;  // QC编码
    private String qcName;  // QC名称
    private Long relatedOrderId;  // 关联的样品ID
    private String relatedOrderNo;  // 关联的样品编号
    private String relatedRunId; // 关联的批号



    private String taskRunId; // 关联的批号


    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getQcOrderId() {
        return qcOrderId;
    }

    public void setQcOrderId(Long qcOrderId) {
        this.qcOrderId = qcOrderId;
    }

    public String getQcOrderNo() {
        return qcOrderNo;
    }

    public void setQcOrderNo(String qcOrderNo) {
        this.qcOrderNo = qcOrderNo;
    }

    public Long getQcCategoryId() {
        return qcCategoryId;
    }

    public void setQcCategoryId(Long qcCategoryId) {
        this.qcCategoryId = qcCategoryId;
    }

    public String getQcCategoryCode() {
        return qcCategoryCode;
    }

    public void setQcCategoryCode(String qcCategoryCode) {
        this.qcCategoryCode = qcCategoryCode;
    }

    public String getQcName() {
        return qcName;
    }

    public void setQcName(String qcName) {
        this.qcName = qcName;
    }

    public Long getRelatedOrderId() {
        return relatedOrderId;
    }

    public void setRelatedOrderId(Long relatedOrderId) {
        this.relatedOrderId = relatedOrderId;
    }

    public String getRelatedOrderNo() {
        return relatedOrderNo;
    }

    public void setRelatedOrderNo(String relatedOrderNo) {
        this.relatedOrderNo = relatedOrderNo;
    }

    public String getRelatedRunId() {
        return relatedRunId;
    }

    public void setRelatedRunId(String relatedRunId) {
        this.relatedRunId = relatedRunId;
    }

    public String getTaskRunId() {
        return taskRunId;
    }

    public void setTaskRunId(String taskRunId) {
        this.taskRunId = taskRunId;
    }
}
