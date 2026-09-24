// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.module.bp.project.project.bean;

import com.alibaba.fastjson.annotation.JSONField;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public class NcProjectReqBean {
    private String commission_no;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime commission_date;
    private String commission_unit;
    private String listno;
    private String reportman;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportindate;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportoutdate;
    private String mission_mny;
    private String reportnum;
    private String remark;
    private String creator;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationtime;
    private String contractno;
    private String confirmno;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime confirmdate;
    private String contractunit;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime settlestartdate;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime settleenddate;
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime signbackdate;
    private String confirmcycle;
    private String pk_psndoc;
    private String workloadno;
    private String testdept;
    private String type;
    private String testproject;
    private String testparameter;
    private String projectid;
    private String sub_project;
    private String contractsubject;
    private String adjustment;
    private String jcoriginalprice;
    private String orgcode;
    private String orgname;
    private String deptcode;
    private String jclxid;
    private String jclxcode;
    private String jclxname;

    public String getCommission_no() {
        return this.commission_no;
    }

    public void setCommission_no(String commission_no) {
        this.commission_no = commission_no;
    }

    public LocalDateTime getCommission_date() {
        return this.commission_date;
    }

    public void setCommission_date(LocalDateTime commission_date) {
        this.commission_date = commission_date;
    }

    public String getCommission_unit() {
        return this.commission_unit;
    }

    public void setCommission_unit(String commission_unit) {
        this.commission_unit = commission_unit;
    }

    public String getListno() {
        return this.listno;
    }

    public void setListno(String listno) {
        this.listno = listno;
    }

    public String getReportman() {
        return this.reportman;
    }

    public void setReportman(String reportman) {
        this.reportman = reportman;
    }

    public LocalDateTime getReportindate() {
        return this.reportindate;
    }

    public void setReportindate(LocalDateTime reportindate) {
        this.reportindate = reportindate;
    }

    public LocalDateTime getReportoutdate() {
        return this.reportoutdate;
    }

    public void setReportoutdate(LocalDateTime reportoutdate) {
        this.reportoutdate = reportoutdate;
    }

    public String getMission_mny() {
        return this.mission_mny;
    }

    public void setMission_mny(String mission_mny) {
        this.mission_mny = mission_mny;
    }

    public String getReportnum() {
        return this.reportnum;
    }

    public void setReportnum(String reportnum) {
        this.reportnum = reportnum;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public LocalDateTime getCreationtime() {
        return this.creationtime;
    }

    public void setCreationtime(LocalDateTime creationtime) {
        this.creationtime = creationtime;
    }

    public String getContractno() {
        return this.contractno;
    }

    public void setContractno(String contractno) {
        this.contractno = contractno;
    }

    public String getConfirmno() {
        return this.confirmno;
    }

    public void setConfirmno(String confirmno) {
        this.confirmno = confirmno;
    }

    public LocalDateTime getConfirmdate() {
        return this.confirmdate;
    }

    public void setConfirmdate(LocalDateTime confirmdate) {
        this.confirmdate = confirmdate;
    }

    public String getContractunit() {
        return this.contractunit;
    }

    public void setContractunit(String contractunit) {
        this.contractunit = contractunit;
    }

    public LocalDateTime getSettlestartdate() {
        return this.settlestartdate;
    }

    public void setSettlestartdate(LocalDateTime settlestartdate) {
        this.settlestartdate = settlestartdate;
    }

    public LocalDateTime getSettleenddate() {
        return this.settleenddate;
    }

    public void setSettleenddate(LocalDateTime settleenddate) {
        this.settleenddate = settleenddate;
    }

    public LocalDateTime getSignbackdate() {
        return this.signbackdate;
    }

    public void setSignbackdate(LocalDateTime signbackdate) {
        this.signbackdate = signbackdate;
    }

    public String getConfirmcycle() {
        return this.confirmcycle;
    }

    public void setConfirmcycle(String confirmcycle) {
        this.confirmcycle = confirmcycle;
    }

    public String getPk_psndoc() {
        return this.pk_psndoc;
    }

    public void setPk_psndoc(String pk_psndoc) {
        this.pk_psndoc = pk_psndoc;
    }

    public String getWorkloadno() {
        return this.workloadno;
    }

    public void setWorkloadno(String workloadno) {
        this.workloadno = workloadno;
    }

    public String getTestdept() {
        return this.testdept;
    }

    public void setTestdept(String testdept) {
        this.testdept = testdept;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTestproject() {
        return this.testproject;
    }

    public void setTestproject(String testproject) {
        this.testproject = testproject;
    }

    public String getTestparameter() {
        return this.testparameter;
    }

    public void setTestparameter(String testparameter) {
        this.testparameter = testparameter;
    }

    public String getProjectid() {
        return this.projectid;
    }

    public void setProjectid(String projectid) {
        this.projectid = projectid;
    }

    public String getSub_project() {
        return this.sub_project;
    }

    public void setSub_project(String sub_project) {
        this.sub_project = sub_project;
    }

    public String getContractsubject() {
        return this.contractsubject;
    }

    public void setContractsubject(String contractsubject) {
        this.contractsubject = contractsubject;
    }

    public String getAdjustment() {
        return this.adjustment;
    }

    public void setAdjustment(String adjustment) {
        this.adjustment = adjustment;
    }

    public String getJcoriginalprice() {
        return this.jcoriginalprice;
    }

    public void setJcoriginalprice(String jcoriginalprice) {
        this.jcoriginalprice = jcoriginalprice;
    }

    public String getOrgcode() {
        return this.orgcode;
    }

    public void setOrgcode(String orgcode) {
        this.orgcode = orgcode;
    }

    public String getOrgname() {
        return this.orgname;
    }

    public void setOrgname(String orgname) {
        this.orgname = orgname;
    }

    public String getDeptcode() {
        return this.deptcode;
    }

    public void setDeptcode(String deptcode) {
        this.deptcode = deptcode;
    }

    public String getJclxid() {
        return this.jclxid;
    }

    public void setJclxid(String jclxid) {
        this.jclxid = jclxid;
    }

    public String getJclxcode() {
        return this.jclxcode;
    }

    public void setJclxcode(String jclxcode) {
        this.jclxcode = jclxcode;
    }

    public String getJclxname() {
        return this.jclxname;
    }

    public void setJclxname(String jclxname) {
        this.jclxname = jclxname;
    }
}
