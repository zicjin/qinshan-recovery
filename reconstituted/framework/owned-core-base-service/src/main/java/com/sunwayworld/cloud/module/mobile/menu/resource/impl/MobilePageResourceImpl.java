/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.menu.resource.impl;

import com.sunwayworld.cloud.module.mobile.menu.bean.MobilePageBean;
import com.sunwayworld.cloud.module.mobile.menu.bean.MobilePageRepresentative;
import com.sunwayworld.cloud.module.mobile.menu.resource.MobilePageResource;
import com.sunwayworld.cloud.module.mobile.menu.service.MobilePageService;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.AbstractGenericChoosableResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u79fb\u52a8\u7aef\u9875\u9762\u914d\u7f6e")
@RestController
@GikamBean
@RequestMapping(value={"/secure/core/module/sys/mobile/pages"})
public class MobilePageResourceImpl
implements MobilePageResource,
AbstractGenericResource<MobilePageService, MobilePageBean, Long>,
AbstractGenericChoosableResource<MobilePageService, MobilePageBean, Long> {
    @Autowired
    private MobilePageService mobilePageService;

    @Override
    public MobilePageService getService() {
        return this.mobilePageService;
    }

    @Override
    @Log(value="\u65b0\u589e\u79fb\u52a8\u9875\u9762(\u540c\u7ea7)", type=LogType.INSERT)
    public void insertSameLevelPage(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().insertSameLevelPage(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u79fb\u52a8\u9875\u9762(\u4e0b\u7ea7)", type=LogType.INSERT)
    public void insertLowerLevelPage(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().insertLowerLevelPage(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u79fb\u52a8\u9875\u9762", type=LogType.DELETE)
    public void deletePage(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().deletePage(id, wrapper);
    }

    @Override
    @Log(value="\u6839\u636eid\u83b7\u53d6page\u8be6\u60c5\u4fe1\u606f", type=LogType.SELECT)
    public MobilePageBean selectPage(@PathVariable Long id) {
        return (MobilePageBean)this.getService().selectByIdIfPresent(id);
    }

    @Override
    @Log(value="\u83b7\u53d6\u79fb\u52a8\u9875\u9762\u6811\u578b\u5217\u8868", type=LogType.SELECT)
    public Pagination<MobilePageRepresentative> selectMobilePageTreeList(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return (Pagination)this.getService().selectMobilePageTreeList("MENUID", id, wrapper);
    }

    @Override
    @Log(value="\u4e92\u6362\u9875\u9762\u7f16\u53f7", type=LogType.UPDATE)
    public void switchPageColumn(@PathVariable Long firstId, @PathVariable Long secondId) {
        this.getService().switchPageColumn(firstId, secondId);
    }
}
