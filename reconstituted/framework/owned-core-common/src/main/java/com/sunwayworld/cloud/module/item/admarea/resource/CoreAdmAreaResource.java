/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.admarea.resource;

import com.sunwayworld.cloud.module.item.admarea.bean.CoreAdmAreaBean;
import com.sunwayworld.cloud.module.item.admarea.bean.CoreAdmAreaDTO;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.tree.resource.GenericTreeCloudResource;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/item/adm-areas"})
public interface CoreAdmAreaResource
extends GenericCloudResource<CoreAdmAreaBean, String>,
GenericTreeCloudResource<CoreAdmAreaBean, String> {
    @RequestMapping(value={"/queries/selectable"}, method={RequestMethod.GET})
    public List<CoreAdmAreaDTO> selectSelectableList();
}
