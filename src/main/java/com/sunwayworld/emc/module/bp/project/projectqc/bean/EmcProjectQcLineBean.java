package com.sunwayworld.emc.module.bp.project.projectqc.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 项目质控明细
 * @author zhengxy@sunwayworld.com 2020年09月21日
 */
@Table("T_EMC_PROJECT_QC_LINE")
public class EmcProjectQcLineBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = -6500526225674164662L;

    @Id
    private Long id;  // 主键
    private Long qcId;  // 质控ID
    private Long qcCategoryId;  // QC类型ID
    private String qcCategoryCode;  // QC类型编码
    private String qcName;  // QC名称
    private Integer qcQty;  // 质控数量

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getQcId() {
        return qcId;
    }

    public void setQcId(Long qcId) {
        this.qcId = qcId;
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

    public Integer getQcQty() {
        return qcQty;
    }

    public void setQcQty(Integer qcQty) {
        this.qcQty = qcQty;
    }

}
