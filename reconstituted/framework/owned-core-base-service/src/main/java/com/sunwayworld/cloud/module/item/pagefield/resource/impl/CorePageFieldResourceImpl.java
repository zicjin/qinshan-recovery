/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.pagefield.resource.impl;

import com.sunwayworld.cloud.module.item.pagefield.bean.CorePageFieldBean;
import com.sunwayworld.cloud.module.item.pagefield.bean.CorePageFieldCompBean;
import com.sunwayworld.cloud.module.item.pagefield.bean.CorePageFieldCompFieldBean;
import com.sunwayworld.cloud.module.item.pagefield.bean.CorePageFieldDTO;
import com.sunwayworld.cloud.module.item.pagefield.resource.CorePageFieldResource;
import com.sunwayworld.cloud.module.item.pagefield.service.CorePageFieldService;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.AbstractGenericChoosableResource;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u9875\u9762\u5b57\u6bb5\u914d\u7f6e")
@RestController
@GikamBean
public class CorePageFieldResourceImpl
implements CorePageFieldResource,
AbstractGenericResource<CorePageFieldService, CorePageFieldBean, Long>,
AbstractGenericChoosableResource<CorePageFieldService, CorePageFieldBean, Long> {
    @Autowired
    private CorePageFieldService corePageFieldService;

    @Override
    public CorePageFieldService getService() {
        return this.corePageFieldService;
    }

    @Override
    @Log(value="\u65b0\u589e\u9875\u9762\u5b57\u6bb5\u914d\u7f6e", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u9875\u9762\u7ec4\u4ef6", type=LogType.INSERT)
    public Long insertComp(@PathVariable Long pfId, RestJsonWrapperBean jsonWrapper) {
        return this.getService().insertComp(pfId, jsonWrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u9875\u9762\u7ec4\u4ef6", type=LogType.DELETE)
    public void deleteComp(@PathVariable Long pfId, RestJsonWrapperBean jsonWrapper) {
        this.getService().deleteComp(pfId, jsonWrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u9875\u9762\u7ec4\u4ef6", type=LogType.SELECT)
    public Pagination<CorePageFieldCompBean> selectCompPagination(@PathVariable Long pfId, RestJsonWrapperBean jsonWrapper) {
        return (Pagination)this.getService().selectCompPagination(pfId, jsonWrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u7ec4\u4ef6\u5b57\u6bb5", type=LogType.INSERT)
    public Long insertCompField(@PathVariable Long pfcId, RestJsonWrapperBean jsonWrapper) {
        return this.getService().insertCompField(pfcId, jsonWrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u7ec4\u4ef6\u5b57\u6bb5", type=LogType.INSERT)
    public void deleteCompField(@PathVariable Long pfcId, RestJsonWrapperBean jsonWrapper) {
        this.getService().deleteCompField(pfcId, jsonWrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u7ec4\u4ef6\u5b57\u6bb5", type=LogType.INSERT)
    public Pagination<CorePageFieldCompFieldBean> selectCompFieldPagination(@PathVariable Long pfcId, RestJsonWrapperBean jsonWrapper) {
        return (Pagination)this.getService().selectCompFieldPagination(pfcId, jsonWrapper);
    }

    @Override
    @Log(value="\u5b57\u6bb5\u987a\u5e8f", type=LogType.SWAPEORDER)
    public void swapCompField(@PathVariable Long pfcId, @PathVariable Long firstId, @PathVariable Long secondId) {
        this.getService().swapCompField(pfcId, firstId, secondId);
    }

    @Override
    @Log(value="\u67e5\u8be2\u9875\u9762\u7ec4\u4ef6\u5b57\u6bb5\u914d\u7f6e", type=LogType.INSERT)
    public ArrayList<CorePageFieldDTO> selectCompFieldList(@PathVariable Long pfId) {
        return (ArrayList)this.getService().selectCompFieldList(pfId);
    }
}
