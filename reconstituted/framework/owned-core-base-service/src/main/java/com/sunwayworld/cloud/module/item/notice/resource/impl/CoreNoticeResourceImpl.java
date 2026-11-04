/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.notice.resource.impl;

import com.sunwayworld.cloud.module.item.notice.bean.CoreNoticeBean;
import com.sunwayworld.cloud.module.item.notice.bean.CoreNoticePermissionBean;
import com.sunwayworld.cloud.module.item.notice.resource.CoreNoticeResource;
import com.sunwayworld.cloud.module.item.notice.service.CoreNoticeService;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.resource.AbstractGenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u901a\u77e5\u516c\u544a")
@RestController
@GikamBean
public class CoreNoticeResourceImpl
implements CoreNoticeResource,
AbstractGenericResource<CoreNoticeService, CoreNoticeBean, Long>,
AbstractGenericAuditableResource<CoreNoticeService, CoreNoticeBean, Long> {
    @Autowired
    private CoreNoticeService noticeService;

    @Override
    public CoreNoticeService getService() {
        return this.noticeService;
    }

    @Override
    @Log(value="\u65b0\u589e\u901a\u77e5\u516c\u544a", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u6dfb\u52a0\u516c\u544a\u6743\u9650", type=LogType.INSERT)
    public Long insertPermission(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertPermission(id, wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u6743\u9650", type=LogType.SELECT)
    public Pagination<CoreNoticePermissionBean> selectPermissionPagationByNotice(@PathVariable Long id, @PathVariable String permissionType, RestJsonWrapperBean wrapper) {
        return (Pagination)this.getService().selectPermissionPagationByNotice(id, permissionType, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u6743\u9650", type=LogType.DELETE)
    public void deletePermission(RestJsonWrapperBean wrapper) {
        this.getService().deletePermission(wrapper);
    }

    @Override
    @Log(value="\u64a4\u9500\u901a\u77e5\u516c\u544a", type=LogType.UPDATE)
    public void cancel(RestJsonWrapperBean wrapper) {
        this.getService().cancel(wrapper);
    }

    @Override
    @Log(value="\u9996\u9875\u67e5\u8be2", type=LogType.SELECT)
    public List<CoreNoticeBean> selectIndexList(RestJsonWrapperBean wrapper) {
        return this.getService().selectIndexList(wrapper);
    }
}
