/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.notification.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.module.item.notification.bean.CoreNotificationInstanceBean;
import com.sunwayworld.module.item.notification.bean.CoreNotificationInstanceFileBean;
import com.sunwayworld.module.item.notification.bean.CoreNotificationMessagePageDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/item/notification/notification-instances"})
public interface CoreNotificationInstanceResource
extends GenericCloudResource<CoreNotificationInstanceBean, Long> {
    @Log(value="\u8bfb\u53d6", type=LogType.UPDATE)
    @RequestMapping(value={"/action/read"}, method={RequestMethod.PUT})
    public void read(RestJsonWrapperBean var1);

    @Log(value="\u5ffd\u7565", type=LogType.UPDATE)
    @RequestMapping(value={"/action/ignore"}, method={RequestMethod.PUT})
    public void ignore(RestJsonWrapperBean var1);

    @RequestMapping(value={"/messages/queries"}, method={RequestMethod.POST})
    public CoreNotificationMessagePageDTO selectNotificationMessageList(RestJsonWrapperBean var1);

    @Log(value="\u67e5\u8be2\u9644\u4ef6\u4fe1\u606f", type=LogType.SELECT)
    @RequestMapping(value={"{id}/files/queries"}, method={RequestMethod.POST})
    public Page<CoreNotificationInstanceFileBean> selectNotificationFilePagination(@PathVariable Long var1, RestJsonWrapperBean var2);
}
