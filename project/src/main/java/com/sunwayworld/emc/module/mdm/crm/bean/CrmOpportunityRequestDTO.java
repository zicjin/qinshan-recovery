package com.sunwayworld.emc.module.mdm.crm.bean;

import java.util.List;

/**
 * @author zhengxy@sunwayworld.com 2021年08月31日
 */
public class CrmOpportunityRequestDTO<T> {

    private String operType;
    private List<T> data;

    public String getOperType() {
        return operType;
    }

    public void setOperType(String operType) {
        this.operType = operType;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CrmOpportunityRequestDTO{" +
                "operType='" + operType + '\'' +
                ", data=" + data +
                '}';
    }
}
