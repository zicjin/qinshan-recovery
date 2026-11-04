/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.notice.service;

import com.sunwayworld.cloud.module.item.notice.bean.CoreNoticeBean;
import com.sunwayworld.cloud.module.item.notice.bean.CoreNoticePermissionBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;

public interface CoreNoticeService
extends GenericService<CoreNoticeBean, Long>,
GenericAuditableService<CoreNoticeBean, Long> {
    public Long insertPermission(Long var1, RestJsonWrapperBean var2);

    public Page<CoreNoticePermissionBean> selectPermissionPagationByNotice(Long var1, String var2, RestJsonWrapperBean var3);

    public void deletePermission(RestJsonWrapperBean var1);

    public void cancel(RestJsonWrapperBean var1);

    public List<CoreNoticeBean> selectIndexList(RestJsonWrapperBean var1);
}
