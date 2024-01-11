package com.sunwayworld.emc.module.mdm.bizmdm.test.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 测试方法默认QC
 * @author zhengxy@sunwayworld.com 2021年06月17日
 */
@Table("T_EMC_TEST_METHOD_DEFAULT_QC")
public class EmcTestMethodDefaultQcBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = -7023464586864744904L;

    @Id
    private Long id;  // 主键
    private Long testCapacityId; //监测能力库Id
    private Long testMethodId;  // 测试方法ID
    private Long qcCategoryId;  // QC类型ID
    private String qcCategoryCode;  // QC类型编码
    private String qcName;  // QC名称
    private Integer addNum;  // 添加QC数量
    @NotNull(defaultValue = "1")
    private Integer startOrderIndex; // 起始样品
    private Integer intervalNum;  // 样品间隔数
    private Integer checkPoint; // 中间校核点
    private String orgId; // 机构ID

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getTestCapacityId() {
        return testCapacityId;
    }

    public void setTestCapacityId(Long testCapacityId) {
        this.testCapacityId = testCapacityId;
    }

    public Long getTestMethodId() {
        return testMethodId;
    }

    public void setTestMethodId(Long testMethodId) {
        this.testMethodId = testMethodId;
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

    public Integer getAddNum() {
        return addNum;
    }

    public void setAddNum(Integer addNum) {
        this.addNum = addNum;
    }

    public Integer getStartOrderIndex() {
        return startOrderIndex;
    }

    public void setStartOrderIndex(Integer startOrderIndex) {
        this.startOrderIndex = startOrderIndex;
    }

    public Integer getIntervalNum() {
        return intervalNum;
    }

    public void setIntervalNum(Integer intervalNum) {
        this.intervalNum = intervalNum;
    }

    public Integer getCheckPoint() {
        return checkPoint;
    }

    public void setCheckPoint(Integer checkPoint) {
        this.checkPoint = checkPoint;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
}
