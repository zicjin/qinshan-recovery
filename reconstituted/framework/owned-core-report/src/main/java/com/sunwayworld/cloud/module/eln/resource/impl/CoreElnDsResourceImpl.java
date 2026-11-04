/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.resource.impl;

import com.sunwayworld.cloud.module.eln.bean.CoreElnDsBean;
import com.sunwayworld.cloud.module.eln.bean.CoreElnDsFieldBean;
import com.sunwayworld.cloud.module.eln.resource.CoreElnDsResource;
import com.sunwayworld.cloud.module.eln.service.CoreElnDsFieldService;
import com.sunwayworld.cloud.module.eln.service.CoreElnDsService;
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

@LogModule(value="eln\u6570\u636e\u6e90\u6a21\u5757")
@RestController
@GikamBean
@RequestMapping(value={"/secure/core/module/eln/eln-dses"})
public class CoreElnDsResourceImpl
implements CoreElnDsResource,
AbstractGenericResource<CoreElnDsService, CoreElnDsBean, Long>,
AbstractGenericActivatableResource<CoreElnDsService, CoreElnDsBean, Long>,
AbstractGenericChoosableResource<CoreElnDsService, CoreElnDsBean, Long> {
    @Autowired
    private CoreElnDsService coreElnDsService;
    @Autowired
    private CoreElnDsFieldService coreElnDsFieldService;

    @Override
    public CoreElnDsService getService() {
        return this.coreElnDsService;
    }

    @Override
    @Log(value="\u65b0\u589eeln\u6570\u636e\u6e90\u5b57\u6bb5", type=LogType.INSERT)
    public Long insertField(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return (Long)this.coreElnDsFieldService.insert(wrapper);
    }

    @Override
    @Log(value="\u5220\u9664eln\u6570\u636e\u6e90\u5b57\u6bb5", type=LogType.DELETE)
    public void deleteField(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.coreElnDsFieldService.delete(wrapper);
    }

    @Override
    @Log(value="\u6839\u636eeln\u6570\u636e\u6e90\u67e5\u8be2\u5b57\u6bb5\u5217\u8868", type=LogType.SELECT)
    public Pagination<CoreElnDsFieldBean> selectFieldPaginationByDs(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return (Pagination)this.coreElnDsFieldService.selectPaginationByFilter(SearchFilter.instance().match("ELNDSID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Log(value="\u66f4\u65b0ELN\u4e2d\u7684\u6709\u6548\u7684SQL\u5185\u5bb9\uff0c\u5e76\u66f4\u65b0\u6570\u636e\u6e90\u5b57\u6bb5", type=LogType.UPDATE)
    public RestValidationResultBean updateSqlIfValid(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.coreElnDsService.updateSqlIfValid(id, wrapper);
    }
}
