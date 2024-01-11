package com.sunwayworld.emc.module.item.qualitysupervision.bean;


import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;



/**
 * 质量监督计划内容
 * @author zhangft@sunwayworld.com
 * @date 2019-12-10
 */
@Table("T_EMC_QUALITY_SUPERVISION_CONT")
public class EmcQualitySupervisionContBean extends AbstractPersistable<Long> implements Insertable<Long>{

    @Transient
    private static final long serialVersionUID = 2495347584365048445L;

    @Id
    private Long id;//主键
    private Long planId;//计划编号
    private String contProject;//项目
    private String contResult;//结果
    private String conNote;//备注
    private String createdById;// 制单人编码
    private String createdByName;// 制单人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间
    private String createdByOrgId;// 制单人单位编码
    private String createdByOrgName;// 制单人单位名称

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }
    public String getContProject() {
        return contProject;
    }

    public void setContProject(String contProject) {
        this.contProject = contProject;
    }
    public String getContResult() {
        return contResult;
    }

    public void setContResult(String contResult) {
        this.contResult = contResult;
    }
    public String getConNote() {
        return conNote;
    }

    public void setConNote(String conNote) {
        this.conNote = conNote;
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

}
