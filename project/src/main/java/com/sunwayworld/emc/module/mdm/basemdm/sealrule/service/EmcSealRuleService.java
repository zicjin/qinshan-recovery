package com.sunwayworld.emc.module.mdm.basemdm.sealrule.service;

import com.sunwayworld.emc.module.mdm.basemdm.sealrule.bean.EmcSealRuleBean;
import com.sunwayworld.emc.module.mdm.basemdm.sealrule.bean.EmcSealRuleDetailsBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcSealRuleService extends GenericService<EmcSealRuleBean, Long> {

    Page<EmcSealRuleDetailsBean> selectRuleDetailsPaginationBySealRuleId(Long id, RestJsonWrapperBean wrapper);

    Long insertRuleDetails(Long id, RestJsonWrapperBean wrapper);

    void deleteRuleDetails(Long id, RestJsonWrapperBean wrapper);

}
