/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.script.test.resource.impl;

import com.sunwayworld.cloud.module.script.test.bean.CoreItemBean;
import com.sunwayworld.cloud.module.script.test.bean.CoreTestBean;
import com.sunwayworld.cloud.module.script.test.resource.CoreTestResource;
import com.sunwayworld.cloud.module.script.test.service.CoreTestService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.selectable.resource.AbstractGenericSelectableResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u6d4b\u8bd5")
@RestController
@GikamBean
public class CoreTestResourceImpl
implements CoreTestResource,
AbstractGenericResource<CoreTestService, CoreTestBean, Long>,
AbstractGenericSelectableResource<CoreTestService, CoreTestBean, Long> {
    @Autowired
    private CoreTestService coreTestService;

    @Override
    public CoreTestService getService() {
        return this.coreTestService;
    }

    @Override
    @Log(value="\u65b0\u589e\u6d4b\u8bd5", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u6d4b\u8bd5ID\u67e5\u8be2\u5206\u6790\u9879", type=LogType.SELECT)
    public Page<CoreItemBean> selectItemPaginationByTestId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectItemPaginationByTestId(id, wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u5206\u6790\u9879", type=LogType.SELECT)
    public Page<CoreItemBean> selectItemPagination(RestJsonWrapperBean wrapper) {
        return this.getService().selectItemPagination(wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u5206\u6790\u9879", type=LogType.INSERT)
    public Long insertItem(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertItem(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u5206\u6790\u9879", type=LogType.DELETE)
    public void deleteItem(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteItem(id, wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u5206\u6790\u9879\u8be6\u60c5", type=LogType.SELECT)
    public CoreItemBean selectItemDetail(Long itemId) {
        return this.getService().selectItemDetail(itemId);
    }

    @Override
    @Log(value="\u6d4b\u8bd5\u5206\u6790\u9879\u8ba1\u7b97\u516c\u5f0f", type=LogType.SELECT)
    public String testItemScript(Long itemId, RestJsonWrapperBean wrapper) {
        return this.getService().testItemScript(itemId, wrapper);
    }

    @Override
    @Log(value="\u751f\u6210\u7ed3\u679c", type=LogType.INSERT)
    public void generateResult(RestJsonWrapperBean wrapper) {
        this.getService().generateResult(wrapper);
    }
}
