package com.sunwayworld.emc.module.mdm.basemdm.revisionrule.service;

import com.sunwayworld.emc.module.mdm.basemdm.revisionrule.bean.EmcRevisionRuleBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface EmcRevisionRuleService extends GenericChoosableService<EmcRevisionRuleBean, String> {

    Page<EmcRevisionRuleBean> getRevisionRuleList();

    String selectRuledValue(String value, RestJsonWrapperBean jsonWrapper);

    String getRuledValue(String value, String rule);
}
