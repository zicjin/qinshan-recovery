/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.configparam.resource;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.lcdp.configparam.bean.LcdpConfigParamBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/lcdp/config-params"})
public interface LcdpConfigParamResource
extends GenericCloudResource<LcdpConfigParamBean, Long> {
    @RequestMapping(value={"/user-config"}, method={RequestMethod.GET})
    public JSONObject selectUserConfigParam(RestJsonWrapperBean var1);
}
