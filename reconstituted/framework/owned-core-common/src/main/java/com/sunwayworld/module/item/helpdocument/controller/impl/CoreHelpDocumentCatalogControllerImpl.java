/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.helpdocument.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.helpdocument.controller.CoreHelpDocumentCatalogController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreHelpDocumentCatalogControllerImpl
implements CoreHelpDocumentCatalogController {
    @Override
    @Log(value="\u5e2e\u52a9\u6587\u6863\u76ee\u5f55\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/item/help-documents/core-help-document-edit-list";
    }

    @Override
    @Log(value="\u5e2e\u52a9\u6587\u6863\u76ee\u5f55\u67e5\u8be2\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/item/help-documents/core-help-document-search-list";
    }
}
