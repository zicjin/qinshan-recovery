package com.sunwayworld.emc.module.bp.archive.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 归档文件类型表
 * @author zhengxy@sunwayworld.com 2020年10月31日
 */
@Table("T_EMC_ARCHIVE_CATEGORY")
public class EmcArchiveCategoryBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = -4852480481915078861L;

    @Id
    private Long id;  // 主键
    private String categoryCode;  // 归档类型编码
    private String categoryName;  // 归档类型名称
    private String remark;  // 备注信息

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
