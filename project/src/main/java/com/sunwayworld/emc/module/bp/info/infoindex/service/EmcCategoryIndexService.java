package com.sunwayworld.emc.module.bp.info.infoindex.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.info.infoindex.bean.EmcCategoryIndexBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-21
 */

public interface EmcCategoryIndexService extends GenericService<EmcCategoryIndexBean, Long> {
    Long insert(Long categoryId, RestJsonWrapperBean wrapper);

    List<EmcCategoryIndexBean> copy(Long fromCategoryId, Long toCategoryId);
}
