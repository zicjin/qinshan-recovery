package com.sunwayworld.emc.module.bp.result.equiptcurve.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 仪器曲线表
 *
 * @author zhw@sunwayworld.com
 * @date 2021-03-08
 */
@Table("T_EMC_EQUIPT_CURVE")
public class EmcEquiptCurveBean extends AbstractInsertable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = -2324706734139276897L;

    @Id
    private Long id;// 主键
    private String equiptCurveNo;// 仪器曲线编码
    private Long equiptId;// 仪器编码
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime confDate;// 标液配制日期
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime analysisDate;//曲线分析日期
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireDate;// 过期日期
    private String markingNo;// 标液编号
    private String markingName;// 标液名称
    private String markingConcent;// 标准使用液浓度
    private String markingUnitName;// 标液单位
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime markingValidate;// 标液有效期
    private String markingManufacturer;// 标液生产单位
    private String useConcent;// 使用液浓度
    private String useConcentUnitName;// 使用液浓度单位
    private String colorVolume;// 显色体积
    private String solConf;// 标准溶液配置
    private String curveCondition;// 曲线绘制条件
    private String diluent;// 稀释剂
    private String constantVolume;// 定然体积
    private String constantVolumeUnit;// 定容体积单位
    private String curveConfigText;// 曲线标液配置文本框
    private String markingReceiveno;//标液领用编号
    private String status;// 状态
    private String remark;// 备注
    private String createdById;// 制单人编码
    private String createdByName;// 制单人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间
    private String createdByOrgId;// 制单人单位编码
    private String createdByOrgName;// 制单人单位名称
    private String testNo;//关联测试编号
    private String testName;//关联测试名称
    private String slope;// 斜率
    private String intercept;// 截距
    private String coefficient;// 相关系数

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEquiptCurveNo() {
        return equiptCurveNo;
    }

    public void setEquiptCurveNo(String equiptCurveNo) {
        this.equiptCurveNo = equiptCurveNo;
    }

    public Long getEquiptId() {
        return equiptId;
    }

    public void setEquiptId(Long equiptId) {
        this.equiptId = equiptId;
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

    public LocalDateTime getMarkingValidate() {
        return markingValidate;
    }

    public void setMarkingValidate(LocalDateTime markingValidate) {
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

    public String getDiluent() {
        return diluent;
    }

    public void setDiluent(String diluent) {
        this.diluent = diluent;
    }

    public String getConstantVolume() {
        return constantVolume;
    }

    public void setConstantVolume(String constantVolume) {
        this.constantVolume = constantVolume;
    }

    public String getConstantVolumeUnit() {
        return constantVolumeUnit;
    }

    public void setConstantVolumeUnit(String constantVolumeUnit) {
        this.constantVolumeUnit = constantVolumeUnit;
    }

    public String getCurveConfigText() {
        return curveConfigText;
    }

    public void setCurveConfigText(String curveConfigText) {
        this.curveConfigText = curveConfigText;
    }

    public String getMarkingReceiveno() {
        return markingReceiveno;
    }

    public void setMarkingReceiveno(String markingReceiveno) {
        this.markingReceiveno = markingReceiveno;
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

    public String getTestNo() {
        return testNo;
    }

    public void setTestNo(String testNo) {
        this.testNo = testNo;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getSlope() {
        return slope;
    }

    public void setSlope(String slope) {
        this.slope = slope;
    }

    public String getIntercept() {
        return intercept;
    }

    public void setIntercept(String intercept) {
        this.intercept = intercept;
    }

    public String getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(String coefficient) {
        this.coefficient = coefficient;
    }

    public LocalDateTime getAnalysisDate() {
        return analysisDate;
    }

    public void setAnalysisDate(LocalDateTime analysisDate) {
        this.analysisDate = analysisDate;
    }
}
