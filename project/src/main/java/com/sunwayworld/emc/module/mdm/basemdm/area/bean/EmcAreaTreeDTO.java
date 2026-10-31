package com.sunwayworld.emc.module.mdm.basemdm.area.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;

/**
 * @author yuanhh@sunwayworld.com 2019年9月16日
 */

public class EmcAreaTreeDTO extends AbstractTreeNode {

    @Transient
    private static final long serialVersionUID = 4348933747867014395L;

    private String areaName; // 区域名称
    private Long orderNo; // 排序码
    
    public String getAreaName() {
        return areaName;
    }
    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
    public Long getOrderNo() {
        return orderNo;
    }
    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }
}
