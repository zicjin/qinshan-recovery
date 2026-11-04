/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.script.test.service;

import com.sunwayworld.cloud.module.script.test.bean.CoreItemBean;
import com.sunwayworld.cloud.module.script.test.bean.CoreTestBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;

public interface CoreTestService
extends GenericService<CoreTestBean, Long>,
GenericSelectableService<CoreTestBean, Long> {
    public Page<CoreItemBean> selectItemPaginationByTestId(Long var1, RestJsonWrapperBean var2);

    public Long insertItem(Long var1, RestJsonWrapperBean var2);

    public void deleteItem(Long var1, RestJsonWrapperBean var2);

    public CoreItemBean selectItemDetail(Long var1);

    public Page<CoreItemBean> selectItemPagination(RestJsonWrapperBean var1);

    public String testItemScript(Long var1, RestJsonWrapperBean var2);

    public void generateResult(RestJsonWrapperBean var1);
}
