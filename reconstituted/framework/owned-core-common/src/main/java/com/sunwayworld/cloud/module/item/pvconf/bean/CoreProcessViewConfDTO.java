/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.pvconf.bean;

import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewMasterConfDTO;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.framework.support.domain.BaseData;
import java.util.List;

public class CoreProcessViewConfDTO
extends AbstractBaseData
implements BaseData {
    private static final long serialVersionUID = 1106424833644560017L;
    private Long id;
    private String confName;
    List<CoreProcessViewMasterConfDTO> masterConfList;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConfName() {
        return this.confName;
    }

    public void setConfName(String confName) {
        this.confName = confName;
    }

    public List<CoreProcessViewMasterConfDTO> getMasterConfList() {
        return this.masterConfList;
    }

    public void setMasterConfList(List<CoreProcessViewMasterConfDTO> masterConfList) {
        this.masterConfList = masterConfList;
    }
}
