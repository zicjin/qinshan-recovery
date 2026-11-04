/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.envproperty.resource.impl;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.envproperty.resource.CoreEnvPropertyResource;
import com.sunwayworld.module.envproperty.service.CoreEnvPropertyService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
public class CoreEnvPropertyResourceImpl
implements CoreEnvPropertyResource {
    @Autowired
    private CoreEnvPropertyService envPropertyService;

    @Override
    public Map<String, String> searchByName(RestJsonWrapperBean wrapper) {
        return this.envPropertyService.searchByName(wrapper);
    }
}
