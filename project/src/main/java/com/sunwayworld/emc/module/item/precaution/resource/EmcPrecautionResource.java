package com.sunwayworld.emc.module.item.precaution.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.precaution.bean.EmcPrecautionBean;
import com.sunwayworld.emc.module.item.precaution.service.EmcPrecautionService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-25
 */
@RequestMapping("/secure/emc/module/item/precautions")
@GikamBean
public interface EmcPrecautionResource
        extends GenericResource<EmcPrecautionService, EmcPrecautionBean, Long>, 
                GenericAuditableResource<EmcPrecautionService, EmcPrecautionBean, Long> {

}
