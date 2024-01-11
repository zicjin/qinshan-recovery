package com.sunwayworld.emc.module.mdm.basemdm.sealrule.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 盖章打印规则详情
 * 
 * @author zhangheng@sunwayworld.com
 * @date 2022-03-16
 */
@Table("T_EMC_SEAL_RULE_DETAILS")
public class EmcSealRuleDetailsBean extends AbstractInsertable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = 1825881094425032439L;

    @Id
    private Long id;// 主键
    private Long sealRuleId;// 盖章打印规则id
    private String sealGroupNo;// 图章组编码
    private String sealGroupName;// 图章组名称
    private String sealNo;// 图章编码
    private String sealName;// 图章名称
    private String pageType;// 页码类型
    private String fixedPageNum;// 固定页码值
    private String customPageNum;// 自定义页码值
    private String xvalue;// X坐标
    private String yvalue;// Y坐标
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

    public Long getSealRuleId() {
        return sealRuleId;
    }

    public void setSealRuleId(Long sealRuleId) {
        this.sealRuleId = sealRuleId;
    }

    public String getSealGroupNo() {
        return sealGroupNo;
    }

    public void setSealGroupNo(String sealGroupNo) {
        this.sealGroupNo = sealGroupNo;
    }

    public String getSealGroupName() {
        return sealGroupName;
    }

    public void setSealGroupName(String sealGroupName) {
        this.sealGroupName = sealGroupName;
    }

    public String getSealNo() {
        return sealNo;
    }

    public void setSealNo(String sealNo) {
        this.sealNo = sealNo;
    }

    public String getSealName() {
        return sealName;
    }

    public void setSealName(String sealName) {
        this.sealName = sealName;
    }

    public String getPageType() {
        return pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    public String getFixedPageNum() {
        return fixedPageNum;
    }

    public void setFixedPageNum(String fixedPageNum) {
        this.fixedPageNum = fixedPageNum;
    }

    public String getCustomPageNum() {
        return customPageNum;
    }

    public void setCustomPageNum(String customPageNum) {
        this.customPageNum = customPageNum;
    }

    public String getXvalue() {
        return xvalue;
    }

    public void setXvalue(String xvalue) {
        this.xvalue = xvalue;
    }

    public String getYvalue() {
        return yvalue;
    }

    public void setYvalue(String yvalue) {
        this.yvalue = yvalue;
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
