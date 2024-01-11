package com.sunwayworld.emc.module.bp.info.infoindex.service;

import com.sunwayworld.emc.module.bp.info.infoindex.bean.EmcInfoIndexCategoryBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-21
 */

public interface EmcInfoIndexCategoryService extends GenericService<EmcInfoIndexCategoryBean, Long> {
    Long insert(Long indexId, RestJsonWrapperBean wrapper);

    EmcInfoIndexCategoryBean copy(Long categoryId);
}
