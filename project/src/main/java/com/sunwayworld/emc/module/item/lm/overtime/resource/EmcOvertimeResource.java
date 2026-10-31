package com.sunwayworld.emc.module.item.lm.overtime.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.lm.overtime.bean.EmcOvertimeBean;
import com.sunwayworld.emc.module.item.lm.overtime.service.EmcOvertimeService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-14
 */
@RequestMapping("/secure/emc/module/item/lm/overtimes")
public interface EmcOvertimeResource extends GenericResource<EmcOvertimeService, EmcOvertimeBean, Long>, GenericAuditableResource<EmcOvertimeService, EmcOvertimeBean, Long> {

    @Override
    Long insert(RestJsonWrapperBean wrapper);

}
