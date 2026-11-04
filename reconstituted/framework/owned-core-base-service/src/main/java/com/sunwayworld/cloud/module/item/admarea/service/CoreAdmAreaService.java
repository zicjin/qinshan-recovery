/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.admarea.service;

import com.sunwayworld.cloud.module.item.admarea.bean.CoreAdmAreaBean;
import com.sunwayworld.cloud.module.item.admarea.bean.CoreAdmAreaDTO;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.tree.service.GenericTreeService;
import java.util.List;

public interface CoreAdmAreaService
extends GenericService<CoreAdmAreaBean, String>,
GenericTreeService<CoreAdmAreaBean, String> {
    public List<CoreAdmAreaDTO> selectSelectableList();
}
