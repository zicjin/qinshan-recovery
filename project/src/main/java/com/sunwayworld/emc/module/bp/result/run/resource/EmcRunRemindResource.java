package com.sunwayworld.emc.module.bp.result.run.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunRemindBean;
import com.sunwayworld.emc.module.bp.result.run.service.EmcRunRemindService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/bp/result/run-reminds")
public interface EmcRunRemindResource extends GenericResource<EmcRunRemindService, EmcRunRemindBean, Long> {

    Page<EmcRunRemindBean> selectRunRemind(String runId, RestJsonWrapperBean wrapper);

    Page<EmcRunRemindBean> selectResultRemind(String runId, RestJsonWrapperBean wrapper);
}
