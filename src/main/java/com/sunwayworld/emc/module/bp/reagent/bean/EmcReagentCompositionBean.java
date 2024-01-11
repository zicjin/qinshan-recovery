package com.sunwayworld.emc.module.bp.reagent.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 一般配制试剂组分
 * @author zhengxy@sunwayworld.com 2020年06月18日
 */
@Table("T_EMC_REAGENT_COMPOSITION")
public class EmcReagentCompositionBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = -1758443400532917318L;

    @Id
    private Long id;  // 主键
    private Long reagentId;  // 试剂ID
    private String reagentName;  // 试剂名称
    private String reagentFactory;  // 试剂厂家
    private String reagentBatchNo;  // 试剂批号
    private String weightVolume;  // 试剂重量/体积
    private String remark;  // 备注

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getReagentId() {
        return reagentId;
    }

    public void setReagentId(Long reagentId) {
        this.reagentId = reagentId;
    }

    public String getReagentName() {
        return reagentName;
    }

    public void setReagentName(String reagentName) {
        this.reagentName = reagentName;
    }

    public String getReagentFactory() {
        return reagentFactory;
    }

    public void setReagentFactory(String reagentFactory) {
        this.reagentFactory = reagentFactory;
    }

    public String getReagentBatchNo() {
        return reagentBatchNo;
    }

    public void setReagentBatchNo(String reagentBatchNo) {
        this.reagentBatchNo = reagentBatchNo;
    }

    public String getWeightVolume() {
        return weightVolume;
    }

    public void setWeightVolume(String weightVolume) {
        this.weightVolume = weightVolume;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
