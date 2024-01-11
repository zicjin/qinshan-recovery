package com.sunwayworld.emc.module.bp.result.equiptcurve.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.Insertable;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * 仪器曲线标液配置
 * 
 * @author zhw@sunwayworld.com
 * @date 2021-04-07
 */
@Table("T_EMC_EQUIPT_CURVE_STANDARD")
public class EmcEquiptCurveStandardBean extends AbstractInsertable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = 6101641812895008679L;

    @Id
    private Long id;// 主键
    private Long equiptCurveId;// 仪器曲线id
    private String curveConfigText;// 仪器曲线标液配置文本
    private String createdById;// 制单人编码
    private String createdByName;// 制单人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间
    private String createdByOrgId;// 制单人单位编码
    private String createdByOrgName;// 制单热单位名称

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEquiptCurveId() {
        return equiptCurveId;
    }

    public void setEquiptCurveId(Long equiptCurveId) {
        this.equiptCurveId = equiptCurveId;
    }

    public String getCurveConfigText() {
        return curveConfigText;
    }

    public void setCurveConfigText(String curveConfigText) {
        this.curveConfigText = curveConfigText;
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
