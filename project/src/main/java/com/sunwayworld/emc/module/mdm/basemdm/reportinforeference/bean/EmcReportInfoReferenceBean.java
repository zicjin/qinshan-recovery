package com.sunwayworld.emc.module.mdm.basemdm.reportinforeference.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 报告信息对照
 * @author zhengxy@sunwayworld.com 2020年03月06日
 */
@Table("T_EMC_REPORT_INFO_REFERENCE")
public class EmcReportInfoReferenceBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = -4059756844649482829L;

    @Id
    private Long id;  // 主键
    private Long sortNum;  // 排序码
    private String bizCategory;  // 业务类型
    private String orderCategory;  // 样品类型
    private String reportName;  // 报告名称
    private String testCode;  // 测试代码
    private String itemName;  // 分析项目
    private String reportShowName;  // 报告中显示名称
    private String method;  // 方法简称
    private String analysisGroup;  // 分析项组
    private String groupConditionOne;  // 分组条件1
    private String groupConditionTwo;  // 分组条件2
    private String groupConditionThree;  // 分组条件3
    private String groupConditionFour;  // 分组条件4

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSortNum() {
        return sortNum;
    }

    public void setSortNum(Long sortNum) {
        this.sortNum = sortNum;
    }

    public String getBizCategory() {
        return bizCategory;
    }

    public void setBizCategory(String bizCategory) {
        this.bizCategory = bizCategory;
    }

    public String getOrderCategory() {
        return orderCategory;
    }

    public void setOrderCategory(String orderCategory) {
        this.orderCategory = orderCategory;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getTestCode() {
        return testCode;
    }

    public void setTestCode(String testCode) {
        this.testCode = testCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getReportShowName() {
        return reportShowName;
    }

    public void setReportShowName(String reportShowName) {
        this.reportShowName = reportShowName;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getAnalysisGroup() {
        return analysisGroup;
    }

    public void setAnalysisGroup(String analysisGroup) {
        this.analysisGroup = analysisGroup;
    }

    public String getGroupConditionOne() {
        return groupConditionOne;
    }

    public void setGroupConditionOne(String groupConditionOne) {
        this.groupConditionOne = groupConditionOne;
    }

    public String getGroupConditionTwo() {
        return groupConditionTwo;
    }

    public void setGroupConditionTwo(String groupConditionTwo) {
        this.groupConditionTwo = groupConditionTwo;
    }

    public String getGroupConditionThree() {
        return groupConditionThree;
    }

    public void setGroupConditionThree(String groupConditionThree) {
        this.groupConditionThree = groupConditionThree;
    }

    public String getGroupConditionFour() {
        return groupConditionFour;
    }

    public void setGroupConditionFour(String groupConditionFour) {
        this.groupConditionFour = groupConditionFour;
    }

}
