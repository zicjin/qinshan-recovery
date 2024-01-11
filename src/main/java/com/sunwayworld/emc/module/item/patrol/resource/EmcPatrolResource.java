package com.sunwayworld.emc.module.item.patrol.resource;

import java.text.ParseException;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.patrol.bean.EmcPatrolBean;
import com.sunwayworld.emc.module.item.patrol.service.EmcPatrolService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-09
 */
@RequestMapping("/secure/emc/module/item/emc-patrols")
public interface EmcPatrolResource extends GenericResource<EmcPatrolService, EmcPatrolBean, Long> {

    @Override
    Long insert(RestJsonWrapperBean wrapper);

    void monthInsert(RestJsonWrapperBean wrapper) throws ParseException;

    Page<EmcPatrolBean> selectMonthRawPagination(RestJsonWrapperBean wrapper);

    Page<EmcPatrolBean> selectMonthRawPaginationByPatrolUserId(RestJsonWrapperBean wrapper);

    Page<EmcPatrolBean> selectMonthRawPaginationByHandleUserId(RestJsonWrapperBean wrapper);

}
