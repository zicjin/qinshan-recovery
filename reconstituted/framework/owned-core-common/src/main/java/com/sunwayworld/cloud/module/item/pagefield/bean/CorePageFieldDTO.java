/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.pagefield.bean;

import com.sunwayworld.cloud.module.item.pagefield.bean.CorePageFieldCompBean;
import com.sunwayworld.cloud.module.item.pagefield.bean.CorePageFieldCompFieldDTO;
import java.io.Serializable;
import java.util.List;

public class CorePageFieldDTO
implements Serializable {
    private static final long serialVersionUID = -7316739280984084962L;
    private CorePageFieldCompBean comp;
    private List<CorePageFieldCompFieldDTO> fieldList;

    public CorePageFieldCompBean getComp() {
        return this.comp;
    }

    public void setComp(CorePageFieldCompBean comp) {
        this.comp = comp;
    }

    public List<CorePageFieldCompFieldDTO> getFieldList() {
        return this.fieldList;
    }

    public void setFieldList(List<CorePageFieldCompFieldDTO> fieldList) {
        this.fieldList = fieldList;
    }
}
