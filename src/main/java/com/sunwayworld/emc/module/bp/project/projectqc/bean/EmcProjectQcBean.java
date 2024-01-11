package com.sunwayworld.emc.module.bp.project.projectqc.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 项目质控
 * @author zhengxy@sunwayworld.com 2020年09月21日
 */
@Table("T_EMC_PROJECT_QC")
public class EmcProjectQcBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = -6433496508449320758L;

    @Id
    private Long id;  // 主键
    private Long projectId;  // 项目ID
    private String monitorTmplId;  // 监测模板ID
    private String orderCategory;  // 样品类型
    private Long testId;  // 测试编码
    private String testName;  // 测试名称
    private Long totalQty;  // 测试总数

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getMonitorTmplId() {
        return monitorTmplId;
    }

    public void setMonitorTmplId(String monitorTmplId) {
        this.monitorTmplId = monitorTmplId;
    }

    public String getOrderCategory() {
        return orderCategory;
    }

    public void setOrderCategory(String orderCategory) {
        this.orderCategory = orderCategory;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public Long getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(Long totalQty) {
        this.totalQty = totalQty;
    }
}
