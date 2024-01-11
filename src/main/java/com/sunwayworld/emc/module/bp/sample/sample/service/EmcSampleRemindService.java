package com.sunwayworld.emc.module.bp.sample.sample.service;

import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleRemindBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcSampleRemindService extends GenericService<EmcSampleRemindBean, Long> {

    Page<EmcSampleRemindBean> selectRemindBySample(RestJsonWrapperBean wrapper);

}
