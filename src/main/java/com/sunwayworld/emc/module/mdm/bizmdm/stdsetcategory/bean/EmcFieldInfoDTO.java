package com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.framework.support.domain.BaseData;

/**
 * 字段信息DTO
 * 
 * @author qianc@sunwayworld.com 2019年11月1日
 */
public class EmcFieldInfoDTO extends AbstractBaseData implements BaseData {
    @Transient
    private static final long serialVersionUID = -8267212014967839732L;

    private String fieldName;

    private String fieldTitle;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldTitle() {
        return fieldTitle;
    }

    public void setFieldTitle(String fieldTitle) {
        this.fieldTitle = fieldTitle;
    }

}
