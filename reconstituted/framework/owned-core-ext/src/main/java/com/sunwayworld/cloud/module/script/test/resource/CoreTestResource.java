/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.script.test.resource;

import com.sunwayworld.cloud.module.script.test.bean.CoreItemBean;
import com.sunwayworld.cloud.module.script.test.bean.CoreTestBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableCloudResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/script/tests"})
public interface CoreTestResource
extends GenericCloudResource<CoreTestBean, Long>,
GenericSelectableCloudResource<CoreTestBean, Long> {
    @RequestMapping(value={"/{id}/items/queries"}, method={RequestMethod.POST})
    public Page<CoreItemBean> selectItemPaginationByTestId(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/items/queries"}, method={RequestMethod.POST})
    public Page<CoreItemBean> selectItemPagination(RestJsonWrapperBean var1);

    @RequestMapping(value={"/{id}/items"}, method={RequestMethod.POST})
    public Long insertItem(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/items"}, method={RequestMethod.DELETE})
    public void deleteItem(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/items/{itemId}"}, method={RequestMethod.GET})
    public CoreItemBean selectItemDetail(@PathVariable Long var1);

    @RequestMapping(value={"/items/{itemId}/action/test-script"}, method={RequestMethod.POST})
    public String testItemScript(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/action/generate-result"}, method={RequestMethod.POST})
    public void generateResult(RestJsonWrapperBean var1);
}
