/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.envproperty.service.impl;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.envproperty.service.CoreEnvPropertyService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreEnvPropertyServiceImpl
implements CoreEnvPropertyService {
    @Override
    public Map<String, String> searchByName(RestJsonWrapperBean wrapper) {
        String propertyNameStr = wrapper.getParamValue("propertyName");
        if (StringUtils.isEmpty(propertyNameStr)) {
            return null;
        }
        String[] propertyNames = propertyNameStr.split(",");
        HashMap<String, String> result = new HashMap<String, String>();
        for (String propertyName : propertyNames) {
            result.put(propertyName, ApplicationContextHelper.getEnvironment().getProperty(propertyName, ""));
        }
        return result;
    }
}
