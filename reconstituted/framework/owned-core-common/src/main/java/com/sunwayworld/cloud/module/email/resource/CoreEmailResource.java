/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.resource;

import com.sunwayworld.cloud.module.email.bean.CoreEmailBean;
import com.sunwayworld.cloud.module.email.bean.CoreEmailReceipentBean;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface CoreEmailResource
extends GenericCloudResource<CoreEmailBean, Long> {
    @RequestMapping(value={"/{id}/email-receipents/queries"}, method={RequestMethod.POST})
    public Pagination<CoreEmailReceipentBean> selectReceiptentByEmail(@PathVariable Long var1, RestJsonWrapperBean var2);
}
