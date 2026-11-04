/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.controller.message.impl;

import com.sunwayworld.cloud.module.item.controller.message.CoreMessageController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreMessageControllerImpl
implements CoreMessageController {
    @Override
    @Log(value="\u7ad9\u5185\u6d88\u606f\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/item/messages/core-message-edit-list";
    }

    @Override
    @Log(value="\u7ad9\u5185\u6d88\u606f\u8be6\u60c5\u9875", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/item/messages/core-message-detail";
    }

    @Override
    @Log(value="\u7ad9\u5185\u6d88\u606f\u67e5\u8be2\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String searchListPage() {
        return "module/item/messages/core-message-search-list";
    }

    @Override
    @Log(value="\u672a\u8bfb\u7ad9\u5185\u6d88\u606f\u67e5\u8be2\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String queryListPage() {
        return "module/item/messages/core-message-query-list";
    }
}
