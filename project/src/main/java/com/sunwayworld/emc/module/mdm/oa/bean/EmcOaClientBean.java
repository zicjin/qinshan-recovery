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

@Table(value="T_EMC_OA_CLIENT")
public class EmcOaClientBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = -9209922186569159357L;
    @Id
    private Long id;
    private String bm;
    private String cjr;
    private LocalDateTime cjsj;
    private String gxczr;
    private LocalDateTime gxsj;
    private String sfdj;
    private String sfyx;
    private String dzyx;
    private String khybm;
    private String khmc;
    private String khlx;
    private String zwmc;
    private String sf;
    private String xypjdj;
    private String sjzg;
    private String sfgys;
    private String khcwfl;
    private String khcwlx;
    private String sffc;
    private String tyshxydm;
    private String qyzt;
    private String fddbr;
    private String fddbrlxfs;
    private LocalDateTime clrq;
    private LocalDateTime dxrq;
    private String djjg;
    private String zzjgdm;
    private String zczb;
    private String sjzb;
    private String qyfl;
    private String dz;
    private LocalDateTime yyksrq;
    private LocalDateTime yyjsrq;
    private LocalDateTime fzrq;
    private String sfss;
    private String sslx;
    private String gpdm;
    private String jyfw;
    private String khsfzh;
    private String khzbm;
    private String ywjl;
    private String jtmc;
    private String jtlb;
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

    public String getGxczr() {
        return this.gxczr;
    }

    public void setGxczr(String gxczr) {
        this.gxczr = gxczr;
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

    public String getKhybm() {
        return this.khybm;
    }

    public void setKhybm(String khybm) {
        this.khybm = khybm;
    }

    public String getKhmc() {
        return this.khmc;
    }

    public void setKhmc(String khmc) {
        this.khmc = khmc;
    }

    public String getKhlx() {
        return this.khlx;
    }

    public void setKhlx(String khlx) {
        this.khlx = khlx;
    }

    public String getZwmc() {
        return this.zwmc;
    }

    public void setZwmc(String zwmc) {
        this.zwmc = zwmc;
    }

    public String getSf() {
        return this.sf;
    }

    public void setSf(String sf) {
        this.sf = sf;
    }

    public String getXypjdj() {
        return this.xypjdj;
    }

    public void setXypjdj(String xypjdj) {
        this.xypjdj = xypjdj;
    }

    public String getSjzg() {
        return this.sjzg;
    }

    public void setSjzg(String sjzg) {
        this.sjzg = sjzg;
    }

    public String getSfgys() {
        return this.sfgys;
    }

    public void setSfgys(String sfgys) {
        this.sfgys = sfgys;
    }

    public String getKhcwfl() {
        return this.khcwfl;
    }

    public void setKhcwfl(String khcwfl) {
        this.khcwfl = khcwfl;
    }

    public String getKhcwlx() {
        return this.khcwlx;
    }

    public void setKhcwlx(String khcwlx) {
        this.khcwlx = khcwlx;
    }

    public String getSffc() {
        return this.sffc;
    }

    public void setSffc(String sffc) {
        this.sffc = sffc;
    }

    public String getTyshxydm() {
        return this.tyshxydm;
    }

    public void setTyshxydm(String tyshxydm) {
        this.tyshxydm = tyshxydm;
    }

    public String getQyzt() {
        return this.qyzt;
    }

    public void setQyzt(String qyzt) {
        this.qyzt = qyzt;
    }

    public String getFddbr() {
        return this.fddbr;
    }

    public void setFddbr(String fddbr) {
        this.fddbr = fddbr;
    }

    public String getFddbrlxfs() {
        return this.fddbrlxfs;
    }

    public void setFddbrlxfs(String fddbrlxfs) {
        this.fddbrlxfs = fddbrlxfs;
    }

    public LocalDateTime getClrq() {
        return this.clrq;
    }

    public void setClrq(LocalDateTime clrq) {
        this.clrq = clrq;
    }

    public LocalDateTime getDxrq() {
        return this.dxrq;
    }

    public void setDxrq(LocalDateTime dxrq) {
        this.dxrq = dxrq;
    }

    public String getDjjg() {
        return this.djjg;
    }

    public void setDjjg(String djjg) {
        this.djjg = djjg;
    }

    public String getZzjgdm() {
        return this.zzjgdm;
    }

    public void setZzjgdm(String zzjgdm) {
        this.zzjgdm = zzjgdm;
    }

    public String getZczb() {
        return this.zczb;
    }

    public void setZczb(String zczb) {
        this.zczb = zczb;
    }

    public String getSjzb() {
        return this.sjzb;
    }

    public void setSjzb(String sjzb) {
        this.sjzb = sjzb;
    }

    public String getQyfl() {
        return this.qyfl;
    }

    public void setQyfl(String qyfl) {
        this.qyfl = qyfl;
    }

    public String getDz() {
        return this.dz;
    }

    public void setDz(String dz) {
        this.dz = dz;
    }

    public LocalDateTime getYyksrq() {
        return this.yyksrq;
    }

    public void setYyksrq(LocalDateTime yyksrq) {
        this.yyksrq = yyksrq;
    }

    public LocalDateTime getYyjsrq() {
        return this.yyjsrq;
    }

    public void setYyjsrq(LocalDateTime yyjsrq) {
        this.yyjsrq = yyjsrq;
    }

    public LocalDateTime getFzrq() {
        return this.fzrq;
    }

    public void setFzrq(LocalDateTime fzrq) {
        this.fzrq = fzrq;
    }

    public String getSfss() {
        return this.sfss;
    }

    public void setSfss(String sfss) {
        this.sfss = sfss;
    }

    public String getSslx() {
        return this.sslx;
    }

    public void setSslx(String sslx) {
        this.sslx = sslx;
    }

    public String getGpdm() {
        return this.gpdm;
    }

    public void setGpdm(String gpdm) {
        this.gpdm = gpdm;
    }

    public String getJyfw() {
        return this.jyfw;
    }

    public void setJyfw(String jyfw) {
        this.jyfw = jyfw;
    }

    public String getKhsfzh() {
        return this.khsfzh;
    }

    public void setKhsfzh(String khsfzh) {
        this.khsfzh = khsfzh;
    }

    public String getKhzbm() {
        return this.khzbm;
    }

    public void setKhzbm(String khzbm) {
        this.khzbm = khzbm;
    }

    public String getYwjl() {
        return this.ywjl;
    }

    public void setYwjl(String ywjl) {
        this.ywjl = ywjl;
    }

    public String getJtmc() {
        return this.jtmc;
    }

    public void setJtmc(String jtmc) {
        this.jtmc = jtmc;
    }

    public String getJtlb() {
        return this.jtlb;
    }

    public void setJtlb(String jtlb) {
        this.jtlb = jtlb;
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
