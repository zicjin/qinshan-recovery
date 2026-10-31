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

@Table(value="T_EMC_OA_ORG")
public class EmcOaOrgBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = -8869517085527313226L;
    @Id
    private Long id;
    private String bm;
    private String cjr;
    private LocalDateTime cjsj;
    private String czr;
    private LocalDateTime gxss;
    private String sfdj;
    private String sfyx;
    private String zzbh;
    private String zzmc;
    private String zzfzr;
    private String zzlx;
    private String ssbk;
    private String sffc;
    private String sjzzbh;
    private String sjzzmc;
    private String sssyb;
    private String ssgfgsmc;
    private String ssgfgsbm;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;
    private String ssgfgscwbm;

    public String getSsgfgscwbm() {
        return this.ssgfgscwbm;
    }

    public void setSsgfgscwbm(String ssgfgscwbm) {
        this.ssgfgscwbm = ssgfgscwbm;
    }

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

    public LocalDateTime getGxss() {
        return this.gxss;
    }

    public void setGxss(LocalDateTime gxss) {
        this.gxss = gxss;
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

    public String getZzbh() {
        return this.zzbh;
    }

    public void setZzbh(String zzbh) {
        this.zzbh = zzbh;
    }

    public String getZzmc() {
        return this.zzmc;
    }

    public void setZzmc(String zzmc) {
        this.zzmc = zzmc;
    }

    public String getZzfzr() {
        return this.zzfzr;
    }

    public void setZzfzr(String zzfzr) {
        this.zzfzr = zzfzr;
    }

    public String getZzlx() {
        return this.zzlx;
    }

    public void setZzlx(String zzlx) {
        this.zzlx = zzlx;
    }

    public String getSsbk() {
        return this.ssbk;
    }

    public void setSsbk(String ssbk) {
        this.ssbk = ssbk;
    }

    public String getSffc() {
        return this.sffc;
    }

    public void setSffc(String sffc) {
        this.sffc = sffc;
    }

    public String getSjzzbh() {
        return this.sjzzbh;
    }

    public void setSjzzbh(String sjzzbh) {
        this.sjzzbh = sjzzbh;
    }

    public String getSjzzmc() {
        return this.sjzzmc;
    }

    public void setSjzzmc(String sjzzmc) {
        this.sjzzmc = sjzzmc;
    }

    public String getSssyb() {
        return this.sssyb;
    }

    public void setSssyb(String sssyb) {
        this.sssyb = sssyb;
    }

    public String getSsgfgsmc() {
        return this.ssgfgsmc;
    }

    public void setSsgfgsmc(String ssgfgsmc) {
        this.ssgfgsmc = ssgfgsmc;
    }

    public String getSsgfgsbm() {
        return this.ssgfgsbm;
    }

    public void setSsgfgsbm(String ssgfgsbm) {
        this.ssgfgsbm = ssgfgsbm;
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
