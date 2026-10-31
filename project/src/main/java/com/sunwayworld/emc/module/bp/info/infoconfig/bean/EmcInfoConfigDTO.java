package com.sunwayworld.emc.module.bp.info.infoconfig.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;

/**
 * 综合信息配置树DTO
 * 
 * @author qianc@sunwayworld.com 2019年11月18日
 */
public class EmcInfoConfigDTO extends AbstractTreeNode {

    @Transient
    private static final long serialVersionUID = 1072716845124725713L;
    
    private String  configName;// 名称
    
    private String parentId; //父id
    
    private Long infoTmplId;// 上报模板id
    
    private Long orderNo; // 排序码
    
    private String configType; //配置类型

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Long getInfoTmplId() {
        return infoTmplId;
    }

    public void setInfoTmplId(Long infoTmplId) {
        this.infoTmplId = infoTmplId;
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }
    
}
