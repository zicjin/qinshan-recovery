package com.sunwayworld.emc.module.mdm.basemdm.bizcategory.bean;

import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 容器类型
 * 
 * @author magt@sunwayworld.com 2019/9/9
 *
 */
@Table("T_EMC_BIZ_CATEGORY")
public class EmcBizCategoryBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = -8177929360183991554L;

    @Id
    private Long id; // 主键
    private String bizCategoryId; // 业务类型编码
    private String bizCategoryName; // 业务类型名称
    private String reportName; // 报告名称
    private String type; // 类别
    private String sample; // 是否采样
    private String sampleOrgId; // 默认采样科室编码
    private String reportEditOrgId; // 默认报告编制科室编码
    private String reportSendOrgId; // 报告发送科室编码
    private String entrustOrg; // 默认委托单位名称
    private Long entrustOrgId; // 默认委托单位ID
    private String entrustOrgNo; // 默认委托单位编码
    private String testOrg; // 默认受检单位名称
    private Long testOrgId; // 默认受检单位名称ID
    private String testOrgNo; // 默认受检单位名称编码
    private String ndCal; // nd计算规则
    private String projName; // 默认项目名称
    private String encryption; // 是否加密
    private String editMonitorInfo; // 编辑监测点信息
    private String reportWord; // 默认报告字
    @NotNull(defaultValue = "form1")
    private String formStyle; // 表单样式
    @NotNull(defaultValue = Constant.NO)
    private String flowControl;// 是否进行报告复审（0，1）
    @NotNull(defaultValue = Constant.NO)
    private String showSample; // 在添加项目时，弹出框是否显示是否采样（0,1）
    @NotNull(defaultValue = "1")
    private String orderNo; // 排序号

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBizCategoryId() {
        return bizCategoryId;
    }

    public void setBizCategoryId(String bizCategoryId) {
        this.bizCategoryId = bizCategoryId;
    }

    public String getBizCategoryName() {
        return bizCategoryName;
    }

    public void setBizCategoryName(String bizCategoryName) {
        this.bizCategoryName = bizCategoryName;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

    public String getSampleOrgId() {
        return sampleOrgId;
    }

    public void setSampleOrgId(String sampleOrgId) {
        this.sampleOrgId = sampleOrgId;
    }

    public String getReportEditOrgId() {
        return reportEditOrgId;
    }

    public void setReportEditOrgId(String reportEditOrgId) {
        this.reportEditOrgId = reportEditOrgId;
    }

    public String getReportSendOrgId() {
        return reportSendOrgId;
    }

    public void setReportSendOrgId(String reportSendOrgId) {
        this.reportSendOrgId = reportSendOrgId;
    }

    public String getEntrustOrg() {
        return entrustOrg;
    }

    public void setEntrustOrg(String entrustOrg) {
        this.entrustOrg = entrustOrg;
    }

    public Long getEntrustOrgId() {
        return entrustOrgId;
    }

    public void setEntrustOrgId(Long entrustOrgId) {
        this.entrustOrgId = entrustOrgId;
    }

    public String getEntrustOrgNo() {
        return entrustOrgNo;
    }

    public void setEntrustOrgNo(String entrustOrgNo) {
        this.entrustOrgNo = entrustOrgNo;
    }

    public String getTestOrg() {
        return testOrg;
    }

    public void setTestOrg(String testOrg) {
        this.testOrg = testOrg;
    }

    public Long getTestOrgId() {
        return testOrgId;
    }

    public void setTestOrgId(Long testOrgId) {
        this.testOrgId = testOrgId;
    }

    public String getTestOrgNo() {
        return testOrgNo;
    }

    public void setTestOrgNo(String testOrgNo) {
        this.testOrgNo = testOrgNo;
    }

    public String getNdCal() {
        return ndCal;
    }

    public void setNdCal(String ndCal) {
        this.ndCal = ndCal;
    }

    public String getProjName() {
        return projName;
    }

    public void setProjName(String projName) {
        this.projName = projName;
    }

    public String getEncryption() {
        return encryption;
    }

    public void setEncryption(String encryption) {
        this.encryption = encryption;
    }

    public String getEditMonitorInfo() {
        return editMonitorInfo;
    }

    public void setEditMonitorInfo(String editMonitorInfo) {
        this.editMonitorInfo = editMonitorInfo;
    }

    public String getReportWord() {
        return reportWord;
    }

    public void setReportWord(String reportWord) {
        this.reportWord = reportWord;
    }

    public String getFormStyle() {
        return formStyle;
    }

    public void setFormStyle(String formStyle) {
        this.formStyle = formStyle;
    }

    public String getFlowControl() {
        return flowControl;
    }

    public void setFlowControl(String flowControl) {
        this.flowControl = flowControl;
    }

    public String getShowSample() {
        return showSample;
    }

    public void setShowSample(String showSample) {
        this.showSample = showSample;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
