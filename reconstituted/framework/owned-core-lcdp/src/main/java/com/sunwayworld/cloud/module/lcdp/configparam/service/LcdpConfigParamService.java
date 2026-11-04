/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.configparam.service;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.lcdp.configparam.bean.LcdpConfigParamBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface LcdpConfigParamService
extends GenericService<LcdpConfigParamBean, Long> {
    public JSONObject selectUserConfigParam(RestJsonWrapperBean var1);

    public String getCurrentDBMybatisMapperParam();
}
