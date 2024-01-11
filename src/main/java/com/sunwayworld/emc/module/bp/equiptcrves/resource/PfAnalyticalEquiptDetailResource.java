package com.sunwayworld.emc.module.bp.equiptcrves.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.equiptcrves.bean.PfAnalyticalEquiptDetailBean;
import com.sunwayworld.emc.module.bp.equiptcrves.service.PfAnalyticalEquiptDetailService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
 * @author licm@sunwayworld.com 2021年10月21日
 */
@RequestMapping("/secure/emc/module/bp/equipt-crves-detail")
public interface PfAnalyticalEquiptDetailResource extends GenericResource<PfAnalyticalEquiptDetailService, PfAnalyticalEquiptDetailBean, Long> {

	Long addEquiptInfo(RestJsonWrapperBean wrapper);

}
