package com.sunwayworld.emc.module.mdm.equipt.equipt.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 设备-DCU配置表
 *
 */
@Table("T_EMC_EQUIPT_DCU_CONF")
public class EmcEquiptDCUConfBean extends AbstractPersistable<Long> implements Insertable<Long> {


    @Transient
    private static final long serialVersionUID = -3728539419726097089L;

    @Id
    private Long id; // 主键 同设备台账ID

    private Long acqScriptId; // 采集脚本ID

    private String interfaceCategory; // 接口类型 下拉列表 串口采集、文件采集

    private String baudRate; // 波特率 下拉列表
                             // 300、1200、2400、4800、9600、19200、38400、57600、115200、230400、460800、921600

    private String terminator; // 终止字符串

    private String dataBit; // 数据位 下拉列表 7、8

    private String parityCheck; // 奇偶校验 下拉列表 None、Odd、Even

    private String com; // COM口 下拉列表
                        // COM1、COM2、COM3、COM4、COM5、COM6、COM7、COM8、COM9

    private String stopBit; // 停止位 下拉列表 1、2

    private String beginSymbol; // 开始标记

    private String endSymbol; // 结束标记

    private String analyticFormula; // 数据解析公式

    private String ip; // ip地址

    private String createdById; // 制单人编码

    private String createdByName; // 制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime; // 制单时间

    private String createdByOrgId; // 制单人单位编码

    private String createdByOrgName; // 制单人单位名称

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInterfaceCategory() {
        return interfaceCategory;
    }

    public void setInterfaceCategory(String interfaceCategory) {
        this.interfaceCategory = interfaceCategory;
    }

    public String getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(String baudRate) {
        this.baudRate = baudRate;
    }

    public String getTerminator() {
        return terminator;
    }

    public void setTerminator(String terminator) {
        this.terminator = terminator;
    }

    public String getDataBit() {
        return dataBit;
    }

    public void setDataBit(String dataBit) {
        this.dataBit = dataBit;
    }

    public String getParityCheck() {
        return parityCheck;
    }

    public void setParityCheck(String parityCheck) {
        this.parityCheck = parityCheck;
    }

    public String getCom() {
        return com;
    }

    public void setCom(String com) {
        this.com = com;
    }

    public String getStopBit() {
        return stopBit;
    }

    public void setStopBit(String stopBit) {
        this.stopBit = stopBit;
    }

    public String getAnalyticFormula() {
        return analyticFormula;
    }

    public void setAnalyticFormula(String analyticFormula) {
        this.analyticFormula = analyticFormula;
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getAcqScriptId() {
        return acqScriptId;
    }

    public void setAcqScriptId(Long acqScriptId) {
        this.acqScriptId = acqScriptId;
    }

    public String getBeginSymbol() {
        return beginSymbol;
    }

    public void setBeginSymbol(String beginSymbol) {
        this.beginSymbol = beginSymbol;
    }

    public String getEndSymbol() {
        return endSymbol;
    }

    public void setEndSymbol(String endSymbol) {
        this.endSymbol = endSymbol;
    }

}
