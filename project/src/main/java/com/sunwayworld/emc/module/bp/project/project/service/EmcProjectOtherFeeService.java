package com.sunwayworld.emc.module.bp.project.project.service;

import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectOtherFeeBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
* @author zhengxy@sunwayworld.com 2022年01月14日
*/
public interface EmcProjectOtherFeeService extends GenericService<EmcProjectOtherFeeBean, Long> {

    void createOtherFee(EmcProjectBean project);
}
