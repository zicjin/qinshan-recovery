/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.applicationproperty;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value={"/secure/core/module/sys/application-property"})
@RestController
public class CoreApplicationPropertyResource {
    @Log(value="\u67e5\u8be2\u914d\u7f6e\u5c5e\u6027\u4fe1\u606f", type=LogType.SELECT)
    @RequestMapping(value={"/queries"}, method={RequestMethod.POST})
    public String getProperty(RestJsonWrapperBean wrapper) {
        String property = wrapper.getParamValue("property");
        if (StringUtils.isEmpty(property)) {
            return null;
        }
        return ApplicationContextHelper.getEnvironment().getProperty(property);
    }
}
