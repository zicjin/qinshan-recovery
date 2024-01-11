package com.sunwayworld.emc.module.bp.project.project.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 项目其他费用
 * @author zhengxy@sunwayworld.com 2023年11月13日
 */
@Table("T_EMC_PROJECT_OTHER_FEE")
public class EmcProjectOtherFeeBean extends AbstractInsertable<Long> {

    private static final long serialVersionUID = -5025229505798241080L;

    @Id
    private Long id;  // 主键

    private Long projId; // 项目ID

    /** 费用类型 */
    private Integer feeType;

    /** 费用名称 */
    private String feeName;

    /** 费用单价 单价分 */
    private String feeSinglePrice;

    /** 费用数量 */
    private Integer feeCount;

    /** 费用金额 单位分 */
    private String amount;

    /** 费用状态(1->不可删除，0->可删除) */
    private String status;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjId() {
        return projId;
    }

    public void setProjId(Long projId) {
        this.projId = projId;
    }

    public Integer getFeeType() {
        return feeType;
    }

    public void setFeeType(Integer feeType) {
        this.feeType = feeType;
    }

    public String getFeeName() {
        return feeName;
    }

    public void setFeeName(String feeName) {
        this.feeName = feeName;
    }

    public String getFeeSinglePrice() {
        return feeSinglePrice;
    }

    public void setFeeSinglePrice(String feeSinglePrice) {
        this.feeSinglePrice = feeSinglePrice;
    }

    public Integer getFeeCount() {
        return feeCount;
    }

    public void setFeeCount(Integer feeCount) {
        this.feeCount = feeCount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
