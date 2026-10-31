package com.sunwayworld.emc.module.mdm.basemdm.revisionrule.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.revisionrule.bean.EmcRevisionRuleBean;
import com.sunwayworld.emc.module.mdm.basemdm.revisionrule.resource.EmcRevisionRuleResource;
import com.sunwayworld.emc.module.mdm.basemdm.revisionrule.service.EmcRevisionRuleService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("修约规则")
@RestController
@GikamBean
public class EmcRevisionRuleResourceImpl implements EmcRevisionRuleResource {

    @Autowired
    private EmcRevisionRuleService limsRevisionRuleService;

    @Override
    public EmcRevisionRuleService getService() {
        return limsRevisionRuleService;
    }

    @Log(value = "选择页查询", type = LogType.SELECT)
    @Override
    public Page<EmcRevisionRuleBean> selectChoosablePagination(RestJsonWrapperBean wrapper) {
        return getService().getRevisionRuleList();
    }

    @Log(value = "请求按修约规则修约之后的值", type = LogType.SELECT)
    @Override
    public String selectRuledValue(@PathVariable String value, RestJsonWrapperBean jsonWrapper) {
        return getService().selectRuledValue(value, jsonWrapper);
    }
}
