/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.resource.impl;

import com.sunwayworld.cloud.module.les.bean.CoreLesDsBean;
import com.sunwayworld.cloud.module.les.bean.CoreLesDsFieldBean;
import com.sunwayworld.cloud.module.les.resource.CoreLesDsResource;
import com.sunwayworld.cloud.module.les.service.CoreLesDsFieldService;
import com.sunwayworld.cloud.module.les.service.CoreLesDsService;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.activatable.resource.AbstractGenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.AbstractGenericChoosableResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="les\u6570\u636e\u6e90\u6a21\u5757")
@RestController
@GikamBean
@RequestMapping(value={"/secure/core/les/les-dses"})
public class CoreLesDsResourceImpl
implements CoreLesDsResource,
AbstractGenericResource<CoreLesDsService, CoreLesDsBean, Long>,
AbstractGenericActivatableResource<CoreLesDsService, CoreLesDsBean, Long>,
AbstractGenericChoosableResource<CoreLesDsService, CoreLesDsBean, Long> {
    @Autowired
    private CoreLesDsService coreLesDsService;
    @Autowired
    private CoreLesDsFieldService coreLesDsFieldService;

    @Override
    public CoreLesDsService getService() {
        return this.coreLesDsService;
    }

    @Override
    @Log(value="\u65b0\u589eles\u6570\u636e\u6e90\u663e\u793a\u5217", type=LogType.INSERT)
    public Long insertField(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return (Long)this.coreLesDsFieldService.insert(wrapper);
    }

    @Override
    @Log(value="\u5220\u9664les\u6570\u636e\u6e90\u663e\u793a\u5217", type=LogType.DELETE)
    public void deleteField(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.coreLesDsFieldService.delete(wrapper);
    }

    @Override
    @Log(value="\u6839\u636eles\u6570\u636e\u6e90\u67e5\u8be2\u663e\u793a\u5217\u5217\u8868", type=LogType.SELECT)
    public Pagination<CoreLesDsFieldBean> selectFieldPaginationByDs(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return (Pagination)this.coreLesDsFieldService.selectPaginationByFilter(SearchFilter.instance().match("LESDSID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Log(value="\u66f4\u65b0les\u4e2d\u7684\u6709\u6548\u7684SQL\u5185\u5bb9\uff0c\u5e76\u66f4\u65b0\u6570\u636e\u6e90\u5b57\u6bb5\u4fe1\u606f", type=LogType.UPDATE)
    public RestValidationResultBean updateSqlIfValid(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.coreLesDsService.updateSqlIfValid(id, wrapper);
    }

    @Override
    @Log(value="\u6570\u636e\u6e90\u5b57\u6bb5\u4e0a\u79fb/\u4e0b\u79fb", type=LogType.UPDATE)
    public void swapField(@PathVariable Long firstId, @PathVariable Long secondId) {
        this.coreLesDsFieldService.swap("ORDERNO", firstId, secondId);
    }
}
