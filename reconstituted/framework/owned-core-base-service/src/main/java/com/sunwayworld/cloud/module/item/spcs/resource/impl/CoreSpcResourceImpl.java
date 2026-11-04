/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.spcs.resource.impl;

import com.sunwayworld.cloud.module.item.spcs.bean.CoreCompFlushRespBean;
import com.sunwayworld.cloud.module.item.spcs.resource.CoreSpcResource;
import com.sunwayworld.cloud.module.item.spcs.service.CoreSpcService;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="SPC")
@RestController
@GikamBean
public class CoreSpcResourceImpl
implements CoreSpcResource {
    @Autowired
    private CoreSpcService coreSpcService;

    @Override
    public CoreCompFlushRespBean handleSpc(RestJsonWrapperBean wrapper) {
        return this.coreSpcService.handleSpc(wrapper);
    }
}
