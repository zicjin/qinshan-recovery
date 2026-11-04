/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.resource.impl;

import com.sunwayworld.cloud.module.office.bean.CoreOfficeDsBean;
import com.sunwayworld.cloud.module.office.bean.CoreOfficeDsFieldBean;
import com.sunwayworld.cloud.module.office.resource.CoreOfficeDsResource;
import com.sunwayworld.cloud.module.office.service.CoreOfficeDsService;
import com.sunwayworld.cloud.module.office.validator.CoreOfficeDsActivateValidator;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.activatable.resource.AbstractGenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.AbstractGenericChoosableResource;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u62a5\u544a\u6570\u636e\u6e90\u8868")
@RestController
@GikamBean
@RequestMapping(value={"/secure/core/module/office/dses"})
public class CoreOfficeDsResourceImpl
implements CoreOfficeDsResource,
AbstractGenericResource<CoreOfficeDsService, CoreOfficeDsBean, Long>,
AbstractGenericActivatableResource<CoreOfficeDsService, CoreOfficeDsBean, Long>,
AbstractGenericChoosableResource<CoreOfficeDsService, CoreOfficeDsBean, Long> {
    @Autowired
    private CoreOfficeDsService officeDsService;

    @Override
    public CoreOfficeDsService getService() {
        return this.officeDsService;
    }

    @Override
    @Log(value="\u65b0\u589e\u62a5\u544a\u6570\u636e\u6e90\u8868", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u65b0\u589eoffice\u6570\u636e\u6e90", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/fields"}, method={RequestMethod.POST})
    public Long insertField(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.officeDsService.insertField(wrapper);
    }

    @Override
    @Log(value="\u5220\u9664office\u6570\u636e\u6e90", type=LogType.DELETE)
    @RequestMapping(value={"/{id}/fields"}, method={RequestMethod.DELETE})
    public void deleteField(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.officeDsService.deleteField(wrapper);
    }

    @Override
    @Log(value="\u6839\u636eoffice\u6570\u636e\u6e90\u67e5\u8be2\u663e\u793a\u5217\u8868", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/fields/queries"}, method={RequestMethod.POST})
    public Pagination<CoreOfficeDsFieldBean> selectFieldPaginationByDsId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.officeDsService.selectFieldPaginationByDsId(id, wrapper);
    }

    @Override
    @Log(value="\u66f4\u65b0office\u6570\u636e\u6e90\u4e2d\u7684\u6709\u6548\u7684SQL\u5185\u5bb9\uff0c\u5e76\u66f4\u65b0\u6570\u636e\u6e90\u5b57\u6bb5\u4fe1\u606f", type=LogType.UPDATE)
    @RequestMapping(value={"/{id}/action/update-sql-if-valid"}, method={RequestMethod.PUT})
    public RestValidationResultBean updateSqlIfValid(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.officeDsService.updateSqlIfValid(id, wrapper);
    }

    @Override
    @Log(value="\u6570\u636e\u6e90\u5b57\u6bb5\u4e0a\u79fb/\u4e0b\u79fb", type=LogType.UPDATE)
    @RequestMapping(value={"/fields/action/swap/{firstId}/{secondId}"}, method={RequestMethod.PUT})
    public void swapField(@PathVariable Long firstId, @PathVariable Long secondId) {
        this.officeDsService.swapField("ORDERNO", firstId, secondId);
    }

    @Override
    @Log(value="\u542f\u7528\u7684\u6570\u636e\u6e90\u5217\u8868", type=LogType.SELECT)
    @RequestMapping(value={"/tree/queries"}, method={RequestMethod.GET})
    public String selectTree() {
        return this.officeDsService.selectTree();
    }

    @Override
    @Log(value="\u6fc0\u6d3b", type=LogType.ACTIVATE)
    @ValidateDataWith(value=CoreOfficeDsActivateValidator.class)
    public void activate(RestJsonWrapperBean wrapper) {
        this.getService().activate(wrapper);
    }
}
