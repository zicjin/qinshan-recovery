/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.prevention.resource;

import com.sunwayworld.cloud.module.qms.prevention.bean.CorePreventionBean;
import com.sunwayworld.cloud.module.qms.prevention.bean.CorePreventionItemBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/qms/preventions"})
public interface CorePreventionResource
extends GenericCloudResource<CorePreventionBean, Long> {
    public Page<CorePreventionItemBean> selectItemPaginationByPreventionId(Long var1, RestJsonWrapperBean var2);

    public Long insertItem(Long var1, RestJsonWrapperBean var2);

    public void deleteItem(Long var1, RestJsonWrapperBean var2);
}
