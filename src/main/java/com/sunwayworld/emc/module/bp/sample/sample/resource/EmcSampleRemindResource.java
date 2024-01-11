package com.sunwayworld.emc.module.bp.sample.sample.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.sample.sample.bean.EmcSampleRemindBean;
import com.sunwayworld.emc.module.bp.sample.sample.service.EmcSampleRemindService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/bp/sample/sample-reminds")
public interface EmcSampleRemindResource extends GenericResource<EmcSampleRemindService, EmcSampleRemindBean, Long> {

    Page<EmcSampleRemindBean> selectRemindBySample(RestJsonWrapperBean wrapper);
}
