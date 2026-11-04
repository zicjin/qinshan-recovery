/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.pvconf.resource.impl;

import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewConfBean;
import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewConfDTO;
import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewGridConfBean;
import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewMasterConfBean;
import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewSubConfBean;
import com.sunwayworld.cloud.module.item.pvconf.resource.CoreProcessViewConfResource;
import com.sunwayworld.cloud.module.item.pvconf.service.CoreProcessViewConfService;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.activatable.resource.AbstractGenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import java.util.ArrayList;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u6570\u636e\u53ef\u89c6\u5316\u914d\u7f6e")
@RestController
@GikamBean
public class CoreProcessViewConfResourceImpl
implements CoreProcessViewConfResource,
AbstractGenericResource<CoreProcessViewConfService, CoreProcessViewConfBean, Long>,
AbstractGenericActivatableResource<CoreProcessViewConfService, CoreProcessViewConfBean, Long> {
    @Autowired
    private CoreProcessViewConfService processViewConfService;

    @Override
    public CoreProcessViewConfService getService() {
        return this.processViewConfService;
    }

    @Override
    @Log(value="\u901a\u8fc7\u914d\u7f6e\u67e5\u8be2\u4e3b\u4e1a\u52a1\u6d41\u7a0b\u914d\u7f6e", type=LogType.SELECT)
    public Pagination<CoreProcessViewMasterConfBean> selectMasterConfPagationByPvConf(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return (Pagination)this.getService().selectMasterConfPagationByPvConf(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u4e3b\u4e1a\u52a1\u6d41\u7a0b\u914d\u7f6e", type=LogType.INSERT)
    public Long insertMasterConf(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertMasterConf(wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u4e3b\u4e1a\u52a1\u6d41\u7a0b\u914d\u7f6e", type=LogType.DELETE)
    public void deleteMasterConf(Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteMasterConf(wrapper);
    }

    @Override
    @Log(value="\u4e92\u6362\u4e3b\u4e1a\u52a1\u6d41\u7a0b\u6392\u5e8f\u7801\u7684\u503c", type=LogType.UPDATE)
    public void swapMasterConf(@PathVariable Long firstId, @PathVariable Long secondId) {
        this.getService().swapMasterConf("ORDERNO", firstId, secondId);
    }

    @Override
    @Log(value="\u4e3b\u4e1a\u52a1\u6d41\u7a0b\u914d\u7f6e\u8be6\u7ec6\u4fe1\u606f\u67e5\u8be2", type=LogType.SELECT)
    public CoreProcessViewMasterConfBean selectMasterConfDetail(@PathVariable Long masterConfId) {
        return this.getService().selectMasterConfDetail(masterConfId);
    }

    @Override
    @Log(value="\u901a\u8fc7\u4e3b\u4e1a\u52a1\u914d\u7f6e\u67e5\u8be2\u5b50\u4e1a\u52a1\u914d\u7f6e", type=LogType.SELECT)
    public Pagination<CoreProcessViewSubConfBean> selectSubConfPagationByMasterConf(@PathVariable Long masterConfId, RestJsonWrapperBean wrapper) {
        return (Pagination)this.getService().selectSubConfPagationByMasterConf(masterConfId, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u5b50\u4e1a\u52a1\u6d41\u7a0b\u914d\u7f6e", type=LogType.INSERT)
    public Long insertSubConf(RestJsonWrapperBean wrapper) {
        return this.getService().insertSubConf(wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u5b50\u4e1a\u52a1\u6d41\u7a0b\u914d\u7f6e", type=LogType.DELETE)
    public void deleteSubConf(RestJsonWrapperBean wrapper) {
        this.getService().deleteSubConf(wrapper);
    }

    @Override
    @Log(value="\u5b50\u4e1a\u52a1\u6d41\u7a0b\u914d\u7f6e\u8be6\u7ec6\u4fe1\u606f\u67e5\u8be2", type=LogType.SELECT)
    public CoreProcessViewSubConfBean selectSubConfDetail(@PathVariable Long subConfId) {
        return this.getService().selectSubConfDetail(subConfId);
    }

    @Override
    @Log(value="\u4e92\u6362\u5b50\u4e1a\u52a1\u6d41\u7a0b\u6392\u5e8f\u7801\u7684\u503c", type=LogType.UPDATE)
    public void swapSubConf(@PathVariable Long firstId, @PathVariable Long secondId) {
        this.getService().swapSubConf("ORDERNO", firstId, secondId);
    }

    @Override
    @Log(value="\u67e5\u8be2\u5217\u8868\u6570\u636e\u914d\u7f6e\u67e5\u8be2SQL", type=LogType.SELECT)
    public String selectGridSql(@PathVariable Long sourceId, @PathVariable String sourceType, RestJsonWrapperBean wrapper) {
        return this.getService().selectGridSql(sourceId, sourceType);
    }

    @Override
    @Log(value="\u65b0\u589e\u5217\u8868\u6570\u636e\u914d\u7f6e", type=LogType.INSERT)
    public void insertGridConf(RestJsonWrapperBean wrapper) {
        this.getService().insertGridConf(wrapper);
    }

    @Override
    @Log(value="\u5217\u8868\u914d\u7f6e\u6570\u636e\u67e5\u8be2", type=LogType.SELECT)
    public Pagination<CoreProcessViewGridConfBean> selectGridConfPagination(RestJsonWrapperBean wrapper) {
        return (Pagination)this.getService().selectGridConfPagination(wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u4e1a\u52a1\u53ef\u89c6\u5316", type=LogType.SELECT)
    public CoreProcessViewConfDTO selectProcessView(RestJsonWrapperBean wrapper) {
        return this.getService().selectProcessView(wrapper);
    }

    @Override
    @Log(value="\u5217\u8868\u914d\u7f6e\u6570\u636e\u542f\u7528", type=LogType.ACTIVATE)
    public void activateGridConf(RestJsonWrapperBean wrapper) {
        this.getService().activateGridConf(wrapper);
    }

    @Override
    @Log(value="\u5217\u8868\u914d\u7f6e\u6570\u636e\u505c\u7528", type=LogType.ACTIVATE)
    public void deactivateGridConf(RestJsonWrapperBean wrapper) {
        this.getService().deactivateGridConf(wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u5217\u8868\u914d\u7f6e\u5217", type=LogType.SELECT)
    public ArrayList<CoreProcessViewGridConfBean> selectGridConfColumnListByConf(@PathVariable Long sourceId, RestJsonWrapperBean wrapper) {
        return (ArrayList)this.getService().selectGridConfColumnListByConf(sourceId, wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u5217\u8868\u914d\u7f6e\u6570\u636e", type=LogType.SELECT)
    public Pagination<Map<String, Object>> selectGridConfDataPagination(@PathVariable Long sourceId, @PathVariable String sourceType, RestJsonWrapperBean wrapper) {
        return (Pagination)this.getService().selectGridDataPagination(sourceId, sourceType, wrapper);
    }
}
