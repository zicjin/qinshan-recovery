package com.sunwayworld.emc.module.mdm.basemdm.revisionrule.resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sunwayworld.emc.module.mdm.basemdm.revisionrule.bean.EmcRevisionRuleBean;
import com.sunwayworld.emc.module.mdm.basemdm.revisionrule.service.EmcRevisionRuleService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

@RequestMapping("/secure/emc/module/mdm/basemdm/revision-rules")
public interface EmcRevisionRuleResource extends GenericChoosableResource<EmcRevisionRuleService, EmcRevisionRuleBean, String> {

    @RequestMapping(value = "/{value}", method = RequestMethod.POST)
    String selectRuledValue(String value, RestJsonWrapperBean jsonWrapper);
}
