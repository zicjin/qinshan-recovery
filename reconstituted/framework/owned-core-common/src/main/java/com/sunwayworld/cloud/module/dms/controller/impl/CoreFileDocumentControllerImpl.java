/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.dms.controller.impl;

import com.sunwayworld.cloud.module.dms.controller.CoreFileDocumentController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreFileDocumentControllerImpl
implements CoreFileDocumentController {
    @Override
    @Log(value="\u5168\u6587\u68c0\u7d22\u7c7b\u578b\u67e5\u8be2\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/dms/documents/core-file-document-search-list";
    }
}
