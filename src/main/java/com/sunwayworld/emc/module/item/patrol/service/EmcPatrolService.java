package com.sunwayworld.emc.module.item.patrol.service;

import java.text.ParseException;

import com.sunwayworld.emc.module.item.patrol.bean.EmcPatrolBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-09
 */

public interface EmcPatrolService extends GenericService<EmcPatrolBean, Long> {
    @Override
    Long insert(RestJsonWrapperBean wrapper);

    void monthInsert(RestJsonWrapperBean jsonWrapper) throws ParseException;

    Page<EmcPatrolBean> selectMonthRawPagination(RestJsonWrapperBean wrapper);

    Page<EmcPatrolBean> selectMonthRawPaginationByPatrolUserId(RestJsonWrapperBean wrapper);

    Page<EmcPatrolBean> selectMonthRawPaginationByHandleUserId(RestJsonWrapperBean wrapper);
}
