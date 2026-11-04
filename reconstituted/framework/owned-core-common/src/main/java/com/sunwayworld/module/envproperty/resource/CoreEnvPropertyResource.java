/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.envproperty.resource;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/env-properties"})
public interface CoreEnvPropertyResource {
    @RequestMapping(path={"/search-by-name"}, method={RequestMethod.POST})
    public Map<String, String> searchByName(RestJsonWrapperBean var1);
}
