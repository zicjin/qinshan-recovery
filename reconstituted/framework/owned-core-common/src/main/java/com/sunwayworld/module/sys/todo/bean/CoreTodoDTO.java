/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.todo.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.framework.support.domain.BaseData;
import com.sunwayworld.module.sys.todo.bean.CoreTodoItemDTO;
import java.util.ArrayList;
import java.util.List;

public class CoreTodoDTO
extends AbstractBaseData
implements BaseData {
    @Transient
    private static final long serialVersionUID = 1920878782483930666L;
    private String menuId;
    private String modelName;
    private String href;
    private Integer qty;
    private String voiceWarning;
    private Long orderNo;
    private List<CoreTodoItemDTO> itemList = new ArrayList<CoreTodoItemDTO>();

    public String getMenuId() {
        return this.menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getModelName() {
        return this.modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getHref() {
        return this.href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getVoiceWarning() {
        return this.voiceWarning;
    }

    public void setVoiceWarning(String voiceWarning) {
        this.voiceWarning = voiceWarning;
    }

    public Integer getQty() {
        return this.qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Long getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public List<CoreTodoItemDTO> getItemList() {
        return this.itemList;
    }

    public void setItemList(List<CoreTodoItemDTO> itemList) {
        this.itemList = itemList;
    }
}
