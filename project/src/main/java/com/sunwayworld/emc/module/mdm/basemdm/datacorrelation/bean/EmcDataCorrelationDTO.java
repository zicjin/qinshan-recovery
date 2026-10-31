package com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.bean;

import java.util.List;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;

/**
 * 数据相关性辅助类
 * 
 */
public class EmcDataCorrelationDTO {

    private EmcDataCorrelationBean dataCorrelation;

    private List<EmcOrderTaskResultBean> orderTaskResultList;

    public EmcDataCorrelationBean getDataCorrelation() {
        return dataCorrelation;
    }

    public void setDataCorrelation(EmcDataCorrelationBean dataCorrelation) {
        this.dataCorrelation = dataCorrelation;
    }

    public List<EmcOrderTaskResultBean> getOrderTaskResultList() {
        return orderTaskResultList;
    }

    public void setOrderTaskResultList(List<EmcOrderTaskResultBean> orderTaskResultList) {
        this.orderTaskResultList = orderTaskResultList;
    }

}
