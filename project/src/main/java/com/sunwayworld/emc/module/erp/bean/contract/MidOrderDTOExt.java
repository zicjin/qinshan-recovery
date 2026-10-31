package com.sunwayworld.emc.module.erp.bean.contract;

import java.util.List;

public class MidOrderDTOExt extends MidOrderDTO {

    /** 采样类型 */
    private Integer samplingType;

    /** 报告类型 1->普通检测数据报告，2->验收监测报告，3->其他 */
    private Integer reportType;

    /** 报告费用 */
    private Long reportFee;

    /** 保密度 1->低，2->中，3->高 */
    private Integer securityLevel;

    /** 父级行业群编号 */
    private String parentIndustryCode;

    /** 父级行业群名称 */
    private String parentIndustryName;

    /** 父级行业群唯一标识 */
    private String parentIndustryGbId;

    /** 中间行业群编号 */
    private String currentIndustryCode;

    /** 中间行业群名称 */
    private String currentIndustryName;

    /** 中间行业群唯一标识 */
    private String currentIndustryGbId;

    /** 子级行业群编号 */
    private String childIndustryCode;

    /** 子级行业群名称 */
    private String childIndustryName;

    /** 子级行业群唯一标识 */
    private String childIndustryGbId;

    /** 其他费用列表 */
    private List<SaveContractOrderOtherFeeReq> otherFeeList;

    /** 订单总工分 */
    private String workPoint;

    public Integer getSamplingType() {
        return samplingType;
    }

    public void setSamplingType(Integer samplingType) {
        this.samplingType = samplingType;
    }

    public Integer getReportType() {
        return reportType;
    }

    public void setReportType(Integer reportType) {
        this.reportType = reportType;
    }

    public Integer getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(Integer securityLevel) {
        this.securityLevel = securityLevel;
    }

    public String getParentIndustryCode() {
        return parentIndustryCode;
    }

    public void setParentIndustryCode(String parentIndustryCode) {
        this.parentIndustryCode = parentIndustryCode;
    }

    public String getParentIndustryName() {
        return parentIndustryName;
    }

    public void setParentIndustryName(String parentIndustryName) {
        this.parentIndustryName = parentIndustryName;
    }

    public String getCurrentIndustryCode() {
        return currentIndustryCode;
    }

    public void setCurrentIndustryCode(String currentIndustryCode) {
        this.currentIndustryCode = currentIndustryCode;
    }

    public String getCurrentIndustryName() {
        return currentIndustryName;
    }

    public void setCurrentIndustryName(String currentIndustryName) {
        this.currentIndustryName = currentIndustryName;
    }

    public String getChildIndustryCode() {
        return childIndustryCode;
    }

    public void setChildIndustryCode(String childIndustryCode) {
        this.childIndustryCode = childIndustryCode;
    }

    public String getChildIndustryName() {
        return childIndustryName;
    }

    public void setChildIndustryName(String childIndustryName) {
        this.childIndustryName = childIndustryName;
    }

    public List<SaveContractOrderOtherFeeReq> getOtherFeeList() {
        return otherFeeList;
    }

    public void setOtherFeeList(List<SaveContractOrderOtherFeeReq> otherFeeList) {
        this.otherFeeList = otherFeeList;
    }

    public String getWorkPoint() {
        return workPoint;
    }

    public void setWorkPoint(String workPoint) {
        this.workPoint = workPoint;
    }

    public Long getReportFee() {
        return reportFee;
    }

    public void setReportFee(Long reportFee) {
        this.reportFee = reportFee;
    }

    public String getParentIndustryGbId() {
        return parentIndustryGbId;
    }

    public void setParentIndustryGbId(String parentIndustryGbId) {
        this.parentIndustryGbId = parentIndustryGbId;
    }

    public String getCurrentIndustryGbId() {
        return currentIndustryGbId;
    }

    public void setCurrentIndustryGbId(String currentIndustryGbId) {
        this.currentIndustryGbId = currentIndustryGbId;
    }

    public String getChildIndustryGbId() {
        return childIndustryGbId;
    }

    public void setChildIndustryGbId(String childIndustryGbId) {
        this.childIndustryGbId = childIndustryGbId;
    }


}
