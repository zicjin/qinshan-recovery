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

@Table(value="T_EMC_OA_USER")
public class EmcOaUserBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = -7787129715263902672L;
    @Id
    private Long id;
    private String oaid;
    private String yggh;
    private String xm;
    private String xb;
    private LocalDateTime csrq;
    private String ygzt;
    private String yddh;
    private String dzyx;
    private String ssgs;
    private String ssbm;
    private String zwoabh;
    private String zwmc;
    private String gwoabh;
    private String gwmc;
    private String sjzg;
    private LocalDateTime updatetime;
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

    public String getOaid() {
        return this.oaid;
    }

    public void setOaid(String oaid) {
        this.oaid = oaid;
    }

    public String getYggh() {
        return this.yggh;
    }

    public void setYggh(String yggh) {
        this.yggh = yggh;
    }

    public String getXm() {
        return this.xm;
    }

    public void setXm(String xm) {
        this.xm = xm;
    }

    public String getXb() {
        return this.xb;
    }

    public void setXb(String xb) {
        this.xb = xb;
    }

    public LocalDateTime getCsrq() {
        return this.csrq;
    }

    public void setCsrq(LocalDateTime csrq) {
        this.csrq = csrq;
    }

    public String getYgzt() {
        return this.ygzt;
    }

    public void setYgzt(String ygzt) {
        this.ygzt = ygzt;
    }

    public String getYddh() {
        return this.yddh;
    }

    public void setYddh(String yddh) {
        this.yddh = yddh;
    }

    public String getDzyx() {
        return this.dzyx;
    }

    public void setDzyx(String dzyx) {
        this.dzyx = dzyx;
    }

    public String getSsgs() {
        return this.ssgs;
    }

    public void setSsgs(String ssgs) {
        this.ssgs = ssgs;
    }

    public String getSsbm() {
        return this.ssbm;
    }

    public void setSsbm(String ssbm) {
        this.ssbm = ssbm;
    }

    public String getZwoabh() {
        return this.zwoabh;
    }

    public void setZwoabh(String zwoabh) {
        this.zwoabh = zwoabh;
    }

    public String getZwmc() {
        return this.zwmc;
    }

    public void setZwmc(String zwmc) {
        this.zwmc = zwmc;
    }

    public String getGwoabh() {
        return this.gwoabh;
    }

    public void setGwoabh(String gwoabh) {
        this.gwoabh = gwoabh;
    }

    public String getGwmc() {
        return this.gwmc;
    }

    public void setGwmc(String gwmc) {
        this.gwmc = gwmc;
    }

    public String getSjzg() {
        return this.sjzg;
    }

    public void setSjzg(String sjzg) {
        this.sjzg = sjzg;
    }

    public LocalDateTime getUpdatetime() {
        return this.updatetime;
    }

    public void setUpdatetime(LocalDateTime updatetime) {
        this.updatetime = updatetime;
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
