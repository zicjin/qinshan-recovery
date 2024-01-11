package com.sunwayworld.emc.module.item.quotation.addprice.bean;

import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.support.domain.AbstractPersistable;

/**
 * 附加费用表
 * 
 * @author xiongjie@sunwayworld.com@sunwayworld.com
 * @date 2021-03-25
 */
@Table("T_EMC_QUOTATION_ADDPRICE")
public class EmcAddpriceBean extends AbstractPersistable<Long> implements Persistable<Long> {

    @Transient
    private static final long serialVersionUID = -82566196618711043L;

    @Id
    private Long id;// 主键
    private String priceName;// 费用名称
    private String price;// 单价
    private String remark;// 备注
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间
    private String createdByOrgId;// 制单人单位编码
    private String createdByOrgName;// 制单人单位名称

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPriceName() {
        return priceName;
    }

    public void setPriceName(String priceName) {
        this.priceName = priceName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

}
