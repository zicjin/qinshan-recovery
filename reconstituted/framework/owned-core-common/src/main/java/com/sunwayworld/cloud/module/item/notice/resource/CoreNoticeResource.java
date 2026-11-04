/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.notice.resource;

import com.sunwayworld.cloud.module.item.notice.bean.CoreNoticeBean;
import com.sunwayworld.cloud.module.item.notice.bean.CoreNoticePermissionBean;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableCloudResource;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/item/notices"})
public interface CoreNoticeResource
extends GenericCloudResource<CoreNoticeBean, Long>,
GenericAuditableCloudResource<CoreNoticeBean, Long> {
    @RequestMapping(value={"/{id}/permissions"}, method={RequestMethod.POST})
    public Long insertPermission(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/permission-types/{permissionType}/permissions/queries"}, method={RequestMethod.POST})
    public Pagination<CoreNoticePermissionBean> selectPermissionPagationByNotice(@PathVariable Long var1, @PathVariable String var2, RestJsonWrapperBean var3);

    @RequestMapping(value={"/permissions"}, method={RequestMethod.DELETE})
    public void deletePermission(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/cancel"}, method={RequestMethod.PUT})
    public void cancel(RestJsonWrapperBean var1);

    @RequestMapping(value={"/queries/index"}, method={RequestMethod.POST})
    public List<CoreNoticeBean> selectIndexList(RestJsonWrapperBean var1);
}
