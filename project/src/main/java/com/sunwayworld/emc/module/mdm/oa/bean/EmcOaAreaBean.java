// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.mdm.oa.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_EMC_OA_AREA")
public class EmcOaAreaBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    private String bm;
    private String cjr;
    private LocalDateTime cjsj;
    private String czr;
    private LocalDateTime gxsj;
    private String sfdj;
    private String sfyx;
    private String dzyx;
    private String zddm;
    private String zdmc;
    private String xzqhdm;
    private String xzqhmc;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getBm() {
        return this.bm;
    }

    public void setBm(String bm) {
        this.bm = bm;
    }

    public String getCjr() {
        return this.cjr;
    }

    public void setCjr(String cjr) {
        this.cjr = cjr;
    }

    public LocalDateTime getCjsj() {
        return this.cjsj;
    }

    public void setCjsj(LocalDateTime cjsj) {
        this.cjsj = cjsj;
    }

    public String getCzr() {
        return this.czr;
    }

    public void setCzr(String czr) {
        this.czr = czr;
    }

    public LocalDateTime getGxsj() {
        return this.gxsj;
    }

    public void setGxsj(LocalDateTime gxsj) {
        this.gxsj = gxsj;
    }

    public String getSfdj() {
        return this.sfdj;
    }

    public void setSfdj(String sfdj) {
        this.sfdj = sfdj;
    }

    public String getSfyx() {
        return this.sfyx;
    }

    public void setSfyx(String sfyx) {
        this.sfyx = sfyx;
    }

    public String getDzyx() {
        return this.dzyx;
    }

    public void setDzyx(String dzyx) {
        this.dzyx = dzyx;
    }

    public String getZddm() {
        return this.zddm;
    }

    public void setZddm(String zddm) {
        this.zddm = zddm;
    }

    public String getZdmc() {
        return this.zdmc;
    }

    public void setZdmc(String zdmc) {
        this.zdmc = zdmc;
    }

    public String getXzqhdm() {
        return this.xzqhdm;
    }

    public void setXzqhdm(String xzqhdm) {
        this.xzqhdm = xzqhdm;
    }

    public String getXzqhmc() {
        return this.xzqhmc;
    }

    public void setXzqhmc(String xzqhmc) {
        this.xzqhmc = xzqhmc;
    }

    @Override
    public String getCreatedById() {
        return this.createdById;
    }

    @Override
    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    @Override
    public String getCreatedByName() {
        return this.createdByName;
    }

    @Override
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    @Override
    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    @Override
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String getCreatedByOrgId() {
        return this.createdByOrgId;
    }

    @Override
    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    @Override
    public String getCreatedByOrgName() {
        return this.createdByOrgName;
    }

    @Override
    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }
}
