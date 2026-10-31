package com.sunwayworld.emc.module.mdm.contract.bean;

import java.time.LocalDateTime;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 合同评审基本信息表
 * @author zhengxy@sunwayworld.com 2020年11月01日
 */
@Table("T_EMC_REVIEW_BASIC_INFO")
public class EmcReviewBasicInfoBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = -6466038005874977964L;

    @Id
    private Long id;  // 主键
    private Long contractId;  // 合同ID
    private String reviewTitle;  // 任务名称
    private String reviewOrgId;  // 评审部门ID
    private String reviewOrgName;  // 评审部门名称
    private String reviewUserId;  // 参会人员ID
    private String reviewUserName;  // 参会人员名称
    private LocalDateTime reviewTime;  // 评审时间

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getReviewTitle() {
        return reviewTitle;
    }

    public void setReviewTitle(String reviewTitle) {
        this.reviewTitle = reviewTitle;
    }

    public String getReviewOrgId() {
        return reviewOrgId;
    }

    public void setReviewOrgId(String reviewOrgId) {
        this.reviewOrgId = reviewOrgId;
    }

    public String getReviewOrgName() {
        return reviewOrgName;
    }

    public void setReviewOrgName(String reviewOrgName) {
        this.reviewOrgName = reviewOrgName;
    }

    public String getReviewUserId() {
        return reviewUserId;
    }

    public void setReviewUserId(String reviewUserId) {
        this.reviewUserId = reviewUserId;
    }

    public String getReviewUserName() {
        return reviewUserName;
    }

    public void setReviewUserName(String reviewUserName) {
        this.reviewUserName = reviewUserName;
    }

    public LocalDateTime getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(LocalDateTime reviewTime) {
        this.reviewTime = reviewTime;
    }

}
