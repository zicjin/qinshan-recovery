/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.rectify.service;

import com.sunwayworld.cloud.module.qms.rectify.bean.CoreRectifyBean;
import com.sunwayworld.cloud.module.qms.rectify.bean.CoreRectifyItemBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreRectifyService
extends GenericService<CoreRectifyBean, Long>,
GenericAuditableService<CoreRectifyBean, Long> {
    public Page<CoreRectifyItemBean> selectItemPaginationByRectifyId(Long var1, RestJsonWrapperBean var2);

    public Long insertItem(Long var1, RestJsonWrapperBean var2);

    public void deleteItem(Long var1, RestJsonWrapperBean var2);
}
