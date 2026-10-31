package com.sunwayworld.emc.module.bp.order.orderretain.bean.vo;

import java.time.LocalDate;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.emc.module.bp.order.orderretain.bean.EmcOrderRetainBean;

public class EmcOrderRetainOverdueBean extends EmcOrderRetainBean {

	private static final long serialVersionUID = 4426234282614319593L;

	private String projNo;
    private String projName;
    private String folderNo;
    private String orderNo;
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String sampleCompleteDate;
    private String retainUserName;
    private String handleUserName;

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate retainDate;
    private String retainIndex;
    private String isRetain;
    private String handleUserNames;

    public String getProjNo() {
        return projNo;
    }

    public void setProjNo(String projNo) {
        this.projNo = projNo;
    }

    public String getProjName() {
        return projName;
    }

    public void setProjName(String projName) {
        this.projName = projName;
    }

    public String getFolderNo() {
        return folderNo;
    }

    public void setFolderNo(String folderNo) {
        this.folderNo = folderNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getSampleCompleteDate() {
        return sampleCompleteDate;
    }

    public void setSampleCompleteDate(String sampleCompleteDate) {
        this.sampleCompleteDate = sampleCompleteDate;
    }

    public String getRetainUserName() {
        return retainUserName;
    }

    public void setRetainUserName(String retainUserName) {
        this.retainUserName = retainUserName;
    }

    public String getHandleUserName() {
        return handleUserName;
    }

    public void setHandleUserName(String handleUserName) {
        this.handleUserName = handleUserName;
    }

    public LocalDate getRetainDate() {
        return retainDate;
    }

    public void setRetainDate(LocalDate retainDate) {
        this.retainDate = retainDate;
    }

    public String getRetainIndex() {
        return retainIndex;
    }

    public void setRetainIndex(String retainIndex) {
        this.retainIndex = retainIndex;
    }

    public String getIsRetain() {
        return isRetain;
    }

    public void setIsRetain(String isRetain) {
        this.isRetain = isRetain;
    }

    public String getHandleUserNames() {
        return handleUserNames;
    }

    public void setHandleUserNames(String handleUserNames) {
        this.handleUserNames = handleUserNames;
    }
}
