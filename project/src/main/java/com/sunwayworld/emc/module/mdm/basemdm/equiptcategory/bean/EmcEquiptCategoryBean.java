package com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 仪器类型
 * 
 * @author magh@sunwayworld.com 2019年9月4日
 *
 */
@Table("T_EMC_EQUIPT_CATEGORY")
public class EmcEquiptCategoryBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = -6411571353804308223L;

    @Id
    private Long id; // 仪器类型编码<主键>
    private String equiptCategory; // 仪器类型名称
    private String equiptCategoryDesc; // 仪器类型描述
    private String maint; // 是否需要维护

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEquiptCategory() {
        return equiptCategory;
    }

    public void setEquiptCategory(String equiptCategory) {
        this.equiptCategory = equiptCategory;
    }

    public String getEquiptCategoryDesc() {
        return equiptCategoryDesc;
    }

    public void setEquiptCategoryDesc(String equiptCategoryDesc) {
        this.equiptCategoryDesc = equiptCategoryDesc;
    }

    public String getMaint() {
        return maint;
    }

    public void setMaint(String maint) {
        this.maint = maint;
    }

}
