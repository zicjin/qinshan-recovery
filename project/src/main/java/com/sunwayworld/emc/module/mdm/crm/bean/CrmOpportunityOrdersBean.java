package com.sunwayworld.emc.module.mdm.crm.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 商机orders表
 * @author zhengxy@sunwayworld.com 2021年08月26日
 */
@Table("T_CRM_OPPORTUNITY_ORDERS")
public class CrmOpportunityOrdersBean extends AbstractInsertable<String> {

    @Transient
    private static final long serialVersionUID = -7740137653059408873L;

    @Id
    private String id;// 主键
    private String batNo;// 批号
    private String clSampNo;// 化妆品批信息-样品说明
    private String opportunityNo;// 检品编号
    private String ordNo;// 点位编号
    private Long spCode;// 模板编号
    private String mainUsrNam;// 负责人用户名
    private String mainFullName;// 负责人
    private String profile;// 模板方案编号
    private String comments;// 备注
    private String foldersId;// foldersid
    private String isRetain;// 留样标记 默认n 留样y 取消n 无需留样f
    private String qctype;// qc类型
    private String addQcUserId;// qc添加人
    private String addQcUserName;// qc添加人
    private String parentorderId;// qc父样编号
    private String qcSampleId;// 质控样id
    private String qcSampleNo;// 质控样编号
    private String needMakeSample;// 是否需要制样 0:不需要 1:需要
    private String isMakeSample;// 是否制样 0:未制样 1:已制样
    private String sampnoIsReportShow;// 样品说明是否在报告书中显示 0 ：否 1：是
    private String testType; //检测类别
    private Double totalPrice; //项目总价
    private String potName; // 点位名称
    private Double potNum; // 点位数量
    private String potNo; //点位编号
    private String createdById;// 制单人编码
    private String createdByName;// 制单人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间
    private String createdByOrgId;// 制单人单位编码
    private String createdByOrgName;// 制单人单位名称

    private String chooseFlag; // 是否选择
    private String sampleManufacture; // 生产单位
    private String manuAdd; //生产单位地址
    private String reportTitleUnit; // 报告单位
    private String reportTitleUnitAddr; // 报告单位地址
    private String issamplereject; // 余样处置
    private String serviceType; // 服务类型
    private String reportFormat; // 报告版式
    private String reportAim; // 报告用途
    private String judgeFlag; //是否判定
    private String cma; //cma
    private String cnas; //cnas
    private String catl; //catl
    private String matName; //样品名称
    private String batchNo; //生产日期/批号
    private String sampleLogo; // 商标
    private String samplemeDicineType;// 规格
    private String baoyanQuantity; //样品数量
    private String status; // 样品状态
    private String samplepcondition; //存储条件
    private String testRule; // 判定依据
    private String testMethods; // 执行标准
    private String totalAmount; // 项目总价
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime productionDate; // 生产日期
    private String projectSubtotal; // 项目小计
    private Double sorter;
    private Long testingFrequency;// 频次/天
    private Long testingDays;// 监测天数
    private Long numberOfInspections; //检测次数
    private String monitorTmplGroup;
    private String productCategories;

    private String sampleSource; // 样品来源
    private String associatedPoint; // 关联点位

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getBatNo() {
        return batNo;
    }

    public void setBatNo(String batNo) {
        this.batNo = batNo;
    }

    public String getClSampNo() {
        return clSampNo;
    }

    public void setClSampNo(String clSampNo) {
        this.clSampNo = clSampNo;
    }

    public String getOpportunityNo() {
        return opportunityNo;
    }

    public void setOpportunityNo(String opportunityNo) {
        this.opportunityNo = opportunityNo;
    }

    public String getOrdNo() {
        return ordNo;
    }

    public void setOrdNo(String ordNo) {
        this.ordNo = ordNo;
    }

    public Long getSpCode() {
        return spCode;
    }

    public void setSpCode(Long spCode) {
        this.spCode = spCode;
    }

    public String getMainUsrNam() {
        return mainUsrNam;
    }

    public void setMainUsrNam(String mainUsrNam) {
        this.mainUsrNam = mainUsrNam;
    }

    public String getMainFullName() {
        return mainFullName;
    }

    public void setMainFullName(String mainFullName) {
        this.mainFullName = mainFullName;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getFoldersId() {
        return foldersId;
    }

    public void setFoldersId(String foldersId) {
        this.foldersId = foldersId;
    }

    public String getIsRetain() {
        return isRetain;
    }

    public void setIsRetain(String isRetain) {
        this.isRetain = isRetain;
    }

    public String getQctype() {
        return qctype;
    }

    public void setQctype(String qctype) {
        this.qctype = qctype;
    }

    public String getAddQcUserId() {
        return addQcUserId;
    }

    public void setAddQcUserId(String addQcUserId) {
        this.addQcUserId = addQcUserId;
    }

    public String getAddQcUserName() {
        return addQcUserName;
    }

    public void setAddQcUserName(String addQcUserName) {
        this.addQcUserName = addQcUserName;
    }

    public String getParentorderId() {
        return parentorderId;
    }

    public void setParentorderId(String parentorderId) {
        this.parentorderId = parentorderId;
    }

    public String getQcSampleId() {
        return qcSampleId;
    }

    public void setQcSampleId(String qcSampleId) {
        this.qcSampleId = qcSampleId;
    }

    public String getQcSampleNo() {
        return qcSampleNo;
    }

    public void setQcSampleNo(String qcSampleNo) {
        this.qcSampleNo = qcSampleNo;
    }

    public String getNeedMakeSample() {
        return needMakeSample;
    }

    public void setNeedMakeSample(String needMakeSample) {
        this.needMakeSample = needMakeSample;
    }

    public String getIsMakeSample() {
        return isMakeSample;
    }

    public void setIsMakeSample(String isMakeSample) {
        this.isMakeSample = isMakeSample;
    }

    public String getSampnoIsReportShow() {
        return sampnoIsReportShow;
    }

    public void setSampnoIsReportShow(String sampnoIsReportShow) {
        this.sampnoIsReportShow = sampnoIsReportShow;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPotName() {
        return potName;
    }

    public void setPotName(String potName) {
        this.potName = potName;
    }

    public Double getPotNum() {
        return potNum;
    }

    public void setPotNum(Double potNum) {
        this.potNum = potNum;
    }

    public String getPotNo() {
        return potNo;
    }

    public void setPotNo(String potNo) {
        this.potNo = potNo;
    }

    @Override
    public String getCreatedById() {
        return createdById;
    }

    @Override
    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    @Override
    public String getCreatedByName() {
        return createdByName;
    }

    @Override
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    @Override
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    @Override
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String getCreatedByOrgId() {
        return createdByOrgId;
    }

    @Override
    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    @Override
    public String getCreatedByOrgName() {
        return createdByOrgName;
    }

    @Override
    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }

    public String getChooseFlag() {
        return chooseFlag;
    }

    public void setChooseFlag(String chooseFlag) {
        this.chooseFlag = chooseFlag;
    }

    public String getSampleManufacture() {
        return sampleManufacture;
    }

    public void setSampleManufacture(String sampleManufacture) {
        this.sampleManufacture = sampleManufacture;
    }

    public String getManuAdd() {
        return manuAdd;
    }

    public void setManuAdd(String manuAdd) {
        this.manuAdd = manuAdd;
    }

    public String getReportTitleUnit() {
        return reportTitleUnit;
    }

    public void setReportTitleUnit(String reportTitleUnit) {
        this.reportTitleUnit = reportTitleUnit;
    }

    public String getReportTitleUnitAddr() {
        return reportTitleUnitAddr;
    }

    public void setReportTitleUnitAddr(String reportTitleUnitAddr) {
        this.reportTitleUnitAddr = reportTitleUnitAddr;
    }

    public String getIssamplereject() {
        return issamplereject;
    }

    public void setIssamplereject(String issamplereject) {
        this.issamplereject = issamplereject;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getReportFormat() {
        return reportFormat;
    }

    public void setReportFormat(String reportFormat) {
        this.reportFormat = reportFormat;
    }

    public String getReportAim() {
        return reportAim;
    }

    public void setReportAim(String reportAim) {
        this.reportAim = reportAim;
    }

    public String getJudgeFlag() {
        return judgeFlag;
    }

    public void setJudgeFlag(String judgeFlag) {
        this.judgeFlag = judgeFlag;
    }

    public String getCma() {
        return cma;
    }

    public void setCma(String cma) {
        this.cma = cma;
    }

    public String getCnas() {
        return cnas;
    }

    public void setCnas(String cnas) {
        this.cnas = cnas;
    }

    public String getCatl() {
        return catl;
    }

    public void setCatl(String catl) {
        this.catl = catl;
    }

    public String getMatName() {
        return matName;
    }

    public void setMatName(String matName) {
        this.matName = matName;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getSampleLogo() {
        return sampleLogo;
    }

    public void setSampleLogo(String sampleLogo) {
        this.sampleLogo = sampleLogo;
    }

    public String getSamplemeDicineType() {
        return samplemeDicineType;
    }

    public void setSamplemeDicineType(String samplemeDicineType) {
        this.samplemeDicineType = samplemeDicineType;
    }

    public String getBaoyanQuantity() {
        return baoyanQuantity;
    }

    public void setBaoyanQuantity(String baoyanQuantity) {
        this.baoyanQuantity = baoyanQuantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSamplepcondition() {
        return samplepcondition;
    }

    public void setSamplepcondition(String samplepcondition) {
        this.samplepcondition = samplepcondition;
    }

    public String getTestRule() {
        return testRule;
    }

    public void setTestRule(String testRule) {
        this.testRule = testRule;
    }

    public String getTestMethods() {
        return testMethods;
    }

    public void setTestMethods(String testMethods) {
        this.testMethods = testMethods;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(LocalDateTime productionDate) {
        this.productionDate = productionDate;
    }

    public String getProjectSubtotal() {
        return projectSubtotal;
    }

    public void setProjectSubtotal(String projectSubtotal) {
        this.projectSubtotal = projectSubtotal;
    }

    public Double getSorter() {
        return sorter;
    }

    public void setSorter(Double sorter) {
        this.sorter = sorter;
    }

    public Long getTestingFrequency() {
        return testingFrequency;
    }

    public void setTestingFrequency(Long testingFrequency) {
        this.testingFrequency = testingFrequency;
    }

    public Long getTestingDays() {
        return testingDays;
    }

    public void setTestingDays(Long testingDays) {
        this.testingDays = testingDays;
    }

    public Long getNumberOfInspections() {
        return numberOfInspections;
    }

    public void setNumberOfInspections(Long numberOfInspections) {
        this.numberOfInspections = numberOfInspections;
    }

    public String getMonitorTmplGroup() {
        return monitorTmplGroup;
    }

    public void setMonitorTmplGroup(String monitorTmplGroup) {
        this.monitorTmplGroup = monitorTmplGroup;
    }

    public String getProductCategories() {
        return productCategories;
    }

    public void setProductCategories(String productCategories) {
        this.productCategories = productCategories;
    }

    public String getSampleSource() {
        return sampleSource;
    }

    public void setSampleSource(String sampleSource) {
        this.sampleSource = sampleSource;
    }

    public String getAssociatedPoint() {
        return associatedPoint;
    }

    public void setAssociatedPoint(String associatedPoint) {
        this.associatedPoint = associatedPoint;
    }
}
