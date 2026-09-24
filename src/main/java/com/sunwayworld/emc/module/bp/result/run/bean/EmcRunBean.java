package com.sunwayworld.emc.module.bp.result.run.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 样品测试批表
 *
 * @author magt@sunwayworld.com 2019/10/16
 *
 */
@Table("T_EMC_RUN")
    public class EmcRunBean extends AbstractAuditable<String> implements Insertable<String> {

	@Transient
	private static final long serialVersionUID = 4908341682974050355L;

	@Id
    private String id; // 批号
    private Long projId;// 项目编码
    private String humidity; // 湿度
    private String temperature; // 温度
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate analysisBeginDate; // 分析开始日期
    private String analysisBeginTime; // 分析开始时间
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate analysisEndDate; // 分析结束日期
    private String analysisEndTime; // 分析结束时间
    private String receiveDate; // 领样日期
    private String receiveTime; // 领样时间
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate handleDate; // 处置日期
    private String handleMethod; // 处置方式
    @Clob
    private String beforeHandle; // 前处理
    private String beforeHandleId; // 前处理id
    private String methodParam; // 方法参数
    private String analysisUserId; // 分析员编码
    private String analysisUserName; // 分析员
    private String partakeUserId; // 共同完成人编码
    private String partakeUserName; // 共同完成人
    private String assistEquiptId; // 辅助仪器
    private String assistEquiptName; // 辅助仪器名称
    private Long equiptId; // 现场仪器
    private String curveId;// 曲线ID
    private String curveNo; // 曲线编码
    private String orderCategory; // 样品类型
    private String checkUserId; // 校核人编码
    private String checkUserName; // 校核人
    private String testMethodId; // 测试方法编码
    private String testId; // 测试编码
    private String testName; // 测试名称
    private Long methodId; // 测试编码
    private Long reportConfigLineId; // 报表模版id
    private Long sol1Id;
    private Long sol2Id;
    private Long reagentId;
    @NotNull(defaultValue = EmcBizConstant.RUN_STATUS_DRAFT)
    private String status;

    private String checkStatus; // 预审状态
    private String assistEquiptModel; // 辅助仪器名称型号
    private String assistEquiptNo; // 辅助仪器编号
    private String relatedRunId; // 关联批号（用于多批次共用同一实验室空白质控样数据情况）
    private String remark; // 备注

    private String createdById;
    private String createdByName;
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;
    private String orgId;// 所属单位编码
    private String orgName;// 所属单位名称
    private String auditedUserId;
    private String auditedUserName;
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditedDate;

    private String sol1Amount;//溶液1使用量(新锐)

    private String sol2Amount;//溶液2使用量(新锐)

    private String projNo;//项目编号

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime weightTime;

    private Long weighingReportLineId; // 称重记录模板

    private String undoReason; // 回退原因

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public Long getProjId() {
        return projId;
    }

    public void setProjId(Long projId) {
        this.projId = projId;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public LocalDate getAnalysisBeginDate() {
        return analysisBeginDate;
    }

    public void setAnalysisBeginDate(LocalDate analysisBeginDate) {
        this.analysisBeginDate = analysisBeginDate;
    }

    public String getAnalysisBeginTime() {
        return analysisBeginTime;
    }

    public void setAnalysisBeginTime(String analysisBeginTime) {
        this.analysisBeginTime = analysisBeginTime;
    }

    public LocalDate getAnalysisEndDate() {
        return analysisEndDate;
    }

    public void setAnalysisEndDate(LocalDate analysisEndDate) {
        this.analysisEndDate = analysisEndDate;
    }

    public String getAnalysisEndTime() {
        return analysisEndTime;
    }

    public void setAnalysisEndTime(String analysisEndTime) {
        this.analysisEndTime = analysisEndTime;
    }

    public String getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(String receiveDate) {
        this.receiveDate = receiveDate;
    }

    public String getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(String receiveTime) {
        this.receiveTime = receiveTime;
    }

    public LocalDate getHandleDate() {
        return handleDate;
    }

    public void setHandleDate(LocalDate handleDate) {
        this.handleDate = handleDate;
    }

    public String getHandleMethod() {
        return handleMethod;
    }

    public void setHandleMethod(String handleMethod) {
        this.handleMethod = handleMethod;
    }

    public String getBeforeHandle() {
        return beforeHandle;
    }

    public void setBeforeHandle(String beforeHandle) {
        this.beforeHandle = beforeHandle;
    }

    public String getMethodParam() {
        return methodParam;
    }

    public void setMethodParam(String methodParam) {
        this.methodParam = methodParam;
    }

    public String getAnalysisUserId() {
        return analysisUserId;
    }

    public void setAnalysisUserId(String analysisUserId) {
        this.analysisUserId = analysisUserId;
    }

    public String getAnalysisUserName() {
        return analysisUserName;
    }

    public void setAnalysisUserName(String analysisUserName) {
        this.analysisUserName = analysisUserName;
    }

    public String getPartakeUserId() {
        return partakeUserId;
    }

    public void setPartakeUserId(String partakeUserId) {
        this.partakeUserId = partakeUserId;
    }

    public String getPartakeUserName() {
        return partakeUserName;
    }

    public void setPartakeUserName(String partakeUserName) {
        this.partakeUserName = partakeUserName;
    }

    public String getAssistEquiptId() {
        return assistEquiptId;
    }

    public void setAssistEquiptId(String assistEquiptId) {
        this.assistEquiptId = assistEquiptId;
    }

    public String getAssistEquiptName() {
        return assistEquiptName;
    }

    public void setAssistEquiptName(String assistEquiptName) {
        this.assistEquiptName = assistEquiptName;
    }

    public Long getEquiptId() {
        return equiptId;
    }

    public void setEquiptId(Long equiptId) {
        this.equiptId = equiptId;
    }

    public String getCurveId() {
        return curveId;
    }

    public void setCurveId(String curveId) {
        this.curveId = curveId;
    }

    public String getCurveNo() {
        return curveNo;
    }

    public void setCurveNo(String curveNo) {
        this.curveNo = curveNo;
    }

    public String getOrderCategory() {
        return orderCategory;
    }

    public void setOrderCategory(String orderCategory) {
        this.orderCategory = orderCategory;
    }

    public String getCheckUserId() {
        return checkUserId;
    }

    public void setCheckUserId(String checkUserId) {
        this.checkUserId = checkUserId;
    }

    public String getCheckUserName() {
        return checkUserName;
    }

    public void setCheckUserName(String checkUserName) {
        this.checkUserName = checkUserName;
    }

    public String getTestMethodId() {
        return testMethodId;
    }

    public void setTestMethodId(String testMethodId) {
        this.testMethodId = testMethodId;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public Long getMethodId() {
        return methodId;
    }

    public void setMethodId(Long methodId) {
        this.methodId = methodId;
    }

    public Long getReportConfigLineId() {
        return reportConfigLineId;
    }

    public void setReportConfigLineId(Long reportConfigLineId) {
        this.reportConfigLineId = reportConfigLineId;
    }

    public Long getSol1Id() {
        return sol1Id;
    }

    public void setSol1Id(Long sol1Id) {
        this.sol1Id = sol1Id;
    }

    public Long getSol2Id() {
        return sol2Id;
    }

    public void setSol2Id(Long sol2Id) {
        this.sol2Id = sol2Id;
    }

    public Long getReagentId() {
        return reagentId;
    }

    public void setReagentId(Long reagentId) {
        this.reagentId = reagentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getAssistEquiptModel() {
        return assistEquiptModel;
    }

    public void setAssistEquiptModel(String assistEquiptModel) {
        this.assistEquiptModel = assistEquiptModel;
    }

    public String getAssistEquiptNo() {
        return assistEquiptNo;
    }

    public void setAssistEquiptNo(String assistEquiptNo) {
        this.assistEquiptNo = assistEquiptNo;
    }

    public String getRelatedRunId() {
        return relatedRunId;
    }

    public void setRelatedRunId(String relatedRunId) {
        this.relatedRunId = relatedRunId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getAuditedUserId() {
        return auditedUserId;
    }

    public void setAuditedUserId(String auditedUserId) {
        this.auditedUserId = auditedUserId;
    }

    public String getAuditedUserName() {
        return auditedUserName;
    }

    public void setAuditedUserName(String auditedUserName) {
        this.auditedUserName = auditedUserName;
    }

    public LocalDateTime getAuditedDate() {
        return auditedDate;
    }

    public void setAuditedDate(LocalDateTime auditedDate) {
        this.auditedDate = auditedDate;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getBeforeHandleId() {
        return beforeHandleId;
    }

    public void setBeforeHandleId(String beforeHandleId) {
        this.beforeHandleId = beforeHandleId;
    }

    public String getSol1Amount() {
        return sol1Amount;
    }

    public void setSol1Amount(String sol1Amount) {
        this.sol1Amount = sol1Amount;
    }

    public String getSol2Amount() {
        return sol2Amount;
    }

    public void setSol2Amount(String sol2Amount) {
        this.sol2Amount = sol2Amount;
    }

    public String getProjNo() {
        return projNo;
    }

    public void setProjNo(String projNo) {
        this.projNo = projNo;
    }

    public LocalDateTime getWeightTime() {
        return weightTime;
    }

    public void setWeightTime(LocalDateTime weightTime) {
        this.weightTime = weightTime;
    }

    public Long getWeighingReportLineId() {
        return weighingReportLineId;
    }

    public void setWeighingReportLineId(Long weighingReportLineId) {
        this.weighingReportLineId = weighingReportLineId;
    }

    public String getUndoReason() {
        return undoReason;
    }

    public void setUndoReason(String undoReason) {
        this.undoReason = undoReason;
    }

    private String undoFlag;
    private String stNo;
    private String stConcent;
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate stValiDate;
    private String stValiDateStr;

    public String getUndoFlag() {
        return this.undoFlag;
    }
    public void setUndoFlag(String undoFlag) {
        this.undoFlag = undoFlag;
    }
    public String getStNo() {
        return this.stNo;
    }
    public void setStNo(String stNo) {
        this.stNo = stNo;
    }
    public String getStConcent() {
        return this.stConcent;
    }
    public void setStConcent(String stConcent) {
        this.stConcent = stConcent;
    }
    public LocalDate getStValiDate() {
        return this.stValiDate;
    }
    public void setStValiDate(LocalDate stValiDate) {
        this.stValiDate = stValiDate;
    }
    public String getStValiDateStr() {
        return this.stValiDateStr;
    }
    public void setStValiDateStr(String stValiDateStr) {
        this.stValiDateStr = stValiDateStr;
    }
}
