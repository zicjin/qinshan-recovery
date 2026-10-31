package com.sunwayworld.emc.module.bp.sample.condition.service;

import com.sunwayworld.emc.module.bp.sample.condition.bean.EmcWorkConditionBean;
import com.sunwayworld.emc.module.bp.sample.condition.bean.EmcWorkConditionLineBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcWorkConditionService extends GenericService<EmcWorkConditionBean, Long> {

    Page<EmcWorkConditionLineBean> queryLineByPagination(RestJsonWrapperBean wrapper);

    void insertLine(RestJsonWrapperBean wrapper);

    void deleteLine(RestJsonWrapperBean wrapper);
}
