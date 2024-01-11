package com.sunwayworld.emc.module.bp.equiptcrves.service;

import com.sunwayworld.emc.module.bp.equiptcrves.bean.PfAnalyticalEquiptDetailBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author licm@sunwayworld.com 2021年10月21日
 */
public interface PfAnalyticalEquiptDetailService extends GenericService<PfAnalyticalEquiptDetailBean, Long> {

	Long addEquiptInfo(RestJsonWrapperBean wrapper);

}
