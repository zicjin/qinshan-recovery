/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.spcs.resource;

import com.sunwayworld.cloud.module.item.spcs.bean.CoreCompFlushRespBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/item/spcs"})
public interface CoreSpcResource {
    @RequestMapping(value={"/handle-spc"}, method={RequestMethod.POST})
    public CoreCompFlushRespBean handleSpc(RestJsonWrapperBean var1);
}
