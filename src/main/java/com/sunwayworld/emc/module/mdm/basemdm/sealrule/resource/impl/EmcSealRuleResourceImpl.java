package com.sunwayworld.emc.module.mdm.basemdm.sealrule.resource.impl;

import com.sunwayworld.emc.module.mdm.basemdm.sealrule.bean.EmcSealRuleDetailsBean;
import com.sunwayworld.emc.module.mdm.basemdm.sealrule.resource.EmcSealRuleResource;
import com.sunwayworld.emc.module.mdm.basemdm.sealrule.service.EmcSealRuleDetailsService;
import com.sunwayworld.emc.module.mdm.basemdm.sealrule.service.EmcSealRuleService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule("盖章打印规则")
@RestController
@GikamBean
public class EmcSealRuleResourceImpl implements EmcSealRuleResource {

    @Autowired
    private EmcSealRuleService emcSealRuleService;

    @Autowired
    private EmcSealRuleDetailsService emcSealRuleDetailsService;

    @Override
    public EmcSealRuleService getService() {
        return emcSealRuleService;
    }

    @Log(value = "新增盖章打印规则", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Override
    @Log(value = "查询盖章打印规则详情", type = LogType.SELECT)
    @RequestMapping(value = "/rule-detailss/queries", method = RequestMethod.POST)
    public Page<EmcSealRuleDetailsBean> selectRuleDetailsPagination(RestJsonWrapperBean wrapper) {
        return emcSealRuleDetailsService.selectPagination(wrapper);
    }

    @Override
    @Log(value = "根据盖章打印规则ID查询盖章打印规则详情", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/rule-detailss/queries", method = RequestMethod.POST)
    public Page<EmcSealRuleDetailsBean> selectRuleDetailsPaginationBySealRuleId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().selectRuleDetailsPaginationBySealRuleId(id, wrapper);
    }

    @Override
    @Log(value = "新增盖章打印规则详情", type = LogType.INSERT)
    @RequestMapping(value = "/{id}/rule-detailss", method = RequestMethod.POST)
    public Long insertRuleDetails(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().insertRuleDetails(id, wrapper);
    }

    @Override
    @Log(value = "删除盖章打印规则详情", type = LogType.DELETE)
    @RequestMapping(value = "/{id}/rule-detailss", method = RequestMethod.DELETE)
    public void deleteRuleDetails(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        getService().deleteRuleDetails(id, wrapper);
    }

}
