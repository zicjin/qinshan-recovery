/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.jdk.hints.resource.impl;

import com.sunwayworld.framework.jdk.hints.CodeHintsClass;
import com.sunwayworld.framework.jdk.hints.core.CodeHintsManager;
import com.sunwayworld.framework.jdk.hints.resource.CodeHintsResource;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
@LogModule(value="\u4ee3\u7801\u63d0\u793a")
public class CodeHintsResourceImpl
implements CodeHintsResource {
    @Override
    public CodeHintsClass selectByPath(String path) {
        return CodeHintsManager.selectByPath(path);
    }

    @Override
    public String formatSourceCodes(RestJsonWrapperBean wrapper) {
        return CodeHintsManager.formatSourceCodes(wrapper);
    }
}
