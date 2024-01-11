package com.sunwayworld.emc.module.item.improvement.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.improvement.bean.EmcImprovementBean;
import com.sunwayworld.emc.module.item.improvement.service.EmcImprovementService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
 * @author guok@sunwayworld.com
 * @date 2019-12-10
 */

@GikamBean
@RequestMapping("/secure/emc/module/item/improvements")
public interface EmcImprovementResource extends GenericResource<EmcImprovementService, EmcImprovementBean, Long>, GenericAuditableResource<EmcImprovementService, EmcImprovementBean, Long> {

}
