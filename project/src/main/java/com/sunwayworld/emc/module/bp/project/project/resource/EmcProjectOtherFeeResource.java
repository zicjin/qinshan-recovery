package com.sunwayworld.emc.module.bp.project.project.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectOtherFeeBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectOtherFeeService;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
 * @author zhengxy@sunwayworld.com 2023年11月13日
 */
@RequestMapping("/secure/emc/module/bp/project/other-fees")
public interface EmcProjectOtherFeeResource extends GenericResource<EmcProjectOtherFeeService, EmcProjectOtherFeeBean, Long> {
}
