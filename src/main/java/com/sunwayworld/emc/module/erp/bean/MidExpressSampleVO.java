package com.sunwayworld.emc.module.erp.bean;

import java.io.Serializable;
import java.util.List;

public class MidExpressSampleVO implements Serializable {

    /** 实体样品编号 */
    private Long gbId;

    /** 样品类型, com.jiancetech.business.model.enumModel.SampleItemType */
    private Integer type;

    /** 实体样品状态, com.jiancetech.business.model.enumModel.ExpressSampleState */
    private Integer state;

    /** 样品图片 */
    private List<String> pictures;

    /** 外部样品编号 */
    private String expressSampleOutNo;

    public Long getGbId() {
        return gbId;
    }

    public void setGbId(Long gbId) {
        this.gbId = gbId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public List<String> getPictures() {
        return pictures;
    }

    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
    }

    public String getExpressSampleOutNo() {
        return expressSampleOutNo;
    }

    public void setExpressSampleOutNo(String expressSampleOutNo) {
        this.expressSampleOutNo = expressSampleOutNo;
    }
}
