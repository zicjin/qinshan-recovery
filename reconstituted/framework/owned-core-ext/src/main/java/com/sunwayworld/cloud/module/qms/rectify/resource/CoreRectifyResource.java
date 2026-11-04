/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.rectify.resource;

import com.sunwayworld.cloud.module.qms.rectify.bean.CoreRectifyBean;
import com.sunwayworld.cloud.module.qms.rectify.bean.CoreRectifyItemBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/qms/rectifys"})
public interface CoreRectifyResource
extends GenericCloudResource<CoreRectifyBean, Long> {
    public Page<CoreRectifyItemBean> selectItemPaginationByRectifyId(Long var1, RestJsonWrapperBean var2);

    public Long insertItem(Long var1, RestJsonWrapperBean var2);

    public void deleteItem(Long var1, RestJsonWrapperBean var2);
}
