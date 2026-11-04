/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.admarea.resource.impl;

import com.sunwayworld.cloud.module.item.admarea.bean.CoreAdmAreaBean;
import com.sunwayworld.cloud.module.item.admarea.bean.CoreAdmAreaDTO;
import com.sunwayworld.cloud.module.item.admarea.resource.CoreAdmAreaResource;
import com.sunwayworld.cloud.module.item.admarea.service.CoreAdmAreaService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.tree.resource.AbstractGenericTreeResource;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u884c\u653f\u533a\u57df")
@RestController
@GikamBean
public class CoreAdmAreaResourceImpl
implements CoreAdmAreaResource,
AbstractGenericResource<CoreAdmAreaService, CoreAdmAreaBean, String>,
AbstractGenericTreeResource<CoreAdmAreaService, CoreAdmAreaBean, String> {
    @Autowired
    private CoreAdmAreaService admAreaService;

    @Override
    public CoreAdmAreaService getService() {
        return this.admAreaService;
    }

    @Override
    @Log(value="\u7ea7\u8054\u4e0b\u62c9\u6846\u6570\u636e\u67e5\u8be2", type=LogType.SELECT)
    public List<CoreAdmAreaDTO> selectSelectableList() {
        return this.getService().selectSelectableList();
    }
}
