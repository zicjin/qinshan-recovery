package com.sunwayworld.emc.module.item.correction.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.correction.bean.EmcCorrectionBean;
import com.sunwayworld.emc.module.item.correction.service.EmcCorrectionService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-25
 */
@RequestMapping("/secure/emc/module/item/corrections")
@GikamBean
public interface EmcCorrectionResource
        extends GenericResource<EmcCorrectionService, EmcCorrectionBean, Long>, 
                GenericAuditableResource<EmcCorrectionService, EmcCorrectionBean, Long> {

}
