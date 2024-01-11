package com.sunwayworld.emc.module.mdm.basemdm.bizcategory.bean;

import java.time.LocalDateTime;

import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Activatable;

/**
 * @author huangc@sunwayworld.com
 * @date 2020-03-23
 */
@Table("T_EMC_BIZ_CATEGORY_PROJECT")
public class EmcBizCategoryProjectBean extends AbstractInsertable<Long> implements Activatable<Long> {

    @Transient
    private static final long serialVersionUID = 6840441629191743096L;

    @Id
    private Long id;// 主键
    private Long bizCategoryId;// 业务类型ID
    private String projName;// 项目名称
    @NotNull(defaultValue = Constant.ACTIVATED_STATUS_YES)
    private String activatedFlag;// 是否启用
    private String activatedById;// 启用人编码
    private String activatedByName;// 启用人名称
    private LocalDateTime activatedTime;// 启用时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBizCategoryId() {
        return bizCategoryId;
    }

    public void setBizCategoryId(Long bizCategoryId) {
        this.bizCategoryId = bizCategoryId;
    }

    public String getProjName() {
        return projName;
    }

    public void setProjName(String projName) {
        this.projName = projName;
    }

    public String getActivatedFlag() {
        return activatedFlag;
    }

    public void setActivatedFlag(String activatedFlag) {
        this.activatedFlag = activatedFlag;
    }

    public String getActivatedById() {
        return activatedById;
    }

    public void setActivatedById(String activatedById) {
        this.activatedById = activatedById;
    }

    public String getActivatedByName() {
        return activatedByName;
    }

    public void setActivatedByName(String activatedByName) {
        this.activatedByName = activatedByName;
    }

    public LocalDateTime getActivatedTime() {
        return activatedTime;
    }

    public void setActivatedTime(LocalDateTime activatedTime) {
        this.activatedTime = activatedTime;
    }
}
