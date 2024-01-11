package com.sunwayworld.emc.module.bp.project.projscheme.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 监测方案样品表
 *
 * @author yuanhh@sunwayworld.com
 * @date 2019-10-08
 */

@Table("T_EMC_PROJ_ORDER")
public class EmcProjOrderBean extends AbstractPersistable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = 6428765035601708948L;

    @Id
    private Long id;// 主键

    private Long projSchemeId;// 项目监测方案编码

    private Long sampleNum;// 采样个数

    private Long testId;// 测试ID

    private String schemeName;// 监测方案名称

    private Long methodId;// 方法编码

    private String subPackageName;// 分包商名称

    private Long itemGroupId;// 分析项组编码

    private String itemGroup;// 分析项组

    private Long indexCategoryId;// 指标分类id

    private String indexId;// 样品指标id

    private Long subcontractorId;// 分包商ID

    private String subPackageNo;// 分包单号

    @NotNull(defaultValue = Constant.NO)
    private String dataSharing;// 是否数据共享

    private Double sampleFee;// 采样费

    private Double analysisFee;// 分析费

    private String createdById;// 制单人编码

    private String createdByName;// 制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间

    private String createdByOrgId;// 制单人单位编码

    private String createdByOrgName;// 制单人单位名称

    private String indexSetId;// 指标设置ID(江苏新锐)

    private String emissionLowlimit;// 排污许可低限(江苏新锐)

    private String emissionHighlimit;// 排污许可高限(江苏新锐)

    /**
     * 广州分析测试中心项目
     */
    @NotNull(defaultValue = Constant.NO)
    private String customerSpecified;// 是否客户指定

    private Long sortNum; // 排序码

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjSchemeId() {
        return projSchemeId;
    }

    public void setProjSchemeId(Long projSchemeId) {
        this.projSchemeId = projSchemeId;
    }

    public Long getSampleNum() {
        return sampleNum;
    }

    public void setSampleNum(Long sampleNum) {
        this.sampleNum = sampleNum;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public Long getMethodId() {
        return methodId;
    }

    public void setMethodId(Long methodId) {
        this.methodId = methodId;
    }

    public String getSubPackageName() {
        return subPackageName;
    }

    public void setSubPackageName(String subPackageName) {
        this.subPackageName = subPackageName;
    }

    public Long getItemGroupId() {
        return itemGroupId;
    }

    public void setItemGroupId(Long itemGroupId) {
        this.itemGroupId = itemGroupId;
    }

    public String getItemGroup() {
        return itemGroup;
    }

    public void setItemGroup(String itemGroup) {
        this.itemGroup = itemGroup;
    }

    public Long getIndexCategoryId() {
        return indexCategoryId;
    }

    public void setIndexCategoryId(Long indexCategoryId) {
        this.indexCategoryId = indexCategoryId;
    }

    public String getIndexId() {
        return indexId;
    }

    public void setIndexId(String indexId) {
        this.indexId = indexId;
    }

    public Double getSampleFee() {
        return sampleFee;
    }

    public void setSampleFee(Double sampleFee) {
        this.sampleFee = sampleFee;
    }

    public Double getAnalysisFee() {
        return analysisFee;
    }

    public void setAnalysisFee(Double analysisFee) {
        this.analysisFee = analysisFee;
    }

    public Long getSubcontractorId() {
        return subcontractorId;
    }

    public void setSubcontractorId(Long subcontractorId) {
        this.subcontractorId = subcontractorId;
    }

    public String getSubPackageNo() {
        return subPackageNo;
    }

    public void setSubPackageNo(String subPackageNo) {
        this.subPackageNo = subPackageNo;
    }

    public String getDataSharing() {
        return dataSharing;
    }

    public void setDataSharing(String dataSharing) {
        this.dataSharing = dataSharing;
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

    public String getCustomerSpecified() {
        return customerSpecified;
    }

    public void setCustomerSpecified(String customerSpecified) {
        this.customerSpecified = customerSpecified;
    }

    public String getEmissionLowlimit() {
        return emissionLowlimit;
    }

    public void setEmissionLowlimit(String emissionLowlimit) {
        this.emissionLowlimit = emissionLowlimit;
    }

    public String getEmissionHighlimit() {
        return emissionHighlimit;
    }

    public void setEmissionHighlimit(String emissionHighlimit) {
        this.emissionHighlimit = emissionHighlimit;
    }

    public String getIndexSetId() {
        return indexSetId;
    }

    public void setIndexSetId(String indexSetId) {
        this.indexSetId = indexSetId;
    }

    public Long getSortNum() {
        return sortNum;
    }

    public void setSortNum(Long sortNum) {
        this.sortNum = sortNum;
    }
}
