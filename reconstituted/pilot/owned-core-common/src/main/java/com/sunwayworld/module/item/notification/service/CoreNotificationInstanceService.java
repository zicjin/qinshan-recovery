/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.notification.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.item.notification.bean.CoreNotificationDTO;
import com.sunwayworld.module.item.notification.bean.CoreNotificationInstanceBean;
import com.sunwayworld.module.item.notification.bean.CoreNotificationInstanceFileBean;
import com.sunwayworld.module.item.notification.bean.CoreNotificationMessagePageDTO;
import java.util.List;

public interface CoreNotificationInstanceService
extends GenericService<CoreNotificationInstanceBean, Long> {
    public void insertNotificationList(List<CoreNotificationDTO> var1);

    public CoreNotificationMessagePageDTO selectNotificationMessageList(RestJsonWrapperBean var1);

    public Page<CoreNotificationInstanceFileBean> selectNotificationFilePagination(Long var1, RestJsonWrapperBean var2);
}
