package com.sunwayworld.emc.module.bp.result.run.service;

import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunRemindBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcRunRemindService extends GenericService<EmcRunRemindBean, Long> {

    Page<EmcRunRemindBean> selectRunRemind(String runId, RestJsonWrapperBean wrapper);

    Page<EmcRunRemindBean> selectResultRemind(String runId, RestJsonWrapperBean wrapper);
}
