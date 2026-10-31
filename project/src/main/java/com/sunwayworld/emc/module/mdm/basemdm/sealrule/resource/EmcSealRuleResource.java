package com.sunwayworld.emc.module.mdm.basemdm.sealrule.resource;

import com.sunwayworld.emc.module.mdm.basemdm.sealrule.bean.EmcSealRuleBean;
import com.sunwayworld.emc.module.mdm.basemdm.sealrule.bean.EmcSealRuleDetailsBean;
import com.sunwayworld.emc.module.mdm.basemdm.sealrule.service.EmcSealRuleService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/secure/emc/module/mdm/basemdm/seal-rules")
public interface EmcSealRuleResource extends GenericResource<EmcSealRuleService, EmcSealRuleBean, Long> {

    Page<EmcSealRuleDetailsBean> selectRuleDetailsPagination(RestJsonWrapperBean wrapper);

    Page<EmcSealRuleDetailsBean> selectRuleDetailsPaginationBySealRuleId(Long id, RestJsonWrapperBean wrapper);

    Long insertRuleDetails(Long id, RestJsonWrapperBean wrapper);

    void deleteRuleDetails(Long id, RestJsonWrapperBean wrapper);
}
