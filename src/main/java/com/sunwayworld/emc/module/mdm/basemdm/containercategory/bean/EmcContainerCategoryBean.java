package com.sunwayworld.emc.module.mdm.basemdm.containercategory.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 容器类型
 * 
 * @author zmagt@sunwayworld.com 2019/9/5
 *
 */
@Table("T_EMC_CONTAINER_CATEGORY")
public class EmcContainerCategoryBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = -8497351073078370562L;

    @Id
    private Long id; // 主键
    private String containerCategoryId; // 容器类型编码
    private String containerCategoryName; // 容器类型名称
    private String capacity; // 容量
    private String color; // 颜色
    private String material; // 材质
    private String sampleBottleCaliber; // 采样瓶口径
    private String bottleneckProcess; // 瓶口处理工艺
    private String remark; // 备注

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContainerCategoryId() {
        return containerCategoryId;
    }

    public void setContainerCategoryId(String containerCategoryId) {
        this.containerCategoryId = containerCategoryId;
    }

    public String getContainerCategoryName() {
        return containerCategoryName;
    }

    public void setContainerCategoryName(String containerCategoryName) {
        this.containerCategoryName = containerCategoryName;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getSampleBottleCaliber() {
        return sampleBottleCaliber;
    }

    public void setSampleBottleCaliber(String sampleBottleCaliber) {
        this.sampleBottleCaliber = sampleBottleCaliber;
    }

    public String getBottleneckProcess() {
        return bottleneckProcess;
    }

    public void setBottleneckProcess(String bottleneckProcess) {
        this.bottleneckProcess = bottleneckProcess;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
