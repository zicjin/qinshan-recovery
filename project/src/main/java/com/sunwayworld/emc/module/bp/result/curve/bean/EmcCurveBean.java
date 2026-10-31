package com.sunwayworld.emc.module.bp.result.curve.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 曲线表
 *
 * @author yuanhh@sunwayworld.com
 * @date 2019-11-12
 */

@Table("T_EMC_CURVE")
public class EmcCurveBean extends AbstractPersistable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = -7882799915127508135L;

    @Id
    private Long id;// 主键

    private String curveNo;// 曲线编号

    private Long folderId;// 点位编码

    private Long equiptId;// 仪器编码

    private Long testId;// 测试编码

    private String testName;// 测试名称

    private Long methodId;// 方法编码

    private String methodName;// 方法名称

    private Long itemId;// 分析项编码

    private String itemName;// 分析项名称

    private String intercept;// 截距

    private String slope;// 斜率

    private String coefficient;// 相关系数

    private String curveType;// 曲线类型（0手工1仪器）

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime confDate;// 配置日期

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireDate;// 过期日期

    private String markingNo;// 标液编号

    private String markingName;// 标液名称

    private String markingConcent;// 标液浓度

    private String markingUnitName;// 标液单位

    private Long markingValidate;// 标液有效期

    private String markingManufacturer;// 标液生产单位

    private String useConcent;// 使用液浓度

    private String useConcentUnitName;// 使用液浓度单位

    private String colorVolume;// 显色体积

    private String solConf;// 标准溶液配制

    private String curveCondition;// 曲线绘制条件

    private String status;// 状态

    private String remark;// 备注

    private String curveCategory; // 曲线类型

    private String createdById;// 制单人编码

    private String createdByName;// 制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间

    private String createdByOrgId;// 制单人单位编码

    private String createdByOrgName;// 制单人单位名称

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurveNo() {
        return curveNo;
    }

    public void setCurveNo(String curveNo) {
        this.curveNo = curveNo;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public Long getEquiptId() {
        return equiptId;
    }

    public void setEquiptId(Long equiptId) {
        this.equiptId = equiptId;
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

    public Long getMethodId() {
        return methodId;
    }

    public void setMethodId(Long methodId) {
        this.methodId = methodId;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getCurveType() {
        return curveType;
    }

    public void setCurveType(String curveType) {
        this.curveType = curveType;
    }

    public String getIntercept() {
        return intercept;
    }

    public void setIntercept(String intercept) {
        this.intercept = intercept;
    }

    public String getSlope() {
        return slope;
    }

    public void setSlope(String slope) {
        this.slope = slope;
    }

    public String getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(String coefficient) {
        this.coefficient = coefficient;
    }

    public LocalDateTime getConfDate() {
        return confDate;
    }

    public void setConfDate(LocalDateTime confDate) {
        this.confDate = confDate;
    }

    public LocalDateTime getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDateTime expireDate) {
        this.expireDate = expireDate;
    }

    public String getMarkingNo() {
        return markingNo;
    }

    public void setMarkingNo(String markingNo) {
        this.markingNo = markingNo;
    }

    public String getMarkingName() {
        return markingName;
    }

    public void setMarkingName(String markingName) {
        this.markingName = markingName;
    }

    public String getMarkingConcent() {
        return markingConcent;
    }

    public void setMarkingConcent(String markingConcent) {
        this.markingConcent = markingConcent;
    }

    public String getMarkingUnitName() {
        return markingUnitName;
    }

    public void setMarkingUnitName(String markingUnitName) {
        this.markingUnitName = markingUnitName;
    }

    public Long getMarkingValidate() {
        return markingValidate;
    }

    public void setMarkingValidate(Long markingValidate) {
        this.markingValidate = markingValidate;
    }

    public String getMarkingManufacturer() {
        return markingManufacturer;
    }

    public void setMarkingManufacturer(String markingManufacturer) {
        this.markingManufacturer = markingManufacturer;
    }

    public String getUseConcent() {
        return useConcent;
    }

    public void setUseConcent(String useConcent) {
        this.useConcent = useConcent;
    }

    public String getUseConcentUnitName() {
        return useConcentUnitName;
    }

    public void setUseConcentUnitName(String useConcentUnitName) {
        this.useConcentUnitName = useConcentUnitName;
    }

    public String getColorVolume() {
        return colorVolume;
    }

    public void setColorVolume(String colorVolume) {
        this.colorVolume = colorVolume;
    }

    public String getSolConf() {
        return solConf;
    }

    public void setSolConf(String solConf) {
        this.solConf = solConf;
    }

    public String getCurveCondition() {
        return curveCondition;
    }

    public void setCurveCondition(String curveCondition) {
        this.curveCondition = curveCondition;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCurveCategory() {
        return curveCategory;
    }

    public void setCurveCategory(String curveCategory) {
        this.curveCategory = curveCategory;
    }

    public String getCreatedById() {
        return createdById;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedByOrgId() {
        return createdByOrgId;
    }

    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    public String getCreatedByOrgName() {
        return createdByOrgName;
    }

    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }
}
